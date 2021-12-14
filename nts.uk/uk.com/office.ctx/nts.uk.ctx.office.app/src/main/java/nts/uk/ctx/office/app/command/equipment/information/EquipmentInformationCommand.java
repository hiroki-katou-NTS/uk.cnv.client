package nts.uk.ctx.office.app.command.equipment.information;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;

@Value
public class EquipmentInformationCommand implements EquipmentInformation.MementoGetter {
	
	/**
	 * コード
	 */
	private String code;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 有効開始日
	 */
	private GeneralDate effectiveStartDate;
	
	/**
	 * 有効終了日
	 */
	private GeneralDate effectiveEndDate;
	
	/**
	 * 備分類コード
	 */
	private String equipmentClsCode;
	
	/**
	 * 備考
	 */
	private String remark;
}
