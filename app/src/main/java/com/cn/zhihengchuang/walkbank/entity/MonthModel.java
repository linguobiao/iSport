package com.cn.zhihengchuang.walkbank.entity;

public class MonthModel extends EntityBase{
	private String userName;
	private String wareUUID;
	private int monthNumber;          //第几月
	private int yearNumber;           //年，如2014
	private int monthTotalSteps;      // 当月的总步数
	private int monthTotalCalories;   // 当月的总卡路里
	private int monthTotalDistance ;  // 当月的总路程
	private String  showDate;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getWareUUID() {
		return wareUUID;
	}
	public void setWareUUID(String wareUUID) {
		this.wareUUID = wareUUID;
	}
	public int getMonthNumber() {
		return monthNumber;
	}
	public void setMonthNumber(int monthNumber) {
		this.monthNumber = monthNumber;
	}
	public int getYearNumber() {
		return yearNumber;
	}
	public void setYearNumber(int yearNumber) {
		this.yearNumber = yearNumber;
	}
	public int getMonthTotalSteps() {
		return monthTotalSteps;
	}
	public void setMonthTotalSteps(int monthTotalSteps) {
		this.monthTotalSteps = monthTotalSteps;
	}
	public int getMonthTotalCalories() {
		return monthTotalCalories;
	}
	public void setMonthTotalCalories(int monthTotalCalories) {
		this.monthTotalCalories = monthTotalCalories;
	}
	public int getMonthTotalDistance() {
		return monthTotalDistance;
	}
	public void setMonthTotalDistance(int monthTotalDistance) {
		this.monthTotalDistance = monthTotalDistance;
	}
	public String getShowDate() {
		return showDate;
	}
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}
	public int getTodaySteps() {
		return todaySteps;
	}
	public void setTodaySteps(int todaySteps) {
		this.todaySteps = todaySteps;
	}
	public int getToadyCalories() {
		return toadyCalories;
	}
	public void setToadyCalories(int toadyCalories) {
		this.toadyCalories = toadyCalories;
	}
	public int getTodayDistance() {
		return todayDistance;
	}
	public void setTodayDistance(int todayDistance) {
		this.todayDistance = todayDistance;
	}
	private int todaySteps;     // 当天的总步数
	private int toadyCalories;  // 当天的总卡路里
	private int todayDistance;  // 当天的总路程


}
