/**
 * 10:38:33 AM Jul 21, 2017
 */
package nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums;

import lombok.AllArgsConstructor;

/**
 * @author hungnm
 *
 */
@AllArgsConstructor
public enum ErrorAlarmClassification {

	/* エラー */
	ERROR(0, "エラー"),
	/* アラーム */
	ALARM(1, "アラーム"),
	/* その他 */
	OTHER(2, "その他");

	public final int value;

	public final String nameId;
	
}
