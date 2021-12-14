package nts.uk.ctx.at.function.ac.processexecution;

//import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.adapter.AffComHistItemImport;
import nts.uk.ctx.at.function.dom.adapter.AffCompanyHistImport;
import nts.uk.ctx.at.function.dom.adapter.EmployeeHistWorkRecordAdapter;
import nts.uk.ctx.bs.employee.pub.company.AffCompanyHistExport;
import nts.uk.ctx.bs.employee.pub.company.SyCompanyPub;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class EmployeeHistWorkRecordAcFinder implements EmployeeHistWorkRecordAdapter {

	@Inject
	private SyCompanyPub syCompanyPub;

	@Override
	public List<AffCompanyHistImport> getWplByListSidAndPeriod(List<String> sids, DatePeriod datePeriod) {
		List<AffCompanyHistImport> importList =
				this.syCompanyPub.GetAffCompanyHistByEmployee(sids, datePeriod)
					.stream()
						.map(x->convertToImport(x))
							.collect(Collectors.toList());
		return importList;
	}
	
	private AffCompanyHistImport convertToImport(AffCompanyHistExport export) {
		List<AffComHistItemImport> subListImport = export.getLstAffComHistItem()
					.stream()
						.map(x-> new AffComHistItemImport(x.getHistoryId(), x.isDestinationData(), x.getDatePeriod()))
							.collect(Collectors.toList());
		return new AffCompanyHistImport(export.getEmployeeId(), subListImport);
	}
}
