package top.fwkj51;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import top.fwkj51.dto.CommonDto;
import top.fwkj51.dto.InitPageDto;

public class InitPageDtoTests {
    @Test
    public void test(){
        String commStr = "{\n" +
                "    \"error\":0,\n" +
                "    \"code\":null,\n" +
                "    \"message\":\"请求成功\",\n" +
                "    \"data\":{\n" +
                "        \"accountId\":56511,\n" +
                "        \"main\":{\n" +
                "            \"username\":\"xjjwqc\",\n" +
                "            \"nickname\":\"xjjwqc\",\n" +
                "            \"type\":1,\n" +
                "            \"registTime\":1541496776000,\n" +
                "            \"loginTime\":1541561548000,\n" +
                "            \"lockTime\":1541518596000,\n" +
                "            \"status\":0,\n" +
                "            \"onlineStatus\":1,\n" +
                "            \"bindStatus\":0,\n" +
                "            \"signTime\":null,\n" +
                "            \"typeName\":\"代理\"\n" +
                "        },\n" +
                "        \"lottery\":{\n" +
                "            \"availableBalance\":105.45824,\n" +
                "            \"blockedBalance\":0,\n" +
                "            \"code\":1970,\n" +
                "            \"point\":3.5,\n" +
                "            \"codeType\":0,\n" +
                "            \"extraPoint\":0,\n" +
                "            \"playStatus\":0,\n" +
                "            \"allowEqualCode\":false,\n" +
                "            \"isDividendAccount\":false,\n" +
                "            \"minCode\":1900,\n" +
                "            \"plamtMinCode\":1900\n" +
                "        },\n" +
                "        \"msgCount\":0\n" +
                "    },\n" +
                "    \"type\":0,\n" +
                "    \"id\":0\n" +
                "}";
        CommonDto<InitPageDto> commonDto = JSONObject.parseObject(commStr ,new TypeReference<CommonDto<InitPageDto>>(){});
        String code = commonDto.getData().getLottery().getCode();
        return;
    }
}
