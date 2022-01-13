package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class WorkDetailDataTest {

    private final WorkDetailData workDetail = Helper.workDetailData;

    /**
     * Getter
     */
    @Test
    public void getters() {
        // Assertion
        NtsAssert.invokeGetters(workDetail);
    }

    /**
     * SummaryItemType = AFFILIATION_WORKPLACE
     */
    @Test
    public void mapSummaryItem_affWkp() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.AFFILIATION_WORKPLACE);
        // Assertion
        assertThat(result).isEqualTo("AFF_WKP_001");
    }

    /**
     * SummaryItemType = WORKPLACE
     */
    @Test
    public void mapSummaryItem_Wkp() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.WORKPLACE);
        // Assertion
        assertThat(result).isEqualTo("WKP_001");
    }

    /**
     * SummaryItemType = EMPLOYEE
     */
    @Test
    public void mapSummaryItem_emp() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.EMPLOYEE);
        // Assertion
        assertThat(result).isEqualTo("SID_001");
    }

    /**
     * SummaryItemType = TASK1
     */
    @Test
    public void mapSummaryItem_task1() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.TASK1);
        // Assertion
        assertThat(result).isEqualTo("TASK1_001");
    }

    /**
     * SummaryItemType = TASK2
     */
    @Test
    public void mapSummaryItem_task2() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.TASK2);
        // Assertion
        assertThat(result).isEqualTo("TASK2_001");
    }

    /**
     * SummaryItemType = TASK3
     */
    @Test
    public void mapSummaryItem_task3() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.TASK3);
        // Assertion
        assertThat(result).isEqualTo("TASK3_001");
    }

    /**
     * SummaryItemType = TASK4
     */
    @Test
    public void mapSummaryItem_task4() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.TASK4);
        // Assertion
        assertThat(result).isEqualTo("TASK4_001");
    }

    /**
     * SummaryItemType = TASK5
     */
    @Test
    public void mapSummaryItem_task5() {
        // Execute
        val result = workDetail.mapSummaryItem(SummaryItemType.TASK5);
        // Assertion
        assertThat(result).isEqualTo("TASK5_001");
    }
}
