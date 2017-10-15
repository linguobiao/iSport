package com.cn.zhihengchuang.walkbank.util;

public interface Constants {
	public static final String SAVEUSERIMAGE = "/avatar.jpg";//头像保存地址
	public static final String DATABASE_NAME = "Band.db";//数据库名称
	public static final String DATABASE_PEDOMETER_MODEL_NAME = "database_pedometermodel";//数据库当天数据表
	public static final String DATABASE_TOTAL_TABLE_NAME = "database_total_name";//数据库睡眠记步详情表名
	public static final String SLEEP_NAME = "sleep";
	public static final String EXERCISE_NAME = "exercise";
	public static final String EMAIL = "e_mail";//当前用户保存地址
	
	public static final String UPDATETIME = "update_time";//同步日期
	public static final String DEVICE_MAC = "device_mac";//设备mac地址
	public static final String DEVICE_NAME = "device_name";//设备名称
	public static final String UPDATE_OK = "update_ok";//同步日期
	public static final String OLD_UPDATE_OK = "old_update_ok";//同步日期
	public static final String SELECT_OK = "select_ok";//
	public static final String CANCEL_REFRESH = "cancel_refresh";//
	public static final String CONNECTING_DEVICE = "action.connection.device";
	public static final String CLEAR_DEVICE = "action.clear.device";
	public static final String CLEAR_AlL = "action.clear.all";
	public static final String SYNCHRONOUS_FAILURE = "action.synchronous.failure";
	public static final String SCAN_DEVICE = "action.scan.device";
	public static final String DISCONNECTING_DEVICE = "action.disconnection.device";
	public static final String UNBOND_DEVICE = "action.unbond.device";
	public static final String SEND = "action.send";
	public static final String SHARE_FILE_NAME = "demo";//分享名称
	public static final String BINDINGADDRESS = "bindingaddress";//绑定地址
	public static final String CONNECTION_STATE="connection_state";//连接状态   share  0 为断开，1为连接
	public static final String CONNECTED_SETS = "connected.sets";//连接成功后设置时间，公制，时区广播
	public static final String BROADCAST_ACTION_CONNECTION_STATECHANGE="flyjiang.Connection.State.Change";//连接状态改变
	public static final String BROADCAST_ACTION_DISCONNECTION_DEVICE="flyjiang.disconnection";//连接断开
	public static final String BROADCAST_ACTION_UNBIND="flyjiang.unbind";
	public static final String BROADCAST_ACTION_USER_SERVICE_STOP="flyjiang.user.service.stop";
	public static final String BROADCAST_ACTION_DATA_STEPS="flyjiang.data.update";//数据更新
	
}