package nts.uk.ctx.at.request.app.command.application.holidaywork;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.applicationsetting.apptypeset.AppTypeSettingCommand;
import nts.uk.ctx.at.request.app.find.application.common.dto.ApprovalRootStateImportDto;

/**
 * Refactor5
 * @author huylq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterMultiCommand {
	
	/**
	 * 会社ID
	 */
	private String companyId;
	
	/**
	 * 申請者リスト
	 */
	private List<String> empList;
	
	/**
	 * 休日出勤申請起動時の表示情報
	 */
	private AppHdWorkDispInfoCmd appHdWorkDispInfo;
	
	/**
	 * 休日出勤申請
	 */
	private AppHolidayWorkInsertCmd appHolidayWork;
	
	/**
	 * List＜社員ID, 承認ルートの内容＞
	 */
	private Map<String, ApprovalRootStateImportDto> approvalRootContentMap;
	
	/**
	 * 申請種類別設定
	 */
	private AppTypeSettingCommand appTypeSetting;
}
