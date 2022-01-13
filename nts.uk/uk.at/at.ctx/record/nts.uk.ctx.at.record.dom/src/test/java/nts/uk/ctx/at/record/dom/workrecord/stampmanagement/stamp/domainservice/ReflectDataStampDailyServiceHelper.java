package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.ArrayList;
import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;

/**
 * 
 * @author chungnt
 *
 */

public class ReflectDataStampDailyServiceHelper {

	String employeeId = "employeeId";
	String cid = "cid";
	GeneralDate date = GeneralDate.today();
	List<ErrorMessageInfo> errorMessageInfos = new ArrayList<>();
	
	public OutputCreateDailyOneDay getErrorsNull(Stamp stamp) {
		
		return new OutputCreateDailyOneDay(new ArrayList<>(),
				new IntegrationOfDaily(employeeId,
						stamp.getStampDateTime().toDate(),
						new WorkInfoOfDailyAttendance(),
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null),
				new ArrayList<>());
		
	}
	
	public OutputCreateDailyOneDay getErrorsNotNull(Stamp stamp) {
		
		ErrorMessageInfo error = new ErrorMessageInfo(cid,
				employeeId,
				date,
				ExecutionContent.DAILY_CALCULATION,
				new ErrMessageResource(cid),
				new ErrMessageContent("This is Message"));
		
		errorMessageInfos.add(error);
		
		return new OutputCreateDailyOneDay(errorMessageInfos,
				new IntegrationOfDaily(employeeId,
						stamp.getStampDateTime().toDate(),
						new WorkInfoOfDailyAttendance(),
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null),
				new ArrayList<>());
		
	}
}
