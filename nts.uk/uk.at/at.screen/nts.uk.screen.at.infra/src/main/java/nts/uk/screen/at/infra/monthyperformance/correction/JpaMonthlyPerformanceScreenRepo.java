package nts.uk.screen.at.infra.monthyperformance.correction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.infra.entity.employee.mngdata.BsymtEmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.infra.entity.workplace.BsymtWorkplaceInfo;
import nts.uk.ctx.bs.employee.infra.entity.workplace.affiliate.BsymtAffiWorkplaceHistItem;
import nts.uk.ctx.bs.person.infra.entity.person.info.BpsmtPerson;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.monthlyperformance.correction.MonthlyPerformanceScreenRepo;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceEmployeeDto;
@Stateless
public class JpaMonthlyPerformanceScreenRepo extends JpaRepository implements MonthlyPerformanceScreenRepo {

	private final static String SEL_EMPLOYEE;
	private final static String SEL_PERSON = "SELECT p FROM BpsmtPerson p WHERE p.bpsmtPersonPk.pId IN :lstPersonId";
	private final static String SEL_WORKPLACE;
	
	static{
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT DISTINCT s FROM BsymtEmployeeDataMngInfo s ");
		// builderString.append("JOIN KmnmtAffiliClassificationHist c ");
		// builderString.append("JOIN KmnmtAffiliEmploymentHist e ");
		// builderString.append("JOIN KmnmtAffiliJobTitleHist j ");
		builderString.append("JOIN BsymtAffiWorkplaceHistItem w ");
		builderString.append("WHERE w.workPlaceId IN :lstWkp ");
		// builderString.append("AND e.kmnmtEmploymentHistPK.emptcd IN :lstEmp
		// ");
		// builderString.append("AND j.kmnmtJobTitleHistPK.jobId IN :lstJob ");
		// builderString.append("AND c.kmnmtClassificationHistPK.clscd IN
		// :lstClas ");
		builderString.append("AND s.bsymtEmployeeDataMngInfoPk.sId = w.sid ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// e.kmnmtEmploymentHistPK.empId ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// j.kmnmtJobTitleHistPK.empId ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// c.kmnmtClassificationHistPK.empId ");
		SEL_EMPLOYEE = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT w FROM BsymtAffiWorkplaceHistItem w JOIN ");
		builderString.append("BsymtAffiWorkplaceHist a ");
		builderString.append("WHERE a.sid = :sId ");
		builderString.append("AND a.strDate <= :baseDate ");
		builderString.append("AND a.endDate >= :baseDate ");
		builderString.append("AND w.sid = a.sid ");
		// builderString.append("AND w.bsymtWorkplaceHist.strD <= :baseDate ");
		// builderString.append("AND w.bsymtWorkplaceHist.endD >= :baseDate");
		SEL_WORKPLACE = builderString.toString();
	}	
	
	@Override
	public Map<String, String> getListWorkplace(String employeeId, DateRange dateRange) {
		Map<String, String> lstWkp = new HashMap<>();
		List<BsymtAffiWorkplaceHistItem> bsymtAffiWorkplaceHistItem = this.queryProxy()
				.query(SEL_WORKPLACE, BsymtAffiWorkplaceHistItem.class).setParameter("sId", employeeId)
				.setParameter("baseDate", dateRange.getEndDate()).getList();
		List<String> workPlaceIds = bsymtAffiWorkplaceHistItem.stream().map(item -> item.getWorkPlaceId())
				.collect(Collectors.toList());

		String query = "SELECT w FROM BsymtWorkplaceInfo w WHERE w.bsymtWorkplaceInfoPK.wkpid IN :wkpId AND w.bsymtWorkplaceHist.strD <= :baseDate AND w.bsymtWorkplaceHist.endD >= :baseDate";
		this.queryProxy().query(query, BsymtWorkplaceInfo.class).setParameter("wkpId", workPlaceIds)
				.setParameter("baseDate", dateRange.getEndDate()).getList().stream().forEach(w -> {
					lstWkp.put(w.getBsymtWorkplaceInfoPK().getWkpid(), w.getWkpName());
				});
		return lstWkp;
	}
	
	@Override
	public List<MonthlyPerformanceEmployeeDto> getListEmployee(List<String> lstJobTitle, List<String> lstEmployment,
			Map<String, String> lstWorkplace, List<String> lstClassification) {
		List<BsymtEmployeeDataMngInfo> lstEmployee = this.queryProxy()
				.query(SEL_EMPLOYEE, BsymtEmployeeDataMngInfo.class)
				.setParameter("lstWkp", lstWorkplace.keySet().stream().collect(Collectors.toList())).getList();
		List<String> ids = lstEmployee.stream().map((employee) -> {
			return employee.bsymtEmployeeDataMngInfoPk.pId.trim();
		}).collect(Collectors.toList());
		List<BpsmtPerson> lstPerson = this.queryProxy().query(SEL_PERSON, BpsmtPerson.class)
				.setParameter("lstPersonId",ids).getList();
		return lstEmployee.stream().map((employee) -> {
			for (BpsmtPerson person : lstPerson) {
				if (person.bpsmtPersonPk.pId.equals(employee.bsymtEmployeeDataMngInfoPk.pId)) {
					return new MonthlyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId,
							employee.employeeCode, person.personName, lstWorkplace.values().stream().findFirst().get(),
							lstWorkplace.keySet().stream().findFirst().get(), "", false);
				}
			}
			return new MonthlyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId, employee.employeeCode, "",
					lstWorkplace.values().stream().findFirst().get(), lstWorkplace.keySet().stream().findFirst().get(),
					"", false);
		}).collect(Collectors.toList());
	}

}
