package com.telenordigital.officecanary;

import com.google.protobuf.DoubleValue;
import com.google.protobuf.Timestamp;
import java.time.Instant;

class Proto {
	public static Double toDouble(DoubleValue d) {
		if (d != null) {
			return d.getValue();
		}
		return null;
	}

	public static Timestamp fromInstant(Instant i) {
		return Timestamp.newBuilder().setSeconds(i.getEpochSecond()).setNanos(i.getNano()).build();
	}

	public static Instant toInstant(Timestamp t) {
		return Instant.ofEpochSecond(t.getSeconds(), t.getNanos());
	}
}