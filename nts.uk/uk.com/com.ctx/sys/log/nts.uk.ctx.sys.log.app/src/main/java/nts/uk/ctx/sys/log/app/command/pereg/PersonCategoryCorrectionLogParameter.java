package nts.uk.ctx.sys.log.app.command.pereg;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.shr.com.security.audittrail.correction.content.DataValueAttribute;
import nts.uk.shr.com.security.audittrail.correction.content.pereg.TargetDataKey;
import nts.uk.shr.com.security.audittrail.correction.content.pereg.CategoryCorrectionLog;
import nts.uk.shr.com.security.audittrail.correction.content.pereg.InfoOperateAttr;
import nts.uk.shr.com.security.audittrail.correction.content.pereg.ItemInfo;
import nts.uk.shr.com.security.audittrail.correction.content.pereg.ReviseInfo;
import nts.uk.shr.pereg.app.ItemValue;
import nts.uk.shr.pereg.app.ItemValueType;

@Value
public class PersonCategoryCorrectionLogParameter implements IPeregCorrection, Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private final String categoryId;
	private final String categoryName;
	private final InfoOperateAttr infoOperateAttr;
	private final List<PersonCorrectionItemInfo> itemInfos;
	private final TargetDataKey targetKey;
	private final Optional<ReviseInfo> reviseInfo;
	private final String hashID;

	public PersonCategoryCorrectionLogParameter(String categoryId, String categoryName, InfoOperateAttr infoOperateAttr,
			List<PersonCorrectionItemInfo> itemInfos, TargetDataKey targetKey, Optional<ReviseInfo> reviseInfo) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.infoOperateAttr = infoOperateAttr;
		this.itemInfos = itemInfos;
		this.targetKey  = targetKey;
		this.reviseInfo = reviseInfo;
		
		this.hashID = IdentifierUtil.randomUniqueId();
	}

	public CategoryCorrectionLog toCategoryInfo() {
		return new CategoryCorrectionLog(this.categoryId, this.categoryName, this.infoOperateAttr, this.targetKey,
				mapToItemInfo(this.itemInfos), this.reviseInfo);
	}

	public CategoryCorrectionLog toCategoryInfoCPS002(String categoryId, String categoryName, InfoOperateAttr infoOperateAttr,
			TargetDataKey targetKey, List<ItemInfo> itemInfos, Optional<ReviseInfo> reviseInfo) {
		return new CategoryCorrectionLog(categoryId, categoryName, infoOperateAttr, targetKey, itemInfos, reviseInfo);
	}

	private List<ItemInfo> mapToItemInfo(List<PersonCorrectionItemInfo> itemInfos) {
		return itemInfos.stream().map(i -> {
			return i.toCreateItemInfo();
		}).collect(Collectors.toList());
	}

	@Override
	public String getHashID() {
		return "CATEGORY_" + this.hashID;
	}

	@Value
	public static class PersonCorrectionItemInfo implements Serializable {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		private final String itemId;
		private final String itemName;
		private final String valueBefore;
		private final String viewValueBefore;
		private final String valueAfter;
		private final String viewValueAfter;
		private final Integer valueType;

		public ItemInfo toCreateItemInfo() {
			return ItemInfo.create("", this.itemId, this.itemName, converType(valueType),
					convertValue(valueType, this.valueBefore), this.viewValueBefore,
					convertValue(valueType, this.valueAfter), this.viewValueAfter);
		}
		
		public static  PersonCorrectionItemInfo createItemInfoToItemLog(ItemValue item) {
			return new PersonCorrectionItemInfo(item.definitionId(), item.itemName(), item.valueBefore(), item.contentBefore(), item.valueAfter(), item.contentAfter(), item.logType());
		}

		private Object convertValue(int valueType, String value) {
			ItemValueType itemValueType = EnumAdaptor.valueOf(valueType, ItemValueType.class);
			
			if (value == null) return null;
			
			switch (itemValueType) {
			case STRING:
			case SELECTION: 
			case SELECTION_BUTTON:
			case SELECTION_RADIO:
			case READONLY:
			case READONLY_BUTTON:
			case RELATE_CATEGORY:
				if(value.equals("")) return null;
				return value;
			case TIME:
			case TIMEPOINT:
				if ("".equals(value)) {
					return null;
				}
				return new Integer(new BigDecimal(value).intValue());
			case NUMBERIC_BUTTON:
			case NUMERIC:
				if(value.equals("")) return new BigDecimal(0);
				return new BigDecimal(value);
			case DATE:
				if ("Invalid date".equals(value)) return null;
				return GeneralDate.fromString(value, "yyyy/MM/dd");
			default:
				return null;
			}

		}

		private DataValueAttribute converType(int valueType) {
			
			ItemValueType itemValueType = EnumAdaptor.valueOf(valueType, ItemValueType.class);
			
			switch (itemValueType) {
			case STRING:
			case SELECTION: 
			case SELECTION_BUTTON:
			case SELECTION_RADIO:
			case READONLY:
			case READONLY_BUTTON:
			case RELATE_CATEGORY:
				return DataValueAttribute.STRING;
			case NUMBERIC_BUTTON:
			case NUMERIC:
				return DataValueAttribute.COUNT;
			case DATE:
				return DataValueAttribute.DATE;
			case TIME:
				return DataValueAttribute.TIME;
			case TIMEPOINT:
				return DataValueAttribute.CLOCK;	
			default:
				return DataValueAttribute.of(-1);

			}
		}
	}
}