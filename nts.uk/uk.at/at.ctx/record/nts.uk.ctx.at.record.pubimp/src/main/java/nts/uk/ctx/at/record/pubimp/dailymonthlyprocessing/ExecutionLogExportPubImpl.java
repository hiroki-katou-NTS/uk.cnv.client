package nts.uk.ctx.at.record.pubimp.dailymonthlyprocessing;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.SetInforReflAprResult;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPersonRepository;
import nts.uk.ctx.at.record.pub.dailymonthlyprocessing.ExeStateOfCalAndSumExport;
import nts.uk.ctx.at.record.pub.dailymonthlyprocessing.ExecutionContentExport;
import nts.uk.ctx.at.record.pub.dailymonthlyprocessing.ExecutionLogExportPub;
import nts.uk.ctx.at.record.pub.dailymonthlyprocessing.ExecutionTypeExport;
import nts.uk.ctx.at.record.pub.dailymonthlyprocessing.SetInforReflAprResultExport;

@Stateless
public class ExecutionLogExportPubImpl implements ExecutionLogExportPub{
	
	@Inject
	private ExecutionLogRepository logRepo;
	@Inject
	private EmpCalAndSumExeLogRepository calAndSumLog;
	@Inject
	private TargetPersonRepository targetPerson;
	@Override
	public Optional<SetInforReflAprResultExport> optReflectResult(String empCalAndSumExecLogID, int executionContent) {
		Optional<ExecutionLog> optExeLog = logRepo.getByExecutionContent(empCalAndSumExecLogID, executionContent);
		if(!optExeLog.isPresent()) {
			return Optional.empty();
		}
		ExecutionLog exeLog = optExeLog.get();
		Optional<SetInforReflAprResult> reflectApprovalSetInfo = exeLog.getReflectApprovalSetInfo();
		if(!reflectApprovalSetInfo.isPresent()) {
			return Optional.empty();
		}
		SetInforReflAprResult refInfo = reflectApprovalSetInfo.get();
		SetInforReflAprResultExport output = new SetInforReflAprResultExport(EnumAdaptor.valueOf(refInfo.getExecutionContent().value, ExecutionContentExport.class),
				EnumAdaptor.valueOf(refInfo.getExecutionType().value, ExecutionTypeExport.class),
				refInfo.getCalExecutionSetInfoID(),
				refInfo.isForciblyReflect());
		return Optional.of(output);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<ExeStateOfCalAndSumExport> executionStatus(String empCalAndSumExecLogID) {
		Optional<EmpCalAndSumExeLog> optCalAndSumLog = calAndSumLog.getByEmpCalAndSumExecLogIDByJDBC(empCalAndSumExecLogID);
		if(!optCalAndSumLog.isPresent()
				|| !optCalAndSumLog.get().getExecutionStatus().isPresent()) {
			return Optional.empty();
		}
		ExeStateOfCalAndSumExport outData = EnumAdaptor.valueOf(optCalAndSumLog.get().getExecutionStatus().get().value, 
				ExeStateOfCalAndSumExport.class);
		return Optional.of(outData);
	}
	@Override
	public void updateLogInfo(String employeeID, String empCalAndSumExecLogId, int executionContent, int state) {
		targetPerson.updateWithContent(employeeID, empCalAndSumExecLogId, executionContent, state);
	}
	@Override
	public void updateLogInfo(String empCalAndSumExecLogID, int executionContent, int processStatus) {
		logRepo.updateLogInfo(empCalAndSumExecLogID, executionContent, processStatus);
		
	}

	@Override
	public boolean isCalWhenLock(String empCalAndSumExecLogId,int executionContent) {
		Optional<ExecutionLog> optExeLog = logRepo.getByExecutionContent(empCalAndSumExecLogId, executionContent);

		return optExeLog.flatMap(c -> c.getIsCalWhenLock()).orElse(false);
	}

}
