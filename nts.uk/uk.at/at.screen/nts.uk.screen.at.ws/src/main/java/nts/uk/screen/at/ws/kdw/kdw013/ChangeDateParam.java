package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class ChangeDateParam {

	// 社員ID
	private String employeeId;

	// 基準日
	private GeneralDate refDate;

	// 表示期間
	private DatePeriodCommand displayPeriod;

	// 対象項目リスト
	private List<String> itemIds;

}
