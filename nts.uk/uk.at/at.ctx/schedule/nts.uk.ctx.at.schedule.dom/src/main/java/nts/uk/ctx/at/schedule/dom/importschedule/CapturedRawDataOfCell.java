package nts.uk.ctx.at.schedule.dom.importschedule;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * 1セル分の内容
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.1セル分の内容
 * @author kumiko_otake
 */
@Value
public class CapturedRawDataOfCell {

	/** 社員コード **/
	private final String employeeCode;
	/** 年月日 **/
	private final GeneralDate ymd;
	/** 取り込みコード **/
	private final ShiftMasterImportCode importCode;

}
