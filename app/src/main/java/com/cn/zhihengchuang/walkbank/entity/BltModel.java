package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;

public class BltModel extends EntityBase{
	// 蓝牙硬件所涉及的数据
	@Column(column = "bltname")
	private String bltname; // 设备名字
	@Column(column = "bltid")
	private String bltid; // 设备UUID
	@Column(column = "bltversion")
	private String bltversion; // 硬件版本
	@Column(column = "bltelec")
	private String bltelec; // 电量
	@Column(column = "bltrssi")
	private String bltrssi; // RSSI
	@Column(column = "bltelecstate")
	private int bltelecstate; // 充电状态
	@Column(column = "islock")
	private boolean islock; // 是否设备了密码，被锁了
	@Column(column = "isconnected")
	private boolean isconnected; // 是否已经连接
	@Column(column = "isignore")
	private boolean isignore; // 是否被忽略
	@Column(column = "isbinding")
	private boolean isbinding; // 是否绑定了
	@Column(column = "isnewdevice")
	private boolean isnewdevice; // 是否是新设备
	@Column(column = "hardtype")
	private String hardtype; // 硬件型号
	@Column(column = "hardversion")
	private int hardversion; // 硬件版本
	
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Column(column = "firmversion")
	private int firmversion; // 固件版本
	@Column(column = "stepsize")
	private int stepsize; // 步距
	public String getBltname() {
		return bltname;
	}
	public void setBltname(String bltname) {
		this.bltname = bltname;
	}
	public String getBltid() {
		return bltid;
	}
	public void setBltid(String bltid) {
		this.bltid = bltid;
	}
	public String getBltversion() {
		return bltversion;
	}
	public void setBltversion(String bltversion) {
		this.bltversion = bltversion;
	}
	public String getBltelec() {
		return bltelec;
	}
	public void setBltelec(String bltelec) {
		this.bltelec = bltelec;
	}
	public String getBltrssi() {
		return bltrssi;
	}
	public void setBltrssi(String bltrssi) {
		this.bltrssi = bltrssi;
	}
	public int getBltelecstate() {
		return bltelecstate;
	}
	public void setBltelecstate(int bltelecstate) {
		this.bltelecstate = bltelecstate;
	}
	public boolean isIslock() {
		return islock;
	}
	public void setIslock(boolean islock) {
		this.islock = islock;
	}
	public boolean isIsconnected() {
		return isconnected;
	}
	public void setIsconnected(boolean isconnected) {
		this.isconnected = isconnected;
	}
	public boolean isIsignore() {
		return isignore;
	}
	public void setIsignore(boolean isignore) {
		this.isignore = isignore;
	}
	public boolean isIsbinding() {
		return isbinding;
	}
	public void setIsbinding(boolean isbinding) {
		this.isbinding = isbinding;
	}
	public boolean isIsnewdevice() {
		return isnewdevice;
	}
	public void setIsnewdevice(boolean isnewdevice) {
		this.isnewdevice = isnewdevice;
	}
	public String getHardtype() {
		return hardtype;
	}
	public void setHardtype(String hardtype) {
		this.hardtype = hardtype;
	}
	public int getHardversion() {
		return hardversion;
	}
	public void setHardversion(int hardversion) {
		this.hardversion = hardversion;
	}
	public int getFirmversion() {
		return firmversion;
	}
	public void setFirmversion(int firmversion) {
		this.firmversion = firmversion;
	}
	public int getStepsize() {
		return stepsize;
	}
	public void setStepsize(int stepsize) {
		this.stepsize = stepsize;
	}
	public int getTargetstep() {
		return targetstep;
	}
	public void setTargetstep(int targetstep) {
		this.targetstep = targetstep;
	}
	public int getBirthday() {
		return birthday;
	}
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	@Column(column = "targetstep")
	private int targetstep; // 目标步数
	@Column(column = "weight")
	private int weight; // 体重
	@Column(column = "birthday")
	private int birthday; // 生日

}
