package nts.uk.ctx.at.function.app.command.alarm.sendemail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamAlarmSendEmailCommand {
	private List<String> listEmployeeSendTaget;
	private List<String> listManagerSendTaget;
	private String currentAlarmCode;
	private String processId;
}
