package com.cn.zhihengchuang.walkbank.entity;

import com.lidroid.xutils.db.annotation.Column;

public class SportsModel extends EntityBase{
	@Column(column = "ware_uuid")
	public String ware_uuid;
	@Column(column = "date_day")
	public String date_day;
	@Column(column = "last_order")
	public int  last_order;
	@Column(column = "current_order")
	public int current_order;
	@Column(column = "steps")
	public int steps ;
	@Column(column = "calorie")
	public int calorie ;
	@Column(column = "distance")
	public float distance ;
	

}
