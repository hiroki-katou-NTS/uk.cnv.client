package nts.uk.ctx.at.record.dom.adapter.workplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Optional;

@AllArgsConstructor
@Getter
public class WorkplaceInformationImport {

	/**
	 * 会社ID
	 */
	private String companyId;

	/**
	 * 削除フラグ
	 */
	private boolean deleteFlag;

	/**
	 * 職場履歴ID
	 */
	private String workplaceHistoryId;

	/**
	 * 職場ID
	 */
	private String workplaceId;

	/**
	 * 職場コード
	 */
	private String workplaceCode;

	/**
	 * 職場名称
	 */
	private String workplaceName;

	/**
	 * 職場総称
	 */
	private String workplaceGeneric;

	/**
	 * 職場表示名
	 */
	private String workplaceDisplayName;

	/**
	 * 階層コード
	 */
	private String hierarchyCode;

	/**
	 * 職場外部コード
	 */
	private Optional<String> workplaceExternalCode;

}
