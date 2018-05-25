package com.telenordigital.officecanary;

import org.junit.Test;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OfficeCanaryTest {
	private static final Logger logger = Logger.getLogger(OfficeCanaryTest.class.getName());

	private static final String apiToken = "YOUR TOKEN GOES HERE";

	@Test
	public void testGetDevices() throws Exception {
		OfficeCanary oc = new OfficeCanary(apiToken);

		Device[] devices = oc.getDevices();
		for (Device d : devices) {
			info("Got device {0}", d.eui);
		}

		oc.shutdown();
	}

	@Test
	public void testGetDatapoints() throws Exception {
		OfficeCanary oc = new OfficeCanary(apiToken);

		Datapoint[] datapoints = oc.getDatapoints("71-3b-3d-d2-84-32-7f-14", Instant.now().minusSeconds(3600), Instant.now());
		for (Datapoint d : datapoints) {
			printDatapoint(d);
		}

		oc.shutdown();
	}

	@Test
	public void testStreamDatapoints() throws Exception {
		OfficeCanary oc = new OfficeCanary(apiToken);

		DatapointStream stream = oc.streamDatapoints();
		while (stream.hasNext()) {
			printDatapoint(stream.next());
		}

		oc.shutdown();
	}

	void printDatapoint(Datapoint d) {
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

	private void info(String msg, Object... params) {
		logger.log(Level.INFO, msg, params);
	}
}
