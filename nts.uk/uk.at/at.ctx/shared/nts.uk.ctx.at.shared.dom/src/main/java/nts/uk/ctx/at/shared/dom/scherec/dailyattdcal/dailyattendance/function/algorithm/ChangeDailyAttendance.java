package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author ThanhNX
 *
 *         日別勤怠の何が変更されたか
 */
@Data
@AllArgsConstructor
public class ChangeDailyAttendance {

	/** 勤務情報 */
	public boolean workInfo;

	/** 出退勤 */
	public boolean attendance;

	/** 計算区分 */
	public boolean calcCategory;
	
	/** 勤務予定から移送した値も補正する */
	public boolean correctValCopyFromSche;
	
	/** 予定実績区分*/
	public ScheduleRecordClassifi classification;
	
	/** 直行直帰区分*/
	public boolean directBounceClassifi; 
	
	public static ChangeDailyAttendance createChangeDailyAtt(List<Integer> lstItemId,  ScheduleRecordClassifi classification) {

		boolean workInfo = lstItemId.stream().filter(x -> x.intValue() == 28 || x.intValue() == 29).findFirst()
				.isPresent();
		boolean attendance = lstItemId.stream()
				.filter(x -> x.intValue() == 31 || x.intValue() == 34 || x.intValue() == 41 || x.intValue() == 44)
				.findFirst().isPresent();
		boolean directBounceClassifi = lstItemId.stream().filter(x -> x.intValue() == 859 || x.intValue() == 860)
				.findFirst().isPresent();
		return new ChangeDailyAttendance(workInfo, attendance, false, workInfo, classification,
				directBounceClassifi);
	}
	
	//ディフォルトの作成
	public static ChangeDailyAttendance createDefault(ScheduleRecordClassifi scheduleRecordClassifi) {
		return new ChangeDailyAttendance(false, false, false, false, scheduleRecordClassifi, false);
	}
}
