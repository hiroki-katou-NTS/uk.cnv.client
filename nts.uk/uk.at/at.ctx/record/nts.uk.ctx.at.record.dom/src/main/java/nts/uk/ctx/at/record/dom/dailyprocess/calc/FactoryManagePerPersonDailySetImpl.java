package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus.MasterShareContainer;
import nts.uk.ctx.at.shared.dom.common.TimeOfDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SEmpHistoryImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionRepository;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HourlyPaymentAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkDeformedLaborAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkFlexAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkRegularAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPTimesheetRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.SpecBPTimesheetRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPayTimesheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.SpecBonusPayTimesheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.FactoryManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryRepositoly;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.DailyStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CheckDateForManageCmpLeaveService;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CheckDateForManageCmpLeaveService.Require;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
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
import nts.uk.shr.com.context.AppContexts;

/**
 * @author kazuki_watanabe
 */
@Stateless
public class FactoryManagePerPersonDailySetImpl implements FactoryManagePerPersonDailySet {

	/*????????????*/
	@Inject
	private BPSettingRepository bPSettingRepository;
	
	/*?????????????????????*/
	@Inject
	private BPTimesheetRepository bPTimesheetRepository;
	
	/* ?????????????????????????????? */
	@Inject
	private SpecBPTimesheetRepository specBPTimesheetRepository;
	
	/* ??????????????? */
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSetRepository;
	
	/* ?????????????????? */
	@Inject
	private HolidayAddtionRepository hollidayAdditonRepository;

	/* ?????????????????? */
	@Inject
	private EmployeeUnitPriceHistoryRepositoly employeeUnitPriceHistoryRepositoly;
	
	@Inject
	private RecordDomRequireService requireService;

	/** ????????????????????????????????????????????????????????? */
	@Inject
	private CheckDateForManageCmpLeaveService checkDateForManageCmpLeaveService;
	// ???????????????????????????????????????????????????????????????????????????????????????Repository
	/** ?????????????????? */
	@Inject
	private SysEmploymentHisAdapter sysEmploymentHisAdapter;
	/** ????????????????????????????????? */
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepo;
	/** ??????????????????????????? */
	@Inject
	private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private FixedWorkSettingRepository fixedWorkSet;
	
	@Inject
	private FlowWorkSettingRepository flowWorkSet;
	
	@Inject
	private FlexWorkSettingRepository flexWorkSet;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Override
	public Optional<ManagePerPersonDailySet> create(String companyId, ManagePerCompanySet companySetting, IntegrationOfDaily daily, WorkingConditionItem nowWorkingItem ) {
		return internalCreate(companyId, companySetting.getUsageSetting(), daily, nowWorkingItem,
				companySetting.getShareContainer());

	}

	private Optional<ManagePerPersonDailySet> internalCreate(String companyId, 
			Optional<UsageUnitSetting> usageSetting, 
			IntegrationOfDaily daily, WorkingConditionItem nowWorkingItem,
			MasterShareContainer<String> shareContainer) {
		try {
			val require = requireService.createRequire();
			
			/*??????????????????*/
			DailyUnit dailyUnit = DailyStatutoryLaborTime.getDailyUnit(
					require,
					new CacheCarrier(),
					companyId,
					daily.getAffiliationInfor().getEmploymentCode().toString(),
					daily.getEmployeeId(),
					daily.getYmd(),
					nowWorkingItem.getLaborSystem(),
					usageSetting);

			if(dailyUnit == null || dailyUnit.getDailyTime() == null)
				dailyUnit = new DailyUnit(new TimeOfDay(0));
			
			/*????????????*/
			AddSetting addSetting = this.getAddSetting(
					companyId,
					hollidayAdditonRepository.findByCompanyId(companyId),
					nowWorkingItem);
	
			/*??????*/
			Optional<BonusPaySettingCode> bpCode = daily.getAffiliationInfor().getBonusPaySettingCode();
			Optional<BonusPaySetting> bonusPaySetting = Optional.empty();
			if(bpCode.isPresent() && bpCode.get() != null ) {
				bonusPaySetting = this.bPSettingRepository.getBonusPaySetting(companyId, bpCode.get());
				List<BonusPayTimesheet> bonusPay = bPTimesheetRepository.getListTimesheet(companyId, bpCode.get());
				List<SpecBonusPayTimesheet> specBonusPay = specBPTimesheetRepository.getListTimesheet(companyId, bpCode.get());
				bonusPaySetting = bonusPaySetting.map(
						b -> BonusPaySetting.createFromJavaType(
								companyId,
								b.getCode().toString(),
								b.getName().toString(),
								bonusPay,
								specBonusPay));
			}
			
			/**??????????????? */
			val workType = require.workType(companyId, nowWorkingItem.getWorkCategory().getWorkType().getWeekdayTimeWTypeCode().v());
			if(!workType.isPresent()) {
				return Optional.empty();
			}
		
			/*?????????*/
			PredetermineTimeSetForCalc predetermineTimeSetByPersonWeekDay = this.getPredByPersonInfo(
					nowWorkingItem.getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode().get(), shareContainer, workType.get());
			
			/** ???????????????Require */
			OverTimeSheet.TransProcRequire overTimeSheetRequire = new TransProcRequireImpl(
					companyId,
					this.checkDateForManageCmpLeaveService,
					this.sysEmploymentHisAdapter,
					this.compensLeaveComSetRepo,
					this.compensLeaveEmSetRepo);

			/*??????????????????*/
			Optional<EmployeeUnitPriceHistoryItem> unitPrice = this.employeeUnitPriceHistoryRepositoly.get(daily.getEmployeeId(), daily.getYmd());
			
			return Optional.of(new ManagePerPersonDailySet(nowWorkingItem, dailyUnit,
								addSetting, bonusPaySetting, predetermineTimeSetByPersonWeekDay,
								overTimeSheetRequire, unitPrice));
		}
		catch(RuntimeException e) {
			return Optional.empty();
		}
	}

	/**
	 * @param map ???????????????
	 * @param workingItem ??????????????????
	 * @return ????????????
	 */
	private AddSetting getAddSetting(String companyID, Map<String, AggregateRoot> map, WorkingConditionItem workingItem) {
		
		switch(workingItem.getLaborSystem()) {
		case REGULAR_WORK:
			if(workingItem.getHourlyPaymentAtr().isHourlyPay()) {
				AggregateRoot hourlyPaymentAdditionSet = map.get("hourlyPaymentAdditionSet");
				return hourlyPaymentAdditionSet != null
						?(HourlyPaymentAdditionSet) hourlyPaymentAdditionSet
						: new HourlyPaymentAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
			}
			AggregateRoot workRegularAdditionSet = map.get("regularWork");
			return workRegularAdditionSet != null
					?(WorkRegularAdditionSet) workRegularAdditionSet
					: new WorkRegularAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
		
		case FLEX_TIME_WORK:
			AggregateRoot workFlexAdditionSet = map.get("flexWork");
			return workFlexAdditionSet != null
					?(WorkFlexAdditionSet) workFlexAdditionSet
					: new WorkFlexAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
			
		case VARIABLE_WORKING_TIME_WORK:
			AggregateRoot workDeformedLaborAdditionSet = map.get("irregularWork");
			return workDeformedLaborAdditionSet != null
					? (WorkDeformedLaborAdditionSet) workDeformedLaborAdditionSet
					: new WorkDeformedLaborAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
		
		default:
			return new WorkDeformedLaborAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
		}
	}
	
	/**
	 * ????????????????????????????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param workTimeCode
	 * @param shareContainer
	 * @return
	 */
	private PredetermineTimeSetForCalc getPredByPersonInfo(WorkTimeCode workTimeCode,
			MasterShareContainer<String> shareContainer, WorkType workType) {

		val predSetting = getPredetermineTimeSetFromShareContainer(shareContainer, AppContexts.user().companyId(),
				workTimeCode.toString());
		if (!predSetting.isPresent())
			throw new RuntimeException("predetermineedSetting is null");
		return PredetermineTimeSetForCalc.convertFromAggregatePremiumTime(predSetting.get(), workType);

	}
	
	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param shareContainer
	 * @param companyId
	 * @param workTimeCode
	 * @return
	 */
	private Optional<PredetemineTimeSetting> getPredetermineTimeSetFromShareContainer(
			MasterShareContainer<String> shareContainer, String companyId, String workTimeCode) {
		val predSet = shareContainer.getShared("PredetemineSet" + workTimeCode,
				() -> predetemineTimeSetRepository.findByWorkTimeCode(companyId, workTimeCode));
		if (predSet.isPresent()) {
			return Optional.of(predSet.get().clone());
		}
		return Optional.empty();
	}

	@Override
	public Optional<ManagePerPersonDailySet> create(String companyId, String sid, GeneralDate ymd,
			IntegrationOfDaily daily) {
		
		val require = requireService.createRequire();
		val workingItem = require.workingConditionItem(sid, ymd);
		if(!workingItem.isPresent()) {
			return Optional.empty();
		}
		val usageSetting = require.usageUnitSetting(companyId);
		MasterShareContainer<String> shareContainer = MasterShareBus.open();
		
		val personDailySet = internalCreate(companyId, usageSetting, daily, workingItem.get(), shareContainer);
		
		shareContainer.clearAll();
		
		return personDailySet;
	}
	
	/**
	 * Require??????????????????????????????????????????????????????????????????
	 * @author shuichi_ishida
	 */
	private class CheckDateRequireImpl implements CheckDateForManageCmpLeaveService.Require{
		
		/** ?????????????????? */
		private SysEmploymentHisAdapter sysEmploymentHisAdapter;
		/** ????????????????????????????????? */
		private CompensLeaveComSetRepository compensLeaveComSetRepo;
		/** ??????????????????????????? */
		private CompensLeaveEmSetRepository compensLeaveEmSetRepo;

		private final KeyDateHistoryCache<String, SEmpHistoryImport> historyCache =
				KeyDateHistoryCache.incremental((employeeId, date) ->
				this.sysEmploymentHisAdapter.findSEmpHistBySid(AppContexts.user().companyId(), employeeId, date)
				.map(h -> DateHistoryCache.Entry.of(h.getPeriod(), h)));
		
		public CheckDateRequireImpl(
				SysEmploymentHisAdapter sysEmploymentHisAdapter,
				CompensLeaveComSetRepository compensLeaveComSetRepo,
				CompensLeaveEmSetRepository compensLeaveEmSetRepo){
			
			this.sysEmploymentHisAdapter = sysEmploymentHisAdapter;
			this.compensLeaveComSetRepo = compensLeaveComSetRepo;
			this.compensLeaveEmSetRepo = compensLeaveEmSetRepo;
		}
		
		@Override
		public Optional<SEmpHistoryImport> getEmploymentHis(String employeeId, GeneralDate baseDate) {
			return this.historyCache.get(employeeId, baseDate);
		}
		
		@Override
		public Optional<CompensatoryLeaveComSetting> getCmpLeaveComSet(String companyId){
			return Optional.ofNullable(this.compensLeaveComSetRepo.find(companyId));
		}
		
		@Override
		public Optional<CompensatoryLeaveEmSetting> getCmpLeaveEmpSet(String companyId, String employmentCode){
			return Optional.ofNullable(this.compensLeaveEmSetRepo.find(companyId, employmentCode));
		}
	}

	/**
	 * Require????????????????????????.????????????Require
	 * @author shuichi_ishida
	 */
	private class TransProcRequireImpl extends CheckDateRequireImpl implements OverTimeSheet.TransProcRequire{
		
		private String cid;
		/** ????????????????????????????????????????????????????????? */
		//private CheckDateForManageCmpLeaveService checkDateForManageCmpLeaveService;
		
		public TransProcRequireImpl(
				String cid, 
				CheckDateForManageCmpLeaveService checkDateForManageCmpLeaveService,
				SysEmploymentHisAdapter sysEmploymentHisAdapter,
				CompensLeaveComSetRepository compensLeaveComSetRepo,
				CompensLeaveEmSetRepository compensLeaveEmSetRepo){
			
			super(sysEmploymentHisAdapter, compensLeaveComSetRepo, compensLeaveEmSetRepo);
			//this.checkDateForManageCmpLeaveService = checkDateForManageCmpLeaveService;
			this.cid = cid;
		}

		@Override
		public boolean checkDateForManageCmpLeave(
				Require require, String companyId, String employeeId, GeneralDate ymd) {
			return CheckDateForManageCmpLeaveService.check(require, companyId, employeeId, ymd);
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return super.compensLeaveComSetRepo.find(companyId);
		}

		@Override
		public Optional<WorkType> findByPK(String companyId, String workTypeCd) {
			return workTypeRepository.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			Optional<FixedWorkSetting> workSetting = fixedWorkSet.findByKey(cid, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			Optional<FlowWorkSetting> workSetting = flowWorkSet.find(cid, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			Optional<FlexWorkSetting> workSetting = flexWorkSet.find(cid, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}
	}
}
