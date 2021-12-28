package nts.uk.shr.pereg.app;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.time.TimeWithDayAttr;

@AllArgsConstructor
@NoArgsConstructor
public class ItemValue {

	private String definitionId;
	private String itemCode;
	private String itemName;
	private String value;
	private String text;
	private String defValue;
	private String defText;
	private int type;
	private int logType;

	// ham nay phuc vu cho viec tren finder chuyen doi sang ItemValue
	public static ItemValue createItemValue(String definitionId, String itemCode,String itemName, String value, String text ,int dataType,
			Integer selectionRefType, String selectionRefCode) {
		ItemValue itemValue = new ItemValue();
		itemValue.definitionId = definitionId;
		itemValue.itemCode = itemCode;
		itemValue.itemName = itemName;
		itemValue.defText = text;
		itemValue.defValue = value;
		itemValue.value = value;
		itemValue.text = text;
		itemValue.logType = dataType;
		ItemValueType itemValueType = EnumAdaptor.valueOf(dataType, ItemValueType.class);
		switch (itemValueType) {
		case STRING:
			itemValue.type = 1;
			break;
		case NUMERIC:
		case TIME:
		case TIMEPOINT:
		case NUMBERIC_BUTTON:
			itemValue.type = 2;
			break;
		case DATE:
			itemValue.type = 3;
			break;
		case SELECTION:
		case SELECTION_RADIO:
		case SELECTION_BUTTON:
			switch (selectionRefType) {
			// 1:専用マスタ(DesignatedMaster)
			case 1:
				if (selectionRefCode.equals("M00006")) {
					itemValue.type = 2;
				} else {
					itemValue.type = 1;
				}
				break;
			//2:コード名称(CodeName)
			case 2:
				itemValue.type = 1;
				break;
			// 3:列挙型(Enum)
			case 3:
				itemValue.type = 2;
				break;
			}
			break;
		
		default:
			itemValue.type = 1;
			break;
		}
		return itemValue;
	}

	/**
	 * 個人情報項目定義ID
	 * 
	 * @return 個人情報項目定義ID
	 */
	public String definitionId() {
		return this.definitionId;
	}

	/**
	 * 項目定義コード
	 * 
	 * @return 項目定義コード
	 */
	public String itemCode() {
		return this.itemCode;
	}
	
	/**
	 * 項目定義名
	 * 
	 * @return 項目定義名
	 */
	public String itemName() {
		return this.itemName;
	}

	public void setValue(Object obj) {
		if (obj == null){
			this.value = null;
			return;
		}
		switch (this.saveDataType()) {
		case NUMERIC:
			this.value = obj.toString();
			break;
		case STRING:
			this.value = obj.toString();
			break;
		case DATE:
			this.value = ((GeneralDate) obj).toString("yyyy/MM/dd");
			break;
		default:
			throw new RuntimeException("invalid type: " + this.type);
		}
	}
	
	public int type() {
		return this.type;
	}
	
	public String valueBefore() {
		return this.defValue;
	}
	
	public String contentBefore() {
		return this.defText;
	}
	
	public String valueAfter() {
		return this.value;
	}
	
	public String contentAfter() {
		return this.text;
	}

	public int logType() {
		return this.logType;
	}

	public SaveDataType saveDataType() {
		return EnumAdaptor.valueOf(this.type, SaveDataType.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T value() {
		Object convertedValue;
		switch (this.saveDataType()) {
		case NUMERIC:
			// In case of value is empty or null return default value
			if (StringUtils.isEmpty(this.value)) {
				convertedValue = null;
			} else {
				convertedValue = new BigDecimal(this.value);
			}
			break;
		case STRING:
			convertedValue = this.value;
			break;
		case DATE:
			// In case of value is empty or null return null
			if ("Invalid date".equals(this.value)) {	
				this.value = null;
			}
			if (StringUtils.isEmpty(this.value)) {
				convertedValue = null;
			} else {
				convertedValue = GeneralDate.fromString(this.value, "yyyy/MM/dd");
			}
			break;
		default:
			throw new RuntimeException("invalid type: " + this.type);
		}

		return (T) convertedValue;
	}


	
	public static ItemValue setContentForCPS002(ItemValue item) {
		String contentNew = (item.contentAfter() == null || item.contentAfter() == "") ? item.valueAfter(): item.contentAfter();
		item.setContentBefore(null);
		item.setValueBefore(null);
		item.setContentAfter(formatContent(item.logType(), contentNew, item.valueAfter()));
		if((item.text == null && item.value == null) 
		|| (item.text == "" && item.value == "") 
		|| (item.text == null && item.value == "")
		|| (item.text == "" && item.value == null)) return null;
		return item;
	}
	
	public static ItemValue setContentForCPS001(ItemValue item) {
		if(item.logType() == 2) {
			item.setValueAfter((item.valueAfter() == "" || item.valueAfter() == null) ? null: item.valueAfter());
			item.setContentAfter((item.contentAfter() == "" || item.contentAfter() == null) ? null: item.contentAfter());
			item.setValueBefore((item.valueBefore() == "" || item.valueBefore() == null) ? null: item.valueBefore());
			item.setContentBefore((item.contentBefore() == "" || item.contentBefore() == null) ? null: item.contentBefore());
		}
		
		return item;
	}
	
	public static  ItemValue filterItem(ItemValue item){
			Object oldValue = formatValue(item, item.valueBefore());
			Object newValue = formatValue(item, item.valueAfter());
			
			if(oldValue == null && newValue == null) {
				return null;
			}
			
			if(oldValue == null && newValue != null) {
				return item;
			}
			
			if(oldValue != null) {
				if (!oldValue.equals(newValue)) {
					return item;
				}
			}
			
		return null;
	}
	
	
	public static Object formatValue(ItemValue item, Object value) {
		
		if (value == null || value == "") return null;
		
		ItemValueType itemValueType = EnumAdaptor.valueOf(item.logType(), ItemValueType.class);
		
		switch(itemValueType) {
		case STRING:
		case DATE:
		case TIME:
		case TIMEPOINT:
		case SELECTION: 
		case SELECTION_BUTTON:
		case SELECTION_RADIO:
		case READONLY:
		case READONLY_BUTTON:
		case RELATE_CATEGORY:
			return value.toString();
		case NUMBERIC_BUTTON:
		case NUMERIC:
			return new BigDecimal(value.toString());
		default:
			return value.toString();
		}
	}
	
	private static String formatContent(int logType, String viewContent, String value) {
		ItemValueType itemValueType = EnumAdaptor.valueOf(logType, ItemValueType.class);
		
		if (viewContent == null || value == null) return null;
		if (viewContent.equals("") && logType != ItemValueType.STRING.value) return null;
		if (value.equals("") && logType != ItemValueType.STRING.value) return null;
		BigDecimal valueAfter  = null;
		switch(itemValueType) {
		case STRING:
		case DATE:
		case SELECTION: 
		case SELECTION_BUTTON:
		case SELECTION_RADIO:
		case NUMERIC: 
		case NUMBERIC_BUTTON:
		case READONLY:
		case READONLY_BUTTON:
		case RELATE_CATEGORY:
			return viewContent;
		case TIME:
			valueAfter = new BigDecimal(value);
			return valueAfter == null? null: formatMinutesToTime(valueAfter.intValue());
		case TIMEPOINT:
			valueAfter = new BigDecimal(value);
			return valueAfter == null? null: new TimeWithDayAttr(valueAfter.intValue()).getFullText();
		default:
			throw new RuntimeException("invalid attribute: " + value);
		}
	}
	
	public static String formatMinutesToTime(int valueAsMinutes) {
		
		boolean isMinus = valueAsMinutes < 0;
		int value = Math.abs(valueAsMinutes);
		int minute = value % 60;
		int hour = value / 60;
		
		return String.format("%s%d:%02d", (isMinus ? "-" : ""), hour, minute);
	}

	public void setValueAfter(String value) {
		this.value = value;
	}

	public void setContentAfter(String text) {
		this.text = text;
	}

	public void setValueBefore(String defValue) {
		this.defValue = defValue;
	}
	
	public void setDataType(int type) {
		this.type = type;
	}
	
	public void setLogType(int logType) {
		this.logType = logType;
	}
	
	public void setValueBefore(Object defValue) {
		if (defValue == null){
			this.defValue = null;
			return;
		}
		switch (this.saveDataType()) {
		case NUMERIC:
			this.defValue = defValue.toString();
			break;
		case STRING:
			this.defValue = defValue.toString();
			break;
		case DATE:
			this.defValue = ((GeneralDate) defValue).toString("yyyy/MM/dd");
			break;
		default:
			throw new RuntimeException("invalid type: " + this.type);
		}
	}

	public void setContentBefore(String defText) {
		this.defText = defText;
	}
	
	public void setItemId(String itemDfId) {
		this.definitionId = itemDfId;
	}
	
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
}
