package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.workingcondition.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetermineTime;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.time.TimeWithDayAttr;


/**
 * 所定時間設定(計算用クラス)
 * @author ken_takasu
 *
 */
@Getter
public class PredetermineTimeSetForCalc implements Cloneable{
	
	//時間帯(使用区分付き)
	private final List<TimezoneUse> timeSheets;
	
	//午前終了時刻
	private final TimeWithDayAttr AMEndTime;

	//午後開始時刻
	private final TimeWithDayAttr PMStartTime;
	
	//所定時間
	private PredetermineTime additionSet;
	
	//1日の計算範囲
	private AttendanceTime oneDayRange;
	
	//日付開始時間
	private TimeWithDayAttr startOneDayTime;
	
	//残業を含めた所定時間を設定する
	private boolean isIncludeOverTimePred = false;
	
	//夜勤区分
	private boolean nightWorkAtr = false;

	/**
	 * 所定時間帯の時間を更新する
	 * @param dateStartTime
	 * @param rangeTimeDay
	 * @param predetermineTimeSheet
	 * @param additionSet
	 * @param timeSheets
	 */
	public PredetermineTimeSetForCalc(
			List<TimezoneUse> timeSheets,
			TimeWithDayAttr AMEndTime,
			TimeWithDayAttr PMStartTime,
			PredetermineTime addtionSet,
            AttendanceTime oneDayRange,
			TimeWithDayAttr startOneDayTime) {
		this.timeSheets = timeSheets.stream().sorted((c1, c2) -> Integer.compare(c1.getWorkNo(), c2.getWorkNo())).collect(Collectors.toList());
		this.AMEndTime = AMEndTime;
		this.PMStartTime = PMStartTime;
		this.additionSet = addtionSet;
		this.oneDayRange = oneDayRange;
		this.startOneDayTime = startOneDayTime;
	}
	
	/**
	 * Aggregateの所定時間から計算用所定時間クラスへの変換
	 */
	public static PredetermineTimeSetForCalc convertFromAggregatePremiumTime(PredetemineTimeSetting predetermineTimeSet, WorkType workType) {
		
		List<TimezoneUse> timeZones;
		switch (workType.getDailyWork().decisionNeedPredTime()) {
		case FULL_TIME:
			timeZones = predetermineTimeSet.getTimezoneByAmPmAtr(AmPmAtr.ONE_DAY);
			break;
		case MORNING:
			timeZones = predetermineTimeSet.getTimezoneByAmPmAtr(AmPmAtr.AM);
			break;
		case AFTERNOON:
			timeZones = predetermineTimeSet.getTimezoneByAmPmAtr(AmPmAtr.PM);
			break;
		default:
			timeZones = new ArrayList<>();
			break;
		}
		
		
		/** TODO: 三浦さんの処理待ち*/
		return new PredetermineTimeSetForCalc(timeZones
											  ,predetermineTimeSet.getPrescribedTimezoneSetting().getMorningEndTime()
											  ,predetermineTimeSet.getPrescribedTimezoneSetting().getAfternoonStartTime()
											  ,predetermineTimeSet.getPredTime()
											  ,predetermineTimeSet.getRangeTimeDay()
											  ,predetermineTimeSet.getStartDateClock());
	}
	
	/**
	 * 所定終了時間を所定開始時間と同じ時刻に変更する
	 */
	public void endTimeSetStartTime() {
		val copyTimeSheet = new ArrayList<>(this.getTimeSheets());
		this.timeSheets.clear();
		for(TimezoneUse timeSheet : copyTimeSheet) {
			this.timeSheets.add(new TimezoneUse(timeSheet.getStart(),
												timeSheet.getStart(),
												timeSheet.getUseAtr(),
												timeSheet.getWorkNo()));
		}
	}
	
	/**
	 * 勤務の単位を基に時間帯の開始、終了を補正
	 * @param dailyWork 1日の勤務
	 */
	public void correctPredetermineTimeSheet(DailyWork dailyWork,int workNo) {
		
		if (dailyWork.getAttendanceHolidayAttr().isHalfDayWorking()) {
			val workingTimeSheet = this.getHalfDayWorkingTimeSheetOf(dailyWork.getAttendanceHolidayAttr(),workNo);
			correctTimeSheet(workingTimeSheet.getStart(), workingTimeSheet.getEnd());
		}
	}

	
	public void correctTimeSheet(TimeWithDayAttr start, TimeWithDayAttr end) {
		  val corrected = extractBetween(start, end);
		  this.timeSheets.clear();
		  this.timeSheets.addAll(corrected);
	 }

	
	private List<TimezoneUse> extractBetween(TimeWithDayAttr start, TimeWithDayAttr end) {
		val targetSpan = new TimeSpanForDailyCalc(start, end);
		List<TimezoneUse> result = new ArrayList<>();
		
		this.timeSheets.stream().forEach(source -> {
			source.timeSpan().getDuplicatedWith(targetSpan.getTimeSpan()).ifPresent(duplicated -> {
				source.updateStartTime(duplicated.getStart());
				source.updateEndTime(duplicated.getEnd());
				result.add(source);
			});
		});
		return result;
	}
	
	/**
	 * 午前出勤、午後出勤の判定
	 * @param attr
	 * @return
	 */
	private TimeSpanForDailyCalc getHalfDayWorkingTimeSheetOf(AttendanceHolidayAttr attr,int workNo) {
		switch (attr) {
		case MORNING:
			return new TimeSpanForDailyCalc(this.timeSheets.stream().filter(tc -> tc.getWorkNo() == workNo).map(tc -> tc.getStart()).collect(Collectors.toList()).get(0), this.AMEndTime);
		case AFTERNOON:
			return new TimeSpanForDailyCalc(this.PMStartTime, this.timeSheets.stream().filter(tc -> tc.getWorkNo() == workNo).map(tc -> tc.getEnd()).collect(Collectors.toList()).get(0));
		case FULL_TIME:
		case HOLIDAY:
			return new TimeSpanForDailyCalc(this.timeSheets.stream().filter(tc -> tc.getWorkNo() == workNo).map(tc -> tc.getStart()).collect(Collectors.toList()).get(0),
										this.timeSheets.stream().filter(tc -> tc.getWorkNo() == workNo).map(tc -> tc.getEnd()).collect(Collectors.toList()).get(0));
		default:
			throw new RuntimeException("unknown attr:" + attr);
		}
	}
	
	/**
	 * 所定時間の取得
	 * @return
	 */
	public AttendanceTime getpredetermineTime(DailyWork dailyWork) {
		switch(dailyWork.decisionWorkTypeRange()) {
		case ONEDAY:
			return additionSet.getPredTime().getOneDay();
		case MORNING:
			return additionSet.getPredTime().getMorning();
		case AFTERNOON:
			return additionSet.getPredTime().getAfternoon();
		case NOTHING:
			return new AttendanceTime(0);
		default:
			throw new RuntimeException("unknown workTypeRange");
		}
	}
	
	/**
	 * 所定時間設定を所定時間設定(計算用)に変換する
	 * @param master 所定時間設定
	 */
	public static PredetermineTimeSetForCalc convertMastarToCalc(PredetemineTimeSetting master) {
		return new PredetermineTimeSetForCalc(master.getPrescribedTimezoneSetting().getLstTimezone(),
											  master.getPrescribedTimezoneSetting().getMorningEndTime(),
											  master.getPrescribedTimezoneSetting().getAfternoonStartTime(),
											  master.getPredTime(),
											  master.getRangeTimeDay(),
											  master.getStartDateClock());
	}
	
	/**
	 * workNoに一致する所定時間を取得する
	 * @param workNo
	 * @return
	 */
	public Optional<TimezoneUse> getTimeSheets(AttendanceHolidayAttr attr,int workNo) {
		
		Optional<TimezoneUse> timeSheet = this.timeSheets.stream().filter(t -> t.getWorkNo()==workNo).findFirst();
		
		switch (attr) {
		case MORNING:
			if(timeSheet.isPresent()) {
				return Optional.of(new TimezoneUse(timeSheet.get().getStart(),this.AMEndTime,timeSheet.get().getUseAtr(),timeSheet.get().getWorkNo()));
			}
			return Optional.empty();
		case AFTERNOON:
			if(timeSheet.isPresent()) {
				return Optional.of(new TimezoneUse(this.PMStartTime,timeSheet.get().getEnd(),timeSheet.get().getUseAtr(),timeSheet.get().getWorkNo()));
			}
			return Optional.empty();
		case FULL_TIME:
		case HOLIDAY:
			return timeSheet;
		default:
			throw new RuntimeException("unknown attr:" + attr);
		}
		
	}
	
	/**
	 * 出勤休日区分に対応する全ての所定時間を取得する
	 * @param attr 出勤休日区分
	 * @return 所定時間設定List(使用区分付き)
	 */
	public List<TimezoneUse> getTimeSheets(AttendanceHolidayAttr attr) {
		
		List<TimezoneUse> results = new ArrayList<>();
		for (TimezoneUse timeSheet : this.timeSheets){
			switch (attr) {
			case MORNING:
				TimeWithDayAttr amStart = timeSheet.getStart();
				TimeWithDayAttr amEnd = this.AMEndTime;
				if (amStart.lessThan(amEnd)){
					results.add(new TimezoneUse(amStart, amEnd, timeSheet.getUseAtr(), timeSheet.getWorkNo()));
				}
				break;
			case AFTERNOON:
				TimeWithDayAttr pmStart = this.PMStartTime;
				TimeWithDayAttr pmEnd = timeSheet.getEnd();
				if (pmStart.lessThan(pmEnd)){
					results.add(new TimezoneUse(pmStart, pmEnd, timeSheet.getUseAtr(), timeSheet.getWorkNo()));
				}
				break;
			case FULL_TIME:
			case HOLIDAY:
				results.add(timeSheet);
			default:
				break;
			}
		}
		return results;
	}
	
	/**
	 * 勤務NOに一致する所定時間帯を取得する
	 * （出勤休日区分は参照しない為、午前休午後休の時間帯補正をしません）
	 * @param workNo 勤務NO
	 * @return 所定時間帯
	 */
	public Optional<TimezoneUse> getTimeSheet(WorkNo workNo) {
		return this.timeSheets.stream().filter(t -> t.getWorkNo() == workNo.v()).findFirst();
	}
	
	public Optional<TimezoneUse> getTimeSheet(int workNo) {
		return this.timeSheets.stream().filter(t -> t.getWorkNo() == workNo).findFirst();
	}
	
	/**
	 * 1日の範囲時間帯を作成する
	 * @return　1日の範囲時間帯
	 */
	public TimeSpanForDailyCalc getOneDayTimeSpan() {
		return new TimeSpanForDailyCalc(this.getStartOneDayTime(), this.getStartOneDayTime().forwardByMinutes(this.getOneDayRange().valueAsMinutes()));
	}
	
	/**
	 * create this Instance
	 * @return new Instance
	 */
	@Override
	public PredetermineTimeSetForCalc clone() {	
		PredetermineTimeSetForCalc cloned;
		try {
			cloned = new PredetermineTimeSetForCalc(
				this.timeSheets.stream().map(t -> t.clone()).collect(Collectors.toList()),
				new TimeWithDayAttr(this.AMEndTime.getDayTime()),
				new TimeWithDayAttr(this.PMStartTime.getDayTime()),
				this.additionSet.clone(),
				new AttendanceTime(this.oneDayRange.valueAsMinutes()),
				new TimeWithDayAttr(this.startOneDayTime.getDayTime()));
		}	
		catch (Exception e){
			throw new RuntimeException("PredetermineTimeSetForCalc clone error.");
		}
		return cloned;
	}
	
	/**
	* 所定時間帯を変動させる
	* @param calculationRangeOfOneDay 1日の計算範囲
	*/
	public void fluctuationPredeterminedTimeSheetToSchedule(List<TimeLeavingWork> timeLeavingWorks) {
		for(int i=0; i<timeLeavingWorks.size(); i++) {
			if(timeLeavingWorks.get(i).getAttendanceTime().isPresent()) {
				this.timeSheets.get(i).updateStartTime(timeLeavingWorks.get(i).getAttendanceTime().get());
			}
			if(timeLeavingWorks.get(i).getLeaveTime().isPresent()) {
				this.timeSheets.get(i).updateStartTime(timeLeavingWorks.get(i).getLeaveTime().get());
			}
		}
	}
	
	/**
	 * 作り直す
	 * @param timeZones 時間帯
	 * @return 時間帯を入れ替えた所定時間設定
	 */
	public PredetermineTimeSetForCalc reCreate(List<TimeZone> timeZones) {
		List<TimezoneUse> newTimes = timeZones.stream()
				.filter(t -> this.getTimeSheet(t.getCnt()).isPresent())
				.map(t -> new TimezoneUse(t.getStart(), t.getEnd(), this.getTimeSheet(t.getCnt()).get().getUseAtr(), t.getCnt()))
				.collect(Collectors.toList());
		return new PredetermineTimeSetForCalc(
				newTimes,
				new TimeWithDayAttr(this.AMEndTime.getDayTime()),
				new TimeWithDayAttr(this.PMStartTime.getDayTime()),
				this.additionSet.clone(),
				new AttendanceTime(this.oneDayRange.valueAsMinutes()),
				new TimeWithDayAttr(this.startOneDayTime.getDayTime()));
	}
}
