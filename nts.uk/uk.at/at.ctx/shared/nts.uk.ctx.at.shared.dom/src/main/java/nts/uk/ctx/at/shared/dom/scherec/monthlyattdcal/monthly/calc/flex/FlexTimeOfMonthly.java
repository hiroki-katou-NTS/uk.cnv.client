package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.flex;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.serialize.binary.SerializableWithOptional;
import nts.uk.ctx.at.shared.dom.common.MonthlyEstimateTime;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkFlexAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationErrorInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.WorkingSystemChangeCheckService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.WorkingSystemChangeCheckService.WorkingSystemChangeState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.AggregateSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.CarryforwardSetInShortageFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.FlexAggregateMethod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.FlexMonthWorkTimeAggrSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.SettlePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.SettlePeriodOfFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.ShortageFlexSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.SettingRequiredByFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.excessoutside.ExcessOutsideWorkMng;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.excessoutside.StandardFlexTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.premiumtarget.AddedVacationUseTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.premiumtarget.getvacationaddtime.AddSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.premiumtarget.getvacationaddtime.GetAddSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.premiumtarget.getvacationaddtime.GetVacationAddTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.premiumtarget.getvacationaddtime.PremiumAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculationAndMinus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.AggregateMonthlyValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.DeductDaysAndTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyAggregateAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.AggregateTotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.Flex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.workform.flex.MonthlyAggrSetOfFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.WeeklyCalculation;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekStart;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 月別実績のフレックス時間
 * @author shuichi_ishida
 */
@Getter
public class FlexTimeOfMonthly implements SerializableWithOptional{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** フレックス時間 */
	private FlexTime flexTime;
	/** フレックス超過時間 */
	private AttendanceTimeMonth flexExcessTime;
	/** フレックス不足時間 */
	private AttendanceTimeMonth flexShortageTime;
	/** フレックス繰越時間 */
	private FlexCarryforwardTime flexCarryforwardTime;
	/** 時間外超過のフレックス時間 */
	@Setter
	private FlexTimeOfExcessOutsideTime flexTimeOfExcessOutsideTime;
	/** フレックス不足控除時間 */
	private FlexShortDeductTime flexShortDeductTime;
	/** 当月精算フレックス時間 */
	private AttendanceTimeMonthWithMinus flexSettleTime;
	
	/** フレックス時間勤務の月の集計設定 */
	private FlexMonthWorkTimeAggrSet flexAggrSet;
	/** 休暇加算時間設定 */
	private Map<String, AggregateRoot> holidayAdditionMap;
	/** 月次法定内のみ加算 */
	private boolean addMonthlyWithinStatutory;
	/** フレックス勤務の月別集計設定 */
	private Optional<MonthlyAggrSetOfFlex> monthlyAggrSetOfFlexOpt;
	/** 加算した休暇使用時間 */
	private AddedVacationUseTime addedVacationUseTime;
	/** 控除の日数と時間 */
	private DeductDaysAndTime deductDaysAndTime;
	/** 控除前の年休控除時間 */
	private AttendanceTimeMonth annualLeaveTimeBeforeDeduct;
	/** エラー情報リスト */
	private List<MonthlyAggregationErrorInfo> errorInfos;
	/** 社員の月別実績のエラー詳細リスト */
	private List<Flex> perErrors; 

	private void writeObject(ObjectOutputStream stream){
		writeObjectWithOptional(stream);
	}
	
	private void readObject(ObjectInputStream stream){
		readObjectWithOptional(stream);
	}
	
	/**
	 * コンストラクタ
	 */
	public FlexTimeOfMonthly(){
		
		this.flexTime = new FlexTime();
		this.flexExcessTime = new AttendanceTimeMonth(0);
		this.flexShortageTime = new AttendanceTimeMonth(0);
		this.flexCarryforwardTime = new FlexCarryforwardTime();
		this.flexTimeOfExcessOutsideTime = new FlexTimeOfExcessOutsideTime();
		this.flexShortDeductTime = new FlexShortDeductTime();
		this.flexSettleTime = new AttendanceTimeMonthWithMinus(0);
		
		this.flexAggrSet = null;
		this.holidayAdditionMap = new HashMap<>();
		this.addMonthlyWithinStatutory = false;
		this.monthlyAggrSetOfFlexOpt = Optional.empty();
		this.addedVacationUseTime = new AddedVacationUseTime();
		this.deductDaysAndTime = new DeductDaysAndTime(
				new AttendanceDaysMonth(0.0), new AttendanceTimeMonth(0));
		this.annualLeaveTimeBeforeDeduct = new AttendanceTimeMonth(0);
		this.errorInfos = new ArrayList<>();
		this.perErrors = new ArrayList<>();
	}

	/**
	 * ファクトリー
	 * @param flexTime フレックス時間
	 * @param flexExcessTime フレックス超過時間
	 * @param flexShortageTime フレックス不足時間
	 * @param flexCarryforwardTime フレックス繰越時間
	 * @param flexTimeOfExcessOutsideWork 時間外超過のフレックス時間
	 * @param flexShortDeductTime フレックス不足控除時間
	 * @param flexSettleTime 当月精算フレックス時間
	 * @return 月別実績のフレックス時間
	 */
	public static FlexTimeOfMonthly of(
			FlexTime flexTime,
			AttendanceTimeMonth flexExcessTime,
			AttendanceTimeMonth flexShortageTime,
			FlexCarryforwardTime flexCarryforwardTime,
			FlexTimeOfExcessOutsideTime flexTimeOfExcessOutsideWork,
			FlexShortDeductTime flexShortDeductTime,
			AttendanceTimeMonthWithMinus flexSettleTime){
		
		val domain = new FlexTimeOfMonthly();
		domain.flexTime = flexTime;
		domain.flexExcessTime = flexExcessTime;
		domain.flexShortageTime = flexShortageTime;
		domain.flexCarryforwardTime = flexCarryforwardTime;
		domain.flexTimeOfExcessOutsideTime = flexTimeOfExcessOutsideWork;
		domain.flexShortDeductTime = flexShortDeductTime;
		domain.flexSettleTime = flexSettleTime;
		return domain;
	}
	
	/**
	 * 月別実績を集計する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param datePeriod 期間
	 * @param workingSystem 労働制
	 * @param aggregateAtr 集計区分
	 * @param closureOpt 締め
	 * @param flexAggregateMethod フレックス集計方法
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 * @param excessOutsideWorkMng 時間外超過管理
	 * @param startWeekNo 開始週NO
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param monthlyCalcDailys 月の計算中の日別実績データ
	 * @return 戻り値：月別実績を集計する
	 */
	public AggregateMonthlyValue aggregateMonthly(RequireM6 require, CacheCarrier cacheCarrier, String companyId, 
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate, 
			DatePeriod datePeriod, WorkingSystem workingSystem,
			MonthlyAggregateAtr aggregateAtr, Optional<Closure> closureOpt, FlexAggregateMethod flexAggregateMethod,
			SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime,
			ExcessOutsideWorkMng excessOutsideWorkMng, int startWeekNo, MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets, MonthlyCalculatingDailys monthlyCalcDailys){
		
		this.flexAggrSet = settingsByFlex.getFlexAggrSet();
		this.monthlyAggrSetOfFlexOpt = settingsByFlex.getMonthlyAggrSetOfFlexOpt();
		this.holidayAdditionMap = settingsByFlex.getHolidayAdditionMap();
		
		List<AttendanceTimeOfWeekly> resultWeeks = new ArrayList<>();
		
		// 「月次法定内のみ加算」を確認する
		this.addMonthlyWithinStatutory = false;
		if (this.holidayAdditionMap.containsKey("flexWork")){
			val setOfFlex = (WorkFlexAdditionSet) this.holidayAdditionMap.get("flexWork");
			this.addMonthlyWithinStatutory = setOfFlex.getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet()
														.flatMap(c -> c.getIncludeVacationSet().getAdditionWithinMonthlyStatutory())
														.orElse(NotUseAtr.NOT_USE) == NotUseAtr.USE;
		}
		else {
			this.errorInfos.add(new MonthlyAggregationErrorInfo(
					"012", new ErrMessageContent(TextResource.localize("Msg_1233"))));
		}
		
		// 「月次法定内のみ加算」を確認する
		AddSet addSet = new AddSet();
		if (!this.addMonthlyWithinStatutory) { // 加算しない
			
			// 加算設定　取得　（割増用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.PREMIUM, this.holidayAdditionMap);
		} else { // 加算する
			
			// 加算設定　取得　（法定内のみ用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.ONLY_LEGAL, this.holidayAdditionMap);
		}
		addSet.getErrorInfo().ifPresent(er -> this.errorInfos.add(er));
		
		// 「集計区分」を確認する
		StandardFlexTime standFlexTime = new StandardFlexTime();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK && excessOutsideWorkMng != null){
			
			// 清算期間内の基準時間を集計する
			standFlexTime = excessOutsideWorkMng.aggrStandardTime(require, cacheCarrier, yearMonth, 
					datePeriod, this.flexAggrSet, Optional.of(aggregateTotalWorkingTime), 
					companySets.getVerticalTotalMethod().getFlexAggregateMethod());
		}
		
		// 期間．開始日を処理日にする
		GeneralDate procDate = datePeriod.start();
		int procWeekNo = startWeekNo;
		
		// 「処理中の週開始日」を期間．開始日にする
		GeneralDate procWeekStartDate = datePeriod.start();
		
		// 処理をする期間の日数分ループ
		val attendanceTimeOfDailyMap = monthlyCalcDailys.getAttendanceTimeOfDailyMap();
		val workInformationOfDailyMap = monthlyCalcDailys.getWorkInfoOfDailyMap();
		while (procDate.beforeOrEquals(datePeriod.end())){
			
			if (attendanceTimeOfDailyMap.containsKey(procDate)){
				val attendanceTimeOfDaily = attendanceTimeOfDailyMap.get(procDate);
			
				// 処理日の職場コードを取得する
				String workplaceId = "empty";
				val affWorkplaceOpt = employeeSets.getWorkplace(procDate);
				if (affWorkplaceOpt.isPresent()){
					workplaceId = affWorkplaceOpt.get().getWorkplaceId();
				}
				
				// 処理日の雇用コードを取得する
				String employmentCd = "empty";
				val employmentOpt = employeeSets.getEmployment(procDate);
				if (employmentOpt.isPresent()){
					employmentCd = employmentOpt.get().getEmploymentCode();
				}
			
				ConcurrentStopwatches.start("12222.3:日別実績の集計：");
				
				// 処理日の勤務情報を取得する
				if (workInformationOfDailyMap.containsKey(procDate)) {
					val workInfo = workInformationOfDailyMap.get(procDate).getRecordInfo();
					
					// 日別実績を集計する　（フレックス時間勤務用）
					val flexTimeDaily = aggregateTotalWorkingTime.aggregateDailyForFlex(require, procDate, attendanceTimeOfDaily,
							companyId, workplaceId, employmentCd, workingSystem, aggregateAtr, workInfo,
							settingsByFlex, companySets);
					
					// フレックス時間への集計結果を取得する
					for (val timeSeriesWork : flexTimeDaily.getTimeSeriesWorks().values()){
						this.flexTime.getTimeSeriesWorks().put(timeSeriesWork.getYmd(), timeSeriesWork);
					}
				}

				ConcurrentStopwatches.stop("12222.3:日別実績の集計：");
				
				// フレックス時間を集計する
				this.flexTime.aggregate(procDate, attendanceTimeOfDaily);
			}

			// 週の集計をする日か確認する
			if (MonthlyCalculation.isAggregateWeek(procDate, WeekStart.TighteningStartDate, datePeriod, closureOpt)){
			
				// 週の期間を計算
				DatePeriod weekAggrPeriod = new DatePeriod(procWeekStartDate, procDate);
				
				// 翌週の開始日を設定しておく
				procWeekStartDate = procDate.addDays(1);
				
				// 対象の「週別実績の勤怠時間」を作成する
				val newWeek = new AttendanceTimeOfWeekly(employeeId, yearMonth, closureId, closureDate,
						procWeekNo, weekAggrPeriod);
				procWeekNo += 1;
				
				// 週別実績を集計する
				{
					ConcurrentStopwatches.start("12222.4:週別実績の集計：");
					
					// 週の計算
					val weekCalc = newWeek.getWeeklyCalculation();
					weekCalc.aggregate(require, companyId, employeeId, yearMonth, weekAggrPeriod,
							datePeriod, workingSystem, aggregateAtr,
							null, null, aggregateTotalWorkingTime,
							WeekStart.TighteningStartDate, new AttendanceTimeMonth(0),
							attendanceTimeOfDailyMap, companySets);
					resultWeeks.add(newWeek);
					if (weekCalc.getErrorInfos().size() > 0) this.errorInfos.addAll(weekCalc.getErrorInfos());

					ConcurrentStopwatches.stop("12222.4:週別実績の集計：");
					
					// 集計区分を確認する
					if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK && excessOutsideWorkMng != null){
					
						ConcurrentStopwatches.start("12222.5:時間外超過の集計：");
						
						// 時間外超過の集計
						newWeek.getExcessOutside().aggregate(weekCalc, companySets);
						
						ConcurrentStopwatches.stop("12222.5:時間外超過の集計：");
					}
				}
			}
			
			/** ○「集計区分」を確認する */
			if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK && excessOutsideWorkMng != null){

				ConcurrentStopwatches.start("12222.6:超過時間割り当て：");
				
				// 時間外超過の時、フレックス超過時間を割り当てる
				excessOutsideWorkMng.assignFlexExcessTime(require, datePeriod, flexAggregateMethod,
						procDate, this.flexAggrSet, aggregateTotalWorkingTime, this.flexTime,
						settingsByFlex, addSet, standFlexTime);
				
				ConcurrentStopwatches.stop("12222.6:超過時間割り当て：");
			}
			
			procDate = procDate.addDays(1);
		}
		
		return AggregateMonthlyValue.of(aggregateTotalWorkingTime, excessOutsideWorkMng, resultWeeks);
	}
	
	/**
	 * 月単位の時間を集計する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param closureId 締めID
	 * @param datePeriod 期間
	 * @param aggregateAtr 集計区分
	 * @param flexAggregateMethod フレックス集計方法
	 * @param workingConditionItem 労働条件項目
	 * @param workplaceId 職場ID
	 * @param employmentCd 雇用コード
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	public void aggregateMonthlyHours(RequireM5 require, CacheCarrier cacheCarrier, String companyId, String employeeId,
			YearMonth yearMonth, ClosureId closureId, DatePeriod datePeriod, MonthlyAggregateAtr aggregateAtr,
			FlexAggregateMethod flexAggregateMethod, WorkingConditionItem workingConditionItem,
			String workplaceId, String employmentCd, MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex, 
			AggregateTotalWorkingTime aggregateTotalWorkingTime, ClosureDate closureDate,
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){

		this.flexAggrSet = settingsByFlex.getFlexAggrSet();
		this.holidayAdditionMap = settingsByFlex.getHolidayAdditionMap();
		this.monthlyAggrSetOfFlexOpt = settingsByFlex.getMonthlyAggrSetOfFlexOpt();
		
		// 「月次法定内のみ加算」を確認する
		this.addMonthlyWithinStatutory = false;
		if (this.holidayAdditionMap.containsKey("flexWork")){
			val setOfFlex = (WorkFlexAdditionSet)this.holidayAdditionMap.get("flexWork");
			this.addMonthlyWithinStatutory = setOfFlex.getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet()
					.flatMap(c -> c.getIncludeVacationSet().getAdditionWithinMonthlyStatutory())
					.orElse(NotUseAtr.NOT_USE) == NotUseAtr.USE;
		} else {
			this.errorInfos.add(new MonthlyAggregationErrorInfo("012", new ErrMessageContent(TextResource.localize("Msg_1233"))));
			return;
		}

		// 「不足設定．清算期間」を確認する
		if (this.flexAggrSet.getInsufficSet().getSettlePeriod() == SettlePeriod.SINGLE_MONTH){
			
			// 前月からのフレ不足を繰越
			this.carryforwardFlex(require, employeeId, yearMonth);
		} else{

			// 前月からのフレを繰越（複数月）
			this.carryforwardFlexForMulti(require, cacheCarrier, employeeId, datePeriod, yearMonth, aggregateAtr);
		}
		
		/** ○「集計方法」を確認する */
		if (flexAggregateMethod == FlexAggregateMethod.FOR_CONVENIENCE){
			// 便宜上集計をする
			this.aggregateForConvenience(require, yearMonth, closureId, datePeriod, aggregateAtr, 
					settingsByFlex, aggregateTotalWorkingTime, dailyAttendanceTime);
			
		} else if (flexAggregateMethod == FlexAggregateMethod.PRINCIPLE){
			// 原則集計をする
			this.aggregatePrinciple(require, cacheCarrier, companyId, employeeId, yearMonth, 
									datePeriod, workplaceId, employmentCd, aggregateAtr,
									employeeSets, settingsByFlex, aggregateTotalWorkingTime, 
									closureId, closureDate, dailyAttendanceTime);
		}
		
		// 「不足設定．清算期間」を確認する
		if (this.flexAggrSet.getInsufficSet().getSettlePeriod() == SettlePeriod.SINGLE_MONTH){
			
			// フレックス繰越不可時間を求める
			this.askNotCarryforwardTime(require, cacheCarrier, companyId, employeeId, yearMonth, datePeriod, closureId,
					companySets, employeeSets, settingsByFlex);
		}
		else{
			
			// 清算処理をする
			this.settleProc(require, cacheCarrier, companyId, employeeId, yearMonth, datePeriod, closureId,
					closureDate, aggregateAtr, employeeSets, settingsByFlex, aggregateTotalWorkingTime, dailyAttendanceTime);
		}
		
		// 大塚モードの判断
		if (!AppContexts.optionLicense().customize().ootsuka()) 
			return;
		
		// 大塚カスタマイズ（フレ永続繰越）
		{
			// フレックス繰越不足時間が計算されているか確認
			int carryforwardShortageMinutes = this.flexCarryforwardTime.getFlexCarryforwardShortageTime().v();
			if (carryforwardShortageMinutes > 0){
				
				// フレ不足時間←フレ繰越不足時間を加算
				this.flexShortageTime = this.flexShortageTime.addMinutes(carryforwardShortageMinutes);
				
				// フレックス時間←フレ繰越不足時間を減算
				this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(-carryforwardShortageMinutes, 0));
				
				// フレ繰越不足時間←0:00
				this.flexCarryforwardTime.setFlexCarryforwardShortageTime(new AttendanceTimeMonth(0));
			}
		}
		
		// 年休控除と欠勤控除のパラメータを確認する　（実際は、計算の早い段階（前月データ確認のタイミング）で確認している）
		this.deductDaysAndTime = new DeductDaysAndTime(
				this.flexShortDeductTime.getAnnualLeaveDeductDays(),
				this.flexShortDeductTime.getAbsenceDeductTime());
		
		// 年休控除する
		this.deductAnnualLeave(require, companyId, employeeId, datePeriod, flexAggregateMethod, workingConditionItem);
		
		// 欠勤控除する
		this.deductAbsence();
		
		// フレックス補填のエラーチェック
		this.checkErrorForInsufficientFlex(settingsByFlex);
	}
	
	/**
	 * 前月からのフレ不足を繰越
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	private void carryforwardFlex(RequireM3 require, String employeeId, YearMonth yearMonth){
		
		// フレックス不足時の繰越設定を確認する　→　翌月繰越の時
		if (this.flexAggrSet.getInsufficSet().getCarryforwardSet() == CarryforwardSetInShortageFlex.NEXT_MONTH_CARRYFORWARD){
	
			// 前月の「月別実績の勤怠時間」を取得する
			val prevYearMonth = yearMonth.previousMonth();
			val prevAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, prevYearMonth);
			
			// 前月のフレックス不足時間を取得する　（開始日が最も大きい日のデータ）
			// 前月のフレックス繰越不可時間を取得する
			AttendanceTimeMonth prevFlexShortageTime = new AttendanceTimeMonth(0);
			AttendanceTimeMonth prevFlexNotCarryforwardTime = new AttendanceTimeMonth(0);
			if (!prevAttendanceTimeList.isEmpty()){
				val prevAttendanceTime = prevAttendanceTimeList.get(prevAttendanceTimeList.size() - 1);
				val prevFlexTime = prevAttendanceTime.getMonthlyCalculation().getFlexTime();
				prevFlexShortageTime = new AttendanceTimeMonth(prevFlexTime.getFlexShortageTime().v());
				prevFlexNotCarryforwardTime = new AttendanceTimeMonth(
						prevFlexTime.getFlexCarryforwardTime().getFlexNotCarryforwardTime().v());
			}
			
			// 当月のフレックス繰越時間を計算する　（前月のフレックス不足時間－前月のフレックス繰越不可時間）
			int prevShortageMinutes = prevFlexShortageTime.v() - prevFlexNotCarryforwardTime.v();
			if (prevShortageMinutes < 0) prevShortageMinutes = 0;
			// ※　マイナス値に変えて入れる
			this.flexCarryforwardTime.setFlexCarryforwardTime(
					new AttendanceTimeMonthWithMinus(-prevShortageMinutes));
		}
	}
	
	/**
	 * 前月からのフレを繰越（複数月）
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param aggregateAtr 集計区分
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	private void carryforwardFlexForMulti(RequireM3 require, CacheCarrier cacheCarrier, String employeeId, DatePeriod period,
			YearMonth yearMonth, MonthlyAggregateAtr aggregateAtr){
		
		// フレックス清算期間の取得
		SettlePeriodOfFlex settlePeriod = this.flexAggrSet.getInsufficSet().getSettlePeriod(require, cacheCarrier, employeeId, period, yearMonth);
		
		// 「開始月かどうか」を確認する
		if (settlePeriod.getIsStartYm()){

			// 当月のフレックス繰越時間　←　０
			this.flexCarryforwardTime.setFlexCarryforwardTime(new AttendanceTimeMonthWithMinus(0));
		}
		else{
			
			// 前月の「月別実績の勤怠時間」を取得する
			val prevYearMonth = yearMonth.previousMonth();

			val prevAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, prevYearMonth);

			// 計算繰越時間を計算する
			int calcCarryforwardMinutes = 0;		// 計算繰越時間
			if (!prevAttendanceTimeList.isEmpty()){
				val prevAttendanceTime = prevAttendanceTimeList.get(prevAttendanceTimeList.size() - 1);
				val prevFlexTime = prevAttendanceTime.getMonthlyCalculation().getFlexTime();
				FlexTimeCurrentMonth prevCurrentMonth = prevFlexTime.getFlexTime().getFlexTimeCurrentMonth();
				if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
					// 時間外超過の時、時間外超過のフレックス時間．当月フレックス時間を利用する
					prevCurrentMonth = prevFlexTime.getFlexTimeOfExcessOutsideTime().getFlexTimeCurrentMonth();
				}
				// 前月のフレックス繰越時間＋前月の当月フレックス時間．フレックス時間－前月の当月清算フレックス時間
				calcCarryforwardMinutes += prevFlexTime.getFlexCarryforwardTime().getFlexCarryforwardTime().v();
				calcCarryforwardMinutes += prevCurrentMonth.getFlexTime().v();
				calcCarryforwardMinutes -= prevFlexTime.getFlexSettleTime().v();
			}
			
			// 当月のフレックス繰越時間に、前月のフレックス時間を反映する
			this.flexCarryforwardTime.setFlexCarryforwardTime(
					new AttendanceTimeMonthWithMinus(calcCarryforwardMinutes));
		}
	}
	
	/**
	 * 便宜上集計をする
	 * @param datePeriod 期間
	 * @param aggregateAtr 集計区分
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 */
	private void aggregateForConvenience(RequireM7 require, YearMonth yearMonth, ClosureId closureId, DatePeriod datePeriod, 
			MonthlyAggregateAtr aggregateAtr, SettingRequiredByFlex settingsByFlex, 
			AggregateTotalWorkingTime aggregateTotalWorkingTime, Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime) {
		
		// 「不足設定．清算期間」を確認する
		if (this.flexAggrSet.getInsufficSet().getSettlePeriod() == SettlePeriod.SINGLE_MONTH){
			// フレックス時間の計算（便宜上集計）
			
			// フレックス時間を取得する　→　繰越時間相殺前に入れる
			val carryforwardTimeBeforeOffset = this.flexTime.getTimeSeriesTotalFlexTime(datePeriod, false);
			if (carryforwardTimeBeforeOffset.greaterThan(0)){
				
				// フレックス超過の処理をする　（便宜上）
				this.flexExcessForConvenience(carryforwardTimeBeforeOffset);
			}
			else {
				
				// フレックス不足の処理をする　（便宜上）
				this.flexShortageForConvenience(carryforwardTimeBeforeOffset);
			}
		}
		else{
			// 複数月フレックス時間の計算（便宜上集計）
			this.calcFlexMultiForConvenience(require, yearMonth, closureId, datePeriod, aggregateAtr, 
					settingsByFlex, aggregateTotalWorkingTime, dailyAttendanceTime);
		}
		
		// 計算フレックス時間の計算（便宜上集計）
		{
			// 計算フレックス時間を取得する　→　繰越時間相殺前
			val totalCalcFlexTime = this.flexTime.getTimeSeriesTotalCalcFlexTime(datePeriod, false);
			if (totalCalcFlexTime.greaterThan(0)){
				
				// 計算フレックス超過の処理をする　（便宜上）
				this.calcFlexExcessForConvenience(totalCalcFlexTime);
			}
			else {
				
				// 計算フレックス不足の処理をする　（便宜上）
				this.calcFlexShortageForConvenience(totalCalcFlexTime);
			}
		}
	}

	/**
	 * フレックス超過の処理をする　（便宜上）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 */
	private void flexExcessForConvenience(AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset){

		// フレックス繰越時間を取得する
		AttendanceTimeMonthWithMinus carryforwardTime = this.flexCarryforwardTime.getFlexCarryforwardTime();
		// （不足分をプラスにして取り出す）
		if (carryforwardTime.lessThan(0)){
			carryforwardTime = new AttendanceTimeMonthWithMinus(-carryforwardTime.v());
		}
		else{
			carryforwardTime = new AttendanceTimeMonthWithMinus(0);
		}
		val carryforwardWorkTime = this.flexCarryforwardTime.getFlexCarryforwardWorkTime();
		val carryforwardShortageTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		
		if (carryforwardTime.lessThan(carryforwardTimeBeforeOffset.v())){
			
			// フレックス繰越時間をフレックス繰越勤務時間に加算する
			this.flexCarryforwardTime.setFlexCarryforwardWorkTime(
					carryforwardWorkTime.addMinutes(carryforwardTime.v()));
			
			// 繰越時間相殺前とフレックス繰越時間の差分をフレックス超過時間・フレックス時間に加算する
			val difference = carryforwardTimeBeforeOffset.minusMinutes(carryforwardTime.v());
			this.flexExcessTime = this.flexExcessTime.addMinutes(difference.v());
			this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(difference.v(), 0));
			
			// 法定外フレックス時間にフレックス時間をセットする
			this.flexTime.setIllegalFlexTime(this.flexTime.getFlexTime().getTime());
			this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(0));
		}
		else {
			
			// 繰越時間相殺前をフレックス繰越勤務時間に加算する
			this.flexCarryforwardTime.setFlexCarryforwardWorkTime(
					carryforwardWorkTime.addMinutes(carryforwardTimeBeforeOffset.v()));
			
			// フレックス繰越時間と繰越時間相殺前の差分をフレックス繰越不足時間に加算する
			val difference = carryforwardTime.minusMinutes(carryforwardTimeBeforeOffset.v());
			this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
					carryforwardShortageTime.addMinutes(difference.v()));
			
			// フレックス時間を 0 にする
			this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
					new AttendanceTimeMonthWithMinus(0),
					this.flexTime.getFlexTime().getCalcTime()));
		}
	}
	
	/**
	 * フレックス不足の処理をする　（便宜上）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 */
	private void flexShortageForConvenience(AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset){
		
		// フレックス繰越時間を取得する
		AttendanceTimeMonthWithMinus carryforwardTime = this.flexCarryforwardTime.getFlexCarryforwardTime();
		// （不足分をプラスにして取り出す）
		if (carryforwardTime.lessThan(0)){
			carryforwardTime = new AttendanceTimeMonthWithMinus(-carryforwardTime.v());
		}
		else{
			carryforwardTime = new AttendanceTimeMonthWithMinus(0);
		}
		val carryforwardShortageTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		
		// フレックス繰越時間をフレックス繰越不足時間に加算する
		this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
				carryforwardShortageTime.addMinutes(carryforwardTime.v()));
		
		// 繰越時間相殺前をフレックス不足時間に加算する
		// ※　繰越時間相殺前はマイナス値なので、減算により、加算扱いにする。
		this.flexShortageTime = this.flexShortageTime.minusMinutes(carryforwardTimeBeforeOffset.v());
		
		// 繰越時間相殺前をフレックス時間に加算する
		this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(
				carryforwardTimeBeforeOffset.v(), 0));
	}

	/**
	 * 複数月フレックス時間の計算（便宜上集計）
	 * @param period 期間
	 * @param aggregateAtr 集計区分
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 */
	private void calcFlexMultiForConvenience(RequireM7 require, YearMonth yearMonth, ClosureId closureId,
			DatePeriod period, MonthlyAggregateAtr aggregateAtr, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime) {
		
		// フレックス時間合計を取得する
		AttendanceTimeMonthWithMinus totalFlexTime = this.flexTime.getTimeSeriesTotalFlexTime(period, false);
		
		// フレックス対象時間を集計する
		AttendanceTimeMonthWithMinus flexTargetTime = this.aggregateFlexTargetTime(period, aggregateTotalWorkingTime, PremiumAtr.PREMIUM);
		
		// 週平均超過時間を計算する
		this.calcExcessWeekAveTime(require, yearMonth, period, closureId, aggregateAtr, flexTargetTime, settingsByFlex);
		
		// 当月フレックス時間を計算する　（フレックス時間合計－週平均超過時間）
		FlexTimeCurrentMonth flexTimeCurrentMonth = this.flexTime.getFlexTimeCurrentMonth();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
			// 時間外超過の時、時間外超過のフレックス時間を利用する
			flexTimeCurrentMonth = this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth();
		}
		flexTimeCurrentMonth.setFlexTime(new AttendanceTimeMonthWithMinus(
				totalFlexTime.v() - flexTimeCurrentMonth.getExcessWeekAveTime().v()));
		
		val prescribed = settingsByFlex.getPrescribedWorkingTimeMonth(require, yearMonth, period, closureId,
				Optional.ofNullable(aggregateTotalWorkingTime.getVacationUseTime().getCompensatoryLeave()),
				dailyAttendanceTime);
		flexTimeCurrentMonth.setStandardTime(new AttendanceTimeMonth(prescribed.v()));
	}
	
	/**
	 * 計算フレックス超過の処理をする　（便宜上）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 */
	private void calcFlexExcessForConvenience(AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset){

		// フレックス繰越時間を取得する
		AttendanceTimeMonthWithMinus carryforwardTime = this.flexCarryforwardTime.getFlexCarryforwardTime();
		// （不足分をプラスにして取り出す）
		if (carryforwardTime.lessThan(0)){
			carryforwardTime = new AttendanceTimeMonthWithMinus(-carryforwardTime.v());
		}
		else{
			carryforwardTime = new AttendanceTimeMonthWithMinus(0);
		}
		
		if (carryforwardTime.lessThan(carryforwardTimeBeforeOffset.v())){
			
			// 繰越時間相殺前とフレックス繰越時間の差分を計算フレックス時間に加算する
			int difference = carryforwardTimeBeforeOffset.v() - carryforwardTime.v();
			this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(0, difference));
		}
		else {
			
			// 計算フレックス時間を 0 にする
			this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
					this.flexTime.getFlexTime().getTime(),
					new AttendanceTimeMonthWithMinus(0)));
		}
	}
	
	/**
	 * 計算フレックス不足の処理をする　（便宜上）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 */
	private void calcFlexShortageForConvenience(AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset){
		
		// 繰越時間相殺前を計算フレックス時間に加算する
		this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(
				0, carryforwardTimeBeforeOffset.v()));
	}
	
	/**
	 * 原則集計をする
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param datePeriod 期間
	 * @param workplaceId 職場ID
	 * @param employmentCd 雇用コード
	 * @param aggregateAtr 集計区分
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間
	 */
	private void aggregatePrinciple(RequireM4 require, CacheCarrier cacheCarrier, String companyId, String employeeId,
			YearMonth yearMonth, DatePeriod datePeriod, String workplaceId, String employmentCd, MonthlyAggregateAtr aggregateAtr,
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime,
			ClosureId closureId, ClosureDate closureDate, Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime) {

		/** 法定労働時間を取得する */
		val compensatoryLeaveAfterDudection = getPrescribedTime(require, yearMonth, datePeriod, settingsByFlex, 
				aggregateTotalWorkingTime, closureId, dailyAttendanceTime);
		
		// 「不足設定．清算期間」を確認する
		if (this.flexAggrSet.getInsufficSet().getSettlePeriod() == SettlePeriod.SINGLE_MONTH)
		{
			// フレックス時間の計算（原則集計）
			// フレックス対象時間を集計する
			val flexTargetTime = this.aggregateFlexTargetTime(datePeriod, aggregateTotalWorkingTime, PremiumAtr.PREMIUM);

			// 繰越時間相殺前を求める
			val carryforwardTimeBeforeOffset = flexTargetTime.minusMinutes(compensatoryLeaveAfterDudection.valueAsMinutes());
			
			// 所定労働時間（代休控除後）を基準時間に入れる
			int standMinutes = compensatoryLeaveAfterDudection.valueAsMinutes();
			if (standMinutes < 0) standMinutes = 0;
			if (aggregateAtr == MonthlyAggregateAtr.MONTHLY){
				this.flexTime.getFlexTimeCurrentMonth().setStandardTime(
						new AttendanceTimeMonth(standMinutes));
			} else{
				// 時間外超過の時、時間外超過のフレックス時間側に入れる
				this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth().setStandardTime(
						new AttendanceTimeMonth(standMinutes));
			}
			
			if (carryforwardTimeBeforeOffset.greaterThan(0)){
				
				// フレックス超過の処理をする
				this.flexExcessPrinciple(require, cacheCarrier, carryforwardTimeBeforeOffset, compensatoryLeaveAfterDudection,
						companyId, employeeId, yearMonth, datePeriod, aggregateAtr, employeeSets, settingsByFlex,
						aggregateTotalWorkingTime, closureId, closureDate, dailyAttendanceTime);
			}
			else {
				
				// 加算設定　取得　（不足時計算用）
				val addSetForShortage = GetAddSet.get(
						WorkingSystem.FLEX_TIME_WORK, PremiumAtr.WHEN_SHORTAGE, this.holidayAdditionMap);
				if (addSetForShortage.getErrorInfo().isPresent()){
					this.errorInfos.add(addSetForShortage.getErrorInfo().get());
				}
				
				// フレックス不足の処理をする
				val addedTimeForShortage = this.flexShortagePrinciple(
						datePeriod, carryforwardTimeBeforeOffset, aggregateTotalWorkingTime, addSetForShortage);
				this.addedVacationUseTime.addMinutesToAddTimePerMonth(addedTimeForShortage.getAddTimePerMonth().v());
			}
		} else {
			// 複数月フレックス時間の計算（原則集計）
			this.calcFlexMultiForPrinciple(require, companyId, employeeId, yearMonth, datePeriod, closureId, aggregateAtr,
					settingsByFlex, aggregateTotalWorkingTime, dailyAttendanceTime, compensatoryLeaveAfterDudection);
		}
		
		// 計算フレックス時間の計算
		{
			// 計算フレックス対象時間を求める
			int calcflexTargetMinutes = this.aggregateCalcFlexTargetTime(datePeriod, aggregateTotalWorkingTime).v();
			
			// 所定労働時間（代休控除後）を求める
//			int compensatoryLeaveAfterDudection = this.askCompensatoryLeaveAfterDeduction(companyId, employeeId, yearMonth, datePeriod,
//							aggregateTotalWorkingTime, prescribedWorkingTimeMonth).v();

			// 繰越時間相殺前を求める
			int carryforwardTimeBeforeOffset = calcflexTargetMinutes - compensatoryLeaveAfterDudection.valueAsMinutes();
			
			if (carryforwardTimeBeforeOffset > 0){
				
				// フレックス超過の処理をする
				this.calcFlexExcessPrinciple(new AttendanceTimeMonthWithMinus(carryforwardTimeBeforeOffset));
			}
			else {
				
				// フレックス不足の処理をする
				this.calcFlexShortagePrinciple(datePeriod,
						new AttendanceTimeMonthWithMinus(carryforwardTimeBeforeOffset), aggregateTotalWorkingTime);
			}
		}
	}

	/** 所定労働時間を求める */
	private AttendanceTimeMonthWithMinus getPrescribedTime(RequireM4 require, 
			YearMonth yearMonth, DatePeriod datePeriod, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime, ClosureId closureId,
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime) {
		
		AttendanceTimeMonth prescribedWorkingTimeMonth;
		val compensatoryLeave = aggregateTotalWorkingTime.getVacationUseTime().getCompensatoryLeave();
		if (settingsByFlex.getComFlexSetOpt().map(c -> c.isWithinTimeUsageAttr()).orElseThrow(() -> new BusinessException("Msg_2242"))) {

			// 所定労働時間（代休控除後）を求める
			prescribedWorkingTimeMonth = settingsByFlex.getPrescribedWorkingTimeMonth(require, yearMonth, 
					datePeriod, closureId, Optional.ofNullable(compensatoryLeave),
					dailyAttendanceTime);
			
			return new AttendanceTimeMonthWithMinus(prescribedWorkingTimeMonth.valueAsMinutes());
		}
		
		/** 所定労働時間を集計する */
		prescribedWorkingTimeMonth =  aggregateTotalWorkingTime.getPrescribedWorkingTime().getTotalSchedulePrescribedWorkingTime(datePeriod);
		
		/** 代休時間を控除する */
		val compen =  settingsByFlex.getFlexAggregateMethodMonthly().getCompensatoryTimeSet().getSubtractedTime(
				compensatoryLeave, dailyAttendanceTime, datePeriod, flexAggrSet, 
				new MonthlyEstimateTime(prescribedWorkingTimeMonth.v()));
				
		return new AttendanceTimeMonthWithMinus(compen.valueAsMinutes());
	}

	/**
	 * 複数月フレックス時間の計算（原則集計）
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param aggregateAtr 集計区分
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 */
	private void calcFlexMultiForPrinciple(RequireM4 require, String companyId, String employeeId,
			YearMonth yearMonth, DatePeriod period, ClosureId closureId,
			MonthlyAggregateAtr aggregateAtr, SettingRequiredByFlex settingsByFlex, 
			AggregateTotalWorkingTime aggregateTotalWorkingTime, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime,
			AttendanceTimeMonthWithMinus prescribed) {
		
		// フレックス対象時間を集計する
		AttendanceTimeMonthWithMinus flexTargetTime = this.aggregateFlexTargetTime(period, aggregateTotalWorkingTime, PremiumAtr.PREMIUM);
		
		// 週平均超過時間を計算する
		this.calcExcessWeekAveTime(require, yearMonth, period, closureId, aggregateAtr, flexTargetTime, settingsByFlex);
		
		// 当月フレックス時間を計算する　（フレックス時間合計－所定労働時間－週平均超過時間）
		FlexTimeCurrentMonth flexTimeCurrentMonth = this.flexTime.getFlexTimeCurrentMonth();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
			// 時間外超過の時、時間外超過のフレックス時間を利用する
			flexTimeCurrentMonth = this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth();
		}
		int excessWeekAveMinutes = flexTimeCurrentMonth.getExcessWeekAveTime().v();
		flexTimeCurrentMonth.setFlexTime(new AttendanceTimeMonthWithMinus(
				flexTargetTime.v() - prescribed.valueAsMinutes() - excessWeekAveMinutes));
		flexTimeCurrentMonth.setStandardTime(new AttendanceTimeMonth(prescribed.valueAsMinutes()));
	}
	
	/**
	 * フレックス対象時間を集計する
	 * @param datePeriod 期間
	 * @param aggregateTotalWorkingTime 総労働時間
	 * @return フレックス対象時間
	 */
	private AttendanceTimeMonthWithMinus aggregateFlexTargetTime(DatePeriod datePeriod,
			AggregateTotalWorkingTime aggregateTotalWorkingTime, PremiumAtr premiumAtr){
		
		AttendanceTimeMonthWithMinus flexTargetTime = new AttendanceTimeMonthWithMinus(0);
		
		// 「月次法定内のみ加算」を確認する
		AddSet addSet = new AddSet();
		if (!this.addMonthlyWithinStatutory){
			// 加算しない
			
			// 加算設定　取得　（割増用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, premiumAtr, this.holidayAdditionMap);
			if (addSet.getErrorInfo().isPresent()){
				this.errorInfos.add(addSet.getErrorInfo().get());
			}
		}
		else {
			// 加算する
			
			// 加算設定　取得　（法定内のみ用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.ONLY_LEGAL, this.holidayAdditionMap);
			if (addSet.getErrorInfo().isPresent()){
				this.errorInfos.add(addSet.getErrorInfo().get());
			}
		}
		
		// 集計対象時間を取得する
		val totalLegalTime = aggregateTotalWorkingTime.getWorkTime().getAggregateTargetTime(datePeriod, addSet);
		
		// フレックス対象時間に集計対象時間を加算する
		flexTargetTime = flexTargetTime.addMinutes(totalLegalTime.v());
				
		// 合計フレックス時間を取得する
		val totalFlexTime = this.flexTime.getTimeSeriesTotalFlexTime(datePeriod, true);
		
		// フレックス対象時間にフレックス時間を加算する
		flexTargetTime = flexTargetTime.addMinutes(totalFlexTime.v());
		
		// 「月次法定内のみ加算」を確認する
		AttendanceTimeMonth vacationAddTime = new AttendanceTimeMonth(0);
		if (!this.addMonthlyWithinStatutory){
			// 加算しない
			
			// 休暇加算時間を取得する
			vacationAddTime = GetVacationAddTime.getTime(
					datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSet);
		}
		else {
			// 加算する
			
			// 休暇加算時間を取得する
			vacationAddTime = GetVacationAddTime.getTime(
					datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSet);
		}

		// フレックス対象時間に休暇加算時間を加算する
		flexTargetTime = flexTargetTime.addMinutes(vacationAddTime.v());

		// 「休暇加算時間」を「加算した休暇使用時間」に退避しておく
		this.addedVacationUseTime.addMinutesToAddTimePerMonth(vacationAddTime.v());
		
		return flexTargetTime;
	}

	/**
	 * 週平均超過時間を計算する
	 * @param aggregateAtr 集計区分
	 * @param flexTargetTime フレックス対象時間
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 */
	private void calcExcessWeekAveTime(RequireM7 require, YearMonth yearMonth, DatePeriod datePeriod, ClosureId closureId, 
			MonthlyAggregateAtr aggregateAtr, AttendanceTimeMonthWithMinus flexTargetTime, SettingRequiredByFlex settingsByFlex) {
		
		int excessWeekAveMinutes = 0;	// 週平均超過時間（分）

		// 週平均基準時間（週平均時間）を取得する
		int weekAveMinutes = settingsByFlex.getWeekAverageTime(require, yearMonth, datePeriod, closureId).valueAsMinutes();
		if (weekAveMinutes > 0){
			if (flexTargetTime.greaterThan(weekAveMinutes)){
				
				// 週平均超過時間を計算する　（フレックス対象時間－週平均基準時間）
				excessWeekAveMinutes = flexTargetTime.v() - weekAveMinutes;
			}
		}
		FlexTimeCurrentMonth flexTimeCurrentMonth = this.flexTime.getFlexTimeCurrentMonth();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
			// 時間外超過の時、時間外超過のフレックス時間を利用する
			flexTimeCurrentMonth = this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth();
		}
		flexTimeCurrentMonth.setExcessWeekAveTime(new AttendanceTimeMonth(excessWeekAveMinutes));
	}
	
	public static interface RequireM7 extends SettingRequiredByFlex.Require {}

	/**
	 * 計算フレックス対象時間を求める
	 * @param datePeriod 期間
	 * @param aggregateTotalWorkingTime 総労働時間
	 * @return フレックス対象時間
	 */
	private AttendanceTimeMonthWithMinus aggregateCalcFlexTargetTime(DatePeriod datePeriod,
			AggregateTotalWorkingTime aggregateTotalWorkingTime){
		
		int calcFlexTargetMinutes = 0;

		// 「月次法定内のみ加算」を確認する
		AddSet addSet = new AddSet();
		if (!this.addMonthlyWithinStatutory){
			// 加算しない
			
			// 加算設定　取得　（割増用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.PREMIUM, this.holidayAdditionMap);
			if (addSet.getErrorInfo().isPresent()){
				this.errorInfos.add(addSet.getErrorInfo().get());
			}
		}
		else {
			// 加算する
			
			// 加算設定　取得　（法定内のみ用）
			addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.ONLY_LEGAL, this.holidayAdditionMap);
			if (addSet.getErrorInfo().isPresent()){
				this.errorInfos.add(addSet.getErrorInfo().get());
			}
		}
		
		// 集計対象時間を取得する
		val totalLegalTime = aggregateTotalWorkingTime.getWorkTime().getAggregateTargetTime(datePeriod, addSet);
		
		// 計算フレックス対象時間に集計対象時間を加算する
		calcFlexTargetMinutes += totalLegalTime.v();
				
		// 合計計算フレックス時間を取得する
		int totalCalcFlexMinutes = this.flexTime.getTimeSeriesTotalCalcFlexTime(datePeriod, true).v();
		
		// 計算フレックス対象時間に合計計算フレックス時間を加算する
		calcFlexTargetMinutes += totalCalcFlexMinutes;
		
		// 「月次法定内のみ加算」を確認する
		int vacationAddMinutes = 0;
		if (!this.addMonthlyWithinStatutory){
			// 加算しない
			
			// 休暇加算時間を取得する
			vacationAddMinutes = GetVacationAddTime.getTime(
					datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSet).v();
		}
		else {
			// 加算する
			
			// 休暇加算時間を取得する
			vacationAddMinutes = GetVacationAddTime.getTime(
					datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSet).v();
		}

		// 計算フレックス対象時間に休暇加算時間を加算する
		calcFlexTargetMinutes += vacationAddMinutes;
		
		return new AttendanceTimeMonthWithMinus(calcFlexTargetMinutes);
	}

	/**
	 * フレックス超過の処理をする　（原則）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 * @param compensatoryLeaveAfterDudection 所定労働時間（代休控除後）
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param datePeriod 期間
	 * @param aggregateAtr 集計区分
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void flexExcessPrinciple(RequireM4 require, CacheCarrier cacheCarrier,
			AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset,
			AttendanceTimeMonthWithMinus compensatoryLeaveAfterDudection,
			String companyId, String employeeId, YearMonth yearMonth, DatePeriod datePeriod,
			MonthlyAggregateAtr aggregateAtr, MonAggrEmployeeSettings employeeSets,
			SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime,
			ClosureId closureId, ClosureDate closureDate, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){

		// 繰越不足時間を取得する
		AttendanceTimeMonthWithMinus carryforwardTime = this.flexCarryforwardTime.getFlexCarryforwardTime();
		if (carryforwardTime.lessThan(0)){
			carryforwardTime = new AttendanceTimeMonthWithMinus(-carryforwardTime.v());
		}
		else{
			carryforwardTime = new AttendanceTimeMonthWithMinus(0);
		}
		
		// フレックス繰越時間を取得する
		val carryforwardWorkTime = this.flexCarryforwardTime.getFlexCarryforwardWorkTime();
		val carryforwardShortageTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		
		if (carryforwardTime.lessThan(carryforwardTimeBeforeOffset.v())){
			
			// 繰越不足時間をフレックス繰越勤務時間に加算する
			this.flexCarryforwardTime.setFlexCarryforwardWorkTime(
					carryforwardWorkTime.addMinutes(carryforwardTime.v()));
			
			// 繰越時間相殺前と繰越不足時間の差分をフレックス超過時間・フレックス時間に加算する
			val difference = carryforwardTimeBeforeOffset.minusMinutes(carryforwardTime.v());
			this.flexExcessTime = this.flexExcessTime.addMinutes(difference.v());
			this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(difference.v(), 0));

			// 法定内・法定外フレックス時間を求める
			this.calcLegalFlexTime(require, cacheCarrier, companyId, employeeId, yearMonth, datePeriod, aggregateAtr,
					this.flexTime.getFlexTime().getTime(), employeeSets, settingsByFlex, aggregateTotalWorkingTime,
					closureId, closureDate, dailyAttendanceTime);
		}
		else {
			
			// 繰越時間相殺前をフレックス繰越勤務時間に加算する
			this.flexCarryforwardTime.setFlexCarryforwardWorkTime(
					carryforwardWorkTime.addMinutes(carryforwardTimeBeforeOffset.v()));
			
			// 繰越不足時間と繰越時間相殺前の差分をフレックス繰越不足時間に加算する
			val difference = carryforwardTime.minusMinutes(carryforwardTimeBeforeOffset.v());
			this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
					carryforwardShortageTime.addMinutes(difference.v()));
			
			// フレックス時間を 0 にする
			this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
					new AttendanceTimeMonthWithMinus(0),
					this.flexTime.getFlexTime().getCalcTime()));
		}
	}
	
	/**
	 * フレックス不足の処理をする　（原則）
	 * @param datePeriod 期間
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 * @param aggregateTotalWorkingTime 総労働時間
	 * @param addSet 加算設定
	 * @return 加算した休暇使用時間
	 */
	private AddedVacationUseTime flexShortagePrinciple(DatePeriod datePeriod,
			AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset,
			AggregateTotalWorkingTime aggregateTotalWorkingTime,
			AddSet addSet){
		
		AddedVacationUseTime addedVacationUseTime = new AddedVacationUseTime();
		
		// フレックス繰越時間を取得する
		AttendanceTimeMonthWithMinus carryforwardTime = this.flexCarryforwardTime.getFlexCarryforwardTime();
		// （不足分をプラスにして取り出す）
		if (carryforwardTime.lessThan(0)){
			carryforwardTime = new AttendanceTimeMonthWithMinus(-carryforwardTime.v());
		}
		else{
			carryforwardTime = new AttendanceTimeMonthWithMinus(0);
		}
		val carryforwardShortageTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		
		// フレックス繰越時間をフレックス繰越不足時間に加算する
		this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
				carryforwardShortageTime.addMinutes(carryforwardTime.v()));
		
		// 繰越時間相殺前を休暇加算後にコピーする
		AttendanceTimeMonthWithMinus afterAddVacation =
				new AttendanceTimeMonthWithMinus(carryforwardTimeBeforeOffset.v());
		
		// 休暇加算後＜ 0 なら、不足分を加算する
		if (afterAddVacation.lessThan(0)){
			
			// 「月次法定内のみ加算」を確認する
			if (!this.addMonthlyWithinStatutory){
				// 「加算しない」時、不足分を加算する

				// 加算設定　取得　（不足時用）
				val addSetWhenShortage = GetAddSet.get(
						WorkingSystem.FLEX_TIME_WORK, PremiumAtr.WHEN_SHORTAGE, this.holidayAdditionMap);
				if (addSetWhenShortage.getErrorInfo().isPresent()){
					this.errorInfos.add(addSetWhenShortage.getErrorInfo().get());
				}
				
				// 休暇加算時間を取得する
				val vacationAddTime = GetVacationAddTime.getTime(
						datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSetWhenShortage);
				
				// 休暇加算後に休暇加算時間を加算する
				afterAddVacation = afterAddVacation.addMinutes(vacationAddTime.v());
				if (afterAddVacation.lessThanOrEqualTo(0)){
					
					// 休暇加算後が 0 以下なら、休暇加算時間全体を「加算した休暇使用時間」とする
					addedVacationUseTime = AddedVacationUseTime.of(vacationAddTime);
				}
				else {
					
					// 休暇加算後が0を超える時、休暇加算後を 0 とし、休暇加算後と相殺前の差分を「加算した休暇使用時間」とする
					afterAddVacation = new AttendanceTimeMonthWithMinus(0);
					val difference = afterAddVacation.minusMinutes(carryforwardTimeBeforeOffset.v());
					addedVacationUseTime = AddedVacationUseTime.of(new AttendanceTimeMonth(difference.v()));
				}
			}
		}
		
		// 休暇加算後をフレックス不足時間に加算する
		// ※　休暇加算後はマイナス値なので、減算により、加算扱いにする。
		this.flexShortageTime = this.flexShortageTime.minusMinutes(afterAddVacation.v());

		// 休暇加算後をフレックス時間に加算する
		this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(
				afterAddVacation.v(), 0));
		
		return addedVacationUseTime;
	}

	/**
	 * 法定内・法定外フレックス時間を求める
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param aggregateAtr 集計区分
	 * @param flexTimeAfterSettle 清算後フレックス時間
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void calcLegalFlexTime(RequireM4 require, CacheCarrier cacheCarrier, 
			String companyId, String employeeId, YearMonth yearMonth, DatePeriod period,
			MonthlyAggregateAtr aggregateAtr, AttendanceTimeMonthWithMinus flexTimeAfterSettle,
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime, ClosureId closureId,
			ClosureDate closureDate, Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){
		
		// 「法定内集計設定．集計設定」を確認する
		val aggrSet = this.flexAggrSet.getLegalAggrSet().getAggregateSet();
		if (aggrSet == AggregateSetting.INCLUDE_ALL_OUTSIDE_TIME_IN_FLEX_TIME){		// 時間外は全てフレ時間
			
			// 全て法定外に入れる
			this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(flexTimeAfterSettle.v()));
			this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(0));
			return;
		}
		
		// フレ時間の内訳を管理する
		// 法定内として扱う時間を求める
		int treatLegalMinutes = this.askTreatLegalTime(require, cacheCarrier, companyId, employeeId, 
				period, yearMonth, aggregateAtr, employeeSets, settingsByFlex, closureId, closureDate, 
				Optional.of(aggregateTotalWorkingTime), dailyAttendanceTime).v();
		
		/** 大塚モードかを確認する */
		// 「月次法定内のみ加算」を確認する
		if (AppContexts.optionLicense().customize().ootsuka() && this.addMonthlyWithinStatutory){
			
			// 大塚モードで求める
			this.calcLegalFlexTimeForOtuka(require, cacheCarrier, employeeId, yearMonth, period, flexTimeAfterSettle,
					new AttendanceTimeMonth(treatLegalMinutes), settingsByFlex, aggregateTotalWorkingTime);
		} else{
			
			// 「法定内として扱う時間」と「清算後フレックス時間」を比較する
			if (treatLegalMinutes >= flexTimeAfterSettle.v()){
				
				// 全て法定内に入れる
				this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(0));
				this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(flexTimeAfterSettle.v()));
			}
			else {
				// 法定内と法定外に分けて入れる
				this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(
						flexTimeAfterSettle.v() - treatLegalMinutes));
				this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(
						treatLegalMinutes));
			}
		}
	}

	/**
	 * 法定内・法定外フレックス時間を求める　（大塚モードで求める）
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param flexTimeAfterSettle 清算後フレックス時間
	 * @param treatLegalTime 法定内として扱う時間
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void calcLegalFlexTimeForOtuka(RequireM3 require, CacheCarrier cacheCarrier, String employeeId,
			YearMonth yearMonth, DatePeriod period, AttendanceTimeMonthWithMinus flexTimeAfterSettle,
			AttendanceTimeMonth treatLegalTime, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime){
		
		// 休暇加算時間合計を求める
		int totalVacationAddMinutes = this.askTotalVacationAddTime(require, cacheCarrier, employeeId, yearMonth, period,
				PremiumAtr.ONLY_LEGAL, settingsByFlex, aggregateTotalWorkingTime).v();
		
		// 加算設定を取得する
		val addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, PremiumAtr.ONLY_LEGAL, this.holidayAdditionMap);
		if (addSet.isSpecialHoliday()){
			
			// 特別休暇使用時間合計を求める
			int totalSpecialLeaveUseMinutes = this.askTotalSpecialHolidayUseTime(require, cacheCarrier, employeeId, yearMonth, period,
					settingsByFlex, aggregateTotalWorkingTime).v();
			
			// 「休暇加算時間合計」に「特別休暇使用時間合計」を加算する
			totalVacationAddMinutes += totalSpecialLeaveUseMinutes;
		}
		
		// 「清算後フレックス時間」と「休暇加算時間合計」を比較する
		if (flexTimeAfterSettle.v() <= totalVacationAddMinutes){
			
			// 全て法定内に入れる
			this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(0));
			this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(flexTimeAfterSettle.v()));
		}
		else {
			
			// 「清算後フレックス時間差分」を求める
			int diffMinutes = flexTimeAfterSettle.v() - totalVacationAddMinutes;
			
			// 「清算後フレックス時間差分」と「法定内として扱う時間」を比較する
			if (diffMinutes <= treatLegalTime.v()){
				
				// 全て法定内に入れる
				this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(0));
				this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(flexTimeAfterSettle.v()));
			}
			else{
				
				// 法定内と法定外に分けて入れる
				this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(
						diffMinutes - treatLegalTime.v()));
				this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(
						totalVacationAddMinutes + treatLegalTime.v()));
			}
		}
	}

	/**
	 * 法定内として扱う時間を求める
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param aggregateAtr 集計区分
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 */
	private AttendanceTimeMonth askTreatLegalTime(RequireM2 require, CacheCarrier cacheCarrier, String companyId, 
			String employeeId, DatePeriod period, YearMonth yearMonth, MonthlyAggregateAtr aggregateAtr, 
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex, ClosureId closureId,
			ClosureDate closureDate, Optional<AggregateTotalWorkingTime> aggregateTotalWorkingTime, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){
		
		int result = 0;
		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		FlexTimeCurrentMonth flexTimeCurrentMonth = this.flexTime.getFlexTimeCurrentMonth();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
			// 時間外超過の時、時間外超過のフレックス時間を利用する
			flexTimeCurrentMonth = this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth();
		}
		
		// 「不足設定．清算期間」を確認する
		if (flexAggrSet.getInsufficSet().getSettlePeriod() == SettlePeriod.SINGLE_MONTH){
			// 単月
			// 「法定内として扱う時間」を求める　（法定労働時間－所定労働時間（基準時間）－繰越勤務時間）
			result = settingsByFlex.getStatutoryWorkingTimeMonth(require, yearMonth, period, closureId,
					aggregateTotalWorkingTime.map(c -> c.getVacationUseTime().getCompensatoryLeave()),
					dailyAttendanceTime).v();
			result -= flexTimeCurrentMonth.getStandardTime().v();
			result -= this.flexCarryforwardTime.getFlexCarryforwardWorkTime().v();
			
			// 「法定内として扱う時間」」を返す
			return new AttendanceTimeMonth(result < 0 ? 0 : result);
		}

		// 複数月
		// フレックス清算期間の取得
		SettlePeriodOfFlex settlePeriod = flexAggrSet.getInsufficSet().getSettlePeriod(require, cacheCarrier, employeeId, period, yearMonth);
		
		int totalStandMinutes = 0;		// 基準時間合計（分）
		int totalStatMinutes = 0;		// 法定労働時間合計（分）
		
		// 開始月～清算月を年月ごとにループする
		YearMonth indexYm = settlePeriod.getStartYm();
		for (; indexYm.lessThanOrEqualTo(settlePeriod.getSettleYm()); indexYm = indexYm.nextMonth()){
			
			// ループ中の年月と「年月」を比較する
			if (indexYm.lessThan(yearMonth)){	// 当月以外
				
				// ループ中の年月の「月別実績の勤怠時間」を取得する
				val prevAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, indexYm, closureId, closureDate);

				// 「基準時間合計」に「基準時間」を加算する
				if (prevAttendanceTimeList.isPresent()) {
					val prevAttendanceTime = prevAttendanceTimeList.get();
					val prevFlexTime = prevAttendanceTime.getMonthlyCalculation().getFlexTime();
					FlexTimeCurrentMonth prevCurrentMonth = prevFlexTime.getFlexTime().getFlexTimeCurrentMonth();
					if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
						// 時間外超過の時、時間外超過のフレックス時間を利用する
						prevCurrentMonth = prevFlexTime.getFlexTimeOfExcessOutsideTime().getFlexTimeCurrentMonth();
					}
					totalStandMinutes += prevCurrentMonth.getStandardTime().v();
					
					// 暦上の年月を渡して、年度に沿った年月を取得する
//					YearMonth statYearMonth = require.yearMonthFromCalender(cacheCarrier, companyId, indexYm);

					
				}
			} else if (indexYm.equals(yearMonth)) {		// 当月
				
				// 「基準時間合計」に「基準時間」を加算する
				totalStandMinutes += flexTimeCurrentMonth.getStandardTime().v();
				
				// 「法定労働時間合計」に「法定労働時間」を加算する
//				totalStatMinutes += settingsByFlex.getStatutoryWorkingTimeMonth(require, indexYm, period, closureId).v();
			}
			// 期間終了日時点の雇用コードを取得する
			val employmentOpt = employeeSets.getEmployment(period.end());
			if (employmentOpt.isPresent()){
				String employmentCd = employmentOpt.get().getEmploymentCode();

				/** 法定労働時間を取得する（フレックス用） */
				val monStatTime = settingsByFlex.getFlexStatutoryLaborTime(require, cacheCarrier, indexYm.equals(yearMonth), indexYm, 
						companyId, employmentCd, employeeId, period.end(), Optional.of(period), closureId, closureDate, 
						aggregateTotalWorkingTime, dailyAttendanceTime);

				// 「法定労働時間合計」に「法定労働時間」を加算する
				totalStatMinutes += monStatTime.map(c -> c.getStatutorySetting().v()).orElse(0);
				
			}
		}
		
		// 「法定内として扱う時間」を求める
		result = totalStatMinutes - totalStandMinutes;
		if (result < 0) result = 0;
		
		// 「法定内として扱う時間」」を返す
		return new AttendanceTimeMonth(result);
	}

	/**
	 * 休暇加算時間合計を求める
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param premiumAtr 割増区分
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private AttendanceTimeYear askTotalVacationAddTime(RequireM3 require, CacheCarrier cacheCarrier, String employeeId, YearMonth yearMonth,
			DatePeriod period, PremiumAtr premiumAtr, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime){
		
		int result = 0;
		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		
		// フレックス清算期間の取得
		SettlePeriodOfFlex settlePeriod = flexAggrSet.getInsufficSet().getSettlePeriod(require, cacheCarrier, employeeId, period, yearMonth);

		// 加算設定　取得
		val addSet = GetAddSet.get(WorkingSystem.FLEX_TIME_WORK, premiumAtr, this.holidayAdditionMap);
		if (addSet.getErrorInfo().isPresent()){
			this.errorInfos.add(addSet.getErrorInfo().get());
		}
		
		// 開始月～「年月」を年月ごとにループする
		YearMonth indexYm = settlePeriod.getStartYm();
		for (; indexYm.lessThanOrEqualTo(yearMonth); indexYm = indexYm.nextMonth()){
			
			int vacationAddMinutes = 0;		// 休暇加算時間
			
			// ループ中の年月と「年月」を比較する
			if (indexYm.lessThan(yearMonth)){	// 前月以前
				
				// ループ中の年月の「月別実績の勤怠時間」を取得する
				val prevAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, indexYm);

				// 休暇加算時間を取得する（過去分）
				if (!prevAttendanceTimeList.isEmpty()){
					val prevAttendanceTime = prevAttendanceTimeList.get(prevAttendanceTimeList.size() - 1);
					val vacationUseTime = prevAttendanceTime.getMonthlyCalculation().getAggregateTime().getVacationUseTime();
					vacationAddMinutes = GetVacationAddTime.getTimeForPast(vacationUseTime, addSet).v();
				}
			}
			else{		// 当月
				
				// 休暇加算時間を取得する
				vacationAddMinutes = GetVacationAddTime.getTime(
						period, aggregateTotalWorkingTime.getVacationUseTime(), addSet).v();
			}
			
			// 「休暇加算時間合計」に「休暇加算時間」を加算する
			result += vacationAddMinutes;
		}
		
		// 休暇加算時間合計を返す
		return new AttendanceTimeYear(result);
	}

	/**
	 * 特別休暇使用時間合計を求める
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private AttendanceTimeYear askTotalSpecialHolidayUseTime(RequireM3 require, CacheCarrier cacheCarrier, String employeeId,
			YearMonth yearMonth, DatePeriod period, SettingRequiredByFlex settingsByFlex,
			AggregateTotalWorkingTime aggregateTotalWorkingTime){
		
		int result = 0;
		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		
		// フレックス清算期間の取得
		SettlePeriodOfFlex settlePeriod = flexAggrSet.getInsufficSet().getSettlePeriod(require, cacheCarrier, employeeId, period, yearMonth);
		
		// 開始月～「年月」を年月ごとにループする
		YearMonth indexYm = settlePeriod.getStartYm();
		for (; indexYm.lessThanOrEqualTo(yearMonth); indexYm = indexYm.nextMonth()){
			
			int useMinutes = 0;		// 使用時間
			
			// ループ中の年月と「年月」を比較する
			if (indexYm.lessThan(yearMonth)){	// 前月以前
				
				// ループ中の年月の「月別実績の勤怠時間」を取得する
				val prevAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, indexYm);

				// 「特別休暇．使用時間」　→　使用時間
				if (!prevAttendanceTimeList.isEmpty()){
					val prevAttendanceTime = prevAttendanceTimeList.get(prevAttendanceTimeList.size() - 1);
					val vacationUseTime = prevAttendanceTime.getMonthlyCalculation().getAggregateTime().getVacationUseTime();
					useMinutes = vacationUseTime.getSpecialHoliday().getUseTime().v();
				}
			}
			else{		// 当月
				
				// 処理中年月の使用時間の合計　→　使用時間
				val vacationUseTime = aggregateTotalWorkingTime.getVacationUseTime();
				useMinutes = vacationUseTime.getSpecialHoliday().getTotalUseTime(period).v();
			}
			
			// 「特別休暇使用時間合計」に使用時間を加算する
			result += useMinutes;
		}
		
		// 特別休暇使用時間合計を返す
		return new AttendanceTimeYear(result);
	}
	
	/**
	 * 計算フレックス超過の処理をする　（原則）
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 */
	private void calcFlexExcessPrinciple(AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset){

		// フレックス繰越時間を取得する
		int carryforwardMinutes = this.flexCarryforwardTime.getFlexCarryforwardTime().v();
		// （不足分をプラスにして取り出す）
		if (carryforwardMinutes < 0){
			carryforwardMinutes = -carryforwardMinutes;
		}
		else{
			carryforwardMinutes = 0;
		}
		
		if (carryforwardMinutes < carryforwardTimeBeforeOffset.v()){
			
			// 繰越時間相殺前とフレックス繰越時間の差分を計算フレックス時間に加算する
			int difference = carryforwardTimeBeforeOffset.v() - carryforwardMinutes;
			this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(0, difference));
		}
		else {
			
			// 計算フレックス時間を 0 にする
			this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
					this.flexTime.getFlexTime().getTime(),
					new AttendanceTimeMonthWithMinus(0)));
		}
	}

	/**
	 * 計算フレックス不足の処理をする　（原則）
	 * @param datePeriod 期間
	 * @param carryforwardTimeBeforeOffset 繰越時間相殺前
	 * @param aggregateTotalWorkingTime 総労働時間
	 * @return 加算した休暇使用時間
	 */
	private AddedVacationUseTime calcFlexShortagePrinciple(DatePeriod datePeriod,
			AttendanceTimeMonthWithMinus carryforwardTimeBeforeOffset,
			AggregateTotalWorkingTime aggregateTotalWorkingTime){
		
		AddedVacationUseTime addedVacationUseTime = new AddedVacationUseTime();
		
		// 休暇加算前を確認する　←　繰越時間相殺前
		int beforeAddVacation = carryforwardTimeBeforeOffset.v();
		
		// 休暇加算前＜ 0 なら、不足分を加算する
		if (beforeAddVacation < 0){
			
			// 「月次法定内のみ加算」を確認する
			if (!this.addMonthlyWithinStatutory){
				// 「加算しない」時、不足分を加算する

				// 加算設定　取得　（不足時用）
				val addSetWhenShortage = GetAddSet.get(
						WorkingSystem.FLEX_TIME_WORK, PremiumAtr.WHEN_SHORTAGE, this.holidayAdditionMap);
				if (addSetWhenShortage.getErrorInfo().isPresent()){
					this.errorInfos.add(addSetWhenShortage.getErrorInfo().get());
				}
				
				// 休暇加算時間を取得する
				int vacationAddMinutes = GetVacationAddTime.getTime(
						datePeriod, aggregateTotalWorkingTime.getVacationUseTime(), addSetWhenShortage).v();
				
				// 休暇加算前に休暇加算時間を加算する　→　休暇加算後
				// ※　使わないので不要
				//int afterAddVacation = beforeAddVacation + vacationAddMinutes;
				
				// 加算した「休暇加算時間」を「加算した休暇使用時間」に退避しておく
				addedVacationUseTime = AddedVacationUseTime.of(new AttendanceTimeMonth(vacationAddMinutes));
			}
		}
		
		// 繰越時間相殺前を計算フレックス時間に加算する
		this.flexTime.setFlexTime(this.flexTime.getFlexTime().addMinutes(
				0, carryforwardTimeBeforeOffset.v()));
		
		return addedVacationUseTime;
	}

	/**
	 * フレックス繰越不可時間を求める
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param closureId 締めID
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 */
	private void askNotCarryforwardTime(RequireM2 require, CacheCarrier cacheCarrier, String companyId, 
			String employeeId, YearMonth yearMonth, DatePeriod period, ClosureId closureId, MonAggrCompanySettings companySets, 
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex){
		
		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		
		// フレックス繰越不可時間　←　０
		this.flexCarryforwardTime.setFlexNotCarryforwardTime(new AttendanceTimeMonth(0));
		
		// 「集計方法」を確認する
		if (flexAggrSet.getAggrMethod() == FlexAggregateMethod.FOR_CONVENIENCE) return;		// 便宜上集計
		
		// 「不足設定．繰越設定」を確認する
		if (flexAggrSet.getInsufficSet().getCarryforwardSet() ==
				CarryforwardSetInShortageFlex.CURRENT_MONTH_INTEGRATION) return;	// 当月精算
		
		// 「法定内集計設定．集計設定」を確認する
		if (flexAggrSet.getLegalAggrSet().getAggregateSet() ==
				AggregateSetting.INCLUDE_ALL_OUTSIDE_TIME_IN_FLEX_TIME) return;		// 時間外は全てフレ

		/** 月別実績の時間項目を丸める */
		int flexShortageMinutes = companySets.getRoundingSet().itemRound(AttendanceItemOfMonthly.FLEX_TIME.value, this.flexShortageTime).v();
		
		// フレックス不足時間を確認する
		if (flexShortageMinutes <= 0) return;
		
		/** 次の集計期間で同じ労働制で集計するかを確認する */
		if(WorkingSystemChangeCheckService.isSameWorkingSystemWithNextAggrPeriod(require, cacheCarrier, employeeId, period, WorkingSystem.FLEX_TIME_WORK) == WorkingSystemChangeState.CHANGED) {
			/** フレックス繰越不可時間　←　フレックス不足時間(丸め後) */
			this.flexCarryforwardTime.setFlexNotCarryforwardTime(new AttendanceTimeMonth(flexShortageMinutes));
			return;
		}
		
		// 翌月の所定と法定の差を求める
		int diffNextMonth = 0;
		{
			// 暦上の年月を渡して、年度に沿った年月を取得する
//			YearMonth statYearMonth = require.yearMonthFromCalender(cacheCarrier,
//					companyId, yearMonth.nextMonth());

			// 翌月末時点の雇用コードを確認する
			if (companySets.getClosureMap().containsKey(closureId.value)){
				val closure = companySets.getClosureMap().get(closureId.value);
				val closurePeriods = closure.getPeriodByYearMonth(yearMonth.nextMonth());
				if (closurePeriods.size() > 0){
					DatePeriod nextPeriod = closurePeriods.get(closurePeriods.size() - 1);	// 翌月締め期間
					val employmentOpt = employeeSets.getEmployment(nextPeriod.end());
					// ※　翌月末時点の雇用コード
					if (employmentOpt.isPresent()){
						String employmentCd = employmentOpt.get().getEmploymentCode();
						
						// 法定労働時間を取得する
						val flexStatTime = MonthlyStatutoryWorkingHours.flexMonAndWeekStatutoryTime(require, cacheCarrier, 
								companyId, employmentCd, employeeId, nextPeriod.end(), yearMonth.nextMonth());
						// 翌月時間の確認　（マスタから参照用）
						int nextStatMinutes = flexStatTime.getStatutorySetting().v();	// 翌月法定
						int nextPredMinutes = flexStatTime.getSpecifiedSetting().v();	// 翌月所定
						
						// 「フレックス勤務所定労働時間取得．参照先」を確認する
						if (!companySets.getComFlexSetOpt().map(c -> c.isWithinTimeUsageAttr()).orElseThrow(() -> new BusinessException("Msg_2242"))){	// 実績から参照
								
							// 処理中の年月の翌月の「月別実績の勤怠時間」を取得する
							val nextYearMonth = yearMonth.nextMonth();
							val nextAttendanceTimeList = require.attendanceTimeOfMonthly(employeeId, nextYearMonth);
							if (!nextAttendanceTimeList.isEmpty()){
								val nextAttendanceTime = nextAttendanceTimeList.get(nextAttendanceTimeList.size() - 1);
								val nextAggrTime = nextAttendanceTime.getMonthlyCalculation().getAggregateTime();
								
								// 「実績から参照」　かつ　翌月の実績から取得出来たら、その計画所定労働時間を採用する
								nextPredMinutes = nextAggrTime.getPrescribedWorkingTime().getSchedulePrescribedWorkingTime().v();
							}
						}
						
						// 翌月の所定と法定の差を求める　（法定労働時間－所定労働時間）
						diffNextMonth = nextStatMinutes - nextPredMinutes;
						if (diffNextMonth < 0) diffNextMonth = 0;
					}
				}
			}
		}
		
		// フレックス繰越不可時間を計算する
		if (flexShortageMinutes > diffNextMonth){
			this.flexCarryforwardTime.setFlexNotCarryforwardTime(new AttendanceTimeMonth(
					flexShortageMinutes - diffNextMonth));
		}
	}
	
	/**
	 * 清算処理をする
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param aggregateAtr 集計区分
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void settleProc(RequireM4 require, CacheCarrier cacheCarrier, String companyId, String employeeId, 
			YearMonth yearMonth, DatePeriod period, ClosureId closureId, ClosureDate closureDate, MonthlyAggregateAtr aggregateAtr,
			MonAggrEmployeeSettings employeeSets, SettingRequiredByFlex settingsByFlex, 
			AggregateTotalWorkingTime aggregateTotalWorkingTime, Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime) {

		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		
		// フレックス清算期間の取得
		SettlePeriodOfFlex settlePeriod = flexAggrSet.getInsufficSet().getSettlePeriod(require, cacheCarrier, employeeId, period, yearMonth);
		
		// 「清算月かどうか」を確認する
		if (settlePeriod.getIsSettleYm() == false){
			
			// フレックス時間　←　０
			this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
					new AttendanceTimeMonthWithMinus(0),
					this.flexTime.getFlexTime().getCalcTime()));
			this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(0));
			this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(0));
			this.flexShortageTime = new AttendanceTimeMonth(0);
			this.flexExcessTime = new AttendanceTimeMonth(0);
			return;
		}
		
		// 清算後フレックス時間を計算する　（フレックス繰越時間＋当月フレックス時間．フレックス時間－当月精算フレックス時間）
		FlexTimeCurrentMonth flexTimeCurrentMonth = this.flexTime.getFlexTimeCurrentMonth();
		if (aggregateAtr == MonthlyAggregateAtr.EXCESS_OUTSIDE_WORK){
			// 時間外超過の時、時間外超過のフレックス時間を利用する
			flexTimeCurrentMonth = this.flexTimeOfExcessOutsideTime.getFlexTimeCurrentMonth();
		}
		int afterSettleMinutes = this.flexCarryforwardTime.getFlexCarryforwardTime().v();
		afterSettleMinutes += flexTimeCurrentMonth.getFlexTime().v();
		afterSettleMinutes -= this.flexSettleTime.v();
		
		// 清算後フレックス時間を確認する
		if (afterSettleMinutes < 0){
			
			// フレックス不足の処理をする（清算）
			this.flexShortageSettle(require, cacheCarrier, employeeId, yearMonth, period,
					new AttendanceTimeMonthWithMinus(afterSettleMinutes),
					settingsByFlex, aggregateTotalWorkingTime);
		}
		else{
			
			// フレックス超過の処理をする（清算）
			this.flexExcessSettle(require, cacheCarrier, companyId, employeeId, yearMonth, period, closureId, closureDate, aggregateAtr,
					new AttendanceTimeMonthWithMinus(afterSettleMinutes),
					employeeSets, settingsByFlex, aggregateTotalWorkingTime, dailyAttendanceTime);
		}
	}
	
	/**
	 * フレックス不足の処理をする（清算）
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param flexTimeAfterSettle 清算後フレックス時間
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void flexShortageSettle(RequireM3 require, CacheCarrier cacheCarrier, String employeeId, YearMonth yearMonth,
			DatePeriod period, AttendanceTimeMonthWithMinus flexTimeAfterSettle,
			SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime){

		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		int afterSettleMinutes = flexTimeAfterSettle.v();	// 清算後フレックス時間（分）
		
		// 「集計方法」を確認する
		if (flexAggrSet.getAggrMethod() == FlexAggregateMethod.PRINCIPLE){
			
			// 「月次法定内のみ加算」を確認する
			if (this.addMonthlyWithinStatutory == false){
				
				// 不足分を加算する（清算）
				{
					// 休暇加算時間合計を求める
					int totalVacationAddMinutes = this.askTotalVacationAddTime(require, cacheCarrier, employeeId, yearMonth, period,
							PremiumAtr.WHEN_SHORTAGE, settingsByFlex, aggregateTotalWorkingTime).v();
					
					// 「清算後フレックス時間」に「休暇加算合計時間」を加算する
					afterSettleMinutes += totalVacationAddMinutes;
				}
			}
		}
		
		// フレックス時間　←　清算後フレックス時間
		this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
				new AttendanceTimeMonthWithMinus(afterSettleMinutes),
				this.flexTime.getFlexTime().getCalcTime()));
		this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(0));
		this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(0));
		this.flexShortageTime = new AttendanceTimeMonth(-afterSettleMinutes);
		this.flexExcessTime = new AttendanceTimeMonth(0);
	}
	
	/**
	 * フレックス超過の処理をする（清算）
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param period 期間
	 * @param aggregateAtr 集計区分
	 * @param flexTimeAfterSettle 清算後フレックス時間
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 総労働時間（当月分）
	 */
	private void flexExcessSettle(RequireM4 require, CacheCarrier cacheCarrier, String companyId, String employeeId, 
			YearMonth yearMonth, DatePeriod period, ClosureId closureId, ClosureDate closureDate, MonthlyAggregateAtr aggregateAtr, 
			AttendanceTimeMonthWithMinus flexTimeAfterSettle, MonAggrEmployeeSettings employeeSets, 
			SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){
		
		val flexAggrSet = settingsByFlex.getFlexAggrSet();
		int afterSettleMinutes = flexTimeAfterSettle.v();	// 清算後フレックス時間（分）
		
		// 「集計方法」を確認する
		if (flexAggrSet.getAggrMethod() == FlexAggregateMethod.PRINCIPLE){
			
			// 「清算後フレックス時間」を確認する
			if (afterSettleMinutes > 0){
				
				// フレックス時間　←　清算後フレックス時間
				this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
						new AttendanceTimeMonthWithMinus(afterSettleMinutes),
						this.flexTime.getFlexTime().getCalcTime()));
				this.flexShortageTime = new AttendanceTimeMonth(0);
				this.flexExcessTime = new AttendanceTimeMonth(afterSettleMinutes);
				
				// 法定内・法定外フレックス時間を求める
				this.calcLegalFlexTime(require, cacheCarrier, companyId, employeeId, yearMonth, period, aggregateAtr,
						new AttendanceTimeMonthWithMinus(afterSettleMinutes),
						employeeSets, settingsByFlex, aggregateTotalWorkingTime, closureId, closureDate, dailyAttendanceTime);
				
				return;
			}
		}
		
		// フレックス時間　←　清算後フレックス時間
		this.flexTime.setFlexTime(new TimeMonthWithCalculationAndMinus(
				new AttendanceTimeMonthWithMinus(afterSettleMinutes),
				this.flexTime.getFlexTime().getCalcTime()));
		this.flexTime.setLegalFlexTime(new AttendanceTimeMonthWithMinus(0));
		this.flexTime.setIllegalFlexTime(new AttendanceTimeMonthWithMinus(afterSettleMinutes));
		this.flexShortageTime = new AttendanceTimeMonth(0);
		this.flexExcessTime = new AttendanceTimeMonth(afterSettleMinutes);
	}
	
	/**
	 * フレックス勤務の就業時間を求める
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月（度）
	 * @param datePeriod 期間
	 * @param flexAggregateMethod フレックス集計方法
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 * @return 就業時間
	 */
	public Optional<AttendanceTimeMonth> askWorkTimeOfFlex(RequireM4 require, String companyId, String employeeId, ClosureId closureId,
			YearMonth yearMonth, DatePeriod datePeriod, FlexAggregateMethod flexAggregateMethod, 
			SettingRequiredByFlex settingsByFlex, AggregateTotalWorkingTime aggregateTotalWorkingTime, 
			Collection<AttendanceTimeOfDailyAttendance> dailyAttendanceTime){
		
		// 「フレックス集計方法」を確認する　（原則集計かどうか）
		if (flexAggregateMethod == FlexAggregateMethod.PRINCIPLE) {
			
			/** フレックス時間発生するかを確認する */
			if (this.checkIsFlexTimeOccur(settingsByFlex.getFlexAggrSet())){
				
				// 設定上の所定労働時間を確認する
				// 所定労働時間を求める(代休使用控除する)
				val prescribedWorkingTime = getPrescribedTime(require, yearMonth, datePeriod, settingsByFlex, 
						aggregateTotalWorkingTime, closureId, dailyAttendanceTime);
				
				int predMinutes = prescribedWorkingTime.v();
				if (predMinutes < 0) predMinutes = 0;
				
				// 所定労働時間を返す　（呼び出し元で就業時間に入れる）
				return Optional.of(new AttendanceTimeMonth(predMinutes));
			}
			// 「フレ不足時間」に値が入っているか確認する
			else if (this.flexShortageTime.greaterThan(0)) {
				
				// フレックス対象時間を集計する　→　所定労働時間
				int predMinutes = this.aggregateFlexTargetTime(datePeriod, 
						aggregateTotalWorkingTime, PremiumAtr.WHEN_SHORTAGE).v();
				if (predMinutes < 0) predMinutes = 0;
				
				// 所定労働時間を返す　（呼び出し元で就業時間に入れる）
				return Optional.of(new AttendanceTimeMonth(predMinutes));
			}
		} else {
			
			/** 就業時間の合計処理 */
			/** ○就業時間←就業合計時間 */
			aggregateTotalWorkingTime.getWorkTime().totalizeWorkTime(datePeriod);
		}
		
		return Optional.empty();
	}
	
	/** フレックス時間発生するかを確認する */
	public boolean checkIsFlexTimeOccur(FlexMonthWorkTimeAggrSet flexAggrSet) {
		
		/** 複数月かを確認する */
		if (!flexAggrSet.isMultiMonthSettlePeriod()) {
			/** ○「フレ超過時間」 OR 「フレ繰越勤務時間」に値が入っているか確認する */
			if (this.flexExcessTime.greaterThan(0) ||
					this.flexCarryforwardTime.getFlexCarryforwardWorkTime().greaterThan(0)) {
				/** 「発生する」を返す */
				return true;
			}
			/** 「発生しない」を返す */
			return false;
		} else {
			if (this.flexTime.getFlexTimeCurrentMonth().getFlexTime().greaterThan(0)) {
				/** 「発生する」を返す */
				return true;
			}
			/** 「発生しない」を返す */
			return false;
		}
	}
	
	/**
	 * 年休控除する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param flexAggregateMethod フレックス集計方法
	 * @param workingConditionItem 労働条件項目
	 * @param flexAggrSet フレックス時間勤務の月の集計設定
	 */
	private void deductAnnualLeave(RequireM1 require, String companyId, String employeeId, DatePeriod period,
			FlexAggregateMethod flexAggregateMethod, WorkingConditionItem workingConditionItem){
		
		// 「控除前のフレックス不足時間」を入れておく
		this.flexShortDeductTime.setFlexShortTimeBeforeDeduct(this.flexShortageTime);
		
		// 年休控除日数を時間換算する
		this.deductDaysAndTime.timeConversionOfDeductAnnualLeaveDays(require,
				companyId, employeeId, period, workingConditionItem);
		if (this.deductDaysAndTime.getErrorInfos().size() > 0){
			this.errorInfos.addAll(this.deductDaysAndTime.getErrorInfos());
			return;
		}
		
		// 年休控除日数に値が入っているか確認する
		if (this.flexShortDeductTime.getAnnualLeaveDeductDays().lessThanOrEqualTo(0.0)) return;
		
		// 控除前の年休控除時間を保存する
		this.annualLeaveTimeBeforeDeduct = this.deductDaysAndTime.getAnnualLeaveDeductTime();
		
		// 「フレックス繰越不足時間」を年休控除する
		val flexCarryforwardShortTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		if (flexCarryforwardShortTime.greaterThan(0)){
			AttendanceTimeMonth subtractTime = this.deductDaysAndTime.getAnnualLeaveDeductTime();
			if (subtractTime.greaterThan(flexCarryforwardShortTime.v())){
				subtractTime = flexCarryforwardShortTime;
			}
			// フレックス繰越不足時間から年休控除時間を引く
			this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
					flexCarryforwardShortTime.minusMinutes(subtractTime.v()));
			// 引いた分を年休控除時間から引く
			this.deductDaysAndTime.minusMinutesToAnnualLeaveDeductTime(subtractTime.v());
			// 引いた分をフレックス繰越勤務時間に足す
			this.flexCarryforwardTime.setFlexCarryforwardWorkTime(new AttendanceTimeMonth(
					this.flexCarryforwardTime.getFlexCarryforwardWorkTime().v() + subtractTime.v()));
		}
		
		// 年休控除時間が残っているか確認する
		if (this.deductDaysAndTime.getAnnualLeaveDeductTime().lessThanOrEqualTo(0)) return;
		
		// フレックス不足時間を年休控除する
		if (this.flexShortageTime.greaterThan(0)){
			AttendanceTimeMonth subtractTime = this.deductDaysAndTime.getAnnualLeaveDeductTime();
			if (subtractTime.greaterThan(this.flexShortageTime.v())){
				subtractTime = this.flexShortageTime;
			}
			// フレックス不足時間から年休控除時間を引く
			this.flexShortageTime = this.flexShortageTime.minusMinutes(subtractTime.v());
			// 引いた分を年休控除時間から引く
			this.deductDaysAndTime.minusMinutesToAnnualLeaveDeductTime(subtractTime.v());
			// 引いた分をフレックス時間に足す
			int flexMinutes = this.flexTime.getFlexTime().getTime().v();
			if (flexMinutes < 0){
				int addMinutes = subtractTime.v();
				if (addMinutes > -flexMinutes) addMinutes = -flexMinutes;
				this.flexTime.getFlexTime().setTime(new AttendanceTimeMonthWithMinus(flexMinutes + addMinutes));
			}
		}
		
		// 年休控除時間が残っているか確認する
		if (this.deductDaysAndTime.getAnnualLeaveDeductTime().lessThanOrEqualTo(0)) return;
		
		// 法定内・法定外フレックス時間に加算する
		{
			// 「フレックス集計方法」を確認すす
			if (flexAggregateMethod == FlexAggregateMethod.PRINCIPLE){
				val aggrSet = this.flexAggrSet.getLegalAggrSet().getAggregateSet();
				if (aggrSet == AggregateSetting.INCLUDE_ALL_OUTSIDE_TIME_IN_FLEX_TIME){
					// 年休控除時間を法定外フレックス時間に加算する
					this.flexTime.setIllegalFlexTime(this.flexTime.getIllegalFlexTime().addMinutes(
							this.deductDaysAndTime.getAnnualLeaveDeductTime().v()));
				}
				if (aggrSet == AggregateSetting.MANAGE_DETAIL){
					// 年休控除時間を法定内フレックス時間に加算する
					this.flexTime.setLegalFlexTime(this.flexTime.getLegalFlexTime().addMinutes(
							this.deductDaysAndTime.getAnnualLeaveDeductTime().v()));
				}
			}
			// 年休控除時間をフレックス時間に加算する
			int flexMinutes = this.flexTime.getFlexTime().getTime().v();
			int addMinutes = this.deductDaysAndTime.getAnnualLeaveDeductTime().v();
			this.flexTime.getFlexTime().setTime(new AttendanceTimeMonthWithMinus(flexMinutes + addMinutes));
		}
		
		int flexMinutes = this.flexTime.getFlexTime().getTime().v();
		if (flexMinutes > 0){
			
			// フレックス時間をフレックス超過時間へ移送
			this.flexExcessTime = new AttendanceTimeMonth(flexMinutes);
		}
	}
	
	/**
	 * 欠勤控除する
	 */
	private void deductAbsence(){
		
		// フレックス繰越不足時間を欠勤控除する
		val flexCarryforwardShortTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		if (flexCarryforwardShortTime.greaterThan(0)){
			AttendanceTimeMonth subtractTime = this.deductDaysAndTime.getAbsenceDeductTime();
			if (subtractTime.greaterThan(flexCarryforwardShortTime.v())){
				subtractTime = flexCarryforwardShortTime;
			}
			// フレックス繰越不足時間から欠勤控除時間を引く
			this.flexCarryforwardTime.setFlexCarryforwardShortageTime(
					flexCarryforwardShortTime.minusMinutes(subtractTime.v()));
			// 引いた分を欠勤控除時間から引く
			this.deductDaysAndTime.minusMinutesToAbsenceDeductTime(subtractTime.v());
		}
		
		// 欠勤控除時間が残っているか確認する
		if (this.deductDaysAndTime.getAbsenceDeductTime().lessThanOrEqualTo(0)) return;
		
		// フレックス不足時間を欠勤控除する
		if (this.flexShortageTime.greaterThan(0)){
			AttendanceTimeMonth subtractTime = this.deductDaysAndTime.getAbsenceDeductTime();
			if (subtractTime.greaterThan(this.flexShortageTime.v())){
				subtractTime = this.flexShortageTime;
			}
			// フレックス不足時間から欠勤控除時間を引く
			this.flexShortageTime = this.flexShortageTime.minusMinutes(subtractTime.v());
			// 引いた分を欠勤控除時間から引く
			this.deductDaysAndTime.minusMinutesToAbsenceDeductTime(subtractTime.v());
			// 引いた分をフレックス時間に足す
			int flexMinutes = this.flexTime.getFlexTime().getTime().v();
			if (flexMinutes < 0){
				int addMinutes = subtractTime.v();
				if (addMinutes > -flexMinutes) addMinutes = -flexMinutes;
				this.flexTime.getFlexTime().setTime(new AttendanceTimeMonthWithMinus(flexMinutes + addMinutes));
			}
		}
	}
	
	/**
	 * フレックス補填のエラーチェック
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 */
	private void checkErrorForInsufficientFlex(SettingRequiredByFlex settingsByFlex){
		
		// フレックス不足の年休補填管理を取得
		val insufficientFlexOpt = settingsByFlex.getInsufficientFlexOpt();
		if (insufficientFlexOpt.isPresent()){
			val insufficientFlex = insufficientFlexOpt.get();
			
			// 年休補填時間のエラーチェック
			val deductDays = new AttendanceDaysMonth(this.flexShortDeductTime.getAnnualLeaveDeductDays().v());
			if (insufficientFlex.checkErrorForSupplementableDays(deductDays)){
				
				// 社員の月別実績のエラーを作成する
				if (!this.perErrors.contains(Flex.FLEX_YEAR_HOLIDAY_DEDUCTIBLE_DAYS)){
					this.perErrors.add(Flex.FLEX_YEAR_HOLIDAY_DEDUCTIBLE_DAYS);
				}
			}
		}
		
		// フレックス不足時間のエラーチェック
		boolean shortageError = false;
		{
			// 社員のフレックス繰越上限時間を求める
			int limitTime = 15 * 60;		// 繰越上限時間
			{
				val flexShortageLimitOpt = settingsByFlex.getFlexShortageLimitOpt();
				if (flexShortageLimitOpt.isPresent()){
					limitTime = flexShortageLimitOpt.get().getLimitTime().v();
				}
				int possibleTime = settingsByFlex.getCanNextCarryforwardTimeMonth().v();	// 翌月繰越可能時間
				if (limitTime > possibleTime) limitTime = possibleTime;
			}
			
			// 「フレックス不足時間」と繰越上限時間を比較
			if (this.flexShortageTime.v() > limitTime) shortageError = true;
			
			if (shortageError){
				
				// 社員の月別実績のエラーを作成する
				if (!this.perErrors.contains(Flex.FLEX_EXCESS_CARRYOVER_TIME)){
					this.perErrors.add(Flex.FLEX_EXCESS_CARRYOVER_TIME);
				}
			}
		}
		
		// 前月繰越の補填未完了のエラーチェック
		val flexCarryforwardShortTime = this.flexCarryforwardTime.getFlexCarryforwardShortageTime();
		if (flexCarryforwardShortTime.v() > 0){
			
			// 社員の月別実績のエラーを作成する
			if (!this.perErrors.contains(Flex.FLEX_INCOMPLETE_OF_CARRYFORWARD)){
				this.perErrors.add(Flex.FLEX_INCOMPLETE_OF_CARRYFORWARD);
			}
		}
	}
	
	/**
	 * 総労働対象時間の取得
	 * @return 総労働対象時間
	 */
	public AttendanceTimeMonth getTotalWorkingTargetTime(){
		
		/** ○「集計方法」を確認する */
		if (this.flexAggrSet.getAggrMethod() == FlexAggregateMethod.FOR_CONVENIENCE) { /** 便宜上集計 */
			
			/** 日単位のフレックス超過時間を合計する */
			return this.flexTime.getPlusFlexTime();
		}
		
		/** 原則集計 */
		/** 総労働対象時間を計算する  */
		return new AttendanceTimeMonth(this.flexExcessTime.v() +
				this.flexCarryforwardTime.getFlexCarryforwardWorkTime().v());
	}
	
	/** フレックス時間の再計算 */
	public void recalcFlexTime() {
		
		/**　フレックス時間を計算する　*/
		val flexTime = this.flexExcessTime.valueAsMinutes() - this.flexShortageTime.valueAsMinutes();
		this.flexTime.getFlexTime().setTime(new AttendanceTimeMonthWithMinus(flexTime));
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(FlexTimeOfMonthly target){
		
		this.flexTime.sum(target.flexTime);
		this.flexExcessTime = this.flexExcessTime.addMinutes(target.flexExcessTime.v());
		this.flexShortageTime = this.flexShortageTime.addMinutes(target.flexShortageTime.v());
		this.flexCarryforwardTime.sum(target.flexCarryforwardTime);
		this.flexTimeOfExcessOutsideTime.sum(target.flexTimeOfExcessOutsideTime);
		this.flexShortDeductTime.sum(target.flexShortDeductTime);
		this.flexSettleTime = this.flexSettleTime.addMinutes(target.flexSettleTime.v());
	}
	
	public static interface RequireM1 extends DeductDaysAndTime.RequireM1 {}
	
	public static interface RequireM3 extends ShortageFlexSetting.Require {
		
		List<AttendanceTimeOfMonthly> attendanceTimeOfMonthly(String employeeId, YearMonth yearMonth);
	}
	
	public static interface RequireM4 extends RequireM2, RequireM3, RequireM7 {
	}
	
	public static interface RequireM5 extends RequireM1, RequireM4 {
	}
	
	public static interface RequireM2 extends MonthlyStatutoryWorkingHours.RequireM1, RequireM3, 
		SettingRequiredByFlex.Require, SettingRequiredByFlex.RequireM1 {
		
//		YearMonth yearMonthFromCalender(CacheCarrier cacheCarrier, String companyId, YearMonth yearMonth);
	}
	
	public static interface RequireM6 extends AggregateTotalWorkingTime.RequireM1, 
		ExcessOutsideWorkMng.RequireM1, ExcessOutsideWorkMng.RequireM7, WeeklyCalculation.Require {
		
	}
}
