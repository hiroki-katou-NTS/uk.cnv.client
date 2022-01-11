package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByApplicationData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByRecordData;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.OutsideOTSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.service.OutsideOTSettingService;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.DailyStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYearRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.CheckCareService;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemService;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantYearHolidayRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LengthServiceRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.YearHolidayRepository;

@Stateless
public class RemainNumberTempRequireService {
	@Inject
	protected ComSubstVacationRepository comSubstVacationRepo;
	@Inject
	protected CompensLeaveComSetRepository compensLeaveComSetRepo;
	@Inject
	protected SpecialLeaveGrantRepository specialLeaveGrantRepo;
	@Inject
	protected EmpEmployeeAdapter empEmployeeAdapter;
	@Inject
	protected GrantDateTblRepository grantDateTblRepo;
	@Inject
	protected AnnLeaEmpBasicInfoRepository annLeaEmpBasicInfoRepo;
	@Inject
	protected SpecialHolidayRepository specialHolidayRepo;
	@Inject
	protected InterimSpecialHolidayMngRepository interimSpecialHolidayMngRepo;
	@Inject
	protected SpecialLeaveBasicInfoRepository specialLeaveBasicInfoRepo;
	@Inject
	protected InterimRecAbasMngRepository interimRecAbasMngRepo;
	@Inject
	protected EmpSubstVacationRepository empSubstVacationRepo;
	@Inject
	protected SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
	@Inject
	protected PayoutManagementDataRepository payoutManagementDataRepo;
	@Inject
	protected InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
	@Inject
	protected ComDayOffManaDataRepository comDayOffManaDataRepo;
	@Inject
	protected CompanyAdapter companyAdapter;
	@Inject
	protected ShareEmploymentAdapter shareEmploymentAdapter;
	@Inject
	protected LeaveManaDataRepository leaveManaDataRepo;
	@Inject
	protected WorkingConditionItemRepository workingConditionItemRepo;
	@Inject
	protected WorkingConditionRepository workingConditionRepo;
	@Inject
	protected WorkTimeSettingRepository workTimeSettingRepo;
	@Inject
	protected FixedWorkSettingRepository fixedWorkSettingRepo;
	@Inject
	protected FlowWorkSettingRepository flowWorkSettingRepo;
	@Inject
	protected DiffTimeWorkSettingRepository diffTimeWorkSettingRepo;
	@Inject
	protected FlexWorkSettingRepository flexWorkSettingRepo;
	@Inject
	protected PredetemineTimeSettingRepository predetemineTimeSettingRepo;
	@Inject
	protected ClosureRepository closureRepo;
	@Inject
	protected ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	protected WorkTypeRepository workTypeRepo;
	@Inject
	protected RemainCreateInforByApplicationData remainCreateInforByApplicationData;
	@Inject
	protected CompensLeaveEmSetRepository compensLeaveEmSetRepo;
	@Inject
	protected EmploymentSettingRepository employmentSettingRepo;
	@Inject
	protected RetentionYearlySettingRepository retentionYearlySettingRepo;
	@Inject
	protected AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepo;
	@Inject
	protected OutsideOTSettingRepository outsideOTSettingRepo;
	@Inject
	protected WorkdayoffFrameRepository workdayoffFrameRepo;
	@Inject
	protected YearHolidayRepository yearHolidayRepo;
	@Inject
	protected UsageUnitSettingRepository usageUnitSettingRepo;
	@Inject
	protected RegularLaborTimeComRepo regularLaborTimeComRepo;
	@Inject
	protected DeforLaborTimeComRepo deforLaborTimeComRepo;
	@Inject
	protected RegularLaborTimeWkpRepo regularLaborTimeWkpRepo;
	@Inject
	protected DeforLaborTimeWkpRepo deforLaborTimeWkpRepo;
	@Inject
	protected RegularLaborTimeEmpRepo regularLaborTimeEmpRepo;
	@Inject
	protected DeforLaborTimeEmpRepo deforLaborTimeEmpRepo;
	@Inject
	protected RegularLaborTimeShaRepo regularLaborTimeShaRepo;
	@Inject
	protected DeforLaborTimeShaRepo deforLaborTimeShaRepo;
	@Inject
	protected SharedAffWorkPlaceHisAdapter sharedAffWorkPlaceHisAdapter;
	@Inject
	protected LengthServiceRepository lengthServiceRepo;
	@Inject
	protected GrantYearHolidayRepository grantYearHolidayRepo;
	@Inject
	protected PayoutSubofHDManaRepository payoutSubofHDManaRepo;
	@Inject
	protected CheckCareService checkChildCareService;
	@Inject
	protected LeaveComDayOffManaRepository leaveComDayOffManaRepo;
	@Inject
	protected WorkTimeSettingService workTimeSettingService;
	@Inject
	protected WorkingConditionItemService workingConditionItemService;
	@Inject
	protected RemainCreateInforByRecordData remainCreateInforByRecordData;
	@Inject
	protected SysEmploymentHisAdapter sysEmploymentHisAdapter;
	@Inject
	protected ElapseYearRepository elapseYearRepository;
	@Inject
	private EmpComHisAdapter empComHisAdapter;
	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepo;

	public static interface Require
			extends InterimRemainOffPeriodCreateData.RequireM4, BreakDayOffMngInPeriodQuery.RequireM10,
			AbsenceReruitmentMngInPeriodQuery.RequireM10, NumberRemainVacationLeaveRangeQuery.Require,
			GetClosureStartForEmployee.RequireM1, ClosureService.RequireM3,
			OutsideOTSettingService.RequireM2, OutsideOTSettingService.RequireM1,
			AbsenceTenProcess.RequireM1, AbsenceTenProcess.RequireM2, AbsenceTenProcess.RequireM4,
			AbsenceTenProcess.RequireM3, AbsenceReruitmentMngInPeriodQuery.RequireM2,
			WorkingConditionService.RequireM1, DailyStatutoryLaborTime.RequireM1,
			CalcNextAnnualLeaveGrantDate.RequireM2, SpecialHoliday.Require {

	}

	public Require createRequire() {
		return new RequireImp(comSubstVacationRepo, compensLeaveComSetRepo, specialLeaveGrantRepo,
				empEmployeeAdapter, grantDateTblRepo, annLeaEmpBasicInfoRepo, specialHolidayRepo,
				interimSpecialHolidayMngRepo, specialLeaveBasicInfoRepo, interimRecAbasMngRepo,
				empSubstVacationRepo, substitutionOfHDManaDataRepo,
				payoutManagementDataRepo, interimBreakDayOffMngRepo, comDayOffManaDataRepo,
				companyAdapter, shareEmploymentAdapter, leaveManaDataRepo, workingConditionItemRepo,
				workingConditionRepo, workTimeSettingRepo, fixedWorkSettingRepo, flowWorkSettingRepo,
				diffTimeWorkSettingRepo, flexWorkSettingRepo, predetemineTimeSettingRepo, closureRepo,
				closureEmploymentRepo, workTypeRepo, remainCreateInforByApplicationData,
				compensLeaveEmSetRepo, employmentSettingRepo, retentionYearlySettingRepo,
				annualPaidLeaveSettingRepo, outsideOTSettingRepo, workdayoffFrameRepo,
				yearHolidayRepo, usageUnitSettingRepo, regularLaborTimeComRepo, deforLaborTimeComRepo,
				regularLaborTimeWkpRepo, deforLaborTimeWkpRepo, regularLaborTimeEmpRepo,
				deforLaborTimeEmpRepo, regularLaborTimeShaRepo, deforLaborTimeShaRepo,
				sharedAffWorkPlaceHisAdapter, lengthServiceRepo, grantYearHolidayRepo,
				payoutSubofHDManaRepo, leaveComDayOffManaRepo, checkChildCareService, workingConditionItemService,
				remainCreateInforByRecordData, sysEmploymentHisAdapter, elapseYearRepository, empComHisAdapter,
				closureStatusManagementRepo
				);
	}


}
