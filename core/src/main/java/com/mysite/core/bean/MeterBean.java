package com.mysite.core.bean;

public class MeterBean {

	private double m15_rate;
	private double m1_rate;
	private double m5_rate;
	private double mean_rate;
	private long count;
	private String units;

	public MeterBean(double m15_rate, double m1_rate, double m5_rate, double mean_rate, long count,String units) {
		super();
		this.m15_rate = m15_rate;
		this.m1_rate = m1_rate;
		this.m5_rate = m5_rate;
		this.mean_rate = mean_rate;
		this.count=count;
		this.units = units;
	}

	public double getM15_rate() {
		return m15_rate;
	}

	public void setM15_rate(double m15_rate) {
		this.m15_rate = m15_rate;
	}

	public double getM1_rate() {
		return m1_rate;
	}

	public void setM1_rate(double m1_rate) {
		this.m1_rate = m1_rate;
	}

	public double getM5_rate() {
		return m5_rate;
	}

	public void setM5_rate(double m5_rate) {
		this.m5_rate = m5_rate;
	}

	public double getMean_rate() {
		return mean_rate;
	}

	public void setMean_rate(double mean_rate) {
		this.mean_rate = mean_rate;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	

}
