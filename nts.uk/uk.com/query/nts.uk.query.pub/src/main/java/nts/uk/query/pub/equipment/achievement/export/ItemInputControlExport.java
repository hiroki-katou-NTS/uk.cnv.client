package nts.uk.query.pub.equipment.achievement.export;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.ItemInputControlModel;

@Data
@Builder
public class ItemInputControlExport {

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

	public static ItemInputControlExport fromModel(ItemInputControlModel model) {
		return ItemInputControlExport.builder().digitsNo(model.getDigitsNo()).itemCls(model.getItemCls())
				.maximum(model.getMaximum()).minimum(model.getMinimum()).require(model.isRequire()).build();
	}
}
