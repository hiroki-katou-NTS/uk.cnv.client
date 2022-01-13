package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.support.SupportWorkSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Getter
/** 応援別勤務の移動時間 */
public class OuenMovementTimeEachTimeSheet implements DomainObject {

	/** 総移動時間: 勤怠時間 */
	private AttendanceTime totalMoveTime;
	
	/** 休憩時間: 勤怠時間 */
	private AttendanceTime breakTime;
	
	/** 所定内移動時間: 勤怠時間 */
	private AttendanceTime withinMoveTime;
	
	/** 割増時間: 割増時間 */
	private PremiumTimeOfDailyPerformance premiumTime;

	private OuenMovementTimeEachTimeSheet(AttendanceTime totalMoveTime, AttendanceTime breakTime,
			AttendanceTime withinMoveTime, PremiumTimeOfDailyPerformance premiumTime) {
		super();
		this.totalMoveTime = totalMoveTime;
		this.breakTime = breakTime;
		this.withinMoveTime = withinMoveTime;
		this.premiumTime = premiumTime;
	}
	
	public static OuenMovementTimeEachTimeSheet create(AttendanceTime totalMoveTime, 
			AttendanceTime breakTime, AttendanceTime withinMoveTime, PremiumTimeOfDailyPerformance premiumTime) {
		return new OuenMovementTimeEachTimeSheet(totalMoveTime, breakTime, withinMoveTime, premiumTime);
	}
	
	/**
	 * 移動時間を計算する
	 * @param scheduleReGetClass 予定
	 * @param recordReGetClass 実績
	 * @param workType 勤務種類
	 * @param recordWorkTimeCode 就業時間帯コード
	 * @param predetermineTimeSetByPersonInfo 所定時間設定(計算用クラス)
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param calculateOfTotalConstraintTime  総拘束時間の計算
	 * @param converter コンバーター
	 * @param allTimeSheets 全ての応援作業時間帯
	 * @param processingTimeSheet 処理中の応援作業時間帯
	 * @return 応援別勤務の移動時間
	 */
	public static OuenMovementTimeEachTimeSheet create(
			ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,
			WorkType workType,
			Optional<WorkTimeCode> recordWorkTimeCode,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime,
			DailyRecordToAttendanceItemConverter converter,
			List<OuenWorkTimeSheetOfDailyAttendance> allTimeSheets,
			OuenWorkTimeSheetOfDailyAttendance processingTimeSheet) {
		
		//移動時間帯を取得する
		Optional<TimeSpanForDailyCalc> moveTimeSheet = getMoveTimeSheet(processingTimeSheet, allTimeSheets, SupportWorkSetting.defaultValue());
		
		if(!moveTimeSheet.isPresent() || moveTimeSheet.get().lengthAsMinutes() < 0)
			return OuenMovementTimeEachTimeSheet.createAllZero();
		
		//日別実績(Work)の退避
		IntegrationOfDaily copyIntegrationOfDaily = converter.setData(recordReGetClass.getIntegrationOfDaily()).toDomain();
		
		//時間帯を変更して日別勤怠の勤怠時間を計算する
		AttendanceTimeOfDailyAttendance calcResult = AttendanceTimeOfDailyAttendance.calcAfterChangedRange(
				scheduleReGetClass,
				recordReGetClass,
				workType,
				recordWorkTimeCode,
				predetermineTimeSetByPersonInfo,
				calculateOfTotalConstraintTime,
				converter,
				moveTimeSheet.get());
		
		copyIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(Optional.of(calcResult));
		
		//手修正後の再計算
		IntegrationOfDaily result = AttendanceTimeOfDailyAttendance.reCalcForSupport(
				copyIntegrationOfDaily,
				converter,
				recordReGetClass);
		
		if(!result.getAttendanceTimeOfDailyPerformance().isPresent())
			return OuenMovementTimeEachTimeSheet.createAllZero();
		
		//項目移送
		return create(result.getAttendanceTimeOfDailyPerformance().get());
	}
	
	/**
	 * 全て0で作成する
	 * @return 応援別勤務の移動時間
	 */
	public static OuenMovementTimeEachTimeSheet createAllZero() {
		return new OuenMovementTimeEachTimeSheet(AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceTime.ZERO, PremiumTimeOfDailyPerformance.createEmpty());
	}
	
	/**
	 * 移動時間帯を取得する
	 * @param processingTimeSheet 日別勤怠の応援作業時間帯
	 * @param allTimeSheets 全ての応援作業時間帯
	 * @param ouenWorkSetting 応援・作業設定
	 * @return 移動時間帯
	 */
	private static Optional<TimeSpanForDailyCalc> getMoveTimeSheet(
			OuenWorkTimeSheetOfDailyAttendance processingTimeSheet,
			List<OuenWorkTimeSheetOfDailyAttendance> allTimeSheets,
			SupportWorkSetting ouenWorkSetting) {
		
		if(ouenWorkSetting.getIsUse().equals(NotUseAtr.NOT_USE))
			return Optional.empty();
		
		//移動時間の計上先を確認する
		switch(ouenWorkSetting.getAccountingOfMoveTime()) {
		case DESTINATION : //移動先
			return getMoveTimeSheetAsDestination(processingTimeSheet, allTimeSheets);
		
		case MOUVING_SOURCE : //移動元
			return getMoveTimeSheetAsMovingSource(processingTimeSheet, allTimeSheets);
			
		case NOT_CALC : //計算しない
			return Optional.empty();
		}
		return Optional.empty();
	}
	
	/**
	 * 移動先として計上する移動時間帯を取得する
	 * @param processingTimeSheet 日別勤怠の応援作業時間帯
	 * @param allTimeSheets 全ての応援作業時間帯
	 * @return 移動時間帯
	 */
	private static Optional<TimeSpanForDailyCalc> getMoveTimeSheetAsDestination(
			OuenWorkTimeSheetOfDailyAttendance processingTimeSheet,
			List<OuenWorkTimeSheetOfDailyAttendance> allTimeSheets) {
		//一つ前の応援作業時間帯の終了．時刻を取得する
		Optional<OuenWorkTimeSheetOfDailyAttendance> previous = allTimeSheets.stream()
			.filter(t->t.getWorkNo().v().equals(processingTimeSheet.getWorkNo().v() - 1))
			.findFirst();
		
		if(!previous.isPresent() || !previous.get().getTimeSheet().getEndTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		if(!processingTimeSheet.getTimeSheet().getStartTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		//取得した時刻～処理中の時間帯．開始．時刻を移動時間帯とする
		return Optional.of(new TimeSpanForDailyCalc(
				previous.get().getTimeSheet().getEndTimeWithDayAttr().get(),
				processingTimeSheet.getTimeSheet().getStartTimeWithDayAttr().get()));
	}
	
	/**
	 * 移動元として計上する移動時間帯を取得する
	 * @param processingTimeSheet 日別勤怠の応援作業時間帯
	 * @param allTimeSheets 全ての応援作業時間帯
	 * @return 移動時間帯
	 */
	private static Optional<TimeSpanForDailyCalc> getMoveTimeSheetAsMovingSource(
			OuenWorkTimeSheetOfDailyAttendance processingTimeSheet,
			List<OuenWorkTimeSheetOfDailyAttendance> allTimeSheets) {
		//一つ後の応援作業時間帯の開始．時刻を取得する
		Optional<OuenWorkTimeSheetOfDailyAttendance> next = allTimeSheets.stream()
			.filter(t->t.getWorkNo().v().equals(processingTimeSheet.getWorkNo().v() + 1))
			.findFirst();
		
		if(!next.isPresent() || !next.get().getTimeSheet().getStartTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		if(!processingTimeSheet.getTimeSheet().getEndTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		//処理中の時間帯．終了．時刻～取得した時刻を移動時間帯とする
		return Optional.of(new TimeSpanForDailyCalc(
				processingTimeSheet.getTimeSheet().getEndTimeWithDayAttr().get(),
				next.get().getTimeSheet().getStartTimeWithDayAttr().get()));
	}

	/**
	 * 日別勤怠の勤怠時間から作成する
	 * @param attendanceTime 日別勤怠の勤怠時間
	 * @return 応援別勤務の移動時間
	 */
	private static OuenMovementTimeEachTimeSheet create(AttendanceTimeOfDailyAttendance attendanceTime) {
		return new OuenMovementTimeEachTimeSheet(
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime(),
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getActualWorkTime().addMinutes(
						attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getActualWithinPremiumTime().minute()),
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime(),
				attendanceTime.getActualWorkingTimeOfDaily().getPremiumTimeOfDailyPerformance());
	}
}
