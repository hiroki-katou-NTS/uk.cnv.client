package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

@RunWith(JMockit.class)
public class ManHourSummaryDataTest {
    /**
     * Getter
     */
    @Test
    public void getters() {
        val masterNameInfo = new MasterNameInformation(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        val workDetails = Helper.workDetailDataList(8);
        val manHourSummany = new ManHourSummaryData(masterNameInfo, workDetails);

        // Assertion
        NtsAssert.invokeGetters(manHourSummany);
    }
}
