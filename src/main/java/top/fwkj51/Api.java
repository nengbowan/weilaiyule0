package top.fwkj51;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.fwkj51.dto.BetDto;
import top.fwkj51.dto.ResultDTO;
import top.fwkj51.enums.BetMoneyType;
import top.fwkj51.enums.LongHuBetMethod;
import top.fwkj51.util.RuoKuai;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 自动投注 www.fwkj51.top 的相关http接口
 */
public class Api {

    private String username;

    private String password;

    private CookieStore cookieStore = new BasicCookieStore();

    //所有的网络请求都用同一个httpClient 实现cookie自动管理
    private CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

    private int [] bets;

    private LongHuBetMethod[] longHuBetMethods;

    public Api(String username, String password , int [] bets , LongHuBetMethod[] longHuBetMethods){
        this.username = username;

        this.password = password;

        this.bets = bets;

        this.longHuBetMethods = longHuBetMethods;
    }

    public String getVerfiyPath(){
        String userHome =  System.getProperty("user.dir");
        String url = "http://www.fwkj51.top/api/utils/loginSecurityCode?"+System.currentTimeMillis();

        Map<String,String> headerParams = new HashMap();
        headerParams.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:63.0) Gecko/20100101 Firefox/63.0");
        try{
            HttpGet httpGet = new HttpGet(url);
            if(headerParams != null && headerParams.size()>0){
                for(Map.Entry<String,String> entry : headerParams.entrySet()){
                    httpGet.addHeader(new BasicHeader(entry.getKey() , entry.getValue()));
                }
            }

            CloseableHttpResponse response = this.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            File targetFile = new File(userHome+"/" + System.currentTimeMillis() + ".png");
            FileUtils.copyToFile(inputStream , targetFile);
            return targetFile.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void login(){
        //保存验证码
        String path = getVerfiyPath();

        //调用模块识别验证码
        String xmlResp = RuoKuai.createByPost(
                RuoKuai.username , RuoKuai.password ,
                RuoKuai.typeId , RuoKuai.timeout,
                RuoKuai.softId , RuoKuai.softKey,
                path);

        String verifyCode = getVerifyCode(xmlResp);

        doLogin(verifyCode);


//        ResultDTO resultDTO = pollingLottery();
//        if(resultDTO == null){
//            System.out.println("获取开奖时间失败");
//            System.out.println("等待三分钟再继续下注");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }



        //下注
        if(this.bets == null || this.bets.length == 0){
            this.bets = new int[]{1,3,7,15,31,63,127,255,511,1023,2047,5095};
        }

        if(this.longHuBetMethods == null || this.longHuBetMethods.length == 0){
            this.longHuBetMethods = new LongHuBetMethod[]{
                    LongHuBetMethod.LHWQ,
                    LongHuBetMethod.LHWB,
                    LongHuBetMethod.LHWS,
                    LongHuBetMethod.LHWG,
                    LongHuBetMethod.LHQB,
                    LongHuBetMethod.LHQS,
                    LongHuBetMethod.LHQG,
                    LongHuBetMethod.LHBS,
                    LongHuBetMethod.LHBG,
                    LongHuBetMethod.LHSG

            };
        }

        System.out.println("余额" + pollingLottery().getData().getLotteryBalance());
        doBet(this.bets , this.longHuBetMethods);
    }

    private void doLogin(String verifyCode){
        //登录
        String doLoginUrl = "http://www.fwkj51.top/api/webPageLogin";
        HttpPost httpPost = new HttpPost(doLoginUrl);
        Map<String,String> headerParams = new HashMap();
        headerParams.put("Content-Type","application/x-www-form-urlencoded");
        headerParams.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:63.0) Gecko/20100101 Firefox/63.0");
        if(headerParams != null && headerParams.size()>0){
            for(Map.Entry<String,String> entry : headerParams.entrySet()){
                httpPost.addHeader(new BasicHeader(entry.getKey() , entry.getValue()));
            }
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username" , this.username));
        nvps.add(new BasicNameValuePair("password" , this.password));
        nvps.add(new BasicNameValuePair("securityCode" , verifyCode));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps , Charset.forName("UTF-8")));
        try {
            CloseableHttpResponse response = this.httpClient.execute(httpPost);
            String respStr = EntityUtils.toString(response.getEntity() , Charset.defaultCharset());
            System.out.println(respStr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //下注策略核心
    private void doBet(int [] bets , LongHuBetMethod[] methods) {

        for(int count = 0 ;count<bets.length;count++){
            //下注万千 1厘
            bet(methods[count] , BetMoneyType.YUAN ,bets[count]);
            ResultDTO before = pollingLottery();
            //余额
            float remainMoney = before.getData().getLotteryBalance();
            //期数
            String record = before.getData().getGameOpenCode().getIssue();

            ResultDTO betAfter = null;
            do{
                betAfter = pollingLottery();
                try {
                    Thread.sleep(3000); //每三秒获取一次是否已开奖
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(record.equals(betAfter.getData().getGameOpenCode().getIssue()));

            float remainMoneyAfterKaiJiang =   betAfter.getData().getLotteryBalance();


            if(remainMoney != remainMoneyAfterKaiJiang){
                //中奖中了
                System.out.println("中奖啦 恭喜 Congratulations");
                System.out.println("余额："+remainMoneyAfterKaiJiang);
                count = -1 ;
            }else{
                //do nothing 执行for循环第二次
                System.out.println("别气馁 继续努力");
                System.out.println("余额："+remainMoneyAfterKaiJiang);
            }

        }




        //记录下当前下注的记录数 下注数 金钱数

        //一直循环读取pollingLottery 如果 下注的期数 和 读取的期数不一致 说明已开奖
        //然后查看余额 如果余额没变动 输了 加倍
//        Date lastBetTime = null;
//        for(int i = 0 ; i<bets.length ; i++){
//            // 假定无缝下注 也就是 24小时下注 不考虑封盘的合理情况
//
//            bet(bets[0] , );
//            lastBetTime = new Date();
//
//
//        }




    }
    private ResultDTO pollingLottery(){
        String is = "http://www.fwkj51.top/api/ajaxWebPage/pollingLottery";
        HttpPost httpPost1 = new HttpPost(is);
        Map<String,String> headerParams1 = new HashMap();
        headerParams1.put("Content-Type","application/x-www-form-urlencoded");
        headerParams1.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:63.0) Gecko/20100101 Firefox/63.0");
        if(headerParams1 != null && headerParams1.size()>0){
            for(Map.Entry<String,String> entry : headerParams1.entrySet()){
                httpPost1.addHeader(new BasicHeader(entry.getKey() , entry.getValue()));
            }
        }
        List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
        nvps1.add(new BasicNameValuePair("lottery" , "txffc"));
        httpPost1.setEntity(new UrlEncodedFormEntity(nvps1 , Charset.forName("UTF-8")));
        ResultDTO resultDTO = null;
        try {
            CloseableHttpResponse response1 = this.httpClient.execute(httpPost1);
            String respStrJson = EntityUtils.toString(response1.getEntity() , Charset.defaultCharset());
            resultDTO = JSONObject.parseObject(respStrJson , ResultDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDTO;
    }


    //下注
    private void bet(LongHuBetMethod method, BetMoneyType model , int money) {
        String url = "http://www.fwkj51.top/api/lottery/addOrder";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:63.0) Gecko/20100101 Firefox/63.0");

        List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();

         BetDto betDto = BetDto.builder()
                                .lottery("txffc")
                .issue("")
                .method(method.getCode())
                .content("龙")
                .model(model.getCode())
                .multiple(money)
                .code(1980)
                .compress(false).build();
        List<BetDto> betDtos = new ArrayList<BetDto>();
        betDtos.add(betDto);
        nvps1.add(new BasicNameValuePair("text" , JSONObject.toJSONString(betDtos)));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps1 , Charset.forName("UTF-8")));
        try {
            CloseableHttpResponse response1 = httpClient.execute(httpPost);
            String respStrJson = EntityUtils.toString(response1.getEntity() , Charset.defaultCharset());
            System.out.println(respStrJson);
            System.out.println("下注"+method.getName() + money+model.getName());
            System.out.println("余额"+pollingLottery().getData().getLotteryBalance());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getVerifyCode(String respXml) {

        try {

            SAXReader reader = new SAXReader();


            Document doc = reader.read(new ByteArrayInputStream(respXml
                    .getBytes("UTF8")));

            if(doc.getRootElement().element("Result") == null){
                System.out.print("验证码识别失败");
                System.exit(0);
            }

            Element rootEle =  doc.getRootElement().element("Result");

            if(rootEle != null){
                return rootEle.getText();
            }else{
                return null;
            }
        }
        catch (DocumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
