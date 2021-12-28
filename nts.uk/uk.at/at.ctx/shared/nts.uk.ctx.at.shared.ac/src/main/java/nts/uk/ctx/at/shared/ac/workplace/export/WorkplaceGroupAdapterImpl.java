package nts.uk.ctx.at.shared.ac.workplace.export;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.WorkplaceGroupPublish;

/**
 * 職場グループAdapter (Implements)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.職場.Imported.職場グループAdapter
 * @author HieuLt
 */
@Stateless
public class WorkplaceGroupAdapterImpl implements WorkplaceGroupAdapter {

	@Inject 
	private WorkplaceGroupPublish workplaceGroupPublish;

	@Inject
	private WorkplacePub pub;

	@Override
	public List<WorkplaceGroupImport> getbySpecWorkplaceGroupID(List<String> workplaceGroupIds) {

		return workplaceGroupPublish.getByWorkplaceGroupID(workplaceGroupIds).stream()
				.map(c -> new WorkplaceGroupImport(
						c.getWorkplaceGroupId()
					,	c.getWorkplaceGroupCode()
					,	c.getWorkplaceName()
					,	c.getWorkplaceGroupType()
				)).collect(Collectors.toList());

	}

	@Override
	public List<EmpOrganizationImport> getGetAllEmployees(GeneralDate date, String workplaceGroupID) {

		return workplaceGroupPublish.getAllEmployees(date, workplaceGroupID).stream()
				.map( c-> new EmpOrganizationImport(
						new EmployeeId(c.getEmpId())
					,	c.getEmpCd()
					,	c.getBusinessName()
					,	c.getWorkplaceId()
					,	c.getWorkplaceGroupId())
				).collect(Collectors.toList());

	}


	@Override
	public List<String> getReferableEmp(String employeeId, GeneralDate date, DatePeriod period, String workplaceGroupID) {

		return workplaceGroupPublish.getReferableEmployees(employeeId, date, period, workplaceGroupID);

	}

	@Override
	public List<String> getAllReferableEmp(String employeeId, GeneralDate date, DatePeriod period) {

		return workplaceGroupPublish.getAllReferableEmployees(employeeId, date, period);

	}

	/* 
	 * revert commit eec80d76aea8820c3a178aba5440aa9cd219daa6
	 * # fix bug 120974
	 */
	@Override
	public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
		val result =  pub.getAffWkpHistItemByEmpDate(employeeID, date);
		String workPlaceId = "";
		if (result != null)
			workPlaceId = result.getWorkplaceId();
		return workPlaceId;
	}

}
