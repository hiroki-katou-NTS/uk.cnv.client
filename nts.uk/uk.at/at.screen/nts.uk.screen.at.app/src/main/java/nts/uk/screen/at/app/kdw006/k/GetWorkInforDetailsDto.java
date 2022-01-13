package nts.uk.screen.at.app.kdw006.k;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetWorkInforDetailsDto {

	/** 履歴ID */
	private String historyId;

	/** 項目ID */
	private int itemId;

	/** コード */
	private String code;

	/** 名称 */
	private String name;

	/** 外部コード */
	private String externalCode;

}
