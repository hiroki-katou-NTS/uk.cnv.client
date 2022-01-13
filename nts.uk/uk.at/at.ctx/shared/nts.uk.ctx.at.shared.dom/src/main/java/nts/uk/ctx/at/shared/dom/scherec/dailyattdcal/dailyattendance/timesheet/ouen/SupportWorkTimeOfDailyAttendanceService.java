package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 日別勤怠の応援作業時間
 * @author daiki_ichioka
 *
 */
public class SupportWorkTimeOfDailyAttendanceService {

	/**
	 * 日別勤怠の応援作業時間(List)を計算する
	 * @param scheduleReGetClass 予定
	 * @param recordReGetClass 実績
	 * @param workType 勤務種類
	 * @param recordWorkTimeCode 就業時間帯コード
	 * @param predetermineTimeSetByPersonInfo 所定時間設定(計算用クラス)
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param calculateOfTotalConstraintTime  総拘束時間の計算
	 * @param converter コンバーター
	 * @return 日別勤怠の応援作業時間(List)
	 */
	public static List<OuenWorkTimeOfDailyAttendance> create(
			ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,
			WorkType workType,
			Optional<WorkTimeCode> recordWorkTimeCode,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime,
			DailyRecordToAttendanceItemConverter converter,
			IntegrationOfDaily integrationOfDaily) {
		
		//日別勤怠の応援作業時間を計算する
		return recordReGetClass.getIntegrationOfDaily().getOuenTimeSheet().stream()
				.map(timeSheet -> OuenWorkTimeOfDailyAttendance.create(
						scheduleReGetClass,
						recordReGetClass,
						workType,
						recordWorkTimeCode,
						predetermineTimeSetByPersonInfo,
						calculateOfTotalConstraintTime,
						converter,
						recordReGetClass.getIntegrationOfDaily().getOuenTimeSheet(),
						timeSheet,
						integrationOfDaily))
				.collect(Collectors.toList());
	}
}
