package nts.uk.ctx.office.dom.equipment.information;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@Getter
@Setter
public class EquipmentInformationDto implements EquipmentInformation.MementoGetter,
												EquipmentInformation.MementoSetter {
	
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
