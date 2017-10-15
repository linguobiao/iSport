package com.lingb.ride.bean;

import java.io.Serializable;
import java.util.Calendar;

public class Ride implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Calendar dateTime;
	private int totalTime;
	private double totalDistance;
	private double speed;
	private double speedMax;
	private double speedMin;
	private double cadence;
	private double cadenceMax;
	private double cadenceMin;
	
	public Calendar getDateTime() {
		return dateTime;
	}
	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getSpeedMax() {
		return speedMax;
	}
	public void setSpeedMax(double speedMax) {
		this.speedMax = speedMax;
	}
	public double getSpeedMin() {
		return speedMin;
	}
	public void setSpeedMin(double speedMin) {
		this.speedMin = speedMin;
	}
	public double getCadence() {
		return cadence;
	}
	public void setCadence(double cadence) {
		this.cadence = cadence;
	}
	public double getCadenceMax() {
		return cadenceMax;
	}
	public void setCadenceMax(double cadenceMax) {
		this.cadenceMax = cadenceMax;
	}
	public double getCadenceMin() {
		return cadenceMin;
	}
	public void setCadenceMin(double cadenceMin) {
		this.cadenceMin = cadenceMin;
	}

}
