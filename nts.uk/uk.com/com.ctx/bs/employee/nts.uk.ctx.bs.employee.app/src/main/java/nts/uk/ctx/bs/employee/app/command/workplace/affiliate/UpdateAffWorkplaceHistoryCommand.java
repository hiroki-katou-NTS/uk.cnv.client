package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.PeregRecordId;

@Getter
public class UpdateAffWorkplaceHistoryCommand{
	/** 社員ID */
	@PeregEmployeeId
	private String employeeId;

	/** The history Id. */
	// 履歴ID
	@PeregRecordId
	private String historyId;
	
	/** The workplaceCode. */
	// 職場コード
	@PeregItem("IS00084")
	private String  workplaceId;
	
//	/** The normalWorkplaceCode. */
//	// 通常職場コード
//	@PeregItem("IS00085")
//	private String  normalWorkplaceId;
	
	/** 期間 */
	/** 開始日 */
	@PeregItem("IS00082")
	private GeneralDate startDate;
	
	@PeregItem("IS00083")
	/** 終了日 */
	private GeneralDate endDate;
	
}
