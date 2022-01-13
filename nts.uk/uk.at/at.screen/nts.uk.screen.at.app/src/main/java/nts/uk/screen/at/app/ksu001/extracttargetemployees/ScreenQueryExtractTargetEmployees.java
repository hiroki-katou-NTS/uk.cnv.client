/**
 *
 */
package nts.uk.screen.at.app.ksu001.extracttargetemployees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.at.schedule.dom.adapter.classification.SyClassificationAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.jobtitle.PositionImport;
import nts.uk.ctx.at.schedule.dom.adapter.jobtitle.SyJobTitleAdapter;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.EmpClassifiImport;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.EmployeePosition;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortSetting;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortSettingRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRank;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.RankPriority;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.RankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeamRepository;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkFormHisItem;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkStyleHistoryRepository;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassificationRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.EmployeeSearchCallSystemType;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.RegulationInfoEmpQuery;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.query.pub.employee.EmployeeSearchQueryDto;
import nts.uk.query.pub.employee.RegulationInfoEmployeeExport;
import nts.uk.query.pub.employee.RegulationInfoEmployeePub;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author laitv
 * <<ScreenQuery>> 対象社員を抽出する
 *
 */
@Stateless
public class ScreenQueryExtractTargetEmployees {

	@Inject
	private EmployeeInformationAdapter empInfoAdapter;

	@Inject
	private WorkplaceGroupAdapter workplaceGroupAdapter;
	@Inject
	private RegulationInfoEmployeePub regulInfoEmpPub;
	@Inject
	private  SortSettingRepository sortSettingRepo;
	@Inject
	private  BelongScheduleTeamRepository belongScheduleTeamRepo;
	@Inject
	private  EmployeeRankRepository employeeRankRepo;
	@Inject
	private  RankRepository rankRepo;
	@Inject
	private  SyJobTitleAdapter syJobTitleAdapter;
	@Inject
	private  SyClassificationAdapter syClassificationAdapter;
	@Inject
	private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHisRepo;
	@Inject
	private NurseClassificationRepository nurseClassificationRepo;


	final static String SPACE = " ";
	final static String ZEZO_TIME = "00:00";
	final static String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";

	public List<EmployeeInformationImport> getListEmp(ExtractTargetEmployeesParam param) {

		// step 1 get domainSv 組織を指定して参照可能な社員を取得する
		RequireGetEmpImpl requireGetEmpImpl = new RequireGetEmpImpl();
		String epmloyeeId = AppContexts.user().employeeId();
		TargetOrgIdenInfor targetOrgIdenInfor = param.targetOrgIdenInfor;
		List<String> sids = GetEmpCanReferService.getByOrg(requireGetEmpImpl, epmloyeeId, param.systemDate, param.period, targetOrgIdenInfor);

		// step 2, 3
		EmployeeInformationQueryDtoImport input = new EmployeeInformationQueryDtoImport(sids, param.systemDate, false, false, false, false, false, false);

		List<EmployeeInformationImport> listEmp = empInfoAdapter.getEmployeeInfo(input);
		//2020/9/7　発注済み step 4
		//※スケ①-5_スケ修正(職場別)
		if(listEmp.isEmpty()){
			throw new BusinessException("Msg_1779");
		}
		listEmp.sort( Comparator.comparing(EmployeeInformationImport :: getEmployeeCode));
		List<String> sids2 = listEmp.stream().map(m -> m.getEmployeeId()).collect(Collectors.toList());

		// step 5 call AR_並び替え設定.
		RequireSortEmpImpl requireSortEmpImpl = new RequireSortEmpImpl(belongScheduleTeamRepo,
				employeeRankRepo, rankRepo, syJobTitleAdapter, syClassificationAdapter, empMedicalWorkStyleHisRepo,
				nurseClassificationRepo);
		// 並び替える(Require, 年月日, List<社員ID>)
		Optional<SortSetting> sortSetting = sortSettingRepo.get(AppContexts.user().companyId());
		// if $並び替え設定.empty---return 社員IDリスト
		if (!sortSetting.isPresent()) {
			return listEmp;
		}

		List<String> listSidOrder = sortSetting.get().sort(requireSortEmpImpl, param.systemDate, sids2);

		listEmp.sort(Comparator.comparing(v-> listSidOrder.indexOf(v.getEmployeeId())));

		return listEmp;
	}

	@AllArgsConstructor
	private class RequireGetEmpImpl implements GetEmpCanReferService.Require {

		@Override
		public List<String> getEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period, String workplaceGroupID) {
			List<String> data = workplaceGroupAdapter.getReferableEmp(empId, date, period, workplaceGroupID);
			return data;
		}

		@Override
		public List<String> getAllEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period) {
			// don't have to implement it
			return null;
		}

		@Override
		public List<String> sortEmployee(List<String> lstmployeeId, EmployeeSearchCallSystemType sysAtr, Integer sortOrderNo,
				GeneralDate referenceDate, Integer nameType) {
			List<String> data = regulInfoEmpPub.sortEmployee(
					AppContexts.user().companyId(),
					lstmployeeId,
					sysAtr.value,
					sortOrderNo,
					nameType,
					GeneralDateTime.fromString(referenceDate.toString() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT));
			return data;
		}

		@Override
		public String getRoleID() {

			return AppContexts.user().roles().forAttendance();
		}

		@Override
		public List<String> searchEmployee(RegulationInfoEmpQuery q, String roleId) {
			EmployeeSearchQueryDto query = EmployeeSearchQueryDto.builder()
					.baseDate(GeneralDateTime.fromString(q.getBaseDate().toString() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT))
					.referenceRange(q.getReferenceRange().value)
					.systemType(q.getSystemType().value)
					.filterByWorkplace(q.getFilterByWorkplace())
					.workplaceCodes(q.getWorkplaceIds())
					.filterByEmployment(false)
					.employmentCodes(new ArrayList<String>())
					.filterByDepartment(false)
					.departmentCodes(new ArrayList<String>())
					.filterByClassification(false)
					.classificationCodes(new ArrayList<String>())
					.filterByJobTitle(false)
					.jobTitleCodes(new ArrayList<String>())
					.filterByWorktype(false)
					.worktypeCodes(new ArrayList<String>())
					.filterByClosure(false)
					.closureIds(new ArrayList<Integer>())
					.periodStart( GeneralDateTime.fromString(q.getPeriodStart() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT) )
					.periodEnd( GeneralDateTime.fromString(q.getPeriodEnd() + SPACE + ZEZO_TIME, DATE_TIME_FORMAT) )
					.includeIncumbents(true)
					.includeWorkersOnLeave(true)
					.includeOccupancy(true)
					.includeRetirees(false)
					.includeAreOnLoan(false)
					.includeGoingOnLoan(false)
					.retireStart(GeneralDateTime.now())
					.retireEnd(GeneralDateTime.now())
					.sortOrderNo(null)
					.nameType(null)

					.build();
			List<RegulationInfoEmployeeExport> data = regulInfoEmpPub.find(query);
			List<String> resultList = data.stream().map(item -> item.getEmployeeId())
					.collect(Collectors.toList());
			return resultList;
		}

	}

	@AllArgsConstructor
	private static class RequireSortEmpImpl implements SortSetting.Require {

		@Inject
		private  BelongScheduleTeamRepository belongScheduleTeamRepo;
		@Inject
		private  EmployeeRankRepository employeeRankRepo;
		@Inject
		private  RankRepository rankRepo;
		@Inject
		private  SyJobTitleAdapter syJobTitleAdapter;
		@Inject
		private  SyClassificationAdapter syClassificationAdapter;
		@Inject
		private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHisRepo;
		@Inject
		private NurseClassificationRepository nurseClassificationRepo;

		@Override
		public List<BelongScheduleTeam> getScheduleTeam(List<String> empIDs) {
			return belongScheduleTeamRepo.get(AppContexts.user().companyId(), empIDs);
		}

		@Override
		public List<EmployeeRank> getEmployeeRanks(List<String> lstSID) {
			return employeeRankRepo.getAll(lstSID);
		}

		@Override
		public List<EmployeePosition> getPositionEmps(GeneralDate ymd, List<String> lstEmp) {
			List<EmployeePosition> data = syJobTitleAdapter.findSJobHistByListSIdV2(lstEmp, ymd);
			return data;
		}

		@Override
		public List<PositionImport> getCompanyPosition(GeneralDate ymd) {
			List<PositionImport> data = syJobTitleAdapter.findAll(AppContexts.user().companyId(), ymd);
			return data;
		}

		@Override
		public List<EmpClassifiImport> getEmpClassifications(GeneralDate ymd, List<String> lstEmpId) {
			List<EmpClassifiImport> data = syClassificationAdapter.getByListSIDAndBasedate(ymd, lstEmpId);
			return data;
		}

		@Override
		public Optional<RankPriority> getRankPriorities() {
			Optional<RankPriority> data = rankRepo.getRankPriority(AppContexts.user().companyId());
			return data;
		}

		@Override
		public List<EmpMedicalWorkFormHisItem> getEmpClassifications(List<String> listEmp, GeneralDate referenceDate) {
			List<EmpMedicalWorkFormHisItem> data = empMedicalWorkStyleHisRepo.get(listEmp, referenceDate);
			return data;
		}

		@Override
		public List<NurseClassification> getListCompanyNurseCategory() {
			List<NurseClassification> data = nurseClassificationRepo.getListCompanyNurseCategory(AppContexts.user().companyId());
			return data;
		}
	}
}
