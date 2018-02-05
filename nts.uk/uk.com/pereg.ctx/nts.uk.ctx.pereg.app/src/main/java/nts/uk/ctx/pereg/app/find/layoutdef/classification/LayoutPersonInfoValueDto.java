package nts.uk.ctx.pereg.app.find.layoutdef.classification;

import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nts.uk.ctx.pereg.app.find.person.info.item.DataTypeStateDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefForLayoutDto;
import nts.uk.ctx.pereg.app.find.person.info.item.SingleItemDto;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.item.ItemType;
import nts.uk.shr.pereg.app.ComboBoxObject;

@Data
@RequiredArgsConstructor
public class LayoutPersonInfoValueDto {

	private String recordId;

	// categoryID
	@NonNull
	private String categoryId;

	// categoryCode
	@NonNull
	private String categoryCode;

	// itemDefID
	@NonNull
	private String itemDefId;

	// for label text
	@NonNull
	private String itemName;

	// for label constraint
	@NonNull
	private String itemCode;

	// for new set item structor
	private String itemParentCode;

	@NonNull
	// index of item in list (multiple, history)
	private Integer row;

	// value of item definition
	private Object value;

	// is required?
	// for render control label
	private boolean required;

	/**
	 * combo box value list when item type selection
	 */
	private List<ComboBoxObject> lstComboBoxValue;

	/*
	 * hidden value - view only - can edit
	 */
	private ActionRole actionRole;

	// contains some information of item for render control
	private DataTypeStateDto item;

	private int type;
	private int ctgType;

	public LayoutPersonInfoValueDto(String categoryId, String categoryCode, String itemDefId, String itemName,
			String itemCode, String itemParentCode, Integer row, Object value) {
		this.categoryId = categoryId;
		this.categoryCode = categoryCode;
		this.itemDefId = itemDefId;
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.itemParentCode = itemParentCode;
		this.row = row;
		this.value = value;
	}

	public LayoutPersonInfoValueDto() {

	}

	public static LayoutPersonInfoValueDto cloneFromItemDef(PersonInfoCategory perInfoCategory,
			PerInfoItemDefDto itemDef) {
		LayoutPersonInfoValueDto dataObject = new LayoutPersonInfoValueDto();

		dataObject.setCategoryId(itemDef.getPerInfoCtgId());
		dataObject.setCategoryCode(perInfoCategory.getCategoryCode().v());
		dataObject.setCtgType(perInfoCategory.getCategoryType().value);

		dataObject.setItemDefId(itemDef.getId());
		dataObject.setItemName(itemDef.getItemName());
		dataObject.setItemCode(itemDef.getItemCode());
		dataObject.setItemParentCode(itemDef.getItemParentCode());
		dataObject.setRow(0);
		dataObject.setRequired(itemDef.getIsRequired() == 1);

		dataObject.setType(itemDef.getItemTypeState().getItemType());
		dataObject.setCtgType(perInfoCategory.getCategoryType().value);
		if (itemDef.getItemTypeState().getItemType() == ItemType.SINGLE_ITEM.value) {
			SingleItemDto sigleItem = (SingleItemDto) itemDef.getItemTypeState();
			dataObject.setItem(sigleItem.getDataTypeState());
		}
		return dataObject;
	}

	public static LayoutPersonInfoValueDto initData(PerInfoItemDefForLayoutDto itemDef, Object value) {
		LayoutPersonInfoValueDto dataObject = new LayoutPersonInfoValueDto();
		dataObject.setRecordId(itemDef.getRecordId());
		dataObject.setLstComboBoxValue(itemDef.getLstComboxBoxValue());
		dataObject.setCategoryId(itemDef.getPerInfoCtgId());
		dataObject.setCategoryCode(itemDef.getPerInfoCtgCd());
		dataObject.setItemDefId(itemDef.getId());
		dataObject.setItemName(itemDef.getItemName());
		dataObject.setItemCode(itemDef.getItemCode());
		dataObject.setItemParentCode(itemDef.getItemParentCode());
		dataObject.setRow(itemDef.getRow());
		dataObject.setValue(value);
		dataObject.setCtgType(itemDef.getCtgType());
		dataObject.setRequired(itemDef.getIsRequired() == 1);

		dataObject.setType(itemDef.getItemTypeState().getItemType());

		if (itemDef.getItemDefType() == 2) {
			SingleItemDto sigleItem = (SingleItemDto) itemDef.getItemTypeState();
			dataObject.setItem(sigleItem.getDataTypeState());
		}
		dataObject.setActionRole(itemDef.getActionRole());
		return dataObject;
	}
}
