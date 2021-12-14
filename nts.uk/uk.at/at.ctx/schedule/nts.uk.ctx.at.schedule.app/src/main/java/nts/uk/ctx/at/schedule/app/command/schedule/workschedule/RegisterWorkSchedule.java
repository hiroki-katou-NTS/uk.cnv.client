/**
 * 
 */
package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author laitv
 */

@Stateless
public class RegisterWorkSchedule<T> {
	
	@Inject
	private RegisWorkScheduleCommandHandler<T> regisWorkSchedule;
	
	@Inject
	private RegisWorkScheduleShiftCmdHandler<T> regisWorkScheduleShift;

	private static final String DATE_FORMAT = "yyyy/MM/dd";

	
	public ResultRegisWorkSchedule handle(List<WorkScheduleCommand> command) {
		if (command.isEmpty())
			return null;
		ResultRegisWorkSchedule rs = null;
		String viewMode = command.get(0).viewMode;
		if (viewMode.equals("shift")) {
			List<WorkScheduleSaveCommand<T>> dataReg = convertParam(command, "shift");
			rs = regisWorkScheduleShift.handle(dataReg);
		} else {
			List<WorkScheduleSaveCommand<T>> dataReg = convertParam(command, "other");
			rs = regisWorkSchedule.handle(dataReg);
		}
		return rs;
	}
	
	@SuppressWarnings({"rawtypes","unchecked"})
	private List<WorkScheduleSaveCommand<T>> convertParam(List<WorkScheduleCommand> command, String viewMode) {
		List<WorkScheduleSaveCommand<T>> rs = new ArrayList<WorkScheduleSaveCommand<T>>();
		if (viewMode == "shift") {
			for (WorkScheduleCommand wsCmd : command) {
				GeneralDate ymd = GeneralDate.fromString(wsCmd.ymd, DATE_FORMAT);
				WorkScheduleSaveCommand<T> ws = new WorkScheduleSaveCommand<T>(wsCmd.sid, ymd, wsCmd.shiftCode);
				rs.add(ws);
			}
		} else {
			for (WorkScheduleCommand wsCmd : command) {
				GeneralDate ymd = GeneralDate.fromString(wsCmd.ymd, DATE_FORMAT);
				String workTypeCd = wsCmd.workTypeCd;
				String workTimeCd = wsCmd.workTimeCd;
				WorkInformationDto workInfor = new WorkInformationDto(workTypeCd, workTimeCd);
				Map<Integer, TimeWithDayAttr> mapAttendIdWithTime = new HashMap<Integer, TimeWithDayAttr>();
				if (wsCmd.isChangeTime) {
					TimeWithDayAttr startTime = new TimeWithDayAttr(wsCmd.startTime); 
					TimeWithDayAttr endTime   = new TimeWithDayAttr(wsCmd.endTime);
					mapAttendIdWithTime.put(31, startTime);
					mapAttendIdWithTime.put(34, endTime);
				} 
				WorkScheduleSaveCommand<T> ws = new WorkScheduleSaveCommand(wsCmd.sid, ymd, workInfor, mapAttendIdWithTime, new ArrayList<>(), false);
				rs.add(ws);
			}
		}
		return rs;
	}
}
