package nts.uk.screen.at.app.ksu001.sortsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.find.schedule.employeeinfo.sortsetting.OrderListDto;
import nts.uk.ctx.at.schedule.dom.adapter.classification.SyClassificationAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.jobtitle.PositionImport;
import nts.uk.ctx.at.schedule.dom.adapter.jobtitle.SyJobTitleAdapter;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.EmpClassifiImport;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.EmployeePosition;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.OrderedList;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortOrder;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortSetting;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortSetting.Require;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortSettingRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort.SortType;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmpRankInfor;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRank;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.GetEmRankInforService;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.Rank;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.RankPriority;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.RankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.EmpTeamInfor;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.GetScheduleTeamInfoService;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpLicenseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkFormHisItem;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkStyleHistoryRepository;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.GetEmpLicenseClassificationService;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassificationRepository;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HieuLt 並び替えした社員リストを取得
 */
@Stateless
public class GetSortedListEmployeeQuery {

	//////////////////////////////////////////////////////////////////////
	@Inject
	private BelongScheduleTeamRepository belongScheduleTeamRepository;

	@Inject
	private ScheduleTeamRepository teamRepository;

	@Inject
	private EmployeeRankRepository employeeRankRepository;

	@Inject
	private RankRepository rankRepository;

	@Inject
	private NurseClassificationRepository nurseClassificationRepo;

	@Inject
	private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHisRepo;

	@Inject
	private EmployeeInformationPub employeeInformationPub;

	@Inject
	private SortSettingRepository sortSettingRepo;
	@Inject
	private BelongScheduleTeamRepository belongScheduleTeamRepo;
	@Inject
	private EmployeeRankRepository employeeRankRepo;
	@Inject
	private RankRepository rankRepo;
	@Inject
	private SyJobTitleAdapter syJobTitleAdapter;
	@Inject
	private SyClassificationAdapter syClassificationAdapter;
	@Inject
	private SyEmployeePub syEmployeePub;

	public SortedListEmpDto getSortListEmp(String companyId, GeneralDate date, List<String> lstEmpId,
			List<EmployeeSwapDto> selectedEmployeeSwap) {
		List<EmpTeamInfor> empTeamInfors = new ArrayList<>();
		List<EmpRankInfor> empRankInfors = new ArrayList<>();
		List<EmpLicenseClassification> empLicenseClassifications = new ArrayList<>();
		List<String> listSidEmp = new ArrayList<>();
		List<EmployeeBaseDto> lstEmpBase = new ArrayList<>();
		List<OrderListDto> lstOrders = new ArrayList<>();

		// 所属スケジュールチーム情報を取得する

		// 1 :取得する :get List<社員所属チーム情報>
		GetScheduleTeamInfoImpl getScheduleTeamInfoImpl = new GetScheduleTeamInfoImpl(belongScheduleTeamRepository,
				teamRepository);
		empTeamInfors = GetScheduleTeamInfoService.get(getScheduleTeamInfoImpl, lstEmpId);
		List<EmpTeamInforDto> lstEmpTeamInforDto = empTeamInfors.stream()
				.map(x -> new EmpTeamInforDto(x.getEmployeeID(),
						x.getOptScheduleTeamCd().isPresent() ? x.getOptScheduleTeamCd().get().v() : "",
						x.getOptScheduleTeamName().isPresent() ? x.getOptScheduleTeamName().get().v() : ""))
				.collect(Collectors.toList());
		// 2: 取得する(Require, List<社員ID>) : List<社員ランク情報>
		GetEmRankInforImpl getEmRankInforImpl = new GetEmRankInforImpl(employeeRankRepository, rankRepository);
		empRankInfors = GetEmRankInforService.get(getEmRankInforImpl, lstEmpId);
		List<EmpRankInforDto> lstEmpRankInforDto = empRankInfors.stream()
				.map(x -> new EmpRankInforDto(x.getEmpId(),
						x.getRankCode().isPresent() ? x.getRankCode().get().v() : "",
						x.getRankSymbol().isPresent() ? x.getRankSymbol().get().v() : ""))
				.collect(Collectors.toList());

		// 3:取得する(Require, 年月日, List<社員ID>) : List<社員免許区分>
		GetEmpLicenseClassificationImpl getEmpLicenseClassificationImpl = new GetEmpLicenseClassificationImpl(
				nurseClassificationRepo, empMedicalWorkStyleHisRepo);
		empLicenseClassifications = GetEmpLicenseClassificationService.get(getEmpLicenseClassificationImpl, date,
				lstEmpId);
		List<EmpLicenseClassificationDto> lstEmpLicenseClassificationDto = empLicenseClassifications.stream()
				.map(x -> {
					
					if ( !x.getOptLicenseClassification().isPresent() ) {
						return new EmpLicenseClassificationDto(x.getEmpID(), "");
					}
					
					String licenseName = EnumAdaptor.convertToValueName(x.getOptLicenseClassification().get()).getLocalizedName();
					return new EmpLicenseClassificationDto(x.getEmpID(), licenseName);
				}).collect(Collectors.toList());
		// 4: call <get> <<Public>> 社員の情報を取得する
		// <<Public>> 社員の情報を取得する
		List<EmployeeInformationExport> empInfoLst = employeeInformationPub
				.find(EmployeeInformationQueryDto.builder().employeeIds(lstEmpId).referenceDate(date)
						.toGetWorkplace(false).toGetDepartment(false).toGetPosition(true).toGetEmployment(false)
						.toGetClassification(true).toGetEmploymentCls(false).build());

		Require requireSortSetting = new SortSettingRequireImp(sortSettingRepo, belongScheduleTeamRepo,
				employeeRankRepo, rankRepo, syJobTitleAdapter, syClassificationAdapter, empMedicalWorkStyleHisRepo,
				nurseClassificationRepo);
		List<OrderedList> orderedListaa = new ArrayList<>();
		for (EmployeeSwapDto item : selectedEmployeeSwap) {
			OrderedList data = new OrderedList(EnumAdaptor.valueOf(item.getSortType(), SortType.class),
					EnumAdaptor.valueOf(item.getSortOrder(), SortOrder.class));
			orderedListaa.add(data);
		}
		// 5: 並び替える(Require, 年月日, List<社員ID>)
		SortSetting sortSettingNew = SortSetting.create(companyId, orderedListaa);
		listSidEmp = sortSettingNew.sort(requireSortSetting, date, lstEmpId);

		lstEmpBase = syEmployeePub.getByListSid(lstEmpId).stream()
				.map(x -> new EmployeeBaseDto(x.getSid(), x.getScd(), x.getBussinessName()))
				.collect(Collectors.toList());
		
		List<OrderListDto> listOrderColum = new ArrayList<>();
		selectedEmployeeSwap.forEach(x -> {
			listOrderColum.add(new OrderListDto(x.getSortOrder(), x.getSortType()));
		});

		lstOrders.add(new OrderListDto(0, I18NText.getText("KSU001_4048")));
		lstOrders.add(new OrderListDto(0, I18NText.getText("KSU001_4049")));
		lstOrders.add(new OrderListDto(0, I18NText.getText("KSU001_4050")));
		lstOrders.add(new OrderListDto(0, I18NText.getText("Com_Jobtitle")));
		lstOrders.add(new OrderListDto(0, I18NText.getText("Com_Class")));
		List<OrderListDto> listOrderColum1 = listOrderColum.stream().map(x -> {
			String sortName = "";
			if (x.getSortType() == 0) {
				sortName = I18NText.getText("KSU001_4048");
			} else if (x.getSortType() == 1) {
				sortName = I18NText.getText("KSU001_4049");
			} else if (x.getSortType() == 2) {
				sortName = I18NText.getText("KSU001_4050");
			} else if (x.getSortType() == 3) {
				sortName = I18NText.getText("Com_Jobtitle");
			} else if (x.getSortType() == 4) {
				sortName = I18NText.getText("Com_Class");
			}
			return new OrderListDto(x.getSortOrder(), sortName);
		}).collect(Collectors.toList());
		if (!listOrderColum1.isEmpty()) {
			lstOrders.removeAll(listOrderColum1);
			listOrderColum1.addAll(lstOrders);

		}
		List<EmplInforATR> lstEmplInforATR = empInfoLst.stream()
				.map(x -> new EmplInforATR(x.getEmployeeId(),
						x.getPosition() == null ? "" : x.getPosition().getPositionName(),
						x.getClassification() == null ? "" : x.getClassification().getClassificationName()))
				.collect(Collectors.toList());
		SortedListEmpDto data = new SortedListEmpDto(lstEmpTeamInforDto, lstEmpRankInforDto,
				lstEmpLicenseClassificationDto, lstEmplInforATR, listSidEmp, lstEmpBase, listOrderColum1);
		return data;

	}

	@AllArgsConstructor
	private static class GetScheduleTeamInfoImpl implements GetScheduleTeamInfoService.Require {

		@Inject
		private BelongScheduleTeamRepository belongScheduleTeamRepository;

		@Inject
		private ScheduleTeamRepository teamRepository;

		@Override
		public List<BelongScheduleTeam> get(List<String> lstEmpId) {
			String companyId = AppContexts.user().companyId();
			List<BelongScheduleTeam> belongScheduleTeams = belongScheduleTeamRepository.get(companyId, lstEmpId);
			return belongScheduleTeams;
		}

		@Override
		public List<ScheduleTeam> getAllSchedule(List<String> listWKPGRPID) {
			String companyId = AppContexts.user().companyId();
			List<ScheduleTeam> scheduleTeams = teamRepository.getAllSchedule(companyId, listWKPGRPID);
			return scheduleTeams;
		}

	}

	@AllArgsConstructor
	private static class GetEmRankInforImpl implements GetEmRankInforService.Require {
		@Inject
		private EmployeeRankRepository employeeRankRepository;

		@Inject
		private RankRepository rankRepository;

		@Override
		public List<EmployeeRank> getAll(List<String> lstSID) {
			List<EmployeeRank> data = employeeRankRepository.getAll(lstSID);
			return data;
		}

		@Override
		public List<Rank> getListRank() {
			List<Rank> data = rankRepository.getListRank(AppContexts.user().companyId());
			return data;
		}

	}

	@AllArgsConstructor
	private static class GetEmpLicenseClassificationImpl implements GetEmpLicenseClassificationService.Require {

		@Inject
		private NurseClassificationRepository nurseClassificationRepo;

		@Inject
		private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHisRepo;

		@Override
		public List<EmpMedicalWorkFormHisItem> getEmpClassifications(List<String> listEmp, GeneralDate referenceDate) {
			List<EmpMedicalWorkFormHisItem> data = empMedicalWorkStyleHisRepo.get(listEmp, referenceDate);
			return data;
		}

		@Override
		public List<NurseClassification> getListCompanyNurseCategory() {
			List<NurseClassification> data = nurseClassificationRepo
					.getListCompanyNurseCategory(AppContexts.user().companyId());
			return data;
		}

	}

	@AllArgsConstructor
	private static class SortSettingRequireImp implements SortSetting.Require {

		@Inject
		private SortSettingRepository sortSettingRepo;
		@Inject
		private BelongScheduleTeamRepository belongScheduleTeamRepo;
		@Inject
		private EmployeeRankRepository employeeRankRepo;
		@Inject
		private RankRepository rankRepo;
		@Inject
		private SyJobTitleAdapter syJobTitleAdapter;
		@Inject
		private SyClassificationAdapter syClassificationAdapter;
		@Inject
		private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHisRepo;
		@Inject
		private NurseClassificationRepository nurseClassificationRepo;

		@Override
		public List<EmpMedicalWorkFormHisItem> getEmpClassifications(List<String> listEmp, GeneralDate referenceDate) {
			List<EmpMedicalWorkFormHisItem> data = empMedicalWorkStyleHisRepo.get(listEmp, referenceDate);
			return data;
		}

		@Override
		public List<NurseClassification> getListCompanyNurseCategory() {
			List<NurseClassification> data = nurseClassificationRepo
					.getListCompanyNurseCategory(AppContexts.user().companyId());
			return data;
		}

		@Override
		public List<BelongScheduleTeam> getScheduleTeam(List<String> empIDs) {
			return belongScheduleTeamRepo.get(AppContexts.user().companyId(), empIDs);
		}

		@Override
		public List<EmployeeRank> getEmployeeRanks(List<String> lstSID) {
			return employeeRankRepo.getAll(lstSID);
		}

		@Override
		public Optional<RankPriority> getRankPriorities() {
			Optional<RankPriority> data = rankRepo.getRankPriority(AppContexts.user().companyId());
			return data;
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
	}

}
