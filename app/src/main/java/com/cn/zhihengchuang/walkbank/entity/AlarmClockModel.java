package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;


public class AlarmClockModel extends EntityBase{
	@Column(column = "name")
	private String username;
	@Column(column = "isopen")
	private boolean isopen;
	@Column(column = "alarmtime")
	private String alarmtime;
	@Column(column = "weekarray")
	private String weekarray;
	@Column(column = "hour")
	private int hour;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isIsopen() {
		return isopen;
	}
	public void setIsopen(boolean isopen) {
		this.isopen = isopen;
	}
	public String getAlarmtime() {
		return alarmtime;
	}
	public void setAlarmtime(String alarmtime) {
		this.alarmtime = alarmtime;
	}
	public String getWeekarray() {
		return weekarray;
	}
	public void setWeekarray(String weekarray) {
		this.weekarray = weekarray;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getOrderindex() {
		return orderindex;
	}
	public void setOrderindex(float orderindex) {
		this.orderindex = orderindex;
	}
	@Column(column = "minutes")
	private int  minutes;
	@Column(column = "seconds")
	private int seconds;
	@Column(column = "repeat")
	private int repeat;
	@Column(column = "height")
	private float height;   // 如果选了7天可能需要2行。
	@Column(column = "orderindex")
	private float orderindex;

}
