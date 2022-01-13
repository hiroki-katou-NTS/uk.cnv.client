package nts.uk.screen.at.app.dailyperformance.correction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
@Data
@NoArgsConstructor
public class DPItemValue {
	
	private String rowId;
	
	private String columnKey;
	
	private int itemId;
	
	private String value;
	
	private String valueType;
	
	private String layoutCode;
	
	private String employeeId;
	
	private GeneralDate date;
	
	private Integer typeGroup;
	
	private String message;
	
	public DPItemValue(String rowId, String employeeId, GeneralDate date, int itemId){
		this.rowId = rowId;
		this.employeeId = employeeId;
		this.date = date;
		this.itemId = itemId;
	}
	
	public DPItemValue(String rowId, String employeeId, GeneralDate date, int itemId, String value, String message){
		this.rowId = rowId;
		this.employeeId = employeeId;
		this.date = date;
		this.itemId = itemId;
		this.value = value;
		this.valueType = message;
	}

	public DPItemValue(String rowId, String columnKey, int itemId, String value, String valueType, String layoutCode,
			String employeeId, GeneralDate date, Integer typeGroup) {
		super();
		this.rowId = rowId;
		this.columnKey = columnKey;
		this.itemId = itemId;
		this.value = value;
		this.valueType = valueType;
		this.layoutCode = layoutCode;
		this.employeeId = employeeId;
		this.date = date;
		this.typeGroup = typeGroup;
	}
	
	public DPItemValue(String employeeId, String message){
		this.employeeId = employeeId;
		this.message = message;
	}
	
	/*
	 * ■INPUT「修正内容」から「DPItemValue」を作成する 
	 * rowId = INPUT「対象社員」+ INPUT「対象日」 
	 * columnKey = INPUT「対象社員」+ INPUT「対象日」+INPUT「修正内容」.itemId 
	 * itemId = INPUT「修正内容」.itemId 
	 * value = INPUT「修正内容」.value 
	 * valueType = INPUT「修正内容」.valueType 
	 * layoutCode = INPUT「修正内容」.layoutCode 
	 * employeeId = INPUT「対象社員」 
	 * date = INPUT「対象日」 
	 * typeGroup= NULL 
	 * message = NULL
	 */
	public DPItemValue(String targetEmployee, GeneralDate targetDate, ItemValue item) {
		super();
		this.rowId = targetEmployee + targetDate.toString("yyyyMMdd");
		this.columnKey = targetEmployee + targetDate.toString("yyyyMMdd") + item.getItemId();
		this.itemId = item.getItemId();
		this.value = item.getValue();
		this.valueType = item.getValueType() == null ? null : item.getValueType().name;
		this.layoutCode = item.getLayoutCode();
		this.employeeId = targetEmployee;
		this.date = targetDate;
		this.typeGroup = null;
		this.message = null;
	}
	
	public DPItemValue createNewError(String errorNew) {
		return new DPItemValue(this.rowId, columnKey, itemId, value, valueType, layoutCode, employeeId, date, typeGroup,errorNew);
	}

	public DPItemValue(String rowId, String columnKey, int itemId, String value, String valueType, String layoutCode,
			String employeeId, GeneralDate date, Integer typeGroup, String message) {
		super();
		this.rowId = rowId;
		this.columnKey = columnKey;
		this.itemId = itemId;
		this.value = value;
		this.valueType = valueType;
		this.layoutCode = layoutCode;
		this.employeeId = employeeId;
		this.date = date;
		this.typeGroup = typeGroup;
		this.message = message;
	}
}
