package com.prospection.coding.assignment.domain;

public class PatientTimeline {
	
	private String medication;
	
	private String label;
	
	private int endYear;
	
	private int endMonth;
	
	private int endDay;	
	
	public PatientTimeline() {
		super();
	}

	public PatientTimeline(String medication, String label, int endYear, int endMonth, int endDay) {
		super();
		this.medication = medication;
		this.label = label;
		this.endYear = endYear;
		this.endMonth = endMonth;
		this.endDay = endDay;
	}

	public String getMedication() {
		return medication;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}


}
