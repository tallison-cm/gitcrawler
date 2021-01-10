package com.tallison.gitcrawler.model;

public class ExtensionData {

	private String name;
	private Integer lines;
	private Double size;
	
	public ExtensionData(String name, Integer lines, Double size) {
		super();
		this.name = name;
		this.lines = lines;
		this.size = size;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLines(Integer lines) {
		this.lines = lines;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public Integer getLines() {
		return lines;
	}

	public Double getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "ExtensionData [name=" + name + ", lines=" + lines + ", size=" + size + "]";
	}
	
}
