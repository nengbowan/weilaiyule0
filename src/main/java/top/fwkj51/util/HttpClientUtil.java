package top.fwkj51.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import top.fwkj51.dto.ResultDTO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    public static String getPageByURL(String url){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity2 = response.getEntity();
            String result = EntityUtils.toString(entity2 , Charset.defaultCharset());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }

    public static String getPageByURLAndCookie(String url,Map<String,String> headerParams,Map<String,String> postParams){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            if(headerParams != null && headerParams.size()>0){
                for(Map.Entry<String,String> entry : headerParams.entrySet()){
                    httpGet.addHeader(new BasicHeader(entry.getKey() , entry.getValue()));
                }
            }

            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity2 = response.getEntity();
            String result = EntityUtils.toString(entity2 , Charset.defaultCharset());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getResByUrlAndCookie(String url , Map<String,String> headerParams , String cookie , boolean getCookie)  {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        Header header = new BasicHeader("Cookie",cookie);
        httpGet.addHeader(header);
        if(headerParams != null && headerParams.size() >0){
            for(Map.Entry<String, String> entry : headerParams.entrySet()){
                Header basicHeader = new BasicHeader(entry.getKey() , entry.getValue());
                httpGet.addHeader(basicHeader);
            }
        }
        CloseableHttpResponse response2 = null;
        try {
            response2 = httpclient.execute(httpGet);

            HttpEntity entity2 = response2.getEntity();
            String respStr = EntityUtils.toString(entity2 , Charset.defaultCharset());
            Header[] cookies = response2.getHeaders("Set-Cookie");
            StringBuffer cookieStr = new StringBuffer();
            if(cookies != null && cookies.length != 0){
                for(Header cookHeader : cookies){
                    cookieStr.append(cookHeader.getValue() + ";");
                }
            }
            response2.close();

            if(getCookie){
                return respStr + "#" + cookieStr.toString();
            }else{
                return respStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }






}
