package nts.uk.ctx.office.dom.equipment.information;

import nts.arc.time.GeneralDate;

public class EquipmentInformationTestHelper {

	public static EquipmentInformation mockDomain(String remark) {
		EquipmentInformationDto dto = new EquipmentInformationDto();
		dto.setCode("00001");
		dto.setName("name");
		dto.setEquipmentClsCode("11111");
		dto.setEffectiveStartDate(GeneralDate.today());
		dto.setEffectiveEndDate(GeneralDate.today());
		dto.setRemark(remark);
		return EquipmentInformation.createFromMemento(dto);
	}
}
