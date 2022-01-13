package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.WorkTypeDaysCountTable;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.SpcVacationUseTimeCalc;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 月別実績の特別休暇日数
 * @author shuichi_ishida
 */
@Getter
public class SpcVacationDaysOfMonthly implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 特別休暇合計日数 */
	private AttendanceDaysMonth totalSpcVacationDays;
	/** 特別休暇合計時間 */
	private AttendanceTimeMonth totalSpcVacationTime;
	/** 特別休暇日数 */
	private Map<Integer, AggregateSpcVacationDays> spcVacationDaysList;
	
	/**
	 * コンストラクタ
	 */
	public SpcVacationDaysOfMonthly(){
		
		this.totalSpcVacationDays = new AttendanceDaysMonth(0.0);
		this.totalSpcVacationTime = new AttendanceTimeMonth(0);
		this.spcVacationDaysList = new HashMap<>();
	}
	
	/**
	 * ファクトリー
	 * @param totalSpcVacationDays 特別休暇合計日数
	 * @param totalSpcVacationTime 特別休暇合計時間
	 * @param spcVacationDaysList 特別休暇日数
	 * @return 月別実績の特別休暇日数
	 */
	public static SpcVacationDaysOfMonthly of(
			AttendanceDaysMonth totalSpcVacationDays,
			AttendanceTimeMonth totalSpcVacationTime,
			List<AggregateSpcVacationDays> spcVacationDaysList){
		
		SpcVacationDaysOfMonthly domain = new SpcVacationDaysOfMonthly();
		domain.totalSpcVacationDays = totalSpcVacationDays;
		domain.totalSpcVacationTime = totalSpcVacationTime;
		for (val spcVacationDays : spcVacationDaysList){
			val spcVacationFrameNo = Integer.valueOf(spcVacationDays.getSpcVacationFrameNo());
			domain.spcVacationDaysList.putIfAbsent(spcVacationFrameNo, spcVacationDays);
		}
		return domain;
	}
	
	/**
	 * 集計
	 * 特別休暇日数
	 * @param workingSystem 労働制
	 * @param workType 勤務種類
	 * @param workTypeDaysCountTable 勤務種類の日数カウント表
	 * @param isAttendanceDay 出勤しているかどうか
	 * @param predetermineTimeSet 所定時間設定
	 * @param predTimeSetOnWeekday 所定時間設定（平日時）
	 */
	public void aggregate(Require require, String cid, String sid, GeneralDate ymd, WorkingSystem workingSystem,
			WorkType workType, WorkInformation workInfo, Optional<AttendanceTimeOfDailyAttendance> attendanceDaily,
			WorkTypeDaysCountTable workTypeDaysCountTable, boolean isAttendanceDay){

		if (workType == null) return;
		if (workTypeDaysCountTable == null) return;
		
		for (val aggrSpcVacationDays : workTypeDaysCountTable.getSpcVacationDaysMap().values()){
			
			/** ○特別休暇枠日数の集計 */
			aggrateSpcVacation(require, cid, sid, ymd, workingSystem, aggrSpcVacationDays.getSpcVacationFrameNo(), 
					workType, workInfo, attendanceDaily, workTypeDaysCountTable, isAttendanceDay, aggrSpcVacationDays);
		}
		
		/** ○特別休暇合計日数の集計 */
		val days = this.spcVacationDaysList.entrySet().stream().mapToDouble(c -> c.getValue().getDays().v()).sum();
		this.totalSpcVacationDays = new AttendanceDaysMonth(days);
		val time = this.spcVacationDaysList.entrySet().stream().mapToInt(c -> c.getValue().getTime().v()).sum();
		this.totalSpcVacationTime = new AttendanceTimeMonth(time);
	}
	
	/** 特別休暇枠日数の集計 */
	private void aggrateSpcVacation(Require require, String cid, String sid, GeneralDate ymd, WorkingSystem workingSystem,
			int spcNo, WorkType workType, WorkInformation workInfo, Optional<AttendanceTimeOfDailyAttendance> attendanceDaily,
			WorkTypeDaysCountTable workTypeDaysCountTable, boolean isAttendanceDay, AggregateSpcVacationDays aggrSpcVacationDays) {
		
		this.spcVacationDaysList.putIfAbsent(spcNo, new AggregateSpcVacationDays(spcNo));
		val targetSpcVacationDays = this.spcVacationDaysList.get(spcNo);
		val spcDays = aggrSpcVacationDays.getDays().v();
				
		/** ○パラメータ「労働制」を取得 */
		if (workingSystem != WorkingSystem.EXCLUDED_WORKING_CALCULATE) 
			/** ○1日半日出勤・1日休日系の判定 */
			if (workType.getAttendanceHolidayAttr().isMorning() ||  workType.getAttendanceHolidayAttr().isAfternoon()) 
				/** ○出勤状態を判断する */
				if (!isAttendanceDay) 
					return;

		/** ○「特別休暇日数」に加算 */
		targetSpcVacationDays.addDays(spcDays);
		
		/** ○枠時間の集計 */
		targetSpcVacationDays.addTime(aggrTime(require, cid, sid, ymd, spcDays, workInfo, spcNo, attendanceDaily));
	}
	
	/** 枠時間の集計 */
	private int aggrTime(Require require, String cid, String sid, GeneralDate ymd, Double spcDays,
			WorkInformation workInfo, int spcNo, Optional<AttendanceTimeOfDailyAttendance> attendanceDaily) {
		
		/** ○INPUT．発生日数を確認する */
		if (spcDays <= 0) {
			/** ○時間←0：00 */
			return 0;
		}
		
		/** 時間←日別実績の特別休暇.使用時間 */
		return SpcVacationUseTimeCalc.calc(require, cid, sid, ymd, workInfo, spcNo, attendanceDaily).valueAsMinutes();
	}
	
	public static interface Require extends SpcVacationUseTimeCalc.Require {
		
	}
	
	/** 特別休暇合計日数の再集計 */
	public void recalcTotal() {
		
		this.totalSpcVacationDays = new AttendanceDaysMonth(0d);
		this.totalSpcVacationTime = new AttendanceTimeMonth(0);
		
		/** ○特別休暇日数を取得 */
		for (val spe : this.spcVacationDaysList.entrySet()) {
			
			/** ○日数、時間を累計 */
			this.totalSpcVacationDays = this.totalSpcVacationDays.addDays(spe.getValue().getDays().v());
			this.totalSpcVacationTime = this.totalSpcVacationTime.addMinutes(spe.getValue().getTime().valueAsMinutes());
		}
	}
	
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(SpcVacationDaysOfMonthly target){
		
		this.totalSpcVacationDays = this.totalSpcVacationDays.addDays(target.totalSpcVacationDays.v());
		this.totalSpcVacationTime = this.totalSpcVacationTime.addMinutes(target.totalSpcVacationTime.v());
		for (val spcVacationDays : this.spcVacationDaysList.values()){
			val frameNo = spcVacationDays.getSpcVacationFrameNo();
			if (target.spcVacationDaysList.containsKey(frameNo)){
				spcVacationDays.addDays(target.spcVacationDaysList.get(frameNo).getDays().v());
				spcVacationDays.addTime(target.spcVacationDaysList.get(frameNo).getTime().v());
			}
		}
		for (val targetSpcVacationDays : target.spcVacationDaysList.values()){
			val frameNo = targetSpcVacationDays.getSpcVacationFrameNo();
			this.spcVacationDaysList.putIfAbsent(frameNo, targetSpcVacationDays);
		}
	}
}
