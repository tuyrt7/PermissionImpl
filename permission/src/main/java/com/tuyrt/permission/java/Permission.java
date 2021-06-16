package com.tuyrt.permission.java;

import java.util.HashMap;

/**
 权限定义类
 */
public class Permission {
    //Calendar
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";
    //Camera
    public static final String CAMERA = "android.permission.CAMERA";
    //Contacts
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
    //Location
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    //Microphone
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    //Phone
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    public static final String USE_SIP = "android.permission.USE_SIP";
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    //Sensors
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    //Sms
    public static final String SEND_SMS = "android.permission.SEND_SMS";
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String READ_SMS = "android.permission.READ_SMS";
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";
    //Storage
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    // 8.0及以上应用安装权限
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";
    // 6.0及以上悬浮窗权限
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /** 8.0危险权限：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数 */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    /** 8.0危险权限：权限允许您的应用读取设备中存储的电话号码 */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    public static final HashMap<String, String> permissionMap = new HashMap<>(24);

    static {
        permissionMap.put(Permission.READ_CALENDAR, "存储");
        permissionMap.put(Permission.WRITE_CALENDAR, "存储");

        permissionMap.put(Permission.CAMERA, "相机");

        permissionMap.put(Permission.READ_CONTACTS, "联系人");
        permissionMap.put(Permission.WRITE_CONTACTS, "联系人");
        permissionMap.put(Permission.GET_ACCOUNTS, "联系人");

        permissionMap.put(Permission.ACCESS_FINE_LOCATION, "定位");
        permissionMap.put(Permission.ACCESS_COARSE_LOCATION, "定位");

        permissionMap.put(Permission.RECORD_AUDIO, "麦克风");

        permissionMap.put(Permission.READ_PHONE_STATE, "电话");
        permissionMap.put(Permission.CALL_PHONE, "电话");
        permissionMap.put(Permission.READ_CALL_LOG, "电话");
        permissionMap.put(Permission.WRITE_CALL_LOG, "电话");
        permissionMap.put(Permission.ADD_VOICEMAIL, "电话");
        permissionMap.put(Permission.USE_SIP, "电话");
        permissionMap.put(Permission.PROCESS_OUTGOING_CALLS, "电话");

        permissionMap.put(Permission.BODY_SENSORS, "传感器");

        permissionMap.put(Permission.SEND_SMS, "短信");
        permissionMap.put(Permission.RECEIVE_SMS, "短信");
        permissionMap.put(Permission.READ_SMS, "短信");
        permissionMap.put(Permission.RECEIVE_WAP_PUSH, "短信");
        permissionMap.put(Permission.RECEIVE_MMS, "短信");

        permissionMap.put(Permission.READ_EXTERNAL_STORAGE, "存储");
        permissionMap.put(Permission.WRITE_EXTERNAL_STORAGE, "存储");

        permissionMap.put(Permission.REQUEST_INSTALL_PACKAGES, "安装");
        permissionMap.put(Permission.SYSTEM_ALERT_WINDOW, "弹窗");

        permissionMap.put(Permission.ANSWER_PHONE_CALLS, "自动接听");
        permissionMap.put(Permission.READ_PHONE_NUMBERS, "读取电话号码");
    }

    public static final class Group {

        /** 日历 */
        public static final String[] CALENDAR = new String[]{
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR};

        /** 联系人 */
        public static final String[] CONTACTS = new String[]{
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS,
                Permission.GET_ACCOUNTS};

        /** 位置 */
        public static final String[] LOCATION = new String[]{
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION};

        /** 存储 */
        public static final String[] STORAGE = new String[]{
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE};
    }
}

