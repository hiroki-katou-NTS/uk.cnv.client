package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class StartWorkInputPanelParam {

	// 社員ID
	private String employeeId;

	// 基準日
	private GeneralDate refDate;

	// 作業グループ
	private WorkGroupDto workGroupDto;

}