package nts.uk.ctx.at.function.ac.processexecution;

//import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.AffComHistItemImport;
import nts.uk.ctx.at.function.dom.adapter.AffCompanyHistImport;
import nts.uk.ctx.at.function.dom.adapter.EmployeeHistWorkRecordAdapter;
import nts.uk.ctx.bs.employee.pub.company.AffCompanyHistExport;
import nts.uk.ctx.bs.employee.pub.company.SyCompanyPub;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.context.AppContexts;

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

	@Override
	public List<AffCompanyHistImport> getWplByListSid(List<String> sids) {
		val cid = AppContexts.user().companyId();
		List<AffCompanyHistImport> importList =
				this.syCompanyPub.getAffComHisBySids(cid, sids)
						.stream()
						.map(this::convertToImport)
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
