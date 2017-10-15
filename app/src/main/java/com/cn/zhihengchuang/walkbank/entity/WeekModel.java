package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;

public class WeekModel extends EntityBase{
	@Column(column = "username")
	private String username;
	@Column(column = "wareuuid")
	private String wareuuid;
	@Column(column = "weeknumber")
	private int weeknumber;         // 第几周
	@Column(column = "yearnumber")
	private int yearnumber;         // 年，如2014
	@Column(column = "weektotalsteps")
	private int weektotalsteps;     // 本周的总步数
	@Column(column = "weektotalcalories")
	private int weektotalcalories;  // 本周的总卡路里
	@Column(column = "weektotaldistance")
	private int weektotaldistance ; // 本周的总路程
	@Column(column = "showdate")
	private String showdate;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getWareuuid() {
		return wareuuid;
	}
	public void setWareuuid(String wareuuid) {
		this.wareuuid = wareuuid;
	}
	public int getWeeknumber() {
		return weeknumber;
	}
	public void setWeeknumber(int weeknumber) {
		this.weeknumber = weeknumber;
	}
	public int getYearnumber() {
		return yearnumber;
	}
	public void setYearnumber(int yearnumber) {
		this.yearnumber = yearnumber;
	}
	public int getWeektotalsteps() {
		return weektotalsteps;
	}
	public void setWeektotalsteps(int weektotalsteps) {
		this.weektotalsteps = weektotalsteps;
	}
	public int getWeektotalcalories() {
		return weektotalcalories;
	}
	public void setWeektotalcalories(int weektotalcalories) {
		this.weektotalcalories = weektotalcalories;
	}
	public int getWeektotaldistance() {
		return weektotaldistance;
	}
	public void setWeektotaldistance(int weektotaldistance) {
		this.weektotaldistance = weektotaldistance;
	}
	public String getShowdate() {
		return showdate;
	}
	public void setShowdate(String showdate) {
		this.showdate = showdate;
	}
	public int getTodaysteps() {
		return todaysteps;
	}
	public void setTodaysteps(int todaysteps) {
		this.todaysteps = todaysteps;
	}
	public int getTodaycalories() {
		return todaycalories;
	}
	public void setTodaycalories(int todaycalories) {
		this.todaycalories = todaycalories;
	}
	public int getTodaydistance() {
		return todaydistance;
	}
	public void setTodaydistance(int todaydistance) {
		this.todaydistance = todaydistance;
	}
	@Column(column = "todaysteps")
	private int todaysteps;     // 当天的总步数
	@Column(column = "todaycalories")
	private int todaycalories;  // 当天的总卡路里
	@Column(column = "todaydistance")
	private int todaydistance ; // 当天的总路程


}
