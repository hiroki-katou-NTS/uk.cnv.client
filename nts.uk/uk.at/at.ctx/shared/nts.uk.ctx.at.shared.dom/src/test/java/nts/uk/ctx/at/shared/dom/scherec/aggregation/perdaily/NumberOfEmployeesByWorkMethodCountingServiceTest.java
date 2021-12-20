package nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import mockit.Injectable;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendanceHelper;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

public class NumberOfEmployeesByWorkMethodCountingServiceTest {
	
	@Injectable
	public NumberOfEmployeesByWorkMethodCountingService.Require require;
	
	@Test
	public void testCount() {
		
		WorkInformation workInfo1 = new WorkInformation(new WorkTypeCode("wtp-code1"), new WorkTimeCode("001") );
		WorkInformation workInfo2 = new WorkInformation(new WorkTypeCode("wtp-code2"), new WorkTimeCode("002") );
		WorkInformation workInfo3 = new WorkInformation(new WorkTypeCode("wtp-code3"), new WorkTimeCode("002") );
		WorkInformation workInfo4 = new WorkInformation(new WorkTypeCode("wtp-code4"), new WorkTimeCode("002") );
		WorkInformation workInfo5 = new WorkInformation(new WorkTypeCode("wtp-code5"), null );
		
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance1 = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo1);
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance2 = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo2);
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance3 = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo3);
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance4 = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo4);
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance5 = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo5);
		List<WorkInfoOfDailyAttendance> workInfoList = Arrays.asList(
				workInfoOfDailyAttendance1,
				workInfoOfDailyAttendance2,
				workInfoOfDailyAttendance3,
				workInfoOfDailyAttendance4,
				workInfoOfDailyAttendance5
				);
		
		Map<String, BigDecimal> mockResult = new HashMap<>();
		mockResult.put("001", BigDecimal.valueOf(1));
		mockResult.put("002", BigDecimal.valueOf(3));
		
		Map<String, BigDecimal> result = 
				NumberOfEmployeesByWorkMethodCountingService.count(
						require, 
						AggregationUnitOfWorkMethod.WORK_TIME, 
						workInfoList);
		
		assertThat( result.entrySet() )
			.extracting(
					entry -> entry.getKey(),
					entry -> entry.getValue() )
			.containsOnly( 
					tuple("001", BigDecimal.valueOf(1)),
					tuple("002", BigDecimal.valueOf(3)) );
		
	}

}
