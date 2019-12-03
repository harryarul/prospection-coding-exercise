package com.prospection.coding.assignment.service;

import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_BI_SWITCH;
import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_B_TRIAL;
import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_IB_SWITCH;
import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_I_TRIAL;
import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_NO_COMED;
import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VIOLATED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.prospection.coding.assignment.data.PurchaseRecordDAO;
import com.prospection.coding.assignment.domain.AnalysisResult;
import com.prospection.coding.assignment.domain.PurchaseRecord;

@Component
public class BIAnalysisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BIAnalysisService.class);

    private final PurchaseRecordDAO purchaseRecordDAO;

    @Autowired
    public BIAnalysisService(PurchaseRecordDAO purchaseRecordDAO) {
        this.purchaseRecordDAO = purchaseRecordDAO;
    }
    
    /**
     * performBIAnalysis
     * 
     * @return
     * @throws Exception
     */
    public AnalysisResult performBIAnalysis() throws Exception {
        List<PurchaseRecord> purchaseRecords = purchaseRecordDAO.allPurchaseRecords();
        //purchaseRecords.forEach((s) -> LOGGER.info(s.toString()));
        
        //do some processing here
        Map<String, String> mapViolationDetails = new LinkedHashMap<String, String>();
        Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> stats = new LinkedHashMap<>(); 
        
        purchaseRecords.sort((PurchaseRecord p1, PurchaseRecord p2) -> p1.getPatientId() - p2.getPatientId());
                  
        LOGGER.info("Start");
        //purchaseRecords.forEach((s) -> LOGGER.info(s.toString()));
        
        List<PurchaseRecord> purRecords = new ArrayList<>();
        int id = purchaseRecords.get(0).getPatientId();
        int counter = 0;
        for(PurchaseRecord pr: purchaseRecords) {
        	
        	//LOGGER.info("Patient "+id);        	
        	if(id < pr.getPatientId()) {
        		
        		LOGGER.info(id + " - " + pr.getPatientId());
        		purRecords.sort((PurchaseRecord p1, PurchaseRecord p2) -> p1.getDay() - p2.getDay());
        		//LOGGER.info("Sorted : "+purRecords);
        		//tempRecords.forEach((s) -> LOGGER.info(s.getMedication()));
        		processStatistics(stats, purRecords, mapViolationDetails);
        		
            	id = pr.getPatientId();
            	purRecords = new ArrayList<>();
        	}
        	
        	purRecords.add(pr);
        	counter++;
        	
        	//last patient record
        	if(counter == purchaseRecords.size()) {
        		LOGGER.info("Process last ");
        		purRecords.sort((PurchaseRecord p1, PurchaseRecord p2) -> p1.getDay() - p2.getDay());
        		//LOGGER.info("Sorted : "+purRecords);
        		processStatistics(stats, purRecords, mapViolationDetails);
        	}        	
        }        
        
		LOGGER.info("stats : "+stats);
		int validBI = 0;
		int validNoComed = 0;
		int violated = 0;
		int validIB = 0;
		int validBTrial = 0;
		int validITrial = 0; 
		
		if(stats.containsKey(AnalysisResult.PatientType.VALID_NO_COMED)) {
			validNoComed = stats.get(AnalysisResult.PatientType.VALID_NO_COMED).size();
			LOGGER.info("validNoComed : "+validNoComed);
		}
		
		if(stats.containsKey(AnalysisResult.PatientType.VIOLATED)) {
			violated = stats.get(AnalysisResult.PatientType.VIOLATED).size();
			LOGGER.info("violated : "+violated);
		}
		if(stats.containsKey(AnalysisResult.PatientType.VALID_BI_SWITCH)) {
			validBI = stats.get(AnalysisResult.PatientType.VALID_BI_SWITCH).size();
			LOGGER.info("validBI : "+validBI);
		}
		   
		if(stats.containsKey(AnalysisResult.PatientType.VALID_IB_SWITCH)) {
			validIB = stats.get(AnalysisResult.PatientType.VALID_IB_SWITCH).size();
			LOGGER.info("validIB : "+validIB);   
		}
		
		if(stats.containsKey(AnalysisResult.PatientType.VALID_I_TRIAL)) {
			validITrial = stats.get(AnalysisResult.PatientType.VALID_I_TRIAL).size();
			LOGGER.info("valid I Trial : "+validITrial);
		}
		
		if(stats.containsKey(AnalysisResult.PatientType.VALID_B_TRIAL)) {
			validBTrial = stats.get(AnalysisResult.PatientType.VALID_B_TRIAL).size();
			LOGGER.info("valid B Trial : "+validBTrial);
		}
		
        // then put real results in here
        AnalysisResult result = new AnalysisResult();
        result.putTotal(VIOLATED, violated);
        result.putTotal(VALID_NO_COMED, validNoComed);
        result.putTotal(VALID_BI_SWITCH, validBI);
        result.putTotal(VALID_IB_SWITCH, validIB);
        result.putTotal(VALID_I_TRIAL, validITrial);
        result.putTotal(VALID_B_TRIAL, validBTrial);
        
        result.setStatistics(stats);

        result.setMapViolationDetails(mapViolationDetails);
		//LOGGER.info("mapViolationDetails : "+mapViolationDetails);
        return result;
    }
    
    /**
     * Process patient statistics
     * 
     * @param stats
     * @param custRecords
     * @param mapViolationDetails
     */
    private void processStatistics(Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> stats, 
    		List<PurchaseRecord> custRecords, Map<String, String> mapViolationDetails) {
    	
    	LinkedHashMap<String, List<PurchaseRecord>> data = new LinkedHashMap<String, List<PurchaseRecord>>();
    	Map<String, Boolean> singleCustomer = new HashMap<String, Boolean>();
    	String violationDetails = "";
    	String key = String.valueOf(custRecords.get(0).getPatientId());
    	data.put(key, custRecords);
    	
        List<String> lstStr = custRecords.stream()
    			.map((p -> p.getDay() + "-" + p.getMedication()))
    			.collect(Collectors.toList());
        LOGGER.info("LstStr : "+lstStr);
        
    	//Identify this customer's category
        //VIOLATED("Patients that violated by taking B and I together.")
        violationDetails = violated(custRecords, lstStr, singleCustomer);
		LOGGER.info(key + " singleCustomer-violated " + singleCustomer);
        
		if(violationDetails.length() == 0) {
			//VALID_BI_SWITCH("Patients that did not violate, because they switched from B to I.")        
			//VALID_IB_SWITCH("Patients that did not violate, because they switched from I to B.")
			//VALID_I_TRIAL("Patients that did not violate, because they simply trialled I during B.")
			//VALID_B_TRIAL("Patients that did not violate, because they simply trialled B during I.")
			violationDetails = validSwitchTrial(custRecords, lstStr, singleCustomer);
			LOGGER.info(key + " singleCustomer-validSwitch-trial " + singleCustomer);
		}

		if(violationDetails.length() == 0) {

			//VALID_NO_COMED("Patients that did not violate, because they never took B and I together.")
			validNoComed(custRecords, lstStr, singleCustomer);
			LOGGER.info(key + " singleCustomer-validNoComed " + singleCustomer);
		}
		LOGGER.info(key + " violationDetails " + violationDetails);

		//According to preference add statistics, to decide here if customer is in more than 1 category
		if(singleCustomer.containsKey("VIOLATED")) {
			addToStats(stats, AnalysisResult.PatientType.VIOLATED, custRecords, data);
			mapViolationDetails.put(key, violationDetails);
			LOGGER.info(key  + " added - VIOLATED");
		}		
		else if(singleCustomer.containsKey("VALID_NO_COMED")) {
			addToStats(stats, AnalysisResult.PatientType.VALID_NO_COMED, custRecords, data);
			LOGGER.info(key + " added - VALID_NO_COMED");
		}
		else if(singleCustomer.containsKey("VALID_BI_SWITCH")) {
			addToStats(stats, AnalysisResult.PatientType.VALID_BI_SWITCH, custRecords, data);
			mapViolationDetails.put(key, violationDetails);
			LOGGER.info(key + " added - VALID_BI_SWITCH");
		}
		else if(singleCustomer.containsKey("VALID_IB_SWITCH")) {
			addToStats(stats, AnalysisResult.PatientType.VALID_IB_SWITCH, custRecords, data);
			mapViolationDetails.put(key, violationDetails);
			LOGGER.info(key + " added - VALID_IB_SWITCH");
		}else if(singleCustomer.containsKey("VALID_I_TRIAL")) {
			addToStats(stats, AnalysisResult.PatientType.VALID_I_TRIAL, custRecords, data);
			mapViolationDetails.put(key, violationDetails);
			LOGGER.info(key + " added - VALID_I_TRIAL");
		}else if(singleCustomer.containsKey("VALID_B_TRIAL")) {
			addToStats(stats, AnalysisResult.PatientType.VALID_B_TRIAL, custRecords, data);
			mapViolationDetails.put(key, violationDetails);
			LOGGER.info(key + " added - VALID_B_TRIAL");
		}    	
    }
    
    /**
     * Build the statistics collection
     * 
     * @param stats
     * @param idKey
     * @param custRecords
     * @param data
     */
    private void addToStats(Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> stats, 
    		AnalysisResult.PatientType idKey, List<PurchaseRecord> custRecords, LinkedHashMap<String, List<PurchaseRecord>> data) {
    	String key = String.valueOf(custRecords.get(0).getPatientId());
		if(stats.containsKey(idKey)) {
			LinkedHashMap<String, List<PurchaseRecord>> temp = stats.get(idKey);
			temp.put(key, custRecords);
		} else {
			stats.put(idKey, data);
		}
		
    }

    /**
     * 
     * 
     * @param custRecords
     * @param lstStr
     * @param singleCustomer
     * @return
     */
    private String violated(List<PurchaseRecord> custRecords, List<String> lstStr, Map<String, Boolean> singleCustomer) {
		String violationDetails = "";
    	StringBuilder sb = new StringBuilder();
		int preDay = custRecords.get(0).getDay();
		String preDrug = custRecords.get(0).getMedication();
    	boolean vbi = false;
    	boolean vib = false;
    	for(PurchaseRecord pr : custRecords) {
    		//For B purchase 30days
    		//Violated from B -> I
    		if((pr.getDay() - preDay) < 30 
    				&& "B".equalsIgnoreCase(preDrug)
    				&& !preDrug.equalsIgnoreCase(pr.getMedication())) {
    			vbi = true;
    			sb.append(preDrug).append("(").append(preDay).append(")->").append(pr.getMedication())
    					.append("(").append(pr.getDay()).append(")");
    			violationDetails = sb.toString();
    			sb.setLength(0);
    			LOGGER.info("(B->I 30d) PreDay " + preDay + " PreDrug " + preDrug + " CDay " + pr.getDay() + " CDrug " + pr.getMedication());
    			break;
    		}	
    			
    			
    		//For I purchase 90days
    		//Violated from I -> B
    		if((pr.getDay() - preDay) < 90 
    				&& "I".equalsIgnoreCase(preDrug)
    				&& !preDrug.equalsIgnoreCase(pr.getMedication())) {
    			vib = true;
    			sb.append(preDrug).append("(").append(preDay).append(")->").append(pr.getMedication())
    					.append("(").append(pr.getDay()).append(")");
    			violationDetails = sb.toString();
    			sb.setLength(0);
    			LOGGER.info("(I->B 90d) PreDay " + preDay + " PreDrug " + preDrug + " CDay " + pr.getDay() + " CDrug " + pr.getMedication());
    			break;
    		}    			

    		preDay = pr.getDay();
    		preDrug = pr.getMedication();

    	}
		LOGGER.info("violationDetails > "+violationDetails);

    	if(vbi || vib) {
    		singleCustomer.put("VIOLATED", Boolean.TRUE);
    	}
    	return violationDetails;
    }
    
    private void validNoComed(List<PurchaseRecord> custRecords, List<String> lstStr, Map<String, Boolean> singleCustomer) {
    	
    	String med = custRecords.get(0).getMedication();
    	//String key = String.valueOf(custRecords.get(0).getPatientId());

    	int ctr = 0;
        for(String s: lstStr) {
        	if(!s.endsWith(med)) {
        		ctr++;
        		break;
        	}
        }
        if(ctr == 0) {
        	singleCustomer.put("VALID_NO_COMED", Boolean.TRUE);
        }
    	
    }
    
    
    private String validSwitchTrial(List<PurchaseRecord> custRecords, List<String> lstStr, Map<String, Boolean> singleCustomer) {
		String violationDetails = "";
		int preDay = custRecords.get(0).getDay();
		StringBuilder sb = new StringBuilder();
		String preDrug = custRecords.get(0).getMedication();
    	boolean validbi = false;
    	boolean validib = false;
    	int ctrBtoI = 0;
    	int ctrItoB = 0;
    	String first = null;
    	for(PurchaseRecord pr : custRecords) {
    		//For B purchase 30days
    		//valid from B -> I
    		if((pr.getDay() - preDay) > 30 
    				&& "B".equalsIgnoreCase(preDrug)
    				&& !preDrug.equalsIgnoreCase(pr.getMedication())) {
    			validbi = true;
    			ctrBtoI++;
    			if(first == null) {
    				first = "BTOI";
    			}
    			sb.append(preDrug).append("(").append(preDay).append(")->").append(pr.getMedication())
    					.append("(").append(pr.getDay()).append(")");
    			violationDetails = sb.toString();
    			sb.setLength(0);
    			LOGGER.info("(B->I greater then 30d) PreDay " + preDay + " PreDrug " + preDrug + " CDay " + pr.getDay() + " CDrug " + pr.getMedication());
    		}   		
    			
    		//For I purchase 90days
    		//Valid from I -> B
    		if((pr.getDay() - preDay) > 90 
    				&& "I".equalsIgnoreCase(preDrug)
    				&& !preDrug.equalsIgnoreCase(pr.getMedication())) {
    			validib = true;
    			ctrItoB++;
    			if(first == null) {
    				first = "ITOB";
    			}
    			sb.append(preDrug).append("(").append(preDay).append(")->").append(pr.getMedication())
    					.append("(").append(pr.getDay()).append(")");
    			violationDetails = sb.toString();
    			sb.setLength(0);
    			LOGGER.info("(I->B greater than 90d) PreDay " + preDay + " PreDrug " + preDrug + " CDay " + pr.getDay() + " CDrug " + pr.getMedication());
    		}    			

    		preDay = pr.getDay();
    		preDrug = pr.getMedication();
    		LOGGER.info("violationDetails > "+violationDetails);

    	}
    	
    	if(validbi && !validib) {
    		singleCustomer.put("VALID_BI_SWITCH", Boolean.TRUE);
    	}    	
    	if(validib && !validbi) {
    		singleCustomer.put("VALID_IB_SWITCH", Boolean.TRUE);
    	}    	
    	if(validib && validbi
    			&& (ctrBtoI == ctrItoB)
    			&& first != null
    			&& first.equalsIgnoreCase("BTOI")) {
    		singleCustomer.put("VALID_I_TRIAL", Boolean.TRUE);
    	}
    	if(validib && validbi
    			&& (ctrBtoI == ctrItoB)
    			&& first != null
    			&& first.equalsIgnoreCase("ITOB")) {
    		singleCustomer.put("VALID_B_TRIAL", Boolean.TRUE);
    	}
    	
    	return violationDetails;
    }
}
