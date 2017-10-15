package com.cn.zhihengchuang.walkbank.entity;

import java.util.ArrayList;

import com.lidroid.xutils.db.annotation.Column;

public class PedometerModel extends EntityBase{
	@Column(column = "uuid")
	private String uuid;           // 设备uuid
	@Column(column = "username")
	private String username;           // 用户名
	@Column(column = "datestring")
	private String datestring;         // 日期
	@Column(column = "totalbytes")
	private int totalbytes;         // 数据包 。
	@Column(column = "settingbytes")
	private int settingbytes;
	@Column(column = "totalsteps")
	private int totalsteps;         // 当天的总步数
	@Column(column = "totalcalories")
	private int totalcalories;      // 当天的总卡路里
	@Column(column = "totaldistance")
	private int totaldistance ;     // 当天的总路程
	@Column(column = "totalsporttime")
	private int totalsporttime ;    // 当天的总运动时间
	@Column(column = "totalsleeptime")
	private int totalsleeptime ;    // 当天的总睡眠时间
	@Column(column = "totalstilltime")
	private int totalstilltime ;    // 当天的总静止时间
	@Column(column = "walktime")
	private int walktime ;          // 当天散步时间
	@Column(column = "slowwalktime")
	private int slowwalktime ;      // 当天慢步时间
	@Column(column = "yearmonth")
	private String yearmonth ;      // 月
	@Column(column = "yearweek")
	private String yearweek ;      // 周
	public String getYearweek() {
		return yearweek;
	}
	public void setYearweek(String yearweek) {
		this.yearweek = yearweek;
	}
	public String getYearmonth() {
		return yearmonth;
	}
	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}
	public ArrayList<SportsModel> getSportsArray() {
		return sportsArray;
	}
	public void setSportsArray(ArrayList<SportsModel> sportsArray) {
		this.sportsArray = sportsArray;
	}
	public ArrayList<SleepModel> getSleepArray() {
		return sleepArray;
	}
	public void setSleepArray(ArrayList<SleepModel> sleepArray) {
		this.sleepArray = sleepArray;
	}
	@Column(column = "midwalktime")
	private int midwalktime ;       // 当天中等散步时间
	@Column(column = "fastwalktime")
	private int fastwalktime ;      // 当天快速散步时间
	@Column(column = "slowruntime")
	private int slowruntime ;       // 当天慢速跑步时间
	@Column(column = "midruntime")
	private int midruntime ;        // 当天中等跑步时间
	@Column(column = "fastruntime")
	private int fastruntime ;       // 当天快速跑步时间
	@Column(column = "targetstep")
	private int targetstep;         // 目标步数
	@Column(column = "targetcalories")
	private float targetcalories;       // 目标卡路里
	@Column(column = "targetdistance")
	private float targetdistance;       // 目标距离
	@Column(column = "targetsleep")
	private int targetsleep;        // 目标睡眠
	@Column(column = "sleeptodaystarttime")
	private int sleeptodaystarttime;// 今天开始的睡眠时间
	@Column(column = "sleeptodayendtime")
	private int sleeptodayendtime;  // 今天结束的睡眠时间
	@Column(column = "sleepnextstartTime")
	private int sleepnextstartTime; // 明天开始睡眠的时间
	@Column(column = "stepSize")
	private int stepSize;           // 步距
	@Column(column = "weight")
	private float weight;               // 体重
	@Column(column = "sportsArray")
	private ArrayList<SportsModel> sportsArray;
	@Column(column = "sleepArray")
	private ArrayList<SleepModel>  sleepArray;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getStepSize() {
		return stepSize;
	}
	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	@Column(column = "lastsleeparray")
	private int[] lastsleeparray;      // 昨天的睡眠
	@Column(column = "issaveallday")
	private boolean issaveallday;            // 是否保存了全天的数据
	@Column(column = "detailsteps")
	private int[] detailsteps;
	@Column(column = "detailsleeps")
	private int[] detailsleeps;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDatestring() {
		return datestring;
	}
	public void setDatestring(String datestring) {
		this.datestring = datestring;
	}
	public int getTotalbytes() {
		return totalbytes;
	}
	public void setTotalbytes(int totalbytes) {
		this.totalbytes = totalbytes;
	}
	public int getSettingbytes() {
		return settingbytes;
	}
	public void setSettingbytes(int settingbytes) {
		this.settingbytes = settingbytes;
	}
	public int getTotalsteps() {
		return totalsteps;
	}
	public void setTotalsteps(int totalsteps) {
		this.totalsteps = totalsteps;
	}
	public int getTotalcalories() {
		return totalcalories;
	}
	public void setTotalcalories(int totalcalories) {
		this.totalcalories = totalcalories;
	}
	public int getTotaldistance() {
		return totaldistance;
	}
	public void setTotaldistance(int totaldistance) {
		this.totaldistance = totaldistance;
	}
	public int getTotalsporttime() {
		return totalsporttime;
	}
	public void setTotalsporttime(int totalsporttime) {
		this.totalsporttime = totalsporttime;
	}
	public int getTotalsleeptime() {
		return totalsleeptime;
	}
	public void setTotalsleeptime(int totalsleeptime) {
		this.totalsleeptime = totalsleeptime;
	}
	public int getTotalstilltime() {
		return totalstilltime;
	}
	public void setTotalstilltime(int totalstilltime) {
		this.totalstilltime = totalstilltime;
	}
	public int getWalktime() {
		return walktime;
	}
	public void setWalktime(int walktime) {
		this.walktime = walktime;
	}
	public int getSlowwalktime() {
		return slowwalktime;
	}
	public void setSlowwalktime(int slowwalktime) {
		this.slowwalktime = slowwalktime;
	}
	public int getMidwalktime() {
		return midwalktime;
	}
	public void setMidwalktime(int midwalktime) {
		this.midwalktime = midwalktime;
	}
	public int getFastwalktime() {
		return fastwalktime;
	}
	public void setFastwalktime(int fastwalktime) {
		this.fastwalktime = fastwalktime;
	}
	public int getSlowruntime() {
		return slowruntime;
	}
	public void setSlowruntime(int slowruntime) {
		this.slowruntime = slowruntime;
	}
	public int getMidruntime() {
		return midruntime;
	}
	public void setMidruntime(int midruntime) {
		this.midruntime = midruntime;
	}
	public int getFastruntime() {
		return fastruntime;
	}
	public void setFastruntime(int fastruntime) {
		this.fastruntime = fastruntime;
	}
	public int getTargetstep() {
		return targetstep;
	}
	public void setTargetstep(int targetstep) {
		this.targetstep = targetstep;
	}
	public float getTargetcalories() {
		return targetcalories;
	}
	public void setTargetcalories(float targetcalories) {
		this.targetcalories = targetcalories;
	}
	public float getTargetdistance() {
		return targetdistance;
	}
	public void setTargetdistance(float targetdistance) {
		this.targetdistance = targetdistance;
	}
	public int getTargetsleep() {
		return targetsleep;
	}
	public void setTargetsleep(int targetsleep) {
		this.targetsleep = targetsleep;
	}
	public int getSleeptodaystarttime() {
		return sleeptodaystarttime;
	}
	public void setSleeptodaystarttime(int sleeptodaystarttime) {
		this.sleeptodaystarttime = sleeptodaystarttime;
	}
	public int getSleeptodayendtime() {
		return sleeptodayendtime;
	}
	public void setSleeptodayendtime(int sleeptodayendtime) {
		this.sleeptodayendtime = sleeptodayendtime;
	}
	public int getSleepnextstartTime() {
		return sleepnextstartTime;
	}
	public void setSleepnextstartTime(int sleepnextstartTime) {
		this.sleepnextstartTime = sleepnextstartTime;
	}
	public int[] getLastsleeparray() {
		return lastsleeparray;
	}
	public void setLastsleeparray(int[] lastsleeparray) {
		this.lastsleeparray = lastsleeparray;
	}
	public boolean isIssaveallday() {
		return issaveallday;
	}
	public void setIssaveallday(boolean issaveallday) {
		this.issaveallday = issaveallday;
	}
	public int[] getDetailsteps() {
		return detailsteps;
	}
	public void setDetailsteps(int[] detailsteps) {
		this.detailsteps = detailsteps;
	}
	public int[] getDetailsleeps() {
		return detailsleeps;
	}
	public void setDetailsleeps(int[] detailsleeps) {
		this.detailsleeps = detailsleeps;
	}
	public int[] getDetailcalories() {
		return detailcalories;
	}
	public void setDetailcalories(int[] detailcalories) {
		this.detailcalories = detailcalories;
	}
	public int[] getDetaildistans() {
		return detaildistans;
	}
	public void setDetaildistans(int[] detaildistans) {
		this.detaildistans = detaildistans;
	}
	@Column(column = "detailcalories")
	private int[] detailcalories;
	@Column(column = "detaildistans")
	private int[] detaildistans;

}
