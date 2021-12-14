package nts.uk.query.app.equipment.classificationmaster;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentClassificationDto {
	// コード
	private String code;
	
	// 名称
	private String name;
}
