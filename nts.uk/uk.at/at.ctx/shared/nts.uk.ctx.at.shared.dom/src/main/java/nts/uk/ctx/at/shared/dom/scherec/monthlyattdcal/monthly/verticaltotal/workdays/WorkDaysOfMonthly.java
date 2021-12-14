package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays;

import java.io.Serializable;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.WorkTypeDaysCountTable;
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
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 月別実績の勤務日数
 * @author shuichi_ishida
 */
@Getter
public class WorkDaysOfMonthly implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 出勤日数 */
	private AttendanceDaysOfMonthly attendanceDays;
	/** 勤務日数 */
	private WorkDaysDetailOfMonthly workDays;
	/** 勤務回数 */
	private WorkTimesOfMonthly workTimes;
	/** 二回勤務回数 */
	private TwoTimesWorkTimesOfMonthly twoTimesWorkTimes;
	/** 臨時勤務回数 */
	private TemporaryWorkTimesOfMonthly temporaryWorkTimes;
	/** 休出日数 */
	private HolidayWorkDaysOfMonthly holidayWorkDays;
	/** 振出日数 */
	private RecruitmentDaysOfMonthly recruitmentDays;
	/** 休日日数 */
	private HolidayDaysOfMonthly holidayDays;
	/** 所定日数 */
	private PredeterminedDaysOfMonthly predetermineDays;
	/** 特定日数 */
	private SpecificDaysOfMonthly specificDays;
	/** 欠勤日数 */
	private AbsenceDaysOfMonthly absenceDays;
	/** 特別休暇日数 */
	private SpcVacationDaysOfMonthly specialVacationDays;
	/** 休業 */
	private LeaveOfMonthly leave;
	/** 直行直帰日数 */
	private StgGoStgBackDaysOfMonthly straightDays;
	/** 時間消化休暇日数 */
	private TimeConsumpVacationDaysOfMonthly timeConsumpDays;
	
	/**
	 * コンストラクタ
	 */
	public WorkDaysOfMonthly(){
		
		this.attendanceDays = new AttendanceDaysOfMonthly();
		this.absenceDays = new AbsenceDaysOfMonthly();
		this.predetermineDays = new PredeterminedDaysOfMonthly();
		this.workDays = new WorkDaysDetailOfMonthly();
		this.holidayDays = new HolidayDaysOfMonthly();
		this.specificDays = new SpecificDaysOfMonthly();
		this.holidayWorkDays = new HolidayWorkDaysOfMonthly();
		this.straightDays = new StgGoStgBackDaysOfMonthly();
		this.workTimes = new WorkTimesOfMonthly();
		this.twoTimesWorkTimes = new TwoTimesWorkTimesOfMonthly();
		this.temporaryWorkTimes = new TemporaryWorkTimesOfMonthly();
		this.leave = new LeaveOfMonthly();
		this.recruitmentDays = new RecruitmentDaysOfMonthly();
		this.specialVacationDays = new SpcVacationDaysOfMonthly();
		this.timeConsumpDays = new TimeConsumpVacationDaysOfMonthly();
	}

	/**
	 * ファクトリー
	 * @param attendanceDays 出勤日数
	 * @param absenceDays 欠勤日数
	 * @param predetermineDays 所定日数
	 * @param workDays 勤務日数
	 * @param holidayDays 休日日数
	 * @param specficDays 特定日数
	 * @param holidayWorkDays 休出日数
	 * @param straightDays 直行直帰日数
	 * @param workTimes 勤務回数
	 * @param twoTimesWorkTimes 二回勤務回数
	 * @param temporaryWorkTimes 臨時勤務回数
	 * @param leave 休業
	 * @param recruitmentDays 振出日数
	 * @param specialVacationDays 特別休暇日数
	 * @param timeConsump 時間消化休暇
	 * @return 月別実績の勤務日数
	 */
	public static WorkDaysOfMonthly of(
			AttendanceDaysOfMonthly attendanceDays,
			AbsenceDaysOfMonthly absenceDays,
			PredeterminedDaysOfMonthly predetermineDays,
			WorkDaysDetailOfMonthly workDays,
			HolidayDaysOfMonthly holidayDays,
			SpecificDaysOfMonthly specficDays,
			HolidayWorkDaysOfMonthly holidayWorkDays,
			StgGoStgBackDaysOfMonthly straightDays,
			WorkTimesOfMonthly workTimes,
			TwoTimesWorkTimesOfMonthly twoTimesWorkTimes,
			TemporaryWorkTimesOfMonthly temporaryWorkTimes,
			LeaveOfMonthly leave,
			RecruitmentDaysOfMonthly recruitmentDays,
			SpcVacationDaysOfMonthly specialVacationDays,
			TimeConsumpVacationDaysOfMonthly timeConsump){
		
		val domain = new WorkDaysOfMonthly();
		domain.attendanceDays = attendanceDays;
		domain.absenceDays = absenceDays;
		domain.predetermineDays = predetermineDays;
		domain.workDays = workDays;
		domain.holidayDays = holidayDays;
		domain.specificDays = specficDays;
		domain.holidayWorkDays = holidayWorkDays;
		domain.straightDays = straightDays;
		domain.workTimes = workTimes;
		domain.twoTimesWorkTimes = twoTimesWorkTimes;
		domain.temporaryWorkTimes = temporaryWorkTimes;
		domain.leave = leave;
		domain.recruitmentDays = recruitmentDays;
		domain.specialVacationDays = specialVacationDays;
		domain.timeConsumpDays = timeConsump;
		return domain;
	}
	
	/**
	 * 集計
	 * @param workingSystem 労働制
	 * @param workType 勤務種類
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 * @param temporaryTimeOfDaily 日別実績の臨時出退勤
	 * @param specificDateAttrOfDaily 日別実績の特定日区分
	 * @param workTypeDaysCountTable 勤務種類の日数カウント表
	 * @param payItemCount 月別実績の給与項目カウント
	 * @param predetermineTimeSet 所定時間設定
	 * @param isAttendanceDay 出勤しているかどうか
	 * @param isTwoTimesStampExists 2回目の打刻が存在するかどうか
	 */
	public void aggregate(
			RequireM1 require, String sid, String cid, GeneralDate ymd, 
			WorkingSystem workingSystem, WorkType workType,
			AttendanceTimeOfDailyAttendance attendanceTimeOfDaily,
			SpecificDateAttrOfDailyAttd specificDateAttrOfDaily,
			WorkTypeDaysCountTable workTypeDaysCountTable,
			WorkInfoOfDailyAttendance workInfo, PredetemineTimeSetting predetermineTimeSet,
			boolean isAttendanceDay, boolean isTwoTimesStampExists){
		
		// 出勤日数の集計
		this.attendanceDays.aggregate(workingSystem, workTypeDaysCountTable, isAttendanceDay);
		
		// 欠勤日数の集計
		this.absenceDays.aggregate(require, cid, sid, ymd, workInfo.getRecordInfo(),
				workingSystem, workType, workTypeDaysCountTable, isAttendanceDay);
		
		// 所定日数の集計
		this.predetermineDays.aggregate(workTypeDaysCountTable);
		
		// 勤務日数の集計
		this.workDays.aggregate(workingSystem, workTypeDaysCountTable, isAttendanceDay);
		
		// 休日日数の集計
		this.holidayDays.aggregate(workTypeDaysCountTable);
		
		// 特定日日数の集計
		this.specificDays.aggregate(require, workingSystem, workType, 
				specificDateAttrOfDaily, workTypeDaysCountTable, isAttendanceDay);
		
		// 休出日数の集計
		this.holidayWorkDays.aggregate(workingSystem, workTypeDaysCountTable, isAttendanceDay);
		
		// 直行直帰日数の集計
		this.straightDays.aggregate(workInfo);
		
		// 勤務回数の集計
		this.workTimes.aggregate(attendanceTimeOfDaily);
		
		// 二回勤務回数の集計
		this.twoTimesWorkTimes.aggregate(predetermineTimeSet, isTwoTimesStampExists);
		
		// 臨時回数・時間の集計
		this.temporaryWorkTimes.aggregate(attendanceTimeOfDaily);
		
		// 休業日数の集計
		this.leave.aggregate(workTypeDaysCountTable);
		
		// 振出日数の集計
		this.recruitmentDays.aggregate(require, workingSystem, workTypeDaysCountTable, isAttendanceDay);
		
		// 特別休暇日数の集計
		this.specialVacationDays.aggregate(require, cid, sid, ymd, workingSystem, workType, 
				workInfo.getRecordInfo(), Optional.ofNullable(attendanceTimeOfDaily), 
				workTypeDaysCountTable, isAttendanceDay);
		
		// 時間消化休暇の集計
		this.timeConsumpDays.aggregate(workType, attendanceTimeOfDaily);
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(WorkDaysOfMonthly target){
		
		this.attendanceDays.sum(target.attendanceDays);
		this.absenceDays.sum(target.absenceDays);
		this.predetermineDays.sum(target.predetermineDays);
		this.workDays.sum(target.workDays);
		this.holidayDays.sum(target.holidayDays);
		this.specificDays.sum(target.specificDays);
		this.holidayWorkDays.sum(target.holidayWorkDays);
		this.straightDays.sum(target.straightDays);
		this.workTimes.sum(target.workTimes);
		this.twoTimesWorkTimes.sum(target.twoTimesWorkTimes);
		this.temporaryWorkTimes.sum(target.temporaryWorkTimes);
		this.leave.sum(target.leave);
		this.recruitmentDays.sum(target.recruitmentDays);
		this.specialVacationDays.sum(target.specialVacationDays);
		this.timeConsumpDays.sum(target.timeConsumpDays);
	}
	
	public static interface RequireM1 extends SpecificDaysOfMonthly.RequireM1, RecruitmentDaysOfMonthly.RequireM1,
		SpcVacationDaysOfMonthly.Require, AbsenceDaysOfMonthly.Require {

	}
}
