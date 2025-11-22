package com.doitteam.foodstore.util;

public class AppConstants {

    // Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Order
    public static final int MAX_ITEMS_PER_ORDER = 50;
    public static final double DEFAULT_SHIPPING_FEE = 30000.0;

    // Product
    public static final int MIN_PRODUCT_NAME_LENGTH = 3;
    public static final int MAX_PRODUCT_NAME_LENGTH = 200;

    // User
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;

    // File Upload
    public static final long MAX_FILE_SIZE = 10485760L; // 10MB
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}