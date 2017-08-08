package find.person.info.item;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.person.dom.person.info.dateitem.DateItem;
import nts.uk.ctx.bs.person.dom.person.info.item.ItemType;
import nts.uk.ctx.bs.person.dom.person.info.item.ItemTypeState;
import nts.uk.ctx.bs.person.dom.person.info.item.PernfoItemDefRepositoty;
import nts.uk.ctx.bs.person.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.bs.person.dom.person.info.numericitem.NumericItem;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.CodeNameReferenceType;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.EnumReferenceCondition;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.MasterReferenceCondition;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.ReferenceType;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.ReferenceTypeState;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.SelectionItem;
import nts.uk.ctx.bs.person.dom.person.info.setitem.SetItem;
import nts.uk.ctx.bs.person.dom.person.info.singleitem.DataTypeState;
import nts.uk.ctx.bs.person.dom.person.info.singleitem.SingleItem;
import nts.uk.ctx.bs.person.dom.person.info.stringitem.StringItem;
import nts.uk.ctx.bs.person.dom.person.info.timeitem.TimeItem;
import nts.uk.ctx.bs.person.dom.person.info.timepointitem.TimePointItem;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class PerInfoItemDefFinder {

	@Inject
	private PernfoItemDefRepositoty pernfoItemDefRep;

	public List<PerInfoItemDefDto> getAllPerInfoItemDefByCtgId(String perInfoCtgId) {
		return pernfoItemDefRep.getAllPerInfoItemDefByCategoryId(perInfoCtgId, AppContexts.user().contractCode()).stream().map(item -> {
			return mappingFromDomaintoDto(item);
		}).collect(Collectors.toList());
	};

	public PerInfoItemDefDto getPerInfoItemDefById(String perInfoItemDefId) {
		return pernfoItemDefRep.getPerInfoItemDefById(perInfoItemDefId, AppContexts.user().contractCode()).map(item -> {
			return mappingFromDomaintoDto(item);
		}).orElse(null);
	};

	public List<PerInfoItemDefDto> getPerInfoItemDefByListId(List<String> listItemDefId) {
		return pernfoItemDefRep.getPerInfoItemDefByListId(listItemDefId, AppContexts.user().contractCode()).stream().map(item -> {
			return mappingFromDomaintoDto(item);
		}).collect(Collectors.toList());
	};

	private PerInfoItemDefDto mappingFromDomaintoDto(PersonInfoItemDefinition itemDef) {
		return new PerInfoItemDefDto(itemDef.getPerInfoItemDefId(), itemDef.getPerInfoCategoryId(),
				itemDef.getItemCode().v(), itemDef.getItemName().v(), itemDef.getIsAbolition().value,
				itemDef.getIsFixed().value, itemDef.getIsRequired().value, itemDef.getSystemRequired().value,
				itemDef.getRequireChangable().value, createItemTypeStateDto(itemDef.getItemTypeState()));
	}

	private ItemTypeStateDto createItemTypeStateDto(ItemTypeState itemTypeState) {
		SetItemDto setItem = null;
		SingleItemDto singleItem = null;
		ItemType itemType = itemTypeState.getItemType();
		if (itemType == ItemType.SINGLE_ITEM) {
			SingleItem singleItemDom = (SingleItem) itemTypeState;
			singleItem = new SingleItemDto(itemType.value, createDataTypeStateDto(singleItemDom.getDataTypeState()));
		} else {
			SetItem setItemDom = (SetItem) itemTypeState;
			setItem = new SetItemDto(itemType.value, setItemDom.getItems());
		}
		return new ItemTypeStateDto(setItem, singleItem);
	}

	private DataTypeStateDto createDataTypeStateDto(DataTypeState dataTypeState) {
		TimeItemDto timeItem = null;
		StringItemDto stringItem = null;
		TimePointItemDto timePointItem = null;
		DateItemDto dateItem = null;
		NumericItemDto numericItem = null;
		SelectionItemDto selectionItem = null;

		int dataTypeValue = dataTypeState.getDataTypeValue().value;
		switch (dataTypeValue) {
		case 1:
			StringItem strItem = (StringItem) dataTypeState;
			stringItem = new StringItemDto(dataTypeValue, strItem.getStringItemLength().v(),
					strItem.getStringItemType().value, strItem.getStringItemDataType().value);
			break;
		case 2:
			NumericItem numItem = (NumericItem) dataTypeState;
			numericItem = new NumericItemDto(dataTypeValue, numItem.getNumericItemMinus().value,
					numItem.getNumericItemAmount().value, numItem.getIntegerPart().v(), numItem.getDecimalPart().v(),
					numItem.getNumericItemMin().v(), numItem.getNumericItemMax().v());
			break;
		case 3:
			DateItem dItem = (DateItem) dataTypeState;
			dateItem = new DateItemDto(dataTypeValue, dItem.getDateItemType().value);
			break;
		case 4:
			TimeItem tItem = (TimeItem) dataTypeState;
			timeItem = new TimeItemDto(dataTypeValue, tItem.getMax().v(), tItem.getMin().v());
			break;
		case 5:
//			TimePointItem tPointItem = (TimePointItem) dataTypeState;
//			timePointItem = new TimePointItemDto(dataTypeValue, tPointItem.getTimePointItemMin().v(),
//					tPointItem.getTimePointItemMax().v());
			break;
		case 6:
			SelectionItem sItem = (SelectionItem) dataTypeState;
			selectionItem = new SelectionItemDto(dataTypeValue, createRefTypeStateDto(sItem.getReferenceTypeState()));
			break;
		default:
			break;
		}

		return new DataTypeStateDto(timeItem, stringItem, timePointItem, dateItem, numericItem, selectionItem);
	}

	private ReferenceTypeStateDto createRefTypeStateDto(ReferenceTypeState refTypeState) {
		MasterRefConditionDto masterRefCondition = null;
		CodeNameRefTypeDto codeNameRefType = null;
		EnumRefConditionDto enumRefCondition = null;
		ReferenceType refType = refTypeState.getReferenceType();
		if (refType == ReferenceType.DESIGNATED_MASTER) {
			MasterReferenceCondition masterRef = (MasterReferenceCondition) refTypeState;
			masterRefCondition = new MasterRefConditionDto(refType.value, masterRef.getMasterType().v());
		} else if (refType == ReferenceType.CODE_NAME) {
			CodeNameReferenceType codeNameRef = (CodeNameReferenceType) refTypeState;
			codeNameRefType = new CodeNameRefTypeDto(refType.value, codeNameRef.getTypeCode().v());
		} else {
			EnumReferenceCondition enumRef = (EnumReferenceCondition) refTypeState;
			enumRefCondition = new EnumRefConditionDto(refType.value, enumRef.getEnumName().v());
		}
		return new ReferenceTypeStateDto(masterRefCondition, codeNameRefType, enumRefCondition);
	}
}
