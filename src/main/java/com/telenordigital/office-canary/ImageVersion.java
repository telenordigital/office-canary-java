package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryProto;

public class ImageVersion {
	public int major;
	public int minor;
	public int revision;
	public int buildNum;

	public static ImageVersion fromProto(AviaryProto.ImageVersion vp) {
		ImageVersion v = new ImageVersion();
		v.major = vp.getMajor();
		v.minor = vp.getMinor();
		v.revision = vp.getRevision();
		v.buildNum = vp.getBuildNum();
		return v;
	}
}
