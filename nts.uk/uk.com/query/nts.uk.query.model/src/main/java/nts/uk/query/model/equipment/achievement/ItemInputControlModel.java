package nts.uk.query.model.equipment.achievement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInputControlModel {

	// 項目分類
	private int itemCls;

	// 必須
	private boolean require;

	// 桁数
	private Integer digitsNo;

	// 最大値
	private Integer maximum;

	// 最小値
	private Integer minimum;
}
