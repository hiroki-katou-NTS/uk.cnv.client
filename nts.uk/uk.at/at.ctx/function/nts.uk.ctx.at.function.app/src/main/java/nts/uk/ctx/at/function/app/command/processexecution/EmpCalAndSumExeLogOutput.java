package nts.uk.ctx.at.function.app.command.processexecution;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.log.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.log.ExecutionLog;

@Data
@NoArgsConstructor
public class EmpCalAndSumExeLogOutput {

	private EmpCalAndSumExeLog empCalAndSumExeLog;

	private List<ExecutionLog> executionLogs;
	
	public void addExecutionLog(ExecutionLog executionLog){
		this.executionLogs.add(executionLog);
	}
}
