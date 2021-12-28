package nts.uk.ctx.at.record.infra.entity.byperiod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.gul.reflection.FieldReflection;
import nts.gul.reflection.ReflectionUtil;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpDaysAbsence;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpAggrSpecDays;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpAggrSpvcDays;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrBnspyTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrDivgTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpTimeGoout;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrPremTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpMedicalTime;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.common.times.AttendanceTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AgreementTimeByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AnyItemByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.ExcessOutsideByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.FlexTimeByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.MonthlyCalculationByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.TotalWorkingTimeByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.anyaggrperiod.AnyAggrFrameCode;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.AggregateTotalTimeSpentAtWork;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.PrescribedWorkingTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.WorkTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.hdwkandcompleave.HolidayWorkTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.overtime.OverTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.vacationusetime.AnnualLeaveUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.vacationusetime.CompensatoryLeaveUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.vacationusetime.RetentionYearlyUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.vacationusetime.SpecialHolidayUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.vacationusetime.VacationUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.totalcount.TotalCountByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.VerticalTotalOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.reservation.OrderAmountMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.reservation.ReservationDetailOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.reservation.ReservationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workamount.WorkAmountOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.EndClockOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.WorkClockOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.pclogon.AggrPCLogonClock;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.pclogon.AggrPCLogonDivergence;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.pclogon.PCLogonClockOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.pclogon.PCLogonDivergenceOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.pclogon.PCLogonOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.StgGoStgBackDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.TimeConsumpVacationDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.WorkDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.leave.AggregateLeaveDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.leave.AnyLeave;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.leave.LeaveOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.specificdays.SpecificDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AbsenceDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AttendanceDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.HolidayDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.HolidayWorkDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.PredeterminedDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.RecruitmentDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.SpcVacationDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.TemporaryWorkTimesOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.TwoTimesWorkTimesOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.WorkDaysDetailOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.WorkTimesOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.WorkTimeOfMonthlyVT;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.actual.HolidayUsageOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.actual.LaborTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.attendanceleave.AttendanceLeaveGateTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.bonuspaytime.BonusPayTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.breaktime.BreakTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.DivergenceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.GoOutForChildCare;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.GoOutOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.interval.IntervalTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.Late;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.LateLeaveEarlyOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.LeaveEarly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.midnighttime.IllegalMidnightTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.midnighttime.MidnightTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.PremiumTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.timevarience.BudgetTimeVarienceOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.toppage.TopPageDisplayOfMonthly;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.worktype.CloseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 任意期間別実績の勤怠時間
 * @author shuichu_ishida
 */
@Entity
@Table(name = "KRCDT_ANP_ATTENDANCE_TIME")
@NoArgsConstructor
public class KrcdtAnpAttendanceTime extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** プライマリキー */
	@EmbeddedId
	public KrcdtAnpAttendanceTimePK PK;
	
	/** 総労働時間 */
	@Column(name = "TOTAL_WORKING_TIME")
	public int totalWorkingTime;
	
	/** フレックス時間 */
	@Column(name = "FLEX_TIME")
	public int flexTime;
	/** フレックス超過時間 */
	@Column(name = "FLEX_EXCESS_TIME")
	public int flexExcessTime;
	/** フレックス不足時間 */
	@Column(name = "FLEX_SHORTAGE_TIME")
	public int flexShortageTime;
	/** 事前フレックス時間 */
	@Column(name = "BEFORE_FLEX_TIME")
	public int beforeFlexTime;


	/** 就業時間金額 */
	@Column(name = "WORK_TIME_AMOUNT")
	public long workTimeAmount;
	
	/** 割増金額合計 */
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public long premiumAmountTotal;
	
	/** 就業時間 */
	@Column(name = "WORK_TIME")
	public int workTime;
	/** 実働就業時間 */
	@Column(name = "ACTWORK_TIME")
	public int actualWorkTime;
	/** 所定内割増時間 */
	@Column(name = "WITPRS_PREMIUM_TIME")
	public int prescribedPremiumTime;
	
	/** 計画所定労働時間 */
	@Column(name = "SCHE_PRS_WORK_TIME")
	public int schedulePrescribedWorkTime;
	/** 実績所定労働時間 */
	@Column(name = "RECD_PRS_WORK_TIME")
	public int recordPrescribedWorkTime;
	
	/** 残業合計時間 */
	@Column(name = "TOTAL_OVER_TIME")
	public int totalOverTime;
	/** 計算残業合計時間 */
	@Column(name = "CALC_TOTAL_OVER_TIME")
	public int calcTotalOverTime;
	/** 事前残業時間 */
	@Column(name = "BEFORE_OVER_TIME")
	public int beforeOverTime;
	/** 振替残業合計時間 */
	@Column(name = "TOTAL_TRNOVR_TIME")
	public int totalTransferOverTime;
	/** 計算振替残業合計時間 */
	@Column(name = "CALC_TOTAL_TRNOVR_TIME")
	public int calcTotalTransferOverTime;

	/** 休出合計時間 */
	@Column(name = "TOTAL_HDWK_TIME")
	public int totalHolidayWorkTime;
	/** 計算休出合計時間 */
	@Column(name = "CALC_TOTAL_HDWK_TIME")
	public int calcTotalHolidayWorkTime;
	/** 事前休出時間 */
	@Column(name = "BEFORE_HDWK_TIME")
	public int beforeHolidayWorkTime;
	/** 振替休出合計時間 */
	@Column(name = "TOTAL_TRNHDWK_TIME")
	public int totalTransferHdwkTime;
	/** 計算振替休出合計時間 */
	@Column(name = "CALC_TOTAL_TRNHDWK_TIME")
	public int calcTotalTransferHdwkTime;
	
	/** 年休使用時間 */
	@Column(name = "ANNLEA_USE_TIME")
	public int annualLeaveUseTime;
	/** 積立年休使用時間 */
	@Column(name = "RSVLEA_USE_TIME")
	public int reserveLeaveUseTime;
	/** 特別休暇使用時間 */
	@Column(name = "SPCLEA_USE_TIME")
	public int specialLeaveUseTime;
	/** 代休使用時間 */
	@Column(name = "CMPLEA_USE_TIME")
	public int compensatoryLeaveUseTime;
	
	/** 拘束残業時間 */
	@Column(name = "SPENT_OVER_TIME")
	public int spentOverTime;
	/** 拘束深夜時間 */
	@Column(name = "SPENT_MIDNIGHT_TIME")
	public int spentMidnightTime;
	/** 拘束休出時間 */
	@Column(name = "SPENT_HOLIDAY_TIME")
	public int spentHolidayTime;
	/** 拘束差異時間 */
	@Column(name = "SPENT_VARIENCE_TIME")
	public int spentVarienceTime;
	/** 総拘束時間 */
	@Column(name = "TOTAL_SPENT_TIME")
	public int totalSpentTime;

	/** 36協定時間 */
	@Column(name = "AGREEMENT_TIME")
	public int agreementTime;
	
	/** 勤務日数 */
	@Column(name = "VT_WORK_DAYS")
	public double vtWorkDays;
	/** 勤務回数 */
	@Column(name = "VT_WORK_TIMES")
	public int vtWorkTimes;
	/** 二回勤務回数 */
	@Column(name = "VT_TWOTIMES_WORK_TIMES")
	public int vtTwoTimesWorkTimes;
	/** 臨時勤務回数 */
	@Column(name = "VT_TEMPORARY_WORK_TIMES")
	public int vtTemporaryWorkTimes;
	/** 臨時勤務時間 */
	@Column(name = "VT_TEMPORARY_WORK_TIME")
	public int vtTemporaryWorkTime;
	/** 所定日数 */
	@Column(name = "VT_PREDET_DAYS")
	public double vtPredetermineDays;
	/** 休日日数 */
	@Column(name = "VT_HOLIDAY_DAYS")
	public double vtHolidayDays;
	/** 出勤日数 */
	@Column(name = "VT_ATTENDANCE_DAYS")
	public double vtAttendanceDays;
	/** 休出日数 */
	@Column(name = "VT_HOLIDAY_WORK_DAYS")
	public double vtHolidayWorkDays;
	/** 欠勤合計日数 */
	@Column(name = "VT_TOTAL_ABSENCE_DAYS")
	public double vtTotalAbsenceDays;
	/** 欠勤合計時間 */
	@Column(name = "VT_TOTAL_ABSENCE_TIME")
	public int vtTotalAbsenceTime;
	/** 振出日数 */
	@Column(name = "VT_RECRUIT_DAYS")
	public double vtRecruitDays;
	/** 特別休暇合計日数 */
	@Column(name = "VT_TOTAL_SPCVACT_DAYS")
	public double vtTotalSpecialVacationDays;
	/** 特別休暇合計時間 */
	@Column(name = "VT_TOTAL_SPCVACT_TIME")
	public int vtTotalSpecialVacationTime;
	/** 直行日数 */
	@Column(name = "STRAIGHT_GO_DAYS")
	public double straightGoDays;
	/** 直帰日数 */
	@Column(name = "STRAIGHT_BACK_DAYS")
	public double straightBackDays;
	/** 直行直帰日数 */
	@Column(name = "STRAIGHT_GO_BACK_DAYS")
	public double straightGoBackDays;
	/** 産前休業日数 */
	@Column(name = "VT_PRENATAL_LEAVE_DAYS")
	public double vtPrenatalLeaveDays;
	/** 産後休業日数 */
	@Column(name = "VT_POSTPARTUM_LEAVE_DAYS")
	public double vtPostpartumLeaveDays;
	/** 育児休業日数 */
	@Column(name = "VT_CHILDCARE_LEAVE_DAYS")
	public double vtChildcareLeaveDays;
	/** 介護休業日数 */
	@Column(name = "VT_CARE_LEAVE_DAYS")
	public double vtCareLeaveDays;
	/** 傷病休業日数 */
	@Column(name = "VT_INJILN_LEAVE_DAYS")
	public double vtInjuryOrIllnessLeaveDays;
	/** 任意休業日数01 */
	@Column(name = "VT_ANY_LEAVE_DAYS_01")
	public double vtAnyLeaveDays01;
	/** 任意休業日数02 */
	@Column(name = "VT_ANY_LEAVE_DAYS_02")
	public double vtAnyLeaveDays02;
	/** 任意休業日数03 */
	@Column(name = "VT_ANY_LEAVE_DAYS_03")
	public double vtAnyLeaveDays03;
	/** 任意休業日数04 */
	@Column(name = "VT_ANY_LEAVE_DAYS_04")
	public double vtAnyLeaveDays04;
	
	/** 育児外出回数 */
	@Column(name = "VT_CLDCAR_GOOUT_TIMES")
	public int vtChildcareGoOutTimes;
	/** 育児外出時間 */
	@Column(name = "VT_CLDCAR_GOOUT_TIME")
	public int vtChildcareGoOutTime;
	/** 育児外出所定内時間 */
	@Column(name = "VT_CLDCAR_WITHIN_TIME")
	public int vtChildcareGoOutWithinTime;
	/** 育児外出所定外時間 */
	@Column(name = "VT_CLDCAR_EXCESS_TIME")
	public int vtChildcareGoOutExcessTime;
	/** 介護外出回数 */
	@Column(name = "VT_CARE_GOOUT_TIMES")
	public int vtCareGoOutTimes;
	/** 介護外出時間 */
	@Column(name = "VT_CARE_GOOUT_TIME")
	public int vtCareGoOutTime;
	/** 介護外出所定内時間 */
	@Column(name = "VT_CARE_WITHIN_TIME")
	public int vtCareGoOutWithinTime;
	/** 介護外出所定外時間 */
	@Column(name = "VT_CARE_EXCESS_TIME")
	public int vtCareGoOutExcessTime;
	/** 休憩時間 */
	@Column(name = "VT_BREAK_TIME")
	public int vtBreakTime;
	/** 休憩回数 */
	@Column(name = "VT_BREAK_TIMES")
	public int vtBreakTimes;
	/** 所定内休憩時間 */
	@Column(name = "VT_BREAK_WITHIN_TIME")
	public int vtBreakWithinTime;
	/** 所定内控除時間 */
	@Column(name = "VT_BREAK_WITHIN_DED_TIME")
	public int vtBreakWithinDeducTime;
	/** 所定外休憩時間 */
	@Column(name = "VT_BREAK_EXCESS_TIME")
	public int vtBreakExcessTime;
	/** 所定外控除時間 */
	@Column(name = "VT_BREAK_EXCESS_DED_TIME")
	public int vtBreakExcessDeducTime;
	/** 残業深夜時間 */
	@Column(name = "VT_OVWK_MDNT_TIME")
	public int vtOverWorkMidnightTime;
	/** 計算残業深夜時間 */
	@Column(name = "VT_CALC_OVWK_MDNT_TIME")
	public int vtCalcOverWorkMidnightTime;
	/** 法定内深夜時間 */
	@Column(name = "VT_LGL_MDNT_TIME")
	public int vtLegalMidnightTime;
	/** 計算法定内深夜時間 */
	@Column(name = "VT_CALC_LGL_MDNT_TIME")
	public int vtCalcLegalMidnightTime;
	/** 法定外深夜時間 */
	@Column(name = "VT_ILG_MDNT_TIME")
	public int vtIllegalMidnightTime;
	/** 計算法定外深夜時間 */
	@Column(name = "VT_CALC_ILG_MDNT_TIME")
	public int vtCalcIllegalMidnightTime;
	/** 法定外事前深夜時間 */
	@Column(name = "VT_ILG_BFR_MDNT_TIME")
	public int vtIllegalBeforeMidnightTime;
	/** 法定内休出深夜時間 */
	@Column(name = "VT_LGL_HDWK_MDNT_TIME")
	public int vtLegalHolidayWorkMidnightTime;
	/** 計算法定内休出深夜時間 */
	@Column(name = "VT_CALC_LGL_HDWK_MN_TIME")
	public int vtCalcLegalHolidayWorkMidnightTime;
	/** 法定外休出深夜時間 */
	@Column(name = "VT_ILG_HDWK_MDNT_TIME")
	public int vtIllegalHolidayWorkMidnightTime;
	/** 計算法定外休出深夜時間 */
	@Column(name = "VT_CALC_ILG_HDWK_MN_TIME")
	public int vtCalcIllegalHolidayWorkMidnightTime;
	/** 祝日休出深夜時間 */
	@Column(name = "VT_SPHD_HDWK_MDNT_TIME")
	public int vtSpecialHolidayWorkMidnightTime;
	/** 計算祝日休出深夜時間 */
	@Column(name = "VT_CALC_SPHD_HDWK_MN_TIME")
	public int vtCalcSpecialHolidayWorkMidnightTime;
	/** 遅刻回数 */
	@Column(name = "VT_LATE_TIMES")
	public int vtLateTimes;
	/** 遅刻時間 */
	@Column(name = "VT_LATE_TIME")
	public int vtLateTime;
	/** 計算遅刻時間 */
	@Column(name = "VT_CALC_LATE_TIME")
	public int vtCalcLateTime;
	/** 早退回数 */
	@Column(name = "VT_LEAVEEARLY_TIMES")
	public int vtLeaveEarlyTimes;
	/** 早退時間 */
	@Column(name = "VT_LEAVEEARLY_TIME")
	public int vtLeaveEarlyTime;
	/** 計算早退時間 */
	@Column(name = "VT_CALC_LEAVEEARLY_TIME")
	public int vtCalcLeaveEarlyTime;
	/** 入退門出勤前時間 */
	@Column(name = "VT_ALGT_BFR_ATND_TIME")
	public int vtAttendanceLeaveGateBeforeAttendanceTime;
	/** 入退門退勤後時間 */
	@Column(name = "VT_ALGT_AFT_LVWK_TIME")
	public int vtAttendanceLeaveGateAfterLeaveWorkTime;
	/** 入退門滞在時間 */
	@Column(name = "VT_ALGT_STAYING_TIME")
	public int vtAttendanceLeaveGateStayingTime;
	/** 入退門不就労時間 */
	@Column(name = "VT_ALGT_UNEMPLOYED_TIME")
	public int vtAttendanceLeaveGateUnemployedTime;
	/** 予実差異時間 */
	@Column(name = "VT_BUDGET_VARIENCE_TIME")
	public int vtBudgetVarienceTime;

	/** 終業回数 */
	@Column(name = "VT_ENDWORK_TIMES")
	public int vtEndWorkTimes;
	/** 終業合計時刻 */
	@Column(name = "VT_ENDWORK_TOTAL_CLOCK")
	public int vtEndWorkTotalClock;
	/** 終業平均時刻 */
	@Column(name = "VT_ENDWORK_AVE_CLOCK")
	public int vtEndWorkAverageClock;
	/** ログオン合計日数 */
	@Column(name = "VT_LOGON_TOTAL_DAYS")
	public double vtLogonTotalDays;
	/** ログオン合計時刻 */
	@Column(name = "VT_LOGON_TOTAL_CLOCK")
	public int vtLogonTotalClock;
	/** ログオン平均時刻 */
	@Column(name = "VT_LOGON_AVE_CLOCK")
	public int vtLogonAverageClock;
	/** ログオフ合計日数 */
	@Column(name = "VT_LOGOFF_TOTAL_DAYS")
	public double vtLogoffTotalDays;
	/** ログオフ合計時刻 */
	@Column(name = "VT_LOGOFF_TOTAL_CLOCK")
	public int vtLogoffTotalClock;
	/** ログオフ平均時刻 */
	@Column(name = "VT_LOGOFF_AVE_CLOCK")
	public int vtLogoffAverageClock;
	/** ログオン乖離日数 */
	@Column(name = "VT_LOGON_DIV_DAYS")
	public double vtLogonDivDays;
	/** ログオン乖離合計時間 */
	@Column(name = "VT_LOGON_DIV_TOTAL_TIME")
	public int vtLogonDivTotalTime;
	/** ログオン乖離平均時間 */
	@Column(name = "VT_LOGON_DIV_AVE_TIME")
	public int vtLogonDivAverageTime;
	/** ログオフ乖離日数 */
	@Column(name = "VT_LOGOFF_DIV_DAYS")
	public double vtLogoffDivDays;
	/** ログオフ乖離合計時間 */
	@Column(name = "VT_LOGOFF_DIV_TOTAL_TIME")
	public int vtLogoffDivTotalTime;
	/** ログオフ乖離平均時間 */
	@Column(name = "VT_LOGOFF_DIV_AVE_TIME")
	public int vtLogoffDivAverageTime;
	
	/** 時間消化休暇日数 */
	@Column(name = "VT_TIME_DIGEST_DAYS")
	public double timeDigestDays;
	/** 時間消化休暇時間 */
	@Column(name = "VT_TIME_DIGEST_TIME")
	public int timeDigestTime;
	
	/** トップページ表示用残業合計時間 */
	@Column(name = "TOP_PAGE_OT_TIME")
	public int topPageOtTime;
	/** トップページ表示用休日出勤合計時間 */
	@Column(name = "TOP_PAGE_HOL_WORK_TIME")
	public int topPageHolWorkTime;
	/** トップページ表示用フレックス合計時間 */
	@Column(name = "TOP_PAGE_FLEX_TIME")
	public int topPageFlexTime;
	
	/** インターバル時間 */
	@Column(name = "INTERVAL_TIME")
	public int intervalTime;
	/** インターバル免除時間 */
	@Column(name = "INTERVAL_DEDUCTION_TIME")
	public int intervalDeductTime;
	
	/** 金額１注文金額 */
	@Column(name = "BENTOU_ORDER_AMOUNT_1")
	public int bentouOrderAmount1;
	/** 金額２注文金額 */
	@Column(name = "BENTOU_ORDER_AMOUNT_2")
	public int bentouOrderAmount2;
	/** 弁当メニュー枠番１注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_1")
	public int bentouOrderNumber1;
	/** 弁当メニュー枠番２注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_2")
	public int bentouOrderNumber2;
	/** 弁当メニュー枠番３注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_3")
	public int bentouOrderNumber3;
	/** 弁当メニュー枠番４注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_4")
	public int bentouOrderNumber4;
	/** 弁当メニュー枠番５注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_5")
	public int bentouOrderNumber5;
	/** 弁当メニュー枠番６注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_6")
	public int bentouOrderNumber6;
	/** 弁当メニュー枠番７注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_7")
	public int bentouOrderNumber7;
	/** 弁当メニュー枠番８注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_8")
	public int bentouOrderNumber8;
	/** 弁当メニュー枠番９注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_9")
	public int bentouOrderNumber9;
	/** 弁当メニュー枠番１０注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_10")
	public int bentouOrderNumber10;
	/** 弁当メニュー枠番１１注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_11")
	public int bentouOrderNumber11;
	/** 弁当メニュー枠番１２注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_12")
	public int bentouOrderNumber12;
	/** 弁当メニュー枠番１３注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_13")
	public int bentouOrderNumber13;
	/** 弁当メニュー枠番１４注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_14")
	public int bentouOrderNumber14;
	/** 弁当メニュー枠番１５注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_15")
	public int bentouOrderNumber15;
	/** 弁当メニュー枠番１６注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_16")
	public int bentouOrderNumber16;
	/** 弁当メニュー枠番１７注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_17")
	public int bentouOrderNumber17;
	/** 弁当メニュー枠番１８注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_18")
	public int bentouOrderNumber18;
	/** 弁当メニュー枠番１９注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_19")
	public int bentouOrderNumber19;
	/** 弁当メニュー枠番２０注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_20")
	public int bentouOrderNumber20;
	/** 弁当メニュー枠番２１注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_21")
	public int bentouOrderNumber21;
	/** 弁当メニュー枠番２２注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_22")
	public int bentouOrderNumber22;
	/** 弁当メニュー枠番２３注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_23")
	public int bentouOrderNumber23;
	/** 弁当メニュー枠番２４注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_24")
	public int bentouOrderNumber24;
	/** 弁当メニュー枠番２５注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_25")
	public int bentouOrderNumber25;
	/** 弁当メニュー枠番２６注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_26")
	public int bentouOrderNumber26;
	/** 弁当メニュー枠番２７注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_27")
	public int bentouOrderNumber27;
	/** 弁当メニュー枠番２８注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_28")
	public int bentouOrderNumber28;
	/** 弁当メニュー枠番２９注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_29")
	public int bentouOrderNumber29;
	/** 弁当メニュー枠番３０注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_30")
	public int bentouOrderNumber30;
	/** 弁当メニュー枠番３１注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_31")
	public int bentouOrderNumber31;
	/** 弁当メニュー枠番３２注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_32")
	public int bentouOrderNumber32;
	/** 弁当メニュー枠番３３注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_33")
	public int bentouOrderNumber33;
	/** 弁当メニュー枠番３４注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_34")
	public int bentouOrderNumber34;
	/** 弁当メニュー枠番３５注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_35")
	public int bentouOrderNumber35;
	/** 弁当メニュー枠番３６注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_36")
	public int bentouOrderNumber36;
	/** 弁当メニュー枠番３７注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_37")
	public int bentouOrderNumber37;
	/** 弁当メニュー枠番３８注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_38")
	public int bentouOrderNumber38;
	/** 弁当メニュー枠番３９注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_39")
	public int bentouOrderNumber39;
	/** 弁当メニュー枠番４０注文数 */
	@Column(name = "BENTOU_ORDER_NUMBER_40")
	public int bentouOrderNumber40;
	/** 振休使用時間 */
	@Column(name = "HOL_TRANSFER_USE_TIME")
	public int holTransferTime;
	/** 欠勤使用時間 */
	@Column(name = "HOL_ABSENCE_USE_TIME")
	public int holAbsenceTime;
	/** 実働時間 */
	@Column(name = "LABOR_ACTUAL_TIME")
	public int laborActualTime;
	/** 総計算時間 */
	@Column(name = "LABOR_TOTAL_CALC_TIME")
	public int laborTotalCalcTime;
	/** 計算差異時間 */
	@Column(name = "LABOR_CALC_DIFF_TIME")
	public int laborCalcDiffTime;

	/** 総労働時間：残業時間：集計残業時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrOverTime> krcdtAnpAggrOverTimes;
	/** 総労働時間：休出・代休：集計休出時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrHdwkTime> krcdtAnpAggrHdwkTimes;
	/** 縦計：勤務日数：集計欠勤日数 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpDaysAbsence> krcdtAnpAggrAbsnDays;
	/** 縦計：勤務日数：集計特定日数 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrSpecDays> krcdtAnpAggrSpecDays;
	/** 縦計：勤務日数：集計特別休暇日数 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrSpvcDays> krcdtAnpAggrSpvcDays;
	/** 縦計：勤務時間：集計加給時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrBnspyTime> krcdtAnpAggrBnspyTime;
	/** 縦計：勤務時間：集計乖離時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrDivgTime> krcdtAnpAggrDivgTime;
	/** 縦計：勤務時間：集計外出 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpTimeGoout> krcdtAnpAggrGoout;
	/** 縦計：勤務時間：集計割増時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAggrPremTime> krcdtAnpAggrPremTime;
	/** 縦計：勤務時間：月別実績の医療時間 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpMedicalTime> krcdtAnpMedicalTime;
	/** 時間外超過：時間外超過 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpExcoutTime> krcdtAnpExcoutTime;
	/** 回数集計：回数集計 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpTimeTotalcount> krcdtAnpTotalTimes;
	/** 任意項目：任意項目値 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtAnpAttendanceTime", orphanRemoval = true)
	public List<KrcdtAnpAnyItemValue> krcdtAnpAnyItemValue;
	
	/**
	 * キー取得
	 */
	@Override
	protected Object getKey() {
		return this.PK;
	}
	
	/**
	 * ドメインに変換
	 * @return 任意期間別実績の勤怠時間
	 */
	public AttendanceTimeOfAnyPeriod toDomain(){
	
		// 期間別のフレックス時間
		val flexTime = FlexTimeByPeriod.of(
				new AttendanceTimeMonthWithMinus(this.flexTime),
				new AttendanceTimeMonth(this.flexExcessTime),
				new AttendanceTimeMonth(this.flexShortageTime),
				new AttendanceTimeMonth(this.beforeFlexTime));
		
		// 月別実績の就業時間
		val workTime = WorkTimeOfMonthly.of(
				new AttendanceTimeMonth(this.workTime),
				new AttendanceTimeMonth(this.prescribedPremiumTime),
				new AttendanceTimeMonth(this.actualWorkTime));
		
		// 月別実績の残業時間
		val overTime = OverTimeOfMonthly.of(
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalOverTime),
						new AttendanceTimeMonth(this.calcTotalOverTime)),
				new AttendanceTimeMonth(this.beforeOverTime),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalTransferOverTime),
						new AttendanceTimeMonth(this.calcTotalTransferOverTime)),
				this.krcdtAnpAggrOverTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
		
		// 月別実績の休出時間
		val holidayWorkTime = HolidayWorkTimeOfMonthly.of(
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalHolidayWorkTime),
						new AttendanceTimeMonth(this.calcTotalHolidayWorkTime)),
				new AttendanceTimeMonth(this.beforeHolidayWorkTime),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalTransferHdwkTime),
						new AttendanceTimeMonth(this.calcTotalTransferHdwkTime)),
				this.krcdtAnpAggrHdwkTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList()));

		// 月別実績の休暇使用時間
		val vacationUseTime = VacationUseTimeOfMonthly.of(
				AnnualLeaveUseTimeOfMonthly.of(new AttendanceTimeMonth(this.annualLeaveUseTime)),
				RetentionYearlyUseTimeOfMonthly.of(new AttendanceTimeMonth(this.reserveLeaveUseTime)),
				SpecialHolidayUseTimeOfMonthly.of(new AttendanceTimeMonth(this.specialLeaveUseTime)),
				CompensatoryLeaveUseTimeOfMonthly.of(new AttendanceTimeMonth(this.compensatoryLeaveUseTime)));
		
		// 月別実績の所定労働時間
		val prescribedWorkingTime = PrescribedWorkingTimeOfMonthly.of(
				new AttendanceTimeMonth(this.schedulePrescribedWorkTime),
				new AttendanceTimeMonth(this.recordPrescribedWorkTime));
		
		// 期間別の総労働時間
		val totalWorkingTime = TotalWorkingTimeByPeriod.of(
				workTime,
				overTime,
				holidayWorkTime,
				vacationUseTime,
				prescribedWorkingTime);
		
		// 期間別の総拘束時間
		val totalSpentTime = AggregateTotalTimeSpentAtWork.of(
				new AttendanceTimeMonth(this.spentOverTime),
				new AttendanceTimeMonth(this.spentMidnightTime),
				new AttendanceTimeMonth(this.spentHolidayTime),
				new AttendanceTimeMonth(this.spentVarienceTime),
				new AttendanceTimeMonth(this.totalSpentTime));
		
		// 期間別の36協定時間
		val agreementTime = AgreementTimeByPeriod.of(
				 new AttendanceTimeMonth(this.agreementTime));
		
		// 期間別の月の計算
		val monthlyCalculation = MonthlyCalculationByPeriod.of(
				totalWorkingTime,
				flexTime,
				new AttendanceTimeMonth(this.totalWorkingTime),
				totalSpentTime);

		// 月別実績の休業
		List<AggregateLeaveDays> fixLeaveDaysList = new ArrayList<>();
		List<AnyLeave> anyLeaveDaysList = new ArrayList<>();
		if (this.vtPrenatalLeaveDays != 0.0){
			fixLeaveDaysList.add(AggregateLeaveDays.of(
					CloseAtr.PRENATAL, new AttendanceDaysMonth(this.vtPrenatalLeaveDays)));
		}
		if (this.vtPostpartumLeaveDays != 0.0){
			fixLeaveDaysList.add(AggregateLeaveDays.of(
					CloseAtr.POSTPARTUM, new AttendanceDaysMonth(this.vtPostpartumLeaveDays)));
		}
		if (this.vtChildcareLeaveDays != 0.0){
			fixLeaveDaysList.add(AggregateLeaveDays.of(
					CloseAtr.CHILD_CARE, new AttendanceDaysMonth(this.vtChildcareLeaveDays)));
		}
		if (this.vtCareLeaveDays != 0.0){
			fixLeaveDaysList.add(AggregateLeaveDays.of(
					CloseAtr.CARE, new AttendanceDaysMonth(this.vtCareLeaveDays)));
		}
		if (this.vtInjuryOrIllnessLeaveDays != 0.0){
			fixLeaveDaysList.add(AggregateLeaveDays.of(
					CloseAtr.INJURY_OR_ILLNESS, new AttendanceDaysMonth(this.vtInjuryOrIllnessLeaveDays)));
		}
		if (this.vtAnyLeaveDays01 != 0.0){
			anyLeaveDaysList.add(AnyLeave.of(1, new AttendanceDaysMonth(this.vtAnyLeaveDays01)));
		}
		if (this.vtAnyLeaveDays02 != 0.0){
			anyLeaveDaysList.add(AnyLeave.of(2, new AttendanceDaysMonth(this.vtAnyLeaveDays02)));
		}
		if (this.vtAnyLeaveDays03 != 0.0){
			anyLeaveDaysList.add(AnyLeave.of(3, new AttendanceDaysMonth(this.vtAnyLeaveDays03)));
		}
		if (this.vtAnyLeaveDays04 != 0.0){
			anyLeaveDaysList.add(AnyLeave.of(4, new AttendanceDaysMonth(this.vtAnyLeaveDays04)));
		}
		
		// 月別実績の勤務日数
		val vtWorkDays = WorkDaysOfMonthly.of(
				AttendanceDaysOfMonthly.of(new AttendanceDaysMonth(this.vtAttendanceDays)),
				AbsenceDaysOfMonthly.of(
						new AttendanceDaysMonth(this.vtTotalAbsenceDays),
						new AttendanceTimeMonth(this.vtTotalAbsenceTime),
						this.krcdtAnpAggrAbsnDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				PredeterminedDaysOfMonthly.of(
						new AttendanceDaysMonth(this.vtPredetermineDays)),
				WorkDaysDetailOfMonthly.of(new AttendanceDaysMonth(this.vtWorkDays)),
				HolidayDaysOfMonthly.of(new AttendanceDaysMonth(this.vtHolidayDays)),
				SpecificDaysOfMonthly.of(
						this.krcdtAnpAggrSpecDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				HolidayWorkDaysOfMonthly.of(new AttendanceDaysMonth(this.vtHolidayWorkDays)),
				StgGoStgBackDaysOfMonthly.of(
						new AttendanceDaysMonth(this.straightGoDays),
						new AttendanceDaysMonth(this.straightBackDays),
						new AttendanceDaysMonth(this.straightGoBackDays)),
				WorkTimesOfMonthly.of(new AttendanceTimesMonth(this.vtWorkTimes)),
				TwoTimesWorkTimesOfMonthly.of(new AttendanceTimesMonth(this.vtTwoTimesWorkTimes)),
				TemporaryWorkTimesOfMonthly.of(
						new AttendanceTimesMonth(this.vtTemporaryWorkTimes),
						new AttendanceTimeMonth(this.vtTemporaryWorkTime)),
				LeaveOfMonthly.of(
						fixLeaveDaysList,
						anyLeaveDaysList),
				RecruitmentDaysOfMonthly.of(new AttendanceDaysMonth(this.vtRecruitDays)),
				SpcVacationDaysOfMonthly.of(
						new AttendanceDaysMonth(this.vtTotalSpecialVacationDays),
						new AttendanceTimeMonth(this.vtTotalSpecialVacationTime),
						this.krcdtAnpAggrSpvcDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				TimeConsumpVacationDaysOfMonthly.of(
						new AttendanceDaysMonth(this.timeDigestDays),
						new AttendanceTimeMonth(this.timeDigestTime)));
		
		// 育児外出
		List<GoOutForChildCare> goOutForChildCares = new ArrayList<>();
		if (this.vtChildcareGoOutTimes != 0 || this.vtChildcareGoOutTime != 0){
			goOutForChildCares.add(GoOutForChildCare.of(
					ChildCareAtr.CHILD_CARE,
					new AttendanceTimesMonth(this.vtChildcareGoOutTimes),
					new AttendanceTimeMonth(this.vtChildcareGoOutTime),
					new AttendanceTimeMonth(this.vtChildcareGoOutWithinTime),
					new AttendanceTimeMonth(this.vtChildcareGoOutExcessTime)));
		}
		if (this.vtCareGoOutTimes != 0 || this.vtCareGoOutTime != 0){
			goOutForChildCares.add(GoOutForChildCare.of(
					ChildCareAtr.CARE,
					new AttendanceTimesMonth(this.vtCareGoOutTimes),
					new AttendanceTimeMonth(this.vtCareGoOutTime),
					new AttendanceTimeMonth(this.vtCareGoOutWithinTime),
					new AttendanceTimeMonth(this.vtCareGoOutExcessTime)));
		}
		
		// 月別実績の勤務時間
		val vtWorkTime = WorkTimeOfMonthlyVT.of(
				BonusPayTimeOfMonthly.of(
						this.krcdtAnpAggrBnspyTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				GoOutOfMonthly.of(
						this.krcdtAnpAggrGoout.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
						goOutForChildCares),
				PremiumTimeOfMonthly.of(
						this.krcdtAnpAggrPremTime.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
						new AttendanceAmountMonth(this.premiumAmountTotal)),
				BreakTimeOfMonthly.of(
						new AttendanceTimesMonth(this.vtBreakTimes), 
						new AttendanceTimeMonth(this.vtBreakTime),
						new AttendanceTimeMonth(this.vtBreakWithinTime),
						new AttendanceTimeMonth(this.vtBreakWithinDeducTime),
						new AttendanceTimeMonth(this.vtBreakExcessTime),
						new AttendanceTimeMonth(this.vtBreakExcessDeducTime)),
				bentou(),
				MidnightTimeOfMonthly.of(
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.vtOverWorkMidnightTime),
								new AttendanceTimeMonth(this.vtCalcOverWorkMidnightTime)),
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.vtLegalMidnightTime),
								new AttendanceTimeMonth(this.vtCalcLegalMidnightTime)),
						IllegalMidnightTime.of(
								new TimeMonthWithCalculation(
										new AttendanceTimeMonth(this.vtIllegalMidnightTime),
										new AttendanceTimeMonth(this.vtCalcIllegalMidnightTime)),
								new AttendanceTimeMonth(this.vtIllegalBeforeMidnightTime)),
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.vtLegalHolidayWorkMidnightTime),
								new AttendanceTimeMonth(this.vtCalcLegalHolidayWorkMidnightTime)),
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.vtIllegalHolidayWorkMidnightTime),
								new AttendanceTimeMonth(this.vtCalcIllegalHolidayWorkMidnightTime)),
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.vtSpecialHolidayWorkMidnightTime),
								new AttendanceTimeMonth(this.vtCalcSpecialHolidayWorkMidnightTime))),
				LateLeaveEarlyOfMonthly.of(
						LeaveEarly.of(
								new AttendanceTimesMonth(this.vtLeaveEarlyTimes),
								new TimeMonthWithCalculation(
										new AttendanceTimeMonth(this.vtLeaveEarlyTime),
										new AttendanceTimeMonth(this.vtCalcLeaveEarlyTime))),
						Late.of(
								new AttendanceTimesMonth(this.vtLateTimes),
								new TimeMonthWithCalculation(
										new AttendanceTimeMonth(this.vtLateTime),
										new AttendanceTimeMonth(this.vtCalcLateTime)))),
				AttendanceLeaveGateTimeOfMonthly.of(
						new AttendanceTimeMonth(this.vtAttendanceLeaveGateBeforeAttendanceTime),
						new AttendanceTimeMonth(this.vtAttendanceLeaveGateAfterLeaveWorkTime),
						new AttendanceTimeMonth(this.vtAttendanceLeaveGateStayingTime),
						new AttendanceTimeMonth(this.vtAttendanceLeaveGateUnemployedTime)),
				BudgetTimeVarienceOfMonthly.of(new AttendanceTimeMonthWithMinus(this.vtBudgetVarienceTime)),
				DivergenceTimeOfMonthly.of(
						this.krcdtAnpAggrDivgTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				this.krcdtAnpMedicalTime.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
				TopPageDisplayOfMonthly.of(
						new AttendanceTimeMonth(this.topPageOtTime), 
						new AttendanceTimeMonth(this.topPageHolWorkTime), 
						new AttendanceTimeMonthWithMinus(this.topPageFlexTime)), 
				IntervalTimeOfMonthly.of(
						new AttendanceTimeMonth(this.intervalTime),
						new AttendanceTimeMonth(this.intervalDeductTime)),
				HolidayUsageOfMonthly.of(
						new AttendanceTimeMonth(this.holTransferTime), 
						new AttendanceTimeMonth(this.holAbsenceTime)),
				LaborTimeOfMonthly.of(
						new AttendanceTimeMonth(this.laborActualTime), 
						new AttendanceTimeMonth(this.laborTotalCalcTime), 
						new AttendanceTimeMonth(this.laborCalcDiffTime)));

		// 月別実績の勤務時刻
		val vtWorkClock = WorkClockOfMonthly.of(
				EndClockOfMonthly.of(
						new AttendanceTimesMonth(this.vtEndWorkTimes),
						new AttendanceTimeMonth(this.vtEndWorkTotalClock),
						new AttendanceTimeMonth(this.vtEndWorkAverageClock)),
				PCLogonOfMonthly.of(
						PCLogonClockOfMonthly.of(
								AggrPCLogonClock.of(
										new AttendanceDaysMonth(this.vtLogonTotalDays),
										new AttendanceTimeMonth(this.vtLogonTotalClock),
										new AttendanceTimeMonth(this.vtLogonAverageClock)),
								AggrPCLogonClock.of(
										new AttendanceDaysMonth(this.vtLogoffTotalDays),
										new AttendanceTimeMonth(this.vtLogoffTotalClock),
										new AttendanceTimeMonth(this.vtLogoffAverageClock))),
						PCLogonDivergenceOfMonthly.of(
								AggrPCLogonDivergence.of(
										new AttendanceDaysMonth(this.vtLogonDivDays),
										new AttendanceTimeMonth(this.vtLogonDivTotalTime),
										new AttendanceTimeMonth(this.vtLogonDivAverageTime)),
								AggrPCLogonDivergence.of(
										new AttendanceDaysMonth(this.vtLogoffDivDays),
										new AttendanceTimeMonth(this.vtLogoffDivTotalTime),
										new AttendanceTimeMonth(this.vtLogoffDivAverageTime)))));
		
		// 期間別の縦計
		val verticalTotal = VerticalTotalOfMonthly.of(
				vtWorkDays,
				vtWorkTime,
				vtWorkClock,
				WorkAmountOfMonthly.of(new AttendanceAmountMonth(this.workTimeAmount)));
		
		return AttendanceTimeOfAnyPeriod.of(
				this.PK.employeeId,
				new AnyAggrFrameCode(this.PK.frameCode),
				monthlyCalculation,
				ExcessOutsideByPeriod.of(
						this.krcdtAnpExcoutTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				agreementTime,
				verticalTotal,
				TotalCountByPeriod.of(
						this.krcdtAnpTotalTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				AnyItemByPeriod.of(
						this.krcdtAnpAnyItemValue.stream().map(c -> c.toDomain()).collect(Collectors.toList())));
	}
	
	/**
	 * ドメインから変換　（for Insert）
	 * @param domain 任意期間別実績の勤怠時間
	 */
	public void fromDomainForPersist(AttendanceTimeOfAnyPeriod domain){
		
		this.PK = new KrcdtAnpAttendanceTimePK(
				domain.getEmployeeId(),
				domain.getAnyAggrFrameCode().v());
		this.fromDomainForUpdate(domain);
	}
	
	/**
	 * ドメインから変換　(for Update)
	 * @param domain 任意期間別実績の勤怠時間
	 */
	public void fromDomainForUpdate(AttendanceTimeOfAnyPeriod domain){

		this.workTimeAmount = domain.getVerticalTotal().getWorkAmount().getWorkTimeAmount().v();
		this.premiumAmountTotal = domain.getVerticalTotal().getWorkTime().getPremiumTime().getPremiumAmountTotal().v();
		
		val monthlyCalculation = domain.getMonthlyAggregation();
		this.totalWorkingTime = monthlyCalculation.getTotalWorkingTime().v();
		
		val flexTime = monthlyCalculation.getFlexTime();
		this.flexTime = flexTime.getFlexTime().v();
		this.flexExcessTime = flexTime.getFlexExcessTime().v();
		this.flexShortageTime = flexTime.getFlexShortageTime().v();
		this.beforeFlexTime = flexTime.getBeforeFlexTime().v();
		
		val aggregateTime = monthlyCalculation.getAggregateTime();
		val workTime = aggregateTime.getWorkTime();
		this.workTime = workTime.getWorkTime().v();
		this.actualWorkTime = workTime.getActualWorkTime().v();
		this.prescribedPremiumTime = workTime.getWithinPrescribedPremiumTime().v();
		
		val overTime = aggregateTime.getOverTime();
		this.totalOverTime = overTime.getTotalOverTime().getTime().v();
		this.calcTotalOverTime = overTime.getTotalOverTime().getCalcTime().v();
		this.beforeOverTime = overTime.getBeforeOverTime().v();
		this.totalTransferOverTime = overTime.getTotalTransferOverTime().getTime().v();
		this.calcTotalTransferOverTime = overTime.getTotalTransferOverTime().getCalcTime().v();
		
		val holidayWorkTime = aggregateTime.getHolidayWorkTime();
		this.totalHolidayWorkTime = holidayWorkTime.getTotalHolidayWorkTime().getTime().v();
		this.calcTotalHolidayWorkTime = holidayWorkTime.getTotalHolidayWorkTime().getCalcTime().v();
		this.beforeHolidayWorkTime = holidayWorkTime.getBeforeHolidayWorkTime().v();
		this.totalTransferHdwkTime = holidayWorkTime.getTotalTransferTime().getTime().v();
		this.calcTotalTransferHdwkTime = holidayWorkTime.getTotalTransferTime().getCalcTime().v();
		
		val vacationUseTime = aggregateTime.getVacationUseTime();
		this.annualLeaveUseTime = vacationUseTime.getAnnualLeave().getUseTime().v();
		this.reserveLeaveUseTime = vacationUseTime.getRetentionYearly().getUseTime().v();
		this.specialLeaveUseTime = vacationUseTime.getSpecialHoliday().getUseTime().v();
		this.compensatoryLeaveUseTime = vacationUseTime.getCompensatoryLeave().getUseTime().v();
		
		val prescribedWorkingTime = aggregateTime.getPrescribedWorkingTime();
		this.schedulePrescribedWorkTime = prescribedWorkingTime.getSchedulePrescribedWorkingTime().v();
		this.recordPrescribedWorkTime = prescribedWorkingTime.getRecordPrescribedWorkingTime().v();
		
		val totalSpentTime = monthlyCalculation.getTotalSpentTime();
		this.spentOverTime = totalSpentTime.getOverTimeSpentAtWork().v();
		this.spentMidnightTime = totalSpentTime.getMidnightTimeSpentAtWork().v();
		this.spentHolidayTime = totalSpentTime.getHolidayTimeSpentAtWork().v();
		this.spentVarienceTime = totalSpentTime.getVarienceTimeSpentAtWork().v();
		this.totalSpentTime = totalSpentTime.getTotalTimeSpentAtWork().v();
		
		val agreementTime = domain.getAgreementTime();
		this.agreementTime = agreementTime.getAgreementTime().v();
		
		val verticalTotal = domain.getVerticalTotal();
		val vtWorkDays = verticalTotal.getWorkDays();
		this.vtWorkDays = vtWorkDays.getWorkDays().getDays().v();
		this.vtWorkTimes = vtWorkDays.getWorkTimes().getTimes().v();
		this.vtTwoTimesWorkTimes = vtWorkDays.getTwoTimesWorkTimes().getTimes().v();
		this.vtTemporaryWorkTimes = vtWorkDays.getTemporaryWorkTimes().getTimes().v();
		this.vtTemporaryWorkTime = vtWorkDays.getTemporaryWorkTimes().getTime().v();
		this.vtPredetermineDays = vtWorkDays.getPredetermineDays().getPredeterminedDays().v();
		this.vtHolidayDays = vtWorkDays.getHolidayDays().getDays().v();
		this.vtAttendanceDays = vtWorkDays.getAttendanceDays().getDays().v();
		this.vtHolidayWorkDays = vtWorkDays.getHolidayWorkDays().getDays().v();
		this.vtTotalAbsenceDays = vtWorkDays.getAbsenceDays().getTotalAbsenceDays().v();
		this.vtTotalAbsenceTime = vtWorkDays.getAbsenceDays().getTotalAbsenceTime().v();
		this.vtRecruitDays = vtWorkDays.getRecruitmentDays().getDays().v();
		this.vtTotalSpecialVacationDays = vtWorkDays.getSpecialVacationDays().getTotalSpcVacationDays().v();
		this.vtTotalSpecialVacationTime = vtWorkDays.getSpecialVacationDays().getTotalSpcVacationTime().v();
		
		this.straightGoDays = vtWorkDays.getStraightDays().getStraightGo().v();
		this.straightBackDays = vtWorkDays.getStraightDays().getStraightBack().v();
		this.straightGoBackDays = vtWorkDays.getStraightDays().getStraightGoStraightBack().v();
		
		this.timeDigestDays = vtWorkDays.getTimeConsumpDays().getDays().v();
		this.timeDigestTime = vtWorkDays.getTimeConsumpDays().getTime().valueAsMinutes();
		
		val leave = vtWorkDays.getLeave();
		this.vtPrenatalLeaveDays = 0.0;
		this.vtPostpartumLeaveDays = 0.0;
		this.vtChildcareLeaveDays = 0.0;
		this.vtCareLeaveDays = 0.0;
		this.vtInjuryOrIllnessLeaveDays = 0.0;
		this.vtAnyLeaveDays01 = 0.0;
		this.vtAnyLeaveDays02 = 0.0;
		this.vtAnyLeaveDays03 = 0.0;
		this.vtAnyLeaveDays04 = 0.0;
		val fixLeaveDaysMap = leave.getFixLeaveDays();
		if (fixLeaveDaysMap.containsKey(CloseAtr.PRENATAL)){
			this.vtPrenatalLeaveDays = fixLeaveDaysMap.get(CloseAtr.PRENATAL).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.POSTPARTUM)){
			this.vtPostpartumLeaveDays = fixLeaveDaysMap.get(CloseAtr.POSTPARTUM).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.CHILD_CARE)){
			this.vtChildcareLeaveDays = fixLeaveDaysMap.get(CloseAtr.CHILD_CARE).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.CARE)){
			this.vtCareLeaveDays = fixLeaveDaysMap.get(CloseAtr.CARE).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.INJURY_OR_ILLNESS)){
			this.vtInjuryOrIllnessLeaveDays = fixLeaveDaysMap.get(CloseAtr.INJURY_OR_ILLNESS).getDays().v();
		}
		val anyLeaveDaysMap = leave.getAnyLeaveDays();
		if (anyLeaveDaysMap.containsKey(1)){
			this.vtAnyLeaveDays01 = anyLeaveDaysMap.get(1).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(2)){
			this.vtAnyLeaveDays02 = anyLeaveDaysMap.get(2).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(3)){
			this.vtAnyLeaveDays03 = anyLeaveDaysMap.get(3).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(4)){
			this.vtAnyLeaveDays04 = anyLeaveDaysMap.get(4).getDays().v();
		}
		
		val vtWorkTime = verticalTotal.getWorkTime();
		
		bentou(vtWorkTime.getReservation());
		
		this.vtChildcareGoOutTimes = 0;
		this.vtChildcareGoOutTime = 0;
		this.vtChildcareGoOutWithinTime = 0;
		this.vtChildcareGoOutExcessTime = 0;
		this.vtCareGoOutTimes = 0;
		this.vtCareGoOutTime = 0;
		this.vtCareGoOutWithinTime = 0;
		this.vtCareGoOutExcessTime = 0;
		val goOutForChildCares = vtWorkTime.getGoOut().getGoOutForChildCares();
		if (goOutForChildCares.containsKey(ChildCareAtr.CHILD_CARE)){
			val goOutForChildCare = goOutForChildCares.get(ChildCareAtr.CHILD_CARE);
			this.vtChildcareGoOutTimes = goOutForChildCare.getTimes().v();
			this.vtChildcareGoOutTime = goOutForChildCare.getTime().v();
			this.vtChildcareGoOutWithinTime = goOutForChildCare.getWithinTime().valueAsMinutes();
			this.vtChildcareGoOutExcessTime = goOutForChildCare.getExcessTime().valueAsMinutes();
		}
		if (goOutForChildCares.containsKey(ChildCareAtr.CARE)){
			val goOutForCare = goOutForChildCares.get(ChildCareAtr.CARE);
			this.vtCareGoOutTimes = goOutForCare.getTimes().v();
			this.vtCareGoOutTime = goOutForCare.getTime().v();
			this.vtCareGoOutWithinTime = goOutForCare.getWithinTime().valueAsMinutes();
			this.vtCareGoOutExcessTime = goOutForCare.getExcessTime().valueAsMinutes();
		}
		
		this.vtBreakExcessDeducTime = vtWorkTime.getBreakTime().getExcessDeductionTime().valueAsMinutes();
		this.vtBreakExcessTime = vtWorkTime.getBreakTime().getExcessTime().valueAsMinutes();
		this.vtBreakTimes = vtWorkTime.getBreakTime().getBreakTimes().v();
		this.vtBreakWithinDeducTime = vtWorkTime.getBreakTime().getWithinDeductionTime().valueAsMinutes();
		this.vtBreakWithinTime = vtWorkTime.getBreakTime().getWithinTime().valueAsMinutes();
		this.vtBreakTime = vtWorkTime.getBreakTime().getBreakTime().v();
		
		this.vtOverWorkMidnightTime = vtWorkTime.getMidnightTime().getOverWorkMidnightTime().getTime().v();
		this.vtCalcOverWorkMidnightTime = vtWorkTime.getMidnightTime().getOverWorkMidnightTime().getCalcTime().v();
		this.vtLegalMidnightTime = vtWorkTime.getMidnightTime().getLegalMidnightTime().getTime().v();
		this.vtCalcLegalMidnightTime = vtWorkTime.getMidnightTime().getLegalMidnightTime().getCalcTime().v();
		this.vtIllegalMidnightTime = vtWorkTime.getMidnightTime().getIllegalMidnightTime().getTime().getTime().v();
		this.vtCalcIllegalMidnightTime = vtWorkTime.getMidnightTime().getIllegalMidnightTime().getTime().getCalcTime().v();
		this.vtIllegalBeforeMidnightTime = vtWorkTime.getMidnightTime().getIllegalMidnightTime().getBeforeTime().v();
		this.vtLegalHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getLegalHolidayWorkMidnightTime().getTime().v();
		this.vtCalcLegalHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getLegalHolidayWorkMidnightTime().getCalcTime().v();
		this.vtIllegalHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getIllegalHolidayWorkMidnightTime().getTime().v();
		this.vtCalcIllegalHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getIllegalHolidayWorkMidnightTime().getCalcTime().v();
		this.vtSpecialHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getSpecialHolidayWorkMidnightTime().getTime().v();
		this.vtCalcSpecialHolidayWorkMidnightTime = vtWorkTime.getMidnightTime().getSpecialHolidayWorkMidnightTime().getCalcTime().v();
		this.vtLateTimes = vtWorkTime.getLateLeaveEarly().getLate().getTimes().v();
		this.vtLateTime = vtWorkTime.getLateLeaveEarly().getLate().getTime().getTime().v();
		this.vtCalcLateTime = vtWorkTime.getLateLeaveEarly().getLate().getTime().getCalcTime().v();
		this.vtLeaveEarlyTimes = vtWorkTime.getLateLeaveEarly().getLeaveEarly().getTimes().v();
		this.vtLeaveEarlyTime = vtWorkTime.getLateLeaveEarly().getLeaveEarly().getTime().getTime().v();
		this.vtCalcLeaveEarlyTime = vtWorkTime.getLateLeaveEarly().getLeaveEarly().getTime().getCalcTime().v();
		this.vtAttendanceLeaveGateBeforeAttendanceTime = vtWorkTime.getAttendanceLeaveGateTime().getTimeBeforeAttendance().v();
		this.vtAttendanceLeaveGateAfterLeaveWorkTime = vtWorkTime.getAttendanceLeaveGateTime().getTimeAfterLeaveWork().v();
		this.vtAttendanceLeaveGateStayingTime = vtWorkTime.getAttendanceLeaveGateTime().getStayingTime().v();
		this.vtAttendanceLeaveGateUnemployedTime = vtWorkTime.getAttendanceLeaveGateTime().getUnemployedTime().v();
		this.vtBudgetVarienceTime = vtWorkTime.getBudgetTimeVarience().getTime().v();
		
		this.topPageOtTime = vtWorkTime.getTopPage().getOvertime().valueAsMinutes();
		this.topPageHolWorkTime = vtWorkTime.getTopPage().getHolidayWork().valueAsMinutes();
		this.topPageFlexTime = vtWorkTime.getTopPage().getFlex().valueAsMinutes();
		
		this.intervalTime = vtWorkTime.getInterval().getTime().valueAsMinutes();
		this.intervalDeductTime = vtWorkTime.getInterval().getExemptionTime().valueAsMinutes();
		
		/** 休暇使用時間 */
		this.holAbsenceTime = vtWorkTime.getHolidayUseTime().getAbsence().valueAsMinutes();
		this.holTransferTime = vtWorkTime.getHolidayUseTime().getTransferHoliday().valueAsMinutes();
		
		/** 労働時間 */
		this.laborActualTime = vtWorkTime.getLaborTime().getActualWorkTime().valueAsMinutes();
		this.laborCalcDiffTime = vtWorkTime.getLaborTime().getCalcDiffTime().valueAsMinutes();
		this.laborTotalCalcTime = vtWorkTime.getLaborTime().getTotalCalcTime().valueAsMinutes();

		val vtWorkClock = verticalTotal.getWorkClock();
		val endClock = vtWorkClock.getEndClock();
		val logonClock = vtWorkClock.getLogonInfo().getLogonClock();
		val logonDiv = vtWorkClock.getLogonInfo().getLogonDivergence();
		this.vtEndWorkTimes = endClock.getTimes().v();
		this.vtEndWorkTotalClock = endClock.getTotalClock().v();
		this.vtEndWorkAverageClock = endClock.getAverageClock().v();
		this.vtLogonTotalDays = logonClock.getLogonClock().getTotalDays().v();
		this.vtLogonTotalClock = logonClock.getLogonClock().getTotalClock().v();
		this.vtLogonAverageClock = logonClock.getLogonClock().getAverageClock().v();
		this.vtLogoffTotalDays = logonClock.getLogoffClock().getTotalDays().v();
		this.vtLogoffTotalClock = logonClock.getLogoffClock().getTotalClock().v();
		this.vtLogoffAverageClock = logonClock.getLogoffClock().getAverageClock().v();
		this.vtLogonDivDays = logonDiv.getLogonDivergence().getDays().v();
		this.vtLogonDivTotalTime = logonDiv.getLogonDivergence().getTotalTime().v();
		this.vtLogonDivAverageTime = logonDiv.getLogonDivergence().getAverageTime().v();
		this.vtLogoffDivDays = logonDiv.getLogoffDivergence().getDays().v();
		this.vtLogoffDivTotalTime = logonDiv.getLogoffDivergence().getTotalTime().v();
		this.vtLogoffDivAverageTime = logonDiv.getLogoffDivergence().getAverageTime().v();
	}
	
	private ReservationOfMonthly bentou() {
		List<ReservationDetailOfMonthly> order = new ArrayList<>();
		for (int i = 1; i <= 40; i++) {
			val number = FieldReflection.getField(this.getClass(), "bentouOrderNumber" + i);
			
			order.add(ReservationDetailOfMonthly.of(i, ReflectionUtil.getFieldValue(number, this)));
		}
		
		return ReservationOfMonthly.of(
				new OrderAmountMonthly(this.bentouOrderAmount1), 
				new OrderAmountMonthly(this.bentouOrderAmount2), 
				order);
	}

	private void bentou(ReservationOfMonthly reservation) {
		this.bentouOrderAmount1 = reservation.getAmount1().v();
		this.bentouOrderAmount2 = reservation.getAmount2().v();
		
		for (val data : reservation.getOrders()) {
			val number = FieldReflection.getField(this.getClass(), "bentouOrderNumber" + data.getFrameNo());
			ReflectionUtil.setFieldValue(number, this, data.getOrder().v());
		}
	}
}