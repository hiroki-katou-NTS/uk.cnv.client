package nts.uk.ctx.at.record.pubimp.workrecord.erroralarm;

import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.pub.workrecord.erroralarm.EmployeeDailyPerErrorPub;
import nts.uk.ctx.at.record.pub.workrecord.erroralarm.EmployeeDailyPerErrorPubExport;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class EmployeeDailyPerErrorPubImpl implements EmployeeDailyPerErrorPub {
	@Inject
	private EmployeeDailyPerErrorRepository repo;

	@Override
	public List<EmployeeDailyPerErrorPubExport> getByErrorCode(String employeeId, DatePeriod datePeriod,
			List<String> errorCodes) {
		String companyID = AppContexts.programId();
		List<EmployeeDailyPerError> employeeDailyList = repo.findByPeriodOrderByYmd(employeeId, datePeriod).stream()
				.filter(e -> errorCodes.contains(e.getErrorAlarmWorkRecordCode().v())
						&& e.getCompanyID().equals(companyID)).collect(Collectors.toList());
		
		return employeeDailyList.stream().map(e ->new EmployeeDailyPerErrorPubExport(e.getCompanyID(), e.getEmployeeID(), e.getDate(),
				e.getErrorAlarmWorkRecordCode().v(), e.getAttendanceItemList(), e.getErrorCancelAble())).collect(Collectors.toList());
	}

}
