package com.telenordigital.officecanary;

public class CO2Datapoint extends DatapointBase implements Datapoint {
	public int co2PPM;
	public CO2Status co2Status;
	public int resistance;

	// Total Volatile Organic Compound equivalent in Parts Per Billion
	public int tvocPPB;

	public static CO2Datapoint fromProto(long id, String deviceEUI, java.time.Instant timestamp, OfficeCanaryProto.CO2Datapoint dp) {
		CO2Datapoint d = new CO2Datapoint();
		d.id = id;
		d.deviceEUI = deviceEUI;
		d.timestamp = timestamp;
		d.co2PPM = dp.getCo2Ppm();
		d.co2Status = CO2Datapoint.CO2Status.fromValue(dp.getCo2Status().getNumber());
		d.resistance = dp.getResistance();
		d.tvocPPB = dp.getTvocPpb();
		return d;
	}

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
