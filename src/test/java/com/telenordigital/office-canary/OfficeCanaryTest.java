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
			info("Got datapoint from device {0} with CO2 {1}", d.deviceEUI, d.co2PPM);
		}

		oc.shutdown();
	}

	private void info(String msg, Object... params) {
		logger.log(Level.INFO, msg, params);
	}
}
