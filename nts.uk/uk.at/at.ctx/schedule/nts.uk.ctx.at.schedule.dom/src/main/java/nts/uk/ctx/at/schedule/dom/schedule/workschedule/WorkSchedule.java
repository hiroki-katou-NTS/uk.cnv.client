package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.gul.util.OptionalUtil;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TimezoneToUseHourlyHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.GettingTimeVacactionService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimeVacation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 勤務予定 root
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定
 * @author HieuLT
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class WorkSchedule implements DomainAggregate {

	/** 社員ID(employeeID) */
	private final String employeeID;

	/** 社員ID(年月日(YMD) */
	private final GeneralDate ymd;

	/** 確定区分 */
	private ConfirmedATR confirmedATR;

	/** 勤務情報 */
	private WorkInfoOfDailyAttendance workInfo;

	/** 所属情報 **/
	private AffiliationInforOfDailyAttd affInfo;
	
	/** 休憩時間帯**/
	@Getter
	private BreakTimeOfDailyAttd lstBreakTime;
	
	/** 編集状態 **/
	private List<EditStateOfDailyAttd> lstEditState;
	
	/** 作業予定 **/
	private TaskSchedule taskSchedule;

	/** 出退勤 */
	private Optional<TimeLeavingOfDailyAttd> optTimeLeaving;

	/** 勤怠時間 */
	private Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime;

	/** 短時間勤務 */
	private Optional<ShortTimeOfDailyAttd> optSortTimeWork;

	/** 外出時間帯 */
	private Optional<OutingTimeOfDailyAttd> outingTime;

	/**
	 * 作る
	 * @param require
	 * @param employeeId 社員ID
	 * @param date 年月日
	 * @param workinformation 勤務情報
	 * @return
	 */
	public static WorkSchedule create(
			Require require,
			String employeeId,
			GeneralDate date,
			WorkInformation workInformation
			){
		
		if (! workInformation.checkNormalCondition(require) ) {
			throw new BusinessException("Msg_2119");
		}
		
		Optional<TimeLeavingOfDailyAttd> optTimeLeaving = Optional.empty();
		if ( workInformation.isAttendanceRate(require) ) {
			optTimeLeaving = Optional.of(
					TimeLeavingOfDailyAttd.createByPredetermineZone(require, workInformation) );
		}
		
		val isBackStraight = workInformation.isBackStraight(require) ? NotUseAttribute.Use : NotUseAttribute.Not_use;
		val isGoStraight = workInformation.isGoStraight(require) ? NotUseAttribute.Use : NotUseAttribute.Not_use;
			
		return new WorkSchedule(
				employeeId, 
				date, 
				ConfirmedATR.UNSETTLED, 
				WorkInfoOfDailyAttendance.create(
						require, 
						workInformation, 
						CalculationState.No_Calculated, 
						isBackStraight, 
						isGoStraight, 
						DayOfWeek.convertFromCommonClass(date.dayOfWeekEnum())), 
				AffiliationInforOfDailyAttd.create(require, employeeId, date), 
				new BreakTimeOfDailyAttd(),
				new ArrayList<>(), 
				TaskSchedule.createWithEmptyList(),
				optTimeLeaving, 
				Optional.empty(), 
				Optional.empty(),
				Optional.empty() );
	}
	
	/**
	 * 勤務情報を指定して手修正で作る
	 * @param require
	 * @param employeeId 社員ID
	 * @param date 年月日
	 * @param workInformation 勤務情報
	 * @return
	 */
	public static WorkSchedule createByHandCorrectionWithWorkInformation(
			Require require,
			String employeeId,
			GeneralDate date,
			WorkInformation workInformation
			) {
		WorkSchedule workSchedule = WorkSchedule.create(require, employeeId, date, workInformation);
		
		List<EditStateOfDailyAttd> editStateOfDailyAttdList = 
				Arrays.asList(
					WS_AttendanceItem.WorkType,
					WS_AttendanceItem.WorkTime,
					WS_AttendanceItem.StartTime1,
					WS_AttendanceItem.EndTime1,
					WS_AttendanceItem.StartTime2,
					WS_AttendanceItem.EndTime2)
				.stream()
				.map( item -> EditStateOfDailyAttd.createByHandCorrection(require, item.ID, employeeId))
				.collect(Collectors.toList());
		
		workSchedule.setLstEditState(editStateOfDailyAttdList);
		
		return workSchedule;
	}
	
	/**
	 * 出退勤時刻の値を手修正で変更する
	 * 勤怠項目IDを指定して手修正で変更する
	 * @param require
	 * @param updateInfoMap 変更する情報Map
	 */
	public <T> void changeAttendanceItemValueByHandCorrection (
			Require require,
			Map<Integer, T > updateInfoMap) {
		
		updateInfoMap.forEach( (key, value) -> this.updateValueByHandCorrection(require, key, value));
	}
	
	/**
	 * 値の変更
	 * @param updateAttendanceItemID 勤怠項目ID
	 * @param value 値
	 * @return
	 */
	private <T> T updateValue(int updateAttendanceItemID, T value) {
		
		val updateAttendanceItem = WS_AttendanceItem.valueOf(updateAttendanceItemID);
		
		switch (updateAttendanceItem) {
			case StartTime1 :
				this.optTimeLeaving.get()
					.getAttendanceLeavingWork( 1 ).get()
					.getAttendanceStamp().get()
					.getStamp().get()
					.getTimeDay()
					.setTimeWithDay( Optional.of( (TimeWithDayAttr) value) );
				break;

			case EndTime1:
				this.optTimeLeaving.get()
					.getAttendanceLeavingWork( 1 ).get()
					.getLeaveStamp().get()
					.getStamp().get()
					.getTimeDay()
					.setTimeWithDay( Optional.of( (TimeWithDayAttr) value) );
				break;
			case StartTime2:
				this.optTimeLeaving.get()
					.getAttendanceLeavingWork( 2 ).get()
					.getAttendanceStamp().get()
					.getStamp().get()
					.getTimeDay()
					.setTimeWithDay( Optional.of( (TimeWithDayAttr) value) );
				break;

			case EndTime2:
				this.optTimeLeaving.get()
					.getAttendanceLeavingWork( 2 ).get()
					.getLeaveStamp().get()
					.getStamp().get()
					.getTimeDay()
					.setTimeWithDay( Optional.of( (TimeWithDayAttr) value) );
				break;
			case GoStraight:
				this.workInfo.setGoStraightAtr( (NotUseAttribute) value );
				break;
			case BackStraight:
				this.workInfo.setBackStraightAtr( (NotUseAttribute) value );
				break;
			default:
				break;
		}
		
		return value;
	}
	
	/**
	 * 勤怠項目IDに対応する値
	 * @param updateAttendanceItemID 勤怠項目ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getAttendanceItemValue(int updateAttendanceItemID) {
		
		val updateAttendanceItem = WS_AttendanceItem.valueOf(updateAttendanceItemID);
		
		switch (updateAttendanceItem) {
			case StartTime1:
				return (T) this.optTimeLeaving.get()
						.getAttendanceLeavingWork(1).get()
						.getAttendanceStamp().get()
						.getStamp().get()
						.getTimeDay().getTimeWithDay().get();
			case EndTime1:
				return (T) this.optTimeLeaving.get()
						.getAttendanceLeavingWork(1).get()
						.getLeaveStamp().get()
						.getStamp().get()
						.getTimeDay().getTimeWithDay().get();
			case StartTime2:
				return (T) this.optTimeLeaving.get()
						.getAttendanceLeavingWork(2).get()
						.getAttendanceStamp().get()
						.getStamp().get()
						.getTimeDay().getTimeWithDay().get();
			case EndTime2:
				return (T) this.optTimeLeaving.get()
						.getAttendanceLeavingWork(2).get()
						.getLeaveStamp().get()
						.getStamp().get()
						.getTimeDay().getTimeWithDay().get();
			case GoStraight:
				return (T) this.workInfo.getGoStraightAtr();
			case BackStraight:
				return (T) this.workInfo.getBackStraightAtr();
			default:
				return null;
		}

	}
	/**
	 * 値を手修正で変更
	 * @param require
	 * @param updateAttendanceItemID 
	 * @param value Optional<T> 値
	 * @return
	 * 
	 * note: 勤怠項目が勤務種類と就業時間帯を含まない this attendance item doesn't accept work-type or work-time
	 */
	private <T> T updateValueByHandCorrection(Require require, int updateAttendanceItemID, T value) {
		
		T existValue = this.getAttendanceItemValue(updateAttendanceItemID);
		
		if ( existValue.equals(value) ) {
			return existValue;
		}
		
		lstEditState.removeIf( editState -> editState.getAttendanceItemId() == updateAttendanceItemID);
		lstEditState.add(EditStateOfDailyAttd.createByHandCorrection(require, updateAttendanceItemID, employeeID));
		
		return this.updateValue(updateAttendanceItemID, value);
		
	} 
	
	/**
	 * 確定する
	 */
	public void confirm() {
		this.confirmedATR = ConfirmedATR.CONFIRMED;
	}
	
	/**
	 * 確定を解除する
	 */
	public void removeConfirm() {
		this.confirmedATR = ConfirmedATR.UNSETTLED;
	}
	
	/**
	 * 手修正を解除する
	 */
	public void removeHandCorrections() {
		this.lstEditState.removeIf( editState -> editState.isHandCorrect() );
	}

	/**
	 * 時間休暇を取得する
	 * @return
	 */
	public Map<TimezoneToUseHourlyHoliday, TimeVacation> getTimeVacation() {
		return GettingTimeVacactionService.get(this.optTimeLeaving, this.optAttendanceTime,	this.outingTime);
	}

	/**
	 * 休憩時間帯を手修正する
	 * @param require
	 * @param newBreakTimeList 時間帯リスト
	 */
	public void handCorrectBreakTimeList(Require require, List<TimeSpanForCalc> newBreakTimeList) {
		
		List<TimeSpanForCalc> sortedBreakTimeList = newBreakTimeList.stream()
				.sorted(Comparator.comparingInt(TimeSpanForCalc::start))
				.collect(Collectors.toList());;
				
		List<BreakTimeSheet> newBreakTimeSheets = 
			IntStream.range(0, sortedBreakTimeList.size()).boxed()
				.map( index ->
						new BreakTimeSheet(
							new BreakFrameNo(index + 1),
							sortedBreakTimeList.get(index).getStart(),
							sortedBreakTimeList.get(index).getEnd() ))
				.collect(Collectors.toList());
		
		// update value of BreakTime
		this.lstBreakTime = new BreakTimeOfDailyAttd(newBreakTimeSheets);
		
		// update EditState of BreakTime(1...size)
		this.lstEditState.removeIf( editState -> WS_AttendanceItem.isBreakTime( editState.getAttendanceItemId() ) );
		
		List<WS_AttendanceItem> updatedAttendanceItemList;
		if ( newBreakTimeList.isEmpty() ) {
			updatedAttendanceItemList = new ArrayList<>(Arrays.asList( 
					WS_AttendanceItem.StartBreakTime1, 
					WS_AttendanceItem.EndBreakTime1) );
		} else {
			updatedAttendanceItemList = WS_AttendanceItem.getBreakTimeItemWithSize( newBreakTimeList.size() );
		}
		updatedAttendanceItemList.forEach( item -> this.lstEditState.add(
				EditStateOfDailyAttd.createByHandCorrection(require, item.ID, this.employeeID)));
	}
	
	/**
	 * 日別勤怠Workに変換する
	 * @return
	 */
	public IntegrationOfDaily convertToIntegrationOfDaily() {
		return new IntegrationOfDaily(
				  this.employeeID
				, this.ymd
				, this.workInfo
				, CalAttrOfDailyAttd.createAllCalculate()
				, this.affInfo
				, Optional.empty()
				, Collections.emptyList()
				, this.outingTime
				, this.lstBreakTime
				, this.optAttendanceTime
				, this.optTimeLeaving
				, this.optSortTimeWork
				, Optional.empty()
				, Optional.empty()
				, Optional.empty()
				, this.lstEditState
				, Optional.empty()
				, Collections.emptyList()
				, Collections.emptyList()
				, Collections.emptyList()
				, Optional.empty());
	}
	
	/**
	 * 作業予定を入れ替える
	 * @param require
	 * @param newtaskSchedule 作業予定
	 */
	public void updateTaskSchedule(Require require, TaskSchedule newtaskSchedule ) {
		
		this.checkWhetherTaskScheduleIsCorrect(require, newtaskSchedule);
		
		this.taskSchedule = newtaskSchedule;
	}
	
	/**
	 * 一日中に作業予定を作成する
	 * @param require
	 * @param taskCode 作業コード
	 */
	public void createTaskScheduleForWholeDay(Require require, TaskCode taskCode) {
		
		List<TimeSpanForCalc> workingTimeSpanList = this.getWorkingTimeSpan(require);
		if ( workingTimeSpanList.isEmpty() ) {
			throw new BusinessException("Msg_2103");
		}
		
		List<TaskScheduleDetail> taskScheduleDetails = workingTimeSpanList.stream()
				.map( timeSpan -> new TaskScheduleDetail(taskCode, timeSpan))
				.collect(Collectors.toList());
		
		this.taskSchedule = TaskSchedule.create( taskScheduleDetails );
	}
	
	/**
	 * 時間帯に作業予定を追加する
	 * @param require
	 * @param targetTimeSpan 対象時間帯
	 * @param taskCode 作業コード
	 */
	public void addTaskScheduleWithTimeSpan(Require require, TimeSpanForCalc targetTimeSpan, TaskCode taskCode) {
		
		List<TimeSpanForCalc> workingTimeSpanList = this.getWorkingTimeSpan(require);
		if ( workingTimeSpanList.isEmpty() ) {
			throw new BusinessException("Msg_2103");
		}
		
		List<TaskScheduleDetail> addingDetails = workingTimeSpanList.stream()
				.map( workingTimeSpan -> workingTimeSpan.getDuplicatedWith(targetTimeSpan))
				.flatMap(OptionalUtil::stream)
				.map( timeSpan -> new TaskScheduleDetail(taskCode, timeSpan) )
				.collect( Collectors.toList() );
		
		addingDetails.forEach( detail -> {
			this.taskSchedule = this.taskSchedule.addTaskScheduleDetail( detail );
		});
	}
	
	/**
	 * 労働時間帯リストを取得する
	 * @param require
	 * @return
	 */
	private List<TimeSpanForCalc> getWorkingTimeSpan(Require require) {
		
		if( !this.workInfo.isAttendanceRate(require) ) {
			return new ArrayList<>();
		}
		
		val timeLeavingSpanList = this.optTimeLeaving.get().getTimeOfTimeLeavingAtt();
		return timeLeavingSpanList.stream()
			.map( timeLeavingSpan -> this.getTimeSpansWhichNotDuplicatedWithTheNotWorkingTimeSpan(require, timeLeavingSpan) )
			.flatMap( x -> x.stream())
			.collect(Collectors.toList());
	}
	
	/**
	 * 対象時間帯の労働しない時間帯に重複していない部分を取得する
	 * @param require
	 * @param targetTimeSpan 対象時間帯
	 * @return
	 */
	private List<TimeSpanForCalc> getTimeSpansWhichNotDuplicatedWithTheNotWorkingTimeSpan(
			Require require, 
			TimeSpanForCalc targetTimeSpan) {
		List<TimeSpanForCalc> notWorkingTimeSpanList = new ArrayList<>();
		
		// break time list 休憩時間帯
		List<TimeSpanForCalc> breakTimeList = this.lstBreakTime.getBreakTimeSheets().stream()
				.map( sheet -> sheet.convertToTimeSpanForCalc())
				.collect(Collectors.toList());
		notWorkingTimeSpanList.addAll(breakTimeList);
		
		// short time list 短時間勤務時間帯
		if ( this.optSortTimeWork.isPresent() ){
			List<TimeSpanForCalc> shortTimeList = this.optSortTimeWork.get().getShortWorkingTimeSheets().stream()
							.map( sheet -> sheet.convertToTimeSpanForCalc())
							.collect(Collectors.toList());
			notWorkingTimeSpanList.addAll( shortTimeList );
		}
		
		// time vacation 時間休暇
		List<TimeSpanForCalc> timeVacationSpanList = this.getTimeVacation().values().stream()
				.map( timeVacation -> timeVacation.getTimeList())
				.flatMap( x -> x.stream() )
				.collect( Collectors.toList() );
		notWorkingTimeSpanList.addAll( timeVacationSpanList );
		
		return targetTimeSpan.subtract(notWorkingTimeSpanList);
	}
	
	/**
	 * 作業予定が妥当かどうかチェックする
	 * @param require
	 * @param targetTaskSchedule 作業予定
	 * @return
	 */
	private boolean checkWhetherTaskScheduleIsCorrect(Require require, TaskSchedule targetTaskSchedule) {
		
		if( !this.workInfo.isAttendanceRate(require) ) {
			throw new BusinessException( "Msg_2103" );
		}
		
		List<TimeSpanForCalc> timeVacationList = this.getTimeVacation().values().stream()
								.map( value -> value.getTimeList())
								.flatMap( x -> x.stream())
								.collect(Collectors.toList());
		
		targetTaskSchedule.getDetails().stream()
			.map( detail -> detail.getTimeSpan())
			.forEach( taskTimeSpan -> {
				
				if ( !this.optTimeLeaving.get().isIncludeInWorkTimeSpan(taskTimeSpan) ) {
					// if this.workInfo is attendance rate, this.optTimeLeaving will always be present.
					throw new BusinessException("Msg_2098");
				}
				
				if ( this.lstBreakTime.isDuplicatedWithBreakTime(taskTimeSpan) ) {
					throw new BusinessException("Msg_2099");
				}
				
				if ( this.optSortTimeWork.isPresent() && this.optSortTimeWork.get().isDuplicatedWithShortTime(taskTimeSpan) ) {
					throw new BusinessException("Msg_2100");
				}
				
				boolean isDuplicateWithTimeVacation = timeVacationList.stream()
						.anyMatch( timeVacation -> timeVacation.checkDuplication(taskTimeSpan).isDuplicated() );
				if ( isDuplicateWithTimeVacation ){
					throw new BusinessException("Msg_2101");
				}
				
			});
		
		return true;
	}
	
	public static interface Require extends 
		WorkInfoOfDailyAttendance.Require, 
		AffiliationInforOfDailyAttd.Require,
		TimeLeavingOfDailyAttd.Require, 
		EditStateOfDailyAttd.Require {
	
	}

}
