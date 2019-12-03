package com.prospection.coding.assignment.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.prospection.coding.assignment.domain.AnalysisResult.PatientType;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisResultTest {
	@Mock
	private Map<PatientType, LinkedHashMap<String, List<PurchaseRecord>>> statistics;
	@InjectMocks
	private AnalysisResult analysisResult;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTestPatienttl() throws Exception {
		Map<String, List<PatientTimeline>> localMap = new HashMap<>();
		Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> statistics = 
				new LinkedHashMap<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>>();
		
		LinkedHashMap<String, List<PurchaseRecord>> mapPr = new LinkedHashMap<String, List<PurchaseRecord>>();
		List<PurchaseRecord> lstPr = new ArrayList<>();
		PurchaseRecord pr = new PurchaseRecord(101, "I", 1);
		lstPr.add(pr);
		
		mapPr.put("100", lstPr);
		
		statistics.put(AnalysisResult.PatientType.VIOLATED, mapPr);
		
		analysisResult.setStatistics(statistics);
		
		localMap = analysisResult.getTestPatienttl();
		
		List<PatientTimeline> lstPt = localMap.get("100");
		assertEquals(lstPt.get(0).getMedication(), pr.getMedication());
	}

	@Test
	public void testGetPatientDetails() throws Exception {
		Map<String, String> localMap = new HashMap<>();
		Map<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>> statistics = 
				new LinkedHashMap<AnalysisResult.PatientType, LinkedHashMap<String, List<PurchaseRecord>>>();
		
		LinkedHashMap<String, List<PurchaseRecord>> mapPr = new LinkedHashMap<String, List<PurchaseRecord>>();
		List<PurchaseRecord> lstPr = new ArrayList<>();
		PurchaseRecord pr = new PurchaseRecord(101, "I", 1);
		lstPr.add(pr);
		
		mapPr.put("100", lstPr);
		
		statistics.put(AnalysisResult.PatientType.VIOLATED, mapPr);
		
		analysisResult.setStatistics(statistics);
		
		localMap = analysisResult.getPatientDetails(AnalysisResult.PatientType.VIOLATED);
		
		String details = localMap.get("100");
		
		assertEquals(details, String.valueOf("I("+pr.getDay()+")"));

	}
}
