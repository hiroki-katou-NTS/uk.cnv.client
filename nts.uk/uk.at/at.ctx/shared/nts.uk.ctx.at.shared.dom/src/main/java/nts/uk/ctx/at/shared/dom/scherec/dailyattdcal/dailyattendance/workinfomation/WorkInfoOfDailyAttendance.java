package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelAppStamp;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * 日別勤怠の勤務情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.勤務情報.日別勤怠の勤務情報
 * @author tutk
 *
 *
 */
@Getter
@NoArgsConstructor
public class WorkInfoOfDailyAttendance implements DomainObject {

	@Setter
	// 勤務実績の勤務情報
	private WorkInformation recordInfo;
	@Setter
	// 計算状態
	private CalculationState calculationState;
	// 直行区分
	private NotUseAttribute goStraightAtr;
	// 直帰区分
	private NotUseAttribute backStraightAtr;
	// 曜日
	private DayOfWeek dayOfWeek;
	// 始業終業時間帯
	private List<ScheduleTimeSheet> scheduleTimeSheets = new ArrayList<>();
	//振休振出として扱う日数
	@Setter
	private Optional<NumberOfDaySuspension> numberDaySuspension = Optional.empty();

	//排他version
	@Setter
	@Getter
	private long ver;

	public WorkInfoOfDailyAttendance(WorkInformation workInfo,
			CalculationState calculationState, NotUseAttribute goStraightAtr, NotUseAttribute backStraightAtr,
			DayOfWeek dayOfWeek, List<ScheduleTimeSheet> scheduleTimeSheets, Optional<NumberOfDaySuspension> numberDaySuspension) {
		super();
		this.recordInfo = workInfo;
		this.calculationState = calculationState;
		this.goStraightAtr = goStraightAtr;
		this.backStraightAtr = backStraightAtr;
		this.dayOfWeek = dayOfWeek;
		this.scheduleTimeSheets = scheduleTimeSheets;
        this.numberDaySuspension = numberDaySuspension;
	}

	/**
	 * [C-1] 作る
	 * @param require
	 * @param workInfo 勤務情報
	 * @param calculationState 計算状態
	 * @param backStraightAtr 直帰区分
	 * @param goStraightAtr 直行区分
	 * @param dayOfWeek 曜日
	 * @return
	 */
	public static WorkInfoOfDailyAttendance create(
			Require require,
			WorkInformation workInfo,
			CalculationState calculationState,
			NotUseAttribute backStraightAtr,
			NotUseAttribute goStraightAtr,
			DayOfWeek dayOfWeek
			) {

		List<TimeZone> timeZoneList = workInfo.getWorkInfoAndTimeZone(require).map(x -> x.getTimeZones()).orElse(new ArrayList<>());
		List<ScheduleTimeSheet> scheduleTimeSheets = new ArrayList<>();
		for ( int index = 0; index < timeZoneList.size(); index++) {
			scheduleTimeSheets.add(
					new ScheduleTimeSheet(
							index + 1,
							timeZoneList.get(index).getStart().v(),
							timeZoneList.get(index).getEnd().v()));
		}

		return new WorkInfoOfDailyAttendance(
				workInfo,
				calculationState,
				goStraightAtr,
				backStraightAtr,
				dayOfWeek,
				scheduleTimeSheets, Optional.empty());
	}

	/**
	 * 計算ステータスの変更
	 * @param state 計算ステータス
	 */
	public void changeCalcState(CalculationState state) {
		this.setCalculationState(state);
	}

	/**
	 * 指定された勤務回数の予定時間帯を取得する
	 *
	 * @param workNo
	 * @return 予定時間帯
	 */
	public Optional<ScheduleTimeSheet> getScheduleTimeSheet(WorkNo workNo) {
		return this.scheduleTimeSheets.stream()
				.filter(ts -> ts.getWorkNo().equals(workNo)).findFirst();
	}

	public void setGoStraightAtr(NotUseAttribute goStraightAtr) {
		this.goStraightAtr = goStraightAtr;
	}

	public void setBackStraightAtr(NotUseAttribute backStraightAtr) {
		this.backStraightAtr = backStraightAtr;
	}

	public void setScheduleTimeSheets(List<ScheduleTimeSheet> scheduleTimeSheets) {
		this.scheduleTimeSheets = scheduleTimeSheets;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * [2] 出勤・休日系の判定
	 * @param require
	 * @return
	 */
	public Optional<WorkStyle> getWorkStyle(Require require){
		return this.recordInfo.getWorkStyle(require);
	}

	// 勤務情報と始業終業を変更する
	public List<Integer> changeWorkSchedule(Require require, WorkInformation workInfo, boolean changeWorkType,
			boolean changeWorkTime) {
		List<Integer> lstState = new ArrayList<>();
		// 勤務情報を変更する
		WorkTypeCode workTypeCode = this.recordInfo.getWorkTypeCode();
		Optional<WorkTimeCode> workTimeCode = this.recordInfo.getWorkTimeCodeNotNull();

		if (changeWorkType) {
			workTypeCode = workInfo.getWorkTypeCode();
		}

		if (changeWorkTime) {
			workTimeCode = workInfo.getWorkTimeCodeNotNull();
		}

		this.recordInfo = new WorkInformation(workTypeCode, workTimeCode.orElse(null));

		// 所定時間帯を取得する
		Optional<WorkInfoAndTimeZone> timeZoneOpt = this.recordInfo.getWorkInfoAndTimeZone(require);


		// 始業終業に取得した所定時間帯をセットする
		this.scheduleTimeSheets.clear();
		timeZoneOpt.ifPresent(workInfoTimeZone -> {

			val timeZone = workInfoTimeZone.getTimeZones().stream()
										.sorted((c1, c2) -> c1.getStart().compareTo(c2.getStart()))
										.collect(Collectors.toList());

			for(int i = 0; i < timeZone.size(); i++) {
				this.scheduleTimeSheets.add(new ScheduleTimeSheet(i + 1,
																	timeZone.get(i).getStart().valueAsMinutes(),
																	timeZone.get(i).getEnd().valueAsMinutes()));
				lstState.add(CancelAppStamp.createItemId(3, i + 1, 2));
				lstState.add(CancelAppStamp.createItemId(4, i + 1, 2));
			}
		});
		return lstState;
	}

	/**
	 * 出勤系か
	 * @param require
	 * @return
	 */
	public boolean isAttendanceRate(Require require) {
		return this.recordInfo.isAttendanceRate(require);
	}

	public static interface Require extends WorkInformation.Require {

	}

}
