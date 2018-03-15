package com.telenordigital.officecanary;

public class Datapoint {
	long id;
	String deviceEUI;
	java.time.Instant timestamp;

	String appEUI;
	String gatewayEUI;
	String dataRate;
	String devAddr;
	double frequency;
	int rssi;
	double snr;

	int co2PPM;
	CO2Status co2Status;
	int resistance;

	// Total Volatile Organic Compound equivalent in Parts Per Billion
	int tvocPPB;

	public enum CO2Status {
		OK(0x00),
		Busy(0x01),
		Runin(0x10),
		Error(0x80);

		private final int value;

		private CO2Status(int value) {
			this.value = value;
		}

		public static CO2Status fromValue(int value) {
			for (CO2Status s : CO2Status.values()) {
				if (s.getValue() == value) {
					return s;
				}
			}
			return null;
		}

		private int getValue() {
			return value;
		}
	}
}
