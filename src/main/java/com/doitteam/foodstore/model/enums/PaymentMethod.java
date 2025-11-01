package com.doitteam.foodstore.model.enums;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    CREDIT_CARD("Thẻ tín dụng"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    E_WALLET("Ví điện tử");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
