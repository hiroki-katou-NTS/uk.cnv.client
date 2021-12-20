package nts.uk.ctx.at.function.dom.adapter;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;

public interface EmployeeHistWorkRecordAdapter {
	List<AffCompanyHistImport> getWplByListSidAndPeriod(List<String> sids,  DatePeriod datePeriod);
	List<AffCompanyHistImport> getWplByListSid(List<String> sids);
}
