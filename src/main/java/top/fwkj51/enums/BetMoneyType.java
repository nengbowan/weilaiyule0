package top.fwkj51.enums;

public enum BetMoneyType {
    LI("li","厘"),
    FEN("fen","分"),
    JIAO("jiao","角"),
    YUAN("yuan","元");
    private String code;

    private String name;


    BetMoneyType(String code , String name){
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
