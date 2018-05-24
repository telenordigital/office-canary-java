package com.telenordigital.officecanary;

import org.junit.Test;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OfficeCanaryTest {
	private static final Logger logger = Logger.getLogger(OfficeCanaryTest.class.getName());

	@Test
	public void testStreamDatapoints() throws Exception {
		OfficeCanary oc = new OfficeCanary("YOUR TOKEN GOES HERE");

		OfficeCanary.DatapointStream stream = oc.streamDatapoints();
		while (stream.hasNext()) {
			Datapoint d = stream.next();
			if (d instanceof LoRaDatapoint) {
				LoRaDatapoint lora = (LoRaDatapoint) d;
				info("Got LoRa datapoint from device {0} with RSSI {1}", lora.deviceEUI, lora.rssi);
			}
			if (d instanceof CO2Datapoint) {
				CO2Datapoint co2 = (CO2Datapoint) d;
				info("Got CO2 datapoint from device {0} with CO2 {1}", co2.deviceEUI, co2.co2PPM);
			}
			if (d == null) {
				info("Got unknown datapoint (client should be updated)");
			}
		}

		oc.shutdown();
	}

	private void info(String msg, Object... params) {
		logger.log(Level.INFO, msg, params);
	}
}
