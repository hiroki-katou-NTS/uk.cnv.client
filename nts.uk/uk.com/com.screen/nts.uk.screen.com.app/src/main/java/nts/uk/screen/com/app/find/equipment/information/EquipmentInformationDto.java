package nts.uk.screen.com.app.find.equipment.information;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;

@Data
public class EquipmentInformationDto implements EquipmentInformation.MementoSetter {
	
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
	
	public static EquipmentInformationDto fromDomain(EquipmentInformation domain) {
		EquipmentInformationDto dto = new EquipmentInformationDto();
		domain.setMemento(dto);
		return dto;
	}
}
