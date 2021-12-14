package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.workdays;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.WorkTypeDaysCountTable;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.AbsenceUseTimeCalc;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 月別実績の欠勤日数
 * @author shuichi_ishida
 */
@Getter
public class AbsenceDaysOfMonthly implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 欠勤合計日数 */
	private AttendanceDaysMonth totalAbsenceDays;
	/** 欠勤合計時間 */
	private AttendanceTimeMonth totalAbsenceTime;
	/** 欠勤日数 */
	private Map<Integer, AggregateAbsenceDays> absenceDaysList;
	
	/**
	 * コンストラクタ
	 */
	public AbsenceDaysOfMonthly(){
		
		this.totalAbsenceDays = new AttendanceDaysMonth(0.0);
		this.totalAbsenceTime = new AttendanceTimeMonth(0);
		this.absenceDaysList = new HashMap<>();
	}
	
	/**
	 * ファクトリー
	 * @param totalAbsenceDays 欠勤合計日数
	 * @param totalAbsenceTime 欠勤合計時間
	 * @param absenceDaysList 欠勤日数
	 * @return 月別実績の欠勤日数
	 */
	public static AbsenceDaysOfMonthly of(
			AttendanceDaysMonth totalAbsenceDays,
			AttendanceTimeMonth totalAbsenceTime,
			List<AggregateAbsenceDays> absenceDaysList){
		
		val domain = new AbsenceDaysOfMonthly();
		domain.totalAbsenceDays = totalAbsenceDays;
		domain.totalAbsenceTime = totalAbsenceTime;
		for (val absenceDays : absenceDaysList){
			val absenceFrameNo = Integer.valueOf(absenceDays.getAbsenceFrameNo());
			domain.absenceDaysList.putIfAbsent(absenceFrameNo, absenceDays);
		}
		return domain;
	}
	
	/**
	 * 集計
	 * 06_欠勤日数
	 * @param workingSystem 労働制
	 * @param workType 勤務種類
	 * @param workTypeDaysCountTable 勤務種類の日数カウント表
	 * @param isAttendanceDay 出勤しているかどうか
	 */
	public void aggregate(Require require, String cid, String sid, GeneralDate ymd, WorkInformation workInfo, 
			WorkingSystem workingSystem, WorkType workType, WorkTypeDaysCountTable workTypeDaysCountTable, boolean isAttendanceDay){

		if (workType == null) return;
		if (workTypeDaysCountTable == null) return;
		
		/** ○欠勤枠日数の集計 */
		aggrAbsenceDays(require, cid, sid, ymd, workInfo, workingSystem, workType, workTypeDaysCountTable, isAttendanceDay);
		
		/** ○欠勤合計日数の集計 */
		val days = this.absenceDaysList.entrySet().stream().mapToDouble(c -> c.getValue().getDays().v()).sum();
		this.totalAbsenceDays = new AttendanceDaysMonth(days);
		val time = this.absenceDaysList.entrySet().stream().mapToInt(c -> c.getValue().getTime().v()).sum();
		this.totalAbsenceTime = new AttendanceTimeMonth(time);
	}
	
	/** 欠勤枠日数の集計 */
	private void aggrAbsenceDays(Require require, String cid, String sid, GeneralDate ymd, WorkInformation workInfo,
			WorkingSystem workingSystem, WorkType workType, WorkTypeDaysCountTable workTypeDaysCountTable, boolean isAttendanceDay) {
		
		workTypeDaysCountTable.getAbsenceDaysMap().entrySet().stream().forEach(c -> {
			int absenceNo = c.getValue().getAbsenceFrameNo(); 
			
			this.absenceDaysList.putIfAbsent(absenceNo, new AggregateAbsenceDays(absenceNo));
			val targetAbsenceDays = this.absenceDaysList.get(absenceNo);
			
			val absenceDays = c.getValue().getDays().v();
			
			/** ○パラメータ「労働制」を取得 */
			if (workingSystem != WorkingSystem.EXCLUDED_WORKING_CALCULATE) 
				/** ○1日半日出勤・1日休日系の判定 */
				if (workType.getAttendanceHolidayAttr().isAfternoon() || workType.getAttendanceHolidayAttr().isMorning()) 
					/** ○出勤状態を判断する */
					if (!isAttendanceDay) 
						return;

			/** ○「欠勤日数」に加算 */
			targetAbsenceDays.addDays(absenceDays);
			
			/** 枠時間の集計 */
			targetAbsenceDays.addTime(aggrTime(require, cid, sid, ymd, absenceDays, workInfo));
		});
	}
	
	/** 枠時間の集計 */
	private int aggrTime(Require require, String cid, String sid, GeneralDate ymd, Double absenceDays, WorkInformation workInfo) {
		
		/** ○INPUT．発生日数を確認する */
		if (absenceDays <= 0) {
			/** ○時間←0：00 */
			return 0;
		}
		
		/** 時間←日別実績の特別休暇.使用時間 */
		return AbsenceUseTimeCalc.calc(require, cid, sid, ymd, workInfo).valueAsMinutes();
	}
	
	public static interface Require extends AbsenceUseTimeCalc.Require { }
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(AbsenceDaysOfMonthly target){
		
		this.totalAbsenceDays = this.totalAbsenceDays.addDays(target.totalAbsenceDays.v());
		this.totalAbsenceTime = this.totalAbsenceTime.addMinutes(target.totalAbsenceTime.v());
		for (val absenceDays : this.absenceDaysList.values()){
			val frameNo = absenceDays.getAbsenceFrameNo();
			if (target.absenceDaysList.containsKey(frameNo)){
				absenceDays.addDays(target.absenceDaysList.get(frameNo).getDays().v());
				absenceDays.addTime(target.absenceDaysList.get(frameNo).getTime().v());
			}
		}
		for (val targetAbsenceDays : target.absenceDaysList.values()){
			val frameNo = targetAbsenceDays.getAbsenceFrameNo();
			this.absenceDaysList.putIfAbsent(frameNo, targetAbsenceDays);
		}
	}
}
