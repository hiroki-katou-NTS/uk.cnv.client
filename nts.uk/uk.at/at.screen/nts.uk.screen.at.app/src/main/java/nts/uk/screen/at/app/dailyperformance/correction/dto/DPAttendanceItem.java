/**
 * 5:44:29 PM Aug 23, 2017
 */
package nts.uk.screen.at.app.dailyperformance.correction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hungnm
 *
 */
@Data
@AllArgsConstructor
public class DPAttendanceItem {
	private Integer id;
	private String name;
	private String displayName;
	private Integer displayNumber;
	private boolean userCanSet;
	private Integer lineBreakPosition;
	/*0:  コード */
	/*1:  マスタを参照する */
	/*2:  回数*/
	/*3:  金額*/
	/*4:  区分 */
	/*5:  時間 */
	/*6:  時刻*/
	/*7:  文字 */
	private Integer attendanceAtr;
	
	/**
	 * 1: 勤務種類 2: 就業時間帯 3: 勤務場所 4: 乖離理由 5: 職場 6: 分類 7: 職位 8: 雇用 9: するしない区分 10:
	 * 時間外の自動計算区分 11: 外出理由"
	 */
	private Integer typeGroup;
	
	private Integer primitive;
	
	public  boolean isNumber() {
		return attendanceAtr ==  2 || attendanceAtr == 3 || attendanceAtr == 5;
	}
}
