package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;

public class BraceletInfoModel extends EntityBase{
	@Column(column = "name")
    private String name;
	@Column(column = "target")
    private String target;    //1， 2， 3
	@Column(column = "islefthand")
    private boolean islefthand;     //是否带在左手
	@Column(column = "showtime")
    private boolean showtime;
	@Column(column = "showsteps")
    private boolean showsteps;
	@Column(column = "showka")
    private boolean showka;
	@Column(column = "showdistance")
    private boolean showdistance;
	@Column(column = "isshowmetricsystem")
    private boolean isshowmetricsystem;  //是否是公制
	@Column(column = "isautomaticalarmClock")
    private boolean isautomaticalarmClock;    //是否自动闹钟
	@Column(column = "ishandalarmClock")
    private boolean ishandalarmClock;    //是否自动闹钟
	@Column(column = "is24hourstime")
	private boolean is24hourstime;  //是否是24小时制
	@Column(column = "alarmarray")
	private AlarmClockModel alarmarray;  //闹钟
	@Column(column = "issyn")
	private boolean issyn;  //是否实时同步
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	public boolean isIslefthand() {
		return islefthand;
	}

	public void setIslefthand(boolean islefthand) {
		this.islefthand = islefthand;
	}

	public boolean isShowtime() {
		return showtime;
	}

	public void setShowtime(boolean showtime) {
		this.showtime = showtime;
	}

	public boolean isShowsteps() {
		return showsteps;
	}

	public void setShowsteps(boolean showsteps) {
		this.showsteps = showsteps;
	}

	public boolean isShowka() {
		return showka;
	}

	public void setShowka(boolean showka) {
		this.showka = showka;
	}

	public boolean isShowdistance() {
		return showdistance;
	}

	public void setShowdistance(boolean showdistance) {
		this.showdistance = showdistance;
	}

	public boolean isIsshowmetricsystem() {
		return isshowmetricsystem;
	}

	public void setIsshowmetricsystem(boolean isshowmetricsystem) {
		this.isshowmetricsystem = isshowmetricsystem;
	}

	public boolean isIsautomaticalarmClock() {
		return isautomaticalarmClock;
	}

	public void setIsautomaticalarmClock(boolean isautomaticalarmClock) {
		this.isautomaticalarmClock = isautomaticalarmClock;
	}

	public boolean isIshandalarmClock() {
		return ishandalarmClock;
	}

	public void setIshandalarmClock(boolean ishandalarmClock) {
		this.ishandalarmClock = ishandalarmClock;
	}

	public boolean isIs24hourstime() {
		return is24hourstime;
	}

	public void setIs24hourstime(boolean is24hourstime) {
		this.is24hourstime = is24hourstime;
	}

	public AlarmClockModel getAlarmarray() {
		return alarmarray;
	}

	public void setAlarmarray(AlarmClockModel alarmarray) {
		this.alarmarray = alarmarray;
	}

	public boolean isIssyn() {
		return issyn;
	}

	public void setIssyn(boolean issyn) {
		this.issyn = issyn;
	}

	public boolean isLongtimesetremind() {
		return longtimesetremind;
	}

	public void setLongtimesetremind(boolean longtimesetremind) {
		this.longtimesetremind = longtimesetremind;
	}

	public boolean isPreventlossremind() {
		return preventlossremind;
	}

	public void setPreventlossremind(boolean preventlossremind) {
		this.preventlossremind = preventlossremind;
	}

	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDeviceversion() {
		return deviceversion;
	}

	public void setDeviceversion(String deviceversion) {
		this.deviceversion = deviceversion;
	}

	public float getDeviceelectricity() {
		return deviceelectricity;
	}

	public void setDeviceelectricity(float deviceelectricity) {
		this.deviceelectricity = deviceelectricity;
	}

	public String getDefaultname() {
		return defaultname;
	}

	public void setDefaultname(String defaultname) {
		this.defaultname = defaultname;
	}

	public int getTimeabsolutevalue() {
		return timeabsolutevalue;
	}

	public void setTimeabsolutevalue(int timeabsolutevalue) {
		this.timeabsolutevalue = timeabsolutevalue;
	}

	public boolean isTimezone() {
		return timezone;
	}

	public void setTimezone(boolean timezone) {
		this.timezone = timezone;
	}

	public int getStepnumber() {
		return stepnumber;
	}

	public void setStepnumber(int stepnumber) {
		this.stepnumber = stepnumber;
	}
	@Column(column = "longtimesetremind")
	private boolean longtimesetremind;  //久坐提醒
	@Column(column = "preventlossremind")
	private boolean preventlossremind;  //防止丢失的提醒
	@Column(column = "orderid")
	private int orderid;  //排列顺序ID
	@Column(column = "deviceid")
	private String deviceid;  //设备唯一ID
	@Column(column = "deviceversion")
	private String deviceversion;  //硬件版本
	@Column(column = "deviceelectricity")
	private float deviceelectricity;  //电量
	@Column(column = "defaultname")
	private String defaultname; //默认名字
	@Column(column = "timeabsolutevalue")
	//界面上没有的，测试使用
	private int  timeabsolutevalue;  //时区绝对值
	@Column(column = "timezone")
	private boolean timezone;  //是否是正时区
	@Column(column = "stepbumber")
	private int  stepnumber;  //目标步数

}
