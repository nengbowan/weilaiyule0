package top.fwkj51.api;

import top.fwkj51.util.HttpClientUtil;

/**
 * 自动投注 www.fwkj51.top 的相关http接口
 */
public class Api {
    private  final String BASEURL = "http://www.fwkj51.top";

    public String getVerifyCode(){
        //http://www.fwkj51.top/api/utils/loginSecurityCode?1540534863736
        String reqUrl = BASEURL + "/api/utils/loginSecurityCode?"+System.currentTimeMillis();
        return HttpClientUtil.getPageByURL(reqUrl);
    }

    public static void main(String[] args) {
        String base64Image = new Api().getVerifyCode();
        return;
    }
}
