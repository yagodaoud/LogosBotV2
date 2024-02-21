package main.db;

public enum Tables {
    CRYPTO("crypto_alert"),
    USERS("users_value"),
    ORDERS("orders_value");

    private final String tableName;

    Tables(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

}
