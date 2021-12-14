package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import lombok.Value;
import nts.arc.time.GeneralDate;

/**
 * 社員と年月日
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.社員と年月日
 * @author kumiko_otake
 */
@Value
public class EmployeeAndYmd {

	/** 社員ID **/
	private final String employeeId;
	/** 年月日 **/
	private final GeneralDate ymd;

}
