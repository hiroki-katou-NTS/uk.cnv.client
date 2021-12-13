package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.残業申請・休出時間申請の対象時間を取得する.残業休出区分
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
public enum OverTimeLeaveType {
	
	// 0: 残業申請
	OVER_TIME_APPLICATION(0, "残業申請"),
	
	//1: 休日出勤申請
	HOLIDAY_WORK_APPLICATION(1, "休出時間申請");

	public int value;

	public String name;
}
