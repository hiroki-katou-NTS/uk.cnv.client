package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;

/**
 * @author thanh_nx
 *
 *         取消すために必要な申請反映履歴を取得するOutput
 */
@AllArgsConstructor
@Data
public class AcquireAppReflectHistForCancelOutput {

	// 最新の申請反映履歴
	private ApplicationReflectHistory appHistLastest;

	// 元に戻すための申請反映履歴
	private ApplicationReflectHistory appHistPrev;
}
