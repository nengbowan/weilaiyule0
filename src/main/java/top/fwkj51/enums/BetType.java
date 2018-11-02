package top.fwkj51.enums;

/**
 * 下注类型 ： 龙 虎
 */
public enum BetType {
    LONG("long","龙"),
    HU("hu","虎");

    private String code;

    private String name;


    BetType(String code , String name){
        this.code = code;

        this.name = name;
    }
    public String getName(){
        return name;
    }


}
