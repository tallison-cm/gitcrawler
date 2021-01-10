package com.tallison.gitcrawler.util;

public class SizeConverter {
	
	public static Double toBytes(String size, String unity) {
		Double dSize = Double.parseDouble(size);
		switch (unity) {
			case "kb":
				return dSize * 1024;
			case "mb":
				return dSize * Math.pow(1024, 2);
			case "gb":
				return dSize * Math.pow(1024, 3);
			case "tb":
				return dSize * Math.pow(1024, 4);
			default:
				return dSize;
		}

	}

}
