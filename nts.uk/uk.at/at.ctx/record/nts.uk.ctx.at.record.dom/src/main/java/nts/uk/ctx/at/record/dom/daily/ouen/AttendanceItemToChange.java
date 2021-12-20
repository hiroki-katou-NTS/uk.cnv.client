package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thanhpv
 * @Temporary 変更する勤怠項目
 */
@AllArgsConstructor
@Getter
public class AttendanceItemToChange {

	/**
	 * 	勤怠項目リスト	List<勤怠項目ID>
	 */
	private List<Integer> attendanceId;
	
	/**
	 * 応援作業別時間帯	日別実績の応援作業別勤怠時間帯
	 */
	private OuenWorkTimeSheetOfDaily ouenWorkTimeSheetOfDaily; 
}
