package com.advanchip.mobile_controlledlighting.models;

public class Switch {
	private String switchId;
	private int switchState;
	private String switchName;
	private String switchSerialNumber;
	
	
	
	public String getSwitchSerialNumber() {
		return switchSerialNumber;
	}
	public void setSwitchSerialNumber(String switchSerialNumber) {
		this.switchSerialNumber = switchSerialNumber;
	}
	public String getSwitchId() {
		return switchId;
	}
	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}
	public int getSwitchState() {
		return switchState;
	}
	public void setSwitchState(int switchState) {
		this.switchState = switchState;
	}
	public String getSwitchName() {
		return switchName;
	}
	public void setSwitchName(String switchName) {
		this.switchName = switchName;
	}
}
