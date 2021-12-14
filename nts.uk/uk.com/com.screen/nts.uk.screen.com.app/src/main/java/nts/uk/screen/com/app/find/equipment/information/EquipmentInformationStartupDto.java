package nts.uk.screen.com.app.find.equipment.information;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.screen.com.app.find.equipment.classification.EquipmentClassificationDto;

@Getter
public class EquipmentInformationStartupDto {

	// List<設備情報>
	private List<EquipmentInformationDto> equipmentInformationList;

	// List<設備分類>
	private List<EquipmentClassificationDto> equipmentClassificationList;

	public static EquipmentInformationStartupDto fromDomains(List<EquipmentInformation> equipmentInformations,
			List<EquipmentClassification> equipmentClassifications) {
		EquipmentInformationStartupDto dto = new EquipmentInformationStartupDto();
		dto.equipmentClassificationList = equipmentClassifications.stream()
				.map(data -> new EquipmentClassificationDto(data.getCode().v(), data.getName().v()))
				.collect(Collectors.toList());
		dto.equipmentInformationList = equipmentInformations.stream()
				.map(EquipmentInformationDto::fromDomain).collect(Collectors.toList());
		return dto;
	}
}
