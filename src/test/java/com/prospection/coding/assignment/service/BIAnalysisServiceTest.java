package com.prospection.coding.assignment.service;

import com.prospection.coding.assignment.data.PurchaseRecordDAO;
import com.prospection.coding.assignment.domain.AnalysisResult;
import com.prospection.coding.assignment.domain.AnalysisResult.PatientType;
import com.prospection.coding.assignment.domain.PurchaseRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BIAnalysisServiceTest {

    @Mock
    private PurchaseRecordDAO purchaseRecordDAO;

    private BIAnalysisService biAnalysisService;

    @Before
    public void setUp() {
        biAnalysisService = new BIAnalysisService(purchaseRecordDAO);
    }

    @Test
    public void didNotViolateB() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(3, "B", 1),
                new PurchaseRecord(4, "B", 1),
                new PurchaseRecord(6, "B", 1),
                new PurchaseRecord(7, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they switched from B to I.", result.getTotal(PatientType.VALID_NO_COMED), is(1));

    }
    
    @Test
    public void didNotViolateI() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "I", 101),
                new PurchaseRecord(3, "I", 101),
                new PurchaseRecord(4, "I", 101),
                new PurchaseRecord(6, "I", 101),
                new PurchaseRecord(7, "I", 101)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they switched from B to I.", result.getTotal(PatientType.VALID_NO_COMED), is(1));

    }

    @Test
    public void voilatedBtoI() throws Exception {

        setupPatients(
                new PurchaseRecord(192, "B", 3),
                new PurchaseRecord(271, "B", 3),
                new PurchaseRecord(427, "B", 3),
                new PurchaseRecord(436, "I", 3),
                new PurchaseRecord(465, "B", 3),
                new PurchaseRecord(845, "B", 3),
                new PurchaseRecord(995, "B", 3)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(PatientType.VIOLATED), is(1));

    }
    
    @Test
    public void voilatedItoB() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "I", 101),
                new PurchaseRecord(20, "I", 101),
                new PurchaseRecord(201, "I", 101),
                new PurchaseRecord(203, "B", 101),
                new PurchaseRecord(204, "B", 101),
                new PurchaseRecord(205, "B", 101)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that violated by taking B and I together.", result.getTotal(PatientType.VIOLATED), is(1));

    }

    @Test
    public void switchedFromBtoI() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "B", 100),
                new PurchaseRecord(2, "B", 100),
                new PurchaseRecord(300, "I", 100),
                new PurchaseRecord(301, "I", 100),
                new PurchaseRecord(302, "I", 100),
                new PurchaseRecord(303, "I", 100),
                new PurchaseRecord(304, "I", 100)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they switched from B to I.", result.getTotal(PatientType.VALID_BI_SWITCH), is(1));

    }
    
    @Test
    public void switchedFromItoB() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "I", 1),
                new PurchaseRecord(200, "I", 1),
                new PurchaseRecord(301, "B", 1),
                new PurchaseRecord(303, "B", 1),
                new PurchaseRecord(305, "B", 1),
                new PurchaseRecord(307, "B", 1),
                new PurchaseRecord(308, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they switched from I to B.", result.getTotal(PatientType.VALID_IB_SWITCH), is(1));

    }
    
    @Test
    public void trailedFromItoB() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(97, "B", 1),
                new PurchaseRecord(129, "B", 1),
                new PurchaseRecord(146, "B", 1),
                new PurchaseRecord(290, "B", 1),
                new PurchaseRecord(573, "I", 1),
                new PurchaseRecord(715, "B", 1),
                new PurchaseRecord(804, "B", 1),
                new PurchaseRecord(991, "B", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they simply trialled I during B.", result.getTotal(PatientType.VALID_I_TRIAL), is(1));

    }
    
    @Test
    public void trailedFromBtoI() throws Exception {

        setupPatients(
                new PurchaseRecord(90, "I", 1),
                new PurchaseRecord(242, "B", 1),
                new PurchaseRecord(279, "B", 1),
                new PurchaseRecord(290, "B", 1),
                new PurchaseRecord(321, "B", 1),
                new PurchaseRecord(335, "B", 1),
                new PurchaseRecord(510, "B", 1),
                new PurchaseRecord(648, "B", 1),
                new PurchaseRecord(788, "B", 1),
                new PurchaseRecord(848, "I", 1),
                new PurchaseRecord(995, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Patients that did not violate, because they simply trialled B during I.", result.getTotal(PatientType.VALID_B_TRIAL), is(1));

    }
    
    private void setupPatients(PurchaseRecord... values) throws Exception {
        when(purchaseRecordDAO.allPurchaseRecords())
                .thenReturn(Arrays.asList(values));
    }
}