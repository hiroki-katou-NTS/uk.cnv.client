package nts.uk.screen.com.app.find.equipment.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;

@Data
@AllArgsConstructor
public class EquipmentDataResultDto {

	/**
	 * 設備利用実績データ<List>
	 */
	private List<EquipmentDataDto> equipmentDatas;
	
	/**
	 * 社員リスト
	 */
	private List<EmployeeInformationImport> employeeInfos;
}
