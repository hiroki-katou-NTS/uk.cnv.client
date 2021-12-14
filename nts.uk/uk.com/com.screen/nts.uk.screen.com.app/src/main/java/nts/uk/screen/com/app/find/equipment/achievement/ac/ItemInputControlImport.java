package nts.uk.screen.com.app.find.equipment.achievement.ac;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.ItemInputControlExport;

@Data
@Builder
public class ItemInputControlImport {

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

	public static ItemInputControlImport fromExport(ItemInputControlExport export) {
		return ItemInputControlImport.builder().digitsNo(export.getDigitsNo()).itemCls(export.getItemCls())
				.maximum(export.getMaximum()).minimum(export.getMinimum()).require(export.isRequire()).build();
	}
}
