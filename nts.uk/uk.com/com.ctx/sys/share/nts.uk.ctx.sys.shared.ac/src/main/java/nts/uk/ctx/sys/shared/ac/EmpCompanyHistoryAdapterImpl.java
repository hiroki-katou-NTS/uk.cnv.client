package nts.uk.ctx.sys.shared.ac;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.companyhistory.EmpComHisPub;
import nts.uk.ctx.sys.shared.dom.employee.EmpCompanyHistoryAdapter;
import nts.uk.ctx.sys.shared.dom.employee.EmpEnrollPeriodImport;
import nts.uk.ctx.sys.shared.dom.employee.SecondSituation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EmpCompanyHistoryAdapterImpl implements EmpCompanyHistoryAdapter {

    @Inject
    private EmpComHisPub empComHisPub;
    @Override
    public List<EmpEnrollPeriodImport> getEnrollmentPeriod(List<String> lstEmpId, DatePeriod datePeriod) {
       return empComHisPub.getEnrollmentPeriod(lstEmpId,datePeriod).stream().map(e->new EmpEnrollPeriodImport(
               e.getEmpId(),
               e.getDatePeriod(),
               EnumAdaptor.valueOf(e.getSecondedSituation().value,SecondSituation.class)
       )).collect(Collectors.toList());

    }
}
