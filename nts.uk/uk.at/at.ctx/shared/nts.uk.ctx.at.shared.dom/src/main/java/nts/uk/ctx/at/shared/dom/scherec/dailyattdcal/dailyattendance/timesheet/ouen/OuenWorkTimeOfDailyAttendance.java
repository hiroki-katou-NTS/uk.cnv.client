package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

@Getter
/** 日別勤怠の応援作業時間 */
public class OuenWorkTimeOfDailyAttendance implements DomainObject {

	/** 応援勤務枠No: 応援勤務枠No */
	private SupportFrameNo workNo;
	
	/** 勤務時間: 時間帯別勤怠の時間 */
	private OuenAttendanceTimeEachTimeSheet workTime;

	/** 移動時間: 応援別勤務の移動時間 */
	private OuenMovementTimeEachTimeSheet moveTime;
	
	/** 金額: 勤怠日別金額 */
	private AttendanceAmountDaily amount;

	private OuenWorkTimeOfDailyAttendance(SupportFrameNo workNo, OuenAttendanceTimeEachTimeSheet workTime,
			OuenMovementTimeEachTimeSheet moveTime, AttendanceAmountDaily amount) {
		super();
		this.workNo = workNo;
		this.workTime = workTime;
		this.moveTime = moveTime;
		this.amount = amount;
	}
	
	public static OuenWorkTimeOfDailyAttendance create(SupportFrameNo workNo,
			OuenAttendanceTimeEachTimeSheet workTime,
			OuenMovementTimeEachTimeSheet moveTime, AttendanceAmountDaily amount) {
		
		return new OuenWorkTimeOfDailyAttendance(workNo, workTime, moveTime, amount);
	}
	
	/**
	 * 日別勤怠の応援作業時間を計算する
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
	 * @param integrationOfDaily 手修正を戻した日別勤怠(Work)
	 * @return 日別勤怠の応援作業時間
	 */
	public static OuenWorkTimeOfDailyAttendance create(
			ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,
			WorkType workType,
			Optional<WorkTimeCode> recordWorkTimeCode,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime,
			DailyRecordToAttendanceItemConverter converter,
			List<OuenWorkTimeSheetOfDailyAttendance> allTimeSheets,
			OuenWorkTimeSheetOfDailyAttendance processingTimeSheet,
			IntegrationOfDaily integrationOfDaily) {
		
		//勤務時間を計算する
		OuenAttendanceTimeEachTimeSheet attendanceTime = OuenAttendanceTimeEachTimeSheet.create(
				scheduleReGetClass,
				recordReGetClass,
				workType,
				recordWorkTimeCode,
				predetermineTimeSetByPersonInfo,
				calculateOfTotalConstraintTime,
				converter,
				processingTimeSheet,
				integrationOfDaily);
		
		//金額を計算する
		AttendanceAmountDaily amount = attendanceTime.calcTotalAmount();
		
		//移動時間を計算する
		OuenMovementTimeEachTimeSheet movementTime = OuenMovementTimeEachTimeSheet.create(
				scheduleReGetClass,
				recordReGetClass,
				workType,
				recordWorkTimeCode,
				predetermineTimeSetByPersonInfo,
				calculateOfTotalConstraintTime,
				converter,
				allTimeSheets,
				processingTimeSheet);
		
		return new OuenWorkTimeOfDailyAttendance(processingTimeSheet.getWorkNo(), attendanceTime, movementTime, amount);
	}

	public void update(OuenWorkTimeOfDailyAttendance inputTime) {
		this.workTime = inputTime.getWorkTime();
		this.moveTime = inputTime.getMoveTime();
		this.amount = inputTime.getAmount();
	}
}
