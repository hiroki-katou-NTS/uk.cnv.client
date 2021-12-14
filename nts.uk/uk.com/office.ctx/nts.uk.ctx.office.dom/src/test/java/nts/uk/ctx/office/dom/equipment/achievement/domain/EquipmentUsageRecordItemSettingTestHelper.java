package nts.uk.ctx.office.dom.equipment.achievement.domain;

import java.util.Optional;

import nts.uk.ctx.office.dom.equipment.achievement.DisplayOfItems;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.UsageItemName;

public class EquipmentUsageRecordItemSettingTestHelper {

	public static EquipmentUsageRecordItemSetting createDoamin() {
		String cid = "cid";
		EquipmentItemNo itemNo = new EquipmentItemNo("10");
		ItemInputControl inputControl = ItemInputControlTestHelper.caseText(21);
		DisplayOfItems items = new DisplayOfItems(new UsageItemName("name"), Optional.empty(), Optional.empty());
		return new EquipmentUsageRecordItemSetting(cid, itemNo, inputControl, items);
	}
	
	public static EquipmentUsageRecordItemSetting mockDomain(String equipmentItemNo, ItemClassification itemCls, int max) {
		String cid = "cid";
		EquipmentItemNo itemNo = new EquipmentItemNo(equipmentItemNo);
		ItemInputControl inputControl = null;
		switch (itemCls) {
		case TEXT:
			inputControl = ItemInputControlTestHelper.caseText(max);
			break;
		case NUMBER:
			inputControl = ItemInputControlTestHelper.caseNumber(max, 1);
			break;
		case TIME:
			inputControl = ItemInputControlTestHelper.caseTime(max, 1);
		}
		DisplayOfItems items = new DisplayOfItems(new UsageItemName("name"), Optional.empty(), Optional.empty());
		return new EquipmentUsageRecordItemSetting(cid, itemNo, inputControl, items);
	}
}
