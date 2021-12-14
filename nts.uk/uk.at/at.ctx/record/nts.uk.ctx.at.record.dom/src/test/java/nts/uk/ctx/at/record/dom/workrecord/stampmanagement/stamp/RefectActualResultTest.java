package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
/**
 * 
 * @author tutk
 *
 */
public class RefectActualResultTest {

	@Test
	public void getters() {
		RefectActualResult data = StampHelper.getRefectActualResultDefault();
		NtsAssert.invokeGetters(data);
	}

	@Test
	public void testRefectActualResult() {
		Integer cardNumberSupport = 9999;//dummy
		WorkLocationCD workLocationCD = new WorkLocationCD("workLocationCD");//dummy
		WorkTimeCode workTimeCode = new WorkTimeCode("workTimeCode");//dummy
		OvertimeDeclaration overtimeDeclaration = new OvertimeDeclaration(new AttendanceTime(1),//dummy
				new AttendanceTime(10));//dummy
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(Optional.empty(), Optional.empty(),
				Optional.of(workLocationCD), 
				Optional.of(new SupportCardNumber(cardNumberSupport)));
		WorkGroup work = WorkGroup.create("workCd1", "workCd2", "workCd3", "workCd4", "workCd5");
		RefectActualResult data = new RefectActualResult(workInformationStamp, workTimeCode, overtimeDeclaration);
		NtsAssert.invokeGetters(data);
	}
}
