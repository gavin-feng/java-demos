package example.db.enums;

public enum CustomerStatus {
    NEW(1, "新建"),
    UN_APPLY(2, "未申请"),
    NOT_SELF_DRIVING(3, "仅非自驾"),
    TO_ALLOCATE(4, "待分配"),
    FOLLOWING(5, "跟进中"),
    TO_GRADE(6, "待评级"),
    UN_PASS(7, "未通过"),
    PASS(8, "通过"),
    CANCELED(9, "已取消"),
    FINISH(10, "已完成");

    private final int code;
    private final String desc;

    private CustomerStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}