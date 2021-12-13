package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

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
		WorkGroup group = new WorkGroup(new WorkCode("DUMMY"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()); //dummy
		OvertimeDeclaration overtimeDeclaration = new OvertimeDeclaration(new AttendanceTime(1),//dummy
				new AttendanceTime(10));//dummy
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(Optional.empty(), Optional.empty(),
				Optional.of(workLocationCD), 
				Optional.of(new SupportCardNumber(cardNumberSupport)));
	
		RefectActualResult data = new RefectActualResult(workInformationStamp, workTimeCode, overtimeDeclaration, group);

		NtsAssert.invokeGetters(data);
	}
}
