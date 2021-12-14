package nts.uk.screen.com.app.find.equipment.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.screen.com.app.find.equipment.classification.EquipmentClassificationDto;
import nts.uk.screen.com.app.find.equipment.information.EquipmentInformationDto;

@Data
@AllArgsConstructor
public class EquipmentInitInfoDto {

	/**
	 * 設備分類
	 */
	private EquipmentClassificationDto equipmentClassification;
	
	/**
	 * 設備情報<List>
	 */
	private List<EquipmentInformationDto> equipmentInformationList;
}
