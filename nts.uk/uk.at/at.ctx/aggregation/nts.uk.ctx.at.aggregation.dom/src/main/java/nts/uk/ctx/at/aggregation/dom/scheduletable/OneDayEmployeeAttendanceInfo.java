package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Value;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 一日分の社員の勤怠情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.一日分の社員の勤怠情報
 * @author dan_pv
 * @param <T>
 */
@Value
public class OneDayEmployeeAttendanceInfo<T> {
	
	/**
	 * 社員ID
	 */
	private final String employeeId;
	
	/**
	 * 年月日
	 */
	private final GeneralDate date;
	
	/**
	 * 勤怠データMap
	 */
	private final Map<ScheduleTableAttendanceItem, T> attendanceItemInfoMap;
	
	/**
	 * 作る
	 * @param dailyData 日別勤怠
	 * @return
	 */
	public static <T> OneDayEmployeeAttendanceInfo<T> create(IntegrationOfDaily dailyData) {
		
		Map<ScheduleTableAttendanceItem, T> attendanceItemInfoMap = new HashMap<>();
		
		attendanceItemInfoMap.putAll( getCodeMap(dailyData) );
		attendanceItemInfoMap.putAll( getTimePointMap(dailyData) );
		attendanceItemInfoMap.putAll( getTimeMap(dailyData) );
		attendanceItemInfoMap.putAll( getLaborCostTimeMap(dailyData) );
		
		return new OneDayEmployeeAttendanceInfo<>(dailyData.getEmployeeId(), dailyData.getYmd(), attendanceItemInfoMap);
	}
	
	/**
	 * シフトマスタを取得する
	 * @param require
	 * @return Optional<シフトマスタ>
	 */
	public Optional<ShiftMaster> getShiftMaster(Require require) {
		
		// 勤務種類コード
		String workTypeCode = (String) this.attendanceItemInfoMap.get(ScheduleTableAttendanceItem.WORK_TYPE);
		
		// 就業時間帯コード
		Optional<String> workTimeCode = this.attendanceItemInfoMap.containsKey(ScheduleTableAttendanceItem.WORK_TIME) ?
											Optional.of( (String) this.attendanceItemInfoMap.get(ScheduleTableAttendanceItem.WORK_TIME)) :
												Optional.empty();
		
		return require.getShiftMaster(workTypeCode, workTimeCode);
	}
	
	/**
	 * 勤務種類を取得する
	 * @param require
	 * @return Optional<勤務種類>
	 */
	public Optional<WorkType> getWorkType(Require require) {
		
		return require.getWorkType( 
				(String) this.attendanceItemInfoMap.get(ScheduleTableAttendanceItem.WORK_TYPE));
	}
	
	/**
	 * 就業時間帯を取得する
	 * @param require
	 * @return Optional<就業時間帯の設定>
	 */
	public Optional<WorkTimeSetting> getWorkTime(Require require) {
		
		if ( !this.attendanceItemInfoMap.containsKey(ScheduleTableAttendanceItem.WORK_TIME) ) {
			return Optional.empty();
		}
		
		return require.getWorkTimeSetting( 
				(String) this.attendanceItemInfoMap.get(ScheduleTableAttendanceItem.WORK_TIME));
	}
	
	/**
	 * コードMapを取得する
	 * @param dailyData 日別勤怠
	 * @return Map<スケジュール表の勤怠項目, T>
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<ScheduleTableAttendanceItem, T> getCodeMap(IntegrationOfDaily dailyData) {
		
		Map<ScheduleTableAttendanceItem, T> codeMap = new HashMap<>();
		
		// 勤務種類コード
		codeMap.put(ScheduleTableAttendanceItem.WORK_TYPE, 
				(T) dailyData.getWorkInformation().getRecordInfo().getWorkTypeCode().v());
		
		// 就業時間帯コード
		if ( dailyData.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().isPresent() ) {
			
			codeMap.put(ScheduleTableAttendanceItem.WORK_TIME, 
					(T) dailyData.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().get().v() );
		}
		
		return codeMap;
	}
	
	/**
	 * 勤務時刻Mapを取得する
	 * @param dailyData 日別勤怠
	 * @return Map<スケジュール表の勤怠項目, T>
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<ScheduleTableAttendanceItem, T> getTimePointMap(IntegrationOfDaily dailyData) { 
		
		Map<ScheduleTableAttendanceItem, T> timePointMap = new HashMap<>();
		
		if ( dailyData.getAttendanceLeave().isPresent() ){
			
			val timeLeavingAttList = dailyData.getAttendanceLeave().get().getTimeOfTimeLeavingAtt();

			if ( timeLeavingAttList.size() > 0) {
				timePointMap.put(ScheduleTableAttendanceItem.START_TIME, (T) timeLeavingAttList.get(0).getStart() );
				timePointMap.put(ScheduleTableAttendanceItem.END_TIME, (T) timeLeavingAttList.get(0).getEnd() );
			}

			if ( timeLeavingAttList.size() > 1) {
				timePointMap.put(ScheduleTableAttendanceItem.START_TIME_2, (T) timeLeavingAttList.get(1).getStart() );
				timePointMap.put(ScheduleTableAttendanceItem.END_TIME_2, (T) timeLeavingAttList.get(1).getEnd() );
			}
		}
		
		return timePointMap;
	}
	
	/**
	 * 勤務時間Mapを取得する
	 * @param dailyData 日別勤怠
	 * @return Map<スケジュール表の勤怠項目, T>
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<ScheduleTableAttendanceItem, T> getTimeMap(IntegrationOfDaily dailyData) {
		
		Map<ScheduleTableAttendanceItem, T> timeMap = new HashMap<>();
		
		if ( dailyData.getAttendanceTimeOfDailyPerformance().isPresent() ) {
			
			// 総労働時間
			timeMap.put(ScheduleTableAttendanceItem.TOTAL_WORKING_HOURS, 
					(T) dailyData.getAttendanceTimeOfDailyPerformance().get()
							.getActualWorkingTimeOfDaily()
							.getTotalWorkingTime()
							.getTotalTime() );
			
			
			// 就業時間
			timeMap.put(ScheduleTableAttendanceItem.WORKING_HOURS, 
					(T) dailyData.getAttendanceTimeOfDailyPerformance().get()
							.getActualWorkingTimeOfDaily()
							.getTotalWorkingTime()
							.getWithinStatutoryTimeOfDaily()
							.getWorkTime() );
			
			// 実働時間
			timeMap.put(ScheduleTableAttendanceItem.ACTUAL_WORKING_HOURS, 
					(T) dailyData.getAttendanceTimeOfDailyPerformance().get()
							.getActualWorkingTimeOfDaily()
							.getTotalWorkingTime()
							.getActualTime() );
			
		}
		
		return timeMap;
	}
	
	/**
	 * 人件費時間Mapを取得する
	 * @param dailyData 日別勤怠
	 * @return Map<スケジュール表の勤怠項目, T>
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<ScheduleTableAttendanceItem, T> getLaborCostTimeMap(IntegrationOfDaily dailyData) {
	
		Map<ScheduleTableAttendanceItem, T> laborCostTimeMap = new HashMap<>();
		
		if ( dailyData.getAttendanceTimeOfDailyPerformance().isPresent() ) {
			
			// 日別勤怠の割増時間
			val preniumTimeOfDailyPerformance = 
					dailyData.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily()
					.getPremiumTimeOfDailyPerformance();
			
			ScheduleTableAttendanceItem.getLaborCostTimeTypes().forEach( (laborItem, laborCostItemNumber) -> {
				
				Optional<PremiumTime> preniumTime = preniumTimeOfDailyPerformance.getPremiumTime(laborCostItemNumber);
				
				if ( preniumTime.isPresent() ) {
					laborCostTimeMap.put(laborItem, (T) preniumTime.get().getPremitumTime() );
				}
				
			});
		}
		
		return laborCostTimeMap;
	}
	
	public static interface Require {
		
		/**
		 * シフトマスタを取得する
		 * @param workTypeCode 勤務種類コード
		 * @param workTimeCode 就業時間帯コード
		 * @return
		 */
		public Optional<ShiftMaster> getShiftMaster(String workTypeCode, Optional<String> workTimeCode);
		
		/**
		 * 勤務種類を取得する
		 * @param workTypeCode 勤務種類コード
		 * @return
		 */
		public Optional<WorkType> getWorkType(String workTypeCode);
		
		/**
		 * 就業時間帯の設定を取得する
		 * @param workTimeCode 就業時間帯コード
		 * @return
		 */
		public Optional<WorkTimeSetting> getWorkTimeSetting(String workTimeCode);
	}

}
