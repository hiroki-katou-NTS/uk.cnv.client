package nts.uk.ctx.at.record.infra.entity.weekly;

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
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.reflection.FieldReflection;
import nts.gul.reflection.ReflectionUtil;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekDaysAbsence;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekAggrSpecDays;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekAggrSpvcDays;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeBonusPay;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeDvgc;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeGoout;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekAggrPremTime;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekMedicalTime;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.common.times.AttendanceTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AnyItemByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.ExcessOutsideByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.FlexTimeByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.AggregateTotalTimeSpentAtWork;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.totalworkingtime.AggregateTotalWorkingTime;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.RegAndIrgTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.WeeklyCalculation;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.worktype.CloseAtr;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * ???????????????????????????
 * @author shuichu_ishida
 */
@Entity
@Table(name = "KRCDT_WEK_ATTENDANCE_TIME")
@NoArgsConstructor
public class KrcdtWekAttendanceTime extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ????????????????????? */
	@EmbeddedId
	public KrcdtWekAttendanceTimePK PK;
	
	/** ??????????????? */
	@Column(name = "START_YMD")
	public GeneralDate startYmd;
	/** ??????????????? */
	@Column(name = "END_YMD")
	public GeneralDate endYmd;
	/** ???????????? */
	@Column(name = "AGGREGATE_DAYS")
	public double aggregateDays;
	
	/** ?????????????????? */
	@Column(name = "WORK_TIME_AMOUNT")
	public long workTimeAmount;
	
	/** ?????????????????? */
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public long premiumAmountTotal;
	
	/** ????????????????????? */
	@Column(name = "WEEK_TOTAL_PREM_TIME")
	public int weekTotalPremiumTime;
	
	/** ????????????????????? */
	@Column(name = "FLEX_TIME")
	public int flexTime;
	/** ??????????????????????????? */
	@Column(name = "FLEX_EXCESS_TIME")
	public int flexExcessTime;
	/** ??????????????????????????? */
	@Column(name = "FLEX_SHORTAGE_TIME")
	public int flexShortageTime;
	/** ??????????????????????????? */
	@Column(name = "BEFORE_FLEX_TIME")
	public int beforeFlexTime;
	
	/** ???????????? */
	@Column(name = "WORK_TIME")
	public int workTime;
	/** ?????????????????? */
	@Column(name = "ACTWORK_TIME")
	public int actualWorkTime;
	/** ????????????????????? */
	@Column(name = "WITPRS_PREMIUM_TIME")
	public int prescribedPremiumTime;
	
	/** ???????????????????????? */
	@Column(name = "SCHE_PRS_WORK_TIME")
	public int schedulePrescribedWorkTime;
	/** ???????????????????????? */
	@Column(name = "RECD_PRS_WORK_TIME")
	public int recordPrescribedWorkTime;
	
	/** ?????????????????? */
	@Column(name = "TOTAL_OVER_TIME")
	public int totalOverTime;
	/** ???????????????????????? */
	@Column(name = "CALC_TOTAL_OVER_TIME")
	public int calcTotalOverTime;
	/** ?????????????????? */
	@Column(name = "BEFORE_OVER_TIME")
	public int beforeOverTime;
	/** ???????????????????????? */
	@Column(name = "TOTAL_TRNOVR_TIME")
	public int totalTransferOverTime;
	/** ?????????????????????????????? */
	@Column(name = "CALC_TOTAL_TRNOVR_TIME")
	public int calcTotalTransferOverTime;

	/** ?????????????????? */
	@Column(name = "TOTAL_HDWK_TIME")
	public int totalHolidayWorkTime;
	/** ???????????????????????? */
	@Column(name = "CALC_TOTAL_HDWK_TIME")
	public int calcTotalHolidayWorkTime;
	/** ?????????????????? */
	@Column(name = "BEFORE_HDWK_TIME")
	public int beforeHolidayWorkTime;
	/** ???????????????????????? */
	@Column(name = "TOTAL_TRNHDWK_TIME")
	public int totalTransferHdwkTime;
	/** ?????????????????????????????? */
	@Column(name = "CALC_TOTAL_TRNHDWK_TIME")
	public int calcTotalTransferHdwkTime;
	
	/** ?????????????????? */
	@Column(name = "ANNLEA_USE_TIME")
	public int annualLeaveUseTime;
	/** ???????????????????????? */
	@Column(name = "RSVLEA_USE_TIME")
	public int reserveLeaveUseTime;
	/** ???????????????????????? */
	@Column(name = "SPCLEA_USE_TIME")
	public int specialLeaveUseTime;
	/** ?????????????????? */
	@Column(name = "CMPLEA_USE_TIME")
	public int compensatoryLeaveUseTime;
	
	/** ?????????????????? */
	@Column(name = "SPENT_OVER_TIME")
	public int spentOverTime;
	/** ?????????????????? */
	@Column(name = "SPENT_MIDNIGHT_TIME")
	public int spentMidnightTime;
	/** ?????????????????? */
	@Column(name = "SPENT_HOLIDAY_TIME")
	public int spentHolidayTime;
	/** ?????????????????? */
	@Column(name = "SPENT_VARIENCE_TIME")
	public int spentVarienceTime;
	/** ??????????????? */
	@Column(name = "TOTAL_SPENT_TIME")
	public int totalSpentTime;
	
	/** ???????????? */
	@Column(name = "VT_WORK_DAYS")
	public double vtWorkDays;
	/** ???????????? */
	@Column(name = "VT_WORK_TIMES")
	public int vtWorkTimes;
	/** ?????????????????? */
	@Column(name = "VT_TWOTIMES_WORK_TIMES")
	public int vtTwoTimesWorkTimes;
	/** ?????????????????? */
	@Column(name = "VT_TEMPORARY_WORK_TIMES")
	public int vtTemporaryWorkTimes;
	/** ?????????????????? */
	@Column(name = "VT_TEMPORARY_WORK_TIME")
	public int vtTemporaryWorkTime;
	/** ???????????? */
	@Column(name = "VT_PREDET_DAYS")
	public double vtPredetermineDays;
	/** ???????????? */
	@Column(name = "VT_HOLIDAY_DAYS")
	public double vtHolidayDays;
	/** ???????????? */
	@Column(name = "VT_ATTENDANCE_DAYS")
	public double vtAttendanceDays;
	/** ???????????? */
	@Column(name = "VT_HOLIDAY_WORK_DAYS")
	public double vtHolidayWorkDays;
	/** ?????????????????? */
	@Column(name = "VT_TOTAL_ABSENCE_DAYS")
	public double vtTotalAbsenceDays;
	/** ?????????????????? */
	@Column(name = "VT_TOTAL_ABSENCE_TIME")
	public int vtTotalAbsenceTime;
	/** ???????????? */
	@Column(name = "VT_RECRUIT_DAYS")
	public double vtRecruitDays;
	/** ???????????????????????? */
	@Column(name = "VT_TOTAL_SPCVACT_DAYS")
	public double vtTotalSpecialVacationDays;
	/** ???????????????????????? */
	@Column(name = "VT_TOTAL_SPCVACT_TIME")
	public int vtTotalSpecialVacationTime;
	/** ???????????? */
	@Column(name = "STRAIGHT_GO_DAYS")
	public double straightGoDays;
	/** ???????????? */
	@Column(name = "STRAIGHT_BACK_DAYS")
	public double straightBackDays;
	/** ?????????????????? */
	@Column(name = "STRAIGHT_GO_BACK_DAYS")
	public double straightGoBackDays;
	/** ?????????????????? */
	@Column(name = "VT_PRENATAL_LEAVE_DAYS")
	public double vtPrenatalLeaveDays;
	/** ?????????????????? */
	@Column(name = "VT_POSTPARTUM_LEAVE_DAYS")
	public double vtPostpartumLeaveDays;
	/** ?????????????????? */
	@Column(name = "VT_CHILDCARE_LEAVE_DAYS")
	public double vtChildcareLeaveDays;
	/** ?????????????????? */
	@Column(name = "VT_CARE_LEAVE_DAYS")
	public double vtCareLeaveDays;
	/** ?????????????????? */
	@Column(name = "VT_INJILN_LEAVE_DAYS")
	public double vtInjuryOrIllnessLeaveDays;
	/** ??????????????????01 */
	@Column(name = "VT_ANY_LEAVE_DAYS_01")
	public double vtAnyLeaveDays01;
	/** ??????????????????02 */
	@Column(name = "VT_ANY_LEAVE_DAYS_02")
	public double vtAnyLeaveDays02;
	/** ??????????????????03 */
	@Column(name = "VT_ANY_LEAVE_DAYS_03")
	public double vtAnyLeaveDays03;
	/** ??????????????????04 */
	@Column(name = "VT_ANY_LEAVE_DAYS_04")
	public double vtAnyLeaveDays04;
	
	/** ?????????????????? */
	@Column(name = "VT_CLDCAR_GOOUT_TIMES")
	public int vtChildcareGoOutTimes;
	/** ?????????????????? */
	@Column(name = "VT_CLDCAR_GOOUT_TIME")
	public int vtChildcareGoOutTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CLDCAR_WITHIN_TIME")
	public int vtChildcareGoOutWithinTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CLDCAR_EXCESS_TIME")
	public int vtChildcareGoOutExcessTime;
	/** ?????????????????? */
	@Column(name = "VT_CARE_GOOUT_TIMES")
	public int vtCareGoOutTimes;
	/** ?????????????????? */
	@Column(name = "VT_CARE_GOOUT_TIME")
	public int vtCareGoOutTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CARE_WITHIN_TIME")
	public int vtCareGoOutWithinTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CARE_EXCESS_TIME")
	public int vtCareGoOutExcessTime;
	/** ???????????? */
	@Column(name = "VT_BREAK_TIME")
	public int vtBreakTime;
	/** ???????????? */
	@Column(name = "VT_BREAK_TIMES")
	public int vtBreakTimes;
	/** ????????????????????? */
	@Column(name = "VT_BREAK_WITHIN_TIME")
	public int vtBreakWithinTime;
	/** ????????????????????? */
	@Column(name = "VT_BREAK_WITHIN_DED_TIME")
	public int vtBreakWithinDeducTime;
	/** ????????????????????? */
	@Column(name = "VT_BREAK_EXCESS_TIME")
	public int vtBreakExcessTime;
	/** ????????????????????? */
	@Column(name = "VT_BREAK_EXCESS_DED_TIME")
	public int vtBreakExcessDeducTime;
	/** ?????????????????? */
	@Column(name = "VT_OVWK_MDNT_TIME")
	public int vtOverWorkMidnightTime;
	/** ???????????????????????? */
	@Column(name = "VT_CALC_OVWK_MDNT_TIME")
	public int vtCalcOverWorkMidnightTime;
	/** ????????????????????? */
	@Column(name = "VT_LGL_MDNT_TIME")
	public int vtLegalMidnightTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CALC_LGL_MDNT_TIME")
	public int vtCalcLegalMidnightTime;
	/** ????????????????????? */
	@Column(name = "VT_ILG_MDNT_TIME")
	public int vtIllegalMidnightTime;
	/** ??????????????????????????? */
	@Column(name = "VT_CALC_ILG_MDNT_TIME")
	public int vtCalcIllegalMidnightTime;
	/** ??????????????????????????? */
	@Column(name = "VT_ILG_BFR_MDNT_TIME")
	public int vtIllegalBeforeMidnightTime;
	/** ??????????????????????????? */
	@Column(name = "VT_LGL_HDWK_MDNT_TIME")
	public int vtLegalHolidayWorkMidnightTime;
	/** ????????????????????????????????? */
	@Column(name = "VT_CALC_LGL_HDWK_MN_TIME")
	public int vtCalcLegalHolidayWorkMidnightTime;
	/** ??????????????????????????? */
	@Column(name = "VT_ILG_HDWK_MDNT_TIME")
	public int vtIllegalHolidayWorkMidnightTime;
	/** ????????????????????????????????? */
	@Column(name = "VT_CALC_ILG_HDWK_MN_TIME")
	public int vtCalcIllegalHolidayWorkMidnightTime;
	/** ???????????????????????? */
	@Column(name = "VT_SPHD_HDWK_MDNT_TIME")
	public int vtSpecialHolidayWorkMidnightTime;
	/** ?????????????????????????????? */
	@Column(name = "VT_CALC_SPHD_HDWK_MN_TIME")
	public int vtCalcSpecialHolidayWorkMidnightTime;
	/** ???????????? */
	@Column(name = "VT_LATE_TIMES")
	public int vtLateTimes;
	/** ???????????? */
	@Column(name = "VT_LATE_TIME")
	public int vtLateTime;
	/** ?????????????????? */
	@Column(name = "VT_CALC_LATE_TIME")
	public int vtCalcLateTime;
	/** ???????????? */
	@Column(name = "VT_LEAVEEARLY_TIMES")
	public int vtLeaveEarlyTimes;
	/** ???????????? */
	@Column(name = "VT_LEAVEEARLY_TIME")
	public int vtLeaveEarlyTime;
	/** ?????????????????? */
	@Column(name = "VT_CALC_LEAVEEARLY_TIME")
	public int vtCalcLeaveEarlyTime;
	/** ???????????????????????? */
	@Column(name = "VT_ALGT_BFR_ATND_TIME")
	public int vtAttendanceLeaveGateBeforeAttendanceTime;
	/** ???????????????????????? */
	@Column(name = "VT_ALGT_AFT_LVWK_TIME")
	public int vtAttendanceLeaveGateAfterLeaveWorkTime;
	/** ????????????????????? */
	@Column(name = "VT_ALGT_STAYING_TIME")
	public int vtAttendanceLeaveGateStayingTime;
	/** ???????????????????????? */
	@Column(name = "VT_ALGT_UNEMPLOYED_TIME")
	public int vtAttendanceLeaveGateUnemployedTime;
	/** ?????????????????? */
	@Column(name = "VT_BUDGET_VARIENCE_TIME")
	public int vtBudgetVarienceTime;

	/** ???????????? */
	@Column(name = "VT_ENDWORK_TIMES")
	public int vtEndWorkTimes;
	/** ?????????????????? */
	@Column(name = "VT_ENDWORK_TOTAL_CLOCK")
	public int vtEndWorkTotalClock;
	/** ?????????????????? */
	@Column(name = "VT_ENDWORK_AVE_CLOCK")
	public int vtEndWorkAverageClock;
	/** ???????????????????????? */
	@Column(name = "VT_LOGON_TOTAL_DAYS")
	public double vtLogonTotalDays;
	/** ???????????????????????? */
	@Column(name = "VT_LOGON_TOTAL_CLOCK")
	public int vtLogonTotalClock;
	/** ???????????????????????? */
	@Column(name = "VT_LOGON_AVE_CLOCK")
	public int vtLogonAverageClock;
	/** ???????????????????????? */
	@Column(name = "VT_LOGOFF_TOTAL_DAYS")
	public double vtLogoffTotalDays;
	/** ???????????????????????? */
	@Column(name = "VT_LOGOFF_TOTAL_CLOCK")
	public int vtLogoffTotalClock;
	/** ???????????????????????? */
	@Column(name = "VT_LOGOFF_AVE_CLOCK")
	public int vtLogoffAverageClock;
	/** ???????????????????????? */
	@Column(name = "VT_LOGON_DIV_DAYS")
	public double vtLogonDivDays;
	/** ?????????????????????????????? */
	@Column(name = "VT_LOGON_DIV_TOTAL_TIME")
	public int vtLogonDivTotalTime;
	/** ?????????????????????????????? */
	@Column(name = "VT_LOGON_DIV_AVE_TIME")
	public int vtLogonDivAverageTime;
	/** ???????????????????????? */
	@Column(name = "VT_LOGOFF_DIV_DAYS")
	public double vtLogoffDivDays;
	/** ?????????????????????????????? */
	@Column(name = "VT_LOGOFF_DIV_TOTAL_TIME")
	public int vtLogoffDivTotalTime;
	/** ?????????????????????????????? */
	@Column(name = "VT_LOGOFF_DIV_AVE_TIME")
	public int vtLogoffDivAverageTime;
	
	/** ???????????????????????? */
	@Column(name = "VT_TIME_DIGEST_DAYS")
	public double timeDigestDays;
	/** ???????????????????????? */
	@Column(name = "VT_TIME_DIGEST_TIME")
	public int timeDigestTime;
	
	/** ????????????????????????????????????????????? */
	@Column(name = "TOP_PAGE_OT_TIME")
	public int topPageOtTime;
	/** ??????????????????????????????????????????????????? */
	@Column(name = "TOP_PAGE_HOL_WORK_TIME")
	public int topPageHolWorkTime;
	/** ?????????????????????????????????????????????????????? */
	@Column(name = "TOP_PAGE_FLEX_TIME")
	public int topPageFlexTime;
	
	/** ???????????????????????? */
	@Column(name = "INTERVAL_TIME")
	public int intervalTime;
	/** ?????????????????????????????? */
	@Column(name = "INTERVAL_DEDUCTION_TIME")
	public int intervalDeductTime;
	
	/** ????????????????????? */
	@Column(name = "BENTOU_ORDER_AMOUNT_1")
	public int bentouOrderAmount1;
	/** ????????????????????? */
	@Column(name = "BENTOU_ORDER_AMOUNT_2")
	public int bentouOrderAmount2;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_1")
	public int bentouOrderNumber1;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_2")
	public int bentouOrderNumber2;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_3")
	public int bentouOrderNumber3;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_4")
	public int bentouOrderNumber4;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_5")
	public int bentouOrderNumber5;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_6")
	public int bentouOrderNumber6;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_7")
	public int bentouOrderNumber7;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_8")
	public int bentouOrderNumber8;
	/** ???????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_9")
	public int bentouOrderNumber9;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_10")
	public int bentouOrderNumber10;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_11")
	public int bentouOrderNumber11;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_12")
	public int bentouOrderNumber12;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_13")
	public int bentouOrderNumber13;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_14")
	public int bentouOrderNumber14;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_15")
	public int bentouOrderNumber15;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_16")
	public int bentouOrderNumber16;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_17")
	public int bentouOrderNumber17;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_18")
	public int bentouOrderNumber18;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_19")
	public int bentouOrderNumber19;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_20")
	public int bentouOrderNumber20;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_21")
	public int bentouOrderNumber21;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_22")
	public int bentouOrderNumber22;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_23")
	public int bentouOrderNumber23;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_24")
	public int bentouOrderNumber24;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_25")
	public int bentouOrderNumber25;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_26")
	public int bentouOrderNumber26;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_27")
	public int bentouOrderNumber27;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_28")
	public int bentouOrderNumber28;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_29")
	public int bentouOrderNumber29;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_30")
	public int bentouOrderNumber30;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_31")
	public int bentouOrderNumber31;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_32")
	public int bentouOrderNumber32;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_33")
	public int bentouOrderNumber33;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_34")
	public int bentouOrderNumber34;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_35")
	public int bentouOrderNumber35;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_36")
	public int bentouOrderNumber36;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_37")
	public int bentouOrderNumber37;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_38")
	public int bentouOrderNumber38;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_39")
	public int bentouOrderNumber39;
	/** ??????????????????????????????????????? */
	@Column(name = "BENTOU_ORDER_NUMBER_40")
	public int bentouOrderNumber40;
	/** ?????????????????? */
	@Column(name = "HOL_TRANSFER_USE_TIME")
	public int holTransferTime;
	/** ?????????????????? */
	@Column(name = "HOL_ABSENCE_USE_TIME")
	public int holAbsenceTime;
	/** ???????????? */
	@Column(name = "LABOR_ACTUAL_TIME")
	public int laborActualTime;
	/** ??????????????? */
	@Column(name = "LABOR_TOTAL_CALC_TIME")
	public int laborTotalCalcTime;
	/** ?????????????????? */
	@Column(name = "LABOR_CALC_DIFF_TIME")
	public int laborCalcDiffTime;

	/** ??????????????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAggrOverTime> krcdtWekAggrOverTimes;
	/** ?????????????????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAggrHdwkTime> krcdtWekAggrHdwkTimes;
	/** ?????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekDaysAbsence> krcdtWekAggrAbsnDays;
	/** ?????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAggrSpecDays> krcdtWekAggrSpecDays;
	/** ???????????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAggrSpvcDays> krcdtWekAggrSpvcDays;
	/** ?????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekTimeBonusPay> krcdtWekAggrBnspyTime;
	/** ?????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekTimeDvgc> krcdtWekAggrDivgTime;
	/** ???????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekTimeGoout> krcdtWekAggrGoout;
	/** ?????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAggrPremTime> krcdtWekAggrPremTime;
	/** ??????????????????????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekMedicalTime> krcdtWekMedicalTime;
	/** ????????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekTimeOutside> krcdtWekExcoutTime;
	/** ??????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekTimeTotalcount> krcdtWekTotalTimes;
	/** ?????????????????????????????? */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="krcdtWekAttendanceTime", orphanRemoval = true)
	public List<KrcdtWekAnyItemValue> krcdtWekAnyItemValue;
	
	/**
	 * ????????????
	 */
	@Override
	protected Object getKey() {
		return this.PK;
	}
	
	/**
	 * ?????????????????????
	 * @return ???????????????????????????
	 */
	public AttendanceTimeOfWeekly toDomain(){
	
		// ???????????????????????????
		val regAndIrgTime = RegAndIrgTimeOfWeekly.of(
				new AttendanceTimeMonth(this.weekTotalPremiumTime));
		
		// ?????????????????????????????????
		val flexTime = FlexTimeByPeriod.of(
				new AttendanceTimeMonthWithMinus(this.flexTime),
				new AttendanceTimeMonth(this.flexExcessTime),
				new AttendanceTimeMonth(this.flexShortageTime),
				new AttendanceTimeMonth(this.beforeFlexTime));
		
		// ???????????????????????????
		val workTime = WorkTimeOfMonthly.of(
				new AttendanceTimeMonth(this.workTime),
				new AttendanceTimeMonth(this.prescribedPremiumTime),
				new AttendanceTimeMonth(this.actualWorkTime));
		
		// ???????????????????????????
		val overTime = OverTimeOfMonthly.of(
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalOverTime),
						new AttendanceTimeMonth(this.calcTotalOverTime)),
				new AttendanceTimeMonth(this.beforeOverTime),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalTransferOverTime),
						new AttendanceTimeMonth(this.calcTotalTransferOverTime)),
				this.krcdtWekAggrOverTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
		
		// ???????????????????????????
		val holidayWorkTime = HolidayWorkTimeOfMonthly.of(
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalHolidayWorkTime),
						new AttendanceTimeMonth(this.calcTotalHolidayWorkTime)),
				new AttendanceTimeMonth(this.beforeHolidayWorkTime),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.totalTransferHdwkTime),
						new AttendanceTimeMonth(this.calcTotalTransferHdwkTime)),
				this.krcdtWekAggrHdwkTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList()));

		// ?????????????????????????????????
		val vacationUseTime = VacationUseTimeOfMonthly.of(
				AnnualLeaveUseTimeOfMonthly.of(new AttendanceTimeMonth(this.annualLeaveUseTime)),
				RetentionYearlyUseTimeOfMonthly.of(new AttendanceTimeMonth(this.reserveLeaveUseTime)),
				SpecialHolidayUseTimeOfMonthly.of(new AttendanceTimeMonth(this.specialLeaveUseTime)),
				CompensatoryLeaveUseTimeOfMonthly.of(new AttendanceTimeMonth(this.compensatoryLeaveUseTime)));
		
		// ?????????????????????????????????
		val prescribedWorkingTime = PrescribedWorkingTimeOfMonthly.of(
				new AttendanceTimeMonth(this.schedulePrescribedWorkTime),
				new AttendanceTimeMonth(this.recordPrescribedWorkTime));
		
		// ?????????????????????
		val totalWorkingTime = AggregateTotalWorkingTime.of(
				workTime,
				overTime,
				holidayWorkTime,
				vacationUseTime,
				prescribedWorkingTime);
		
		// ???????????????????????????
		val totalSpentTime = AggregateTotalTimeSpentAtWork.of(
				new AttendanceTimeMonth(this.spentOverTime),
				new AttendanceTimeMonth(this.spentMidnightTime),
				new AttendanceTimeMonth(this.spentHolidayTime),
				new AttendanceTimeMonth(this.spentVarienceTime),
				new AttendanceTimeMonth(this.totalSpentTime));
		
		// ???????????????
		val weeklyCalculation = WeeklyCalculation.of(
				regAndIrgTime,
				flexTime,
				totalWorkingTime,
				totalSpentTime);

		// ?????????????????????
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
		
		// ???????????????????????????
		val vtWorkDays = WorkDaysOfMonthly.of(
				AttendanceDaysOfMonthly.of(new AttendanceDaysMonth(this.vtAttendanceDays)),
				AbsenceDaysOfMonthly.of(
						new AttendanceDaysMonth(this.vtTotalAbsenceDays),
						new AttendanceTimeMonth(this.vtTotalAbsenceTime),
						this.krcdtWekAggrAbsnDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				PredeterminedDaysOfMonthly.of(
						new AttendanceDaysMonth(this.vtPredetermineDays)),
				WorkDaysDetailOfMonthly.of(new AttendanceDaysMonth(this.vtWorkDays)),
				HolidayDaysOfMonthly.of(new AttendanceDaysMonth(this.vtHolidayDays)),
				SpecificDaysOfMonthly.of(
						this.krcdtWekAggrSpecDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
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
						this.krcdtWekAggrSpvcDays.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				TimeConsumpVacationDaysOfMonthly.of(
						new AttendanceDaysMonth(this.timeDigestDays),
						new AttendanceTimeMonth(this.timeDigestTime)));
		
		// ????????????
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
		
		// ???????????????????????????
		val vtWorkTime = WorkTimeOfMonthlyVT.of(
				BonusPayTimeOfMonthly.of(
						this.krcdtWekAggrBnspyTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				GoOutOfMonthly.of(
						this.krcdtWekAggrGoout.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
						goOutForChildCares),
				PremiumTimeOfMonthly.of(
						this.krcdtWekAggrPremTime.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
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
						this.krcdtWekAggrDivgTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				this.krcdtWekMedicalTime.stream().map(c -> c.toDomain()).collect(Collectors.toList()),
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

		// ???????????????????????????
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
		
		// ??????????????????
		val verticalTotal = VerticalTotalOfMonthly.of(
				vtWorkDays,
				vtWorkTime,
				vtWorkClock,
				WorkAmountOfMonthly.of(new AttendanceAmountMonth(this.workTimeAmount)));
		
		return AttendanceTimeOfWeekly.of(
				this.PK.employeeId,
				new YearMonth(this.PK.yearMonth),
				ClosureId.valueOf(this.PK.closureId),
				new ClosureDate(this.PK.closureDay, (this.PK.isLastDay != 0)),
				this.PK.weekNo,
				new DatePeriod(this.startYmd, this.endYmd),
				weeklyCalculation,
				ExcessOutsideByPeriod.of(
						this.krcdtWekExcoutTime.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				verticalTotal,
				TotalCountByPeriod.of(
						this.krcdtWekTotalTimes.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				AnyItemByPeriod.of(
						this.krcdtWekAnyItemValue.stream().map(c -> c.toDomain()).collect(Collectors.toList())),
				new AttendanceDaysMonth(this.aggregateDays));
	}
	
	/**
	 * ??????????????????????????????for Insert???
	 * @param domain ???????????????????????????
	 */
	public void fromDomainForPersist(AttendanceTimeOfWeekly domain){
		
		this.PK = new KrcdtWekAttendanceTimePK(
				domain.getEmployeeId(),
				domain.getYearMonth().v(),
				domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v(),
				(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0),
				domain.getWeekNo());
		this.fromDomainForUpdate(domain);
	}
	
	/**
	 * ???????????????????????????(for Update)
	 * @param domain ???????????????????????????
	 */
	public void fromDomainForUpdate(AttendanceTimeOfWeekly domain){

		this.startYmd = domain.getPeriod().start();
		this.endYmd = domain.getPeriod().end();
		this.aggregateDays = domain.getAggregateDays().v();
		
		this.workTimeAmount = domain.getVerticalTotal().getWorkAmount().getWorkTimeAmount().v();
		
		val weeklyCalculation = domain.getWeeklyCalculation();
		val regAndIrgTime = weeklyCalculation.getRegAndIrgTime();
		this.weekTotalPremiumTime = regAndIrgTime.getWeeklyTotalPremiumTime().v();
		this.premiumAmountTotal = domain.getVerticalTotal().getWorkTime().getPremiumTime().getPremiumAmountTotal().v();
		
		val flexTime = weeklyCalculation.getFlexTime();
		this.flexTime = flexTime.getFlexTime().v();
		this.flexExcessTime = flexTime.getFlexExcessTime().v();
		this.flexShortageTime = flexTime.getFlexShortageTime().v();
		this.beforeFlexTime = flexTime.getBeforeFlexTime().v();
		
		val totalWorkingTime = weeklyCalculation.getTotalWorkingTime();
		val workTime = totalWorkingTime.getWorkTime();
		this.workTime = workTime.getWorkTime().v();
		this.actualWorkTime = workTime.getActualWorkTime().v();
		this.prescribedPremiumTime = workTime.getWithinPrescribedPremiumTime().v();
		
		val overTime = totalWorkingTime.getOverTime();
		this.totalOverTime = overTime.getTotalOverTime().getTime().v();
		this.calcTotalOverTime = overTime.getTotalOverTime().getCalcTime().v();
		this.beforeOverTime = overTime.getBeforeOverTime().v();
		this.totalTransferOverTime = overTime.getTotalTransferOverTime().getTime().v();
		this.calcTotalTransferOverTime = overTime.getTotalTransferOverTime().getCalcTime().v();
		
		val holidayWorkTime = totalWorkingTime.getHolidayWorkTime();
		this.totalHolidayWorkTime = holidayWorkTime.getTotalHolidayWorkTime().getTime().v();
		this.calcTotalHolidayWorkTime = holidayWorkTime.getTotalHolidayWorkTime().getCalcTime().v();
		this.beforeHolidayWorkTime = holidayWorkTime.getBeforeHolidayWorkTime().v();
		this.totalTransferHdwkTime = holidayWorkTime.getTotalTransferTime().getTime().v();
		this.calcTotalTransferHdwkTime = holidayWorkTime.getTotalHolidayWorkTime().getCalcTime().v();
		
		val vacationUseTime = totalWorkingTime.getVacationUseTime();
		this.annualLeaveUseTime = vacationUseTime.getAnnualLeave().getUseTime().v();
		this.reserveLeaveUseTime = vacationUseTime.getRetentionYearly().getUseTime().v();
		this.specialLeaveUseTime = vacationUseTime.getSpecialHoliday().getUseTime().v();
		this.compensatoryLeaveUseTime = vacationUseTime.getCompensatoryLeave().getUseTime().v();
		
		val prescribedWorkingTime = totalWorkingTime.getPrescribedWorkingTime();
		this.schedulePrescribedWorkTime = prescribedWorkingTime.getSchedulePrescribedWorkingTime().v();
		this.recordPrescribedWorkTime = prescribedWorkingTime.getRecordPrescribedWorkingTime().v();
		
		val totalSpentTime = weeklyCalculation.getTotalSpentTime();
		this.spentOverTime = totalSpentTime.getOverTimeSpentAtWork().v();
		this.spentMidnightTime = totalSpentTime.getMidnightTimeSpentAtWork().v();
		this.spentHolidayTime = totalSpentTime.getHolidayTimeSpentAtWork().v();
		this.spentVarienceTime = totalSpentTime.getVarienceTimeSpentAtWork().v();
		this.totalSpentTime = totalSpentTime.getTotalTimeSpentAtWork().v();
		
		// ?????????????????????????????????2018.7.22 del shuichi_ishida
		//val agreementTime = weeklyCalculation.getAgreementTime();
		//this.agreementTime = agreementTime.getAgreementTime().v();
		//this.limitErrorTime = agreementTime.getLimitErrorTime().v();
		//this.limitAlarmTime = agreementTime.getLimitAlarmTime().v();
		//this.agreementStatus = agreementTime.getStatus().value;
		
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
		
		/** ?????????????????? */
		this.holAbsenceTime = vtWorkTime.getHolidayUseTime().getAbsence().valueAsMinutes();
		this.holTransferTime = vtWorkTime.getHolidayUseTime().getTransferHoliday().valueAsMinutes();
		
		/** ???????????? */
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
