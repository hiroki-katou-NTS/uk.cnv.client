package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.AffCompanyHistSharedImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyDto;
import nts.uk.ctx.at.shared.dom.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.outsideot.OutsideOTSetting;
import nts.uk.ctx.at.shared.dom.outsideot.OutsideOTSettingRepository;
import nts.uk.ctx.at.shared.dom.outsideot.service.OutsideOTSettingService;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemainRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.DataManagementAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveManagementService;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByApplicationData;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTbl;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.YearHolidayRepository;

@Stateless
public class RemainNumberTempRequireService {

	public static interface Require
			extends InterimRemainOffPeriodCreateData.RequireM4, BreakDayOffMngInPeriodQuery.RequireM10,
			AbsenceReruitmentMngInPeriodQuery.RequireM10, SpecialLeaveManagementService.RequireM5,
			GetClosureStartForEmployee.RequireM1, ClosureService.RequireM3,
			OutsideOTSettingService.RequireM2, OutsideOTSettingService.RequireM1, 
			AbsenceTenProcess.RequireM1, AbsenceTenProcess.RequireM2, AbsenceTenProcess.RequireM4,
			AbsenceTenProcess.RequireM3, AbsenceReruitmentMngInPeriodQuery.RequireM2,
			WorkingConditionService.RequireM1 {

	}

	public Require createRequire() {
		return new RequireImp();
	}
	
	public static class RequireImp implements Require {

		public RequireImp() {}
		
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
		protected InterimRemainRepository interimRemainRepo;
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

		@Override
		public Optional<GrantDateTbl> grantDateTbl(String companyId, int specialHolidayCode) {
			return grantDateTblRepo.findByCodeAndIsSpecified(companyId, specialHolidayCode);
		}

		@Override
		public List<ElapseYear> elapseYear(String companyId, int specialHolidayCode, String grantDateCode) {
			return grantDateTblRepo.findElapseByGrantDateCd(companyId, specialHolidayCode, grantDateCode);
		}

		@Override
		public Optional<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo(String employeeId) {
			return annLeaEmpBasicInfoRepo.get(employeeId);
		}
		
		@Override
		public Optional<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String specialId) {
			return specialLeaveGrantRepo.getBySpecialId(specialId);
		}

		@Override
		public List<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String sid, int speCode,
				DatePeriod datePriod, GeneralDate startDate, LeaveExpirationStatus expirationStatus) {

			return specialLeaveGrantRepo.getByNextDate(sid, speCode, datePriod, startDate, expirationStatus);
		}

		@Override
		public List<SpecialLeaveGrantRemainingData> specialLeaveGrantRemainingData(String sid, int specialLeaveCode,
				LeaveExpirationStatus expirationStatus, GeneralDate grantDate, GeneralDate deadlineDate) {

			return specialLeaveGrantRepo.getByPeriodStatus(sid, specialLeaveCode, expirationStatus, grantDate,
					deadlineDate);
		}

		@Override
		public List<InterimSpecialHolidayMng> interimSpecialHolidayMng(String mngId) {
			return interimSpecialHolidayMngRepo.findById(mngId);
		}

		@Override
		public Optional<SpecialLeaveBasicInfo> specialLeaveBasicInfo(String sid, int spLeaveCD, UseAtr use) {
			return specialLeaveBasicInfoRepo.getBySidLeaveCdUser(sid, spLeaveCD, use);
		}

		@Override
		public Optional<ComSubstVacation> comSubstVacation(String companyId) {
			return comSubstVacationRepo.findById(companyId);
		}

		@Override
		public Optional<InterimAbsMng> interimAbsMng(String absId) {
			return interimRecAbasMngRepo.getAbsById(absId);
		}

		@Override
		public List<InterimRecAbsMng> interimRecAbsMng(String interimId, boolean isRec, DataManagementAtr mngAtr) {
			return interimRecAbasMngRepo.getRecOrAbsMng(interimId, isRec, mngAtr);
		}

		@Override
		public Optional<InterimRecMng> interimRecMng(String recId) {
			return interimRecAbasMngRepo.getReruitmentById(recId);
		}

		@Override
		public List<SubstitutionOfHDManagementData> substitutionOfHDManagementData(String cid, String sid,
				GeneralDate ymd, double unOffseDays) {
			return substitutionOfHDManaDataRepo.getByYmdUnOffset(cid, sid, ymd, unOffseDays);
		}

		@Override
		public List<PayoutManagementData> payoutManagementData(String cid, String sid, GeneralDate ymd,
				double unUse, DigestionAtr state) {
			return payoutManagementDataRepo.getByUnUseState(cid, sid, ymd, unUse, state);
		}

		@Override
		public List<InterimRemain> interimRemains(String employeeId, DatePeriod dateData, RemainType remainType) {
			return interimRemainRepo.getRemainBySidPriod(employeeId, dateData, remainType);
		}

		@Override
		public Optional<InterimDayOffMng> interimDayOffMng(String dayOffManaId) {
			return interimBreakDayOffMngRepo.getDayoffById(dayOffManaId);
		}

		@Override
		public Optional<InterimBreakMng> interimBreakMng(String breakManaId) {
			return interimBreakDayOffMngRepo.getBreakManaBybreakMngId(breakManaId);
		}

		@Override
		public List<CompensatoryDayOffManaData> compensatoryDayOffManaData(String cid, String sid,
				GeneralDate ymd) {
			return comDayOffManaDataRepo.getBySidDate(cid, sid, ymd);
		}

		@Override
		public List<InterimBreakDayOffMng> interimBreakDayOffMng(String mngId, boolean breakDay,
				DataManagementAtr mngAtr) {
			return interimBreakDayOffMngRepo.getBreakDayOffMng(mngId, breakDay, mngAtr);
		}

		@Override
		public CompanyDto firstMonth(CacheCarrier cacheCarrier, String companyId) {
			return companyAdapter.getFirstMonthRequire(cacheCarrier, companyId);
		}

		@Override
		public Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate) {
			return shareEmploymentAdapter.findEmploymentHistoryRequire(cacheCarrier, companyId, employeeId, baseDate);
		}

		@Override
		public List<SharedSidPeriodDateEmploymentImport> employmentHistory(CacheCarrier cacheCarrier,
				List<String> sids, DatePeriod datePeriod) {
			return shareEmploymentAdapter.getEmpHistBySidAndPeriodRequire(cacheCarrier, sids, datePeriod);
		}

		@Override
		public List<LeaveManagementData> leaveManagementData(String cid, String sid, GeneralDate ymd,
				DigestionAtr state) {
			return leaveManaDataRepo.getBySidYmd(cid, sid, ymd, state);
		}

		@Override
		public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
			return workingConditionItemRepo.getByHistoryId(historyId);
		}

		@Override
		public Optional<WorkingCondition> workingCondition(String companyId, String employeeId,
				GeneralDate baseDate) {
			return workingConditionRepo.getBySidAndStandardDate(companyId, employeeId, baseDate);
		}

		@Override
		public Optional<FlowWorkSetting> flowWorkSetting(String companyId, String workTimeCode) {
			return flowWorkSettingRepo.find(companyId, workTimeCode);
		}

		@Override
		public Optional<FlexWorkSetting> flexWorkSetting(String companyId, String workTimeCode) {
			return flexWorkSettingRepo.find(companyId, workTimeCode);
		}

		@Override
		public Optional<FixedWorkSetting> fixedWorkSetting(String companyId, String workTimeCode) {
			return fixedWorkSettingRepo.findByKey(companyId, workTimeCode);
		}

		@Override
		public Optional<DiffTimeWorkSetting> diffTimeWorkSetting(String companyId, String workTimeCode) {
			return diffTimeWorkSettingRepo.find(companyId, workTimeCode);
		}

		@Override
		public Optional<WorkTimeSetting> workTimeSetting(String companyId, String workTimeCode) {
			return workTimeSettingRepo.findByCode(companyId, workTimeCode);
		}

		@Override
		public CompensatoryLeaveComSetting compensatoryLeaveComSetting(String companyId) {
			return compensLeaveComSetRepo.find(companyId);
		}

		@Override
		public Optional<PredetemineTimeSetting> predetemineTimeSetting(String companyId, String workTimeCode) {
			return predetemineTimeSettingRepo.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public Optional<SpecialHoliday> specialHoliday(String companyID, int specialHolidayCD) {
			return specialHolidayRepo.findByCode(companyID, specialHolidayCD);
		}

		@Override
		public List<Integer> getSpecialHolidayNumber(String cid, int sphdSpecLeaveNo) {
			return specialHolidayRepo.findBySphdSpecLeave(cid, sphdSpecLeaveNo);
		}

		@Override
		public Optional<Closure> closure(String companyId, int closureId) {
			return closureRepo.findById(companyId, closureId);
		}

		@Override
		public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
			return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
		}

		@Override
		public Optional<WorkType> workType(String companyId, String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Integer excludeHolidayAtr(CacheCarrier cacheCarrier, String cid, String appID) {

			return remainCreateInforByApplicationData.excludeHolidayAtr(cacheCarrier, cid, appID);
		}
		@Override
		public Optional<EmpSubstVacation> empSubstVacation(String companyId, String contractTypeCode) {
			return empSubstVacationRepo.findById(companyId, contractTypeCode);
		}

		@Override
		public CompensatoryLeaveEmSetting compensatoryLeaveEmSetting(String companyId,
				String employmentCode) {
			return compensLeaveEmSetRepo.find(companyId, employmentCode);
		}

		@Override
		public List<Closure> closure(String companyId) {
			return closureRepo.findAll(companyId);
		}

		@Override
		public EmployeeImport employee(CacheCarrier cacheCarrier, String empId) {
			return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
		}
		
		@Override
		public EmployeeRecordImport employeeFullInfo(CacheCarrier cacheCarrier, String empId) {
			return empEmployeeAdapter.findByAllInforEmpId(cacheCarrier, empId);
		}
		
		@Override
		public EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId) {
			return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
		}

		@Override
		public List<SClsHistImport> employeeClassificationHistoires(CacheCarrier cacheCarrier, String companyId,
				List<String> employeeIds, DatePeriod datePeriod) {
			return empEmployeeAdapter.lstClassByEmployeeId(cacheCarrier, companyId, employeeIds, datePeriod);
		}

		@Override
		public List<AffCompanyHistSharedImport> employeeAffiliatedCompanyHistories(CacheCarrier cacheCarrier,
				List<String> sids, DatePeriod datePeriod) {
			return empEmployeeAdapter.getAffCompanyHistByEmployee(cacheCarrier, sids, datePeriod);
		}

		@Override
		public Optional<OutsideOTSetting> outsideOTSetting(String companyId) {
			return outsideOTSettingRepo.findById(companyId);
		}

		@Override
		public List<WorkdayoffFrame> workdayoffFrames(String companyId) {
			return workdayoffFrameRepo.getAllWorkdayoffFrame(companyId);
		}

		@Override
		public AnnualPaidLeaveSetting annualPaidLeaveSetting(String companyId) {
			return annualPaidLeaveSettingRepo.findByCompanyId(companyId);
		}

		@Override
		public Optional<EmptYearlyRetentionSetting> employmentYearlyRetentionSetting(String companyId,
				String employmentCode) {
			return employmentSettingRepo.find(companyId, employmentCode);
		}

		@Override
		public Optional<RetentionYearlySetting> retentionYearlySetting(String companyId) {
			return retentionYearlySettingRepo.findByCompanyId(companyId);
		}
	}

}
