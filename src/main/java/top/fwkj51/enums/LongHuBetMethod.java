package top.fwkj51.enums;

public enum LongHuBetMethod {
    LHWQ("lhwq","龙虎万千"),
    LHWB("lhwb","龙虎万百"),
    LHWS("lhws","龙虎万十"),
    LHWG("lhwg","龙虎万个"),
    LHQB("lhqb","龙虎千百"),
    LHQS("lhqs","龙虎千十"),
    LHQG("lhqg","龙虎千个"),
    LHBS("lhbs","龙虎百十"),
    LHBG("lhbg","龙虎百个"),
    LHSG("lhsg","龙虎十个");
    private String code;

    private String name;


    LongHuBetMethod(String code , String name){
        this.code = code;

        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
