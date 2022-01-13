package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.残業申請・休出時間申請の対象時間を取得する.残業休出時間
 * @author tutt
 *
 */
@AllArgsConstructor
@Setter
@Getter
public class OvertimeLeaveTimeDto {
	
	//年月日
	private GeneralDate date;
	
	//時間
	private int time;
	
	//残業休出区分
	private int overtimeLeaveAtr;

}
