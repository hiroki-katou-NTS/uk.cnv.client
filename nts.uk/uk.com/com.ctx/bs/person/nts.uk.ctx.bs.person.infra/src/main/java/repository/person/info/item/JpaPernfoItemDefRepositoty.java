package repository.person.info.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.info.item.ItemType;
import nts.uk.ctx.bs.person.dom.person.info.item.ItemTypeState;
import nts.uk.ctx.bs.person.dom.person.info.item.PernfoItemDefRepositoty;
import nts.uk.ctx.bs.person.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.ReferenceType;
import nts.uk.ctx.bs.person.dom.person.info.selectionitem.ReferenceTypeState;
import nts.uk.ctx.bs.person.dom.person.info.singleitem.DataTypeState;

@Stateless
public class JpaPernfoItemDefRepositoty extends JpaRepository implements PernfoItemDefRepositoty {

	private final static String SELECT_ITEMS_BY_CATEGORY_ID_QUERY = "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.itemCd, i.itemName, i.abolitionAtr, i.requiredAtr,"
			+ " ic.itemParentCd, ic.systemRequiredAtr, ic.requireChangabledAtr, ic.fixedAtr, ic.itemType,"
			+ " ic.dataType, ic.timeItemMin, ic.timeItemMax, ic.timepointItemMin, ic.timepointItemMax, ic.dateItemType,"
			+ " ic.stringItemType, ic.stringItemLength, ic.stringItemDataType, ic.numericItemMin, ic.numericItemMax, ic.numericItemAmountAtr,"
			+ " ic.numericItemMinusAtr, ic.numericItemDecimalPart, ic.numericItemIntegerPart, ic.selectionItemRefType, ic.selectionItemRefCode, i.perInfoCtgId"
			+ " FROM PpemtPerInfoItem i INNER JOIN PpemtPerInfoCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtPerInfoItemCm ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd "
			+ " AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd"
			+ " WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL";

	private final static String SELECT_ITEM_BY_ITEM_ID_QUERY = "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.itemCd, i.itemName, i.abolitionAtr, i.requiredAtr,"
			+ " ic.itemParentCd, ic.systemRequiredAtr, ic.requireChangabledAtr, ic.fixedAtr, ic.itemType,"
			+ " ic.dataType, ic.timeItemMin, ic.timeItemMax, ic.timepointItemMin, ic.timepointItemMax, ic.dateItemType,"
			+ " ic.stringItemType, ic.stringItemLength, ic.stringItemDataType, ic.numericItemMin, ic.numericItemMax, ic.numericItemAmountAtr,"
			+ " ic.numericItemMinusAtr, ic.numericItemDecimalPart, ic.numericItemIntegerPart, ic.selectionItemRefType, ic.selectionItemRefCode, i.perInfoCtgId"
			+ " FROM PpemtPerInfoItem i INNER JOIN PpemtPerInfoCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtPerInfoItemCm ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd "
			+ " AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd"
			+ " WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemPK.perInfoItemDefId = :perInfoItemDefId";

	private final static String SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY = "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.itemCd, i.itemName, i.abolitionAtr, i.requiredAtr,"
			+ " ic.itemParentCd, ic.systemRequiredAtr, ic.requireChangabledAtr, ic.fixedAtr, ic.itemType,"
			+ " ic.dataType, ic.timeItemMin, ic.timeItemMax, ic.timepointItemMin, ic.timepointItemMax, ic.dateItemType,"
			+ " ic.stringItemType, ic.stringItemLength, ic.stringItemDataType, ic.numericItemMin, ic.numericItemMax, ic.numericItemAmountAtr,"
			+ " ic.numericItemMinusAtr, ic.numericItemDecimalPart, ic.numericItemIntegerPart, ic.selectionItemRefType, ic.selectionItemRefCode, i.perInfoCtgId"
			+ " FROM PpemtPerInfoItem i INNER JOIN PpemtPerInfoCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtPerInfoItemCm ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd "
			+ " AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd"
			+ " WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemPK.perInfoItemDefId IN :listItemDefId";

	@Override
	public List<PersonInfoItemDefinition> getAllPerInfoItemDefByCategoryId(String perInfoCtgId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_CATEGORY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					return createDomainFromEntity(i);
				});
	}

	@Override
	public Optional<PersonInfoItemDefinition> getPerInfoItemDefById(String perInfoItemDefId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEM_BY_ITEM_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoItemDefId", perInfoItemDefId)
				.getSingle(i -> {
					return createDomainFromEntity(i);
				});
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemDefByListId(List<String> listItemDefId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("listItemDefId", listItemDefId).getList(i -> {
					return createDomainFromEntity(i);
				});
	}

	private PersonInfoItemDefinition createDomainFromEntity(Object[] i) {
		String perInfoItemDefId = String.valueOf(i[0]);
		String itemCode = String.valueOf(i[1]);
		String itemName = String.valueOf(i[2]);
		int isAbolition = Integer.parseInt(String.valueOf(i[3]));
		int isRequired = Integer.parseInt(String.valueOf(i[4]));
		String itemParentCode = (i[5] == null) ? null : String.valueOf(i[5]);
		int systemRequired = Integer.parseInt(String.valueOf(i[6]));
		int requireChangable = Integer.parseInt(String.valueOf(i[7]));
		int isFixed = Integer.parseInt(String.valueOf(i[8]));
		int itemType = Integer.parseInt(String.valueOf(i[9]));
		BigDecimal dataType = i[10] == null ? null : new BigDecimal(String.valueOf(i[10]));
		BigDecimal timeItemMin = i[11] == null ? null : new BigDecimal(String.valueOf(i[11]));
		BigDecimal timeItemMax = i[12] == null ? null : new BigDecimal(String.valueOf(i[12]));
		BigDecimal timepointItemMin = i[13] == null ? null : new BigDecimal(String.valueOf(i[13]));
		BigDecimal timepointItemMax = i[14] == null ? null : new BigDecimal(String.valueOf(i[14]));
		BigDecimal dateItemType = i[15] == null ? null : new BigDecimal(String.valueOf(i[15]));
		BigDecimal stringItemType = i[16] == null ? null : new BigDecimal(String.valueOf(i[16]));
		BigDecimal stringItemLength = i[17] == null ? null : new BigDecimal(String.valueOf(i[17]));
		BigDecimal stringItemDataType = i[18] == null ? null : new BigDecimal(String.valueOf(i[18]));
		BigDecimal numericItemMin = i[19] == null ? null : new BigDecimal(String.valueOf(i[19]));
		BigDecimal numericItemMax = i[20] == null ? null : new BigDecimal(String.valueOf(i[20]));
		BigDecimal numericItemAmount = i[21] == null ? null : new BigDecimal(String.valueOf(i[21]));
		BigDecimal numericItemMinus = i[22] == null ? null : new BigDecimal(String.valueOf(i[22]));
		BigDecimal numericItemDecimalPart = i[23] == null ? null : new BigDecimal(String.valueOf(i[23]));
		BigDecimal numericItemIntegerPart = i[24] == null ? null : new BigDecimal(String.valueOf(i[24]));
		BigDecimal selectionItemRefType = i[25] == null ? null : new BigDecimal(String.valueOf(i[25]));
		String selectionItemRefCode = String.valueOf(i[26]);
		String perInfoCategoryId = String.valueOf(i[27]);

		PersonInfoItemDefinition item = PersonInfoItemDefinition.createFromEntity(perInfoItemDefId, perInfoCategoryId,
				itemCode, itemParentCode, itemName, isAbolition, isFixed, isRequired, systemRequired, requireChangable);
		if (itemType == ItemType.SINGLE_ITEM.value) {
			DataTypeState dataTypeState = null;
			switch (dataType.intValue()) {
			case 1:
				dataTypeState = DataTypeState.createStringItem(stringItemLength.intValue(), stringItemType.intValue(),
						stringItemDataType.intValue());
				break;
			case 2:
				dataTypeState = DataTypeState.createNumericItem(numericItemMinus.intValue(),
						numericItemAmount.intValue(), numericItemIntegerPart.intValue(),
						numericItemDecimalPart.intValue(), numericItemMin, numericItemMax);
				break;
			case 3:
				dataTypeState = DataTypeState.createDateItem(dateItemType.intValue());
				break;
			case 4:
				dataTypeState = DataTypeState.createTimeItem(timeItemMin.intValue(), timeItemMax.intValue());
				break;
			case 5:
				dataTypeState = DataTypeState.createTimePointItem(timepointItemMin.longValue(),
						timepointItemMax.longValue());
				break;
			case 6:
				ReferenceTypeState referenceTypeState = null;
				if (selectionItemRefType.intValue() == ReferenceType.DESIGNATED_MASTER.value) {
					referenceTypeState = ReferenceTypeState.createMasterReferenceCondition(selectionItemRefCode);
				} else if (selectionItemRefType.intValue() == ReferenceType.CODE_NAME.value) {
					referenceTypeState = ReferenceTypeState.createCodeNameReferenceType(selectionItemRefCode);
				} else {
					referenceTypeState = ReferenceTypeState.createEnumReferenceCondition(selectionItemRefCode);
				}
				dataTypeState = DataTypeState.createSelectionItem(referenceTypeState);
				break;
			default:
				break;
			}
			item.setItemTypeState(ItemTypeState.createSingleItem(dataTypeState));
		} else {
			item.setItemTypeState(ItemTypeState.createSetItem(null));
		}
		return item;
	}
}
