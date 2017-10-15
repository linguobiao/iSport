package com.androidex.appformwork.preference;

import com.androidex.appformwork.preference.AppSettings.StringPreference;
import com.cn.zhihengchuang.walkbank.util.DateUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class MicroRecruitSettings extends AppSettings {

    private static final String SHARED_PREFERENCES_NAME = "microRecruit.settings";

    private final SharedPreferences mGlobalPreferences;
    private String date;
    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public MicroRecruitSettings(Context context)
    {
        mGlobalPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
    }

    @Override
    public SharedPreferences getGlobalPreferences() {
        return mGlobalPreferences;
    }

    public String getDefFrameworkPackageData() {
        return "{'tabbar':{'height':50,'focus':0,'display':1,'bg_color':'#F3F3F3','bg_image':'','is_frame':1,'frame_color':'#D1D1D1','items':[{'index':1,'uri':'mod://mr_main_findwork','text':'找工作','size':10,'type':1,'is_enable':1,'status':[{'state':1,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_home_page_selicon@2x.png','text_color':'#0075DFFF'},{'state':2,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_home_page_selicon@2x.png','text_color':'#0075DFFF'},{'state':0,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_home_page_icon@2x.png','text_color':'#6A6A6AFF'}]},{'index':2,'uri':'mod://mr_main_message','text':'消息','size':10,'type':1,'is_enable':1,'status':[{'state':1,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_dynamic_selicon@2x.png','text_color':'#0075DFFF'},{'state':2,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_dynamic_selicon@2x.png','text_color':'#0075DFFF'},{'state':0,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_dynamic_icon@2x.png','text_color':'#6A6A6AFF'}]},{'index':3,'uri':'mod://mr_main_me','text':'我','size':10,'type':1,'is_enable':1,'status':[{'state':1,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_me_selicon@2x.png','text_color':'#0075DFFF'},{'state':2,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_me_selicon@2x.png','text_color':'#0075DFFF'},{'state':0,'icon':'http://192.168.1.8:81/resource/system/ios/bottom_me_icon@2x.png','text_color':'#6A6A6AFF'}]}]}}";
    }

    public LongPreference CONFIG_DB_VER = new LongPreference("db_version", 0);

    // 定位
    public StringPreference GIS_LONGITUDE = new StringPreference("gis_longitude", "0.0");
    public StringPreference GIS_LATITUDE = new StringPreference("gis_latitude", "0.0");
    public StringPreference GIS_CITY_NAME = new StringPreference("gis_city_name", "深圳市");
    public StringPreference GIS_CITY_ID = new StringPreference("gis_city_id", "4403");
    public StringPreference LAST_CONNECT_MAC = new StringPreference("last_connect_mac", "");
    public StringPreference LAST_CONNECT_NAME = new StringPreference("last_connect_name", "");
   
    public StringPreference CALIBRATION_TIME = new StringPreference(DateUtil.getCurrentDate(), "");
    public StringPreference SPORT_HALF = new StringPreference(date+"sport_half", "");
    public StringPreference SPORT_HALF_TIME = new StringPreference(date+"sport_half_time", "");
    public StringPreference SLEEP_HALF = new StringPreference(date+"sleep_half", "");
    public StringPreference SLEEP_HALF_TIME = new StringPreference(date+"sleep_half_time", "");
    //首次获取历史数据
    public BooleanPreference FRIST_HOSTORY = new BooleanPreference("frist_hostory",true);
    public StringPreference LANG = new StringPreference("lang", "zh-cn");

    public void setGISInfo(String longitude, String latitude, String cityName, String cityId) {
        GIS_LONGITUDE.setValue(longitude);
        GIS_LATITUDE.setValue(latitude);
        GIS_CITY_NAME.setValue(cityName);
        GIS_CITY_ID.setValue(cityId);
    }

   

    // ///////////////用户信息/////////
    public StringPreference LOGIN_USER_CODE = new StringPreference("user_code", "");
    public StringPreference LOGIN_USER_TOKEN = new StringPreference("user_token", "");
    // 用户基本信息。
    public StringPreference USER_INFO_USERNAME = new StringPreference("user_name", "");
    public StringPreference USER_INFO_PHONE = new StringPreference("user_phone", "");
    public StringPreference USER_INFO_EMAIL = new StringPreference("user_email", "");
    public StringPreference USER_INFO_QQ = new StringPreference("user_qq", "");
    public StringPreference USER_INFO_AVATAR = new StringPreference("user_avatar", "");
    public StringPreference USER_INFO_BIRTH = new StringPreference("user_birth", "");
    public StringPreference USER_INFO_CARD = new StringPreference("user_card", "");
    public StringPreference USER_INFO_SIGN = new StringPreference("user_sign", "");
    public StringPreference USER_INFO_NICENAME = new StringPreference("user_nicename", "");
    public StringPreference USER_INFO_SIX = new StringPreference("user_six", "");

    // 用户通知设置
    public BooleanPreference USER_NOTICE_NODISTURB = new BooleanPreference("noDisturb", true);
    public BooleanPreference USER_NOTICE_INTERVIEWINVITE = new BooleanPreference("interviewInvite", true);
    public BooleanPreference USER_NOTICE_MYSUBSCRIBE = new BooleanPreference("mySubscribe", true);
    public BooleanPreference USER_NOTICE_PRIVACY = new BooleanPreference("privacy", true);

    // 用户隐私设置
    public BooleanPreference USER_PRIVACY_FINDMEBYCOMPANY = new BooleanPreference("findMeByCompany", true);
    public StringPreference USER_PRIVACY_STOPCOMPANYFINDME = new StringPreference("stopCompanyFindMe", "");

    public BooleanPreference IS_NEED_CONNECT = new BooleanPreference("IS_NEED_CONNECT", true);
   
}
