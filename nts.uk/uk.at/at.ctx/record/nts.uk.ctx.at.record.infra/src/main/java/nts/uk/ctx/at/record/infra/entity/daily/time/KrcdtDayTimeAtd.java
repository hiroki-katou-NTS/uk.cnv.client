package nts.uk.ctx.at.record.infra.entity.daily.time;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.val;
import nts.gul.reflection.FieldReflection;
import nts.gul.text.StringUtil;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.infra.entity.breakorgoout.KrcdtDayTimeGoout;
import nts.uk.ctx.at.record.infra.entity.daily.latetime.KrcdtDayLateTime;
import nts.uk.ctx.at.record.infra.entity.daily.leaveearlytime.KrcdtDayLeaveEarlyTime;
import nts.uk.ctx.at.record.infra.entity.daily.premiumtime.KrcdtDayTimePremium;
import nts.uk.ctx.at.record.infra.entity.daily.shortwork.KrcdtDaiShortWorkTime;
import nts.uk.ctx.at.record.infra.entity.daily.shortwork.KrcdtDayShorttime;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceReasonContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.temporarytime.TemporaryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AbsenceOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AnnualOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.OverSalaryOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SpecialHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SubstituteHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TimeDigestOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TransferHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.YearlyReservedOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.StayingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.shr.com.time.AttendanceClock;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KRCDT_DAY_TIME_ATD")
public class KrcdtDayTimeAtd extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/* 主キー */
	@EmbeddedId
	public KrcdtDayTimePK krcdtDayTimePK;
	
	/*----------------------日別実績の勤怠時間------------------------------*/
	/* 総労働時間 */
	@Column(name = "TOTAL_ATT_TIME")
	public int totalAttTime;
	/* 総計算時間 */
	@Column(name = "TOTAL_CALC_TIME")
	public int totalCalcTime;
	/* 実働時間 */
	@Column(name = "ACTWORK_TIME")
	public int actWorkTime;
	/* 勤務回数 */
	@Column(name = "WORK_TIMES")
	public int workTimes;
	/* 総拘束時間 */
	@Column(name = "TOTAL_BIND_TIME")
	public int totalBindTime;
	/* 深夜拘束時間 */
	@Column(name = "MIDN_BIND_TIME")
	public int midnBindTime;
	/* 拘束差異時間 */
	@Column(name = "BIND_DIFF_TIME")
	public int bindDiffTime;
	/* 時差勤務時間 */
	@Column(name = "DIFF_TIME_WORK_TIME")
	public int diffTimeWorkTime;
	/* 所定外深夜時間 */
	@Column(name = "OUT_PRS_MIDN_TIME")
	public int outPrsMidnTime;
	/* 計算所定外深夜時間 */
	@Column(name = "CALC_OUT_PRS_MIDN_TIME")
	public int calcOutPrsMidnTime;
	/* 事前所定外深夜時間 */
	@Column(name = "PRE_OUT_PRS_MIDN_TIME")
	public int preOutPrsMidnTime;
	/* 予実差異時間 */
	@Column(name = "BUDGET_TIME_VARIANCE")
	public int budgetTimeVariance;
	/* 不就労時間 */
	@Column(name = "UNEMPLOYED_TIME")
	public int unemployedTime;
	/* 滞在時間 */
	@Column(name = "STAYING_TIME")
	public int stayingTime;
	/* 出勤前時間 */
	@Column(name = "BFR_WORK_TIME")
	public int bfrWorkTime;
	/* 退勤後時間 */
	@Column(name = "AFT_LEAVE_TIME")
	public int aftLeaveTime;
	/* PCログオン前時間 */
	@Column(name = "BFR_PC_LOGON_TIME")
	public int bfrPcLogonTime;
	/* PCログオフ後時間 */
	@Column(name = "AFT_PC_LOGOFF_TIME")
	public int aftPcLogoffTime;
	/*所定外深夜乖離時間*/
	@Column(name = "DIV_OUT_PRS_MIDN_TIME")
	public int divOutPrsMidnTime;
	/*----------------------日別実績の勤怠時間------------------------------*/
	
	/*----------------------日別実績の休憩時間帯------------------------------*/
	@Column(name = "TO_RECORD_TOTAL_TIME")
	public int toRecordTotalTime;
	@Column(name ="TO_RECORD_IN_TIME")
	public int toRecordInTime;
	@Column(name ="TO_RECORD_OUT_TIME")
	public int toRecordOutTime;
	
	@Column(name ="DEDUCTION_TOTAL_TIME")
	public int deductionTotalTime;
	@Column(name ="DEDUCTION_IN_TIME")
	public int deductionInTime;
	@Column(name ="DEDUCTION_OUT_TIME")
	public int deductionOutTime;
	
	@Column(name ="CAL_TO_RECORD_TOTAL_TIME")
	public int calToRecordTotalTime;
	@Column(name ="CAL_TO_RECORD_IN_TIME")
	public int calToRecordInTime;
	@Column(name ="CAL_TO_RECORD_OUT_TIME")
	public int calToRecordOutTime;
	
	@Column(name ="CAL_DEDUCTION_TOTAL_TIME")
	public int calDeductionTotalTime;
	@Column(name ="CAL_DEDUCTION_IN_TIME")
	public int calDeductionInTime;
	@Column(name ="CAL_DEDUCTION_OUT_TIME")
	public int calDeductionOutTime;
	
	@Column(name ="DURINGWORK_TIME")
	public int duringworkTime;
	
	@Column(name ="COUNT")
	public int count;
	/*----------------------日別実績の休憩時間帯------------------------------*/
	
	/*----------------------日別実績の休出枠時間------------------------------*/
	/*休日出勤時間1*/
	@Column(name = "HOLI_WORK_TIME_1")
	public int holiWorkTime1;
	/*休日出勤時間2*/
	@Column(name = "HOLI_WORK_TIME_2")
	public int holiWorkTime2;
	/*休日出勤時間3*/
	@Column(name = "HOLI_WORK_TIME_3")
	public int holiWorkTime3;
	/*休日出勤時間4*/
	@Column(name = "HOLI_WORK_TIME_4")
	public int holiWorkTime4;
	/*休日出勤時間5*/
	@Column(name = "HOLI_WORK_TIME_5")
	public int holiWorkTime5;
	/*休日出勤時間6*/
	@Column(name = "HOLI_WORK_TIME_6")
	public int holiWorkTime6;
	/*休日出勤時間7*/
	@Column(name = "HOLI_WORK_TIME_7")
	public int holiWorkTime7;
	/*休日出勤時間8*/
	@Column(name = "HOLI_WORK_TIME_8")
	public int holiWorkTime8;
	/*休日出勤時間9*/
	@Column(name = "HOLI_WORK_TIME_9")
	public int holiWorkTime9;
	/*休日出勤時間10*/
	@Column(name = "HOLI_WORK_TIME_10")
	public int holiWorkTime10;
	/*振替時間1*/
	@Column(name = "TRANS_TIME_1")
	public int transTime1;
	/*振替時間2*/
	@Column(name = "TRANS_TIME_2")
	public int transTime2;
	/*振替時間3*/
	@Column(name = "TRANS_TIME_3")
	public int transTime3;
	/*振替時間4*/
	@Column(name = "TRANS_TIME_4")
	public int transTime4;
	/*振替時間5*/
	@Column(name = "TRANS_TIME_5")
	public int transTime5;
	/*振替時間6*/
	@Column(name = "TRANS_TIME_6")
	public int transTime6;
	/*振替時間7*/
	@Column(name = "TRANS_TIME_7")
	public int transTime7;
	/*振替時間8*/
	@Column(name = "TRANS_TIME_8")
	public int transTime8;
	/*振替時間9*/
	@Column(name = "TRANS_TIME_9")
	public int transTime9;
	/*振替時間10*/
	@Column(name = "TRANS_TIME_10")
	public int transTime10;
	/*計算休日出勤時間1*/
	@Column(name = "CALC_HOLI_WORK_TIME_1")
	public int calcHoliWorkTime1;
	/*計算休日出勤時間2*/
	@Column(name = "CALC_HOLI_WORK_TIME_2")
	public int calcHoliWorkTime2;
	/*計算休日出勤時間3*/
	@Column(name = "CALC_HOLI_WORK_TIME_3")
	public int calcHoliWorkTime3;
	/*計算休日出勤時間4*/
	@Column(name = "CALC_HOLI_WORK_TIME_4")
	public int calcHoliWorkTime4;
	/*計算休日出勤時間5*/
	@Column(name = "CALC_HOLI_WORK_TIME_5")
	public int calcHoliWorkTime5;
	/*計算休日出勤時間6*/
	@Column(name = "CALC_HOLI_WORK_TIME_6")
	public int calcHoliWorkTime6;
	/*計算休日出勤時間7*/
	@Column(name = "CALC_HOLI_WORK_TIME_7")
	public int calcHoliWorkTime7;
	/*計算休日出勤時間8*/
	@Column(name = "CALC_HOLI_WORK_TIME_8")
	public int calcHoliWorkTime8;
	/*計算休日出勤時間9*/
	@Column(name = "CALC_HOLI_WORK_TIME_9")
	public int calcHoliWorkTime9;
	/*計算休日出勤時間10*/
	@Column(name = "CALC_HOLI_WORK_TIME_10")
	public int calcHoliWorkTime10;
	/*計算振替時間1*/
	@Column(name = "CALC_TRANS_TIME_1")
	public int calcTransTime1;
	/*計算振替時間2*/
	@Column(name = "CALC_TRANS_TIME_2")
	public int calcTransTime2;
	/*計算振替時間3*/
	@Column(name = "CALC_TRANS_TIME_3")
	public int calcTransTime3;
	/*計算振替時間4*/
	@Column(name = "CALC_TRANS_TIME_4")
	public int calcTransTime4;
	/*計算振替時間5*/
	@Column(name = "CALC_TRANS_TIME_5")
	public int calcTransTime5;
	/*計算振替時間6*/
	@Column(name = "CALC_TRANS_TIME_6")
	public int calcTransTime6;
	/*計算振替時間7*/
	@Column(name = "CALC_TRANS_TIME_7")
	public int calcTransTime7;
	/*計算振替時間8*/
	@Column(name = "CALC_TRANS_TIME_8")
	public int calcTransTime8;
	/*計算振替時間9*/
	@Column(name = "CALC_TRANS_TIME_9")
	public int calcTransTime9;
	/*計算振替時間10*/
	@Column(name = "CALC_TRANS_TIME_10")
	public int calcTransTime10;
	/*事前申請時間1*/
	@Column(name = "PRE_APP_TIME_1")
	public int preAppTime1;
	/*事前申請時間2*/
	@Column(name = "PRE_APP_TIME_2")
	public int preAppTime2;
	/*事前申請時間3*/
	@Column(name = "PRE_APP_TIME_3")
	public int preAppTime3;
	/*事前申請時間4*/
	@Column(name = "PRE_APP_TIME_4")
	public int preAppTime4;
	/*事前申請時間5*/
	@Column(name = "PRE_APP_TIME_5")
	public int preAppTime5;
	/*事前申請時間6*/
	@Column(name = "PRE_APP_TIME_6")
	public int preAppTime6;
	/*事前申請時間7*/
	@Column(name = "PRE_APP_TIME_7")
	public int preAppTime7;
	/*事前申請時間8*/
	@Column(name = "PRE_APP_TIME_8")
	public int preAppTime8;
	/*事前申請時間9*/
	@Column(name = "PRE_APP_TIME_9")
	public int preAppTime9;
	/*事前申請時間10*/
	@Column(name = "PRE_APP_TIME_10")
	public int preAppTime10;
	/*法定内休日出勤深夜*/
	@Column(name = "LEG_HOLI_WORK_MIDN")
	public int legHoliWorkMidn;
	/*法定外休日出勤深夜*/
	@Column(name = "ILLEG_HOLI_WORK_MIDN")
	public int illegHoliWorkMidn;
	/*祝日日出勤深夜*/
	@Column(name = "PB_HOLI_WORK_MIDN")
	public int pbHoliWorkMidn;
	/*計算法定内休日出勤深夜*/
	@Column(name = "CALC_LEG_HOLI_WORK_MIDN")
	public int calcLegHoliWorkMidn;
	/*計算法定外休日出勤深夜*/
	@Column(name = "CALC_ILLEG_HOLI_WORK_MIDN")
	public int calcIllegHoliWorkMidn;
	/*計算祝日日出勤深夜*/
	@Column(name = "CALC_PB_HOLI_WORK_MIDN")
	public int calcPbHoliWorkMidn;
	/*休日出勤拘束時間*/
	@Column(name = "HOLI_WORK_BIND_TIME")
	public int holiWorkBindTime;
	/*休日出勤乖離時間１*/
	@Column(name = "DIV_HOLI_TIME_1")
	public int divHoliTime1;
	/*休日出勤乖離時間2*/
	@Column(name = "DIV_HOLI_TIME_2")
	public int divHoliTime2;
	/*休日出勤乖離時間3*/
	@Column(name = "DIV_HOLI_TIME_3")
	public int divHoliTime3;
	/*休日出勤乖離時間4*/
	@Column(name = "DIV_HOLI_TIME_4")
	public int divHoliTime4;
	/*休日出勤乖離時間5*/
	@Column(name = "DIV_HOLI_TIME_5")
	public int divHoliTime5;
	/*休日出勤乖離時間6*/
	@Column(name = "DIV_HOLI_TIME_6")
	public int divHoliTime6;
	/*休日出勤乖離時間7*/
	@Column(name = "DIV_HOLI_TIME_7")
	public int divHoliTime7;
	/*休日出勤乖離時間8*/
	@Column(name = "DIV_HOLI_TIME_8")
	public int divHoliTime8;
	/*休日出勤乖離時間9*/
	@Column(name = "DIV_HOLI_TIME_9")
	public int divHoliTime9;
	/*休日出勤乖離時間１0*/
	@Column(name = "DIV_HOLI_TIME_10")
	public int divHoliTime10;
	/*振替乖離時間１*/
	@Column(name = "DIV_HOLI_TRANS_TIME_1")
	public int divHoliTransTime1;
	/*振替乖離時間2*/
	@Column(name = "DIV_HOLI_TRANS_TIME_2")
	public int divHoliTransTime2;
	/*振替乖離時間3*/
	@Column(name = "DIV_HOLI_TRANS_TIME_3")
	public int divHoliTransTime3;
	/*振替乖離時間4*/
	@Column(name = "DIV_HOLI_TRANS_TIME_4")
	public int divHoliTransTime4;
	/*振替乖離時間5*/
	@Column(name = "DIV_HOLI_TRANS_TIME_5")
	public int divHoliTransTime5;
	/*振替乖離時間6*/
	@Column(name = "DIV_HOLI_TRANS_TIME_6")
	public int divHoliTransTime6;
	/*振替乖離時間7*/
	@Column(name = "DIV_HOLI_TRANS_TIME_7")
	public int divHoliTransTime7;
	/*振替乖離時間8*/
	@Column(name = "DIV_HOLI_TRANS_TIME_8")
	public int divHoliTransTime8;
	/*振替乖離時間9*/
	@Column(name = "DIV_HOLI_TRANS_TIME_9")
	public int divHoliTransTime9;
	/*振替乖離時間１0*/
	@Column(name = "DIV_HOLI_TRANS_TIME_10")
	public int divHoliTransTime10;
	/*法定内休日出勤深夜乖離時間*/
	@Column(name = "DIV_LEG_HOLI_WORK_MIDN")
	public int divLegHoliWorkMidn;
	/*法定外休日出勤深夜乖離時間*/
	@Column(name = "DIV_ILLEG_HOLI_WORK_MIDN")
	public int divIllegHoliWorkMidn;
	/*祝日出勤深夜乖離時間*/
	@Column(name = "DIV_PB_HOLI_WORK_MIDN")
	public int divPbHoliWorkMidn;
	/*----------------------日別実績の休出枠時間------------------------------*/
	
	/*----------------------日別実績の休暇時間------------------------------*/
	/* 年休使用時間 */
	@Column(name = "ANNUALLEAVE_TIME")
	public int annualleaveTime;
	/* 年休時間消化休暇使用時間 */
	@Column(name = "ANNUALLEAVE_TDV_TIME")
	public int annualleaveTdvTime;
	/* 代休使用時間 */
	@Column(name = "COMPENSATORYLEAVE_TIME")
	public int compensatoryLeaveTime;
	/* 代休時間消化休暇使用時間 */
	@Column(name = "COMPENSATORYLEAVE_TDV_TIME")
	public int compensatoryLeaveTdvTime;
	/* 特別休暇使用時間 */
	@Column(name = "SPECIALHOLIDAY_TIME")
	public int specialHolidayTime;
	/* 特別休暇時間消化休暇使用時間 */
	@Column(name = "SPECIALHOLIDAY_TDV_TIME")
	public int specialHolidayTdvTime;
	/* 超過有休使用時間 */
	@Column(name = "EXCESSSALARIES_TIME")
	public int excessSalaryiesTime;
	/* 超過有休時間消化休暇使用時間*/
	@Column(name = "EXCESSSALARIES_TDV_TIME")
	public int excessSalaryiesTdvTime;
	/* 積立年休使用時間*/
	@Column(name = "RETENTIONYEARLY_TIME")
	public int retentionYearlyTime;
	/* 欠勤使用時間*/
	@Column(name = "ABSENCE_TIME")
	public int absenceTime;
	/* 時間消化休暇使用時間*/
	@Column(name = "TDV_TIME")
	public int tdvTime;
	/* 時間消化休暇不足時間*/
	@Column(name = "TDV_SHORTAGE_TIME")
	public int tdvShortageTime;
	/*----------------------日別実績の休暇時間------------------------------*/
	
	/*----------------------日別実績の予定時間------------------------------*/
	/*勤務予定時間*/
	@Column(name = "WORK_SCHEDULE_TIME")
	public int workScheduleTime;
	/*計画所定労働時間*/
	@Column(name = "SCHEDULE_PRE_LABOR_TIME")
	public int schedulePreLaborTime;
	/*実績所定労働時間*/
	@Column(name = "RECORE_PRE_LABOR_TIME")
	public int recorePreLaborTime;
	/*----------------------日別実績の予定時間------------------------------*/
	
	/*----------------------日別実績の所定内時間------------------------------*/
	/*就業時間*/
	@Column(name = "WORK_TIME")
	public int workTime;
	/*実働就業時間*/
	@Column(name = "PEFOM_WORK_TIME")
	public int pefomWorkTime;
	/*所定内割増時間*/
	@Column(name = "PRS_INCLD_PRMIM_TIME")
	public int prsIncldPrmimTime;
	/*所定内深夜時間*/
	@Column(name = "PRS_INCLD_MIDN_TIME")
	public int prsIncldMidnTime;
	/*休暇加算時間*/
	@Column(name = "VACTN_ADD_TIME")
	public int vactnAddTime;
	/*計算所定内深夜時間*/
	@Column(name = "CALC_PRS_INCLD_MIDN_TIME")
	public int calcPrsIncldMidnTime;
	/*所定内深夜乖離時間*/
	@Column(name = "DIV_PRS_INCLD_MIDN_TIME")
	public int divPrsIncldMidnTime;
	/*----------------------日別実績の所定内時間------------------------------*/
	
	/*----------------------日別実績の残業時間------------------------------*/
	/*残業時間1*/
	@Column(name = "OVER_TIME_1")
	public int overTime1;
	/*残業時間2*/
	@Column(name = "OVER_TIME_2")
	public int overTime2;
	/*残業時間3*/
	@Column(name = "OVER_TIME_3")
	public int overTime3;
	/*残業時間4*/
	@Column(name = "OVER_TIME_4")
	public int overTime4;
	/*残業時間5*/
	@Column(name = "OVER_TIME_5")
	public int overTime5;
	/*残業時間6*/
	@Column(name = "OVER_TIME_6")
	public int overTime6;
	/*残業時間7*/
	@Column(name = "OVER_TIME_7")
	public int overTime7;
	/*残業時間8*/
	@Column(name = "OVER_TIME_8")
	public int overTime8;
	/*残業時間9*/
	@Column(name = "OVER_TIME_9")
	public int overTime9;
	/*残業時間10*/
	@Column(name = "OVER_TIME_10")
	public int overTime10;
	/*振替時間1*/
	@Column(name = "TRANS_OVER_TIME_1")
	public int transOverTime1;
	/*振替時間2*/
	@Column(name = "TRANS_OVER_TIME_2")
	public int transOverTime2;
	/*振替時間3*/
	@Column(name = "TRANS_OVER_TIME_3")
	public int transOverTime3;
	/*振替時間4*/
	@Column(name = "TRANS_OVER_TIME_4")
	public int transOverTime4;
	/*振替時間5*/
	@Column(name = "TRANS_OVER_TIME_5")
	public int transOverTime5;
	/*振替時間6*/
	@Column(name = "TRANS_OVER_TIME_6")
	public int transOverTime6;
	/*振替時間7*/
	@Column(name = "TRANS_OVER_TIME_7")
	public int transOverTime7;
	/*振替時間8*/
	@Column(name = "TRANS_OVER_TIME_8")
	public int transOverTime8;
	/*振替時間9*/
	@Column(name = "TRANS_OVER_TIME_9")
	public int transOverTime9;
	/*振替時間10*/
	@Column(name = "TRANS_OVER_TIME_10")
	public int transOverTime10;
	/*計算残業時間1*/
	@Column(name = "CALC_OVER_TIME_1")
	public int calcOverTime1;
	/*計算残業時間2*/
	@Column(name = "CALC_OVER_TIME_2")
	public int calcOverTime2;
	/*計算残業時間3*/
	@Column(name = "CALC_OVER_TIME_3")
	public int calcOverTime3;
	/*計算残業時間4*/
	@Column(name = "CALC_OVER_TIME_4")
	public int calcOverTime4;
	/*計算残業時間5*/
	@Column(name = "CALC_OVER_TIME_5")
	public int calcOverTime5;
	/*計算残業時間6*/
	@Column(name = "CALC_OVER_TIME_6")
	public int calcOverTime6;
	/*計算残業時間7*/
	@Column(name = "CALC_OVER_TIME_7")
	public int calcOverTime7;
	/*計算残業時間8*/
	@Column(name = "CALC_OVER_TIME_8")
	public int calcOverTime8;
	/*計算残業時間9*/
	@Column(name = "CALC_OVER_TIME_9")
	public int calcOverTime9;
	/*計算残業時間10*/
	@Column(name = "CALC_OVER_TIME_10")
	public int calcOverTime10;
	/*計算振替時間1*/
	@Column(name = "CALC_TRANS_OVER_TIME_1")
	public int calcTransOverTime1;
	/*計算振替時間2*/
	@Column(name = "CALC_TRANS_OVER_TIME_2")
	public int calcTransOverTime2;
	/*計算振替時間3*/
	@Column(name = "CALC_TRANS_OVER_TIME_3")
	public int calcTransOverTime3;
	/*計算振替時間4*/
	@Column(name = "CALC_TRANS_OVER_TIME_4")
	public int calcTransOverTime4;
	/*計算振替時間5*/
	@Column(name = "CALC_TRANS_OVER_TIME_5")
	public int calcTransOverTime5;
	/*計算振替時間6*/
	@Column(name = "CALC_TRANS_OVER_TIME_6")
	public int calcTransOverTime6;
	/*計算振替時間7*/
	@Column(name = "CALC_TRANS_OVER_TIME_7")
	public int calcTransOverTime7;
	/*計算振替時間8*/
	@Column(name = "CALC_TRANS_OVER_TIME_8")
	public int calcTransOverTime8;
	/*計算振替時間9*/
	@Column(name = "CALC_TRANS_OVER_TIME_9")
	public int calcTransOverTime9;
	/*計算振替時間10*/
	@Column(name = "CALC_TRANS_OVER_TIME_10")
	public int calcTransOverTime10;
	/*事前残業申請時間1*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_1")
	public int preOverTimeAppTime1;
	/*事前残業申請時間2*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_2")
	public int preOverTimeAppTime2;
	/*事前残業申請時間3*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_3")
	public int preOverTimeAppTime3;
	/*事前残業申請時間4*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_4")
	public int preOverTimeAppTime4;
	/*事前残業申請時間5*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_5")
	public int preOverTimeAppTime5;
	/*事前残業申請時間6*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_6")
	public int preOverTimeAppTime6;
	/*事前残業申請時間7*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_7")
	public int preOverTimeAppTime7;
	/*事前残業申請時間8*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_8")
	public int preOverTimeAppTime8;
	/*事前残業申請時間9*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_9")
	public int preOverTimeAppTime9;
	/*事前残業申請時間10*/
	@Column(name = "PRE_OVER_TIME_APP_TIME_10")
	public int preOverTimeAppTime10;
	/*法定外残業深夜時間*/
	@Column(name = "ILEGL_MIDN_OVER_TIME")
	public int ileglMidntOverTime;
	/*計算法定外残業深夜時間*/
	@Column(name = "CALC_ILEGL_MIDN_OVER_TIME")
	public int calcIleglMidNOverTime;
	/*残業拘束時間*/
	@Column(name = "OVER_TIME_BIND_TIME")
	public int overTimeBindTime;
	/*変形法定内残業*/
	@Column(name = "DEFORM_LEGL_OVER_TIME")
	public int deformLeglOverTime;
	/*フレックス時間*/
	@Column(name = "FLEX_TIME")
	public int flexTime;
	/*計算フレックス時間*/
	@Column(name = "CALC_FLEX_TIME")
	public int calcFlexTime;
	/*事前申請フレックス時間*/
	@Column(name = "PRE_APP_FLEX_TIME")
	public int preAppFlexTime;
	/*残業乖離時間1*/
	@Column(name = "DIV_OVER_TIME_1")
	public int divOverTime1;
	/*残業乖離時間2*/
	@Column(name = "DIV_OVER_TIME_2")
	public int divOverTime2;
	/*残業乖離時間3*/
	@Column(name = "DIV_OVER_TIME_3")
	public int divOverTime3;
	/*残業乖離時間4*/
	@Column(name = "DIV_OVER_TIME_4")
	public int divOverTime4;
	/*残業乖離時間5*/
	@Column(name = "DIV_OVER_TIME_5")
	public int divOverTime5;
	/*残業乖離時間6*/
	@Column(name = "DIV_OVER_TIME_6")
	public int divOverTime6;
	/*残業乖離時間7*/
	@Column(name = "DIV_OVER_TIME_7")
	public int divOverTime7;
	/*残業乖離時間8*/
	@Column(name = "DIV_OVER_TIME_8")
	public int divOverTime8;
	/*残業乖離時間9*/
	@Column(name = "DIV_OVER_TIME_9")
	public int divOverTime9;
	/*残業乖離時間10*/
	@Column(name = "DIV_OVER_TIME_10")
	public int divOverTime10;
	/*振替乖離時間１*/
	@Column(name = "DIV_TRANS_OVER_TIME_1")
	public int divTransOverTime1;
	/*振替乖離時間2*/
	@Column(name = "DIV_TRANS_OVER_TIME_2")
	public int divTransOverTime2;
	/*振替乖離時間3*/
	@Column(name = "DIV_TRANS_OVER_TIME_3")
	public int divTransOverTime3;
	/*振替乖離時間4*/
	@Column(name = "DIV_TRANS_OVER_TIME_4")
	public int divTransOverTime4;
	/*振替乖離時間5*/
	@Column(name = "DIV_TRANS_OVER_TIME_5")
	public int divTransOverTime5;
	/*振替乖離時間6*/
	@Column(name = "DIV_TRANS_OVER_TIME_6")
	public int divTransOverTime6;
	/*振替乖離時間7*/
	@Column(name = "DIV_TRANS_OVER_TIME_7")
	public int divTransOverTime7;
	/*振替乖離時間8*/
	@Column(name = "DIV_TRANS_OVER_TIME_8")
	public int divTransOverTime8;
	/*振替乖離時間9*/
	@Column(name = "DIV_TRANS_OVER_TIME_9")
	public int divTransOverTime9;
	/*振替乖離時間１0*/
	@Column(name = "DIV_TRANS_OVER_TIME_10")
	public int divTransOverTime10;
	/*フレックス乖離時間*/
	@Column(name = "DIVERGENCE_FLEX_TIME")
	public int divergenceFlexTime;
	/*法定外残業深夜乖離時間*/
	@Column(name = "DIV_ILEGL_MIDN_OVER_TIME")
	public int divIleglMidnOverTime;
	/*----------------------日別実績の残業時間------------------------------*/
	
	/*----------------------日別実績の乖離時間------------------------------*/
	/*乖離時間１*/
	@Column(name = "DIVERGENCE_TIME1")
	public int divergenceTime1;
	/*乖離時間2*/
	@Column(name = "DIVERGENCE_TIME2")
	public int divergenceTime2;
	/*乖離時間3*/
	@Column(name = "DIVERGENCE_TIME3")
	public int divergenceTime3;
	/*乖離時間4*/
	@Column(name = "DIVERGENCE_TIME4")
	public int divergenceTime4;
	/*乖離時間5*/
	@Column(name = "DIVERGENCE_TIME5")
	public int divergenceTime5;
	/*乖離時間6*/
	@Column(name = "DIVERGENCE_TIME6")
	public int divergenceTime6;
	/*乖離時間7*/
	@Column(name = "DIVERGENCE_TIME7")
	public int divergenceTime7;
	/*乖離時間8*/
	@Column(name = "DIVERGENCE_TIME8")
	public int divergenceTime8;
	/*乖離時間9*/
	@Column(name = "DIVERGENCE_TIME9")
	public int divergenceTime9;
	/*乖離時間１0*/
	@Column(name = "DIVERGENCE_TIME10")
	public int divergenceTime10;
	
	/*乖離理由コード1*/
	@Column(name = "REASON_CODE1")
	public String reasonCode1;
	/*乖離理由コード2*/
	@Column(name = "REASON_CODE2")
	public String reasonCode2;
	/*乖離理由コード3*/
	@Column(name = "REASON_CODE3")
	public String reasonCode3;
	/*乖離理由コード4*/
	@Column(name = "REASON_CODE4")
	public String reasonCode4;
	/*乖離理由コード5*/
	@Column(name = "REASON_CODE5")
	public String reasonCode5;
	/*乖離理由コード6*/
	@Column(name = "REASON_CODE6")
	public String reasonCode6;
	/*乖離理由コード7*/
	@Column(name = "REASON_CODE7")
	public String reasonCode7;
	/*乖離理由コード8*/
	@Column(name = "REASON_CODE8")
	public String reasonCode8;
	/*乖離理由コード9*/
	@Column(name = "REASON_CODE9")
	public String reasonCode9;
	/*乖離理由コード10*/
	@Column(name = "REASON_CODE10")
	public String reasonCode10;

	/*乖離理由1*/
	@Column(name = "REASON1")
	public String reason1;
	/*乖離理由2*/
	@Column(name = "REASON2")
	public String reason2;
	/*乖離理由3*/
	@Column(name = "REASON3")
	public String reason3;
	/*乖離理由4*/
	@Column(name = "REASON4")
	public String reason4;
	/*乖離理由5*/
	@Column(name = "REASON5")
	public String reason5;
	/*乖離理由6*/
	@Column(name = "REASON6")
	public String reason6;
	/*乖離理由7*/
	@Column(name = "REASON7")
	public String reason7;
	/*乖離理由8*/
	@Column(name = "REASON8")
	public String reason8;
	/*乖離理由9*/
	@Column(name = "REASON9")
	public String reason9;
	/*乖離理由10*/
	@Column(name = "REASON10")
	public String reason10;
	/*----------------------日別実績の乖離時間------------------------------*/
	
	/*----------------------日別実績の休出時間帯------------------------------*/
	/*休日出勤1開始時刻*/
	@Column(name = "HOLI_WORK_1_STR_CLC")
	public Integer holiWork1StrClc;
	/*休日出勤1終了時刻*/
	@Column(name = "HOLI_WORK_1_END_CLC")
	public Integer holiWork1EndClc;
	/*休日出勤2開始時刻*/
	@Column(name = "HOLI_WORK_2_STR_CLC")
	public Integer holiWork2StrClc;
	/*休日出勤2終了時刻*/
	@Column(name = "HOLI_WORK_2_END_CLC")
	public Integer holiWork2EndClc;
	/*休日出勤3開始時刻*/
	@Column(name = "HOLI_WORK_3_STR_CLC")
	public Integer holiWork3StrClc;
	/*休日出勤3終了時刻*/
	@Column(name = "HOLI_WORK_3_END_CLC")
	public Integer holiWork3EndClc;
	/*休日出勤4開始時刻*/
	@Column(name = "HOLI_WORK_4_STR_CLC")
	public Integer holiWork4StrClc;
	/*休日出勤4終了時刻*/
	@Column(name = "HOLI_WORK_4_END_CLC")
	public Integer holiWork4EndClc;
	/*休日出勤5開始時刻*/
	@Column(name = "HOLI_WORK_5_STR_CLC")
	public Integer holiWork5StrClc;
	/*休日出勤5終了時刻*/
	@Column(name = "HOLI_WORK_5_END_CLC")
	public Integer holiWork5EndClc;
	/*休日出勤6開始時刻*/
	@Column(name = "HOLI_WORK_6_STR_CLC")
	public Integer holiWork6StrClc;
	/*休日出勤6終了時刻*/
	@Column(name = "HOLI_WORK_6_END_CLC")
	public Integer holiWork6EndClc;
	/*休日出勤7開始時刻*/
	@Column(name = "HOLI_WORK_7_STR_CLC")
	public Integer holiWork7StrClc;
	/*休日出勤7終了時刻*/
	@Column(name = "HOLI_WORK_7_END_CLC")
	public Integer holiWork7EndClc;
	/*休日出勤8開始時刻*/
	@Column(name = "HOLI_WORK_8_STR_CLC")
	public Integer holiWork8StrClc;
	/*休日出勤8終了時刻*/
	@Column(name = "HOLI_WORK_8_END_CLC")
	public Integer holiWork8EndClc;
	/*休日出勤9開始時刻*/
	@Column(name = "HOLI_WORK_9_STR_CLC")
	public Integer holiWork9StrClc;
	/*休日出勤9終了時刻*/
	@Column(name = "HOLI_WORK_9_END_CLC")
	public Integer holiWork9EndClc;
	/*休日出勤10開始時刻*/
	@Column(name = "HOLI_WORK_10_STR_CLC")
	public Integer holiWork10StrClc;
	/*休日出勤10終了時刻*/
	@Column(name = "HOLI_WORK_10_END_CLC")
	public Integer holiWork10EndClc;
	/*----------------------日別実績の休出時間帯------------------------------*/
	
	/*----------------------日別実績の残業時間帯------------------------------*/
	/*残業1開始時刻*/
	@Column(name = "OVER_TIME_1_STR_CLC")
	public Integer overTime1StrClc;
	/*残業1終了時刻*/
	@Column(name = "OVER_TIME_1_END_CLC")
	public Integer overTime1EndClc;
	/*残業2開始時刻*/
	@Column(name = "OVER_TIME_2_STR_CLC")
	public Integer overTime2StrClc;
	/*残業2終了時刻*/
	@Column(name = "OVER_TIME_2_END_CLC")
	public Integer overTime2EndClc;
	/*残業3開始時刻*/
	@Column(name = "OVER_TIME_3_STR_CLC")
	public Integer overTime3StrClc;
	/*残業3終了時刻*/
	@Column(name = "OVER_TIME_3_END_CLC")
	public Integer overTime3EndClc;
	/*残業4開始時刻*/
	@Column(name = "OVER_TIME_4_STR_CLC")
	public Integer overTime4StrClc;
	/*残業4終了時刻*/
	@Column(name = "OVER_TIME_4_END_CLC")
	public Integer overTime4EndClc;
	/*残業5開始時刻*/
	@Column(name = "OVER_TIME_5_STR_CLC")
	public Integer overTime5StrClc;
	/*残業5終了時刻*/
	@Column(name = "OVER_TIME_5_END_CLC")
	public Integer overTime5EndClc;
	/*残業6開始時刻*/
	@Column(name = "OVER_TIME_6_STR_CLC")
	public Integer overTime6StrClc;
	/*残業6終了時刻*/
	@Column(name = "OVER_TIME_6_END_CLC")
	public Integer overTime6EndClc;
	/*残業7開始時刻*/
	@Column(name = "OVER_TIME_7_STR_CLC")
	public Integer overTime7StrClc;
	/*残業7終了時刻*/
	@Column(name = "OVER_TIME_7_END_CLC")
	public Integer overTime7EndClc;
	/*残業8開始時刻*/
	@Column(name = "OVER_TIME_8_STR_CLC")
	public Integer overTime8StrClc;
	/*残業8終了時刻*/
	@Column(name = "OVER_TIME_8_END_CLC")
	public Integer overTime8EndClc;
	/*残業9開始時刻*/
	@Column(name = "OVER_TIME_9_STR_CLC")
	public Integer overTime9StrClc;
	/*残業9終了時刻*/
	@Column(name = "OVER_TIME_9_END_CLC")
	public Integer overTime9EndClc;
	/*残業10開始時刻*/
	@Column(name = "OVER_TIME_10_STR_CLC")
	public Integer overTime10StrClc;
	/*残業10終了時刻*/
	@Column(name = "OVER_TIME_10_END_CLC")
	public Integer overTime10EndClc;
	/*----------------------日別実績の残業時間帯------------------------------*/
	/*----------------------日別実績の加給時間------------------------------*/
	/*加給時間1*/
	@Column(name = "RAISESALARY_TIME1")
	public int raiseSalaryTime1;
	
	/*加給時間2*/
	@Column(name = "RAISESALARY_TIME2")
	public int raiseSalaryTime2;
	
	/*加給時間3*/
	@Column(name = "RAISESALARY_TIME3")
	public int raiseSalaryTime3;
	
	/*加給時間4*/
	@Column(name = "RAISESALARY_TIME4")
	public int raiseSalaryTime4;
	
	/*加給時間5*/
	@Column(name = "RAISESALARY_TIME5")
	public int raiseSalaryTime5;
	
	/*加給時間6*/
	@Column(name = "RAISESALARY_TIME6")
	public int raiseSalaryTime6;
	
	/*加給時間7*/
	@Column(name = "RAISESALARY_TIME7")
	public int raiseSalaryTime7;
	
	/*加給時間8*/
	@Column(name = "RAISESALARY_TIME8")
	public int raiseSalaryTime8;
	
	/*加給時間9*/
	@Column(name = "RAISESALARY_TIME9")
	public int raiseSalaryTime9;
	
	/*加給時間10*/
	@Column(name = "RAISESALARY_TIME10")
	public int raiseSalaryTime10;
	
	/*所定内加給時間1*/
	@Column(name = "RAISESALARY_IN_TIME1")
	public int raiseSalaryInTime1;
	
	/*所定内加給時間2*/
	@Column(name = "RAISESALARY_IN_TIME2")
	public int raiseSalaryInTime2;
	
	/*所定内加給時間3*/
	@Column(name = "RAISESALARY_IN_TIME3")
	public int raiseSalaryInTime3;
	
	/*所定内加給時間4*/
	@Column(name = "RAISESALARY_IN_TIME4")
	public int raiseSalaryInTime4;
	
	/*所定内加給時間5*/
	@Column(name = "RAISESALARY_IN_TIME5")
	public int raiseSalaryInTime5;
	
	/*所定内加給時間6*/
	@Column(name = "RAISESALARY_IN_TIME6")
	public int raiseSalaryInTime6;
	
	/*所定内加給時間7*/
	@Column(name = "RAISESALARY_IN_TIME7")
	public int raiseSalaryInTime7;
	
	/*所定内加給時間8*/
	@Column(name = "RAISESALARY_IN_TIME8")
	public int raiseSalaryInTime8;
	
	/*所定内加給時間9*/
	@Column(name = "RAISESALARY_IN_TIME9")
	public int raiseSalaryInTime9;
	
	/*所定内加給時間10*/
	@Column(name = "RAISESALARY_IN_TIME10")
	public int raiseSalaryInTime10;
	
	/*所定外加給時間1*/
	@Column(name = "RAISESALARY_OUT_TIME1")
	public int raiseSalaryOutTime1;
	
	/*所定外加給時間2*/
	@Column(name = "RAISESALARY_OUT_TIME2")
	public int raiseSalaryOutTime2;
	
	/*所定外加給時間3*/
	@Column(name = "RAISESALARY_OUT_TIME3")
	public int raiseSalaryOutTime3;
	
	/*所定外加給時間4*/
	@Column(name = "RAISESALARY_OUT_TIME4")
	public int raiseSalaryOutTime4;
	
	/*所定外加給時間5*/
	@Column(name = "RAISESALARY_OUT_TIME5")
	public int raiseSalaryOutTime5;
	
	/*所定外加給時間6*/
	@Column(name = "RAISESALARY_OUT_TIME6")
	public int raiseSalaryOutTime6;
	
	/*所定外加給時間7*/
	@Column(name = "RAISESALARY_OUT_TIME7")
	public int raiseSalaryOutTime7;
	
	/*所定外加給時間8*/
	@Column(name = "RAISESALARY_OUT_TIME8")
	public int raiseSalaryOutTime8;
	
	/*所定外加給時間9*/
	@Column(name = "RAISESALARY_OUT_TIME9")
	public int raiseSalaryOutTime9;
	
	/*所定外加給時間10*/
	@Column(name = "RAISESALARY_OUT_TIME10")
	public int raiseSalaryOutTime10;
	
	/*特定日加給時間1*/
	@Column(name = "SP_RAISESALARY_TIME1")
	public int spRaiseSalaryTime1;
	
	/*特定日加給時間2*/
	@Column(name = "SP_RAISESALARY_TIME2")
	public int spRaiseSalaryTime2;
	
	/*特定日加給時間3*/
	@Column(name = "SP_RAISESALARY_TIME3")
	public int spRaiseSalaryTime3;
	
	/*特定日加給時間4*/
	@Column(name = "SP_RAISESALARY_TIME4")
	public int spRaiseSalaryTime4;
	
	/*特定日加給時間5*/
	@Column(name = "SP_RAISESALARY_TIME5")
	public int spRaiseSalaryTime5;
	
	/*特定日加給時間6*/
	@Column(name = "SP_RAISESALARY_TIME6")
	public int spRaiseSalaryTime6;
	
	/*特定日加給時間7*/
	@Column(name = "SP_RAISESALARY_TIME7")
	public int spRaiseSalaryTime7;
	
	/*特定日加給時間8*/
	@Column(name = "SP_RAISESALARY_TIME8")
	public int spRaiseSalaryTime8;
	
	/*特定日加給時間9*/
	@Column(name = "SP_RAISESALARY_TIME9")
	public int spRaiseSalaryTime9;
	
	/*特定日加給時間10*/
	@Column(name = "SP_RAISESALARY_TIME10")
	public int spRaiseSalaryTime10;
	
	/*特定日所定内加給時間1*/
	@Column(name = "SP_RAISESALARY_IN_TIME1")
	public int spRaiseSalaryInTime1;
	
	/*特定日所定内加給時間2*/
	@Column(name = "SP_RAISESALARY_IN_TIME2")
	public int spRaiseSalaryInTime2;
	
	/*特定日所定内加給時間3*/
	@Column(name = "SP_RAISESALARY_IN_TIME3")
	public int spRaiseSalaryInTime3;
	
	/*特定日所定内加給時間4*/
	@Column(name = "SP_RAISESALARY_IN_TIME4")
	public int spRaiseSalaryInTime4;
	
	/*特定日所定内加給時間5*/
	@Column(name = "SP_RAISESALARY_IN_TIME5")
	public int spRaiseSalaryInTime5;
	
	/*特定日所定内加給時間6*/
	@Column(name = "SP_RAISESALARY_IN_TIME6")
	public int spRaiseSalaryInTime6;
	
	/*特定日所定内加給時間7*/
	@Column(name = "SP_RAISESALARY_IN_TIME7")
	public int spRaiseSalaryInTime7;
	
	/*特定日所定内加給時間8*/
	@Column(name = "SP_RAISESALARY_IN_TIME8")
	public int spRaiseSalaryInTime8;
	
	/*特定日所定内加給時間9*/
	@Column(name = "SP_RAISESALARY_IN_TIME9")
	public int spRaiseSalaryInTime9;
	
	/*特定日所定内加給時間10*/
	@Column(name = "SP_RAISESALARY_IN_TIME10")
	public int spRaiseSalaryInTime10;
	
	/*特定日所定外加給時間1*/
	@Column(name = "SP_RAISESALARY_OUT_TIME1")
	public int spRaiseSalaryOutTime1;
	
	/*特定日所定外加給時間2*/
	@Column(name = "SP_RAISESALARY_OUT_TIME2")
	public int spRaiseSalaryOutTime2;
	
	/*特定日所定外加給時間3*/
	@Column(name = "SP_RAISESALARY_OUT_TIME3")
	public int spRaiseSalaryOutTime3;
	
	/*特定日所定外加給時間4*/
	@Column(name = "SP_RAISESALARY_OUT_TIME4")
	public int spRaiseSalaryOutTime4;
	
	/*特定日所定外加給時間5*/
	@Column(name = "SP_RAISESALARY_OUT_TIME5")
	public int spRaiseSalaryOutTime5;
	
	/*特定日所定外加給時間6*/
	@Column(name = "SP_RAISESALARY_OUT_TIME6")
	public int spRaiseSalaryOutTime6;
	
	/*特定日所定外加給時間7*/
	@Column(name = "SP_RAISESALARY_OUT_TIME7")
	public int spRaiseSalaryOutTime7;
	
	/*特定日所定外加給時間8*/
	@Column(name = "SP_RAISESALARY_OUT_TIME8")
	public int spRaiseSalaryOutTime8;
	
	/*特定日所定外加給時間9*/
	@Column(name = "SP_RAISESALARY_OUT_TIME9")
	public int spRaiseSalaryOutTime9;
	
	/*特定日所定外加給時間10*/
	@Column(name = "SP_RAISESALARY_OUT_TIME10")
	public int spRaiseSalaryOutTime10;
	
	/** インターバル出勤時刻 */
	@Column(name = "INTERVAL_ATTENDANCE_CLOCK")
	public int intervalAttendance;
	
	/** インターバル時間 */
	@Column(name = "INTERVAL_TIME")
	public int intervalTime;
	
	/** 振休使用時間 */
	@Column(name = "HOL_TRANSFER_USE_TIME")
	public int transferHolidayTime;
	
	/** 計算差異時間 */
	@Column(name = "CALC_DIFF_TIME")
	public int calcDiffTime;
	
	/** 就業時間金額 */
	@Column(name = "WORK_TIME_AMOUNT")
	public int workTimeAmount;
	
	/*----------------------日別実績の加給時間------------------------------*/
	
	@Override
	protected Object getKey() {
		return this.krcdtDayTimePK;
	}
	
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumns(value = { 
			@JoinColumn(name = "SID", referencedColumnName = "SID", insertable = false, updatable = false),
			@JoinColumn(name = "YMD", referencedColumnName = "YMD", insertable = false, updatable = false) })
	public KrcdtDayTimePremium krcdtDayPremiumTime;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "krcdtDayTime", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<KrcdtDayLeaveEarlyTime> krcdtDayLeaveEarlyTime;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "krcdtDayTime", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<KrcdtDayLateTime> krcdtDayLateTime;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "krcdtDayTime", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<KrcdtDaiShortWorkTime> krcdtDaiShortWorkTime;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "krcdtDayTime", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<KrcdtDayShorttime> krcdtDayShorttime;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "krcdtDayTime", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<KrcdtDayTimeGoout> krcdtDayOutingTime; 
	
	public static KrcdtDayTimeAtd toEntity(AttendanceTimeOfDailyPerformance attendanceTime) {
		val entity = new KrcdtDayTimeAtd();
		entity.krcdtDayTimePK = new KrcdtDayTimePK(attendanceTime.getEmployeeId(),
				attendanceTime.getYmd());
		entity.setData(attendanceTime);
		return entity;
	}
	
	public void setData(AttendanceTimeOfDailyPerformance attendanceTime) {
		/*----------------------日別実績の勤怠時間------------------------------*/
		ActualWorkingTimeOfDaily actualWork = attendanceTime.getTime().getActualWorkingTimeOfDaily();
		TotalWorkingTime totalWork = actualWork == null ? null :actualWork.getTotalWorkingTime();
		ConstraintTime constraintTime = actualWork == null ? null : actualWork.getConstraintTime();
		ExcessOfStatutoryMidNightTime excessStt = totalWork == null ? null : totalWork.getExcessOfStatutoryTimeOfDaily() == null ? null 
				: totalWork.getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime();
		StayingTimeOfDaily staying = attendanceTime.getTime().getStayingTime();
		if(totalWork != null){
			/* 総労働時間 */
			this.totalAttTime = totalWork.getTotalTime() == null ? 0 : totalWork.getTotalTime().valueAsMinutes();
			/* 総計算時間 */
			this.totalCalcTime = totalWork.getTotalCalcTime() == null ? 0 : totalWork.getTotalCalcTime().valueAsMinutes();
			/* 実働時間 */
			this.actWorkTime = totalWork.getActualTime() == null ? 0 : totalWork.getActualTime().valueAsMinutes();
			/* 勤務回数 */
			this.workTimes = totalWork.getWorkTimes() == null ? 0 : totalWork.getWorkTimes().v();
			/*休暇加算時間*/
			this.vactnAddTime = totalWork.getVacationAddTime() == null ? 0 : totalWork.getVacationAddTime().valueAsMinutes();
			
			if (totalWork.getIntervalTime() != null) {
				this.intervalAttendance = totalWork.getIntervalTime().getIntervalAttendance() == null ? 
						0 : totalWork.getIntervalTime().getIntervalAttendance().valueAsMinutes();
				this.intervalTime = totalWork.getIntervalTime().getIntervalTime() == null ? 
						0 : totalWork.getIntervalTime().getIntervalTime().valueAsMinutes();
			} else {
				this.intervalAttendance = 0;
				this.intervalTime = 0;
			}
			this.calcDiffTime = totalWork.getCalcDiffTime().valueAsMinutes();
		}
		if(constraintTime != null){
			/* 総拘束時間 */
			this.totalBindTime = constraintTime.getTotalConstraintTime() == null ? 0 : constraintTime.getTotalConstraintTime().valueAsMinutes();
			/* 深夜拘束時間 */
			this.midnBindTime = constraintTime.getLateNightConstraintTime() == null ? 0 : constraintTime.getLateNightConstraintTime().valueAsMinutes();
		}
		if(actualWork != null){
			/* 拘束差異時間 */
			this.bindDiffTime = actualWork.getConstraintDifferenceTime() == null ? 0 : actualWork.getConstraintDifferenceTime().valueAsMinutes();
			/* 時差勤務時間 */
			this.diffTimeWorkTime = actualWork.getTimeDifferenceWorkingHours() == null ? 0 : actualWork.getTimeDifferenceWorkingHours().valueAsMinutes();
		}
		if(excessStt != null){
			/* 所定外深夜時間 */
			this.outPrsMidnTime = excessStt.getTime() == null | excessStt.getTime().getTime() == null ? 0 : excessStt.getTime().getTime().valueAsMinutes();
			this.calcOutPrsMidnTime = excessStt.getTime() == null | excessStt.getTime().getCalcTime() == null ? 0 : excessStt.getTime().getCalcTime().valueAsMinutes();
			/* 事前所定外深夜時間 */
			this.preOutPrsMidnTime = excessStt.getBeforeApplicationTime() == null ? 0 : excessStt.getBeforeApplicationTime().valueAsMinutes();
			//所定外深夜乖離時間
			this.divOutPrsMidnTime = excessStt.getTime() == null | excessStt.getTime().getDivergenceTime() == null ? 0 : excessStt.getTime().getDivergenceTime().valueAsMinutes();  
		}
		
		/* 予実差異時間 */
		this.budgetTimeVariance = attendanceTime.getTime().getBudgetTimeVariance() == null ? 0 : attendanceTime.getTime().getBudgetTimeVariance().valueAsMinutes();
		/* 不就労時間 */
		this.unemployedTime = attendanceTime.getTime().getUnEmployedTime() == null ? 0 : attendanceTime.getTime().getUnEmployedTime().valueAsMinutes();
		
		if(staying != null){
			/* 滞在時間 */
			this.stayingTime = staying.getStayingTime() == null ? 0 : staying.getStayingTime().valueAsMinutes();
			/* 出勤前時間 */
			this.bfrWorkTime = staying.getBeforeWoringTime() == null ? 0 : staying.getBeforeWoringTime().valueAsMinutes();
			/* 退勤後時間 */
			this.aftLeaveTime = staying.getAfterLeaveTime() == null ? 0 : staying.getAfterLeaveTime().valueAsMinutes();
			/* PCログオン前時間 */
			this.bfrPcLogonTime = staying.getBeforePCLogOnTime() == null ? 0 : staying.getBeforePCLogOnTime().valueAsMinutes();
			/* PCログオフ後時間 */
			this.aftPcLogoffTime = staying.getAfterPCLogOffTime() == null ? 0 : staying.getAfterPCLogOffTime().valueAsMinutes();
		}
		/*----------------------日別実績の勤怠時間------------------------------*/
		
		/*----------------------日別実績の休憩時間------------------------------*/
		this.toRecordTotalTime = 0;
		this.calToRecordTotalTime = 0;
		
		this.toRecordInTime = 0;
		this.calToRecordInTime = 0;
		
		this.toRecordOutTime = 0;
		this.calToRecordOutTime = 0;
		
		this.deductionTotalTime = 0;
		this.calDeductionTotalTime = 0;
		
		this.deductionInTime = 0;
		this.calDeductionInTime = 0;
		
		this.deductionOutTime = 0;
		this.calDeductionOutTime = 0;
		
		this.duringworkTime = 0;
		
		this.count = 0;
		
		if(attendanceTime != null) {
			/*----------------------日別実績の予定時間------------------------------*/
			if(attendanceTime.getTime().getWorkScheduleTimeOfDaily() !=null) {
				val domain = attendanceTime.getTime().getWorkScheduleTimeOfDaily(); 
				/*勤務予定時間*/
				this.workScheduleTime = domain.getWorkScheduleTime() == null || domain.getWorkScheduleTime().getTotal() == null ? 0 
					: domain.getWorkScheduleTime().getTotal().valueAsMinutes();
				/*計画所定労働時間*/
				this.schedulePreLaborTime = 0;//domain.getSchedulePrescribedLaborTime() == null ? 0 : domain.getSchedulePrescribedLaborTime().valueAsMinutes();
				/*実績所定労働時間*/
				this.recorePreLaborTime = domain.getRecordPrescribedLaborTime() == null ? 0 : domain.getRecordPrescribedLaborTime().valueAsMinutes();
			}
			/*----------------------日別実績の予定時間------------------------------*/
			
			if(attendanceTime.getTime().getActualWorkingTimeOfDaily() != null) {
				/*----------------------日別実績の乖離時間------------------------------*/
				if(actualWork != null&& actualWork.getDivTime() != null) {
					val a = actualWork.getDivTime();
					for(int loopNumber = 1 ; loopNumber <= 10 ; loopNumber++) {
						val divergenceTime = getDivergenceTime(a , loopNumber);
						setValue(divergenceTime, loopNumber);
					}
				}
				/*----------------------日別実績の乖離時間------------------------------*/
				if(attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime() != null) {
					/*----------------------日別実績の休憩時間------------------------------*/
					if(attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily() != null) {
						val recordTime = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime();
						val dedTime = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getDeductionTotalTime();
						val duringTime = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getWorkTime();
						val workTimes = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getGooutTimes();
						if(recordTime.getTotalTime() != null) {
							this.toRecordTotalTime = recordTime.getTotalTime().getTime() == null ? 0 : recordTime.getTotalTime().getTime().valueAsMinutes();
							this.calToRecordTotalTime = recordTime.getTotalTime().getCalcTime() == null ? 0 : recordTime.getTotalTime().getCalcTime().valueAsMinutes();
						}
						
						if(recordTime.getWithinStatutoryTotalTime() != null) {
							this.toRecordInTime = recordTime.getWithinStatutoryTotalTime().getTime() == null ? 0 : recordTime.getWithinStatutoryTotalTime().getTime().valueAsMinutes();
							this.calToRecordInTime = recordTime.getWithinStatutoryTotalTime().getCalcTime() == null ? 0 : recordTime.getWithinStatutoryTotalTime().getCalcTime().valueAsMinutes();;
						}

						if(recordTime.getExcessOfStatutoryTotalTime() != null) {
							this.toRecordOutTime = recordTime.getExcessOfStatutoryTotalTime().getTime() == null ? 0 : recordTime.getExcessOfStatutoryTotalTime().getTime().valueAsMinutes();
							this.calToRecordOutTime = recordTime.getExcessOfStatutoryTotalTime().getCalcTime() == null ? 0 : recordTime.getExcessOfStatutoryTotalTime().getCalcTime().valueAsMinutes();
						}
						
						if(dedTime.getTotalTime() != null) {
							this.deductionTotalTime = dedTime.getTotalTime().getTime() == null ? 0 : dedTime.getTotalTime().getTime().valueAsMinutes();
							this.calDeductionTotalTime = dedTime.getTotalTime().getCalcTime() == null ? 0 : dedTime.getTotalTime().getCalcTime().valueAsMinutes();
						}

						if(dedTime.getWithinStatutoryTotalTime() != null) {
							this.deductionInTime = dedTime.getWithinStatutoryTotalTime().getTime() == null ? 0 : dedTime.getWithinStatutoryTotalTime().getTime().valueAsMinutes();
							this.calDeductionInTime = dedTime.getWithinStatutoryTotalTime().getCalcTime() == null ? 0 : dedTime.getWithinStatutoryTotalTime().getCalcTime().valueAsMinutes();
						}
						
						if(dedTime.getExcessOfStatutoryTotalTime() != null) {
							this.deductionOutTime = dedTime.getExcessOfStatutoryTotalTime().getTime() == null ? 0 : dedTime.getExcessOfStatutoryTotalTime().getTime().valueAsMinutes();;
							this.calDeductionOutTime = dedTime.getExcessOfStatutoryTotalTime().getCalcTime() == null ? 0 : dedTime.getExcessOfStatutoryTotalTime().getCalcTime().valueAsMinutes();;
						}
						
						if(duringTime != null)
							this.duringworkTime = duringTime.v() == null ? 0 : duringTime.v();
						
						if(workTimes != null)
							this.count = workTimes.v() == null ? 0 : workTimes.v();
					}
					/*----------------------日別実績の休憩時間------------------------------*/
					/*----------------------日別実績の休出枠時間------------------------------*/
					val test = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily();
					val domain = test == null ? null : test.getWorkHolidayTime().orElse(null);
					
					if(domain != null && domain.getHolidayWorkFrameTime() != null || !domain.getHolidayWorkFrameTime().isEmpty()){
						HolidayWorkFrameTime frame1 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 1);
						HolidayWorkFrameTime frame2 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 2);
						HolidayWorkFrameTime frame3 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 3);
						HolidayWorkFrameTime frame4 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 4);
						HolidayWorkFrameTime frame5 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 5);
						HolidayWorkFrameTime frame6 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 6);
						HolidayWorkFrameTime frame7 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 7);
						HolidayWorkFrameTime frame8 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 8);
						HolidayWorkFrameTime frame9 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 9);
						HolidayWorkFrameTime frame10 = getHolidayTimeFrame(domain.getHolidayWorkFrameTime(), 10);
						/*休日出勤時間*/
						this.holiWorkTime1 = !frame1.getHolidayWorkTime().isPresent() || frame1.getHolidayWorkTime().get().getTime() == null ? 0 : frame1.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime2 = !frame2.getHolidayWorkTime().isPresent() || frame2.getHolidayWorkTime().get().getTime() == null ? 0 : frame2.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime3 = !frame3.getHolidayWorkTime().isPresent() || frame3.getHolidayWorkTime().get().getTime() == null ? 0 : frame3.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime4 = !frame4.getHolidayWorkTime().isPresent() || frame4.getHolidayWorkTime().get().getTime() == null ? 0 : frame4.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime5 = !frame5.getHolidayWorkTime().isPresent() || frame5.getHolidayWorkTime().get().getTime() == null ? 0 : frame5.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime6 = !frame6.getHolidayWorkTime().isPresent() || frame6.getHolidayWorkTime().get().getTime() == null ? 0 : frame6.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime7 = !frame7.getHolidayWorkTime().isPresent() || frame7.getHolidayWorkTime().get().getTime() == null ? 0 : frame7.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime8 = !frame8.getHolidayWorkTime().isPresent() || frame8.getHolidayWorkTime().get().getTime() == null ? 0 : frame8.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime9 = !frame9.getHolidayWorkTime().isPresent() || frame9.getHolidayWorkTime().get().getTime() == null ? 0 : frame9.getHolidayWorkTime().get().getTime().valueAsMinutes();
						this.holiWorkTime10 = !frame10.getHolidayWorkTime().isPresent() || frame10.getHolidayWorkTime().get().getTime() == null ? 0 : frame10.getHolidayWorkTime().get().getTime().valueAsMinutes();
						/*振替時間*/
						this.transTime1 = !frame1.getTransferTime().isPresent() || frame1.getTransferTime().get().getTime() == null ? 0 : frame1.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime2 = !frame2.getTransferTime().isPresent() || frame2.getTransferTime().get().getTime() == null ? 0 : frame2.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime3 = !frame3.getTransferTime().isPresent() || frame3.getTransferTime().get().getTime() == null ? 0 : frame3.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime4 = !frame4.getTransferTime().isPresent() || frame4.getTransferTime().get().getTime() == null ? 0 : frame4.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime5 = !frame5.getTransferTime().isPresent() || frame5.getTransferTime().get().getTime() == null ? 0 : frame5.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime6 = !frame6.getTransferTime().isPresent() || frame6.getTransferTime().get().getTime() == null ? 0 : frame6.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime7 = !frame7.getTransferTime().isPresent() || frame7.getTransferTime().get().getTime() == null ? 0 : frame7.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime8 = !frame8.getTransferTime().isPresent() || frame8.getTransferTime().get().getTime() == null ? 0 : frame8.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime9 = !frame9.getTransferTime().isPresent() || frame9.getTransferTime().get().getTime() == null ? 0 : frame9.getTransferTime().get().getTime().valueAsMinutes();
						this.transTime10 = !frame10.getTransferTime().isPresent() || frame10.getTransferTime().get().getTime() == null ? 0 : frame10.getTransferTime().get().getTime().valueAsMinutes();
						/*計算休日出勤時間*/
						this.calcHoliWorkTime1 = !frame1.getHolidayWorkTime().isPresent() || frame1.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame1.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime2 = !frame2.getHolidayWorkTime().isPresent() || frame2.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame2.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime3 = !frame3.getHolidayWorkTime().isPresent() || frame3.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame3.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime4 = !frame4.getHolidayWorkTime().isPresent() || frame4.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame4.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime5 = !frame5.getHolidayWorkTime().isPresent() || frame5.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame5.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime6 = !frame6.getHolidayWorkTime().isPresent() || frame6.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame6.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime7 = !frame7.getHolidayWorkTime().isPresent() || frame7.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame7.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime8 = !frame8.getHolidayWorkTime().isPresent() || frame8.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame8.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime9 = !frame9.getHolidayWorkTime().isPresent() || frame9.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame9.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						this.calcHoliWorkTime10 = !frame10.getHolidayWorkTime().isPresent() || frame10.getHolidayWorkTime().get().getCalcTime() == null ? 0 : frame10.getHolidayWorkTime().get().getCalcTime().valueAsMinutes();
						/*計算振替時間*/
						this.calcTransTime1 = !frame1.getTransferTime().isPresent() || frame1.getTransferTime().get().getCalcTime() == null ? 0 : frame1.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime2 = !frame2.getTransferTime().isPresent() || frame2.getTransferTime().get().getCalcTime() == null ? 0 : frame2.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime3 = !frame3.getTransferTime().isPresent() || frame3.getTransferTime().get().getCalcTime() == null ? 0 : frame3.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime4 = !frame4.getTransferTime().isPresent() || frame4.getTransferTime().get().getCalcTime() == null ? 0 : frame4.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime5 = !frame5.getTransferTime().isPresent() || frame5.getTransferTime().get().getCalcTime() == null ? 0 : frame5.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime6 = !frame6.getTransferTime().isPresent() || frame6.getTransferTime().get().getCalcTime() == null ? 0 : frame6.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime7 = !frame7.getTransferTime().isPresent() || frame7.getTransferTime().get().getCalcTime() == null ? 0 : frame7.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime8 = !frame8.getTransferTime().isPresent() || frame8.getTransferTime().get().getCalcTime() == null ? 0 : frame8.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime9 = !frame9.getTransferTime().isPresent() || frame9.getTransferTime().get().getCalcTime() == null ? 0 : frame9.getTransferTime().get().getCalcTime().valueAsMinutes();
						this.calcTransTime10 = !frame10.getTransferTime().isPresent() || frame10.getTransferTime().get().getCalcTime() == null ? 0 : frame10.getTransferTime().get().getCalcTime().valueAsMinutes();
						/*事前申請時間*/
						this.preAppTime1 = !frame1.getBeforeApplicationTime().isPresent() || frame1.getBeforeApplicationTime().get() == null ? 0 : frame1.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime2 = !frame2.getBeforeApplicationTime().isPresent() || frame2.getBeforeApplicationTime().get() == null ? 0 : frame2.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime3 = !frame3.getBeforeApplicationTime().isPresent() || frame3.getBeforeApplicationTime().get() == null ? 0 : frame3.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime4 = !frame4.getBeforeApplicationTime().isPresent() || frame4.getBeforeApplicationTime().get() == null ? 0 : frame4.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime5 = !frame5.getBeforeApplicationTime().isPresent() || frame5.getBeforeApplicationTime().get() == null ? 0 : frame5.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime6 = !frame6.getBeforeApplicationTime().isPresent() || frame6.getBeforeApplicationTime().get() == null ? 0 : frame6.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime7 = !frame7.getBeforeApplicationTime().isPresent() || frame7.getBeforeApplicationTime().get() == null ? 0 : frame7.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime8 = !frame8.getBeforeApplicationTime().isPresent() || frame8.getBeforeApplicationTime().get() == null ? 0 : frame8.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime9 = !frame9.getBeforeApplicationTime().isPresent() || frame9.getBeforeApplicationTime().get() == null ? 0 : frame9.getBeforeApplicationTime().get().valueAsMinutes();
						this.preAppTime10 = !frame10.getBeforeApplicationTime().isPresent() || frame10.getBeforeApplicationTime().get() == null ? 0 : frame10.getBeforeApplicationTime().get().valueAsMinutes();	
						/*休日出勤乖離時間*/
						this.divHoliTime1 = !frame1.getHolidayWorkTime().isPresent() || frame1.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame1.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime2 = !frame2.getHolidayWorkTime().isPresent() || frame2.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame2.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime3 = !frame3.getHolidayWorkTime().isPresent() || frame3.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame3.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime4 = !frame4.getHolidayWorkTime().isPresent() || frame4.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame4.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime5 = !frame5.getHolidayWorkTime().isPresent() || frame5.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame5.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime6 = !frame6.getHolidayWorkTime().isPresent() || frame6.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame6.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime7 = !frame7.getHolidayWorkTime().isPresent() || frame7.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame7.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime8 = !frame8.getHolidayWorkTime().isPresent() || frame8.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame8.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime9 = !frame9.getHolidayWorkTime().isPresent() || frame9.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame9.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTime10 = !frame10.getHolidayWorkTime().isPresent() || frame10.getHolidayWorkTime().get().getDivergenceTime() == null ? 0 : frame10.getHolidayWorkTime().get().getDivergenceTime().valueAsMinutes();
						/*振替乖離時間*/
						this.divHoliTransTime1 = !frame1.getTransferTime().isPresent() || frame1.getTransferTime().get().getDivergenceTime() == null ? 0 : frame1.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime2 = !frame2.getTransferTime().isPresent() || frame2.getTransferTime().get().getDivergenceTime() == null ? 0 : frame2.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime3 = !frame3.getTransferTime().isPresent() || frame3.getTransferTime().get().getDivergenceTime() == null ? 0 : frame3.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime4 = !frame4.getTransferTime().isPresent() || frame4.getTransferTime().get().getDivergenceTime() == null ? 0 : frame4.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime5 = !frame5.getTransferTime().isPresent() || frame5.getTransferTime().get().getDivergenceTime() == null ? 0 : frame5.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime6 = !frame6.getTransferTime().isPresent() || frame6.getTransferTime().get().getDivergenceTime() == null ? 0 : frame6.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime7 = !frame7.getTransferTime().isPresent() || frame7.getTransferTime().get().getDivergenceTime() == null ? 0 : frame7.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime8 = !frame8.getTransferTime().isPresent() || frame8.getTransferTime().get().getDivergenceTime() == null ? 0 : frame8.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime9 = !frame9.getTransferTime().isPresent() || frame9.getTransferTime().get().getDivergenceTime() == null ? 0 : frame9.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						this.divHoliTransTime10 = !frame10.getTransferTime().isPresent() || frame10.getTransferTime().get().getDivergenceTime() == null ? 0 : frame10.getTransferTime().get().getDivergenceTime().valueAsMinutes();
						
						if(domain.getHolidayMidNightWork().isPresent()) {
							getHolidayMidNightWork(domain.getHolidayMidNightWork().get(), StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork).ifPresent(within -> {
								if(within.getTime() != null){
									/*法定内休日出勤深夜*/
									this.legHoliWorkMidn = within.getTime().getTime() == null ? 0 : within.getTime().getTime().valueAsMinutes();
									/*計算法定内休日出勤深夜*/
									this.calcLegHoliWorkMidn = within.getTime().getCalcTime() == null ? 0 : within.getTime().getCalcTime().valueAsMinutes();
									/*法定内休日出勤深夜乖離時間*/
									this.divLegHoliWorkMidn = within.getTime().getDivergenceTime() == null ? 0 : within.getTime().getDivergenceTime().valueAsMinutes();
								}
							});
							getHolidayMidNightWork(domain.getHolidayMidNightWork().get(), StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork).ifPresent(excess -> {
								if(excess.getTime() != null){
									/*法定外休日出勤深夜*/
									this.illegHoliWorkMidn = excess.getTime().getTime() == null ? 0 : excess.getTime().getTime().valueAsMinutes();
									/*計算法定外休日出勤深夜*/
									this.calcIllegHoliWorkMidn = excess.getTime().getCalcTime() == null ? 0 : excess.getTime().getCalcTime().valueAsMinutes();
									/*法定外休日出勤深夜乖離時間*/
									this.divIllegHoliWorkMidn = excess.getTime().getDivergenceTime() == null ? 0 : excess.getTime().getDivergenceTime().valueAsMinutes();
								}
							});
							getHolidayMidNightWork(domain.getHolidayMidNightWork().get(), StaturoryAtrOfHolidayWork.PublicHolidayWork).ifPresent(publicWork -> {
								if(publicWork.getTime() != null){
									/*祝日日出勤深夜*/
									this.pbHoliWorkMidn = publicWork.getTime().getTime() == null ? 0 : publicWork.getTime().getTime().valueAsMinutes();
									/*計算祝日日出勤深夜*/
									this.calcPbHoliWorkMidn = publicWork.getTime().getCalcTime() == null ? 0 : publicWork.getTime().getCalcTime().valueAsMinutes();
									/*祝日出勤深夜乖離時間*/
									this.divPbHoliWorkMidn = publicWork.getTime().getDivergenceTime() == null ? 0 : publicWork.getTime().getDivergenceTime().valueAsMinutes();
								}
							});
						}
						/*休日出勤拘束時間*/
						this.holiWorkBindTime = domain.getHolidayTimeSpentAtWork() == null ? 0 : domain.getHolidayTimeSpentAtWork().valueAsMinutes();
					}

					/*----------------------日別実績の休出枠時間------------------------------*/
					/*----------------------日別実績の休出時間帯------------------------------*/
					if(totalWork != null 
						&& totalWork.getExcessOfStatutoryTimeOfDaily() != null
						&& totalWork.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()
						&& !totalWork.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayWorkFrameTimeSheet().isEmpty()) {
						val holTimeSheet = totalWork.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayWorkFrameTimeSheet(); 
						Optional<HolidayWorkFrameTimeSheet> sheet = Optional.empty();
						
						allClearHolidayWorkTS();
						sheet = getTimeSheet(holTimeSheet, 1);
						sheet.ifPresent(tc -> {
							/*休日出勤1開始時刻*/
							this.holiWork1StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤1終了時刻*/
							this.holiWork1EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 2);
						sheet.ifPresent(tc -> {
							/*休日出勤2開始時刻*/
							this.holiWork2StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤2終了時刻*/
							this.holiWork2EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 3);
						sheet.ifPresent(tc -> {
							/*休日出勤3開始時刻*/
							this.holiWork3StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤3終了時刻*/
							this.holiWork3EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 4);
						sheet.ifPresent(tc -> {
							/*休日出勤4開始時刻*/
							this.holiWork4StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤4終了時刻*/
							this.holiWork4EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 5);
						sheet.ifPresent(tc -> {
							/*休日出勤5開始時刻*/
							this.holiWork5StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤5終了時刻*/
							this.holiWork5EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 6);
						sheet.ifPresent(tc -> {
							/*休日出勤6開始時刻*/
							this.holiWork6StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤6終了時刻*/
							this.holiWork6EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 7);
						sheet.ifPresent(tc -> {
							/*休日出勤7開始時刻*/
							this.holiWork7StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤7終了時刻*/
							this.holiWork7EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 8);
						sheet.ifPresent(tc -> {
							/*休日出勤8開始時刻*/
							this.holiWork8StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤8終了時刻*/
							this.holiWork8EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 9);
						sheet.ifPresent(tc -> {
							/*休日出勤9開始時刻*/
							this.holiWork9StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤9終了時刻*/
							this.holiWork9EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
						sheet = getTimeSheet(holTimeSheet, 10);
						sheet.ifPresent(tc -> {
							/*休日出勤10開始時刻*/
							this.holiWork10StrClc = tc.getTimeSheet().getStart().valueAsMinutes();
							/*休日出勤10終了時刻*/
							this.holiWork10EndClc = tc.getTimeSheet().getEnd().valueAsMinutes();
						});
					}
				/*----------------------日別実績の休出時間帯------------------------------*/
					
					
					/*----------------------日別実績の休暇------------------------------*/
					val vacationDomain = attendanceTime.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getHolidayOfDaily();
					this.annualleaveTime = 0;
					this.annualleaveTdvTime = 0;
					this.compensatoryLeaveTime = 0;
					this.compensatoryLeaveTdvTime = 0;
					this.specialHolidayTime = 0;
					this.specialHolidayTdvTime = 0;
					this.excessSalaryiesTime = 0;
					this.excessSalaryiesTdvTime = 0;
					this.retentionYearlyTime = 0;
					this.absenceTime = 0;
					this.tdvTime = 0;
					this.tdvShortageTime = 0;
					this.transferHolidayTime = 0;
					if(vacationDomain != null) {
						//年休
						if(vacationDomain.getAnnual() != null) {
							this.annualleaveTime = vacationDomain.getAnnual() == null ? 0 : vacationDomain.getAnnual().getUseTime().valueAsMinutes();
							this.annualleaveTdvTime = vacationDomain.getAnnual() == null ? 0 : vacationDomain.getAnnual().getDigestionUseTime().valueAsMinutes();
						}

						//代休
						if(vacationDomain.getSubstitute() != null) {
							this.compensatoryLeaveTime = vacationDomain.getSubstitute() == null ? 0 : vacationDomain.getSubstitute().getUseTime().valueAsMinutes();
							this.compensatoryLeaveTdvTime = vacationDomain.getSubstitute() == null ? 0 : vacationDomain.getSubstitute().getDigestionUseTime().valueAsMinutes();
						}
						
						//特別休暇
						if(vacationDomain.getSpecialHoliday() != null) {
							this.specialHolidayTime = vacationDomain.getSpecialHoliday() == null ? 0 : vacationDomain.getSpecialHoliday().getUseTime().valueAsMinutes();
							this.specialHolidayTdvTime = vacationDomain.getSpecialHoliday() == null ? 0 : vacationDomain.getSpecialHoliday().getDigestionUseTime().valueAsMinutes();
						}
						//超過有休
						if(vacationDomain.getOverSalary() != null) {
							this.excessSalaryiesTime = vacationDomain.getOverSalary() == null ? 0 : vacationDomain.getOverSalary().getUseTime().valueAsMinutes();
							this.excessSalaryiesTdvTime = vacationDomain.getOverSalary() == null ? 0 : vacationDomain.getOverSalary().getDigestionUseTime().valueAsMinutes();
						}
						//積立
						if(vacationDomain.getYearlyReserved() != null) {
							this.retentionYearlyTime = vacationDomain.getYearlyReserved() == null ? 0 : vacationDomain.getYearlyReserved().getUseTime().valueAsMinutes(); 
						}
						//欠勤
						if(vacationDomain.getAbsence() != null) {
							this.absenceTime = vacationDomain.getAbsence() == null ? 0 : vacationDomain.getAbsence().getUseTime().valueAsMinutes(); 
						}
						//時間消化休暇
						if(vacationDomain.getTimeDigest() != null) {
							this.tdvTime = vacationDomain.getTimeDigest() == null ? 0 : vacationDomain.getTimeDigest().getUseTime().valueAsMinutes();
							this.tdvShortageTime = vacationDomain.getTimeDigest() == null ? 0 : vacationDomain.getTimeDigest().getLeakageTime().valueAsMinutes();;
						}
						// 振休
						if(vacationDomain.getTransferHoliday() != null) {
							this.transferHolidayTime = vacationDomain.getTransferHoliday() == null ? 0 : vacationDomain.getTransferHoliday().getUseTime().valueAsMinutes(); 
						}
					}
					/*----------------------日別実績の休暇------------------------------*/
					
					
					
					/*----------------------日別実績の所定内時間------------------------------*/
					if(totalWork.getWithinStatutoryTimeOfDaily() != null){
						val withinDomain = totalWork.getWithinStatutoryTimeOfDaily();
						/*就業時間*/
						this.workTime = withinDomain.getWorkTime() == null ? 0 : withinDomain.getWorkTime().valueAsMinutes();
						/*実働就業時間*/
						this.pefomWorkTime = withinDomain.getActualWorkTime() == null ? 0 : withinDomain.getActualWorkTime().valueAsMinutes();
						/*所定内割増時間*/
						this.prsIncldPrmimTime = withinDomain.getWithinPrescribedPremiumTime() == null ? 0 : withinDomain.getWithinPrescribedPremiumTime().valueAsMinutes();
						if(withinDomain.getWithinStatutoryMidNightTime() != null){
							TimeDivergenceWithCalculation winthinTime = withinDomain.getWithinStatutoryMidNightTime().getTime();
							/*所定内深夜時間*/
						    this.prsIncldMidnTime = winthinTime == null || winthinTime.getTime() == null ? 0 
						         : withinDomain.getWithinStatutoryMidNightTime().getTime().getTime().valueAsMinutes();
						    /*計算所定内深夜時間*/
						    this.calcPrsIncldMidnTime = winthinTime == null || winthinTime.getCalcTime() == null ? 0 
						         : withinDomain.getWithinStatutoryMidNightTime().getTime().getCalcTime().valueAsMinutes();
							/*所定内深夜乖離時間*/
							this.divPrsIncldMidnTime = winthinTime == null || winthinTime.getDivergenceTime() == null ? 0
									: withinDomain.getWithinStatutoryMidNightTime().getTime().getDivergenceTime().valueAsMinutes();
						}
						/*就業時間金額*/
						this.workTimeAmount = withinDomain.getWithinWorkTimeAmount() == null ? 0 : withinDomain.getWithinWorkTimeAmount().v();
//						/*休暇加算時間*/
//						this.vactnAddTime = withinDomain.getVacationAddTime() == null ? 0 : withinDomain.getVacationAddTime().valueAsMinutes();
					}
					/*----------------------日別実績の所定内時間------------------------------*/
					
					/*----------------------日別実績の残業時間------------------------------*/
					this.overTime1  = 0;
					this.overTime2  = 0;
					this.overTime3  = 0;
					this.overTime4  = 0;
					this.overTime5  = 0;
					this.overTime6  = 0;
					this.overTime7  = 0;
					this.overTime8  = 0;
					this.overTime9  = 0;
					this.overTime10 = 0;
					//振替時間
					this.transOverTime1 = 0;
					this.transOverTime2 = 0;
					this.transOverTime3 = 0;
					this.transOverTime4 = 0;
					this.transOverTime5 = 0;
					this.transOverTime6 = 0;
					this.transOverTime7 = 0;
					this.transOverTime8 = 0;
					this.transOverTime9 = 0;
					this.transOverTime10 = 0;
					//計算残業時間
					this.calcOverTime1 = 0;
					this.calcOverTime2 = 0;
					this.calcOverTime3 = 0;
					this.calcOverTime4 = 0;
					this.calcOverTime5 = 0;
					this.calcOverTime6 = 0;
					this.calcOverTime7 = 0;
					this.calcOverTime8 = 0;
					this.calcOverTime9 = 0;
					this.calcOverTime10= 0;
					//計算振替時間
					this.calcTransOverTime1 = 0;
					this.calcTransOverTime2 = 0;
					this.calcTransOverTime3 = 0;
					this.calcTransOverTime4 = 0;
					this.calcTransOverTime5 = 0;
					this.calcTransOverTime6 = 0;
					this.calcTransOverTime7 = 0;
					this.calcTransOverTime8 = 0;
					this.calcTransOverTime9 = 0;
					this.calcTransOverTime10 = 0;
					//事前残業申請
					this.preOverTimeAppTime1 = 0;
					this.preOverTimeAppTime2 = 0;
					this.preOverTimeAppTime3 = 0;
					this.preOverTimeAppTime4 = 0;
					this.preOverTimeAppTime5 = 0;
					this.preOverTimeAppTime6 = 0;
					this.preOverTimeAppTime7 = 0;
					this.preOverTimeAppTime8 = 0;
					this.preOverTimeAppTime9 = 0;
					this.preOverTimeAppTime10 = 0;
					//残業乖離時間
					this.divOverTime1  = 0;
					this.divOverTime2  = 0;
					this.divOverTime3  = 0;
					this.divOverTime4  = 0;
					this.divOverTime5  = 0;
					this.divOverTime6  = 0;
					this.divOverTime7  = 0;
					this.divOverTime8  = 0;
					this.divOverTime9  = 0;
					this.divOverTime10 = 0;
					//振替乖離時間
					this.divTransOverTime1 = 0;
					this.divTransOverTime2 = 0;
					this.divTransOverTime3 = 0;
					this.divTransOverTime4 = 0;
					this.divTransOverTime5 = 0;
					this.divTransOverTime6 = 0;
					this.divTransOverTime7 = 0;
					this.divTransOverTime8 = 0;
					this.divTransOverTime9 = 0;
					this.divTransOverTime10 = 0;
					
					this.ileglMidntOverTime = 0;
					//計算法定外
					this.calcIleglMidNOverTime = 0;
					//法定外残業深夜乖離時間
					this.divIleglMidnOverTime = 0;
					
					//拘束時間
					this.overTimeBindTime = 0;
					//変形法定内残業
					this.deformLeglOverTime = 0;
					//フレックス時間
					this.flexTime = 0;
					//計算フレックス時間
					this.calcFlexTime = 0;
					//事前フレックス時間
					this.preAppFlexTime = 0;
					//フレックス乖離時間
					this.divergenceFlexTime = 0;
					
					if(totalWork != null
						&& totalWork.getExcessOfStatutoryTimeOfDaily() != null
						&& totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
						//深夜時間
						if(totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getExcessOverTimeWorkMidNightTime().isPresent()) {
							
							Finally<ExcessOverTimeWorkMidNightTime> excessOver = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getExcessOverTimeWorkMidNightTime();
							//法定外
							this.ileglMidntOverTime = excessOver.get().getTime().getTime() == null ? 0 : excessOver.get().getTime().getTime().valueAsMinutes();
							//計算法定外
							this.calcIleglMidNOverTime = excessOver.get().getTime().getCalcTime() == null ? 0 : excessOver.get().getTime().getCalcTime().valueAsMinutes();
							//法定外残業深夜乖離時間
							this.divIleglMidnOverTime = excessOver.get().getTime().getDivergenceTime() == null ? 0 : excessOver.get().getTime().getDivergenceTime().valueAsMinutes();
						}
							
						//拘束時間
						this.overTimeBindTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getExcessOverTimeWorkMidNightTime() == null ? 0 : totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkSpentAtWork().valueAsMinutes();
						//変形法定内残業
						this.deformLeglOverTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getIrregularWithinPrescribedOverTimeWork() == null ? 0 : totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getIrregularWithinPrescribedOverTimeWork().valueAsMinutes();
						
						if(totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime() != null) {
							//フレックス時間
							this.flexTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime().getTime().valueAsMinutes();
							//計算フレックス時間
							this.calcFlexTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime().getCalcTime().valueAsMinutes();
							//事前フレックス時間
							this.preAppFlexTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getBeforeApplicationTime() == null ? 0 :totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getBeforeApplicationTime().valueAsMinutes();
							//フレックス乖離時間
							this.divergenceFlexTime = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime().getDivergenceTime().valueAsMinutes();
						}
						if(!totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime().isEmpty()) {
							//残業枠時間
							val overTimeOfDaily = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get();
							if(overTimeOfDaily.getOverTimeWorkFrameTime() != null
									&& !overTimeOfDaily.getOverTimeWorkFrameTime().isEmpty()) {
								OverTimeFrameTime frame1 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 1);
								OverTimeFrameTime frame2 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 2);
								OverTimeFrameTime frame3 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 3);
								OverTimeFrameTime frame4 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 4);
								OverTimeFrameTime frame5 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 5);
								OverTimeFrameTime frame6 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 6);
								OverTimeFrameTime frame7 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 7);
								OverTimeFrameTime frame8 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 8);
								OverTimeFrameTime frame9 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 9);
								OverTimeFrameTime frame10 = getOverTimeFrame(overTimeOfDaily.getOverTimeWorkFrameTime(), 10);
								
								//残業時間
								this.overTime1  = frame1.getOverTimeWork() == null || frame1.getOverTimeWork().getTime() == null ? 0 : frame1.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime2  = frame2.getOverTimeWork() == null || frame2.getOverTimeWork().getTime() == null ? 0 : frame2.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime3  = frame3.getOverTimeWork() == null || frame3.getOverTimeWork().getTime() == null ? 0 : frame3.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime4  = frame4.getOverTimeWork() == null || frame4.getOverTimeWork().getTime() == null ? 0 : frame4.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime5  = frame5.getOverTimeWork() == null || frame5.getOverTimeWork().getTime() == null ? 0 : frame5.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime6  = frame6.getOverTimeWork() == null || frame6.getOverTimeWork().getTime() == null ? 0 : frame6.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime7  = frame7.getOverTimeWork() == null || frame7.getOverTimeWork().getTime() == null ? 0 : frame7.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime8  = frame8.getOverTimeWork() == null || frame8.getOverTimeWork().getTime() == null ? 0 : frame8.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime9  = frame9.getOverTimeWork() == null || frame9.getOverTimeWork().getTime() == null ? 0 : frame9.getOverTimeWork().getTime().valueAsMinutes();
								this.overTime10 = frame10.getOverTimeWork() == null || frame10.getOverTimeWork().getTime() == null ? 0 : frame10.getOverTimeWork().getTime().valueAsMinutes();
								//振替時間
								this.transOverTime1 = frame1.getTransferTime() == null || frame1.getTransferTime().getTime() == null ? 0 : frame1.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime2 = frame2.getTransferTime() == null || frame2.getTransferTime().getTime() == null ? 0 : frame2.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime3 = frame3.getTransferTime() == null || frame3.getTransferTime().getTime() == null ? 0 : frame3.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime4 = frame4.getTransferTime() == null || frame4.getTransferTime().getTime() == null ? 0 : frame4.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime5 = frame5.getTransferTime() == null || frame5.getTransferTime().getTime() == null ? 0 : frame5.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime6 = frame6.getTransferTime() == null || frame6.getTransferTime().getTime() == null ? 0 : frame6.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime7 = frame7.getTransferTime() == null || frame7.getTransferTime().getTime() == null ? 0 : frame7.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime8 = frame8.getTransferTime() == null || frame8.getTransferTime().getTime() == null ? 0 : frame8.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime9 = frame9.getTransferTime() == null || frame9.getTransferTime().getTime() == null ? 0 : frame9.getTransferTime().getTime().valueAsMinutes();
								this.transOverTime10 = frame10.getTransferTime() == null || frame10.getTransferTime().getTime() == null ? 0 : frame10.getTransferTime().getTime().valueAsMinutes();
								//計算残業時間
								this.calcOverTime1 = frame1.getOverTimeWork() == null || frame1.getOverTimeWork().getCalcTime() == null ? 0 : frame1.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime2 = frame2.getOverTimeWork() == null || frame2.getOverTimeWork().getCalcTime() == null ? 0 : frame2.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime3 = frame3.getOverTimeWork() == null || frame3.getOverTimeWork().getCalcTime() == null ? 0 : frame3.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime4 = frame4.getOverTimeWork() == null || frame4.getOverTimeWork().getCalcTime() == null ? 0 : frame4.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime5 = frame5.getOverTimeWork() == null || frame5.getOverTimeWork().getCalcTime() == null ? 0 : frame5.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime6 = frame6.getOverTimeWork() == null || frame6.getOverTimeWork().getCalcTime() == null ? 0 : frame6.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime7 = frame7.getOverTimeWork() == null || frame7.getOverTimeWork().getCalcTime() == null ? 0 : frame7.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime8 = frame8.getOverTimeWork() == null || frame8.getOverTimeWork().getCalcTime() == null ? 0 : frame8.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime9 = frame9.getOverTimeWork() == null || frame9.getOverTimeWork().getCalcTime() == null ? 0 : frame9.getOverTimeWork().getCalcTime().valueAsMinutes();
								this.calcOverTime10= frame10.getOverTimeWork() == null || frame10.getOverTimeWork().getCalcTime() == null ? 0 : frame10.getOverTimeWork().getCalcTime().valueAsMinutes();
								//計算振替時間
								this.calcTransOverTime1 = frame1.getTransferTime() == null || frame1.getTransferTime().getCalcTime() == null ? 0 : frame1.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime2 = frame2.getTransferTime() == null || frame2.getTransferTime().getCalcTime() == null ? 0 : frame2.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime3 = frame3.getTransferTime() == null || frame3.getTransferTime().getCalcTime() == null ? 0 : frame3.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime4 = frame4.getTransferTime() == null || frame4.getTransferTime().getCalcTime() == null ? 0 : frame4.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime5 = frame5.getTransferTime() == null || frame5.getTransferTime().getCalcTime() == null ? 0 : frame5.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime6 = frame6.getTransferTime() == null || frame6.getTransferTime().getCalcTime() == null ? 0 : frame6.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime7 = frame7.getTransferTime() == null || frame7.getTransferTime().getCalcTime() == null ? 0 : frame7.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime8 = frame8.getTransferTime() == null || frame8.getTransferTime().getCalcTime() == null ? 0 : frame8.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime9 = frame9.getTransferTime() == null || frame9.getTransferTime().getCalcTime() == null ? 0 : frame9.getTransferTime().getCalcTime().valueAsMinutes();
								this.calcTransOverTime10 = frame10.getTransferTime() == null || frame10.getTransferTime().getCalcTime() == null ? 0 : frame10.getTransferTime().getCalcTime().valueAsMinutes();
								//事前残業申請
								this.preOverTimeAppTime1 = frame1.getBeforeApplicationTime() == null ? 0 : frame1.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime2 = frame2.getBeforeApplicationTime() == null ? 0 : frame2.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime3 = frame3.getBeforeApplicationTime() == null ? 0 : frame3.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime4 = frame4.getBeforeApplicationTime() == null ? 0 : frame4.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime5 = frame5.getBeforeApplicationTime() == null ? 0 : frame5.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime6 = frame6.getBeforeApplicationTime() == null ? 0 : frame6.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime7 = frame7.getBeforeApplicationTime() == null ? 0 : frame7.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime8 = frame8.getBeforeApplicationTime() == null ? 0 : frame8.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime9 = frame9.getBeforeApplicationTime() == null ? 0 : frame9.getBeforeApplicationTime().valueAsMinutes();
								this.preOverTimeAppTime10 = frame10.getBeforeApplicationTime() == null ? 0 : frame10.getBeforeApplicationTime().valueAsMinutes();
								//残業乖離時間
								this.divOverTime1  = frame1.getOverTimeWork() == null || frame1.getOverTimeWork().getTime() == null ? 0 : frame1.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime2  = frame2.getOverTimeWork() == null || frame2.getOverTimeWork().getTime() == null ? 0 : frame2.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime3  = frame3.getOverTimeWork() == null || frame3.getOverTimeWork().getTime() == null ? 0 : frame3.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime4  = frame4.getOverTimeWork() == null || frame4.getOverTimeWork().getTime() == null ? 0 : frame4.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime5  = frame5.getOverTimeWork() == null || frame5.getOverTimeWork().getTime() == null ? 0 : frame5.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime6  = frame6.getOverTimeWork() == null || frame6.getOverTimeWork().getTime() == null ? 0 : frame6.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime7  = frame7.getOverTimeWork() == null || frame7.getOverTimeWork().getTime() == null ? 0 : frame7.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime8  = frame8.getOverTimeWork() == null || frame8.getOverTimeWork().getTime() == null ? 0 : frame8.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime9  = frame9.getOverTimeWork() == null || frame9.getOverTimeWork().getTime() == null ? 0 : frame9.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								this.divOverTime10 = frame10.getOverTimeWork() == null || frame10.getOverTimeWork().getTime() == null ? 0 : frame10.getOverTimeWork().getDivergenceTime().valueAsMinutes();
								//振替乖離時間
								this.divTransOverTime1 = frame1.getTransferTime() == null || frame1.getTransferTime().getTime() == null ? 0 : frame1.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime2 = frame2.getTransferTime() == null || frame2.getTransferTime().getTime() == null ? 0 : frame2.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime3 = frame3.getTransferTime() == null || frame3.getTransferTime().getTime() == null ? 0 : frame3.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime4 = frame4.getTransferTime() == null || frame4.getTransferTime().getTime() == null ? 0 : frame4.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime5 = frame5.getTransferTime() == null || frame5.getTransferTime().getTime() == null ? 0 : frame5.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime6 = frame6.getTransferTime() == null || frame6.getTransferTime().getTime() == null ? 0 : frame6.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime7 = frame7.getTransferTime() == null || frame7.getTransferTime().getTime() == null ? 0 : frame7.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime8 = frame8.getTransferTime() == null || frame8.getTransferTime().getTime() == null ? 0 : frame8.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime9 = frame9.getTransferTime() == null || frame9.getTransferTime().getTime() == null ? 0 : frame9.getTransferTime().getDivergenceTime().valueAsMinutes();
								this.divTransOverTime10 = frame10.getTransferTime() == null || frame10.getTransferTime().getTime() == null ? 0 : frame10.getTransferTime().getDivergenceTime().valueAsMinutes();
							}
						}
					}
					/*----------------------日別実績の残業時間------------------------------*/
					
					/*----------------------日別実績の残業時間帯------------------------------*/
					if(totalWork != null
							&& totalWork.getExcessOfStatutoryTimeOfDaily() != null
							&& totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()
							&& !totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTimeSheet().isEmpty()) {
						Optional<TimeSpanForCalc> span;
						allClearOverTimeTS();
						val overTimeSheet = totalWork.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTimeSheet();
						span = getTimeSpan(overTimeSheet, 1);
						span.ifPresent( tc -> {
							this.overTime1StrClc = tc == null ? 0 : tc.start();
							this.overTime1EndClc = tc == null ? 0 : tc.end();
						});

						
						span = getTimeSpan(overTimeSheet, 2);
						span.ifPresent( tc -> {
							this.overTime2StrClc = tc == null ? 0 : tc.start();
							this.overTime2EndClc = tc == null ? 0 : tc.end();
						});

						span = getTimeSpan(overTimeSheet, 3);
						span.ifPresent( tc -> {
							this.overTime3StrClc = tc == null ? 0 : tc.start();
							this.overTime3EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 4);
						span.ifPresent( tc -> {
							this.overTime4StrClc = tc == null ? 0 : tc.start();
							this.overTime4EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 5);
						span.ifPresent( tc -> {
							this.overTime5StrClc = tc == null ? 0 : tc.start();
							this.overTime5EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 6);
						span.ifPresent( tc -> {
							this.overTime6StrClc = tc == null ? 0 : tc.start();
							this.overTime6EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 7);
						span.ifPresent( tc -> {
							this.overTime7StrClc = tc == null ? 0 : tc.start();
							this.overTime7EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 8);
						span.ifPresent( tc -> {
							this.overTime8StrClc = tc == null ? 0 : tc.start();
							this.overTime8EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 9);
						span.ifPresent( tc -> {
							this.overTime9StrClc = tc == null ? 0 : tc.start();
							this.overTime9EndClc = tc == null ? 0 : tc.end();
						});
				 
						span = getTimeSpan(overTimeSheet, 10);
						span.ifPresent( tc -> {
							this.overTime10StrClc = tc == null ? 0 : tc.start();
							this.overTime10EndClc = tc == null ? 0 : tc.end();
						});
					}
					/*----------------------日別実績の残業時間帯------------------------------*/
					/*----------------------日別実績の加給時間------------------------------*/
					 this.raiseSalaryTime1 = 0;
					 this.raiseSalaryTime2 = 0;
					 this.raiseSalaryTime3 = 0;
					 this.raiseSalaryTime4 = 0;
					 this.raiseSalaryTime5 = 0;
					 this.raiseSalaryTime6 = 0;
					 this.raiseSalaryTime7 = 0;
					 this.raiseSalaryTime8 = 0;
					 this.raiseSalaryTime9 = 0;
					 this.raiseSalaryTime10 = 0;
					 this.raiseSalaryInTime1 = 0;
					 this.raiseSalaryInTime2 = 0;
					 this.raiseSalaryInTime3 = 0;
					 this.raiseSalaryInTime4 = 0;
					 this.raiseSalaryInTime5 = 0;
					 this.raiseSalaryInTime6 = 0;
					 this.raiseSalaryInTime7 = 0;
					 this.raiseSalaryInTime8 = 0;
					 this.raiseSalaryInTime9 = 0 ;
					 this.raiseSalaryInTime10 = 0 ;
					 this.raiseSalaryOutTime1 = 0 ;
					 this.raiseSalaryOutTime2 = 0 ;
					 this.raiseSalaryOutTime3 = 0 ;
					 this.raiseSalaryOutTime4 = 0 ;
					 this.raiseSalaryOutTime5 = 0 ;
					 this.raiseSalaryOutTime6 = 0 ;
					 this.raiseSalaryOutTime7 = 0 ;
					 this.raiseSalaryOutTime8 = 0 ;
					 this.raiseSalaryOutTime9 = 0 ;
					 this.raiseSalaryOutTime10 = 0 ;
					 this.spRaiseSalaryTime1 = 0 ;
					 this.spRaiseSalaryTime2 = 0 ;
					 this.spRaiseSalaryTime3 = 0 ;
					 this.spRaiseSalaryTime4 = 0 ;
					 this.spRaiseSalaryTime5 = 0 ;
					 this.spRaiseSalaryTime6 = 0 ;
					 this.spRaiseSalaryTime7 = 0 ;
					 this.spRaiseSalaryTime8 = 0 ;
					 this.spRaiseSalaryTime9 = 0 ;
					 this.spRaiseSalaryTime10 = 0 ;
					 this.spRaiseSalaryInTime1 = 0 ;
					 this.spRaiseSalaryInTime2 = 0 ;
					 this.spRaiseSalaryInTime3 = 0 ;
					 this.spRaiseSalaryInTime4 = 0 ;
					 this.spRaiseSalaryInTime5 = 0 ;
					 this.spRaiseSalaryInTime6 = 0 ;
					 this.spRaiseSalaryInTime7 = 0 ;
					 this.spRaiseSalaryInTime8 = 0 ;
					 this.spRaiseSalaryInTime9 = 0 ;
					 this.spRaiseSalaryInTime10 = 0 ;
					 this.spRaiseSalaryOutTime1 = 0 ;
					 this.spRaiseSalaryOutTime2 = 0 ;
					 this.spRaiseSalaryOutTime3 = 0 ;
					 this.spRaiseSalaryOutTime4 = 0 ;
					 this.spRaiseSalaryOutTime5 = 0 ;
					 this.spRaiseSalaryOutTime6 = 0 ;
					 this.spRaiseSalaryOutTime7 = 0 ;
					 this.spRaiseSalaryOutTime8 = 0 ;
					 this.spRaiseSalaryOutTime9 = 0 ;
					 this.spRaiseSalaryOutTime10 = 0 ;
					
					if(totalWork != null
					 &&totalWork.getRaiseSalaryTimeOfDailyPerfor() != null) {
						val bpOfDaily = totalWork.getRaiseSalaryTimeOfDailyPerfor().summary(BonusPayAtr.BonusPay);
						for(BonusPayTime bounsTime : bpOfDaily) {
							setBonusPayValue(bounsTime, true);
						}
						val spBpOfDaily = totalWork.getRaiseSalaryTimeOfDailyPerfor().summary(BonusPayAtr.SpecifiedBonusPay);
						for(BonusPayTime spcBonusTime : spBpOfDaily) {
							setBonusPayValue(spcBonusTime, false);
						}
					}
					/*----------------------日別実績の加給時間------------------------------*/
					
				}
			}
		}
	}
	private void setBonusPayValue(BonusPayTime bonusPayTime,boolean isBonusPay) {
		//加給から呼び出した場合
		if(isBonusPay) {
			setPerData("raiseSalaryTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getBonusPayTime().valueAsMinutes());
			setPerData("raiseSalaryInTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getWithinBonusPay().getTime().valueAsMinutes());
			setPerData("raiseSalaryOutTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getExcessBonusPayTime().getTime().valueAsMinutes());
		}
		//特定日加給から呼びだした場合
		else {
			setPerData("spRaiseSalaryTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getBonusPayTime().valueAsMinutes());
			setPerData("spRaiseSalaryInTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getWithinBonusPay().getTime().valueAsMinutes());
			setPerData("spRaiseSalaryOutTime", bonusPayTime.getBonusPayTimeItemNo(), bonusPayTime.getExcessBonusPayTime().getTime().valueAsMinutes());
		}
	}
	
	
	private void setValue(Optional<DivergenceTime> frame,int number) {
		if(frame.isPresent()) {
			setPerData("divergenceTime",number,frame.get().getDivTime() == null ? 0 : frame.get().getDivTime().valueAsMinutes());
			setPerData("reasonCode",number,frame.get().getDivResonCode().map(c -> c.v()).orElse(null));
			setPerData("reason",number,frame.get().getDivReason().map(c -> c.v()).orElse(null));
		}
		else {
			setPerData("divergenceTime",number,0);
			setPerData("reasonCode",number,null);
			setPerData("reason",number,null);
		}
	}
	
	
	private void setPerData(String fieldName ,int number ,Object value) {
		//自分自身の値セット先(フィールド)取得
		Field field = FieldReflection.getField(this.getClass(), fieldName + number);
		//値セット
		FieldReflection.setField(field, this, value);
		
	}
	
	private Optional<DivergenceTime> getDivergenceTime(DivergenceTimeOfDaily domain,int number){
		return domain.getDivergenceTime().stream().filter(tc -> tc.getDivTimeId() == number).findFirst();
	}
	
	private HolidayWorkFrameTime getHolidayTimeFrame(List<HolidayWorkFrameTime> frames, int frameNo) {
		
		val getFrame = frames.stream().filter(tc -> tc.getHolidayFrameNo().v() == frameNo).findFirst();
		if(getFrame.isPresent()) {
			return getFrame.get();
		}
		else {
			return new HolidayWorkFrameTime(new HolidayWorkFrameNo(frameNo), 
											Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))), 
											Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))), 
											Finally.of(new AttendanceTime(0)));
					
		}
	}
	
	private OverTimeFrameTime getOverTimeFrame(List<OverTimeFrameTime> overTimeFrame, int frameNo) {
		val getFrame = overTimeFrame.stream().filter(tc -> tc.getOverWorkFrameNo().v() == frameNo).findFirst();
		if(getFrame.isPresent()) {
			return getFrame.get();
		}
		else {
			return new OverTimeFrameTime(new OverTimeFrameNo(frameNo),
										 TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)),
										 TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)),
										 new AttendanceTime(0),
										 new AttendanceTime(0));
		}
			
	}
	
	private Optional<TimeSpanForCalc> getTimeSpan(List<OverTimeFrameTimeSheet> overTimeSheet, int sheetNo) {
		 return decisionConnectSpan(overTimeSheet.stream()
 				 								 .filter(tc -> tc.getFrameNo().v().intValue() == sheetNo)
 				 								 .map(tc -> tc.getTimeSpan().getTimeSpan())
 				 								 .collect(Collectors.toList()));
	}

	private void allClearOverTimeTS() {
		this.overTime1StrClc = 0;
		this.overTime1EndClc = 0;
		this.overTime2StrClc = 0;
		this.overTime2EndClc = 0;
		this.overTime3StrClc = 0;
		this.overTime3EndClc = 0;
		this.overTime4StrClc = 0;
		this.overTime4EndClc = 0;
		this.overTime5StrClc = 0;
		this.overTime5EndClc = 0;
		this.overTime6StrClc = 0;
		this.overTime6EndClc = 0;
		this.overTime7StrClc = 0;
		this.overTime7EndClc = 0;
		this.overTime8StrClc = 0;
		this.overTime8EndClc = 0;
		this.overTime9StrClc = 0;
		this.overTime9EndClc = 0;
		this.overTime10StrClc = 0;
		this.overTime10EndClc = 0;
	}
	private void allClearHolidayWorkTS() {
		this.holiWork1StrClc = 0;
		this.holiWork1EndClc = 0;
		this.holiWork2StrClc = 0;
		this.holiWork2EndClc = 0;
		this.holiWork3StrClc = 0;
		this.holiWork3EndClc = 0;
		this.holiWork4StrClc = 0;
		this.holiWork4EndClc = 0;
		this.holiWork5StrClc = 0;
		this.holiWork5EndClc = 0;
		this.holiWork6StrClc = 0;
		this.holiWork6EndClc = 0;
		this.holiWork7StrClc = 0;
		this.holiWork7EndClc = 0;
		this.holiWork8StrClc = 0;
		this.holiWork8EndClc = 0;
		this.holiWork9StrClc = 0;
		this.holiWork9EndClc = 0;
		this.holiWork10StrClc = 0;
		this.holiWork10EndClc = 0;
	}
	
	private Optional<TimeSpanForCalc> decisionConnectSpan(List<TimeSpanForCalc> collect) {
		if(collect.isEmpty()) return Optional.empty();
		if(collect.size() == 1) return Optional.of(collect.get(0));
		return Optional.of(createConnectSpan(collect));
	}
	
	public TimeSpanForCalc createConnectSpan(List<TimeSpanForCalc> collect) {
		TimeSpanForCalc connectSpan = collect.get(0);
		for(TimeSpanForCalc nowTimeSpan : collect) {
			if(!connectSpan.equals(nowTimeSpan) && connectSpan.end().intValue() == nowTimeSpan.getStart().valueAsMinutes()) {
				connectSpan = new TimeSpanForCalc(connectSpan.getStart(), nowTimeSpan.getEnd());
			}
		}
		return connectSpan;
	}
	
	private Optional<HolidayWorkFrameTimeSheet> getTimeSheet(List<HolidayWorkFrameTimeSheet> domain, int sheetNo) {
		return domain.stream()
					 .filter(tc -> tc.getHolidayWorkTimeSheetNo().v().intValue() == sheetNo)
					 .findFirst();
	}
	
	private Optional<HolidayWorkMidNightTime> getHolidayMidNightWork(HolidayMidnightWork domain, StaturoryAtrOfHolidayWork statutoryAttr) {
		return domain.getHolidayWorkMidNightTime().stream().filter(tc -> tc.getStatutoryAtr().equals(statutoryAttr)).findFirst();
	}

	
	public AttendanceTimeOfDailyPerformance toDomain() {
		
		// 日別実績の勤怠時間
		return toDomain(this, this.krcdtDayPremiumTime, 
						getKrcdtDayLeaveEarlyTime(), 
						getKrcdtDayLateTime(), 
						this.krcdtDayShorttime, 
						this.krcdtDayOutingTime);
		
	}
	
	public List<KrcdtDayLeaveEarlyTime> getKrcdtDayLeaveEarlyTime() {
		return krcdtDayLeaveEarlyTime == null ? new ArrayList<>() : krcdtDayLeaveEarlyTime;
	}

	public List<KrcdtDayLateTime> getKrcdtDayLateTime() {
		return krcdtDayLateTime == null ? new ArrayList<>() : krcdtDayLateTime;
	}
	
	public static AttendanceTimeOfDailyPerformance toDomain(KrcdtDayTimeAtd entity,
												   			KrcdtDayTimePremium krcdtDayPremiumTime,
												   			List<KrcdtDayLeaveEarlyTime> krcdtDayLeaveEarlyTime,
												   			List<KrcdtDayLateTime> krcdtDayLateTime,
												   			List<KrcdtDayShorttime> krcdtDayShorttime
												   			,List<KrcdtDayTimeGoout> krcdtDayOutingTime) {
		
		/*日別実績の休憩時間*/
		val breakTime = createBreakTime(entity);
		
		
		//勤務予定時間 - 日別実績の勤務予定時間
		WorkScheduleTimeOfDaily workScheduleTimeOfDaily = createScheduleWorkTime(entity);
		
		
		/*日別実績の所定内時間*/
		val within =  createWithinStatutoryTime(entity);
		
		/*日別実績の休出時間*/
		List<HolidayWorkFrameTime> holiWorkFrameTimeList = createHolidayWorkTime(entity);
							
		List<HolidayWorkMidNightTime> holidayWorkMidNightTimeList = createHolidayWorkMidNightTime(entity);
		
		
		/*日別実績の休出時間帯*/
		List<HolidayWorkFrameTimeSheet> holidayWOrkTimeTS = createHolidayWorkTimeSheet(entity);
		
		val holidayWork = new HolidayWorkTimeOfDaily(holidayWOrkTimeTS,holiWorkFrameTimeList,Finally.of(new HolidayMidnightWork(holidayWorkMidNightTimeList)), new AttendanceTime(entity.holiWorkBindTime));
		
		/*日別実績の休暇*/
		val vacation =  createHoliday(entity);
		
		val overTime = createOverTime(entity);
		
		/*日別実績の遅刻時間*/
		List<LateTimeOfDaily> lateTime = new ArrayList<>();
		for (KrcdtDayLateTime krcdt : krcdtDayLateTime) {
			lateTime.add(krcdt.toDomain());
		}
		/*日別実績の早退時間*/
		List<LeaveEarlyTimeOfDaily> leaveEarly = new ArrayList<>();
		for (KrcdtDayLeaveEarlyTime krcdt : krcdtDayLeaveEarlyTime) {
			leaveEarly.add(krcdt.toDomain());
		}
		
		/*日別実績の短時間勤務*/
		List<ShortWorkTimeOfDaily> shortTime = createShortTime(krcdtDayShorttime);
		
		/*日別実績の加給時間*/
		List<BonusPayTime> bonusPayTime = createBonusTime(entity);
		
		//特定日加給
		List<BonusPayTime> specBonusPayTime = createBonusSpecTime(entity);
		
		List<OutingTimeOfDaily> outingOfDaily = new ArrayList<>();
		for(KrcdtDayTimeGoout outingTime : krcdtDayOutingTime) {
			outingOfDaily.add(outingTime.toDomain());
		}
		
		// 日別実績の総労働時間
		TotalWorkingTime totalTime = new TotalWorkingTime(new AttendanceTime(entity.totalAttTime),
														  new AttendanceTime(entity.totalCalcTime),
														  new AttendanceTime(entity.actWorkTime),
														  within, 
														  new ExcessOfStatutoryTimeOfDaily(
																  new ExcessOfStatutoryMidNightTime(
																		  TimeDivergenceWithCalculation.createTimeWithCalculation(
																				  new AttendanceTime(entity.outPrsMidnTime),
																				  new AttendanceTime(entity.calcOutPrsMidnTime)),
																				  new AttendanceTime(entity.preOutPrsMidnTime)),
																		  Optional.of(overTime),
																		  Optional.of(holidayWork)), 
														  lateTime, 
														  leaveEarly,
														  breakTime,
														  outingOfDaily,
														  new RaiseSalaryTimeOfDailyPerfor(bonusPayTime, specBonusPayTime),
														  new WorkTimes(entity.workTimes),
														  new TemporaryTimeOfDaily(),
														  shortTime.get(0),
														  vacation,
														  IntervalTimeOfDaily.of(
																  new AttendanceClock(entity.intervalAttendance), 
																  new AttendanceTime(entity.intervalTime)),
														  new AttendanceTime(entity.calcDiffTime),
														  new AttendanceTime(entity.vactnAddTime));

		/*日別実績の乖離時間*/
		val divergence = createDivergence(entity);
		
		//実働時間/実績時間  - 日別実績の勤務実績時間
		ActualWorkingTimeOfDaily actualWorkingTimeOfDaily = ActualWorkingTimeOfDaily.of(totalTime,
																						entity.midnBindTime,
																						entity.totalBindTime,
																						entity.bindDiffTime,
																						entity.diffTimeWorkTime,
																						divergence,
																						entity.krcdtDayPremiumTime == null ? PremiumTimeOfDailyPerformance.createEmpty() : entity.krcdtDayPremiumTime.toDomain());
		
		AttendanceTimeOfDailyPerformance domain = new AttendanceTimeOfDailyPerformance(entity.krcdtDayTimePK.employeeID,
																					   entity.krcdtDayTimePK.generalDate,
																					   workScheduleTimeOfDaily,
																					   actualWorkingTimeOfDaily,
																					   new StayingTimeOfDaily(new AttendanceTimeOfExistMinus(entity.aftPcLogoffTime),
																								new AttendanceTimeOfExistMinus(entity.bfrPcLogonTime), new AttendanceTimeOfExistMinus(entity.bfrWorkTime),
																								new AttendanceTime(entity.stayingTime), new AttendanceTimeOfExistMinus(entity.aftLeaveTime)),
																					   new AttendanceTimeOfExistMinus(entity.unemployedTime),
																					   new AttendanceTimeOfExistMinus(entity.budgetTimeVariance)
																					   );
		
		return domain;
	}

	private static List<BonusPayTime> createBonusSpecTime(KrcdtDayTimeAtd entity) {
		List<BonusPayTime> specBonusPayTime = new ArrayList<>();
		specBonusPayTime.add(new BonusPayTime(1, new AttendanceTime(entity.spRaiseSalaryTime1), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime1), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime1), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(2, new AttendanceTime(entity.spRaiseSalaryTime2), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime2), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime2), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(3, new AttendanceTime(entity.spRaiseSalaryTime3), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime3), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime3), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(4, new AttendanceTime(entity.spRaiseSalaryTime4), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime4), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime4), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(5, new AttendanceTime(entity.spRaiseSalaryTime5), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime5), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime5), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(6, new AttendanceTime(entity.spRaiseSalaryTime6), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime6), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime6), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(7, new AttendanceTime(entity.spRaiseSalaryTime7), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime7), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime7), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(8, new AttendanceTime(entity.spRaiseSalaryTime8), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime8), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime8), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(9, new AttendanceTime(entity.spRaiseSalaryTime9), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime9), new AttendanceTime(0)), 
																								TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime9), new AttendanceTime(0))));
		specBonusPayTime.add(new BonusPayTime(10, new AttendanceTime(entity.spRaiseSalaryTime10), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryInTime10), new AttendanceTime(0)), 
																								  TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.spRaiseSalaryOutTime10), new AttendanceTime(0))));
		return specBonusPayTime;
	}

	private static List<BonusPayTime> createBonusTime(KrcdtDayTimeAtd entity) {
		List<BonusPayTime> bonusPayTime = new ArrayList<>();
		bonusPayTime.add(new BonusPayTime(1, new AttendanceTime(entity.raiseSalaryTime1), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime1), new AttendanceTime(0)), 
																   						  TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime1), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(2, new AttendanceTime(entity.raiseSalaryTime2), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime2), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime2), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(3, new AttendanceTime(entity.raiseSalaryTime3), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime3), new AttendanceTime(0)), 
																   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime3), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(4, new AttendanceTime(entity.raiseSalaryTime4), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime4), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime4), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(5, new AttendanceTime(entity.raiseSalaryTime5), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime5), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime5), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(6, new AttendanceTime(entity.raiseSalaryTime6), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime6), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime6), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(7, new AttendanceTime(entity.raiseSalaryTime7), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime7), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime7), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(8, new AttendanceTime(entity.raiseSalaryTime8), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime8), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime8), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(9, new AttendanceTime(entity.raiseSalaryTime9), TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime9), new AttendanceTime(0)), 
				   												   							TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime9), new AttendanceTime(0))));
		bonusPayTime.add(new BonusPayTime(10, new AttendanceTime(entity.raiseSalaryTime10),TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryInTime10), new AttendanceTime(0)), 
				   												   							 TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.raiseSalaryOutTime10), new AttendanceTime(0))));
		return bonusPayTime;
	}

	private static List<ShortWorkTimeOfDaily> createShortTime(List<KrcdtDayShorttime> krcdtDayShorttime) {
		List<ShortWorkTimeOfDaily> shortTime = new ArrayList<>();
		
		for(KrcdtDayShorttime shortTimeValue : krcdtDayShorttime) {
			if(shortTimeValue != null) {
				shortTime.add(new ShortWorkTimeOfDaily(new WorkTimes(shortTimeValue.count == null ? 0 : shortTimeValue.count),
											  DeductionTotalTime.of(TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.toRecordTotalTime == null ? 0 : shortTimeValue.toRecordTotalTime), 
													  															  new AttendanceTime(shortTimeValue.calToRecordTotalTime == null ? 0 : shortTimeValue.calToRecordTotalTime)), 
													  				TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.toRecordInTime == null ? 0 : shortTimeValue.toRecordInTime), 
													  															  new AttendanceTime(shortTimeValue.calToRecordInTime == null ? 0 : shortTimeValue.calToRecordInTime)), 
													  				TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.toRecordOutTime == null ? 0 : shortTimeValue.toRecordOutTime), 
													  															  new AttendanceTime(shortTimeValue.calToRecordOutTime == null ? 0 : shortTimeValue.calToRecordOutTime ))),
											  DeductionTotalTime.of(TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.deductionTotalTime == null ? 0 : shortTimeValue.deductionTotalTime ), 
													  															  new AttendanceTime(shortTimeValue.calDeductionTotalTime == null ? 0 : shortTimeValue.calDeductionTotalTime)), 
													  				TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.deductionInTime == null ? 0 : shortTimeValue.deductionInTime), 
													  															  new AttendanceTime(shortTimeValue.calDeductionInTime == null ? 0 : shortTimeValue.calDeductionInTime)), 
													  				TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(shortTimeValue.deductionOutTime == null ? 0 : shortTimeValue.deductionOutTime),
													  															  new AttendanceTime(shortTimeValue.calDeductionOutTime == null ? 0 : shortTimeValue.calDeductionOutTime))),
											  ChildCareAtr.valueOf(shortTimeValue.krcdtDayShorttimePK == null || shortTimeValue.krcdtDayShorttimePK.childCareAtr == null? 0 : shortTimeValue.krcdtDayShorttimePK.childCareAtr)));
			}
		}
		
		if(shortTime.isEmpty()) {
			shortTime.add( new  ShortWorkTimeOfDaily(new WorkTimes(0),
                    DeductionTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
                                           TimeWithCalculation.sameTime(new AttendanceTime(0)),
                                           TimeWithCalculation.sameTime(new AttendanceTime(0))),
                    DeductionTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
                                              TimeWithCalculation.sameTime(new AttendanceTime(0)),
                                              TimeWithCalculation.sameTime(new AttendanceTime(0))),
                    ChildCareAtr.CARE));

		}
		
		return shortTime;
	}
	
	private static DivergenceTimeOfDaily createDivergence(KrcdtDayTimeAtd entity) {
		List<DivergenceTime> divergenceTimeList = new ArrayList<>();
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime1),
												  1,
												  StringUtil.isNullOrEmpty(entity.reason1, true) ? null : new DivergenceReasonContent(entity.reason1),
												  StringUtil.isNullOrEmpty(entity.reasonCode1, true) ? null : new DiverdenceReasonCode(entity.reasonCode1)));
		
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime2),
				  								  2,
				  								StringUtil.isNullOrEmpty(entity.reason2, true) ? null : new DivergenceReasonContent(entity.reason2),
		  										StringUtil.isNullOrEmpty(entity.reasonCode2, true) ? null : new DiverdenceReasonCode(entity.reasonCode2)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime3),
				  								  3,
				  								StringUtil.isNullOrEmpty(entity.reason3, true) ? null : new DivergenceReasonContent(entity.reason3),
		  										StringUtil.isNullOrEmpty(entity.reasonCode3, true) ? null : new DiverdenceReasonCode(entity.reasonCode3)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime4),
												  4,
												  StringUtil.isNullOrEmpty(entity.reason4, true) ? null : new DivergenceReasonContent(entity.reason4),
												  StringUtil.isNullOrEmpty(entity.reasonCode4, true) ? null : new DiverdenceReasonCode(entity.reasonCode4)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime5),
				  								  5,
				  								StringUtil.isNullOrEmpty(entity.reason5, true) ? null : new DivergenceReasonContent(entity.reason5),
		  										StringUtil.isNullOrEmpty(entity.reasonCode5, true) ? null : new DiverdenceReasonCode(entity.reasonCode5)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime6),
				  								  6,
				  								StringUtil.isNullOrEmpty(entity.reason6, true) ? null : new DivergenceReasonContent(entity.reason6),
		  										StringUtil.isNullOrEmpty(entity.reasonCode6, true) ? null : new DiverdenceReasonCode(entity.reasonCode6)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime7),
				  								  7,
				  								StringUtil.isNullOrEmpty(entity.reason7, true) ? null : new DivergenceReasonContent(entity.reason7),
		  										StringUtil.isNullOrEmpty(entity.reasonCode7, true) ? null : new DiverdenceReasonCode(entity.reasonCode7)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime8),
				  								  8,
				  								StringUtil.isNullOrEmpty(entity.reason8, true) ? null : new DivergenceReasonContent(entity.reason8),
		  										StringUtil.isNullOrEmpty(entity.reasonCode8, true) ? null : new DiverdenceReasonCode(entity.reasonCode8)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime9),
				  								  9,
				  								StringUtil.isNullOrEmpty(entity.reason9, true) ? null : new DivergenceReasonContent(entity.reason9),
		  										StringUtil.isNullOrEmpty(entity.reasonCode9, true) ? null : new DiverdenceReasonCode(entity.reasonCode9)));
		divergenceTimeList.add(new DivergenceTime(new AttendanceTimeOfExistMinus(entity.divergenceTime10),
				  								  10,
				  								StringUtil.isNullOrEmpty(entity.reason10, true) ? null : new DivergenceReasonContent(entity.reason10),
		  										StringUtil.isNullOrEmpty(entity.reasonCode10, true) ? null : new DiverdenceReasonCode(entity.reasonCode10)));
		
		return new DivergenceTimeOfDaily(divergenceTimeList);
	}
	
	private static OverTimeOfDaily createOverTime(KrcdtDayTimeAtd entity) {
		
		/*日別実績の残業時間帯*/
		List<OverTimeFrameTimeSheet> timeSheet = createOverTimeTimeSheet(entity);
		
		/*日別実績の残業時間*/
		List<OverTimeFrameTime> list = createOverTimeFrame(entity);
		
		
		return new OverTimeOfDaily(timeSheet, 
				   						   list,
				   						   Finally.of(new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.ileglMidntOverTime),new AttendanceTime(entity.calcIleglMidNOverTime)))),
				   						   new AttendanceTime(entity.deformLeglOverTime),
				   						   new FlexTime(TimeDivergenceWithCalculationMinusExist.createTimeWithCalculation(new AttendanceTimeOfExistMinus(entity.flexTime), new AttendanceTimeOfExistMinus(entity.calcFlexTime)),new AttendanceTime(entity.preAppFlexTime)),
				   						   new AttendanceTime(entity.overTimeBindTime));
	}

	private static List<OverTimeFrameTime> createOverTimeFrame(KrcdtDayTimeAtd entity) {
		List<OverTimeFrameTime> list = new ArrayList<>();
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(1),
									   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime1), new AttendanceTime(entity.calcOverTime1)),
									   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime1), new AttendanceTime(entity.calcTransOverTime1)),
									   new AttendanceTime(entity.preOverTimeAppTime1),
									   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(2),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime2), new AttendanceTime(entity.calcOverTime2)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime2), new AttendanceTime(entity.calcTransOverTime2)),
				   new AttendanceTime(entity.preOverTimeAppTime2),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(3),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime3), new AttendanceTime(entity.calcOverTime3)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime3), new AttendanceTime(entity.calcTransOverTime3)),
				   new AttendanceTime(entity.preOverTimeAppTime3),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(4),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime4), new AttendanceTime(entity.calcOverTime4)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime4), new AttendanceTime(entity.calcTransOverTime4)),
				   new AttendanceTime(entity.preOverTimeAppTime4),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(5),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime5), new AttendanceTime(entity.calcOverTime5)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime5), new AttendanceTime(entity.calcTransOverTime5)),
				   new AttendanceTime(entity.preOverTimeAppTime5),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(6),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime6), new AttendanceTime(entity.calcOverTime6)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime6), new AttendanceTime(entity.calcTransOverTime6)),
				   new AttendanceTime(entity.preOverTimeAppTime6),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(7),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime7), new AttendanceTime(entity.calcOverTime7)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime7), new AttendanceTime(entity.calcTransOverTime7)),
				   new AttendanceTime(entity.preOverTimeAppTime7),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(8),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime8), new AttendanceTime(entity.calcOverTime8)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime8), new AttendanceTime(entity.calcTransOverTime8)),
				   new AttendanceTime(entity.preOverTimeAppTime8),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(9),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime9), new AttendanceTime(entity.calcOverTime9)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime9), new AttendanceTime(entity.calcTransOverTime9)),
				   new AttendanceTime(entity.preOverTimeAppTime9),
				   new AttendanceTime(0)));
		
		list.add(new OverTimeFrameTime(new OverTimeFrameNo(10),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.overTime10), new AttendanceTime(entity.calcOverTime10)),
				   TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transOverTime10), new AttendanceTime(entity.calcTransOverTime10)),
				   new AttendanceTime(entity.preOverTimeAppTime10),
				   new AttendanceTime(0)));
		
		return list;
	}
	
	private static List<OverTimeFrameTimeSheet> createOverTimeTimeSheet(KrcdtDayTimeAtd entity) {
		List<OverTimeFrameTimeSheet> timeSheet = new ArrayList<>();
		if (entity.overTime1StrClc != null && entity.overTime1EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime1StrClc),new TimeWithDayAttr(entity.overTime1EndClc)),new OverTimeFrameNo(1)));
		}
		if (entity.overTime2StrClc != null && entity.overTime2EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime2StrClc),new TimeWithDayAttr(entity.overTime2EndClc)),new OverTimeFrameNo(2)));
		}
		if (entity.overTime3StrClc != null && entity.overTime3EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime3StrClc),new TimeWithDayAttr(entity.overTime3EndClc)),new OverTimeFrameNo(3)));
		}
		if (entity.overTime4StrClc != null && entity.overTime4EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime4StrClc),new TimeWithDayAttr(entity.overTime4EndClc)),new OverTimeFrameNo(4)));
		}
		if (entity.overTime5StrClc != null && entity.overTime5EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime5StrClc),new TimeWithDayAttr(entity.overTime5EndClc)),new OverTimeFrameNo(5)));
		}
		if (entity.overTime6StrClc != null && entity.overTime6EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime6StrClc),new TimeWithDayAttr(entity.overTime6EndClc)),new OverTimeFrameNo(6)));
		}
		if (entity.overTime7StrClc != null && entity.overTime7EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime7StrClc),new TimeWithDayAttr(entity.overTime7EndClc)),new OverTimeFrameNo(7)));
		}
		if (entity.overTime8StrClc != null && entity.overTime8EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime8StrClc),new TimeWithDayAttr(entity.overTime8EndClc)),new OverTimeFrameNo(8)));
		}
		if (entity.overTime9StrClc != null && entity.overTime9EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime9StrClc),new TimeWithDayAttr(entity.overTime9EndClc)),new OverTimeFrameNo(9)));
		}
		if (entity.overTime10StrClc != null && entity.overTime10EndClc != null) {
			timeSheet.add(new OverTimeFrameTimeSheet(new TimeSpanForDailyCalc(new TimeWithDayAttr(entity.overTime10StrClc),new TimeWithDayAttr(entity.overTime10EndClc)),new OverTimeFrameNo(10)));
		}
		
		return timeSheet;
	}

	private static HolidayOfDaily createHoliday(KrcdtDayTimeAtd entity) {
		return new HolidayOfDaily(new AbsenceOfDaily(new AttendanceTime(entity.absenceTime)),
				  new TimeDigestOfDaily(new AttendanceTime(entity.tdvTime),new AttendanceTime(entity.tdvShortageTime)),
				  new YearlyReservedOfDaily(new AttendanceTime(entity.retentionYearlyTime)),
				  new SubstituteHolidayOfDaily(new AttendanceTime(entity.compensatoryLeaveTime),new AttendanceTime(entity.compensatoryLeaveTdvTime)),
				  new OverSalaryOfDaily(new AttendanceTime(entity.excessSalaryiesTime),new AttendanceTime(entity.excessSalaryiesTdvTime)),
				  new SpecialHolidayOfDaily(new AttendanceTime(entity.specialHolidayTime),new AttendanceTime(entity.specialHolidayTdvTime)),
				  new AnnualOfDaily(new AttendanceTime(entity.annualleaveTime), new AttendanceTime(entity.annualleaveTdvTime)),
				  new TransferHolidayOfDaily(new AttendanceTime(entity.transferHolidayTime)));
	}
	
	private static List<HolidayWorkFrameTimeSheet> createHolidayWorkTimeSheet(KrcdtDayTimeAtd entity) {
		List<HolidayWorkFrameTimeSheet> holidayWOrkTimeTS = new ArrayList<>();
		if(entity.holiWork1StrClc != null && entity.holiWork1EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(1)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork1StrClc),new TimeWithDayAttr(entity.holiWork1EndClc))));
		}
		if(entity.holiWork2StrClc != null && entity.holiWork2EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(2)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork2StrClc),new TimeWithDayAttr(entity.holiWork2EndClc))));
		}
		if(entity.holiWork3StrClc != null && entity.holiWork3EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(3)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork3StrClc),new TimeWithDayAttr(entity.holiWork3EndClc))));
		}
		if(entity.holiWork4StrClc != null && entity.holiWork4EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(4)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork4StrClc),new TimeWithDayAttr(entity.holiWork4EndClc))));
		}
		if(entity.holiWork5StrClc != null && entity.holiWork5EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(5)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork5StrClc),new TimeWithDayAttr(entity.holiWork5EndClc))));
		}
		if(entity.holiWork6StrClc != null && entity.holiWork6EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(6)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork6StrClc),new TimeWithDayAttr(entity.holiWork6EndClc))));
		}
		if(entity.holiWork7StrClc != null && entity.holiWork7EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(7)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork7StrClc),new TimeWithDayAttr(entity.holiWork7EndClc))));
		}
		if(entity.holiWork8StrClc != null && entity.holiWork8EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(8)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork8StrClc),new TimeWithDayAttr(entity.holiWork8EndClc))));
		}
		if(entity.holiWork9StrClc != null && entity.holiWork9EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(9)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork9StrClc),new TimeWithDayAttr(entity.holiWork9EndClc))));
		}
		if(entity.holiWork10StrClc != null && entity.holiWork10EndClc != null) {
			holidayWOrkTimeTS.add(new HolidayWorkFrameTimeSheet(new HolidayWorkFrameNo(Integer.valueOf(10)),new TimeSpanForCalc(new TimeWithDayAttr(entity.holiWork10StrClc),new TimeWithDayAttr(entity.holiWork10EndClc))));
		}
		
		return holidayWOrkTimeTS;
	}
	private static List<HolidayWorkMidNightTime> createHolidayWorkMidNightTime(KrcdtDayTimeAtd entity) {
		List<HolidayWorkMidNightTime> holidayWorkMidNightTimeList = new ArrayList<>();
		
		holidayWorkMidNightTimeList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.legHoliWorkMidn),new AttendanceTime(entity.calcLegHoliWorkMidn)),
				StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork));
		holidayWorkMidNightTimeList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.illegHoliWorkMidn),new AttendanceTime(entity.calcIllegHoliWorkMidn)),
				StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork));
		holidayWorkMidNightTimeList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.pbHoliWorkMidn),new AttendanceTime(entity.calcPbHoliWorkMidn)),
				StaturoryAtrOfHolidayWork.PublicHolidayWork));
		
		return holidayWorkMidNightTimeList;
	}
	
	private static List<HolidayWorkFrameTime> createHolidayWorkTime(KrcdtDayTimeAtd entity) {
		
		List<HolidayWorkFrameTime> holiWorkFrameTimeList = new ArrayList<>();
		
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(1),
					Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime1),new AttendanceTime(entity.calcHoliWorkTime1))),
					Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime1),new AttendanceTime(entity.calcTransTime1))),
					Finally.of(new AttendanceTime(entity.preAppTime1))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(2),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime2),new AttendanceTime(entity.calcHoliWorkTime2))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime2),new AttendanceTime(entity.calcTransTime2))),
				Finally.of(new AttendanceTime(entity.preAppTime2))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(3),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime3),new AttendanceTime(entity.calcHoliWorkTime3))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime3),new AttendanceTime(entity.calcTransTime3))),
				Finally.of(new AttendanceTime(entity.preAppTime3))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(4),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime4),new AttendanceTime(entity.calcHoliWorkTime4))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime4),new AttendanceTime(entity.calcTransTime4))),
				Finally.of(new AttendanceTime(entity.preAppTime4))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(5),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime5),new AttendanceTime(entity.calcHoliWorkTime5))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime5),new AttendanceTime(entity.calcTransTime5))),
				Finally.of(new AttendanceTime(entity.preAppTime5))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(6),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime6),new AttendanceTime(entity.calcHoliWorkTime6))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime6),new AttendanceTime(entity.calcTransTime6))),
				Finally.of(new AttendanceTime(entity.preAppTime6))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(7),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime7),new AttendanceTime(entity.calcHoliWorkTime7))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime7),new AttendanceTime(entity.calcTransTime7))),
				Finally.of(new AttendanceTime(entity.preAppTime7))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(8),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime8),new AttendanceTime(entity.calcHoliWorkTime8))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime8),new AttendanceTime(entity.calcTransTime8))),
				Finally.of(new AttendanceTime(entity.preAppTime8))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(9),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime9),new AttendanceTime(entity.calcHoliWorkTime9))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime9),new AttendanceTime(entity.calcTransTime9))),
				Finally.of(new AttendanceTime(entity.preAppTime9))));
		holiWorkFrameTimeList.add(new HolidayWorkFrameTime(new HolidayWorkFrameNo(10),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.holiWorkTime10),new AttendanceTime(entity.calcHoliWorkTime10))),
				Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.transTime10),new AttendanceTime(entity.calcTransTime10))),
				Finally.of(new AttendanceTime(entity.preAppTime10))));
		
		return holiWorkFrameTimeList;
	}

	private static WithinStatutoryTimeOfDaily createWithinStatutoryTime(KrcdtDayTimeAtd entity) {
		return WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(new AttendanceTime(entity.workTime),
				   																  new AttendanceTime(entity.pefomWorkTime),
				   																  new AttendanceTime(entity.prsIncldPrmimTime),
				   																  new WithinStatutoryMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.prsIncldMidnTime),
				   																		  																				  new AttendanceTime(entity.calcPrsIncldMidnTime))),
				   																  new AttendanceAmountDaily(entity.workTimeAmount));
	}

	private static WorkScheduleTimeOfDaily createScheduleWorkTime(KrcdtDayTimeAtd entity) {
		return new WorkScheduleTimeOfDaily(new WorkScheduleTime(new AttendanceTime(entity.workScheduleTime),
						   																				   new AttendanceTime(0),
						   																				   new AttendanceTime(0)),
//				   																	  new AttendanceTime(entity.schedulePreLaborTime),
				   																	  new AttendanceTime(entity.recorePreLaborTime));
	}

	private static BreakTimeOfDaily createBreakTime(KrcdtDayTimeAtd entity) {
		return new BreakTimeOfDaily(DeductionTotalTime.of(TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.toRecordTotalTime), new AttendanceTime(entity.calToRecordTotalTime)),
				  			                       TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.toRecordInTime), new AttendanceTime(entity.calToRecordInTime)),
				  								   TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.toRecordOutTime), new AttendanceTime(entity.calToRecordOutTime))), 
							DeductionTotalTime.of(TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.deductionTotalTime), new AttendanceTime(entity.calDeductionTotalTime)),
												  TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.deductionInTime), new AttendanceTime(entity.calDeductionInTime)),
												  TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(entity.deductionOutTime), new AttendanceTime(entity.calDeductionOutTime))), 
							new BreakTimeGoOutTimes(entity.count), 
							new AttendanceTime(entity.duringworkTime), 
							Collections.emptyList());
	}
	
	
}
