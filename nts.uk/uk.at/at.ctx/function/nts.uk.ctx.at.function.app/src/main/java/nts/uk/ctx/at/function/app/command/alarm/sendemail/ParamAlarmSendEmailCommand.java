package nts.uk.ctx.at.function.app.command.alarm.sendemail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.EmployeeSendEmail;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ManagerTagetDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamAlarmSendEmailCommand {
	private List<String> listEmployeeSendTaget;
	private List<String> listManagerSendTaget;
	private String currentAlarmCode;
	private String processId;
	private List<ManagerTagetDto> listManagerSelected;
}
