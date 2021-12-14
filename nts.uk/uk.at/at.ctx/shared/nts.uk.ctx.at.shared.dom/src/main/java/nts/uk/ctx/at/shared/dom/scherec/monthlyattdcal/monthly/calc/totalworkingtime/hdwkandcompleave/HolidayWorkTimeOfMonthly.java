package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.hdwkandcompleave;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.ExcessOutsideTimeSetReg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.legaltransferorder.LegalHolidayWorkTransferOrderOfAggrMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.SettingRequiredByFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.timeseries.HolidayWorkTimeOfTimeSeries;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyAggregateAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.flex.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.subholtransferset.GetHolidayWorkAndTransferOrder;
import nts.uk.ctx.at.shared.dom.worktime.common.subholtransferset.HolidayWorkAndTransferAtr;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;

/**
 * 月別実績の休出時間
 * @author shuichi_ishida
 */
@Getter
public class HolidayWorkTimeOfMonthly implements Cloneable, Serializable {

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 休出合計時間 */
	private TimeMonthWithCalculation totalHolidayWorkTime;
	/** 事前休出時間 */
	private AttendanceTimeMonth beforeHolidayWorkTime;
	/** 振替合計時間 */
	private TimeMonthWithCalculation totalTransferTime;
	/** 集計休出時間 */
	private Map<HolidayWorkFrameNo, AggregateHolidayWorkTime> aggregateHolidayWorkTimeMap;
	
	/**
	 * コンストラクタ
	 */
	public HolidayWorkTimeOfMonthly(){
		
		this.totalHolidayWorkTime = TimeMonthWithCalculation.ofSameTime(0);
		this.beforeHolidayWorkTime = new AttendanceTimeMonth(0);
		this.totalTransferTime = TimeMonthWithCalculation.ofSameTime(0);
		
		this.aggregateHolidayWorkTimeMap = new HashMap<>();
	}

	/**
	 * ファクトリー
	 * @param totalHolidayWorkTime 休出合計時間
	 * @param beforeHolidayWorkTime 事前休出時間
	 * @param totalTransferTime 振替合計時間
	 * @param aggregateHolidayWorkTimeList 集計休出時間
	 * @return 月別実績の休出時間
	 */
	public static HolidayWorkTimeOfMonthly of(
			TimeMonthWithCalculation totalHolidayWorkTime,
			AttendanceTimeMonth beforeHolidayWorkTime,
			TimeMonthWithCalculation totalTransferTime,
			List<AggregateHolidayWorkTime> aggregateHolidayWorkTimeList){

		val domain = new HolidayWorkTimeOfMonthly();
		domain.totalHolidayWorkTime = totalHolidayWorkTime;
		domain.beforeHolidayWorkTime = beforeHolidayWorkTime;
		domain.totalTransferTime = totalTransferTime;
		for (AggregateHolidayWorkTime aggregateHolidayWorkTime : aggregateHolidayWorkTimeList){
			val holidayWorkFrameNo = aggregateHolidayWorkTime.getHolidayWorkFrameNo();
			domain.aggregateHolidayWorkTimeMap.putIfAbsent(holidayWorkFrameNo, aggregateHolidayWorkTime);
		}
		return domain;
	}
	
	@Override
	public HolidayWorkTimeOfMonthly clone() {
		HolidayWorkTimeOfMonthly cloned = new HolidayWorkTimeOfMonthly();
		try {
			cloned.totalHolidayWorkTime = new TimeMonthWithCalculation(
					new AttendanceTimeMonth(this.totalHolidayWorkTime.getTime().v()),
					new AttendanceTimeMonth(this.totalHolidayWorkTime.getCalcTime().v()));
			cloned.beforeHolidayWorkTime = new AttendanceTimeMonth(this.beforeHolidayWorkTime.v());
			cloned.totalTransferTime = new TimeMonthWithCalculation(
					new AttendanceTimeMonth(this.totalTransferTime.getTime().v()),
					new AttendanceTimeMonth(this.totalTransferTime.getCalcTime().v()));
			for (val aggrHolidayWorkTime : this.aggregateHolidayWorkTimeMap.entrySet()){
				cloned.aggregateHolidayWorkTimeMap.putIfAbsent(
						aggrHolidayWorkTime.getKey(), aggrHolidayWorkTime.getValue().clone());
			}
		}
		catch (Exception e){
			throw new RuntimeException("HolidayWorkTimeOfMonthly clone error.");
		}
		return cloned;
	}
	
	/**
	 * 対象の集計休出時間を取得する
	 * @param holidayWorkFrameNo 休出枠NO
	 * @return 対象の集計休出時間
	 */
	private AggregateHolidayWorkTime getTargetAggregateHolidayWorkTime(HolidayWorkFrameNo holidayWorkFrameNo){

		this.aggregateHolidayWorkTimeMap.putIfAbsent(holidayWorkFrameNo, new AggregateHolidayWorkTime(holidayWorkFrameNo));
		return this.aggregateHolidayWorkTimeMap.get(holidayWorkFrameNo);
	}
	
	/**
	 * 集計する　（通常・変形労働時間勤務用）
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 * @param companyId 会社ID
	 * @param workplaceId 職場ID
	 * @param employmentCd 雇用コード
	 * @param workingSystem 労働制
	 * @param aggregateAtr 集計区分
	 * @param workInfo 勤務情報
	 * @param legalHolidayWorkTransferOrder 法定内休出振替順
	 * @param excessOutsideTimeSet 割増集計方法
	 * @param roleHolidayWorkFrameMap 休出枠の役割
	 * @param autoExceptHolidayWorkFrames 自動的に除く休出枠
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	public void aggregateForRegAndIrreg(RequireM3 require, GeneralDate ymd,
			AttendanceTimeOfDailyAttendance attendanceTimeOfDaily,
			String companyId, String workplaceId, String employmentCd, WorkingSystem workingSystem,
			MonthlyAggregateAtr aggregateAtr, WorkInformation workInfo,
			LegalHolidayWorkTransferOrderOfAggrMonthly legalHolidayWorkTransferOrder,
			ExcessOutsideTimeSetReg excessOutsideTimeSet,
			Map<Integer, WorkdayoffFrame> roleHolidayWorkFrameMap,
			List<Integer> autoExceptHolidayWorkFrames,
			MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets){
		
		// 「休出枠」を取得する　（「休出枠の役割」を確認する）
		if (roleHolidayWorkFrameMap.size() > 0) {
			
			// 自動計算して休出時間を集計する
			this.aggregateByAutoCalc(require, ymd, attendanceTimeOfDaily, companyId, workplaceId, employmentCd, workingSystem,
					aggregateAtr,  workInfo, legalHolidayWorkTransferOrder, excessOutsideTimeSet,
					roleHolidayWorkFrameMap, companySets, employeeSets);
		}
		
		// 法定内休出の計算休出を休出時間の計算休出へ移送
		for (val entry : this.aggregateHolidayWorkTimeMap.entrySet()){
			for (val workEntry : entry.getValue().getTimeSeriesWorks().entrySet()){
				workEntry.getValue().addCalcLegalHWTimeToCalcHWTime();
			}
		}
	}
	
	/**
	 * 自動計算して集計する
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 * @param companyId 会社ID
	 * @param placeId 職場ID
	 * @param employmentCd 雇用コード
	 * @param workingSystem 労働制
	 * @param aggregateAtr 集計区分
	 * @param workInfo 勤務情報
	 * @param legalHolidayWorkTransferOrder 法定内休出振替順
	 * @param excessOutsideTimeSet 割増集計方法
	 * @param roleHolidayWorkFrameMap 休出枠の役割
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 */
	private void aggregateByAutoCalc(RequireM3 require, GeneralDate ymd,
			AttendanceTimeOfDailyAttendance attendanceTimeOfDaily,
			String companyId, String placeId, String employmentCd, WorkingSystem workingSystem,
			MonthlyAggregateAtr aggregateAtr,
			WorkInformation workInfo,
			LegalHolidayWorkTransferOrderOfAggrMonthly legalHolidayWorkTransferOrder,
			ExcessOutsideTimeSetReg excessOutsideTimeSet,
			Map<Integer, WorkdayoffFrame> roleHolidayWorkFrameMap,
			MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets){
		
		// 法定内休出にできる時間を計算する
		AttendanceTime canLegalHolidayWork = this.calcLegalHolidayWork(require, ymd, attendanceTimeOfDaily,
				companyId, placeId, employmentCd, workingSystem, companySets, employeeSets);
		
		// 「休出枠時間」を取得する
		val actualWorkingTimeOfDaily = attendanceTimeOfDaily.getActualWorkingTimeOfDaily();
		val totalWorkingTime = actualWorkingTimeOfDaily.getTotalWorkingTime();
		val excessOfStatutoryTimeOfDaily = totalWorkingTime.getExcessOfStatutoryTimeOfDaily();
		val holidayWorkTimeOfDaily = excessOfStatutoryTimeOfDaily.getWorkHolidayTime();
		// 休出時間がない時、集計しない
		if (!holidayWorkTimeOfDaily.isPresent()) return;
		val holidayWorkFrameTimeSrcs = holidayWorkTimeOfDaily.get().getHolidayWorkFrameTime();
			
		// 休出枠時間リストをマップに組み換え　（枠での検索用）
		Map<HolidayWorkFrameNo, HolidayWorkFrameTime> holidayWorkFrameTimes = new HashMap<>();
		for (val holidayWorkFrameTimeSrc : holidayWorkFrameTimeSrcs){
			holidayWorkFrameTimes.putIfAbsent(holidayWorkFrameTimeSrc.getHolidayFrameNo(), holidayWorkFrameTimeSrc);
		}
	
		// 休出・振替の処理順序を取得する
		if (workInfo.getWorkTimeCode() == null) return;
		val workTimeCode = workInfo.getWorkTimeCode().v();
		val holidayWorkAndTransferAtrs = GetHolidayWorkAndTransferOrder.get(require, companyId, workTimeCode, false);
		
		val statutoryHolAggrAtr = aggregateAtr == MonthlyAggregateAtr.MONTHLY 
				? StatutoryHolidayAggregateAtr.AGGREGATE_STATUTORY_HOLIDAYS : StatutoryHolidayAggregateAtr.NO_AGGREGATE_STATUTORY_HOLIDAYS;
		
		// 休出・振替のループ
		for (val holidayWorkAndTransferAtr : holidayWorkAndTransferAtrs){
		
			// 休出枠時間のループ処理
			canLegalHolidayWork = this.holidayWorkFrameTimeProcess(require, holidayWorkAndTransferAtr,
					legalHolidayWorkTransferOrder, excessOutsideTimeSet, canLegalHolidayWork,
					roleHolidayWorkFrameMap, holidayWorkFrameTimes, ymd,
					statutoryHolAggrAtr, workInfo, companySets);
		}
	}
	
	/**
	 * 法定内休出に出来る時間を計算する
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 * @param companyId 会社ID
	 * @param placeId 職場ID
	 * @param employmentCd 雇用コード
	 * @param workingSystem 労働制
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 */
	private AttendanceTime calcLegalHolidayWork(RequireM2 require, GeneralDate ymd, 
			AttendanceTimeOfDailyAttendance attendanceTimeOfDaily,
			String companyId, String placeId, String employmentCd, WorkingSystem workingSystem,
			MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets){
		
		// 日の法定労働時間を取得する
		DailyUnit dailyUnit = DailyUnit.zero();
		val workTimeSetOpt = companySets.getWorkingTimeSetting(require, employmentCd,
				employeeSets.getWorkplacesToRoot(ymd),
				workingSystem, employeeSets);
		if (workTimeSetOpt.isPresent()){
			if (workTimeSetOpt.get().getDailyTime() != null){
				dailyUnit = workTimeSetOpt.get().getDailyTime();
			}
		}
		
		// 法定内休出にできる時間
		AttendanceTime canLegalHolidayWork = new AttendanceTime(dailyUnit.getDailyTime().v());
		return canLegalHolidayWork;
	}
	
	/**
	 * 休出枠時間のループ処理
	 * @param holidayWorkAndTransferAtr 休出振替区分
	 * @param legalHolidayWorkTransferOrderOfAggrMonthly 法定内休出振替順
	 * @param excessOutsideTimeSet 割増集計方法
	 * @param canLegalHolidayWork 法定内休出に出来る時間
	 * @param roleHolidayWorkFrameMap 休出枠の役割
	 * @param holidayWorkFrameTimeMap 休出枠時間　（日別実績より取得）
	 * @param ymd 年月日
	 * @param aggregateAtr 集計区分
	 * @param workInfo 勤務情報
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 法定内休出に出来る時間　（計算後）
	 */
	private AttendanceTime holidayWorkFrameTimeProcess(RequireM1 require, 
			HolidayWorkAndTransferAtr holidayWorkAndTransferAtr,
			LegalHolidayWorkTransferOrderOfAggrMonthly legalHolidayWorkTransferOrderOfAggrMonthly,
			ExcessOutsideTimeSetReg excessOutsideTimeSet, 
			AttendanceTime canLegalHolidayWork, Map<Integer, WorkdayoffFrame> roleHolidayWorkFrameMap,
			Map<HolidayWorkFrameNo, HolidayWorkFrameTime> holidayWorkFrameTimeMap,
			GeneralDate ymd, StatutoryHolidayAggregateAtr aggregateAtr, WorkInformation workInfo,
			MonAggrCompanySettings companySets){
		
		AttendanceTime timeAfterCalc = canLegalHolidayWork;
		
		// 休出枠時間分ループ
		for (val legalHolidayWorkTransferOrder : legalHolidayWorkTransferOrderOfAggrMonthly.getLegalHolidayWorkTransferOrders()){
			val holidayWorkFrameNo = legalHolidayWorkTransferOrder.getHolidayWorkFrameNo();
			
			// 該当する（日別実績の）休出枠時間がなければ、次の枠へ
			if (!holidayWorkFrameTimeMap.containsKey(holidayWorkFrameNo)) continue;
			val holidayWorkFrameTime = holidayWorkFrameTimeMap.get(holidayWorkFrameNo);
			
			// 対象の時系列ワークを確認する
			val targetHolidayWorkTime = this.getTargetAggregateHolidayWorkTime(holidayWorkFrameNo);
			val timeSeriesWork = targetHolidayWorkTime.getAndPutTimeSeriesWork(ymd);
			
			// 休出枠．役割を取得する
			if (!roleHolidayWorkFrameMap.containsKey(holidayWorkFrameNo.v())) continue;			// 取得できない場合
			val roleHolidayWorkFrame = roleHolidayWorkFrameMap.get(holidayWorkFrameNo.v());
			
			/** 対象日の実績から休出時間を求める区分を確認する */
			val holWorkAtr = roleHolidayWorkFrame.getHolWorkAtrforDailyRecord(require, workInfo);
			
			if (holWorkAtr == HolidayAtr.STATUTORY_HOLIDAYS) {
				
				/** パラメータ。集計区分を確認する */
				if (aggregateAtr == StatutoryHolidayAggregateAtr.AGGREGATE_STATUTORY_HOLIDAYS) {
					
					/** ○取得した休出枠時間を「集計休出時間」に入れる（法定内） */
					addHolidayWorkTime(holidayWorkAndTransferAtr, holidayWorkFrameTime, timeSeriesWork);
				}
			} else if (holWorkAtr == HolidayAtr.NON_STATUTORY_HOLIDAYS) {
				
				/** ○「法定外休出を含める」を確認 */
				if (excessOutsideTimeSet.isLegalHoliday()) {
					/** ○取得した休出枠時間を「集計休出時間」に入れる（法定外） */
					/** ○法定内休出時間（法定内振替休出時間）に入れた時間分を「法定内休出にできる時間」から引く */
					timeAfterCalc = addHolidayWorkTime(holidayWorkAndTransferAtr, timeAfterCalc, holidayWorkFrameTime,
							timeSeriesWork);
				} else {
					/** ○取得した休出枠時間を「集計休出時間」に入れる（法定内） */
					addHolidayWorkTime(holidayWorkAndTransferAtr, holidayWorkFrameTime, timeSeriesWork);
				}
			}

			// 取得した休出枠時間の「事前申請時間」を入れる
			if (holidayWorkAndTransferAtr == HolidayWorkAndTransferAtr.HOLIDAY_WORK){
				timeSeriesWork.addBeforeAppTimeInHolidayWorkTime(holidayWorkFrameTime.getBeforeApplicationTime().get());
			}
		}
		
		// 「法定内休出に出来る時間」を返す
		return timeAfterCalc;
	}

	private AttendanceTime addHolidayWorkTime(HolidayWorkAndTransferAtr holidayWorkAndTransferAtr,
			AttendanceTime timeAfterCalc, HolidayWorkFrameTime holidayWorkFrameTime, HolidayWorkTimeOfTimeSeries timeSeriesWork) {
		
		val legalWorkTimeWithCalc = holidayWorkAndTransferAtr == HolidayWorkAndTransferAtr.HOLIDAY_WORK ?
				holidayWorkFrameTime.getHolidayWorkTime().get() : holidayWorkFrameTime.getTransferTime().get();
		AttendanceTime legalWorkTime = legalWorkTimeWithCalc.getTime();
		AttendanceTime workTime = new AttendanceTime(0);
		if (legalWorkTime.lessThanOrEqualTo(timeAfterCalc.v())){
			// 休出時間が法定内休出にできる時間以下の時
			timeAfterCalc = timeAfterCalc.minusMinutes(legalWorkTime.v());
		} else {
			// 休出時間が法定内休出にできる時間を超える時
			workTime = new AttendanceTime(legalWorkTime.v());
			workTime = workTime.minusMinutes(timeAfterCalc.v());
			legalWorkTime = new AttendanceTime(timeAfterCalc.v());
			timeAfterCalc = new AttendanceTime(0);
		}
		val legalWorkTimeWithDiv = TimeDivergenceWithCalculation.createTimeWithCalculation(legalWorkTime, new AttendanceTime(0));
		val workTimeWithDiv = TimeDivergenceWithCalculation.createTimeWithCalculation(workTime, legalWorkTimeWithCalc.getCalcTime());
		
		switch (holidayWorkAndTransferAtr) {
			case HOLIDAY_WORK:
				timeSeriesWork.addHolidayWorkTimeInLegalHolidayWorkTime(legalWorkTimeWithDiv);
				timeSeriesWork.addHolidayWorkTimeInHolidayWorkTime(workTimeWithDiv);
				break;
			case TRANSFER:
				timeSeriesWork.addTransferTimeInLegalHolidayWorkTime(legalWorkTimeWithDiv);
				timeSeriesWork.addTransferTimeInHolidayWorkTime(workTimeWithDiv);
				break;
		}
		return timeAfterCalc;
	}

	private void addHolidayWorkTime(HolidayWorkAndTransferAtr holidayWorkAndTransferAtr,
			HolidayWorkFrameTime holidayWorkFrameTime, HolidayWorkTimeOfTimeSeries timeSeriesWork) {
		// 取得した休出枠時間を「集計休出時間」に入れる（法定内）
		switch (holidayWorkAndTransferAtr){
		case HOLIDAY_WORK:
			timeSeriesWork.addHolidayWorkTimeInHolidayWorkTime(holidayWorkFrameTime.getHolidayWorkTime().get());
			break;
		case TRANSFER:
			timeSeriesWork.addTransferTimeInHolidayWorkTime(holidayWorkFrameTime.getTransferTime().get());
			break;
		}
	}
	
	/**
	 * 集計する　（フレックス時間勤務用）
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 * @param companyId 会社ID
	 * @param aggregateAtr 集計区分
	 * @param flexTime フレックス時間
	 * @param settingsByFlex フレックス勤務が必要とする設定
	 * @param workInfo 勤務情報
	 * @param companySets 月別集計で必要な会社別設定
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	public FlexTime aggregateForFlex(RequireM1 require, GeneralDate ymd,
			AttendanceTimeOfDailyAttendance attendanceTimeOfDaily,
			String companyId, MonthlyAggregateAtr aggregateAtr,
			FlexTime flexTime, SettingRequiredByFlex settingsByFlex,
			WorkInformation workInfo,
			MonAggrCompanySettings companySets){
		
		// 「休出枠時間」を取得する（日別）
		val holidayWorkTimeOfDaily = attendanceTimeOfDaily.getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().orElse(null);
		
		// 休出時間がない時、集計しない
		if (holidayWorkTimeOfDaily == null) return flexTime;
		
		for (val holFrame : holidayWorkTimeOfDaily.getHolidayWorkFrameTime()){

			// 対象の時系列ワークを確認する
			val targetHolidayWorkTime = this.getTargetAggregateHolidayWorkTime(holFrame.getHolidayFrameNo());
			val timeSeriesWork = targetHolidayWorkTime.getAndPutTimeSeriesWork(ymd);
			
			// 「休出枠．役割」を取得する
			val roleHolidayWorkFrame = settingsByFlex.getRoleHolidayWorkFrameMap().get(holFrame.getHolidayFrameNo().v());
			if (roleHolidayWorkFrame == null) continue;			// 取得できない場合
			
			/** 対象日の実績から休出時間を求める区分を確認する */
			val holWorkAtr = roleHolidayWorkFrame.getHolWorkAtrforDailyRecord(require, workInfo);
			if (holWorkAtr == HolidayAtr.STATUTORY_HOLIDAYS) {
				
				if (aggregateAtr == MonthlyAggregateAtr.MONTHLY) {

					/** ○取得した休出枠時間を「集計休出時間」に入れる */
					timeSeriesWork.addHolidayWorkTimeInHolidayWorkTime(holFrame.getHolidayWorkTime().get());
					timeSeriesWork.addTransferTimeInHolidayWorkTime(holFrame.getTransferTime().get());
				}
			} else if (holWorkAtr == HolidayAtr.NON_STATUTORY_HOLIDAYS) {

				/** ○「フレックス時間の扱い．法定外休出時間をフレックス時間に含める」を取得する */
				if (!settingsByFlex.getFlexAggrSet().getFlexTimeHandle().isIncludeIllegalHdwk()) {

					/** ○取得した休出枠時間を「集計休出時間」に入れる */
					timeSeriesWork.addHolidayWorkTimeInHolidayWorkTime(holFrame.getHolidayWorkTime().get());
					timeSeriesWork.addTransferTimeInHolidayWorkTime(holFrame.getTransferTime().get());
				} else {

					/** ○取得した休出枠時間を「集計休出時間」に入れる (就業内休出時間)*/
					timeSeriesWork.addHolidayWorkTimeInLegalHolidayWorkTime(holFrame.getHolidayWorkTime().get());
					timeSeriesWork.addTransferTimeInLegalHolidayWorkTime(holFrame.getTransferTime().get());
					
					/** ○取得した休出枠時間を「フレックス時間」に入れる */
					flexTime.addHolidayWorkTimeFrameTime(ymd, holFrame);
				}
			}
			
			/** ○取得した休出枠時間の「事前申請時間」を入れる */
			timeSeriesWork.addBeforeAppTimeInHolidayWorkTime(holFrame.getBeforeApplicationTime().get());
		}
		
		return flexTime;
	}
	
	/**
	 * 休日出勤の集計　（期間別集計用）
	 * @param datePeriod 期間
	 * @param attendanceTimeOfDailyMap 日別実績の勤怠時間リスト
	 * @param roleHolidayWorkFrameMap 休出枠の役割
	 */
	public void aggregateForByPeriod(
			DatePeriod datePeriod, WorkingSystem workingSystem,
			Map<GeneralDate, AttendanceTimeOfDailyAttendance> attendanceTimeOfDailyMap){
		
		// 休出時間を縦計する
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyMap.entrySet()) {
			val ymd = attendanceTimeOfDaily.getKey();
			
			// 期間外はスキップする
			if (!datePeriod.contains(ymd)) continue;
			
			// 「休出枠時間」を取得する
			val actualWorkingTimeOfDaily = attendanceTimeOfDaily.getValue().getActualWorkingTimeOfDaily();
			val totalWorkingTime = actualWorkingTimeOfDaily.getTotalWorkingTime();
			val excessOfStatutoryTimeOfDaily = totalWorkingTime.getExcessOfStatutoryTimeOfDaily();
			val holidayWorkTimeOfDailyOpt = excessOfStatutoryTimeOfDaily.getWorkHolidayTime();
			if (!holidayWorkTimeOfDailyOpt.isPresent()) continue;
			val holidayWorkTimeFrames = holidayWorkTimeOfDailyOpt.get().getHolidayWorkFrameTime();
			
			// 取得した「休出枠時間」を「集計休出時間」に入れる
			for (val holidayWorkTimeFrame : holidayWorkTimeFrames){
				int frameNo = holidayWorkTimeFrame.getHolidayFrameNo().v();
				val target = this.getTargetAggregateHolidayWorkTime(new HolidayWorkFrameNo(frameNo));
				target.addHolidayWorkTimeInTimeSeriesWork(ymd, holidayWorkTimeFrame);
			}
		}
		
		// 休出合計時間を集計する
		this.aggregateTotal(datePeriod, workingSystem);
	}
	
	/**
	 * 法定内休出時間を取得する
	 * @param datePeriod 期間
	 * @return 法定内休出時間
	 */
	public AttendanceTimeMonth getLegalHolidayWorkTime(DatePeriod datePeriod){

		AttendanceTimeMonth returnTime = new AttendanceTimeMonth(0);

		// 法定内休出時間．休出時間　＋　法定内休出時間．振替時間　の合計
		for (val aggregateHolidayWorkTime : this.aggregateHolidayWorkTimeMap.values()){
			for (val timeSeriesWork : aggregateHolidayWorkTime.getTimeSeriesWorks().values()){
				if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
				val legalHolidayWorkTime = timeSeriesWork.getLegalHolidayWorkTime();
				returnTime = returnTime.addMinutes(legalHolidayWorkTime.getHolidayWorkTime().get().getTime().v());
				returnTime = returnTime.addMinutes(legalHolidayWorkTime.getTransferTime().get().getTime().v());
			}
		}
		
		return returnTime;
	}
	
	/**
	 * 休出合計時間を集計する
	 * @param datePeriod 期間
	 */
	public void aggregateTotal(DatePeriod datePeriod, WorkingSystem workingSystem){

		this.totalHolidayWorkTime = TimeMonthWithCalculation.ofSameTime(0);
		this.beforeHolidayWorkTime = new AttendanceTimeMonth(0);
		this.totalTransferTime = TimeMonthWithCalculation.ofSameTime(0);

		for (val aggregateHolidayWorkTime : this.aggregateHolidayWorkTimeMap.values()){
			aggregateHolidayWorkTime.aggregate(datePeriod, workingSystem);
			this.totalHolidayWorkTime = this.totalHolidayWorkTime.addMinutes(
					aggregateHolidayWorkTime.getHolidayWorkTime().getTime().v(),
					aggregateHolidayWorkTime.getHolidayWorkTime().getCalcTime().v());
			this.beforeHolidayWorkTime = this.beforeHolidayWorkTime.addMinutes(
					aggregateHolidayWorkTime.getBeforeHolidayWorkTime().v());
			this.totalTransferTime = this.totalTransferTime.addMinutes(
					aggregateHolidayWorkTime.getTransferTime().getTime().v(),
					aggregateHolidayWorkTime.getTransferTime().getCalcTime().v());
		}
	}
	
	/**
	 * 休出合計時間を集計する　（再計算用）
	 */
	public void recalcTotal(){

		this.totalHolidayWorkTime = TimeMonthWithCalculation.ofSameTime(0);
		this.beforeHolidayWorkTime = new AttendanceTimeMonth(0);
		this.totalTransferTime = TimeMonthWithCalculation.ofSameTime(0);

		for (val aggregateHolidayWorkTime : this.aggregateHolidayWorkTimeMap.values()){
			this.totalHolidayWorkTime = this.totalHolidayWorkTime.addMinutes(
					aggregateHolidayWorkTime.getHolidayWorkTime().getTime().v(),
					aggregateHolidayWorkTime.getHolidayWorkTime().getCalcTime().v());
			this.beforeHolidayWorkTime = this.beforeHolidayWorkTime.addMinutes(
					aggregateHolidayWorkTime.getBeforeHolidayWorkTime().v());
			this.totalTransferTime = this.totalTransferTime.addMinutes(
					aggregateHolidayWorkTime.getTransferTime().getTime().v(),
					aggregateHolidayWorkTime.getTransferTime().getCalcTime().v());
		}
	}
	
	/**
	 * 総労働対象時間の取得
	 * @return 総労働対象時間
	 */
	public AttendanceTimeMonth getTotalWorkingTargetTime(){
		
		return new AttendanceTimeMonth(this.totalHolidayWorkTime.getTime().v() +
				this.totalTransferTime.getTime().v());
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(HolidayWorkTimeOfMonthly target){
		
		this.totalHolidayWorkTime = this.totalHolidayWorkTime.addMinutes(
				target.totalHolidayWorkTime.getTime().v(), target.totalHolidayWorkTime.getCalcTime().v());
		this.beforeHolidayWorkTime = this.beforeHolidayWorkTime.addMinutes(target.beforeHolidayWorkTime.v());
		this.totalTransferTime = this.totalTransferTime.addMinutes(
				target.totalTransferTime.getTime().v(), target.totalTransferTime.getCalcTime().v());
		
		for (val aggrHolidayWorkTime : this.aggregateHolidayWorkTimeMap.values()){
			val frameNo = aggrHolidayWorkTime.getHolidayWorkFrameNo();
			if (target.aggregateHolidayWorkTimeMap.containsKey(frameNo)){
				aggrHolidayWorkTime.sum(target.aggregateHolidayWorkTimeMap.get(frameNo));
			}
		}
		for (val targetAggrHolidayWorkTime : target.aggregateHolidayWorkTimeMap.values()){
			val frameNo = targetAggrHolidayWorkTime.getHolidayWorkFrameNo();
			this.aggregateHolidayWorkTimeMap.putIfAbsent(frameNo, targetAggrHolidayWorkTime);
		}
	}
	
	public static interface RequireM1 extends MonAggrCompanySettings.RequireM4, WorkdayoffFrame.Require {
	}
	
	public static interface RequireM2 extends MonAggrCompanySettings.RequireM1 {
	}
	
	public static interface RequireM3 extends RequireM1, RequireM2, 
		MonAggrCompanySettings.RequireM3, GetHolidayWorkAndTransferOrder.RequireM1 {

	}

}