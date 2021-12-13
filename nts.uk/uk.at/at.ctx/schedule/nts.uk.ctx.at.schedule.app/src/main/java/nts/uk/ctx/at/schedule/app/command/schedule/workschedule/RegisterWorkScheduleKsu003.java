package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.task.AsyncTaskInfo;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterWorkScheduleKsu003<T> {
	
	@Inject
	private RegisWorkScheduleCommandHandler<T> regisWorkSchedule;
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public ExecutionInfor handle(List<WorkScheduleParam> command) {
		if (command.isEmpty())
			return null;
		String executeId = IdentifierUtil.randomUniqueId();
		
		List<WorkScheduleSaveCommand<T>> dataReg = convertParam(command, "other");
		
		AsyncTaskInfo taskInfor = regisWorkSchedule.handle(dataReg);
		
		return ExecutionInfor.builder()
                .taskInfor(taskInfor)
                .executeId(executeId)
                .build();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<WorkScheduleSaveCommand<T>> convertParam(List<WorkScheduleParam> command, String viewMode) {
		List<WorkScheduleSaveCommand<T>> rs = new ArrayList<WorkScheduleSaveCommand<T>>();

			for (WorkScheduleParam wsCmd : command) {
				GeneralDate ymd = GeneralDate.fromString(wsCmd.ymd, DATE_FORMAT);
				String workTypeCd = wsCmd.workTypeCd;
				String workTimeCd = wsCmd.workTimeCd;
				WorkInformationDto workInfor = new WorkInformationDto(workTypeCd, workTimeCd);
				Map<Integer, Object> mapAttendIdWithTime = new HashMap<>();
				TimeWithDayAttr startTime = null;
				TimeWithDayAttr endTime = null;
				TimeWithDayAttr startTime2 = null;
				TimeWithDayAttr endTime2 = null;
				
				if(wsCmd.startTime != null) 
					startTime = new TimeWithDayAttr(wsCmd.startTime);
				
				if(wsCmd.endTime != null)
					endTime   = new TimeWithDayAttr(wsCmd.endTime);
				
				if(wsCmd.startTime2 != null) 
					startTime2 = new TimeWithDayAttr(wsCmd.startTime2);
					
				if(wsCmd.endTime2 != null)
					endTime2   = new TimeWithDayAttr(wsCmd.endTime2);
				
				if (wsCmd.workTimeCd != null && wsCmd.startTime != null && wsCmd.endTime != null ) {
					mapAttendIdWithTime.put(31, startTime);
					mapAttendIdWithTime.put(34, endTime);
				}
				
				if (wsCmd.workTimeCd != null && wsCmd.startTime2 != null && wsCmd.endTime2 != null ) {
					mapAttendIdWithTime.put(41, startTime2);
					mapAttendIdWithTime.put(44, endTime2);
				}
// 				Hỏi lại thiết kế vì đang ko có map int vs boolean TQP
				
				if(wsCmd.directAtr == true) {
					mapAttendIdWithTime.put(859, EnumAdaptor.valueOf(1, NotUseAttribute.class));
				} else {
					mapAttendIdWithTime.put(859, EnumAdaptor.valueOf(0, NotUseAttribute.class));
				}
				
				if(wsCmd.bounceAtr == true) {
					mapAttendIdWithTime.put(860, EnumAdaptor.valueOf(1, NotUseAttribute.class));
				} else {
					mapAttendIdWithTime.put(860, EnumAdaptor.valueOf(0, NotUseAttribute.class));
				}
				List<TimeSpanForCalc> breakTimeList = wsCmd.listBreakTime.stream().map(i -> new TimeSpanForCalc(new TimeWithDayAttr(i.getStart()), new TimeWithDayAttr(i.getEnd())))
				.collect(Collectors.toList());
				WorkScheduleSaveCommand<T> ws = new WorkScheduleSaveCommand(wsCmd.sid, ymd, workInfor, mapAttendIdWithTime, breakTimeList, null, wsCmd.isBreakByHand());
				rs.add(ws);
			}
		
		return rs;
	}

}
