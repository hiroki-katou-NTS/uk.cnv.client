package nts.uk.ctx.at.record.app.command.workrecord.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.CalExeSettingInfor;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.CaseSpecExeContent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.CaseSpecExeContentRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.PartResetClassification;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.SetInforReflAprResult;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.SettingInforForDailyCreation;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.DailyRecreateClassification;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExeStateOfCalAndSum;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;

@Stateless
public class ExecutionLogAssembler {
	
	@Inject 
	CaseSpecExeContentRepository caseSpecExeContentRepository;
	
	public List<ExecutionLog> fromDTO(AddEmpCalSumAndTargetCommand command, String empCalAndSumExecLogID) {
		
		List<ExecutionLog> result = Collections.emptyList();
		
		if (command.getScreen().equals("B")) {
			result = buildExecutionLog(empCalAndSumExecLogID, command);
		} else if (command.getScreen().equals("J")) {
			CaseSpecExeContent caseSpecExeContent = caseSpecExeContentRepository.getCaseSpecExeContentById(command.getCaseSpecExeContentID()).get();
			
			val optDailyCreationSetInfo = caseSpecExeContent.getDailyCreationSetInfo();
			val optDailyCalSetInfo = caseSpecExeContent.getDailyCalSetInfo(); 
			val optReflectApprovalSetInfo = caseSpecExeContent.getReflectApprovalSetInfo();
			val optMonlyAggregationSetInfo = caseSpecExeContent.getMonlyAggregationSetInfo(); 
			if (optDailyCreationSetInfo.isPresent()) {
				val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.DAILY_CREATION, command);
				executionLog.setDailyCreationSetInfo(optDailyCreationSetInfo.get());
				result.add(executionLog);
			}
			if(optDailyCalSetInfo.isPresent()){
				val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.DAILY_CALCULATION, command);
				executionLog.setDailyCalSetInfo(optDailyCalSetInfo.get());
				result.add(executionLog);
			}
			if(optReflectApprovalSetInfo.isPresent()){
				val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.REFLRCT_APPROVAL_RESULT, command);
				executionLog.setReflectApprovalSetInfo(optReflectApprovalSetInfo.get());
				result.add(executionLog);
			}
			if(optMonlyAggregationSetInfo.isPresent()){
				val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.MONTHLY_AGGREGATION, command);
				executionLog.setMonlyAggregationSetInfo(optMonlyAggregationSetInfo.get());
				result.add(executionLog);
			}
		} else {
			throw new BusinessException("Not valid screen");
		}
		
		return result;
	}
	
	/** Build list ExecutionLog for Screen B */
	private List<ExecutionLog> buildExecutionLog(String empCalAndSumExecLogID, AddEmpCalSumAndTargetCommand command) {
		List<ExecutionLog> result = new ArrayList<ExecutionLog>();
		// Create DailyCreationSetInfo
		if (command.isDailyCreation()) {
			ExecutionType executionType = EnumAdaptor.valueOf(command.getCreationType(), ExecutionType.class);
			DailyRecreateClassification creationType = EnumAdaptor.valueOf(command.getResetClass(), DailyRecreateClassification.class);
			// Create PartResetClassification
			Optional<PartResetClassification> getPartResetClassification = Optional.empty();
			if (creationType == DailyRecreateClassification.PARTLY_MODIFIED) {
				getPartResetClassification = Optional.of(new PartResetClassification(
						// masterReconfiguration
						command.isMasterReconfiguration(),
						// closedHolidays
						command.isClosedHolidays(),
						// resettingWorkingHours
						command.isResettingWorkingHours(),
						// reflectsTheNumberOfFingerprintChecks
						command.isRefNumberFingerCheck(),
						// specificDateClassificationResetting
						command.isSpecDateClassReset(),
						// resetTimeAssignment
						command.isResetTimeForAssig(),
						// resetTimeChildOrNurseCare
						command.isResetTimeForChildOrNurseCare(),
						// calculationClassificationResetting
						command.isCalClassReset()));
			}
			
			SettingInforForDailyCreation dailyCreationSetInfo = new SettingInforForDailyCreation(
					// executionContent
					ExecutionContent.DAILY_CREATION,
					// executionType
					executionType, 
					// calExecutionSetInfoID
					IdentifierUtil.randomUniqueId(), 
					// creationType
					creationType, 
					// partResetClassification
					getPartResetClassification);
			val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.DAILY_CREATION, command);
			executionLog.setDailyCreationSetInfo(dailyCreationSetInfo);
			result.add(executionLog);
		}
		// Create DailyCalSetInfo
		if (command.isDailyCalClass()) {
			CalExeSettingInfor dailyCalSetInfo = new CalExeSettingInfor(
					ExecutionContent.DAILY_CALCULATION,
					EnumAdaptor.valueOf(command.getCalClass(), ExecutionType.class), 
				IdentifierUtil.randomUniqueId());
			val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.DAILY_CALCULATION, command);
			executionLog.setDailyCalSetInfo(dailyCalSetInfo);
			result.add(executionLog);
		}
		// Create ReflectApprovalSetInfo
		if (command.isRefApprovalresult()) {
			SetInforReflAprResult reflectApprovalSetInfo = new SetInforReflAprResult(
					ExecutionContent.REFLRCT_APPROVAL_RESULT,
					EnumAdaptor.valueOf(command.getRefClass(), ExecutionType.class), 
					IdentifierUtil.randomUniqueId(),
					command.isAlsoForciblyReflectEvenIfItIsConfirmed());
			val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.REFLRCT_APPROVAL_RESULT, command);
			executionLog.setReflectApprovalSetInfo(reflectApprovalSetInfo);
			result.add(executionLog);
		}
		// Create MonlyAggregationSetInfo
		if (command.isMonthlyAggregation()) {
			CalExeSettingInfor monlyAggregationSetInfo = new CalExeSettingInfor(
					ExecutionContent.MONTHLY_AGGREGATION,
					EnumAdaptor.valueOf(command.getSummaryClass(), ExecutionType.class), 
					IdentifierUtil.randomUniqueId());
			val executionLog = createExecutionLog(empCalAndSumExecLogID, ExecutionContent.MONTHLY_AGGREGATION, command);
			executionLog.setMonlyAggregationSetInfo(monlyAggregationSetInfo);
			result.add(executionLog);
		}
		return result;
	}

	/** Build each ExecutionLog */
	private ExecutionLog createExecutionLog(String empCalAndSumExecLogID, ExecutionContent executionContent, AddEmpCalSumAndTargetCommand command) {
		ExecutionLog executionLog = ExecutionLog.createFromJavaType(
				empCalAndSumExecLogID,
				executionContent.value,
				ErrorPresent.NO_ERROR.value,
				GeneralDateTime.now(), GeneralDateTime.now(),
				ExeStateOfCalAndSum.PROCESSING.value,
				// objectPeriod param Screen C
				GeneralDate.fromString(command.getPeriodStartDate(), "yyyy/MM/dd"),
				GeneralDate.fromString(command.getPeriodEndDate(), "yyyy/MM/dd"),
				Optional.ofNullable(command.getIsCalWhenLock() == null ? null : command.getIsCalWhenLock() ==1 ));
		return executionLog;
	}
	
}
