package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;

/** 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.残業休出申請を促すか確認する.促す対象申請
 * Temporary 促す対象申請
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class EncouragedTargetApplication {
	
	// 年月日
	private final GeneralDate date;
	
	// 申請種類
	private final ApplicationTypeShare appType;
}
