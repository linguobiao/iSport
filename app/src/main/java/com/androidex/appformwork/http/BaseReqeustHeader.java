package com.androidex.appformwork.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpRequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.cn.zhihengchuang.walkbank.util.DeviceConfiger;


public class BaseReqeustHeader implements AbsReqeustHeader {

    // 常量字符串
    private static String market = "市场";
    private static String device = "Android";

    // 系统常量
    private static String udid; // imsi
    private static String openudid; // imei
    private static String mac; // mac地址
    private static String deviceVer; // 设备操作系统版本
    private static String model; // 设备类型
    private static String screenwidth; // 屏幕宽
    private static String screenheight; // 屏幕高
    private static String screenDensity; // 屏幕密度
    private static String appVer; // app版本
    //private static String crack; // 是否root
    private static String tel; // 手机号码
    private static String telcom; // 运营商名称

    // 系统变量 设置监听
    private static String country; // 国家
    private static String lang; // 语言
    private static String screenOrientation; // 屏幕方向
    private static String net; // 网络类型

    // 服务器返回配置 appentry.php获取
    private static String dkey; // 设备的唯一识别码
    private static String source; // 用户的内内部来源
    private Context context;

    public BaseReqeustHeader(Context context)
    {
        this.context = context;

        // 监听变化
        lang = DeviceConfiger.getLanguage();
        country = DeviceConfiger.getCountry();
        net = DeviceConfiger.getNetTypeInChina();
        screenOrientation = String.valueOf(DeviceConfiger.getScreenOrientation());
        telcom = DeviceConfiger.getOperators();
        telcom = parmaURLEncode(telcom);

        market = parmaURLEncode(market);
        // source = parmaURLEncode(source);
        udid = DeviceConfiger.getDeviceId();
        mac = DeviceConfiger.getMacAddress();
        deviceVer = DeviceConfiger.getSystemVersion();
        screenwidth = String.valueOf(DeviceConfiger.getScreenWidth());
        screenheight = String.valueOf(DeviceConfiger.getScreenHeight());
        screenDensity = String.valueOf(DeviceConfiger.getScreenDensity());
        appVer = String.valueOf(DeviceConfiger.getAPPVersion());
       // crack = DeviceConfiger.getCrack();
        model = android.os.Build.MODEL;

        IntentFilter filter = new IntentFilter();
        // 屏幕方向改变监听 <uses-permission
        // Android:name="android.permission.CHANGE_CONFIGURATION" />
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        // 语言国家改变监听 <uses-permission
        // Android:name="android.permission.CHANGE_CONFIGURATION" />
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        // 网络变化监听 添加权限 <uses-permission
        // android:name="android.permission.ACCESS_NETWORK_STATE" />
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(changedReceiver, filter);

    }

    public void unRegisterchangedReceive() {
        context.unregisterReceiver(changedReceiver);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String getCmpId() {
        return "";
    }

    @Override
    public String getTelCom() {
        return telcom;
    }

    @Override
    public String getNetType() {
        return net;
    }

    @Override
    public String getScreenScale() {
        return screenDensity;
    }

    @Override
    public String getScreenRotate() {
        return screenOrientation;
    }

    @Override
    public String getScreenHeight() {
        return screenheight;
    }

    @Override
    public String getScreenWidth() {
        return screenwidth;
    }

    
    @Override
    public String getDeviceVer() {
        return deviceVer;
    }

    @Override
    public String getDevice() {
        return device;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String getAppVer() {
        return appVer;
    }

    @Override
    public String getMarket() {
        return market;
    }

    @Override
    public String getLang() {
        return lang;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getTel() {
        return tel;
    }

    @Override
    public String getMAC() {
        return mac;
    }

    @Override
    public String getIMEI() {
        return openudid;
    }

    public String getIMSI() {
        return udid;
    }

    @Override
    public String getPushToken() {
        // TODO 获取pushToken
        return "";
    }

    @Override
    public String getSource() {
        // TODO 获取source
        return source;
    }

    @Override
    public String getDKey() {
        // TODO 获取DKey
        return dkey;
    }

    @Override
    public String getUnique() {
        // TODO 获取unique
        return "";
    }

    @Override
    public String getFrom() {
        // TODO 获取内容提供商分成ID 比如用于和视频内容提供方在广告植入上进行分成
        return "";
    }

    @Override
    public String getModule() {
        // TODO 请求应用模块名称 与广告位配置中的模块限制对应
        return "";
    }

    @Override
    public String getAge() {
        // TODO 获取 age
        return "";
    }

    @Override
    public String getSDKVer() {
        // TODO SDK 版本号
        return "";
    }

    @Override
    public String getAppKey() {
        // TODO app 识别码
        return "";
    }

    @Override
    public String getCorpId() {
        // TODO 企业id
        return "";
    }

    private BroadcastReceiver changedReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
                // 更新 国家和语言
                lang = DeviceConfiger.getLanguage();
                country = DeviceConfiger.getCountry();
            } else if (intent.getAction().compareTo(Intent.ACTION_CONFIGURATION_CHANGED) == 0) {
                // 更新屏幕方向
                screenOrientation = String.valueOf(DeviceConfiger.getScreenOrientation());
            } else if (intent.getAction().compareTo("android.net.conn.CONNECTIVITY_CHANGE") == 0) {
                // 更新网络状态
                net = DeviceConfiger.getNetTypeInChina();
            }
        }
    };

    public String parmaURLEncode(String params) {
        try {
            return URLEncoder.encode(params, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return params;
    }

    @Override
    public String getUid() {
        return "";
    }

    @Override
    public String getSex() {
        return "";
    }

    /**
     * 当前地理坐标(经度)
     * 
     * @return
     */
    public String getLongitude() {
        return "";
    };

    /**
     * 当前地理坐标 (纬度)
     * 
     * @return
     */
    public String getLatitude() {
        return "";
    };

    /**
     * 
     * @return
     */
    public String getADSID() {
        return "";
    }

    /**
     * 
     * @return
     */
    public String getADID() {
        return "";
    }

    /**
     * 
     * @return
     */
    public String getUseragent() {
        return "";
    };

    public static void addHeaders(HttpRequest request, AbsReqeustHeader headers) {

        // //
        // if (!TextUtils.isEmpty(getBefVersionBuild())) {
        // request.addHeader("bef_version_build", getBefVersionBuild());
        // }
        // if (!TextUtils.isEmpty(getBefClienBuild())) {
        // request.addHeader("bef_client_build", getBefClienBuild());
        // }
        // if (!TextUtils.isEmpty(getBefPlatform())) {
        // request.addHeader("bef_platform", getBefPlatform());
        // }
        // if (!TextUtils.isEmpty(getBefOsVersion())) {
        // request.addHeader("bef_os_version", getBefOsVersion());
        // }

        //

        if (!TextUtils.isEmpty(headers.getIMSI())) {
            request.addHeader("bef-udid", headers.getIMSI());
        }
        if (!TextUtils.isEmpty(headers.getIMEI())) {
            request.addHeader("bef-openudid", headers.getIMEI());
        }
        if (!TextUtils.isEmpty(headers.getADID())) {
            request.addHeader("bef-dadid", headers.getADID());
        }
        if (!TextUtils.isEmpty(headers.getADSID())) {
            request.addHeader("bef-dasid", headers.getADSID());
        }
        if (!TextUtils.isEmpty(headers.getMAC())) {
            request.addHeader("bef-mac", headers.getMAC());
        }
        if (!TextUtils.isEmpty(headers.getTel())) {
            request.addHeader("bef-tel", headers.getTel());
        }
        if (!TextUtils.isEmpty(headers.getCorpId())) {
            request.addHeader("bef-corpid", headers.getCorpId());
        }
        if (!TextUtils.isEmpty(headers.getCountry())) {
            request.addHeader("bef-country", headers.getCountry());
        }
        if (!TextUtils.isEmpty(headers.getLang())) {
            request.addHeader("bef-lang", headers.getLang());
        }
        if (!TextUtils.isEmpty(headers.getAppKey())) {
            request.addHeader("bef-appkey", headers.getAppKey());
        }
        if (!TextUtils.isEmpty(headers.getAppVer())) {
            request.addHeader("bef-appver", headers.getAppVer());
        }
        if (!TextUtils.isEmpty(headers.getMarket())) {
            request.addHeader("bef-market", headers.getMarket());
        }
        if (!TextUtils.isEmpty(headers.getDevice())) {
            request.addHeader("bef-device", headers.getDevice());
        }
        if (!TextUtils.isEmpty(headers.getDeviceVer())) {
            request.addHeader("bef-devicever", headers.getDeviceVer());
        }
        if (!TextUtils.isEmpty(headers.getModel())) {
            request.addHeader("bef-model", headers.getModel());
        }
        if (!TextUtils.isEmpty(headers.getCrack())) {
            request.addHeader("bef-crack", headers.getCrack());
        }
        if (!TextUtils.isEmpty(headers.getSDKVer())) {
            request.addHeader("bef-sdkver", headers.getSDKVer());
        }
        if (!TextUtils.isEmpty(headers.getScreenWidth())) {
            request.addHeader("bef-screenwidth", headers.getScreenWidth());
        }
        if (!TextUtils.isEmpty(headers.getScreenHeight())) {
            request.addHeader("bef-screenheight", headers.getScreenHeight());
        }
        if (!TextUtils.isEmpty(headers.getScreenRotate())) {
            request.addHeader("bef-screenrotate", headers.getScreenRotate());
        }
        if (!TextUtils.isEmpty(headers.getScreenScale())) {
            request.addHeader("bef-screenscale", headers.getScreenScale());
        }
        if (!TextUtils.isEmpty(headers.getNetType())) {
            request.addHeader("bef-net", headers.getNetType());
        }
        if (!TextUtils.isEmpty(headers.getSex())) {
            request.addHeader("bef-sex", headers.getSex());
        }
        if (!TextUtils.isEmpty(headers.getAge())) {
            request.addHeader("bef-age", headers.getAge());
        }
        if (!TextUtils.isEmpty(headers.getModule())) {
            request.addHeader("bef-module", headers.getModule());
        }
        if (!TextUtils.isEmpty(headers.getFrom())) {
            request.addHeader("bef-from", headers.getFrom());
        }
        if (!TextUtils.isEmpty(headers.getUseragent())) {
            request.addHeader("bef-useragent", headers.getUseragent());
        }
        if (!TextUtils.isEmpty(headers.getUnique())) {
            request.addHeader("bef-unique", headers.getUnique());
        }
        if (!TextUtils.isEmpty(headers.getUid())) {
            request.addHeader("bef-uid", headers.getUid());
        }
        if (!TextUtils.isEmpty(headers.getUid())) {
            request.addHeader("bef-cmpid", headers.getCmpId());
        }
        if (!TextUtils.isEmpty(headers.getPushToken())) {
            request.addHeader("bef-token", headers.getPushToken());
        }
        if (!TextUtils.isEmpty(headers.getDKey())) {
            request.addHeader("bef-dkey", headers.getDKey());
        }
        if (!TextUtils.isEmpty(headers.getSource())) {
            request.addHeader("bef-source", headers.getSource());
        }
        if (!TextUtils.isEmpty(headers.getTelCom())) {
            request.addHeader("bef-telcom", headers.getTelCom());
        }
        if (!TextUtils.isEmpty(headers.getLatitude())) {
            request.addHeader("bef-gpsx", headers.getLatitude());
        }
        if (!TextUtils.isEmpty(headers.getLongitude())) {
            request.addHeader("bef-gpxy", headers.getLongitude());
        }
    }

	@Override
	public String getCrack() {
		// TODO Auto-generated method stub
		return null;
	}

}
