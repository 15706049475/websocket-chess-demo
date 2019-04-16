package com.xk.demo.util;

public class Localtion {
	
	private Double x;
	private Double y;
	
	public Localtion() {
		// TODO Auto-generated constructor stub
	}
	
	public Localtion(Double x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Localtion [x=" + x + ", y=" + y + "]";
	}
	
}
