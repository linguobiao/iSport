package com.cn.zhihengchuang.walkbank.util;

import android.net.Uri;
/**@author longke
 * 系统配置常量类
 */
public class SystemConfig {
	
	public final static String UPDATE_SAVENAME = "pedometer.apk";

	public final static String DB_NAME = "bally.db";
	public final static int DB_VERSION = 1;

	public final static String INTENT_KEY = "ID";
	public final static String INTENT_MSG = "MSG";
	
	public final static String TYPEP118 = "P118";
	public final static String TYPEW240 = "W240";

	

	/**
	 *设备地址ַ
	 * */
	public static final String KEY_BLUE_ADDRESS = "blue_address";
	
	/**
	 * 设备名字
	 * */
	public static final String KEY_BLE_NAME = "bleName";
	
	/**
	 * 设备型号
	 */
	public static final String KEY_DEVICE_TYPE = "device_type";
	
	/**
	 * ��λ
	 * */
	public static final String KEY_MEASURE = "measure";

	/**
	 * 出生年
	 * */
	public static final String KEY_BIRTHDAY_YEAR = "birthday_year";


	/**
	 * 出生月
	 * */
	public static final String KEY_BIRTHDAY_MONTH = "birthday_month";
	
	/**
	 * 出生日
	 * */
	public static final String KEY_BIRTHDAY_DAY = "birthday_day";
	
	/**
	 * 体重
	 * */
	public static final String KEY_WEIGHT = "weight";

	/**
	 * 性别
	 * */
	public static final String KEY_GENDER = "gender";

	/**
	 * 目标
	 * */
	public static final String KEY_TARGET = "target";

	/**
	 * ����
	 * */
	public static final String KEY_STRIDE = "stride";

	/**
	 * ֪ͨ�� ֪ͨ
	 * */
	public static final String KEY_NOTIFICATION = "notification";

	/**
	 * ֪ͨ�� ����
	 * */
	public static final String KEY_SOUND = "sound";

	/**
	 * ֪ͨ�� ��
	 * */
	public static final String KEY_VIBRATE = "vibrate";

	/**
	 * Email ��ȡƵ��
	 * */
	public static final String KEY_INTERVAL = "interval";
	
	//����
	/**
	 * 铃声的名字1
	 */
	public static final String KEY_ALARM1_NAME = "alarm1_name";
	
	/**
	 * 铃声的名字2
	 */
	public static final String KEY_ALARM2_NAME = "alarm2_name";
	
	/**
	 * 铃声的名字3
	 */
	public static final String KEY_ALARM3_NAME = "alarm3_name";
	
	/**
	 * 铃声的名字4
	 */
	public static final String KEY_ALARM4_NAME = "alarm4_name";
	/**
	 * 铃声的名字4
	 */
	public static final String KEY_ALARM5_NAME = "alarm5_name";
	
	/**
	 * 闹铃的时间1
	 */
	public static final String KEY_ALARM1_TIME = "alarm1_time";
	
	/**
	 * 闹铃的时间2
	 */
	public static final String KEY_ALARM2_TIME = "alarm2_time";
	
	/**
	 * 闹铃的时间3
	 */
	public static final String KEY_ALARM3_TIME = "alarm3_time";
	
	/**
	 * 闹铃的时间4
	 */
	public static final String KEY_ALARM4_TIME = "alarm4_time";
	/**
	 * 闹铃的时间5
	 */
	public static final String KEY_ALARM5_TIME = "alarm5_time";
	
	/**
	 * ����1����
	 */
	public static final String KEY_ALARM1_SWITCH = "alarm1_switch";
	
	/**
	 * ����2����
	 */
	public static final String KEY_ALARM2_SWITCH = "alarm2_switch";
	
	/**
	 * ����3����
	 */
	public static final String KEY_ALARM3_SWITCH = "alarm3_switch";
	
	/**
	 * ����4����
	 */
	public static final String KEY_ALARM4_SWITCH = "alarm4_switch";
	/**
	 * ����4����
	 */
	public static final String KEY_ALARM5_SWITCH = "alarm5_switch";
	
	/**
	 * ����1�ظ�
	 */
	public static final String KEY_ALARM1_REPEAT = "alarm1_repeat";
	
	/**
	 * ����2�ظ�
	 */
	public static final String KEY_ALARM2_REPEAT= "alarm2_repeat";
	
	/**
	 * ����3�ظ�
	 */
	public static final String KEY_ALARM3_REPEAT = "alarm3_repeat";
	
	/**
	 * ����4�ظ�
	 */
	public static final String KEY_ALARM4_REPEAT = "alarm4_repeat";
	/**
	 * ����4�ظ�
	 */
	public static final String KEY_ALARM5_REPEAT = "alarm5_repeat";
	
	/**
	 * ����1�ظ�����
	 */
	public static final String KEY_ALARM1_REPEATSWITCH = "alarm1_repeat_switch";
	
	/**
	 * ����2�ظ�����
	 */
	public static final String KEY_ALARM2_REPEATSWITCH= "alarm2_repeat_switch";
	
	/**
	 * ����3�ظ�����
	 */
	public static final String KEY_ALARM3_REPEATSWITCH = "alarm3_repeat_switch";
	
	/**
	 * ����4�ظ�����
	 */
	public static final String KEY_ALARM4_REPEATSWITCH = "alarm4_repeat_switch";
	/**
	 * ����4�ظ�����
	 */
	public static final String KEY_ALARM5_REPEATSWITCH = "alarm4_repeat_switch";
	
	//���ѹ���
	/**
	 * ���ѿ���
	 */
	public static final String KEY_REMINDER_SWITCH = "reminder_switch";
	
	/**
	 * ���Ѽ��ʱ��
	 */
	public static final String KEY_REMINDER_TIME = "reminder_time";
	
	/**
	 * ���ѿ�ʼʱ��
	 */
	public static final String KEY_REMINDER_STARTTIME = "reminder_starttime";
	
	/**
	 * ���ѽ���ʱ��
	 */
	public static final String KEY_REMINDER_ENDTIME = "reminder_endtime";
	
	/**
	 * ���������
	 */
	public static final String KEY_WEARINFO= "wear_info";
	/**
	 * 未读短信
	 */
	public static final String KEY_UNREADSMSCOUNT= "unread_smsCount";
	/**
	 * 未读电话
	 */
	public static final String KEY_UNREADPHONE= "unread_phone";
	/**
	 * 来电电话
	 */
	public static final String KEY_COMMING_PHONE= "comming_phone";
	/**
	 * 来电电话姓名
	 */
	public static final String KEY_COMMING_PHONE_NAME= "comming_phone_name";
	
}
