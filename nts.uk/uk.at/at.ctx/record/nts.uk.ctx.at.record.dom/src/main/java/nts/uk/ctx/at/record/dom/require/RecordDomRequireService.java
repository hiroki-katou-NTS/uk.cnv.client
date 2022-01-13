package nts.uk.ctx.at.record.dom.require;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.tran.TransactionService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.adapter.classification.affiliate.AffClassificationAdapter;
import nts.uk.ctx.at.record.dom.adapter.company.SyCompanyRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.employment.EmploymentHistAdapter;
import nts.uk.ctx.at.record.dom.adapter.employment.SyEmploymentAdapter;
import nts.uk.ctx.at.record.dom.adapter.shift.pattern.GetPredWorkingDaysAdaptor;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkAdapter;
import nts.uk.ctx.at.record.dom.affiliationinformation.repository.AffiliationInforOfDailyPerforRepository;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.PCLogOnInfoOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.export.AggregateSpecifiedDailys;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionRepository;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.dom.monthly.MonthlyAggregateForEmployees;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.AgeementTimeCommonSettingService;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetAgreementTime;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetExcessTimesYear;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetYearAndMultiMonthAgreementTime;
import nts.uk.ctx.at.record.dom.monthly.updatedomain.UpdateAllDomainMonthService;
import nts.uk.ctx.at.record.dom.monthly.verticaltotal.VerticalTotalAggregateService;
import nts.uk.ctx.at.record.dom.monthlyclosureupdatelog.MonthlyClosureUpdateErrorInforRepository;
import nts.uk.ctx.at.record.dom.monthlyclosureupdatelog.MonthlyClosureUpdateLogRepository;
import nts.uk.ctx.at.record.dom.monthlyclosureupdatelog.MonthlyClosureUpdatePersonLogRepository;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.cancelactuallock.CancelActualLock;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.logprocess.MonthlyClosureUpdateLogProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.monthlyupdatemgr.MonthlyUpdateMgr;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.ymupdate.ProcessYearMonthUpdate;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.GetClosurePeriod;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.MonthlyAggregationService;
import nts.uk.ctx.at.record.dom.monthlyprocess.vacation.CreateDailyInterimRemainMngs;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
import nts.uk.ctx.at.record.dom.raisesalarytime.repo.SpecificDateAttrOfDailyPerforRepo;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.CreateTempAnnLeaMngProc;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.FindAnnLeaUsedDaysFromPreviousToNextGrantDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnAndRsvRemNumWithinPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetPeriodFromPreviousToNextGrantDate;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.GetHolidayDetailByPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.GetRemainingNumberChildCareNurseService;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.GetRsvLeaRemNumUsageDetail;
import nts.uk.ctx.at.record.dom.reservation.bento.BentoReservationRepository;
import nts.uk.ctx.at.record.dom.reservation.bentomenu.BentoMenuRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.standardtime.AgreementDomainService;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementMonthSettingRepository;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementOperationSettingRepository;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementUnitSettingRepository;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementYearSettingRepository;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLockRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfoRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPersonRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisAdapter;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementUsageUnitRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistoryRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.GetRemainingNumberPublicHolidayService;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagementRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.GetDaysForCalcAttdRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveTimeRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnLeaMaxDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CalcAnnLeaAttendanceRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpAnnualHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.CareUsedNumberRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagementRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.ChildCareUsedNumberRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagementRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.CareLeaveRemainingInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.ChildCareLeaveRemInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.RervLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.RsvLeaveGrantRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.RsvLeaveGrantTimeRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByApplicationData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByRecordData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByScheData;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionRepository;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.MonthlyCalculationByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.com.ComFlexMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.emp.EmpFlexMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.sha.ShaFlexMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.wkp.WkpFlexMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.com.ComDeforLaborMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.com.ComRegulaMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.emp.EmpDeforLaborMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.emp.EmpRegulaMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.sha.ShaDeforLaborMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.sha.ShaRegulaMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.wkp.WkpDeforLaborMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.wkp.WkpRegulaMonthActCalSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.legaltransferorder.LegalTransferOrderSetOfAggrMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.getprocessingdate.GetProcessingDate;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth.AggregatePastMonthsService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.roundingset.RoundingSetOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.PayItemCountOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.VerticalTotalMethodOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Classification36AgreementTimeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Company36AgreedHoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Employment36HoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.Workplace36AgreedHoursRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.InterimRemainOffMonthProcess;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flex.CheckBeforeCalcFlexChangeService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flexshortage.FlexShortageLimitRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flexshortage.InsufficientFlexHolidayMntRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.aggframe.OuenAggregateFrameSetOfMonthlyRepo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.totalcount.TotalCountByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.VerticalTotalOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.workform.flex.MonthlyAggrSetOfFlexRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.OutsideOTSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.holiday.SuperHD60HConMedRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpConditionRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.FormulaRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrderRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.DailyStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYearRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.OperationStartSetDailyPerformRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.CheckCareService;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemService;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeIsFluidWork;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantYearHolidayRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LengthServiceRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.YearHolidayRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.CalcNextAnnLeaGrantInfo;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.GetNextAnnualLeaveGrantProcKdm002;

@Stateless
public class RecordDomRequireService {

	@Inject
	private TmpResereLeaveMngRepository tmpResereLeaveMngRepo;
	@Inject
	private SysEmploymentHisAdapter sysEmploymentHisAdapter;
	@Inject
	private RervLeaGrantRemDataRepository rervLeaGrantRemDataRepo;
	@Inject
	private WorkInformationRepository workInformationRepo;
	@Inject
	private AnnLeaRemNumEachMonthRepository annLeaRemNumEachMonthRepo;
	@Inject
	private LengthServiceRepository lengthServiceRepository;
	@Inject
	private GrantYearHolidayRepository grantYearHolidayRepo;
	@Inject
	private TmpAnnualHolidayMngRepository tmpAnnualHolidayMngRepo;
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepo;
	@Inject
	private OperationStartSetDailyPerformRepository operationStartSetDailyPerformRepo;
	@Inject
	private AnnualLeaveRemainHistRepository annualLeaveRemainHistRepo;
	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepo;
	@Inject
	private AnnLeaMaxDataRepository annLeaMaxDataRepo;
	@Inject
	private AnnLeaGrantRemDataRepository annLeaGrantRemDataRepo;
	@Inject
	private EmploymentHistAdapter employmentHistAdapter;
	@Inject
	private RemainCreateInforByScheData remainCreateInforByScheData;
	@Inject
	private RemainCreateInforByRecordData remainCreateInforByRecordData;
	@Inject
	private RemainCreateInforByApplicationData remainCreateInforByApplicationData;
	@Inject
	private UsageUnitSettingRepository usageUnitSettingRepo;
	@Inject
	private AffWorkplaceAdapter affWorkplaceAdapter;
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingOfDailyPerformanceRepo;
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepo;
	@Inject
	private SpecificDateAttrOfDailyPerforRepo specificDateAttrOfDailyPerforRepo;
	@Inject
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepo;
	@Inject
	private AnyItemValueOfDailyRepo anyItemValueOfDailyRepo;
	@Inject
	private PCLogOnInfoOfDailyRepo pcLogOnInfoOfDailyRepo;
	@Inject
	private AttendanceTimeRepository attendanceTimeRepo;
	@Inject
	private PayItemCountOfMonthlyRepository payItemCountOfMonthlyRepo;
	@Inject
	private OptionalItemRepository optionalItemRepo;
	@Inject
	private EmpConditionRepository empConditionRepo;
	@Inject
	private FormulaRepository formulaRepo;
	@Inject
	private FormulaDispOrderRepository formulaDispOrderRepo;
	@Inject
	private ActualLockRepository actualLockRepo;
	@Inject
	private LegalTransferOrderSetOfAggrMonthlyRepository legalTransferOrderSetOfAggrMonthlyRepo;
	@Inject
	private OvertimeWorkFrameRepository roleOvertimeWorkRepo;
	@Inject
	private HolidayAddtionRepository holidayAddtionRepo;
	@Inject
	private MonthlyAggrSetOfFlexRepository monthlyAggrSetOfFlexRepo;
	@Inject
	private InsufficientFlexHolidayMntRepository insufficientFlexHolidayMntRepo;
	@Inject
	private FlexShortageLimitRepository flexShortageLimitRepo;
	@Inject
	private RoundingSetOfMonthlyRepository roundingSetOfMonthlyRepo;
	@Inject
	private TotalTimesRepository totalTimesRepo;
	@Inject
	private AgreementOperationSettingRepository agreementOperationSettingRepo;
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepo;
	@Inject
	private ManagedParallelWithContext parallel;
	@Inject
	private CheckBeforeCalcFlexChangeService checkBeforeCalcFlexChangeService;
	@Inject
	private CompanyAdapter companyAdapter;
	@Inject
	private AnyItemOfMonthlyRepository anyItemOfMonthlyRepo;
	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepo;
	@Inject
	private EditStateOfMonthlyPerRepository editStateOfMonthlyPerRepo;
	/** 並列処理用 */
//	@Resource
//	private ManagedExecutorService executorService;
	@Inject
	private AffiliationInforOfDailyPerforRepository affiliationInforOfDailyPerforRepo;
	@Inject
	private SpecialHolidayRepository specialHolidayRepo;
	@Inject
	private AttendanceItemConvertFactory converterFactory;
	@Inject
	private GetPredWorkingDaysAdaptor predWorkingDaysAdaptor;
	@Inject
	private UpdateAllDomainMonthService updateAllDomainMonthService;
	@Inject
	private AgreementUnitSettingRepository agreementUnitSetRepo;
	@Inject
	private Workplace36AgreedHoursRepository agreementTimeWorkPlaceRepo;
	@Inject
	private AffClassificationAdapter affClassficationAdapter;
	@Inject
	private SyEmploymentAdapter syEmploymentAdapter;
	@Inject
	private Employment36HoursRepository agreementTimeOfEmploymentRepo;
	@Inject
	private Classification36AgreementTimeRepository agreementTimeOfClassificationRepo;
	@Inject
	private Company36AgreedHoursRepository agreementTimeCompanyRepo;
	@Inject
	private RemainMergeRepository remainMergeRepo;
	@Inject
	private AgreementYearSettingRepository agreementYearSettingRepo;
	@Inject
	private AgreementMonthSettingRepository agreementMonthSettingRepo;
	@Inject
	private AgreementTimeOfManagePeriodRepository agreementTimeOfManagePeriodRepo;
	@Inject
	private TargetPersonRepository targetPersonRepo;
	@Inject
	private ErrMessageInfoRepository errMessageInfoRepo;
	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepo;
	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepo;
	@Inject
	private LeaveManaDataRepository leaveManaDataRepo;
	@Inject
	private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
	@Inject
	private SpecialLeaveGrantRepository specialLeaveGrantRepo;
	@Inject
	private AnnualLeaveTimeRemainHistRepository annualLeaveTimeRemainHistRepo;
	@Inject
	private AnnualLeaveMaxHistRepository annualLeaveMaxHistRepo;
	@Inject
	private RsvLeaveGrantRemainHistRepository rsvLeaveGrantRemainHistRepo;
	@Inject
	private RsvLeaveGrantTimeRemainHistRepository rsvLeaveGrantTimeRemainHistRepo;
	@Inject
	private InterimRecAbasMngRepository interimRecAbasMngRepo;
	@Inject
	private InterimSpecialHolidayMngRepository interimSpecialHolidayMngRepo;
	@Inject
	private InterimRemainOffMonthProcess interimRemainOffMonthProcess;
	@Inject
	private MonthlyClosureUpdateErrorInforRepository monthlyClosureUpdateErrorInforRepo;
	@Inject
	private MonthlyClosureUpdateLogRepository monthlyClosureUpdateLogRepo;
	@Inject
	private MonthlyClosureUpdatePersonLogRepository monthlyClosureUpdatePersonLogRepo;
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkTimeSheetOfDailyRepo;
	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;
	@Inject
	private OuenAggregateFrameSetOfMonthlyRepo ouenAggregateFrameSetOfMonthlyRepo;
	@Inject
	private RegularLaborTimeComRepo regularLaborTimeComRepo;
	@Inject
	private DeforLaborTimeComRepo deforLaborTimeComRepo;
	@Inject
	private RegularLaborTimeWkpRepo regularLaborTimeWkpRepo;
	@Inject
	private DeforLaborTimeWkpRepo deforLaborTimeWkpRepo;
	@Inject
	private RegularLaborTimeEmpRepo regularLaborTimeEmpRepo;
	@Inject
	private DeforLaborTimeEmpRepo deforLaborTimeEmpRepo;
	@Inject
	private RegularLaborTimeShaRepo regularLaborTimeShaRepo;
	@Inject
	private DeforLaborTimeShaRepo deforLaborTimeShaRepo;
	@Inject
	private ShaFlexMonthActCalSetRepo shaFlexMonthActCalSetRepo;
	@Inject
	private ComFlexMonthActCalSetRepo comFlexMonthActCalSetRepo;
	@Inject
	private EmpFlexMonthActCalSetRepo empFlexMonthActCalSetRepo;
	@Inject
	private WkpFlexMonthActCalSetRepo wkpFlexMonthActCalSetRepo;
	@Inject
	private EmpDeforLaborMonthActCalSetRepo empDeforLaborMonthActCalSetRepo;
	@Inject
	private EmpRegulaMonthActCalSetRepo empRegulaMonthActCalSetRepo;
	@Inject
	private ComDeforLaborMonthActCalSetRepo comDeforLaborMonthActCalSetRepo;
	@Inject
	private ComRegulaMonthActCalSetRepo comRegulaMonthActCalSetRepo;
	@Inject
	private ShaDeforLaborMonthActCalSetRepo shaDeforLaborMonthActCalSetRepo;
	@Inject
	private ShaRegulaMonthActCalSetRepo shaRegulaMonthActCalSetRepo;
	@Inject
	private WkpDeforLaborMonthActCalSetRepo wkpDeforLaborMonthActCalSetRepo;
	@Inject
	private WkpRegulaMonthActCalSetRepo wkpRegulaMonthActCalSetRepo;
	@Inject
	private MonthlyWorkTimeSetRepo monthlyWorkTimeSetRepo;
	@Inject
	protected ComSubstVacationRepository comSubstVacationRepo;
	@Inject
	protected CompensLeaveComSetRepository compensLeaveComSetRepo;
	@Inject
	protected EmpEmployeeAdapter empEmployeeAdapter;
	@Inject
	protected GrantDateTblRepository grantDateTblRepo;
	@Inject
	protected AnnLeaEmpBasicInfoRepository annLeaEmpBasicInfoRepo;
	@Inject
	protected SpecialLeaveBasicInfoRepository specialLeaveBasicInfoRepo;
	@Inject
	protected EmpSubstVacationRepository empSubstVacationRepo;
	@Inject
	protected ShareEmploymentAdapter shareEmploymentAdapter;
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
	protected ClosureRepository closureRepo;
	@Inject
	protected ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	protected WorkTypeRepository workTypeRepo;
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
	protected SharedAffWorkPlaceHisAdapter sharedAffWorkPlaceHisAdapter;
	@Inject
	private VerticalTotalMethodOfMonthlyRepository verticalTotalMethodOfMonthlyRepo;
	@Inject
	private StampCardRepository stampCardRepo;
	@Inject
	private BentoReservationRepository bentoReservationRepo;
	@Inject
	private BentoMenuRepository bentoMenuRepo;
	@Inject
	private WeekRuleManagementRepo weekRuleManagementRepo;
	@Inject
	private IntegrationOfDailyGetter integrationOfDailyGetter;
	@Inject
	private GetProcessingDate getProcessingDate;
	@Inject
	private DailySnapshotWorkAdapter snapshotAdapter;
	@Inject
	private SuperHD60HConMedRepository superHD60HConMedRepo;
	@Inject
	protected ElapseYearRepository elapseYearRepository;
	@Inject
	private SyCompanyRecordAdapter syCompanyRecordAdapter;
	@Inject
	private MonthlyAggregationRemainingNumber monthlyAggregationRemainingNumber;
	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepo;
	@Inject
	private PayoutSubofHDManaRepository payoutSubofHDManaRepo;
	@Inject
	private CheckCareService checkChildCareService;
	@Inject
	private WorkingConditionItemService workingConditionItemService;
	@Inject
	private PublicHolidaySettingRepository publicHolidaySettingRepo;
	@Inject
	private PublicHolidayManagementUsageUnitRepository publicHolidayManagementUsageUnitRepo;
	@Inject
	private CompanyMonthDaySettingRepository companyMonthDaySettingRepo;
	@Inject
	private TempPublicHolidayManagementRepository tempPublicHolidayManagementRepo;
	@Inject
	private PublicHolidayCarryForwardDataRepository publicHolidayCarryForwardDataRepo;
	@Inject
	private EmploymentMonthDaySettingRepository employmentMonthDaySettingRepo;
	@Inject
	private WorkplaceMonthDaySettingRepository workplaceMonthDaySettingRepo;
	@Inject
	private EmployeeMonthDaySettingRepository employeeMonthDaySettingRepo;
	@Inject
	private PublicHolidayCarryForwardHistoryRepository publicHolidayCarryForwardHistoryRepo;
	@Inject
	private ChildCareUsedNumberRepository childCareUsedNumberRepo;
	@Inject
	private CareUsedNumberRepository careUsedNumberRepo;
	@Inject
	private ChildCareLeaveRemInfoRepository childCareLeaveRemInfoRepo;
	@Inject
	private CareLeaveRemainingInfoRepository careLeaveRemainingInfoRepo;
	@Inject
	private TempChildCareManagementRepository tempChildCareManagementRepo;
	@Inject
	private TempCareManagementRepository tempCareManagementRepo;
	@Inject
	private NursingLeaveSettingRepository nursingLeaveSettingRepo;
	@Inject 
	private ExecutionLogRepository executionLogRepo;
	@Inject
	private WorkingConditionRepository workingConditionRepository;
	@Inject
	private WorkDaysNumberOnLeaveCountRepository workDaysNumberOnLeaveCountRepo;
	@Inject 
	private GetPeriodFromPreviousToNextGrantDate getPeriodFromPreviousToNextGrantDate;
	@Inject
	protected TransactionService transaction;
	@Inject
	private EmploymentAdapter employmentAdapter;
	@Inject
	private CreatingDailyResultsConditionRepository creatingDailyResultsConditionRepo;
	@Inject
	private  CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;
	@Inject
	private EmpComHisAdapter empComHisAdapter;
  
	public static interface Require extends RemainNumberTempRequireService.Require, GetAnnAndRsvRemNumWithinPeriod.RequireM2, CalcAnnLeaAttendanceRate.RequireM3,
		GetClosurePeriod.RequireM1, GetClosureStartForEmployee.RequireM1, CalcNextAnnLeaGrantInfo.RequireM1, GetNextAnnualLeaveGrantProcKdm002.RequireM1,
		GetYearAndMultiMonthAgreementTime.RequireM1, InterimRemainOffPeriodCreateData.RequireM2, DailyStatutoryLaborTime.RequireM1, AggregateMonthlyRecordService.RequireM1,
		MonAggrCompanySettings.RequireM6, WorkTimeIsFluidWork.RequireM2, MonAggrEmployeeSettings.RequireM2, MonthlyCalculationByPeriod.RequireM1, GetClosurePeriod.RequireM2,
		VerticalTotalOfMonthly.RequireM1, TotalCountByPeriod.RequireM1, GetAgreementTime.RequireM4, WorkingConditionService.RequireM1, MonthlyAggregationService.RequireM1,
		AgeementTimeCommonSettingService.RequireM1, CreateTempAnnLeaMngProc.RequireM3, AggregateSpecifiedDailys.RequireM1, ClosureService.RequireM6, ClosureService.RequireM5,
		MonthlyUpdateMgr.RequireM4, MonthlyClosureUpdateLogProcess.RequireM3, CancelActualLock.RequireM1, ProcessYearMonthUpdate.RequireM1, BreakDayOffMngInPeriodQuery.RequireM2,
		AgreementDomainService.RequireM5, AgreementDomainService.RequireM6, GetAgreementTime.RequireM5, VerticalTotalAggregateService.RequireM1, GetExcessTimesYear.RequireM2, 
		GetRemainingNumberPublicHolidayService.RequireM1, GetRemainingNumberChildCareNurseService.Require, GetDaysForCalcAttdRate.RequireM2, MonthlyAggregateForEmployees.Require, AggregatePastMonthsService.Require, 
		GetHolidayDetailByPeriod.Require, GetRsvLeaRemNumUsageDetail.Require, FindAnnLeaUsedDaysFromPreviousToNextGrantDate.Require,  CreateDailyInterimRemainMngs.Require{

		Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate);
		
		List<WorkingConditionItem> workingConditionItemClones(List<String> employeeId, GeneralDate baseDate);

	}

	public Require createRequire() {
		return new RecordDomRequireServiceImpl(comSubstVacationRepo, compensLeaveComSetRepo, specialLeaveGrantRepo, empEmployeeAdapter, grantDateTblRepo,
				annLeaEmpBasicInfoRepo, specialHolidayRepo, interimSpecialHolidayMngRepo, specialLeaveBasicInfoRepo, interimRecAbasMngRepo, empSubstVacationRepo,
				substitutionOfHDManaDataRepo, payoutManagementDataRepo, interimBreakDayOffMngRepo, comDayOffManaDataRepo, companyAdapter,
				shareEmploymentAdapter, leaveManaDataRepo, workingConditionItemRepo, workingConditionRepo, workTimeSettingRepo, fixedWorkSettingRepo,
				flowWorkSettingRepo, diffTimeWorkSettingRepo, flexWorkSettingRepo, predetemineTimeSettingRepo, closureRepo, closureEmploymentRepo,
				workTypeRepo, remainCreateInforByApplicationData, compensLeaveEmSetRepo, employmentSettingRepo, retentionYearlySettingRepo, annualPaidLeaveSettingRepo,
				outsideOTSettingRepo, workdayoffFrameRepo, yearHolidayRepo, tmpResereLeaveMngRepo, sysEmploymentHisAdapter, rervLeaGrantRemDataRepo, workInformationRepo,
				annLeaRemNumEachMonthRepo, lengthServiceRepository, grantYearHolidayRepo, tmpAnnualHolidayMngRepo, attendanceTimeOfMonthlyRepo, operationStartSetDailyPerformRepo,
				annualLeaveRemainHistRepo, closureStatusManagementRepo, annLeaMaxDataRepo, annLeaGrantRemDataRepo, employmentHistAdapter, remainCreateInforByScheData,
				remainCreateInforByRecordData, usageUnitSettingRepo, affWorkplaceAdapter, timeLeavingOfDailyPerformanceRepo,
				temporaryTimeOfDailyPerformanceRepo, specificDateAttrOfDailyPerforRepo, employeeDailyPerErrorRepo, anyItemValueOfDailyRepo, pcLogOnInfoOfDailyRepo,
				attendanceTimeRepo, payItemCountOfMonthlyRepo, optionalItemRepo, empConditionRepo, formulaRepo, formulaDispOrderRepo, actualLockRepo,
				legalTransferOrderSetOfAggrMonthlyRepo, roleOvertimeWorkRepo, holidayAddtionRepo, monthlyAggrSetOfFlexRepo, insufficientFlexHolidayMntRepo,
				flexShortageLimitRepo, roundingSetOfMonthlyRepo, totalTimesRepo, agreementOperationSettingRepo, parallel, checkBeforeCalcFlexChangeService,
				anyItemOfMonthlyRepo, empCalAndSumExeLogRepo, editStateOfMonthlyPerRepo, affiliationInforOfDailyPerforRepo,
				converterFactory, predWorkingDaysAdaptor, updateAllDomainMonthService, agreementUnitSetRepo, agreementTimeWorkPlaceRepo, affClassficationAdapter, syEmploymentAdapter,
				agreementTimeOfEmploymentRepo, agreementTimeOfClassificationRepo, agreementTimeCompanyRepo, remainMergeRepo, agreementYearSettingRepo, agreementMonthSettingRepo,
				agreementTimeOfManagePeriodRepo, targetPersonRepo, errMessageInfoRepo,
				annualLeaveTimeRemainHistRepo, annualLeaveMaxHistRepo, rsvLeaveGrantRemainHistRepo,
				rsvLeaveGrantTimeRemainHistRepo, interimRemainOffMonthProcess, monthlyClosureUpdateErrorInforRepo, monthlyClosureUpdateLogRepo,
				monthlyClosureUpdatePersonLogRepo, ouenWorkTimeSheetOfDailyRepo, ouenWorkTimeOfDailyRepo, ouenAggregateFrameSetOfMonthlyRepo, regularLaborTimeComRepo, deforLaborTimeComRepo,
				regularLaborTimeWkpRepo, deforLaborTimeWkpRepo, regularLaborTimeEmpRepo, deforLaborTimeEmpRepo, regularLaborTimeShaRepo, deforLaborTimeShaRepo, shaFlexMonthActCalSetRepo,
				comFlexMonthActCalSetRepo, empFlexMonthActCalSetRepo, wkpFlexMonthActCalSetRepo, empDeforLaborMonthActCalSetRepo, empRegulaMonthActCalSetRepo, comDeforLaborMonthActCalSetRepo,
				comRegulaMonthActCalSetRepo, shaDeforLaborMonthActCalSetRepo, shaRegulaMonthActCalSetRepo, wkpDeforLaborMonthActCalSetRepo, wkpRegulaMonthActCalSetRepo, monthlyWorkTimeSetRepo,
				verticalTotalMethodOfMonthlyRepo, stampCardRepo, bentoReservationRepo, bentoMenuRepo, integrationOfDailyGetter, weekRuleManagementRepo, sharedAffWorkPlaceHisAdapter, getProcessingDate,
//				elapseYearRepository, syCompanyRecordAdapter, snapshotAdapter, superHD60HConMedRepo, monthlyAggregationRemainingNumber,
//				payoutSubofHDManaRepo, leaveComDayOffManaRepo , checkChildCareService, workingConditionItemService,executionLogRepo);
				elapseYearRepository, syCompanyRecordAdapter, snapshotAdapter, superHD60HConMedRepo, monthlyAggregationRemainingNumber,
				payoutSubofHDManaRepo, leaveComDayOffManaRepo , checkChildCareService, workingConditionItemService, publicHolidaySettingRepo, publicHolidayManagementUsageUnitRepo,
				companyMonthDaySettingRepo,tempPublicHolidayManagementRepo, publicHolidayCarryForwardDataRepo, employmentMonthDaySettingRepo, workplaceMonthDaySettingRepo,
				employeeMonthDaySettingRepo, publicHolidayCarryForwardHistoryRepo, childCareUsedNumberRepo, careUsedNumberRepo, childCareLeaveRemInfoRepo, careLeaveRemainingInfoRepo,
				tempChildCareManagementRepo, tempCareManagementRepo, nursingLeaveSettingRepo,executionLogRepo,workingConditionRepository, transaction, employmentAdapter, creatingDailyResultsConditionRepo, 
				getPeriodFromPreviousToNextGrantDate, workDaysNumberOnLeaveCountRepo, calculateDailyRecordServiceCenter, empComHisAdapter);
	}
}
