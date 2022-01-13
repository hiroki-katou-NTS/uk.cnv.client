package nts.uk.ctx.at.record.dom.monthlyprocess.aggr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.common.times.AttendanceTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.hdwkandcompleave.AggregateHolidayWorkTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.overtime.AggregateOverTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.excessoutside.ExcessOutsideWork;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.excessoutside.ExcessOutsideWorkOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.excessoutside.SuperHD60HConTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.leave.AggregateLeaveDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.leave.AnyLeave;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.specificdays.AggregateSpecificDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AggregateAbsenceDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.bonuspaytime.AggregateBonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.AggregateDivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.DivergenceAtrOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.AggregateGoOut;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.medicaltime.MedicalTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.AggregatePremiumTime;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;
import nts.uk.ctx.at.shared.dom.worktype.CloseAtr;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * テスト実装：月別実績関連データの永続化入出力
 * @author shuichi_ishida
 */
@Stateless
public class MonthlyRelatedDataInOutTestImpl implements MonthlyRelatedDataInOutTest {

	/** 月別集計が必要とするリポジトリ */
//	@Inject
//	private RepositoriesRequiredByMonthlyAggr repositories;
	
	/** 管理期間の36協定時間リポジトリ */
	@Inject
	private AgreementTimeOfManagePeriodRepository agreementTimeOfManagePeriodRepository;
	
	/**
	 * テスト入出力
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param datePeriod 期間
	 * @return 集計結果
	 */
	@Override
	public AggregateMonthlyRecordValue aggregate(String companyId, String employeeId, YearMonth yearMonth,
			ClosureId closureId, ClosureDate closureDate, DatePeriod datePeriod) {
		
		val returnValue = new AggregateMonthlyRecordValue();
		
		//*****start（テスト shuichi_ishida）　2017.12 検収用。仮データ設定。
		Random random = new Random();
		val randomVal = random.nextInt(9) + 1;		// 1～9の乱数発生
		val attendanceTime = new AttendanceTimeOfMonthly(employeeId, yearMonth,
				closureId, closureDate, datePeriod);
		val monthlyCalculation = attendanceTime.getMonthlyCalculation();
		val aggregateTime = monthlyCalculation.getAggregateTime();
		aggregateTime.getWorkTime().setWorkTime(new AttendanceTimeMonth(450 + randomVal));
		val aggrOvertime1 =	AggregateOverTime.of(
				new OverTimeFrameNo(1),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(118),
						new AttendanceTimeMonth(120 + randomVal)),
				new AttendanceTimeMonth(0),
				TimeMonthWithCalculation.ofSameTime(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		val aggrOvertime2 =	AggregateOverTime.of(
				new OverTimeFrameNo(2),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(236),
						new AttendanceTimeMonth(240 + randomVal)),
				new AttendanceTimeMonth(0),
				TimeMonthWithCalculation.ofSameTime(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		aggregateTime.getOverTime().getAggregateOverTimeMap().put(
				aggrOvertime1.getOverTimeFrameNo(), aggrOvertime1);
		if (randomVal >= 5){
			aggregateTime.getOverTime().getAggregateOverTimeMap().put(
					aggrOvertime2.getOverTimeFrameNo(), aggrOvertime2);
		}
		aggregateTime.getOverTime().setTotalOverTime(new TimeMonthWithCalculation(
				new AttendanceTimeMonth(440 + randomVal), new AttendanceTimeMonth(0)));
		val aggrHdwktime1 = AggregateHolidayWorkTime.of(
				new HolidayWorkFrameNo(1),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(178),
						new AttendanceTimeMonth(180 + randomVal)),
				new AttendanceTimeMonth(0),
				TimeMonthWithCalculation.ofSameTime(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		val aggrHdwktime2 = AggregateHolidayWorkTime.of(
				new HolidayWorkFrameNo(2),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(296),
						new AttendanceTimeMonth(300 + randomVal)),
				new AttendanceTimeMonth(0),
				TimeMonthWithCalculation.ofSameTime(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		aggregateTime.getHolidayWorkTime().getAggregateHolidayWorkTimeMap().put(
				aggrHdwktime1.getHolidayWorkFrameNo(), aggrHdwktime1);
		if (randomVal >= 6){
			aggregateTime.getHolidayWorkTime().getAggregateHolidayWorkTimeMap().put(
					aggrHdwktime2.getHolidayWorkFrameNo(), aggrHdwktime2);
		}
		val actualWorkingTime = monthlyCalculation.getActualWorkingTime();
		actualWorkingTime.setWeeklyTotalPremiumTime(new AttendanceTimeMonth(540 + randomVal));
		actualWorkingTime.setMonthlyTotalPremiumTime(new AttendanceTimeMonth(2460 + randomVal));
		
		// 時間外超過追加　2018.3.1
		val excoutVal = randomVal - 1;	// 0～8の乱数
		List<ExcessOutsideWork> excoutList = new ArrayList<>();
		for (int ixExcoutTime = 0; ixExcoutTime <= excoutVal; ixExcoutTime++){
			val breakdownNo = ixExcoutTime / 3 + 1;
			val excessNo = ixExcoutTime % 3 + 1;
			excoutList.add(ExcessOutsideWork.of(breakdownNo, excessNo, new AttendanceTimeMonth(120 + randomVal)));
		}
		val excessOutsideWork = ExcessOutsideWorkOfMonthly.of(
				new AttendanceTimeMonth(1200 + randomVal),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonthWithMinus(0),
				excoutList,
				new SuperHD60HConTime());
		attendanceTime.setExcessOutsideWork(excessOutsideWork);
		
		// 縦計分追加　2018.2.8
		val verticalTotal = attendanceTime.getVerticalTotal();
		val vWorkTime = verticalTotal.getWorkTime();
		val vBonusPayTime = vWorkTime.getBonusPayTime();
		val vBonusPayTimeMap = vBonusPayTime.getBonusPayTime();
		val aggrBonusPayTime01 = AggregateBonusPayTime.of(1,
				new AttendanceTimeMonth(3110 + randomVal),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		val aggrBonusPayTime02 = AggregateBonusPayTime.of(1,
				new AttendanceTimeMonth(3120 + randomVal),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		vBonusPayTimeMap.put(1, aggrBonusPayTime01);
		if (randomVal >= 6) vBonusPayTimeMap.put(2, aggrBonusPayTime02);
		val vDivergenceTime = vWorkTime.getDivergenceTime();
		val vDivergenceTimeMap = vDivergenceTime.getDivergenceTimeList();
		val aggrDivergenceTime01 = AggregateDivergenceTime.of(1,
				new AttendanceTimeMonth(3210 + randomVal),
				DivergenceAtrOfMonthly.NORMAL);
		val aggrDivergenceTime02 = AggregateDivergenceTime.of(2,
				new AttendanceTimeMonth(3220 + randomVal),
				DivergenceAtrOfMonthly.NORMAL);
		vDivergenceTimeMap.put(1, aggrDivergenceTime01);
		if (randomVal >= 6) vDivergenceTimeMap.put(2, aggrDivergenceTime02);
		val vGoOut = vWorkTime.getGoOut();
		val vGoOuts = vGoOut.getGoOuts();
		val aggrGoOut01 = AggregateGoOut.of(GoingOutReason.PRIVATE,
				new AttendanceTimesMonth(10 + randomVal),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0));
		val aggrGoOut02 = AggregateGoOut.of(GoingOutReason.PUBLIC,
				new AttendanceTimesMonth(20 + randomVal),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0),
				TimeMonthWithCalculation.ofSameTime(0));
		vGoOuts.put(GoingOutReason.PRIVATE, aggrGoOut01);
		if (randomVal >= 6) vGoOuts.put(GoingOutReason.PUBLIC, aggrGoOut02);
		val vPremiumTime = vWorkTime.getPremiumTime();
		val vPremiumTimeMap = vPremiumTime.getPremiumTime();
		val aggrPremiumTime01 = AggregatePremiumTime.of(ExtraTimeItemNo.valueOf(1), new AttendanceTimeMonth(3410 + randomVal), new AttendanceAmountMonth(0L));
		val aggrPremiumTime02 = AggregatePremiumTime.of(ExtraTimeItemNo.valueOf(2), new AttendanceTimeMonth(3420 + randomVal), new AttendanceAmountMonth(0L));
		vPremiumTimeMap.put(1, aggrPremiumTime01);
		if (randomVal >= 6) vPremiumTimeMap.put(2, aggrPremiumTime02);
		val medicalTime = vWorkTime.getMedicalTime();
		val medicalTime01 = MedicalTimeOfMonthly.of(WorkTimeNightShift.DAY_SHIFT,
				new AttendanceTimeMonth(3510 + randomVal),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		val medicalTime02 = MedicalTimeOfMonthly.of(WorkTimeNightShift.NIGHT_SHIFT,
				new AttendanceTimeMonth(3520 + randomVal),
				new AttendanceTimeMonth(0),
				new AttendanceTimeMonth(0));
		medicalTime.put(WorkTimeNightShift.DAY_SHIFT, medicalTime01);
		if (randomVal >= 6) medicalTime.put(WorkTimeNightShift.NIGHT_SHIFT, medicalTime02);
		val vWorkDays = verticalTotal.getWorkDays();
		val vAbsenceDays = vWorkDays.getAbsenceDays();
		val vAbsenceDaysMap = vAbsenceDays.getAbsenceDaysList();
		val aggrAbsenceDays01 = AggregateAbsenceDays.of(1, new AttendanceDaysMonth(10.0 + randomVal),
				new AttendanceTimeMonth(0));
		val aggrAbsenceDays02 = AggregateAbsenceDays.of(2, new AttendanceDaysMonth(20.0 + randomVal),
				new AttendanceTimeMonth(0));
		vAbsenceDaysMap.put(1, aggrAbsenceDays01);
		if (randomVal >= 6) vAbsenceDaysMap.put(2, aggrAbsenceDays02);
		val vSpecificDays = vWorkDays.getSpecificDays();
		val vSpecificDaysMap = vSpecificDays.getSpecificDays();
		val specificDays01 = AggregateSpecificDays.of(new SpecificDateItemNo(1),
				new AttendanceDaysMonth(30.0 + randomVal),
				new AttendanceDaysMonth(0.0));
		val specificDays02 = AggregateSpecificDays.of(new SpecificDateItemNo(2),
				new AttendanceDaysMonth(40.0 + randomVal),
				new AttendanceDaysMonth(0.0));
		vSpecificDaysMap.put(new SpecificDateItemNo(1), specificDays01);
		if (randomVal >= 6) vSpecificDaysMap.put(new SpecificDateItemNo(2), specificDays02);
		val vLeave = vWorkDays.getLeave();
		val fixLeaveDays = vLeave.getFixLeaveDays();
		val anyLeaveDays = vLeave.getAnyLeaveDays();
		val aggrLeaveDays01 = AggregateLeaveDays.of(CloseAtr.PRENATAL, new AttendanceDaysMonth(10.0 + randomVal));
		val aggrLeaveDays02 = AggregateLeaveDays.of(CloseAtr.POSTPARTUM, new AttendanceDaysMonth(20.0 + randomVal));
		val anyLeave01 = AnyLeave.of(1, new AttendanceDaysMonth(30.0 + randomVal));
		val anyLeave02 = AnyLeave.of(2, new AttendanceDaysMonth(40.0 + randomVal));
		fixLeaveDays.put(CloseAtr.PRENATAL, aggrLeaveDays01);
		if (randomVal >= 6) fixLeaveDays.put(CloseAtr.POSTPARTUM, aggrLeaveDays02);
		anyLeaveDays.put(0, anyLeave01);
		if (randomVal >= 6) anyLeaveDays.put(1, anyLeave02);
		vWorkDays.getWorkDays().setDays(new AttendanceDaysMonth(20.0 +  randomVal));
		
		returnValue.setAttendanceTime(Optional.of(attendanceTime));
		//*****end（テスト shuichi_ishida）　2017.12 検収用。仮データ設定。
		
		//*****（テスト 2017.12 shuichi_ichida）　集計設定読み込み　（テストデータは、DB直接手入力）
		/*
		val aggrSetOpt = this.repositories.getAggrSettingMonthly().get("TESTCMP", "TESTWKP", "XM", "XESTSYA");
		List<OverTimeFrameNo> otlist = new ArrayList<>();
		if (aggrSetOpt.isPresent()){
			otlist = aggrSetOpt.get().getRegularWork().getAggregateTimeSet().getTreatOverTimeOfLessThanCriteriaPerDay().getAutoExcludeOverTimeFrames();
		}
		*/

		//*****（テスト 2017.3.1 shuichi_ichida）　月別実績の丸め設定読み書き
		/*
		List<ItemRoundingSetOfMonthly> itemRoundingSets = new ArrayList<>();
		itemRoundingSets.add(ItemRoundingSetOfMonthly.of(companyId, 12,
				new TimeRoundingSetting(Unit.ROUNDING_TIME_15MIN, Rounding.ROUNDING_DOWN_OVER)));
		itemRoundingSets.add(ItemRoundingSetOfMonthly.of(companyId, 15,
				new TimeRoundingSetting(Unit.ROUNDING_TIME_5MIN, Rounding.ROUNDING_DOWN)));
		this.repositories.getRoundingSetOfMonthly().persistAndUpdate(RoundingSetOfMonthly.of(
				companyId,
				Optional.of(TimeRoundingOfExcessOutsideTime.of(
						Unit.ROUNDING_TIME_30MIN,
						RoundingProcessOfExcessOutsideTime.ROUNDING_UP)),
				itemRoundingSets));
		//this.repositories.getRoundingSetOfMonthly().persistAndUpdate(RoundingSetOfMonthly.of(
		//		companyId,
		//		Optional.empty(),
		//		itemRoundingSets));
		val roundingSetOpt = this.repositories.getRoundingSetOfMonthly().find(companyId);
		RoundingSetOfMonthly roundingSet = null;
		if (roundingSetOpt.isPresent()) roundingSet = roundingSetOpt.get();
		*/
		
		//*****（テスト　2018.3.16 shuichi_ishida）　管理期間の36協定時間読み書き
		/*
		val agreementTimeOfManagePeriod = AgreementTimeOfManagePeriod.of(
				employeeId,
				yearMonth,
				new Year(yearMonth.year()),
				AgreementTimeOfMonthly.of(
						new AttendanceTimeMonth(1200 + randomVal),
						new LimitOneMonth(700 + randomVal),
						new LimitOneMonth(0),
						Optional.empty(),
						Optional.of(new LimitOneMonth(400 + randomVal)),
						AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM),
				AgreementTimeBreakdown.of(
						new AttendanceTimeMonth(500 + randomVal),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0),
						new AttendanceTimeMonth(0))
			);
		this.agreementTimeOfManagePeriodRepository.persistAndUpdate(agreementTimeOfManagePeriod);
		*/
//		val agreementTimeOpt = this.agreementTimeOfManagePeriodRepository.find(employeeId, yearMonth);
//		AgreementTimeOfManagePeriod agreementTime = null;
//		if (agreementTimeOpt.isPresent()) agreementTime = agreementTimeOpt.get();
		
		return returnValue;
	}	
}
