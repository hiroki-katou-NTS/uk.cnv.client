package nts.uk.screen.com.app.find.equipment.data;

import lombok.Value;
import nts.arc.time.GeneralDate;

@Value
public class EquipmentInfoParam {

	/**
	 * 設備分類コード
	 */
	private String equipmentClsCode;
	
	/**
	 * 基準日
	 */
	private GeneralDate baseDate;
	
	/**
	 * 入力する
	 */
	private boolean isInput;
}
