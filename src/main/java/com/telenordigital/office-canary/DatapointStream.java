package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryProto;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatapointStream {
	private static final Logger logger = Logger.getLogger(DatapointStream.class.getName());

	private final Iterator<AviaryProto.StreamDatapointsResponse> iter;

	DatapointStream(Iterator<AviaryProto.StreamDatapointsResponse> iter) {
		this.iter = iter;
	}

	public boolean hasNext() {
		try {
			return iter.hasNext();
		} catch (io.grpc.StatusRuntimeException x) {
			warn("error: {0}", x);
			return false;
		}
	}

	public Datapoint next() {
		AviaryProto.Datapoint dp = iter.next().getDatapoint();
		try {
			return Datapoint.fromProto(dp);
		} catch (com.google.protobuf.InvalidProtocolBufferException x) {
			warn("error: {0}", x);
			return null;
		}
	}

	private void warn(String msg, Object... params) {
		logger.log(Level.WARNING, msg, params);
	}
}
