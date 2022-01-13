package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.Data;
import nts.arc.time.GeneralDate;

@Data
public class StartParamDto {
	//社員ID
	public String employeeId; 
	//基準日
	public GeneralDate refDate;
	//List<工数実績項目ID>
	public List<Integer> itemIds;
	//List<選択中の作業情報>
	public List<WorkCodeFrameNoParamDto> workCodeFrameNo;
}
