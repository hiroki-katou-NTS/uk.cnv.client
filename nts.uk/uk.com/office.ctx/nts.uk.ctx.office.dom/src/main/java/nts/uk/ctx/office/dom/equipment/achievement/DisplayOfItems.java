package nts.uk.ctx.office.dom.equipment.achievement;

import java.util.Optional;

import org.eclipse.persistence.internal.xr.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目の表示
 * @author NWS-DungDV
 *
 */
@Getter
@AllArgsConstructor
public class DisplayOfItems extends ValueObject {
	// 項目名称
	private UsageItemName itemName;
	
	// 単位
	private Optional<UsageRecordUnit> unit;
	
	// 説明
	private Optional<ItemDescription> memo;
}
