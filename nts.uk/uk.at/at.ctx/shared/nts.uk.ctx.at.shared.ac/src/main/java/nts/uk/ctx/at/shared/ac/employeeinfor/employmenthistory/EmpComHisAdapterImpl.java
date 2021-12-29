package nts.uk.ctx.at.shared.ac.employeeinfor.employmenthistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;


import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.SecondSituation;
import nts.uk.ctx.bs.employee.pub.companyhistory.EmpComHisPub;
import nts.uk.ctx.bs.employee.pub.companyhistory.EmpEnrollPeriodExported;

/**
 * 社員の所属会社履歴Adapter Impl
 * @author HieuLt
 */
@Stateless
public class EmpComHisAdapterImpl implements EmpComHisAdapter {

	@Inject
	private EmpComHisPub pub;

	@Override
	public List<EmpEnrollPeriodImport> getEnrollmentPeriod(List<String> lstEmpId, DatePeriod datePeriod) {
		// 基幹コンテキストから社員の在籍期間を取得する
		List<EmpEnrollPeriodExported> data = pub.getEnrollmentPeriod(lstEmpId, datePeriod);
		List<EmpEnrollPeriodImport> result = data.stream()
				.map(c -> new EmpEnrollPeriodImport(c.getEmpId(), c.getDatePeriod(), SecondSituation.valueOf(c.getSecondedSituation().value)))
				.collect(Collectors.toList());
		// return 社員の所属会社履歴Publish.取得する( 基準日, 社員IDリスト )
		return result;
	}

	@Override
	public Optional<EmpEnrollPeriodImport> getLatestEnrollmentPeriod(String lstEmpId, DatePeriod datePeriod) {
		
		List<EmpEnrollPeriodImport> empEnrollPeriodImport = getEnrollmentPeriod(Arrays.asList(lstEmpId), datePeriod);
		
		return empEnrollPeriodImport.stream()
				.sorted((a,b)->b.getDatePeriod().start().compareTo(a.getDatePeriod().start()))
				.findFirst();
	}
	
	

}
