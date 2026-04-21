package com.phghuy.calmihome.model;

public class SessionManager {
    public static int userId = -1;
    public static String email = null;
    public static String password = null;

    public static void clear() {
        userId = -1;
        email = null;
        password = null;
    }
}
