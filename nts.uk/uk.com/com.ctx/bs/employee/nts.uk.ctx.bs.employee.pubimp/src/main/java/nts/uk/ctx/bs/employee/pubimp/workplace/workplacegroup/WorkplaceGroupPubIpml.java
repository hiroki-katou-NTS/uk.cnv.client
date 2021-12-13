package nts.uk.ctx.bs.employee.pubimp.workplace.workplacegroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.access.role.SyRoleAdapter;
import nts.uk.ctx.bs.employee.dom.employee.service.EmployeeReferenceRangeImport;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;
import nts.uk.ctx.bs.employee.dom.workplace.adapter.GetStringWorkplaceManagerAdapter;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroup;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroup;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.EmployeeInfoData;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.GetAllEmpWhoBelongWorkplaceGroupService;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.GetEmpCanReferByWorkplaceGroupService;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceGroupExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport;
import nts.uk.ctx.bs.employee.pub.workplace.ResultRequest597Export;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.WorkplaceGroupPublish;
import nts.uk.shr.com.context.AppContexts;

/**
 * 職場グループPublish (Implements)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職場.職場グループ.Export.職場グループPublish
 * @author HieuLt
 */
@Stateless
public class WorkplaceGroupPubIpml implements WorkplaceGroupPublish {

	@Inject private WorkplaceGroupRespository repo;

	@Inject private WorkplacePub pub;

	@Inject private WorkplaceGroupRespository repoWorkplaceGroup;

	// 職場グループ所属情報Repository
	@Inject private AffWorkplaceGroupRespository repoAffWorkplaceGroup;

	@Inject private GetStringWorkplaceManagerAdapter adapter;

	@Inject private SyRoleAdapter syRoleAdapter;



	@Override
	public List<WorkplaceGroupExport> getByWorkplaceGroupID(List<String> workplaceGroupIds) {

		return new Require(repo).getAllById(workplaceGroupIds).stream()
				.map(c -> new WorkplaceGroupExport(c.getId(), c.getCode().v(), c.getName().v(), c.getType().value))
				.collect(Collectors.toList());
	}


	@Override
	public List<EmpOrganizationExport> getAllEmployees(GeneralDate date, String workplaceGroupId) {

		// 取得
		val requireGetAllEmp = new RequireAllEmpWhoBelongWklGroupSv(repoAffWorkplaceGroup, pub);
		val data = GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(requireGetAllEmp, DatePeriod.oneDay(date), workplaceGroupId);

		// 変換
		return data.stream()
				.map(c -> new EmpOrganizationExport(
						c.getEmployeeID()
					,	c.getEmployeeCode().map(EmployeeCode::v)
					,	c.getBusinessName()
					,	c.getWorkplaceID()
					,	c.getWorkplaceGroupID()
				)).collect(Collectors.toList());

	}


	@Override
	public List<String> getReferableEmployees(String employeeId, GeneralDate date, DatePeriod period, String workplaceGroupId) {

		// 職場グループ単位で参照可能な社員を取得する(職場グループ指定)
		val require = new RequireWorkgroupService(
				repoWorkplaceGroup, repoAffWorkplaceGroup, pub, adapter, pub, syRoleAdapter
			);
		return GetEmpCanReferByWorkplaceGroupService.getByWorkplaceGroup(require, employeeId, date, period, workplaceGroupId);

	}


	@Override
	public List<String> getAllReferableEmployees(String employeeId, GeneralDate date, DatePeriod period) {

		// 職場グループ単位で参照可能な社員を取得する(全社員)
		val require = new RequireWorkgroupService(
				repoWorkplaceGroup, repoAffWorkplaceGroup, pub, adapter, pub, syRoleAdapter
			);
		return GetEmpCanReferByWorkplaceGroupService.getAll(require, employeeId, date, period).values().stream()
				.flatMap(Collection::stream).collect(Collectors.toList());

	}



	@AllArgsConstructor
	class Require {
		@Inject
		private WorkplaceGroupRespository repo;

		/**
		 * 職場グループIDを指定して職場グループを取得する
		 * @param lstWKPGRPID
		 * @return
		 */
		public List<WorkplaceGroup> getAllById(List<String> lstWKPGRPID) {
			return repo.getAllById(AppContexts.user().companyId(), lstWKPGRPID);
		}
	}

	@AllArgsConstructor
	private static class RequireAllEmpWhoBelongWklGroupSv implements GetAllEmpWhoBelongWorkplaceGroupService.Require {
		@Inject
		private AffWorkplaceGroupRespository repo;

		@Inject
		private WorkplacePub pub;

		@Override
		public List<String> getWorkplaceBelongsWorkplaceGroup(String workplaceGroupId) {
			// JpaAffWorkplaceGroupRespository List<String> getWKPID(String CID,
			// String WKPGRPID);
			List<String> data = repo.getWKPID(AppContexts.user().companyId(), workplaceGroupId);
			return data;
		}

		@Override
		public List<EmployeeInfoData> getEmployeesWhoBelongWorkplace(String workplaceId, DatePeriod datePeriod) {
			// [No.597]職場の所属社員を取得する
			List<ResultRequest597Export> data = pub.getLstEmpByWorkplaceIdsAndPeriod(Arrays.asList(workplaceId),
					datePeriod);
			List<EmployeeInfoData> result = data.stream()
					.map(c -> new EmployeeInfoData(c.getSid(), c.getEmployeeCode(), c.getEmployeeName()))
					.collect(Collectors.toList());
			return result;
		}

	}

	@AllArgsConstructor
	private static class RequireWorkgroupService implements GetEmpCanReferByWorkplaceGroupService.Require {

		@Inject
		private WorkplaceGroupRespository repoWorkplaceGroup;

		// 職場グループ所属情報Repository
		@Inject
		private AffWorkplaceGroupRespository repoAffWorkplaceGroup;

		@Inject
		private WorkplacePub syWorkplacePub;

		@Inject
		private GetStringWorkplaceManagerAdapter adapter;

		@Inject
		private WorkplacePub wkplacePub;

		@Inject
		private SyRoleAdapter syRoleAdapter;

		@Override
		public List<WorkplaceGroup> getAll() {
			String companyId = AppContexts.user().companyId();
			List<WorkplaceGroup> data = repoWorkplaceGroup.getAll(companyId);
			return data;
		}

		@Override
		public List<AffWorkplaceGroup> getByListWKPID(List<String> WKPID) {
			String companyId = AppContexts.user().companyId();
			List<AffWorkplaceGroup> data = repoAffWorkplaceGroup.getByListWKPID(companyId, WKPID);
			return data;
		}

		@Override
		public boolean whetherThePersonInCharge(String empId) {
			// ログイン社員が*就業*担当者かどうか
			String roleId = AppContexts.user().roles().forAttendance();
			if (roleId == null)
				return false;
			return true;
		}

		@Override
		public EmployeeReferenceRangeImport getEmployeeReferRangeOfLoginEmployees(String empId) {
			// ※ログイン社員の社員参照範囲
			String roleID = null;
			if (AppContexts.user().roles().forAttendance() != null)
				roleID = AppContexts.user().roles().forAttendance();
			else if (AppContexts.user().roles().forCompanyAdmin() != null)
				roleID = AppContexts.user().roles().forCompanyAdmin();
			else if (AppContexts.user().roles().forGroupCompaniesAdmin() != null)
				roleID = AppContexts.user().roles().forGroupCompaniesAdmin();
			else if (AppContexts.user().roles().forOfficeHelper() != null)
				roleID = AppContexts.user().roles().forOfficeHelper();
			else if (AppContexts.user().roles().forPayroll() != null)
				roleID = AppContexts.user().roles().forPayroll();
			else if (AppContexts.user().roles().forPersonalInfo() != null)
				roleID = AppContexts.user().roles().forPersonalInfo();
			else if (AppContexts.user().roles().forPersonnel() != null)
				roleID = AppContexts.user().roles().forPersonnel();
			else if (AppContexts.user().roles().forSystemAdmin() != null)
				roleID = AppContexts.user().roles().forSystemAdmin();
			EmployeeReferenceRangeImport result = syRoleAdapter.getRangeByRoleID(roleID);
			return result;
		}

		@Override
		public List<String> getAllManagedWorkplaces(String empId, GeneralDate baseDate) {
			List<String> result = adapter.getAllWorkplaceID(empId, baseDate);
			return result;
		}

		@Override
		public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
			// [No.650]社員が所属している職場を取得する
			AffWorkplaceHistoryItemExport data = wkplacePub.getAffWkpHistItemByEmpDate(employeeID, date);
			return data.getWorkplaceId();
		}

		@Override
		public List<AffWorkplaceGroup> getWGInfo(List<String> WKPID) {
			String companyId = AppContexts.user().companyId();
			List<AffWorkplaceGroup> data = repoAffWorkplaceGroup.getByListWKPID(companyId, WKPID);
			return data;
		}

		@Override
		public List<String> getWorkplaceBelongsWorkplaceGroup(String workplaceGroupId) {
			String companyId = AppContexts.user().companyId();
			List<String> data = repoAffWorkplaceGroup.getWKPID(companyId, workplaceGroupId);
			return data;
		}

		@Override
		public List<EmployeeInfoData> getEmployeesWhoBelongWorkplace(String workplaceId, DatePeriod datePeriod) {
			// Request 597 職場の所属社員を取得する
			List<ResultRequest597Export> data = syWorkplacePub
					.getLstEmpByWorkplaceIdsAndPeriod(Arrays.asList(workplaceId), datePeriod);
			List<EmployeeInfoData> result = data.stream()
					.map(c -> new EmployeeInfoData(c.getSid(), c.getEmployeeCode(), c.getEmployeeName()))
					.collect(Collectors.toList());
			return result;
		}

	}

}
