package nts.uk.ctx.at.request.dom.application.appabsence.appforspecleave;

import lombok.Data;

/**
 * @author loivt
 * 特別休暇申請
 */
@Data
public class AppForSpecLeave {
	/**
	 * 申請ID
	 */
	private String appID;
	/**
	 * 喪主フラグ
	 */
	private boolean mournerFlag;
	/**
	 * 続柄コード
	 */
	private RelationshipCDPrimitive relationshipCD;
	/**
	 * 続柄理由
	 */
	private RelationshipReasonPrimitive relationshipReason;
}
