package com.prospection.coding.assignment.domain;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalysisResult {

    private Map<PatientType, Integer> patients;
    private Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> statistics;
    private Map<String, String> mapViolationDetails;
	   
    public AnalysisResult() {
        patients = new EnumMap<>(PatientType.class);
    	statistics = new LinkedHashMap<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>>();
        mapViolationDetails = new LinkedHashMap<>();
    }

    public void putTotal(PatientType patientType, int total) {
        this.patients.put(patientType, total);
    }

    public Integer getTotal(PatientType patientType) {
        return this.patients.get(patientType);
    }

    public Map<PatientType, Integer> getPatients() {
        return patients;
    }

    public AnalysisResult setPatients(Map<PatientType, Integer> patients) {
        this.patients = patients;
        return this;
    }
    
    public Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> getStatistics() {
		return statistics;
	}

	public void setStatistics(Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> statistics) {
		this.statistics = statistics;
	}    


	public Map<String, String> getViolatedPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VIOLATED);
	}

	public Map<String, String> getNocomedPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VALID_NO_COMED);
	}

	public Map<String, String> getBiSwitchPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VALID_BI_SWITCH);
	}

	public Map<String, String> getIbSwitchPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VALID_IB_SWITCH);
	}

	public Map<String, String> getiTrialPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VALID_I_TRIAL);
	}

	public Map<String, String> getbTrialPatDetails() {
		return getPatientDetails(AnalysisResult.PatientType.VALID_B_TRIAL);
	}

	public Map<PatientType, String> getPatientTypeNameMap() {
        return Arrays.stream(PatientType.values())
                .collect(toMap(identity(), PatientType::getName));
    }
	
	public Map<String, String> getMapViolationDetails() {
		return mapViolationDetails;
	}

	public void setMapViolationDetails(Map<String, String> mapViolationDetails) {
		this.mapViolationDetails = mapViolationDetails;
	}

	/**
	 * To return 2 column data PatientId and Details like below 
	 * 11  -	B(212), B(270), B(360), B(366), B(465), B(528), B(676), B(890) 
	 * 
	 * @param type
	 * @return
	 */
	public Map<String, String> getPatientDetails(AnalysisResult.PatientType type) {
    	
    	Map<String, String> response = new HashMap<>();
    	
    	LinkedHashMap<String, List<PurchaseRecord>> singleCat = new  LinkedHashMap<String, List<PurchaseRecord>>();
    	if(statistics.containsKey(type)) {
    		singleCat = statistics.get(type);
    	}
    	
    	for( Map.Entry<String, List<PurchaseRecord>> entry : singleCat.entrySet()){
    		  String key = entry.getKey();
    		  List<PurchaseRecord> value = entry.getValue();
    		  
    		  String str = value.stream()
    		  .map(pr -> pr.getMedication() + "(" + pr.getDay() + ")")
    		  .collect(Collectors.joining(", "));

    		  response.put(key, str);
    	}
    	
    	return response;
    }
    
	/**
	 * Data for Timeline chart
	 * returning flat map of Patients with PatiemtTimelime records
	 * 
	 * @return
	 */
    public Map<String, List<PatientTimeline>> getTestPatienttl() {
    	HashMap<String, List<PatientTimeline>> allPatients = new HashMap<String, List<PatientTimeline>>();    	
    	List<PatientTimeline> listPT = new ArrayList<PatientTimeline>();
    	PatientTimeline pt = new PatientTimeline();
    	
    	for (Map.Entry<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> entry : statistics.entrySet()) {
    		LinkedHashMap<String, List<PurchaseRecord>> values = entry.getValue();
    		
    		for(Map.Entry<String, List<PurchaseRecord>> entryInner: values.entrySet()) {
    			List<PurchaseRecord> lstPr = entryInner.getValue();
    			String key = String.valueOf(entryInner.getKey());
				LocalDateTime initialDate =  LocalDateTime.of(2018, Month.JANUARY, 01, 01, 01);
				
				for(PurchaseRecord pr: lstPr) {

    				if(allPatients.containsKey(key)) {
    					//Do not expect to execute assuming no duplicate customer
    					listPT = allPatients.get(key);
    					
    				} else {        				
    					pt.setMedication(pr.getMedication());
    					pt.setLabel("Day-" + String.valueOf(pr.getDay()));
    					LocalDateTime prDay = initialDate.plusDays(pr.getDay());
    					pt.setEndYear(prDay.getYear());
    					pt.setEndMonth(prDay.getMonthValue());
    					pt.setEndDay(prDay.getDayOfMonth());
					}
					
    				listPT.add(pt);
    				pt = new PatientTimeline();        				
    			}    			
				allPatients.put(key, listPT);
				listPT = new ArrayList<PatientTimeline>();
    		}    		
    	}
		return allPatients;
	}

	public enum PatientType {
        VIOLATED("Patients that violated by taking B and I together."), //
        VALID_NO_COMED("Patients that did not violate, because they never took B and I together."), //
        VALID_BI_SWITCH("Patients that did not violate, because they switched from B to I."), //
        VALID_IB_SWITCH("Patients that did not violate, because they switched from I to B."), //
        VALID_I_TRIAL("Patients that did not violate, because they simply trialled I during B."), //
        VALID_B_TRIAL("Patients that did not violate, because they simply trialled B during I.");

        private String name;

        PatientType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
		
}