package nts.uk.ctx.at.shared.dom.scherec.taskmanagement.domainservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace.NarrowingDownTaskByWorkplace;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.ExternalCooperationInfo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskAbName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskDisplayInfo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskExternalCode;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskName;

/**
 * 
 * @author chungnt
 *
 */

public class GetWorkAvailableToEmployeesServiceHelper {

	public static TaskFrameUsageSetting getTask() {

		List<TaskFrameSetting> frameSettingList = new ArrayList<>();

		TaskFrameSetting frameSetting = new TaskFrameSetting(new TaskFrameNo(2), new TaskFrameName("DUMMY"),
				EnumAdaptor.valueOf(1, UseAtr.class));

		frameSettingList.add(frameSetting);
		frameSettingList.add(frameSetting);
		frameSettingList.add(frameSetting);
		frameSettingList.add(frameSetting);
		frameSettingList.add(frameSetting);

		TaskFrameUsageSetting result = new TaskFrameUsageSetting(frameSettingList);

		return result;
	}

	public static Task getTaskDefault() {
		
		List<TaskCode> taskCodes = new ArrayList<>();
		taskCodes.add(new TaskCode("DUMMY"));
		taskCodes.add(new TaskCode("DUMMY"));
		taskCodes.add(new TaskCode("DUMMY"));
		taskCodes.add(new TaskCode("DUMMY"));
		
		Task task = new Task(
				new TaskCode("DUMMY"),
				new TaskFrameNo(2),
				new ExternalCooperationInfo(
						Optional.of(new TaskExternalCode("DUMMY")),
						Optional.of(new TaskExternalCode("DUMMY")),
						Optional.of(new TaskExternalCode("DUMMY")),
						Optional.of(new TaskExternalCode("DUMMY")),
						Optional.of(new TaskExternalCode("DUMMY"))),
				taskCodes,
				new DatePeriod(GeneralDate.today(), GeneralDate.today()),
				new TaskDisplayInfo(
						new TaskName("DUMMY"),
						new TaskAbName("DUMMY"),
						Optional.empty(),
						Optional.empty()));
		return task;
	}
	
	public static NarrowingDownTaskByWorkplace getNarrowingDown() {
		
		List<TaskCode> taskCodeList = new ArrayList<>();
		
		taskCodeList.add(new TaskCode("DUMMY"));
		taskCodeList.add(new TaskCode("DUMMY"));
		taskCodeList.add(new TaskCode("DUMMY"));
		
		NarrowingDownTaskByWorkplace result = new NarrowingDownTaskByWorkplace(
				"workPlaceId",
				new TaskFrameNo(2),
				taskCodeList);
		
		return result;
	}

}
