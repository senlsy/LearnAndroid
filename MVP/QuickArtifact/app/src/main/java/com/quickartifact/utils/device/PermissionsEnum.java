package com.quickartifact.utils.device;

import android.Manifest;

/**
 * Description: 权限枚举，6.0之后需要动态申请的权限，均应该定义在这里
 *
 * @author mark.lin
 * @date 2016/9/13 16:55
 */
public enum PermissionsEnum {

    /*  以下权限均需要动态申请
        group:android.permission-group.CONTACTS
        permission:android.permission.WRITE_CONTACTS
        permission:android.permission.GET_ACCOUNTS
        permission:android.permission.READ_CONTACTS

        group:android.permission-group.PHONE
        permission:android.permission.READ_CALL_LOG
        permission:android.permission.READ_PHONE_STATE
        permission:android.permission.CALL_PHONE
        permission:android.permission.WRITE_CALL_LOG
        permission:android.permission.USE_SIP
        permission:android.permission.PROCESS_OUTGOING_CALLS
        permission:com.android.voicemail.permission.ADD_VOICEMAIL

        group:android.permission-group.CALENDAR
        permission:android.permission.READ_CALENDAR
        permission:android.permission.WRITE_CALENDAR

        group:android.permission-group.CAMERA
        permission:android.permission.CAMERA

        group:android.permission-group.SENSORS
        permission:android.permission.BODY_SENSORS

        group:android.permission-group.LOCATION
        permission:android.permission.ACCESS_FINE_LOCATION
        permission:android.permission.ACCESS_COARSE_LOCATION

        group:android.permission-group.STORAGE
        permission:android.permission.READ_EXTERNAL_STORAGE
        permission:android.permission.WRITE_EXTERNAL_STORAGE

        group:android.permission-group.MICROPHONE
        permission:android.permission.RECORD_AUDIO

        group:android.permission-group.SMS
        permission:android.permission.READ_SMS
        permission:android.permission.RECEIVE_WAP_PUSH
        permission:android.permission.RECEIVE_MMS
        permission:android.permission.RECEIVE_SMS
        permission:android.permission.SEND_SMS
        permission:android.permission.READ_CELL_BROADCASTS
        */

    GROUP_MICROPHONE(Manifest.permission_group.MICROPHONE, "音频输入权限"),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, "音频输入权限"),


    GROUP_CAMERA(Manifest.permission_group.CAMERA, "相机权限"),
    CAMERA(Manifest.permission.CAMERA, "相机权限"),

    GROUP_SENSORS(Manifest.permission_group.SENSORS, "传感器权限"),
    BODY_SENSORS(Manifest.permission.BODY_SENSORS, "传感器权限"),

    GROUP_CALENDAR(Manifest.permission_group.CALENDAR, "日历权限"),
    READ_CALENDAR(Manifest.permission.READ_CALENDAR, "日历读取权限"),
    WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR, "日历操作权限"),

    GROUP_CONTACTS(Manifest.permission_group.CONTACTS, "联系人权限"),
    WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS, "联系人操作权限"),
    GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS, "账户获取权限"),
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, "联系人读取权限"),

    GROUP_SMS(Manifest.permission_group.SMS, "SMS权限"),
    READ_SMS(Manifest.permission.READ_SMS, "SMS读取权限"),
    RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH, "WAP推送消息接收权限"),
    RECEIVE_MMS(Manifest.permission.RECEIVE_MMS, "MMS接收权限"),
    RECEIVE_SMS(Manifest.permission.SEND_SMS, "SMS发送权限"),


    GROUP_PHONE(Manifest.permission_group.PHONE, "设备状态权限"),
    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG, "通话记录读取权限"),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, "设备状态读取权限"),
    CALL_PHONE(Manifest.permission.CALL_PHONE, "电话呼叫权限"),
    WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG, "通话记录操作权限"),
    USE_SIP(Manifest.permission.USE_SIP, "SIP使用权限"),
    PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS, "电话呼出权限"),
    ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL, "语音信箱添加权限"),

    GROUP_STORAGE(Manifest.permission_group.STORAGE, "存储卡权限"),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, "存储卡读取权限"),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储卡写出权限"),


    GROUP_LOCATION(Manifest.permission_group.LOCATION, "定位权限"),
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, "高精度定位权限"),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, "低精度行为权限");

    private String mKey;
    private String mDescription;

    PermissionsEnum(String key, String desc) {
        mKey = key;
        mDescription = desc;
    }

    public String getKey() {
        return mKey;
    }

    public String getDescription() {
        return mDescription;
    }

    /**
     * 根据name获取对应的权限枚举常量
     *
     * @param name 权限名称Manifest.permission.***
     * @return 返回对应的权限枚举常量
     */
    public static PermissionsEnum getPermissionEnum(String name) {
        switch (name) {

            case Manifest.permission.READ_PHONE_STATE:
                return READ_PHONE_STATE;

            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return WRITE_EXTERNAL_STORAGE;

        }
        return null;
    }

}
