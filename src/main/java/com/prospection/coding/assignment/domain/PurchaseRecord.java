package com.prospection.coding.assignment.domain;

public class PurchaseRecord implements Comparable<PurchaseRecord>{

    private int day;
    private String medication;
    private int patientId;
    private String reasonLocation;

    public PurchaseRecord(int day, String medication, int patientId) {
        this.day = day;
        this.medication = medication;
        this.patientId = patientId;
    }

    public PurchaseRecord(int day, String medication, int patientId, String reasonLocation) {
		super();
		this.day = day;
		this.medication = medication;
		this.patientId = patientId;
		this.reasonLocation = reasonLocation;
	}

	public int getDay() {
        return day;
    }

    public PurchaseRecord setDay(int day) {
        this.day = day;
        return this;
    }

    public String getMedication() {
        return medication;
    }

    public PurchaseRecord setMedication(String medication) {
        this.medication = medication;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public PurchaseRecord setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public String toString() {
        return day + "," + medication + "," + patientId;
    }
    
	public String getReasonLocation() {
		return reasonLocation;
	}

	public void setReasonLocation(String reasonLocation) {
		this.reasonLocation = reasonLocation;
	}

	@Override
	public int compareTo(PurchaseRecord pRec) {
		if(getPatientId() <= 0 || pRec.getPatientId() <= 0)
			return 0;
		
		Integer src = Integer.valueOf(getPatientId());
		Integer dest = Integer.valueOf(pRec.getPatientId());
		return src.compareTo(dest); 

	}
}