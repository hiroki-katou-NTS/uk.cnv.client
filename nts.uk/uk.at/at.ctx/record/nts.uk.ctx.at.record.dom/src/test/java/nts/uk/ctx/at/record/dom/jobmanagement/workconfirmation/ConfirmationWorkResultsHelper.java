package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.ArrayList;
import java.util.List;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * 
 * @author chungnt
 *
 */

public class ConfirmationWorkResultsHelper {

	private String companyID = "companyID";
	private String employeeID = "employeeID";
	private GeneralDate date = GeneralDate.today();
	private TaskFrameNo taskFrameNo = new TaskFrameNo(2);
	private TaskCode taskCodes = new TaskCode("taskCodes");
	
	private TaskFrameSetting taskFrameSetting = new TaskFrameSetting(new TaskFrameNo(2),
			new TaskFrameName("TaskFrameName"),
			EnumAdaptor.valueOf(1, UseAtr.class));
	
	private List<TaskFrameSetting> frameSettingList = new ArrayList<>();
	
}
