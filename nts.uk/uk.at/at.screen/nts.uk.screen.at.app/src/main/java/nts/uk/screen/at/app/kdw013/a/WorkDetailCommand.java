package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

@Getter
@AllArgsConstructor
public class WorkDetailCommand {
	
	/** 年月日 */
	private GeneralDate date;

	/** List<作業詳細> */
	private List<WorkDetailsParamCommand> lstWorkDetailsParamCommand;

}
