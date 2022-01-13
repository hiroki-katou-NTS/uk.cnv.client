package nts.uk.ctx.at.record.app.find.manhourrecordusesetting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmWorkRecordDto;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ElapsedMonths;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSettingRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ReferenceRange;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ErrorAlarmClassification;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.RangeCompareType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ManHourRecordUseSettingFinder {
	
	@Inject
	ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepository;
	
	@Inject
	ErrorAlarmConditionRepository errorAlarmConditionRepository;
	
	@Inject
	ManHourRecordReferenceSettingRepository manHourRecordReferenceSettingRepository;
	
	@Inject
	ManHrInputUsageSettingRepository manHrInputUsageSettingRepository;
	
	public ErrorAlarmWorkRecordDto getErrorAlarmWorkRecord() {
		String companyId = AppContexts.user().companyId();
		//ドメインモデル「日別実績のエラーアラーム」を取得する
		Optional<ErrorAlarmWorkRecord> errorAlarmWorkRecordOp = errorAlarmWorkRecordRepository.findByCode("T001");
		
		if (!errorAlarmWorkRecordOp.isPresent()) {
			ErrorAlarmWorkRecord errorAlarmWorkRecord = ErrorAlarmWorkRecord.createFromJavaType(
					companyId,
					"T001",
					"作業時間超過チェック", 
					false,
					false,
					false, 
					833,
					ErrorAlarmClassification.ERROR.value,
					false, 
					"",
					false,
					null, 
					Collections.emptyList(),
					IdentifierUtil.randomUniqueId()
					);
			
			ErrorAlarmCondition errorAlarmCondition = ErrorAlarmCondition.init();
			errorAlarmCondition.setCheckId(errorAlarmWorkRecord.getErrorAlarmCheckID());
			// Set WorkTypeCondition
			errorAlarmCondition.createWorkTypeCondition(false, 0);
			errorAlarmCondition.setWorkTypePlan(false, Collections.emptyList());
			errorAlarmCondition.setWorkTypeActual(false, Collections.emptyList());
			errorAlarmCondition.chooseWorkTypeOperator(0);
			
			// Set WorkTimeCondtion
			errorAlarmCondition.createWorkTimeCondition(false, 0);
			errorAlarmCondition.setWorkTimePlan(false, Collections.emptyList());
			errorAlarmCondition.setWorkTimeActual(false, Collections.emptyList());
			errorAlarmCondition.chooseWorkTimeOperator(0);

			// Set AttendanceItemCondition
			ErAlAttendanceItemCondition<CheckedTimeDuration> erAlAttendanceItemCondition = new ErAlAttendanceItemCondition<CheckedTimeDuration>(
					companyId,
					"T001",
					1,
					1,
					true,
					0
					);
			erAlAttendanceItemCondition.setCountableTarget(Arrays.asList(559), 
					Arrays.asList(
							1305,
							1349,
							1393,
							1437,
							1481,
							1525,
							1569,
							1613,
							1657,
							1701,
							1745,
							1789,
							1833,
							1877,
							1921,
							1965,
							2009,
							2053,
							2097,
							2141
							));
			erAlAttendanceItemCondition.setCompareRange(
					RangeCompareType.OUTSIDE_RANGE_CLOSED.value,
					new CheckedTimeDuration(-1),
					new CheckedTimeDuration(1)
					);
			
			List<ErAlAttendanceItemCondition<?>> conditionsGroup1 = Arrays.asList(erAlAttendanceItemCondition);
			
			List<ErAlAttendanceItemCondition<?>> conditionsGroup2 = Arrays.asList(erAlAttendanceItemCondition);
			errorAlarmCondition.createAttendanceItemCondition(0, false)
					.setAttendanceItemConditionGroup1(0, conditionsGroup1)
					.setAttendanceItemConditionGroup2(0, conditionsGroup2);
			// Set AlCheckTargetCondition
			errorAlarmCondition.createAlCheckTargetCondition(
					false,
					false,
					false,
					false, 
					Collections.emptyList(),
					Collections.emptyList(),
					Collections.emptyList(),
					Collections.emptyList());
			// Set DisplayMessage
			errorAlarmCondition.setDisplayMessage("総労働時間と作業の合計時間に差があります。");
			// Set ContinuousPeriod
//			errorAlarmCondition.setContinuousPeriod(0);
			
			//ドメインモデル「日別実績のエラーアラーム」を新規登録する
			//ドメインモデル「勤務実績のエラーアラームチェック」をを新規登録する
			//ドメインモデル「勤怠項目のエラーアラーム条件」を新規登録する
			errorAlarmWorkRecordRepository.addErrorAlarmWorkRecord(errorAlarmWorkRecord, errorAlarmCondition);
			
			errorAlarmWorkRecordOp = errorAlarmWorkRecordRepository.findByCode("T001");
		}
			
		ErrorAlarmWorkRecord errorAlarmWorkRecord = errorAlarmWorkRecordOp.get();
		
		//ドメインモデル「勤務実績のエラーアラームチェック」を取得する
		//ドメインモデル「勤怠項目のエラーアラーム条件」を取得する
		Optional<ErrorAlarmCondition> errorAlarmCondition = errorAlarmConditionRepository.findConditionByErrorAlamCheckId(errorAlarmWorkRecord.getErrorAlarmCheckID());
		
		//取得したドメインを返す
		return ErrorAlarmWorkRecordDto.fromDomain(errorAlarmWorkRecord, errorAlarmCondition.orElse(null));

	}
	
	public ManHourRecordReferenceSettingDto getManHourRecordReferenceSetting() {
		String companyId = AppContexts.user().companyId();
		//ドメインモデル「工数実績参照設定」を取得する
		Optional<ManHourRecordReferenceSetting> manHourRecordReferenceSettingOp = manHourRecordReferenceSettingRepository.get(companyId);
		
		if (!manHourRecordReferenceSettingOp.isPresent()) {
			ManHourRecordReferenceSetting manHourRecordReferenceSetting = new ManHourRecordReferenceSetting(
					EnumAdaptor.valueOf(0, ElapsedMonths.class),
					EnumAdaptor.valueOf(0, ReferenceRange.class)
					);
			//ドメインモデル「工数実績参照設定」を新規登録する
			manHourRecordReferenceSettingRepository.insert(manHourRecordReferenceSetting);
			
			manHourRecordReferenceSettingOp = manHourRecordReferenceSettingRepository.get(companyId);
		}
		
		ManHourRecordReferenceSetting manHourRecordReferenceSetting = manHourRecordReferenceSettingOp.get();
		//取得したドメインを返す
		return ManHourRecordReferenceSettingDto.fromDomain(manHourRecordReferenceSetting);
	}
	
	public int getArt() {
		String cId = AppContexts.user().companyId();
		Optional<ManHrInputUsageSetting> manHrInputUsageSetting = manHrInputUsageSettingRepository.get(cId);
		return manHrInputUsageSetting.map(m -> m.getUsrAtr().value).orElse(1);
	}

}
