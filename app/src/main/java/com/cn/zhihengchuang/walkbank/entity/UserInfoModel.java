package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;

public class UserInfoModel extends EntityBase{
	//添加了2个，其它有些就暂未存储,如地区，签名等
	@Column(column = "ismetricsystem")
	private boolean ismetricsystem;  //是否是公制
	@Column(column = "avatar")
	private String avatar;  //头像
	@Column(column = "username")
	private String username;   // 用户名
	@Column(column = "nickname")
	private String nickname;   // 昵称
	@Column(column = "password")
	private String password;   // 密码
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public float getStep() {
		return step;
	}
	public void setStep(float step) {
		this.step = step;
	}
	
	@Column(column = "birthday")
	private String birthday;   // 生日   格式为1990-05-01
	@Column(column = "gender")
	private String gender;     // 性别
	@Column(column = "age")
	private int  age;        // 年龄
	@Column(column = "height")
	private float height;       // 身高
	public boolean isIsmetricsystem() {
		return ismetricsystem;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getTargetsteps() {
		return targetsteps;
	}
	public void setTargetsteps(int targetsteps) {
		this.targetsteps = targetsteps;
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
	public void setIsmetricsystem(boolean ismetricsystem) {
		this.ismetricsystem = ismetricsystem;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	@Column(column = "weight")
	private float weight;       // 体重
	@Column(column = "step")
	private float step;         // 步距
	@Column(column = "targetsteps")
	private int targetsteps;      // 目标步数
	@Column(column = "targetcalories")
	private float targetcalories;     // 目标卡路里
	@Column(column = "targetdistance")
	private float targetdistance;     // 目标距离
	@Column(column = "targetsleep")
	private int targetsleep;      // 目标睡眠


}
