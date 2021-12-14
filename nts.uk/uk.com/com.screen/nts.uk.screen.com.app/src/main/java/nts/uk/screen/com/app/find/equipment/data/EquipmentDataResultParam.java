package nts.uk.screen.com.app.find.equipment.data;

import lombok.Value;

@Value
public class EquipmentDataResultParam {

	/**
	 * 設備分類コード
	 */
	private String equipmentClsCode;
	
	/**
	 * 設備コード
	 */
	private String equipmentCode;
	
	/**
	 * 年月
	 */
	private int ym;
}
