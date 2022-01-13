package nts.uk.ctx.pereg.app.find.person.info.item;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.pereg.dom.person.info.item.ItemType;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.pereg.dom.person.info.setitem.SetItem;
import nts.uk.ctx.pereg.dom.person.info.setitem.SetTableItem;
import nts.uk.ctx.pereg.dom.person.info.singleitem.SingleItem;

@Data
@NoArgsConstructor
public class PerInfoItemDefDto {

	private String id;

	private String perInfoCtgId;
	
	private String itemCode;
	
	private String itemParentCode;

	private String itemName;

	private int isAbolition;

	private int isFixed;

	private int isRequired;

	private int systemRequired;

	private int requireChangable;
	
	private ItemTypeStateDto itemTypeState;
	
	private String resourceId;

	// is not ItemDefinition of attribute
	private int dispOrder;

	/**
	 * will remove: lanlt
	 */
	private BigDecimal selectionItemRefType;

	// is not ItemDefinition of attribute
	private List<EnumConstant> selectionItemRefTypes;
	
	private boolean canAbolition;
	
	private String initValue;
	
	private String personInfoItemGroupId;
	
	private String fieldGroupName;
	
	/**
	 * @param itemDefinition
	 * This constructor initial value from domain
	 * don't initial for dispOrder and selectionItemRefTypes
	 */
	public PerInfoItemDefDto(PersonInfoItemDefinition itemDefinition) {
		this.id = itemDefinition.getPerInfoItemDefId();
		this.perInfoCtgId = itemDefinition.getPerInfoCategoryId();
		this.itemCode = itemDefinition.getItemCode().v();
		this.itemParentCode = itemDefinition.getItemParentCode().v();
		this.itemName = itemDefinition.getItemName().v();
		this.isAbolition = itemDefinition.getIsAbolition().value;
		this.isFixed = itemDefinition.getIsFixed().value;
		this.isRequired = itemDefinition.getIsRequired().value;
		this.systemRequired = itemDefinition.getSystemRequired().value;
		this.requireChangable = itemDefinition.getRequireChangable().value;

		ItemType itemType = itemDefinition.getItemTypeState().getItemType();

		if (itemType == ItemType.SINGLE_ITEM) {
			SingleItem singleItemDom = (SingleItem) itemDefinition.getItemTypeState();
			DataTypeStateDto dataTypeStateDto = DataTypeStateDto.createDto(singleItemDom.getDataTypeState());
			this.itemTypeState = ItemTypeStateDto.createSingleItemDto(dataTypeStateDto);
		} else if (itemType == ItemType.SET_ITEM) {
			SetItem setItemDom = (SetItem) itemDefinition.getItemTypeState();
			this.itemTypeState = ItemTypeStateDto.createSetItemDto(setItemDom.getItems());
		} else {
			SetTableItem setItemDom = (SetTableItem) itemDefinition.getItemTypeState();
			this.itemTypeState = ItemTypeStateDto.createSetTableItemDto(setItemDom.getItems());
		}

		this.resourceId = itemDefinition.getResourceId().isPresent() ? itemDefinition.getResourceId().get() : null;
		// will remove
		this.selectionItemRefType = itemDefinition.getSelectionItemRefType();
		this.canAbolition = itemDefinition.isCanAbolition();
		this.initValue = itemDefinition.getInitValue().isPresent()? itemDefinition.getInitValue().get().toString(): null;

	}
	
	public boolean isSingleItem() {
		return itemTypeState.itemType == ItemType.SINGLE_ITEM.value;
	}
}
