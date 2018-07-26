package example.db.enums;

public enum CustomerCreditLevel {
    UN_PASS(null, "未通过"),
    PASS_B("B", "通过-B"),
    PASS_BP("B+", "老B级"),
    PASS_A("A", "通过-A");

    private final String code;
    private final String desc;

    CustomerCreditLevel(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
