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
import top.fwkj51.enums.BetType;
import top.fwkj51.enums.LongHuBetMethod;
import top.fwkj51.util.RuoKuai;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
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

    private List<LongHuBetMethod> longHuBetMethods;

    private BetType betType;

    private BetMoneyType betMoneyType;

    private String baseUrl ;

    private JTextPane textPane;

    //textpane 是不必要传的参数  耦合性高了 TODO 商量一个哦好办法
    public Api(JTextPane textPane , String baseUrl , String username, String password , int [] bets , List<LongHuBetMethod> longHuBetMethods , BetType betType , BetMoneyType betMoneyType ){
        this.textPane = textPane;

        this.baseUrl = baseUrl;
        //重定向输出流到文件中
        String userHome = System.getProperty("user.home");
        LocalDate now = LocalDate.now();
        File logFile = new File(userHome + "/" + now.getYear() + "-" + now.getMonthValue() + "-"+ now.getDayOfMonth() + ".log");
        try {
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos ,true);
            System.setOut(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.username = username;

        this.password = password;

        this.bets = bets;

        this.longHuBetMethods = longHuBetMethods;

        this.betType = betType;

        this.betMoneyType = betMoneyType;
    }

    public String getVerfiyPath(){
        String userHome =  System.getProperty("user.dir");
        String url = baseUrl + "/api/utils/loginSecurityCode?"+System.currentTimeMillis();

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

        System.out.println("余额" + pollingLottery().getData().getLotteryBalance());

        new Thread(new Runnable() {
            @Override
            public void run() {
                textPane.setText(textPane.getText() + "\n" + "余额" + pollingLottery().getData().getLotteryBalance());
            }
        }).start();

        //对齐后的methods
        List<LongHuBetMethod> alignedMethods = new ArrayList<>();
        alignedMethods.addAll(longHuBetMethods);
        //自动补齐下注金额 和 龙虎万千的个数匹配
        if(this.longHuBetMethods.size() < this.bets.length){
            int size = this.bets.length; //20
            int methodSize = longHuBetMethods.size(); //3
            int decreaseSize =  size - methodSize;  //17
            int shang = decreaseSize / methodSize ;
            int yu = decreaseSize % methodSize ;
            if(shang != 0){
                for(int shangIndex = 0 ; shangIndex < shang ; shangIndex ++){
                    alignedMethods.addAll(longHuBetMethods);
                }
            }
            if(yu != 0)
            alignedMethods.addAll( longHuBetMethods.subList(0 , yu));
        }


        doBet(this.bets , alignedMethods , this.betType , this.betMoneyType);
    }

    private void doLogin(String verifyCode){
        //登录
        String doLoginUrl = baseUrl+ "/api/webPageLogin";
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
    private void doBet(int [] bets , List<LongHuBetMethod> methods , BetType betType ,BetMoneyType betMoneyType) {
        //modify for 修复赢了还继续倍投的BUG FIX by fushiyong at 2018-11-03 end
        //作为输赢的标记
//        int beforeCount = 0 ;
        //modify for 修复赢了还继续倍投的BUG FIX by fushiyong at 2018-11-03 end
        for(int methodIndex = 0 ,index = 0 , betIndex = 0;methodIndex<bets.length;methodIndex++ , betIndex++ , index++){

            //下注万千 1厘

            //modify for 修复下注金额个数多余下注龙虎万千等method的个数的BUG FIX by fushiyong at 2018-11-03 start
            //暂时不修复 因为count是共享变量 所以目前采用补齐下注龙虎万千的个数
            //比如 下注金额 1,3,7,15,32,65,129,219
            //下注龙虎 为 龙虎万千 龙虎万百 则补齐龙虎万千 龙虎万百 龙虎万千 龙虎万百 龙虎万千 龙虎万百 龙虎万千
//            int methodCount = -1;
//            if(methods.size() < bets.length){
//                methodCount = methodIndex % methods.size();
//            }else{
//                methodCount = methodIndex;
//            }
            int shouldMethodIndex = index > methodIndex ? index % methodIndex -1  : methodIndex;
            bet(methods.get(shouldMethodIndex ) , betMoneyType ,bets[  betIndex  ] , betType);
            //modify for 修复下注金额个数多余下注龙虎万千等method的个数的BUG FIX by fushiyong at 2018-11-03 end

            ResultDTO before = pollingLottery();
            //余额
            float remainMoney = before.getData().getLotteryBalance();
            //期数
            String record = before.getData().getGameOpenCode().getIssue();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    textPane.setText(textPane.getText() + "\n" + "下注后的余额 : " + remainMoney);
                    textPane.setText(textPane.getText() + "\n" + "下注后的期数 : " + record );
                }
            }).start();

            System.out.println("下注后的余额 : " + remainMoney );
            System.out.println("下注后的期数 : " + record );

            ResultDTO betAfter = null;
            do{
                betAfter = pollingLottery();
                try {
                    Thread.sleep(3000); //每三秒获取一次是否已开奖
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(record.equals(betAfter.getData().getGameOpenCode().getIssue()));

            waitFor3Seconds();
            //modify for 解决延迟开奖的BUG 三秒之后取余额 by fushiyong at 2018-11-6 start
            ResultDTO kaiJiangHou = pollingLottery();
            float remainMoneyAfterKaiJiang =   kaiJiangHou.getData().getLotteryBalance();
            String recordAfterKaiJaing = kaiJiangHou.getData().getGameOpenCode().getIssue();
            System.out.println("开奖后的余额 : " + remainMoneyAfterKaiJiang );
            System.out.println("开奖后的期数 : " + recordAfterKaiJaing );

            new Thread(new Runnable() {
                @Override
                public void run() {
                    textPane.setText(textPane.getText() + "\n" + "开奖后的余额 : " + remainMoneyAfterKaiJiang );
                    textPane.setText(textPane.getText() + "\n" + "开奖后的期数 : " + recordAfterKaiJaing );
                }
            }).start();
            //modify for 解决延迟开奖的BUG 三秒之后取余额 by fushiyong at 2018-11-6 end


            if(remainMoney != remainMoneyAfterKaiJiang){
                //中奖中了

                System.out.println("中奖啦 恭喜 Congratulations");
                System.out.println("余额："+remainMoneyAfterKaiJiang);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        textPane.setText(textPane.getText() + "\n" + "中奖啦 恭喜 Congratulations");
                        textPane.setText(textPane.getText() + "\n" + "余额："+remainMoneyAfterKaiJiang);
                    }
                }).start();

                //modify for 修改业务逻辑 赢了之后投注下一个不是重头继续 by fushiyong at 2018-11-3 start
//                beforeCount = -1;//TODO FIX BUG 金额下注错误 下注项目正确
//                methodIndex = -1; 共享投注变量
                //modify for 修改业务逻辑 赢了之后投注下一个不是重头继续 by fushiyong at 2018-11-3 end
                betIndex = -1;
//                methodIndex =-1;


            }else{
//                beforeCount = 0;
                //do nothing 执行for循环第二次
                System.out.println("别气馁 继续努力");
                System.out.println("余额："+remainMoneyAfterKaiJiang);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textPane.setText(textPane.getText() + "\n" + "别气馁 继续努力");
                        textPane.setText(textPane.getText() + "\n" + "余额："+remainMoneyAfterKaiJiang);
                    }
                });
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

    private void waitFor3Seconds() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ResultDTO pollingLottery(){
        String is = baseUrl + "/api/ajaxWebPage/pollingLottery";
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
    private void bet(LongHuBetMethod method, BetMoneyType model , int money , BetType content) {
        String url = baseUrl + "/api/lottery/addOrder";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:63.0) Gecko/20100101 Firefox/63.0");

        List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();

         BetDto betDto = BetDto.builder()
                                .lottery("txffc")
                .issue("")
                .method(method.getCode())
                .content(content.getName())
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
            System.out.println("下注"+method.getName() + " "+this.betType.getName()+ money+model.getName());
            System.out.println("余额"+pollingLottery().getData().getLotteryBalance());

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textPane.setText(textPane.getText() + "\n" + "下注"+method.getName() + " "+betType.getName()+ money+model.getName());
                    textPane.setText(textPane.getText() + "\n" + "余额"+pollingLottery().getData().getLotteryBalance());

                }
            });
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
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textPane.setText(textPane.getText() + "\n" + "验证码识别失败,程序退出");
                    }
                });
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
