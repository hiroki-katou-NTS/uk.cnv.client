package nts.uk.ctx.workflow.app.find.approvermanagement.setting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobAssignSettingDto {
	/**
	 * 会社ID
	 */
	private String companyId;
	/**
	 * 兼務者を含める
	 */
	private Boolean isConcurrently;
}
