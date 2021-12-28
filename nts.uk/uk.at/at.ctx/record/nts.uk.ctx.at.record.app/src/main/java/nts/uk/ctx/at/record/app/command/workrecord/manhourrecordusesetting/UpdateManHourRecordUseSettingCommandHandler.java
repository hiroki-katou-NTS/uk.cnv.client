package nts.uk.ctx.at.record.app.command.workrecord.manhourrecordusesetting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ElapsedMonths;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSettingRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ReferenceRange;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmMessage;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
@Transactional
public class UpdateManHourRecordUseSettingCommandHandler extends CommandHandler<ManHourRecordUseSettingCommand>{
	
	@Inject
	ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepository;
	
	@Inject
	ErrorAlarmConditionRepository errorAlarmConditionRepository;
	
	@Inject
	ManHourRecordReferenceSettingRepository manHourRecordReferenceSettingRepository;
	
	@Inject
	ManHrInputUsageSettingRepository manHrInputUsageSettingRepository;

	@SuppressWarnings("unchecked")
	@Override
	protected void handle(CommandHandlerContext<ManHourRecordUseSettingCommand> context) {
		ManHourRecordUseSettingCommand command = context.getCommand();
		String cId = AppContexts.user().companyId();
		
		ManHourRecordReferenceSetting manHourRecordReferenceSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(command.getManHourRecordReferenceSetting().getElapsedMonths(), ElapsedMonths.class),
				EnumAdaptor.valueOf(command.getManHourRecordReferenceSetting().getReferenceRange(), ReferenceRange.class)
				);
		//	ドメインモデル「工数実績参照設定」を更新する
		manHourRecordReferenceSettingRepository.update(manHourRecordReferenceSetting);
		
		ErrorAlarmWorkRecord errorAlarmWorkRecord = errorAlarmWorkRecordRepository.findByCode("T001").get();
		ErrorAlarmCondition errorAlarmCondition = errorAlarmConditionRepository.findConditionByErrorAlamCheckId(errorAlarmWorkRecord.getErrorAlarmCheckID()).get();
		
		errorAlarmWorkRecord.setUseAtr(command.getErrorAlarmWorkRecord().getUseAtr() == 0 ? false : true);
		errorAlarmWorkRecord.setMessage(ErrorAlarmMessage.createFromJavaType(command.getErrorAlarmWorkRecord().getBoldAtr() == 0 ? false : true, 
				command.getErrorAlarmWorkRecord().getMessageColor()));
		
		errorAlarmCondition.setDisplayMessage(command.getErrorAlarmWorkRecord().getDisplayMessage());
		
		ErAlAttendanceItemCondition<CheckedTimeDuration> erAlAttendanceItemCondition = 
				(ErAlAttendanceItemCondition<CheckedTimeDuration>) errorAlarmCondition.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon().get(0);
		erAlAttendanceItemCondition.setCompareRange(erAlAttendanceItemCondition.getCompareRange().getCompareOperator().value, 
				new CheckedTimeDuration(command.getErrorAlarmWorkRecord().getErAlAtdItemConditionGroup1().get(0).getCompareStartValue().intValue()), 
				new CheckedTimeDuration(command.getErrorAlarmWorkRecord().getErAlAtdItemConditionGroup1().get(0).getCompareEndValue().intValue()));
		List<ErAlAttendanceItemCondition<?>> conditionsGroup1 = Arrays.asList(erAlAttendanceItemCondition);
		List<ErAlAttendanceItemCondition<?>> conditionsGroup2 = Arrays.asList(erAlAttendanceItemCondition);
		Integer conditionOperatorGroup1 = errorAlarmCondition.getAtdItemCondition().getGroup1().getConditionOperator().value;
		Integer conditionOperatorGroup2 = errorAlarmCondition.getAtdItemCondition().getGroup2().getConditionOperator().value;
		errorAlarmCondition.createAttendanceItemCondition(errorAlarmCondition.getAtdItemCondition().getOperatorBetweenGroups().value, errorAlarmCondition.getAtdItemCondition().isUseGroup2())
				.setAttendanceItemConditionGroup1(conditionOperatorGroup1, conditionsGroup1)
				.setAttendanceItemConditionGroup2(conditionOperatorGroup2, conditionsGroup2);
		
		//	ドメインモデル「日別実績のエラーアラーム」を更新する
		//	ドメインモデル「勤務実績のエラーアラームチェック」を更新する
		//	ドメインモデル「勤怠項目のエラーアラーム条件」を更新する
		errorAlarmWorkRecordRepository.updateErrorAlarmWorkRecord(errorAlarmWorkRecord, errorAlarmCondition);
		
		Optional<ManHrInputUsageSetting> manHrInput = manHrInputUsageSettingRepository.get(cId);
		
		if(manHrInput.isPresent()) {
			manHrInputUsageSettingRepository.update(new ManHrInputUsageSetting(cId, NotUseAtr.valueOf(command.usrAtr)));
		} else {
			manHrInputUsageSettingRepository.insert(new ManHrInputUsageSetting(cId, NotUseAtr.valueOf(command.usrAtr)));
		}

	}

}
