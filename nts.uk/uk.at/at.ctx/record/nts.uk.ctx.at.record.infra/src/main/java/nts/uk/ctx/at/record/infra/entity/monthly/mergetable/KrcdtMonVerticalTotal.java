package nts.uk.ctx.at.record.infra.entity.monthly.mergetable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.arc.time.YearMonth;
import nts.gul.reflection.FieldReflection;
import nts.gul.reflection.ReflectionUtil;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.common.times.AttendanceTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.MonthMergeKey;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.specificdays.AggregateSpecificDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.specificdays.SpecificDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AbsenceDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AggregateAbsenceDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays.AggregateSpcVacationDays;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.bonuspaytime.AggregateBonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.bonuspaytime.BonusPayTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.breaktime.BreakTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.AggregateDivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.DivergenceAtrOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.divergencetime.DivergenceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.AggregateGoOut;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.GoOutForChildCare;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.goout.GoOutOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.interval.IntervalTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.Late;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.LateLeaveEarlyOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.lateleaveearly.LeaveEarly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.medicaltime.MedicalTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.midnighttime.IllegalMidnightTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.midnighttime.MidnightTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.AggregatePremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.PremiumTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.timevarience.BudgetTimeVarienceOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.toppage.TopPageDisplayOfMonthly;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;
import nts.uk.ctx.at.shared.dom.worktype.CloseAtr;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 月別実績の縦計
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_MON_VERTICAL_TOTAL")
public class KrcdtMonVerticalTotal extends ContractUkJpaEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public static final JpaEntityMapper<KrcdtMonVerticalTotal> MAPPER = new JpaEntityMapper<>(KrcdtMonVerticalTotal.class);

	@EmbeddedId
	public KrcdtMonMergePk id;
	
	@Version
	@Column(name = "EXCLUS_VER")
	public long version;

	/** 就業時間金額 */
	@Column(name = "WORK_TIME_AMOUNT")
	public long workTimeAmount;
	/** 終業回数 */
	@Column(name = "ENDWORK_TIMES")
	public int endWorkTimes;
	/** 終業合計時刻 */
	@Column(name = "ENDWORK_TOTAL_CLOCK")
	public int endWorkTotalClock;
	/** 終業平均時刻 */
	@Column(name = "ENDWORK_AVE_CLOCK")
	public int endWorkAveClock;

	/** ログオン合計日数 */
	@Column(name = "PC_LOGON_TOTAL_DAYS")
	public double pcLogonTotalDays;
	/** ログオン合計時刻 */
	@Column(name = "PC_LOGON_TOTAL_CLOCK")
	public int pcLogonTotalClock;
	/** ログオン平均時刻 */
	@Column(name = "PC_LOGON_AVE_CLOCK")
	public int pcLogonAveClock;
	/** ログオフ合計日数 */
	@Column(name = "PC_LOGOFF_TOTAL_DAYS")
	public double pcLogoffTotalDays;
	/** ログオフ合計時刻 */
	@Column(name = "PC_LOGOFF_TOTAL_CLOCK")
	public int pcLogoffTotalClock;
	/** ログオフ平均時刻 */
	@Column(name = "PC_LOGOFF_AVE_CLOCK")
	public int pcLogoffAveClock;
	/** ログオン乖離日数 */
	@Column(name = "PC_LOGON_DIV_DAYS")
	public double pcLogonDivDays;
	/** ログオン乖離合計時間 */
	@Column(name = "PC_LOGON_DIV_TOTAL_TIME")
	public int pcLogonDivTotalTime;
	/** ログオン乖離平均時間 */
	@Column(name = "PC_LOGON_DIV_AVE_TIME")
	public int pcLogonDivAveTime;
	/** ログオフ乖離日数 */
	@Column(name = "PC_LOGOFF_DIV_DAYS")
	public double pcLogoffDivDays;
	/** ログオフ乖離合計時間 */
	@Column(name = "PC_LOGOFF_DIV_TOTAL_TIME")
	public int pcLogoffDivTotalTime;
	/** ログオフ乖離平均時間 */
	@Column(name = "PC_LOGOFF_DIV_AVE_TIME")
	public int pcLogoffDivAveTime;
	
	/** 特別休暇日数1 */
	@Column(name = "SPCVACT_DAYS_1")
	public double specifiVactDays1;
	/** 特別休暇日数2 */
	@Column(name = "SPCVACT_DAYS_2")
	public double specifiVactDays2;
	/** 特別休暇日数3 */
	@Column(name = "SPCVACT_DAYS_3")
	public double specifiVactDays3;
	/** 特別休暇日数4 */
	@Column(name = "SPCVACT_DAYS_4")
	public double specifiVactDays4;
	/** 特別休暇日数5 */
	@Column(name = "SPCVACT_DAYS_5")
	public double specifiVactDays5;
	/** 特別休暇日数6 */
	@Column(name = "SPCVACT_DAYS_6")
	public double specifiVactDays6;
	/** 特別休暇日数7 */
	@Column(name = "SPCVACT_DAYS_7")
	public double specifiVactDays7;
	/** 特別休暇日数8 */
	@Column(name = "SPCVACT_DAYS_8")
	public double specifiVactDays8;
	/** 特別休暇日数9 */
	@Column(name = "SPCVACT_DAYS_9")
	public double specifiVactDays9;
	/** 特別休暇日数10 */
	@Column(name = "SPCVACT_DAYS_10")
	public double specifiVactDays10;
	/** 特別休暇日数11 */
	@Column(name = "SPCVACT_DAYS_11")
	public double specifiVactDays11;
	/** 特別休暇日数12 */
	@Column(name = "SPCVACT_DAYS_12")
	public double specifiVactDays12;
	/** 特別休暇日数13 */
	@Column(name = "SPCVACT_DAYS_13")
	public double specifiVactDays13;
	/** 特別休暇日数14 */
	@Column(name = "SPCVACT_DAYS_14")
	public double specifiVactDays14;
	/** 特別休暇日数15 */
	@Column(name = "SPCVACT_DAYS_15")
	public double specifiVactDays15;
	/** 特別休暇日数16 */
	@Column(name = "SPCVACT_DAYS_16")
	public double specifiVactDays16;
	/** 特別休暇日数17 */
	@Column(name = "SPCVACT_DAYS_17")
	public double specifiVactDays17;
	/** 特別休暇日数18 */
	@Column(name = "SPCVACT_DAYS_18")
	public double specifiVactDays18;
	/** 特別休暇日数19 */
	@Column(name = "SPCVACT_DAYS_19")
	public double specifiVactDays19;
	/** 特別休暇日数20 */
	@Column(name = "SPCVACT_DAYS_20")
	public double specifiVactDays20;
	/** 特別休暇日数21 */
	@Column(name = "SPCVACT_DAYS_21")
	public double specifiVactDays21;
	/** 特別休暇日数22 */
	@Column(name = "SPCVACT_DAYS_22")
	public double specifiVactDays22;
	/** 特別休暇日数23 */
	@Column(name = "SPCVACT_DAYS_23")
	public double specifiVactDays23;
	/** 特別休暇日数24 */
	@Column(name = "SPCVACT_DAYS_24")
	public double specifiVactDays24;
	/** 特別休暇日数25 */
	@Column(name = "SPCVACT_DAYS_25")
	public double specifiVactDays25;
	/** 特別休暇日数26 */
	@Column(name = "SPCVACT_DAYS_26")
	public double specifiVactDays26;
	/** 特別休暇日数27 */
	@Column(name = "SPCVACT_DAYS_27")
	public double specifiVactDays27;
	/** 特別休暇日数28 */
	@Column(name = "SPCVACT_DAYS_28")
	public double specifiVactDays28;
	/** 特別休暇日数29 */
	@Column(name = "SPCVACT_DAYS_29")
	public double specifiVactDays29;
	/** 特別休暇日数30 */
	@Column(name = "SPCVACT_DAYS_30")
	public double specifiVactDays30;
	/** 特別休暇時間1 */
	@Column(name = "SPCVACT_TIME_1")
	public int specifiVactTime1;
	/** 特別休暇時間2 */
	@Column(name = "SPCVACT_TIME_2")
	public int specifiVactTime2;
	/** 特別休暇時間3 */
	@Column(name = "SPCVACT_TIME_3")
	public int specifiVactTime3;
	/** 特別休暇時間4 */
	@Column(name = "SPCVACT_TIME_4")
	public int specifiVactTime4;
	/** 特別休暇時間5 */
	@Column(name = "SPCVACT_TIME_5")
	public int specifiVactTime5;
	/** 特別休暇時間6 */
	@Column(name = "SPCVACT_TIME_6")
	public int specifiVactTime6;
	/** 特別休暇時間7 */
	@Column(name = "SPCVACT_TIME_7")
	public int specifiVactTime7;
	/** 特別休暇時間8 */
	@Column(name = "SPCVACT_TIME_8")
	public int specifiVactTime8;
	/** 特別休暇時間9 */
	@Column(name = "SPCVACT_TIME_9")
	public int specifiVactTime9;
	/** 特別休暇時間10 */
	@Column(name = "SPCVACT_TIME_10")
	public int specifiVactTime10;
	/** 特別休暇時間11 */
	@Column(name = "SPCVACT_TIME_11")
	public int specifiVactTime11;
	/** 特別休暇時間12 */
	@Column(name = "SPCVACT_TIME_12")
	public int specifiVactTime12;
	/** 特別休暇時間13 */
	@Column(name = "SPCVACT_TIME_13")
	public int specifiVactTime13;
	/** 特別休暇時間14 */
	@Column(name = "SPCVACT_TIME_14")
	public int specifiVactTime14;
	/** 特別休暇時間15 */
	@Column(name = "SPCVACT_TIME_15")
	public int specifiVactTime15;
	/** 特別休暇時間16 */
	@Column(name = "SPCVACT_TIME_16")
	public int specifiVactTime16;
	/** 特別休暇時間17 */
	@Column(name = "SPCVACT_TIME_17")
	public int specifiVactTime17;
	/** 特別休暇時間18 */
	@Column(name = "SPCVACT_TIME_18")
	public int specifiVactTime18;
	/** 特別休暇時間19 */
	@Column(name = "SPCVACT_TIME_19")
	public int specifiVactTime19;
	/** 特別休暇時間20 */
	@Column(name = "SPCVACT_TIME_20")
	public int specifiVactTime20;
	/** 特別休暇時間21 */
	@Column(name = "SPCVACT_TIME_21")
	public int specifiVactTime21;
	/** 特別休暇時間22 */
	@Column(name = "SPCVACT_TIME_22")
	public int specifiVactTime22;
	/** 特別休暇時間23 */
	@Column(name = "SPCVACT_TIME_23")
	public int specifiVactTime23;
	/** 特別休暇時間24 */
	@Column(name = "SPCVACT_TIME_24")
	public int specifiVactTime24;
	/** 特別休暇時間25 */
	@Column(name = "SPCVACT_TIME_25")
	public int specifiVactTime25;
	/** 特別休暇時間26 */
	@Column(name = "SPCVACT_TIME_26")
	public int specifiVactTime26;
	/** 特別休暇時間27 */
	@Column(name = "SPCVACT_TIME_27")
	public int specifiVactTime27;
	/** 特別休暇時間28 */
	@Column(name = "SPCVACT_TIME_28")
	public int specifiVactTime28;
	/** 特別休暇時間29 */
	@Column(name = "SPCVACT_TIME_29")
	public int specifiVactTime29;
	/** 特別休暇時間30 */
	@Column(name = "SPCVACT_TIME_30")
	public int specifiVactTime30;
	/** 特別休暇合計日数 */
	@Column(name = "SPCVACT_TOTAL_DAYS")
	public double specifiVactTotalDays;
	/** 特別休暇合計時間 */
	@Column(name = "SPCVACT_TOTAL_TIME")
	public int specifiVactTotalTime;
	
	/** 欠勤日数1 */
	@Column(name = "ABSENCE_DAYS_1")
	public double absenceDays1;
	/** 欠勤日数2 */
	@Column(name = "ABSENCE_DAYS_2")
	public double absenceDays2;
	/** 欠勤日数3 */
	@Column(name = "ABSENCE_DAYS_3")
	public double absenceDays3;
	/** 欠勤日数4 */
	@Column(name = "ABSENCE_DAYS_4")
	public double absenceDays4;
	/** 欠勤日数5 */
	@Column(name = "ABSENCE_DAYS_5")
	public double absenceDays5;
	/** 欠勤日数6 */
	@Column(name = "ABSENCE_DAYS_6")
	public double absenceDays6;
	/** 欠勤日数7 */
	@Column(name = "ABSENCE_DAYS_7")
	public double absenceDays7;
	/** 欠勤日数8 */
	@Column(name = "ABSENCE_DAYS_8")
	public double absenceDays8;
	/** 欠勤日数9 */
	@Column(name = "ABSENCE_DAYS_9")
	public double absenceDays9;
	/** 欠勤日数10 */
	@Column(name = "ABSENCE_DAYS_10")
	public double absenceDays10;
	/** 欠勤日数11 */
	@Column(name = "ABSENCE_DAYS_11")
	public double absenceDays11;
	/** 欠勤日数12 */
	@Column(name = "ABSENCE_DAYS_12")
	public double absenceDays12;
	/** 欠勤日数13 */
	@Column(name = "ABSENCE_DAYS_13")
	public double absenceDays13;
	/** 欠勤日数14 */
	@Column(name = "ABSENCE_DAYS_14")
	public double absenceDays14;
	/** 欠勤日数15 */
	@Column(name = "ABSENCE_DAYS_15")
	public double absenceDays15;
	/** 欠勤日数16 */
	@Column(name = "ABSENCE_DAYS_16")
	public double absenceDays16;
	/** 欠勤日数17 */
	@Column(name = "ABSENCE_DAYS_17")
	public double absenceDays17;
	/** 欠勤日数18 */
	@Column(name = "ABSENCE_DAYS_18")
	public double absenceDays18;
	/** 欠勤日数19 */
	@Column(name = "ABSENCE_DAYS_19")
	public double absenceDays19;
	/** 欠勤日数20 */
	@Column(name = "ABSENCE_DAYS_20")
	public double absenceDays20;
	/** 欠勤日数21 */
	@Column(name = "ABSENCE_DAYS_21")
	public double absenceDays21;
	/** 欠勤日数22 */
	@Column(name = "ABSENCE_DAYS_22")
	public double absenceDays22;
	/** 欠勤日数23 */
	@Column(name = "ABSENCE_DAYS_23")
	public double absenceDays23;
	/** 欠勤日数24 */
	@Column(name = "ABSENCE_DAYS_24")
	public double absenceDays24;
	/** 欠勤日数25 */
	@Column(name = "ABSENCE_DAYS_25")
	public double absenceDays25;
	/** 欠勤日数26 */
	@Column(name = "ABSENCE_DAYS_26")
	public double absenceDays26;
	/** 欠勤日数27 */
	@Column(name = "ABSENCE_DAYS_27")
	public double absenceDays27;
	/** 欠勤日数28 */
	@Column(name = "ABSENCE_DAYS_28")
	public double absenceDays28;
	/** 欠勤日数29 */
	@Column(name = "ABSENCE_DAYS_29")
	public double absenceDays29;
	/** 欠勤日数30 */
	@Column(name = "ABSENCE_DAYS_30")
	public double absenceDays30;
	/** 欠勤時間1 */
	@Column(name = "ABSENCE_TIME_1")
	public int absenceTime1;
	/** 欠勤時間2 */
	@Column(name = "ABSENCE_TIME_2")
	public int absenceTime2;
	/** 欠勤時間3 */
	@Column(name = "ABSENCE_TIME_3")
	public int absenceTime3;
	/** 欠勤時間4 */
	@Column(name = "ABSENCE_TIME_4")
	public int absenceTime4;
	/** 欠勤時間5 */
	@Column(name = "ABSENCE_TIME_5")
	public int absenceTime5;
	/** 欠勤時間6 */
	@Column(name = "ABSENCE_TIME_6")
	public int absenceTime6;
	/** 欠勤時間7 */
	@Column(name = "ABSENCE_TIME_7")
	public int absenceTime7;
	/** 欠勤時間8 */
	@Column(name = "ABSENCE_TIME_8")
	public int absenceTime8;
	/** 欠勤時間9 */
	@Column(name = "ABSENCE_TIME_9")
	public int absenceTime9;
	/** 欠勤時間10 */
	@Column(name = "ABSENCE_TIME_10")
	public int absenceTime10;
	/** 欠勤時間11 */
	@Column(name = "ABSENCE_TIME_11")
	public int absenceTime11;
	/** 欠勤時間12 */
	@Column(name = "ABSENCE_TIME_12")
	public int absenceTime12;
	/** 欠勤時間13 */
	@Column(name = "ABSENCE_TIME_13")
	public int absenceTime13;
	/** 欠勤時間14 */
	@Column(name = "ABSENCE_TIME_14")
	public int absenceTime14;
	/** 欠勤時間15 */
	@Column(name = "ABSENCE_TIME_15")
	public int absenceTime15;
	/** 欠勤時間16 */
	@Column(name = "ABSENCE_TIME_16")
	public int absenceTime16;
	/** 欠勤時間17 */
	@Column(name = "ABSENCE_TIME_17")
	public int absenceTime17;
	/** 欠勤時間18 */
	@Column(name = "ABSENCE_TIME_18")
	public int absenceTime18;
	/** 欠勤時間19 */
	@Column(name = "ABSENCE_TIME_19")
	public int absenceTime19;
	/** 欠勤時間20 */
	@Column(name = "ABSENCE_TIME_20")
	public int absenceTime20;
	/** 欠勤時間21 */
	@Column(name = "ABSENCE_TIME_21")
	public int absenceTime21;
	/** 欠勤時間22 */
	@Column(name = "ABSENCE_TIME_22")
	public int absenceTime22;
	/** 欠勤時間23 */
	@Column(name = "ABSENCE_TIME_23")
	public int absenceTime23;
	/** 欠勤時間24 */
	@Column(name = "ABSENCE_TIME_24")
	public int absenceTime24;
	/** 欠勤時間25 */
	@Column(name = "ABSENCE_TIME_25")
	public int absenceTime25;
	/** 欠勤時間26 */
	@Column(name = "ABSENCE_TIME_26")
	public int absenceTime26;
	/** 欠勤時間27 */
	@Column(name = "ABSENCE_TIME_27")
	public int absenceTime27;
	/** 欠勤時間28 */
	@Column(name = "ABSENCE_TIME_28")
	public int absenceTime28;
	/** 欠勤時間29 */
	@Column(name = "ABSENCE_TIME_29")
	public int absenceTime29;
	/** 欠勤時間30 */
	@Column(name = "ABSENCE_TIME_30")
	public int absenceTime30;
	/** 欠勤合計日数 */
	@Column(name = "ABSENCE_TOTAL_DAYS")
	public double absenceTotalDays;
	/** 欠勤合計時間 */
	@Column(name = "ABSENCE_TOTAL_TIME")
	public int absenceTotalTime;
	
	/** 特定日数1 */
	@Column(name = "SPECIFIC_DAYS_1")
	public double specificDays1;
	/** 特定日数2 */
	@Column(name = "SPECIFIC_DAYS_2")
	public double specificDays2;
	/** 特定日数3 */
	@Column(name = "SPECIFIC_DAYS_3")
	public double specificDays3;
	/** 特定日数4 */
	@Column(name = "SPECIFIC_DAYS_4")
	public double specificDays4;
	/** 特定日数5 */
	@Column(name = "SPECIFIC_DAYS_5")
	public double specificDays5;
	/** 特定日数6 */
	@Column(name = "SPECIFIC_DAYS_6")
	public double specificDays6;
	/** 特定日数7 */
	@Column(name = "SPECIFIC_DAYS_7")
	public double specificDays7;
	/** 特定日数8 */
	@Column(name = "SPECIFIC_DAYS_8")
	public double specificDays8;
	/** 特定日数9 */
	@Column(name = "SPECIFIC_DAYS_9")
	public double specificDays9;
	/** 特定日数10 */
	@Column(name = "SPECIFIC_DAYS_10")
	public double specificDays10;
	/** 休出特定日数1 */
	@Column(name = "SPECIFIC_HDWK_DAYS_1")
	public double specificHdwkDays1;
	/** 休出特定日数2 */
	@Column(name = "SPECIFIC_HDWK_DAYS_2")
	public double specificHdwkDays2;
	/** 休出特定日数3 */
	@Column(name = "SPECIFIC_HDWK_DAYS_3")
	public double specificHdwkDays3;
	/** 休出特定日数4 */
	@Column(name = "SPECIFIC_HDWK_DAYS_4")
	public double specificHdwkDays4;
	/** 休出特定日数5 */
	@Column(name = "SPECIFIC_HDWK_DAYS_5")
	public double specificHdwkDays5;
	/** 休出特定日数6 */
	@Column(name = "SPECIFIC_HDWK_DAYS_6")
	public double specificHdwkDays6;
	/** 休出特定日数7 */
	@Column(name = "SPECIFIC_HDWK_DAYS_7")
	public double specificHdwkDays7;
	/** 休出特定日数8 */
	@Column(name = "SPECIFIC_HDWK_DAYS_8")
	public double specificHdwkDays8;
	/** 休出特定日数9 */
	@Column(name = "SPECIFIC_HDWK_DAYS_9")
	public double specificHdwkDays9;
	/** 休出特定日数10 */
	@Column(name = "SPECIFIC_HDWK_DAYS_10")
	public double specificHdwkDays10;

	/** 産前休業日数 */
	@Column(name = "SUSPEND_PRENATAL_DAYS")
	public double suspendPrenatalDays;
	/** 産後休業日数 */
	@Column(name = "SUSPEND_POSTPARTUM_DAYS")
	public double suspendPostpartumDays;
	/** 育児休業日数 */
	@Column(name = "SUSPEND_CHILDCARE_DAYS")
	public double suspendChildCareDays;
	/** 介護休業日数 */
	@Column(name = "SUSPEND_CARE_DAYS")
	public double suspendCareDays;
	/** 傷病休業日数 */
	@Column(name = "SUSPEND_INJILN_DAYS")
	public double suspendInjilnDays;
	/** 任意休業日数01 */
	@Column(name = "SUSPEND_ANY_DAYS_01")
	public double suspendAnyDays1;
	/** 任意休業日数02 */
	@Column(name = "SUSPEND_ANY_DAYS_02")
	public double suspendAnyDays2;
	/** 任意休業日数03 */
	@Column(name = "SUSPEND_ANY_DAYS_03")
	public double suspendAnyDays3;
	/** 任意休業日数04 */
	@Column(name = "SUSPEND_ANY_DAYS_04")
	public double suspendAnyDays4;
	
	/** 勤務日数 */
	@Column(name = "WORK_DAYS")
	public double workDays;
	/** 勤務回数 */
	@Column(name = "WORK_TIMES")
	public int workTimes;
	/** 二回勤務回数 */
	@Column(name = "WORK_TWOTIMES_TIMES")
	public int workTwoTimesDays;
	/** 臨時勤務回数 */
	@Column(name = "WORK_TEMPORARY_TIMES")
	public int workTemporaryTimes;
	/** 臨時勤務時間 */
	@Column(name = "WORK_TEMPORARY_TIME")
	public int workTemporaryTime;
	/** 所定日数 */
	@Column(name = "WORK_WITHIN_DAYS")
	public double workWithinDays;
	/** 出勤日数 */
	@Column(name = "WORK_ATTENDANCE_DAYS")
	public double workAttendanceDays;
	/** 休出日数 */
	@Column(name = "WORK_HOLIDAY_DAYS")
	public double workHolidayDays;
	/** 振出日数 */
	@Column(name = "WORK_TRANSFER_DAYS")
	public double workTransferDays;
	/** 休日日数 */
	@Column(name = "HOLIDAY_DAYS")
	public double holidayDays;
	
	/** 直行日数 */
	@Column(name = "STRAIGHT_GO_DAYS")
	public double straightGoDays;
	/** 直帰日数 */
	@Column(name = "STRAIGHT_BACK_DAYS")
	public double straightBackDays;
	/** 直行直帰日数 */
	@Column(name = "STRAIGHT_GO_BACK_DAYS")
	public double straightGoBackDays;
	
	/** 時間消化休暇日数 */
	@Column(name = "TIME_DIGEST_DAYS")
	public double timeDigestDays;
	/** 時間消化休暇時間 */
	@Column(name = "TIME_DIGEST_TIME")
	public int timeDigestTime;
	
	/** 育児外出回数 */
	@Column(name = "GOOUT_CLDCAR_TIMES")
	public int gooutCldcarTimes;
	/** 育児外出時間 */
	@Column(name = "GOOUT_CLDCAR_TIME")
	public int gooutCldcarTime;
	/** 育児外出所定内時間 */
	@Column(name = "GOOUT_CLDCAR_WITHIN_TIME")
	public int gooutCldcarWithinTime;
	/** 育児外出所定外時間 */
	@Column(name = "GOOUT_CLDCAR_EXCESS_TIME")
	public int gooutCldcarExcessTime;
	/** 介護外出回数 */
	@Column(name = "GOOUT_CARE_TIMES")
	public int gooutCareTimes;
	/** 介護外出時間 */
	@Column(name = "GOOUT_CARE_TIME")
	public int gooutCareTime;
	/** 介護外出所定内時間 */
	@Column(name = "GOOUT_CARE_WITHIN_TIME")
	public int gooutCareWithinTime;
	/** 介護外出所定外時間 */
	@Column(name = "GOOUT_CARE_EXCESS_TIME")
	public int gooutCareExcessTime;
	/** 私用外出回数 */
	@Column(name = "GOOUT_TIMES_PRIVATE")
	public int gooutTimesPrivate;
	/** 公用外出回数 */
	@Column(name = "GOOUT_TIMES_PUBLIC")
	public int gooutTimesPublic;
	/** 有償外出回数 */
	@Column(name = "GOOUT_TIMES_COMPENSATION")
	public int gooutTimesCompensation;
	/** 組合外出回数 */
	@Column(name = "GOOUT_TIMES_UNION")
	public int gooutTimesUnion;
	/** 私用所定内回数 */
	@Column(name = "GOOUT_WITHIN_TIME_PRIVATE")
	public int gooutWithinTimePrivate;
	/** 公用所定内回数 */
	@Column(name = "GOOUT_WITHIN_TIME_PUBLIC")
	public int gooutWithinTimePublic;
	/** 有償所定内回数 */
	@Column(name = "GOOUT_WITHIN_TIME_COMPEN")
	public int gooutWithinTimeCompensation;
	/** 組合所定内回数 */
	@Column(name = "GOOUT_WITHIN_TIME_UNION")
	public int gooutWithinTimeUnion;
	/** 私用計算所定内回数 */
	@Column(name = "GOOUT_WITHIN_CALC_TIME_PRIVATE")
	public int gooutWithinCalcTimePrivate;
	/** 公用計算所定内回数 */
	@Column(name = "GOOUT_WITHIN_CALC_TIME_PUBLIC")
	public int gooutWithinCalcTimePublic;
	/** 有償計算所定内回数 */
	@Column(name = "GOOUT_WITHIN_CALC_TIME_COMPEN")
	public int gooutWithinCalcTimeCompensation;
	/** 組合計算所定内回数 */
	@Column(name = "GOOUT_WITHIN_CALC_TIME_UNION")
	public int gooutWithinCalcTimeUnion;
	/** 私用所定外回数 */
	@Column(name = "GOOUT_EXCESS_TIME_PRIVATE")
	public int gooutExcessTimePrivate;
	/** 公用所定外回数 */
	@Column(name = "GOOUT_EXCESS_TIME_PUBLIC")
	public int gooutExcessTimePublic;
	/** 有償所定外回数 */
	@Column(name = "GOOUT_EXCESS_TIME_COMPEN")
	public int gooutExcessTimeCompensation;
	/** 組合所定外回数 */
	@Column(name = "GOOUT_EXCESS_TIME_UNION")
	public int gooutExcessTimeUnion;
	/** 私用計算所定外回数 */
	@Column(name = "GOOUT_EXCESS_CALC_TIME_PRIVATE")
	public int gooutExcessCalcTimePrivate;
	/** 公用計算所定外回数 */
	@Column(name = "GOOUT_EXCESS_CALC_TIME_PUBLIC")
	public int gooutExcessCalcTimePublic;
	/** 有償計算所定外回数 */
	@Column(name = "GOOUT_EXCESS_CALC_TIME_COMPEN")
	public int gooutExcessCalcTimeCompensation;
	/** 組合計算所定外回数 */
	@Column(name = "GOOUT_EXCESS_CALC_TIME_UNION")
	public int gooutExcessCalcTimeUnion;
	/** 私用合計回数 */
	@Column(name = "GOOUT_TOTAL_TIME_PRIVATE")
	public int gooutTotalTimePrivate;
	/** 公用合計回数 */
	@Column(name = "GOOUT_TOTAL_TIME_PUBLIC")
	public int gooutTotalTimePublic;
	/** 有償合計回数 */
	@Column(name = "GOOUT_TOTAL_TIME_COMPEN")
	public int gooutTotalTimeCompensation;
	/** 組合合計回数 */
	@Column(name = "GOOUT_TOTAL_TIME_UNION")
	public int gooutTotalTimeUnion;
	/** 私用計算合計回数 */
	@Column(name = "GOOUT_TOTAL_CALC_TIME_PRIVATE")
	public int gooutTotalCalcTimePrivate;
	/** 公用計算合計回数 */
	@Column(name = "GOOUT_TOTAL_CALC_TIME_PUBLIC")
	public int gooutTotalCalcTimePublic;
	/** 有償計算合計回数 */
	@Column(name = "GOOUT_TOTAL_CALC_TIME_COMPEN")
	public int gooutTotalCalcTimeCompensation;
	/** 組合計算合計回数 */
	@Column(name = "GOOUT_TOTAL_CALC_TIME_UNION")
	public int gooutTotalCalcTimeUnion;
	/** 私用コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_TIME_PRIVATE")
	public int gooutCoreOutTimePrivate;
	/** 公用コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_TIME_PUBLIC")
	public int gooutCoreOutTimePublic;
	/** 有償コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_TIME_COMPEN")
	public int gooutCoreOutTimeCompensation;
	/** 組合コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_TIME_UNION")
	public int gooutCoreOutTimeUnion;
	/** 私用計算コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_CALC_TIME_PRIVATE")
	public int gooutCoreOutCalcTimePrivate;
	/** 公用計算コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_CALC_TIME_PUBLIC")
	public int gooutCoreOutCalcTimePublic;
	/** 有償計算コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_CALC_TIME_COMPEN")
	public int gooutCoreOutCalcTimeCompensation;
	/** 組合計算コアタイム外時間 */
	@Column(name = "GOOUT_COREOUT_CALC_TIME_UNION")
	public int gooutCoreOutCalcTimeUnion;
	
	/** 割増時間1 */
	@Column(name = "PREMIUM_TIME_1")
	public int premiumTime1;
	/** 割増時間2 */
	@Column(name = "PREMIUM_TIME_2")
	public int premiumTime2;
	/** 割増時間3 */
	@Column(name = "PREMIUM_TIME_3")
	public int premiumTime3;
	/** 割増時間4 */
	@Column(name = "PREMIUM_TIME_4")
	public int premiumTime4;
	/** 割増時間5 */
	@Column(name = "PREMIUM_TIME_5")
	public int premiumTime5;
	/** 割増時間6 */
	@Column(name = "PREMIUM_TIME_6")
	public int premiumTime6;
	/** 割増時間7 */
	@Column(name = "PREMIUM_TIME_7")
	public int premiumTime7;
	/** 割増時間8 */
	@Column(name = "PREMIUM_TIME_8")
	public int premiumTime8;
	/** 割増時間9 */
	@Column(name = "PREMIUM_TIME_9")
	public int premiumTime9;
	/** 割増時間10 */
	@Column(name = "PREMIUM_TIME_10")
	public int premiumTime10;
	/** 割増金額1 */
	@Column(name = "PREMIUM_AMOUNT_1")
	public long premiumAmount1;
	/** 割増金額2 */
	@Column(name = "PREMIUM_AMOUNT_2")
	public long premiumAmount2;
	/** 割増金額3 */
	@Column(name = "PREMIUM_AMOUNT_3")
	public long premiumAmount3;
	/** 割増金額4 */
	@Column(name = "PREMIUM_AMOUNT_4")
	public long premiumAmount4;
	/** 割増金額5 */
	@Column(name = "PREMIUM_AMOUNT_5")
	public long premiumAmount5;
	/** 割増金額6 */
	@Column(name = "PREMIUM_AMOUNT_6")
	public long premiumAmount6;
	/** 割増金額7 */
	@Column(name = "PREMIUM_AMOUNT_7")
	public long premiumAmount7;
	/** 割増金額8 */
	@Column(name = "PREMIUM_AMOUNT_8")
	public long premiumAmount8;
	/** 割増金額9 */
	@Column(name = "PREMIUM_AMOUNT_9")
	public long premiumAmount9;
	/** 割増金額10 */
	@Column(name = "PREMIUM_AMOUNT_10")
	public long premiumAmount10;
	/** 割増金額合計 */
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public long premiumAmountTotal;

	/** 休憩時間 */
	@Column(name = "BREAK_TIME")
	public int breakTime;
	/** 休憩回数 */
	@Column(name = "BREAK_TIMES")
	public int breakTimes;
	/** 所定内休憩時間 */
	@Column(name = "BREAK_WITHIN_TIME")
	public int breakWithinTime;
	/** 所定内控除時間 */
	@Column(name = "BREAK_WITHIN_DEDUC_TIME")
	public int breakWithinDeducTime;
	/** 所定外休憩時間 */
	@Column(name = "BREAK_EXCESS_TIME")
	public int breakExcessTime;
	/** 所定外控除時間 */
	@Column(name = "BREAK_EXCESS_DEDUC_TIME")
	public int breakExcessDeducTime;
	
	/** 残業深夜時間 */
	@Column(name = "MDNT_OVWK_TIME")
	public int nightOtTime;
	/** 計算残業深夜時間 */
	@Column(name = "MDNT_OVWK_CALC_TIME")
	public int nightOtCalcTime;
	/** 所定内深夜時間 */
	@Column(name = "MDNT_WITHIN_TIME")
	public int nightWithinTime;
	/** 計算所定内深夜時間 */
	@Column(name = "MDNT_WITHIN_CALC_TIME")
	public int nightWithinCalcTime;
	/** 所定外深夜時間 */
	@Column(name = "MDNT_EXCESS_TIME")
	public int nightExcessTime;
	/** 計算所定外深夜時間 */
	@Column(name = "MDNT_EXCESS_CALC_TIME")
	public int nightExcessCalcTime;
	/** 所定外事前深夜時間 */
	@Column(name = "MDNT_EXCESS_BFR_TIME")
	public int nightExcessBeforeTime;
	/** 法定内休出深夜時間 */
	@Column(name = "MDNT_LGL_HDWK_TIME")
	public int nightLegalHolWorkTime;
	/** 計算法定内休出深夜時間 */
	@Column(name = "MDNT_LGL_HDWK_CALC_TIME")
	public int nightLegalHolWorkCalcTime;
	/** 法定外休出深夜時間 */
	@Column(name = "MDNT_ILG_HDWK_MDNT_TIME")
	public int nightIllegalHolWorkTime;
	/** 計算法定外休出深夜時間 */
	@Column(name = "MDNT_ILG_HDWK_CALC_TIME")
	public int nightIllegalHolWorkCalcTime;
	/** 祝日休出深夜時間 */
	@Column(name = "MDNT_SPHD_HDWK_TIME")
	public int nightSpecHolWorkTime;
	/** 計算祝日休出深夜時間 */
	@Column(name = "MDNT_SPHD_HDWK_CALC_TIME")
	public int nightSpecHolWorkCalcTime;

	/** 遅刻回数 */
	@Column(name = "LATE_TIMES")
	public int lateTimes;
	/** 遅刻時間 */
	@Column(name = "LATE_TIME")
	public int lateTime;
	/** 計算遅刻時間 */
	@Column(name = "LATE_CALC_TIME")
	public int lateCalcTime;
	/** 早退回数 */
	@Column(name = "LEAVEEARLY_TIMES")
	public int leaveEarlyTimes;
	/** 早退時間 */
	@Column(name = "LEAVEEARLY_TIME")
	public int leaveEarlyTime;
	/** 計算早退時間 */
	@Column(name = "LEAVEEARLY_CALC_TIME")
	public int leaveEarlyCalcTime;
	
	/** 入退門出勤前時間 */
	@Column(name = "ALGT_BFR_ATND_TIME")
	public int algtBeforeAttendanceTime;
	/** 入退門退勤後時間 */
	@Column(name = "ALGT_AFT_LVWK_TIME")
	public int algtAfterLeaveTime;
	/** 入退門滞在時間 */
	@Column(name = "ALGT_STAYING_TIME")
	public int algtStayingTime;
	/** 入退門不就労時間 */
	@Column(name = "ALGT_UNEMPLOYED_TIME")
	public int algtUnemployedTime;
	/** 予実差異時間 */
	@Column(name = "BUDGET_VARIENCE_TIME")
	public int budgetVarienceTime;
	
	/** 加給時間1 */
	@Column(name = "BONUS_PAY_TIME_1")
	public int bonusPayTime1;
	/** 加給時間2 */
	@Column(name = "BONUS_PAY_TIME_2")
	public int bonusPayTime2;
	/** 加給時間3 */
	@Column(name = "BONUS_PAY_TIME_3")
	public int bonusPayTime3;
	/** 加給時間4 */
	@Column(name = "BONUS_PAY_TIME_4")
	public int bonusPayTime4;
	/** 加給時間5 */
	@Column(name = "BONUS_PAY_TIME_5")
	public int bonusPayTime5;
	/** 加給時間6 */
	@Column(name = "BONUS_PAY_TIME_6")
	public int bonusPayTime6;
	/** 加給時間7 */
	@Column(name = "BONUS_PAY_TIME_7")
	public int bonusPayTime7;
	/** 加給時間8 */
	@Column(name = "BONUS_PAY_TIME_8")
	public int bonusPayTime8;
	/** 加給時間9 */
	@Column(name = "BONUS_PAY_TIME_9")
	public int bonusPayTime9;
	/** 加給時間10 */
	@Column(name = "BONUS_PAY_TIME_10")
	public int bonusPayTime10;
	/** 特定加給時間1 */
	@Column(name = "BONUS_PAY_SPEC_TIME_1")
	public int bonusSpecPayTime1;
	/** 特定加給時間2 */
	@Column(name = "BONUS_PAY_SPEC_TIME_2")
	public int bonusSpecPayTime2;
	/** 特定加給時間3 */
	@Column(name = "BONUS_PAY_SPEC_TIME_3")
	public int bonusSpecPayTime3;
	/** 特定加給時間4 */
	@Column(name = "BONUS_PAY_SPEC_TIME_4")
	public int bonusSpecPayTime4;
	/** 特定加給時間5 */
	@Column(name = "BONUS_PAY_SPEC_TIME_5")
	public int bonusSpecPayTime5;
	/** 特定加給時間6 */
	@Column(name = "BONUS_PAY_SPEC_TIME_6")
	public int bonusSpecPayTime6;
	/** 特定加給時間7 */
	@Column(name = "BONUS_PAY_SPEC_TIME_7")
	public int bonusSpecPayTime7;
	/** 特定加給時間8 */
	@Column(name = "BONUS_PAY_SPEC_TIME_8")
	public int bonusSpecPayTime8;
	/** 特定加給時間9 */
	@Column(name = "BONUS_PAY_SPEC_TIME_9")
	public int bonusSpecPayTime9;
	/** 特定加給時間10 */
	@Column(name = "BONUS_PAY_SPEC_TIME_10")
	public int bonusSpecPayTime10;
	/** 休出加給時間1 */
	@Column(name = "BONUS_PAY_HDWK_TIME_1")
	public int bonusPayHolWorkTime1;
	/** 休出加給時間2 */
	@Column(name = "BONUS_PAY_HDWK_TIME_2")
	public int bonusPayHolWorkTime2;
	/** 休出加給時間3 */
	@Column(name = "BONUS_PAY_HDWK_TIME_3")
	public int bonusPayHolWorkTime3;
	/** 休出加給時間4 */
	@Column(name = "BONUS_PAY_HDWK_TIME_4")
	public int bonusPayHolWorkTime4;
	/** 休出加給時間5 */
	@Column(name = "BONUS_PAY_HDWK_TIME_5")
	public int bonusPayHolWorkTime5;
	/** 休出加給時間6 */
	@Column(name = "BONUS_PAY_HDWK_TIME_6")
	public int bonusPayHolWorkTime6;
	/** 休出加給時間7 */
	@Column(name = "BONUS_PAY_HDWK_TIME_7")
	public int bonusPayHolWorkTime7;
	/** 休出加給時間8 */
	@Column(name = "BONUS_PAY_HDWK_TIME_8")
	public int bonusPayHolWorkTime8;
	/** 休出加給時間9 */
	@Column(name = "BONUS_PAY_HDWK_TIME_9")
	public int bonusPayHolWorkTime9;
	/** 休出加給時間10 */
	@Column(name = "BONUS_PAY_HDWK_TIME_10")
	public int bonusPayHolWorkTime10;
	/** 休出特定加給時間1 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_1")
	public int bonusPaySpecHolWorkTime1;
	/** 休出特定加給時間2 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_2")
	public int bonusPaySpecHolWorkTime2;
	/** 休出特定加給時間3 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_3")
	public int bonusPaySpecHolWorkTime3;
	/** 休出特定加給時間4 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_4")
	public int bonusPaySpecHolWorkTime4;
	/** 休出特定加給時間5 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_5")
	public int bonusPaySpecHolWorkTime5;
	/** 休出特定加給時間6 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_6")
	public int bonusPaySpecHolWorkTime6;
	/** 休出特定加給時間7 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_7")
	public int bonusPaySpecHolWorkTime7;
	/** 休出特定加給時間8 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_8")
	public int bonusPaySpecHolWorkTime8;
	/** 休出特定加給時間9 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_9")
	public int bonusPaySpecHolWorkTime9;
	/** 休出特定加給時間10 */
	@Column(name = "BNSPAY_HDWK_SPEC_TIME_10")
	public int bonusPaySpecHolWorkTime10;
	/** 所定内加給時間1 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_1")
	public int bonusPayWithinTime1;
	/** 所定内加給時間2 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_2")
	public int bonusPayWithinTime2;
	/** 所定内加給時間3 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_3")
	public int bonusPayWithinTime3;
	/** 所定内加給時間4 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_4")
	public int bonusPayWithinTime4;
	/** 所定内加給時間5 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_5")
	public int bonusPayWithinTime5;
	/** 所定内加給時間6 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_6")
	public int bonusPayWithinTime6;
	/** 所定内加給時間7 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_7")
	public int bonusPayWithinTime7;
	/** 所定内加給時間8 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_8")
	public int bonusPayWithinTime8;
	/** 所定内加給時間9 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_9")
	public int bonusPayWithinTime9;
	/** 所定内加給時間10 */
	@Column(name = "BONUS_PAY_WITHIN_TIME_10")
	public int bonusPayWithinTime10;
	/** 所定内特定加給時間1 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_1")
	public int bonusPaySpecWithinTime1;
	/** 所定内特定加給時間2 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_2")
	public int bonusPaySpecWithinTime2;
	/** 所定内特定加給時間3 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_3")
	public int bonusPaySpecWithinTime3;
	/** 所定内特定加給時間4 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_4")
	public int bonusPaySpecWithinTime4;
	/** 所定内特定加給時間5 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_5")
	public int bonusPaySpecWithinTime5;
	/** 所定内特定加給時間6 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_6")
	public int bonusPaySpecWithinTime6;
	/** 所定内特定加給時間7 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_7")
	public int bonusPaySpecWithinTime7;
	/** 所定内特定加給時間8 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_8")
	public int bonusPaySpecWithinTime8;
	/** 所定内特定加給時間9 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_9")
	public int bonusPaySpecWithinTime9;
	/** 所定内特定加給時間10 */
	@Column(name = "BNSPAY_WITHIN_SPEC_TIME_10")
	public int bonusPaySpecWithinTime10;
	/** 所定外加給時間1 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_1")
	public int bonusPayExcessTime1;
	/** 所定外加給時間2 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_2")
	public int bonusPayExcessTime2;
	/** 所定外加給時間3 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_3")
	public int bonusPayExcessTime3;
	/** 所定外加給時間4 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_4")
	public int bonusPayExcessTime4;
	/** 所定外加給時間5 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_5")
	public int bonusPayExcessTime5;
	/** 所定外加給時間6 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_6")
	public int bonusPayExcessTime6;
	/** 所定外加給時間7 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_7")
	public int bonusPayExcessTime7;
	/** 所定外加給時間8 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_8")
	public int bonusPayExcessTime8;
	/** 所定外加給時間9 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_9")
	public int bonusPayExcessTime9;
	/** 所定外加給時間10 */
	@Column(name = "BONUS_PAY_EXCESS_TIME_10")
	public int bonusPayExcessTime10;
	/** 所定外特定加給時間1 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_1")
	public int bonusPaySpecExcessTime1;
	/** 所定外特定加給時間2 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_2")
	public int bonusPaySpecExcessTime2;
	/** 所定外特定加給時間3 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_3")
	public int bonusPaySpecExcessTime3;
	/** 所定外特定加給時間4 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_4")
	public int bonusPaySpecExcessTime4;
	/** 所定外特定加給時間5 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_5")
	public int bonusPaySpecExcessTime5;
	/** 所定外特定加給時間6 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_6")
	public int bonusPaySpecExcessTime6;
	/** 所定外特定加給時間7 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_7")
	public int bonusPaySpecExcessTime7;
	/** 所定外特定加給時間8 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_8")
	public int bonusPaySpecExcessTime8;
	/** 所定外特定加給時間9 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_9")
	public int bonusPaySpecExcessTime9;
	/** 所定外特定加給時間10 */
	@Column(name = "BNSPAY_EXCESS_SPEC_TIME_10")
	public int bonusPaySpecExcessTime10;

	/** 乖離フラグ1 */
	@Column(name = "DIVERGENCE_ATR_1")
	public int divergenceFlag1;
	/** 乖離フラグ2 */
	@Column(name = "DIVERGENCE_ATR_2")
	public int divergenceFlag2;
	/** 乖離フラグ3 */
	@Column(name = "DIVERGENCE_ATR_3")
	public int divergenceFlag3;
	/** 乖離フラグ4 */
	@Column(name = "DIVERGENCE_ATR_4")
	public int divergenceFlag4;
	/** 乖離フラグ5 */
	@Column(name = "DIVERGENCE_ATR_5")
	public int divergenceFlag5;
	/** 乖離フラグ6 */
	@Column(name = "DIVERGENCE_ATR_6")
	public int divergenceFlag6;
	/** 乖離フラグ7 */
	@Column(name = "DIVERGENCE_ATR_7")
	public int divergenceFlag7;
	/** 乖離フラグ8 */
	@Column(name = "DIVERGENCE_ATR_8")
	public int divergenceFlag8;
	/** 乖離フラグ9 */
	@Column(name = "DIVERGENCE_ATR_9")
	public int divergenceFlag9;
	/** 乖離フラグ10 */
	@Column(name = "DIVERGENCE_ATR_10")
	public int divergenceFlag10;
	/** 乖離時間1 */
	@Column(name = "DIVERGENCE_TIME_1")
	public int divergenceTime1;
	/** 乖離時間2 */
	@Column(name = "DIVERGENCE_TIME_2")
	public int divergenceTime2;
	/** 乖離時間3 */
	@Column(name = "DIVERGENCE_TIME_3")
	public int divergenceTime3;
	/** 乖離時間4 */
	@Column(name = "DIVERGENCE_TIME_4")
	public int divergenceTime4;
	/** 乖離時間5 */
	@Column(name = "DIVERGENCE_TIME_5")
	public int divergenceTime5;
	/** 乖離時間6 */
	@Column(name = "DIVERGENCE_TIME_6")
	public int divergenceTime6;
	/** 乖離時間7 */
	@Column(name = "DIVERGENCE_TIME_7")
	public int divergenceTime7;
	/** 乖離時間8 */
	@Column(name = "DIVERGENCE_TIME_8")
	public int divergenceTime8;
	/** 乖離時間9 */
	@Column(name = "DIVERGENCE_TIME_9")
	public int divergenceTime9;
	/** 乖離時間10 */
	@Column(name = "DIVERGENCE_TIME_10")
	public int divergenceTime10;

	/** 日勤勤務時間 */
	@Column(name = "MEDICAL_DAY_TIME")
	public int medicalDayTime;
	/** 日勤控除時間 */
	@Column(name = "MEDICAL_DAY_DEDUCTION_TIME")
	public int medicalDayDeductTime;
	/** 日勤申送時間 */
	@Column(name = "MEDICAL_DAY_TAKEOVER_TIME")
	public int medicalDayTakeOverTime;
	/** 夜勤勤務時間 */
	@Column(name = "MEDICAL_NIGHT_TIME")
	public int medicalNightTime;
	/** 夜勤控除時間 */
	@Column(name = "MEDICAL_NIGHT_DEDUCTION_TIME")
	public int medicalNightDeductTime;
	/** 夜勤申送時間 */
	@Column(name = "MEDICAL_NIGHT_TAKEOVER_TIME")
	public int medicalNightTakeOverTime;
	
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

	@Override
	protected Object getKey() {
		return this.id;
	}
	
	public 	MonthMergeKey toDomainKey() {
		MonthMergeKey key = new MonthMergeKey();
		key.setEmployeeId(this.id.getEmployeeId());
		key.setYearMonth(new YearMonth(this.id.getYearMonth()));
		key.setClosureId(EnumAdaptor.valueOf(this.id.getClosureId(), ClosureId.class));
		key.setClosureDate(new ClosureDate(this.id.getClosureDay(),
			(this.id.getIsLastDay() == 1)));
		return key;
	}
	
	public VerticalTotalOfMonthly domain() {
		
		return VerticalTotalOfMonthly.of(workDays(), workTime(), workClock(),
				WorkAmountOfMonthly.of(new AttendanceAmountMonth(workTimeAmount)));
	}
	
	public void entity(VerticalTotalOfMonthly domain) {
		/** 勤務日数 */
		workDays(domain.getWorkDays());

		/** 勤務時間 */
		workTime(domain.getWorkTime());
		
		/** 勤務時刻 */
		workCLock(domain.getWorkClock());

		/** 勤務金額*/
		workAmount(domain.getWorkAmount());
	}
	
	public void reset() {

		/** 勤務日数 */
		workDays(new WorkDaysOfMonthly());
		
		/** 勤務時間 */
		workTime(new WorkTimeOfMonthlyVT());
		
		/** 勤務時刻 */
		workCLock(new WorkClockOfMonthly());

		/** 勤務金額*/
		workAmount(new WorkAmountOfMonthly());
	}
	
	private void workAmount(WorkAmountOfMonthly workTime) {

		this.workTimeAmount = workTime.getWorkTimeAmount().v();
	}
	
	private void workTime(WorkTimeOfMonthlyVT workTime) {
		/** 遅刻早退 */
		this.lateTimes = workTime.getLateLeaveEarly().getLate().getTimes().v();
		this.lateTime = workTime.getLateLeaveEarly().getLate().getTime().getTime().valueAsMinutes();
		this.lateCalcTime = workTime.getLateLeaveEarly().getLate().getTime().getCalcTime().valueAsMinutes();
		this.leaveEarlyTimes = workTime.getLateLeaveEarly().getLeaveEarly().getTimes().v();
		this.leaveEarlyTime = workTime.getLateLeaveEarly().getLeaveEarly().getTime().getTime().valueAsMinutes();
		this.leaveEarlyCalcTime = workTime.getLateLeaveEarly().getLeaveEarly().getTime().getCalcTime().valueAsMinutes();
		
		/** 深夜時間 */
		this.nightExcessBeforeTime = workTime.getMidnightTime().getIllegalMidnightTime().getBeforeTime().valueAsMinutes();
		this.nightExcessCalcTime = workTime.getMidnightTime().getIllegalMidnightTime().getTime().getCalcTime().valueAsMinutes();
		this.nightExcessTime = workTime.getMidnightTime().getIllegalMidnightTime().getTime().getTime().valueAsMinutes();
		this.nightIllegalHolWorkCalcTime = workTime.getMidnightTime().getIllegalHolidayWorkMidnightTime().getCalcTime().valueAsMinutes();
		this.nightIllegalHolWorkTime = workTime.getMidnightTime().getIllegalHolidayWorkMidnightTime().getTime().valueAsMinutes();
		this.nightLegalHolWorkCalcTime = workTime.getMidnightTime().getLegalHolidayWorkMidnightTime().getCalcTime().valueAsMinutes();
		this.nightLegalHolWorkTime = workTime.getMidnightTime().getLegalHolidayWorkMidnightTime().getTime().valueAsMinutes();
		this.nightOtCalcTime = workTime.getMidnightTime().getOverWorkMidnightTime().getCalcTime().valueAsMinutes();
		this.nightOtTime = workTime.getMidnightTime().getOverWorkMidnightTime().getTime().valueAsMinutes();
		this.nightSpecHolWorkCalcTime = workTime.getMidnightTime().getSpecialHolidayWorkMidnightTime().getCalcTime().valueAsMinutes();
		this.nightSpecHolWorkTime = workTime.getMidnightTime().getSpecialHolidayWorkMidnightTime().getTime().valueAsMinutes();
		this.nightWithinCalcTime = workTime.getMidnightTime().getLegalMidnightTime().getCalcTime().valueAsMinutes();
		this.nightWithinTime = workTime.getMidnightTime().getLegalMidnightTime().getTime().valueAsMinutes();
		
		/** 休憩時間 */
		this.breakExcessDeducTime = workTime.getBreakTime().getExcessDeductionTime().valueAsMinutes();
		this.breakExcessTime = workTime.getBreakTime().getExcessTime().valueAsMinutes();
		this.breakTime = workTime.getBreakTime().getBreakTime().valueAsMinutes();
		this.breakTimes = workTime.getBreakTime().getBreakTimes().v();
		this.breakWithinDeducTime = workTime.getBreakTime().getWithinDeductionTime().valueAsMinutes();
		this.breakWithinTime = workTime.getBreakTime().getWithinTime().valueAsMinutes();
		
		/** 加給時間 */
		bonusPay(workTime.getBonusPayTime().getBonusPayTime());
		
		/** 入退門時間 */
		this.algtStayingTime = workTime.getAttendanceLeaveGateTime().getStayingTime().v();
		this.algtAfterLeaveTime = workTime.getAttendanceLeaveGateTime().getTimeAfterLeaveWork().valueAsMinutes();
		this.algtBeforeAttendanceTime = workTime.getAttendanceLeaveGateTime().getTimeBeforeAttendance().valueAsMinutes();
		this.algtUnemployedTime = workTime.getAttendanceLeaveGateTime().getUnemployedTime().valueAsMinutes();
		
		/** 予約 */
		bentou(workTime.getReservation());
		
		/** 割増時間 */
		premium(workTime.getPremiumTime().getPremiumTime());
		this.premiumAmountTotal = workTime.getPremiumTime().getPremiumAmountTotal().v();
		
		/** 予実差異時間 */
		this.budgetVarienceTime = workTime.getBudgetTimeVarience().getTime().valueAsMinutes();
		
		/** 乖離時間 */
		divergence(workTime.getDivergenceTime());
		
		/** 外出 */
		goOut(workTime.getGoOut());
		
		/** トップページ表示用時間 */
		this.topPageFlexTime = workTime.getTopPage().getFlex().valueAsMinutes();
		this.topPageHolWorkTime = workTime.getTopPage().getHolidayWork().valueAsMinutes();
		this.topPageOtTime = workTime.getTopPage().getOvertime().valueAsMinutes();
		
		/** 医療時間 */
		medicalTime(workTime.getMedicalTime());
		
		/** インターバル時間 */
		this.intervalTime = workTime.getInterval().getTime().valueAsMinutes();
		this.intervalDeductTime = workTime.getInterval().getExemptionTime().valueAsMinutes();
		
		/** 休暇使用時間 */
		this.holAbsenceTime = workTime.getHolidayUseTime().getAbsence().valueAsMinutes();
		this.holTransferTime = workTime.getHolidayUseTime().getTransferHoliday().valueAsMinutes();
		
		/** 労働時間 */
		this.laborActualTime = workTime.getLaborTime().getActualWorkTime().valueAsMinutes();
		this.laborCalcDiffTime = workTime.getLaborTime().getCalcDiffTime().valueAsMinutes();
		this.laborTotalCalcTime = workTime.getLaborTime().getTotalCalcTime().valueAsMinutes();
	}

	private void bonusPay(Map<Integer, AggregateBonusPayTime> bonusPayTime) {
		for (val data : bonusPayTime.entrySet()) {
			val time = FieldReflection.getField(this.getClass(), "bonusPayTime" + data.getKey());
			ReflectionUtil.setFieldValue(time, this, data.getValue().getBonusPayTime().valueAsMinutes());
			
			val spec = FieldReflection.getField(this.getClass(), "bonusSpecPayTime" + data.getKey());
			ReflectionUtil.setFieldValue(spec, this, data.getValue().getSpecificBonusPayTime().valueAsMinutes());
			
			val excess = FieldReflection.getField(this.getClass(), "bonusPayExcessTime" + data.getKey());
			ReflectionUtil.setFieldValue(excess, this, data.getValue().getExcess().valueAsMinutes());
			
			val excessSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecExcessTime" + data.getKey());
			ReflectionUtil.setFieldValue(excessSpec, this, data.getValue().getExcessSpecific().valueAsMinutes());
			
			val holWork = FieldReflection.getField(this.getClass(), "bonusPayHolWorkTime" + data.getKey());
			ReflectionUtil.setFieldValue(holWork, this, data.getValue().getHolidayWorkBonusPayTime().valueAsMinutes());
			
			val holWorkSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecHolWorkTime" + data.getKey());
			ReflectionUtil.setFieldValue(holWorkSpec, this, data.getValue().getHolidayWorkSpecificBonusPayTime().valueAsMinutes());
			
			val within = FieldReflection.getField(this.getClass(), "bonusPayWithinTime" + data.getKey());
			ReflectionUtil.setFieldValue(within, this, data.getValue().getWithin().valueAsMinutes());
			
			val withinSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecWithinTime" + data.getKey());
			ReflectionUtil.setFieldValue(withinSpec, this, data.getValue().getWithinSpecific().valueAsMinutes());
		}
	}

	private void bentou(ReservationOfMonthly reservation) {
		this.bentouOrderAmount1 = reservation.getAmount1().v();
		this.bentouOrderAmount2 = reservation.getAmount2().v();
		
		for (val data : reservation.getOrders()) {
			val number = FieldReflection.getField(this.getClass(), "bentouOrderNumber" + data.getFrameNo());
			ReflectionUtil.setFieldValue(number, this, data.getOrder().v());
		}
	}

	private void premium(Map<Integer, AggregatePremiumTime> premiumTime) {
		for (val data : premiumTime.entrySet()) {
			val time = FieldReflection.getField(this.getClass(), "premiumTime" + data.getKey());
			ReflectionUtil.setFieldValue(time, this, data.getValue().getTime().valueAsMinutes());
			
			val amount = FieldReflection.getField(this.getClass(), "premiumAmount" + data.getKey());
			ReflectionUtil.setFieldValue(amount, this, data.getValue().getAmount().v());
		}
	}

	private void divergence(DivergenceTimeOfMonthly divergenceTime) {
		for (val data : divergenceTime.getDivergenceTimeList().entrySet()) {
			val flag = FieldReflection.getField(this.getClass(), "divergenceFlag" + data.getKey());
			ReflectionUtil.setFieldValue(flag, this, data.getValue().getDivergenceAtr().value);
			
			val time = FieldReflection.getField(this.getClass(), "divergenceTime" + data.getKey());
			ReflectionUtil.setFieldValue(time, this, data.getValue().getDivergenceTime().v());
		}
	}

	private void goOut(GoOutOfMonthly goOut) {
		/** 外出 */
		goOut(goOut.getGoOuts());
		
		/** 育児外出 */
		care(goOut.getGoOutForChildCares());
	}

	private void care(Map<ChildCareAtr, GoOutForChildCare> goOutForChildCares) {
		if (goOutForChildCares.containsKey(ChildCareAtr.CARE)) {
			GoOutForChildCare goOut = goOutForChildCares.get(ChildCareAtr.CARE);
			this.gooutCareTimes = goOut.getTimes().v();
			this.gooutCareTime = goOut.getTime().valueAsMinutes();
			this.gooutCareExcessTime = goOut.getExcessTime().valueAsMinutes();
			this.gooutCareWithinTime = goOut.getWithinTime().valueAsMinutes();
		} else {
			this.gooutCareTimes = 0;
			this.gooutCareTime = 0;
			this.gooutCareExcessTime = 0;
			this.gooutCareWithinTime = 0;
		}
		if (goOutForChildCares.containsKey(ChildCareAtr.CHILD_CARE)) {
			GoOutForChildCare goOut = goOutForChildCares.get(ChildCareAtr.CHILD_CARE);
			this.gooutCldcarTimes = goOut.getTimes().v();
			this.gooutCldcarTime = goOut.getTime().valueAsMinutes();
			this.gooutCldcarExcessTime = goOut.getExcessTime().valueAsMinutes();
			this.gooutCldcarWithinTime = goOut.getWithinTime().valueAsMinutes();
		} else {
			this.gooutCldcarTimes = 0;
			this.gooutCldcarTime = 0;
			this.gooutCldcarExcessTime = 0;
			this.gooutCldcarWithinTime = 0;
		}
	}

	private void goOut(Map<GoingOutReason, AggregateGoOut> goOuts) {
		if (goOuts.containsKey(GoingOutReason.PRIVATE)) {
			AggregateGoOut goOut = goOuts.get(GoingOutReason.PRIVATE);
			this.gooutTimesPrivate = goOut.getTimes().v();
			this.gooutTotalTimePrivate = goOut.getTotalTime().getTime().valueAsMinutes();
			this.gooutTotalCalcTimePrivate = goOut.getTotalTime().getCalcTime().valueAsMinutes();
			this.gooutWithinTimePrivate = goOut.getLegalTime().getTime().valueAsMinutes();
			this.gooutWithinCalcTimePrivate = goOut.getLegalTime().getCalcTime().valueAsMinutes();
			this.gooutExcessTimePrivate = goOut.getIllegalTime().getTime().valueAsMinutes();
			this.gooutExcessCalcTimePrivate = goOut.getIllegalTime().getCalcTime().valueAsMinutes();
			this.gooutCoreOutTimePrivate = goOut.getCoreOutTime().getTime().valueAsMinutes();
			this.gooutCoreOutCalcTimePrivate = goOut.getCoreOutTime().getCalcTime().valueAsMinutes();
		} else {
			this.gooutTimesPrivate = 0;
			this.gooutTotalTimePrivate = 0;
			this.gooutTotalCalcTimePrivate = 0;
			this.gooutWithinTimePrivate = 0;
			this.gooutWithinCalcTimePrivate = 0;
			this.gooutExcessTimePrivate = 0;
			this.gooutExcessCalcTimePrivate = 0;
			this.gooutCoreOutTimePrivate = 0;
			this.gooutCoreOutCalcTimePrivate = 0;
		}
		
		if (goOuts.containsKey(GoingOutReason.PUBLIC)) {
			AggregateGoOut goOut = goOuts.get(GoingOutReason.PUBLIC);
			this.gooutTimesPublic = goOut.getTimes().v();
			this.gooutTotalTimePublic = goOut.getTotalTime().getTime().valueAsMinutes();
			this.gooutTotalCalcTimePublic = goOut.getTotalTime().getCalcTime().valueAsMinutes();
			this.gooutWithinTimePublic = goOut.getLegalTime().getTime().valueAsMinutes();
			this.gooutWithinCalcTimePublic = goOut.getLegalTime().getCalcTime().valueAsMinutes();
			this.gooutExcessTimePublic = goOut.getIllegalTime().getTime().valueAsMinutes();
			this.gooutExcessCalcTimePublic = goOut.getIllegalTime().getCalcTime().valueAsMinutes();
			this.gooutCoreOutTimePublic = goOut.getCoreOutTime().getTime().valueAsMinutes();
			this.gooutCoreOutCalcTimePublic = goOut.getCoreOutTime().getCalcTime().valueAsMinutes();
		} else {
			this.gooutTimesPublic = 0;
			this.gooutTotalTimePublic = 0;
			this.gooutTotalCalcTimePublic = 0;
			this.gooutWithinTimePublic = 0;
			this.gooutWithinCalcTimePublic = 0;
			this.gooutExcessTimePublic = 0;
			this.gooutExcessCalcTimePublic = 0;
			this.gooutCoreOutTimePublic = 0;
			this.gooutCoreOutCalcTimePublic = 0;
		}
		
		if (goOuts.containsKey(GoingOutReason.COMPENSATION)) {
			AggregateGoOut goOut = goOuts.get(GoingOutReason.COMPENSATION);
			this.gooutTimesCompensation = goOut.getTimes().v();
			this.gooutTotalTimeCompensation = goOut.getTotalTime().getTime().valueAsMinutes();
			this.gooutTotalCalcTimeCompensation = goOut.getTotalTime().getCalcTime().valueAsMinutes();
			this.gooutWithinTimeCompensation = goOut.getLegalTime().getTime().valueAsMinutes();
			this.gooutWithinCalcTimeCompensation = goOut.getLegalTime().getCalcTime().valueAsMinutes();
			this.gooutExcessTimeCompensation = goOut.getIllegalTime().getTime().valueAsMinutes();
			this.gooutExcessCalcTimeCompensation = goOut.getIllegalTime().getCalcTime().valueAsMinutes();
			this.gooutCoreOutTimeCompensation = goOut.getCoreOutTime().getTime().valueAsMinutes();
			this.gooutCoreOutCalcTimeCompensation = goOut.getCoreOutTime().getCalcTime().valueAsMinutes();
		} else {
			this.gooutTimesCompensation = 0;
			this.gooutTotalTimeCompensation = 0;
			this.gooutTotalCalcTimeCompensation = 0;
			this.gooutWithinTimeCompensation = 0;
			this.gooutWithinCalcTimeCompensation = 0;
			this.gooutExcessTimeCompensation = 0;
			this.gooutExcessCalcTimeCompensation = 0;
			this.gooutCoreOutTimeCompensation = 0;
			this.gooutCoreOutCalcTimeCompensation = 0;
		}
		
		if (goOuts.containsKey(GoingOutReason.UNION)) {
			AggregateGoOut goOut = goOuts.get(GoingOutReason.UNION);
			this.gooutTimesUnion = goOut.getTimes().v();
			this.gooutTotalTimeUnion = goOut.getTotalTime().getTime().valueAsMinutes();
			this.gooutTotalCalcTimeUnion = goOut.getTotalTime().getCalcTime().valueAsMinutes();
			this.gooutWithinTimeUnion = goOut.getLegalTime().getTime().valueAsMinutes();
			this.gooutWithinCalcTimeUnion = goOut.getLegalTime().getCalcTime().valueAsMinutes();
			this.gooutExcessTimeUnion = goOut.getIllegalTime().getTime().valueAsMinutes();
			this.gooutExcessCalcTimeUnion = goOut.getIllegalTime().getCalcTime().valueAsMinutes();
			this.gooutCoreOutTimeUnion = goOut.getCoreOutTime().getTime().valueAsMinutes();
			this.gooutCoreOutCalcTimeUnion = goOut.getCoreOutTime().getCalcTime().valueAsMinutes();
		} else {
			this.gooutTimesUnion = 0;
			this.gooutTotalTimeUnion = 0;
			this.gooutTotalCalcTimeUnion = 0;
			this.gooutWithinTimeUnion = 0;
			this.gooutWithinCalcTimeUnion = 0;
			this.gooutExcessTimeUnion = 0;
			this.gooutExcessCalcTimeUnion = 0;
			this.gooutCoreOutTimeUnion = 0;
			this.gooutCoreOutCalcTimeUnion = 0;
		}
	}

	private void medicalTime(Map<WorkTimeNightShift, MedicalTimeOfMonthly> medicalTime) {
		if (medicalTime.containsKey(WorkTimeNightShift.DAY_SHIFT)) {
			MedicalTimeOfMonthly day = medicalTime.get(WorkTimeNightShift.DAY_SHIFT);
			this.medicalDayDeductTime = day.getDeducationTime().valueAsMinutes();
			this.medicalDayTakeOverTime = day.getTakeOverTime().valueAsMinutes();
			this.medicalDayTime = day.getWorkTime().valueAsMinutes();
		} else {
			this.medicalDayDeductTime = 0;
			this.medicalDayTakeOverTime = 0;
			this.medicalDayTime = 0;
		}
		if (medicalTime.containsKey(WorkTimeNightShift.NIGHT_SHIFT)) {
			MedicalTimeOfMonthly night = medicalTime.get(WorkTimeNightShift.NIGHT_SHIFT);
			this.medicalNightDeductTime = night.getDeducationTime().valueAsMinutes();
			this.medicalNightTakeOverTime = night.getTakeOverTime().valueAsMinutes();
			this.medicalDayTime = night.getWorkTime().valueAsMinutes();
		} else {
			this.medicalNightDeductTime = 0;
			this.medicalNightTakeOverTime = 0;
			this.medicalNightTime = 0;
		}
	}

	/** 勤務時刻 */
	private void workCLock(WorkClockOfMonthly workClock) {
		/** 終業時刻 */
		val endClock = workClock.getEndClock();
		this.endWorkTimes = endClock.getTimes().v();
		this.endWorkTotalClock = endClock.getTotalClock().v();
		this.endWorkAveClock = endClock.getAverageClock().v();
		
		/** PCログオン情報 */
		val logonInfo = workClock.getLogonInfo();
		
		/** PCログオン時刻 */
		val logonClock = logonInfo.getLogonClock().getLogonClock();
		
		/** 合計日数 */
		this.pcLogonTotalDays = logonClock.getTotalDays().v();
		/** 合計時刻 */
		this.pcLogonTotalClock = logonClock.getTotalClock().v();
		/** 平均時刻 */
		this.pcLogonAveClock = logonClock.getAverageClock().v();
		
		/** PCログオフ時刻 */
		val logoffClock = logonInfo.getLogonClock().getLogoffClock();
		this.pcLogoffTotalDays = logoffClock.getTotalDays().v();
		this.pcLogoffTotalClock = logoffClock.getTotalClock().v();
		this.pcLogoffAveClock = logoffClock.getAverageClock().v();
		
		/** PCログオン乖離 */
		val logonDivergence = logonInfo.getLogonDivergence().getLogonDivergence();
		
		/** 日数 */
		this.pcLogonDivDays = logonDivergence.getDays().v();
		/** 合計時間 */
		this.pcLogonDivTotalTime = logonDivergence.getTotalTime().v();
		/** 平均時間 */
		this.pcLogonDivAveTime = logonDivergence.getAverageTime().v();
		
		/** PCログオフ乖離 */		
	    val logoffDivergence = logonInfo.getLogonDivergence().getLogoffDivergence();
	    /** 日数 */
	    this.pcLogoffDivDays = logoffDivergence.getDays().v();
	    /** 合計時間 */
	    this.pcLogoffDivTotalTime = logoffDivergence.getTotalTime().v();
	    /** 平均時間 */
	    this.pcLogoffDivAveTime = logoffDivergence.getAverageTime().v();
	}
	
	/** 勤務日数 */
	private void workDays(WorkDaysOfMonthly vtWorkDays) {
		
		/** 特別休暇日数 */
		val specialVacationDays = vtWorkDays.getSpecialVacationDays();
		this.specifiVactTotalDays = specialVacationDays.getTotalSpcVacationDays().v();
		this.specifiVactTotalTime = specialVacationDays.getTotalSpcVacationTime().v();
		this.specVacationDays(specialVacationDays.getSpcVacationDaysList());
		
		/** 欠勤日数 */
		val absenceDays =  vtWorkDays.getAbsenceDays();
		this.absenceTotalDays = absenceDays.getTotalAbsenceDays().v();
		this.absenceTotalTime = absenceDays.getTotalAbsenceTime().v();
		absenceDays(absenceDays.getAbsenceDaysList());
		
		/** 特定日数 */
		this.specificDays(vtWorkDays.getSpecificDays().getSpecificDays());
		
		/** 休業 */
		this.leave(vtWorkDays.getLeave());
		
		/** 勤務日数  */
		this.workDays = vtWorkDays.getWorkDays().getDays().v();
		
		/** 勤務回数  */
		this.workTimes = vtWorkDays.getWorkTimes().getTimes().v();
		
		/** 二回勤務回数 */
		this.workTwoTimesDays = vtWorkDays.getTwoTimesWorkTimes().getTimes().v();
		
		/** 臨時勤務回数 */
		this.workTemporaryTimes = vtWorkDays.getTemporaryWorkTimes().getTimes().v();
		this.workTemporaryTime = vtWorkDays.getTemporaryWorkTimes().getTime().v();

		/** 所定日数  */
		this.workWithinDays = vtWorkDays.getPredetermineDays().getPredeterminedDays().v();
		
		/** 出勤日数 */
		this.workAttendanceDays = vtWorkDays.getAttendanceDays().getDays().v();
		
		/** 休出日数  */
		this.workHolidayDays = vtWorkDays.getHolidayWorkDays().getDays().v();
		
		/** 振出日数 */
		this.workTransferDays = vtWorkDays.getRecruitmentDays().getDays().v();
		
		/** 休日日数 */
		this.holidayDays = vtWorkDays.getHolidayDays().getDays().v();
		
		/** 直行直帰 */
		this.straightGoDays = vtWorkDays.getStraightDays().getStraightGo().v();
		this.straightBackDays = vtWorkDays.getStraightDays().getStraightBack().v();
		this.straightGoBackDays = vtWorkDays.getStraightDays().getStraightGoStraightBack().v();
		
		/** 時間消化休暇 */
		this.timeDigestDays = vtWorkDays.getTimeConsumpDays().getDays().v();
		this.timeDigestTime = vtWorkDays.getTimeConsumpDays().getTime().valueAsMinutes();
	}

	private void specVacationDays(Map<Integer, AggregateSpcVacationDays> spcVacationDaysList) {
		for (val data : spcVacationDaysList.entrySet()) {
			val days = FieldReflection.getField(this.getClass(), "specifiVactDays" + data.getKey());
			ReflectionUtil.setFieldValue(days, this, data.getValue().getDays().v());
			
			val time = FieldReflection.getField(this.getClass(), "specifiVactTime" + data.getKey());
			ReflectionUtil.setFieldValue(time, this, data.getValue().getTime().v());
		}
	}

	private void specificDays(Map<SpecificDateItemNo, AggregateSpecificDays> specificDays) {
		for (val data : specificDays.entrySet()) {
			val specDays = FieldReflection.getField(this.getClass(), "specificDays" + data.getKey());
			ReflectionUtil.setFieldValue(specDays, this, data.getValue().getSpecificDays().v());
			
			val holWorkSpecDays = FieldReflection.getField(this.getClass(), "specificHdwkDays" + data.getKey());
			ReflectionUtil.setFieldValue(holWorkSpecDays, this, data.getValue().getHolidayWorkSpecificDays().v());
		}
	}

	private void absenceDays(Map<Integer, AggregateAbsenceDays> absenceDaysList) {
		for (val data : absenceDaysList.entrySet()) {
			val days = FieldReflection.getField(this.getClass(), "absenceDays" + data.getKey());
			ReflectionUtil.setFieldValue(days, this, data.getValue().getDays().v());
			
			val time = FieldReflection.getField(this.getClass(), "absenceTime" + data.getKey());
			ReflectionUtil.setFieldValue(time, this, data.getValue().getTime().v());
		}
	}
	
	private void leave(LeaveOfMonthly domain) {
		this.suspendPrenatalDays = 0.0;
		this.suspendPostpartumDays = 0.0;
		this.suspendChildCareDays = 0.0;
		this.suspendCareDays = 0.0;
		this.suspendInjilnDays = 0.0;
		this.suspendAnyDays1 = 0.0;
		this.suspendAnyDays2 = 0.0;
		this.suspendAnyDays3 = 0.0;
		this.suspendAnyDays4 = 0.0;
		if(domain == null){
			return;
		}
		val fixLeaveDaysMap = domain.getFixLeaveDays();
		if (fixLeaveDaysMap.containsKey(CloseAtr.PRENATAL)) {
			this.suspendPrenatalDays = fixLeaveDaysMap.get(CloseAtr.PRENATAL).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.POSTPARTUM)) {
			this.suspendPostpartumDays = fixLeaveDaysMap.get(CloseAtr.POSTPARTUM).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.CHILD_CARE)) {
			this.suspendChildCareDays = fixLeaveDaysMap.get(CloseAtr.CHILD_CARE).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.CARE)) {
			this.suspendCareDays = fixLeaveDaysMap.get(CloseAtr.CARE).getDays().v();
		}
		if (fixLeaveDaysMap.containsKey(CloseAtr.INJURY_OR_ILLNESS)) {
			this.suspendInjilnDays = fixLeaveDaysMap.get(CloseAtr.INJURY_OR_ILLNESS).getDays().v();
		}
		val anyLeaveDaysMap = domain.getAnyLeaveDays();
		if (anyLeaveDaysMap.containsKey(1)) {
			this.suspendAnyDays1 = anyLeaveDaysMap.get(1).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(2)) {
			this.suspendAnyDays2 = anyLeaveDaysMap.get(2).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(3)) {
			this.suspendAnyDays3 = anyLeaveDaysMap.get(3).getDays().v();
		}
		if (anyLeaveDaysMap.containsKey(4)) {
			this.suspendAnyDays4 = anyLeaveDaysMap.get(4).getDays().v();
		}
	}
	
	private WorkDaysOfMonthly workDays() {
		return WorkDaysOfMonthly.of(
				AttendanceDaysOfMonthly.of(new AttendanceDaysMonth(this.workAttendanceDays)), 
				absenceDays(), 
				PredeterminedDaysOfMonthly.of(new AttendanceDaysMonth(this.workWithinDays)), 
				WorkDaysDetailOfMonthly.of(new AttendanceDaysMonth(this.workDays)), 
				HolidayDaysOfMonthly.of(new AttendanceDaysMonth(this.holidayDays)), 
				specficDays(), 
				HolidayWorkDaysOfMonthly.of(new AttendanceDaysMonth(workHolidayDays)),
				straightDays(), 
				WorkTimesOfMonthly.of(new AttendanceTimesMonth(this.workTimes)), 
				TwoTimesWorkTimesOfMonthly.of(new AttendanceTimesMonth(this.workTwoTimesDays)), 
				TemporaryWorkTimesOfMonthly.of(
						new AttendanceTimesMonth(this.workTemporaryTimes), 
						new AttendanceTimeMonth(this.workTemporaryTime)), 
				leave(), 
				RecruitmentDaysOfMonthly.of(new AttendanceDaysMonth(this.workTransferDays)),
				spcVacationDays(),
				TimeConsumpVacationDaysOfMonthly.of(
						new AttendanceDaysMonth(this.timeDigestDays),
						new AttendanceTimeMonth(this.timeDigestTime)));
	}
	
	private SpcVacationDaysOfMonthly spcVacationDays() {
		List<AggregateSpcVacationDays> spcVacationDaysList = new ArrayList<>();
		for (int i = 1; i <= 30; i++) {
			val days = FieldReflection.getField(this.getClass(), "specifiVactDays" + i);
			val time = FieldReflection.getField(this.getClass(), "specifiVactTime" + i);
			
			spcVacationDaysList.add(AggregateSpcVacationDays.of(
					i, 
					new AttendanceDaysMonth(ReflectionUtil.getFieldValue(days, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(time, this))));
		}
		
		return SpcVacationDaysOfMonthly.of(
				new AttendanceDaysMonth(this.specifiVactTotalDays),
				new AttendanceTimeMonth(this.specifiVactTotalTime), 
				spcVacationDaysList);
	}
	
	private LeaveOfMonthly leave() {
		List<AggregateLeaveDays> fixLeaveDaysList = new ArrayList<>();
		List<AnyLeave> anyLeaveDaysList = new ArrayList<>();
		
		fixLeaveDaysList.add(AggregateLeaveDays.of(CloseAtr.PRENATAL, new AttendanceDaysMonth(this.suspendPrenatalDays)));
		fixLeaveDaysList.add(AggregateLeaveDays.of(CloseAtr.POSTPARTUM, new AttendanceDaysMonth(this.suspendPostpartumDays)));
		fixLeaveDaysList.add(AggregateLeaveDays.of(CloseAtr.CHILD_CARE, new AttendanceDaysMonth(this.suspendChildCareDays)));
		fixLeaveDaysList.add(AggregateLeaveDays.of(CloseAtr.CARE, new AttendanceDaysMonth(this.suspendCareDays)));
		fixLeaveDaysList.add(AggregateLeaveDays.of(CloseAtr.INJURY_OR_ILLNESS, new AttendanceDaysMonth(this.suspendInjilnDays)));
		
		anyLeaveDaysList.add(AnyLeave.of(1, new AttendanceDaysMonth(this.suspendAnyDays1)));
		anyLeaveDaysList.add(AnyLeave.of(2, new AttendanceDaysMonth(this.suspendAnyDays2)));
		anyLeaveDaysList.add(AnyLeave.of(3, new AttendanceDaysMonth(this.suspendAnyDays3)));
		anyLeaveDaysList.add(AnyLeave.of(4, new AttendanceDaysMonth(this.suspendAnyDays4)));
		
		return LeaveOfMonthly.of(fixLeaveDaysList, anyLeaveDaysList);
	}
	
	private StgGoStgBackDaysOfMonthly straightDays() {
		
		return StgGoStgBackDaysOfMonthly.of(
					new AttendanceDaysMonth(this.straightGoDays),
					new AttendanceDaysMonth(this.straightBackDays), 
					new AttendanceDaysMonth(this.straightGoBackDays));
	}
	
	private SpecificDaysOfMonthly specficDays() {
		List<AggregateSpecificDays> specificDaysList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			val specDays = FieldReflection.getField(this.getClass(), "specificDays" + i);
			val holWorkSpecDays = FieldReflection.getField(this.getClass(), "specificHdwkDays" + i);
			
			specificDaysList.add(AggregateSpecificDays.of(
					new SpecificDateItemNo(i), 
					new AttendanceDaysMonth(ReflectionUtil.getFieldValue(specDays, this)), 
					new AttendanceDaysMonth(ReflectionUtil.getFieldValue(holWorkSpecDays, this))));
		}
		
		return SpecificDaysOfMonthly.of(specificDaysList);
	}
	
	private AbsenceDaysOfMonthly absenceDays() {
		List<AggregateAbsenceDays> absenceDaysList = new ArrayList<>();
		for (int i = 1; i <= 30; i++) {
			val days = FieldReflection.getField(this.getClass(), "absenceDays" + i);
			val time = FieldReflection.getField(this.getClass(), "absenceTime" + i);
			
			absenceDaysList.add(AggregateAbsenceDays.of(i, 
					new AttendanceDaysMonth(ReflectionUtil.getFieldValue(days, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(time, this))));
		}
		
		return AbsenceDaysOfMonthly.of(new AttendanceDaysMonth(this.absenceTotalDays),
										new AttendanceTimeMonth(this.absenceTotalTime), 
										absenceDaysList);
	}
	
	private WorkClockOfMonthly workClock() {
		
		return WorkClockOfMonthly.of(
				EndClockOfMonthly.of(
						new AttendanceTimesMonth(this.endWorkTimes), 
						new AttendanceTimeMonth(this.endWorkTotalClock),
						new AttendanceTimeMonth(this.endWorkAveClock)),
				PCLogonOfMonthly.of(
						PCLogonClockOfMonthly.of(
								AggrPCLogonClock.of(
										new AttendanceDaysMonth(this.pcLogonTotalDays),
										new AttendanceTimeMonth(this.pcLogonTotalClock), 
										new AttendanceTimeMonth(this.pcLogonAveClock)),
								AggrPCLogonClock.of(
										new AttendanceDaysMonth(this.pcLogoffTotalDays),
										new AttendanceTimeMonth(this.pcLogoffTotalClock), 
										new AttendanceTimeMonth(this.pcLogoffAveClock))),
						PCLogonDivergenceOfMonthly.of(
								AggrPCLogonDivergence.of(
										new AttendanceDaysMonth(this.pcLogonDivDays),
										new AttendanceTimeMonth(this.pcLogonDivTotalTime), 
										new AttendanceTimeMonth(this.pcLogonDivAveTime)), 
								AggrPCLogonDivergence.of(
										new AttendanceDaysMonth(this.pcLogoffDivDays),
										new AttendanceTimeMonth(this.pcLogoffDivTotalTime), 
										new AttendanceTimeMonth(this.pcLogoffDivAveTime)))));
	}

	private WorkTimeOfMonthlyVT workTime() {
		
		return WorkTimeOfMonthlyVT.of(
				bonusPay(), 
				goOut(), 
				premium(), 
				breakTime(), 
				bentou(),
				midNightTime(), 
				lateLeave(),
				attendanceLeaveGate(), 
				BudgetTimeVarienceOfMonthly.of(new AttendanceTimeMonthWithMinus(this.budgetVarienceTime)), 
				divergenceTime(), 
				medicalTime(), 
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
	}
	
	private List<MedicalTimeOfMonthly> medicalTime() {
		List<MedicalTimeOfMonthly> medicalTimeList = new ArrayList<>();
		
		medicalTimeList.add(MedicalTimeOfMonthly.of(
				WorkTimeNightShift.DAY_SHIFT, 
				new AttendanceTimeMonth(this.medicalDayTime), 
				new AttendanceTimeMonth(this.medicalDayDeductTime), 
				new AttendanceTimeMonth(this.medicalDayTakeOverTime)));
		medicalTimeList.add(MedicalTimeOfMonthly.of(
				WorkTimeNightShift.NIGHT_SHIFT, 
				new AttendanceTimeMonth(this.medicalNightTime), 
				new AttendanceTimeMonth(this.medicalNightDeductTime), 
				new AttendanceTimeMonth(this.medicalNightTakeOverTime)));
		
		return medicalTimeList;
	}
	
	private GoOutOfMonthly goOut() {
		List<AggregateGoOut> goOuts = new ArrayList<>();
		List<GoOutForChildCare> goOutForChildCares = new ArrayList<>();
		
		goOuts.add(AggregateGoOut.of(
				GoingOutReason.PRIVATE, 
				new AttendanceTimesMonth(this.gooutTimesPrivate), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutWithinTimePrivate), 
						new AttendanceTimeMonth(this.gooutWithinCalcTimePrivate)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutExcessTimePrivate), 
						new AttendanceTimeMonth(this.gooutExcessCalcTimePrivate)),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutTotalTimePrivate), 
						new AttendanceTimeMonth(this.gooutTotalCalcTimePrivate)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutCoreOutTimePrivate), 
						new AttendanceTimeMonth(this.gooutCoreOutCalcTimePrivate))));
		goOuts.add(AggregateGoOut.of(
				GoingOutReason.PUBLIC, 
				new AttendanceTimesMonth(this.gooutTimesPublic), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutWithinTimePublic), 
						new AttendanceTimeMonth(this.gooutWithinCalcTimePublic)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutExcessTimePublic), 
						new AttendanceTimeMonth(this.gooutExcessCalcTimePublic)),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutTotalTimePublic), 
						new AttendanceTimeMonth(this.gooutTotalCalcTimePublic)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutCoreOutTimePublic), 
						new AttendanceTimeMonth(this.gooutCoreOutCalcTimePublic))));
		goOuts.add(AggregateGoOut.of(
				GoingOutReason.COMPENSATION, 
				new AttendanceTimesMonth(this.gooutTimesCompensation), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutWithinTimeCompensation), 
						new AttendanceTimeMonth(this.gooutWithinCalcTimeCompensation)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutExcessTimeCompensation), 
						new AttendanceTimeMonth(this.gooutExcessCalcTimeCompensation)),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutTotalTimeCompensation), 
						new AttendanceTimeMonth(this.gooutTotalCalcTimeCompensation)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutCoreOutTimeCompensation), 
						new AttendanceTimeMonth(this.gooutCoreOutCalcTimeCompensation))));
		goOuts.add(AggregateGoOut.of(
				GoingOutReason.UNION, 
				new AttendanceTimesMonth(this.gooutTimesUnion), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutWithinTimeUnion), 
						new AttendanceTimeMonth(this.gooutWithinCalcTimeUnion)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutExcessTimeUnion), 
						new AttendanceTimeMonth(this.gooutExcessCalcTimeUnion)),
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutTotalTimeUnion), 
						new AttendanceTimeMonth(this.gooutTotalCalcTimeUnion)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.gooutCoreOutTimeUnion), 
						new AttendanceTimeMonth(this.gooutCoreOutCalcTimeUnion))));
		
		goOutForChildCares.add(GoOutForChildCare.of(
				ChildCareAtr.CARE, 
				new AttendanceTimesMonth(this.gooutCareTimes), 
				new AttendanceTimeMonth(this.gooutCareTime), 
				new AttendanceTimeMonth(this.gooutCareWithinTime), 
				new AttendanceTimeMonth(this.gooutCareExcessTime)));
		goOutForChildCares.add(GoOutForChildCare.of(
				ChildCareAtr.CHILD_CARE, 
				new AttendanceTimesMonth(this.gooutCldcarTimes), 
				new AttendanceTimeMonth(this.gooutCldcarTime), 
				new AttendanceTimeMonth(this.gooutCldcarWithinTime), 
				new AttendanceTimeMonth(this.gooutCldcarExcessTime)));
		
		return GoOutOfMonthly.of(goOuts, goOutForChildCares);
	}
	
	private DivergenceTimeOfMonthly divergenceTime() {
		List<AggregateDivergenceTime> divergenceTimeList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			val flag = FieldReflection.getField(this.getClass(), "divergenceFlag" + i);
			val time = FieldReflection.getField(this.getClass(), "divergenceTime" + i);
			
			divergenceTimeList.add(AggregateDivergenceTime.of(i,
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(time, this)),
					EnumAdaptor.valueOf(ReflectionUtil.getFieldValue(flag, this), DivergenceAtrOfMonthly.class)));
		}
		
		return DivergenceTimeOfMonthly.of(divergenceTimeList);
	}
	
	private LateLeaveEarlyOfMonthly lateLeave() {

		return LateLeaveEarlyOfMonthly.of(
				LeaveEarly.of(
						new AttendanceTimesMonth(this.leaveEarlyTimes), 
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.leaveEarlyTime), 
								new AttendanceTimeMonth(this.leaveEarlyCalcTime))), 
				Late.of(
						new AttendanceTimesMonth(this.lateTimes), 
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.lateTime), 
								new AttendanceTimeMonth(this.lateCalcTime))));
	}

	private MidnightTimeOfMonthly midNightTime() {
		
		return MidnightTimeOfMonthly.of(
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.nightOtTime), 
						new AttendanceTimeMonth(this.nightOtCalcTime)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.nightWithinTime), 
						new AttendanceTimeMonth(this.nightWithinCalcTime)),
				IllegalMidnightTime.of(
						new TimeMonthWithCalculation(
								new AttendanceTimeMonth(this.nightExcessTime), 
								new AttendanceTimeMonth(this.nightExcessCalcTime)), 
						new AttendanceTimeMonth(this.nightExcessBeforeTime)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.nightLegalHolWorkTime), 
						new AttendanceTimeMonth(this.nightLegalHolWorkCalcTime)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.nightIllegalHolWorkTime), 
						new AttendanceTimeMonth(this.nightIllegalHolWorkCalcTime)), 
				new TimeMonthWithCalculation(
						new AttendanceTimeMonth(this.nightSpecHolWorkTime), 
						new AttendanceTimeMonth(this.nightSpecHolWorkCalcTime)));
	}
	
	private BreakTimeOfMonthly breakTime() {
		
		return BreakTimeOfMonthly.of(
				new AttendanceTimesMonth(this.breakTime), 
				new AttendanceTimeMonth(this.breakTime),
				new AttendanceTimeMonth(this.breakWithinTime), 
				new AttendanceTimeMonth(this.breakWithinDeducTime),
				new AttendanceTimeMonth(this.breakExcessTime),
				new AttendanceTimeMonth(this.breakExcessDeducTime));
	}
	
	private BonusPayTimeOfMonthly bonusPay() {
		List<AggregateBonusPayTime> bonusPayTime = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			val time = FieldReflection.getField(this.getClass(), "bonusPayTime" + i);
			val spec = FieldReflection.getField(this.getClass(), "bonusSpecPayTime" + i);
			val excess = FieldReflection.getField(this.getClass(), "bonusPayExcessTime" + i);
			val excessSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecExcessTime" + i);
			val holWork = FieldReflection.getField(this.getClass(), "bonusPayHolWorkTime" + i);
			val holWorkSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecHolWorkTime" + i);
			val within = FieldReflection.getField(this.getClass(), "bonusPayWithinTime" + i);
			val withinSpec = FieldReflection.getField(this.getClass(), "bonusPaySpecWithinTime" + i);

			bonusPayTime.add(AggregateBonusPayTime.of(i,
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(time, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(spec, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(holWork, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(holWorkSpec, this)),
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(within, this)), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(withinSpec, this)),
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(excess, this)),
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(excessSpec, this))));
		}
		
		return BonusPayTimeOfMonthly.of(bonusPayTime);
	}
	
	private AttendanceLeaveGateTimeOfMonthly attendanceLeaveGate() {
		
		return AttendanceLeaveGateTimeOfMonthly.of(
				new AttendanceTimeMonth(this.algtBeforeAttendanceTime), 
				new AttendanceTimeMonth(this.algtAfterLeaveTime), 
				new AttendanceTimeMonth(this.algtStayingTime),
				new AttendanceTimeMonth(this.algtUnemployedTime));
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
	
	private PremiumTimeOfMonthly premium() {
		List<AggregatePremiumTime> premiumTimes = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			val time = FieldReflection.getField(this.getClass(), "premiumTime" + i);
			val amount = FieldReflection.getField(this.getClass(), "premiumAmount" + i);
			
			premiumTimes.add(AggregatePremiumTime.of(ExtraTimeItemNo.valueOf(i), 
					new AttendanceTimeMonth(ReflectionUtil.getFieldValue(time, this)), 
					new AttendanceAmountMonth(ReflectionUtil.getFieldValue(amount, this))));
		}
		
		return PremiumTimeOfMonthly.of(premiumTimes, new AttendanceAmountMonth(this.premiumAmountTotal));
	}

}
