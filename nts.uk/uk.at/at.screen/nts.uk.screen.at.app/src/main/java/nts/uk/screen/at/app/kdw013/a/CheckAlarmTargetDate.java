package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.アラーム発生対象日を確認する
 * 
 * @author tutt
 *
 */
@Stateless
public class CheckAlarmTargetDate {

	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepo;

	@Inject
	private ErrorAlarmConditionRepository errorAlarmConditionRepo;

	public void checkAlarm(List<EmployeeDailyPerError> errors) {
		
		// アラーム対象日 = エラー一覧：filter $.勤務実績のエラーアラームコード = 'T001' map $.処理年月日
		List<String> processingDates = errors.stream()
				.filter(f -> f.getErrorAlarmWorkRecordCode().v().equals("T001")).map(m -> m.getDate().toString("YYYY/MM/DD"))
				.collect(Collectors.toList());
		
		if(!processingDates.isEmpty()) {
			
			// 1: get(ログイン会社ID,'T001')
			Optional<ErrorAlarmWorkRecord> errorAlarmWorkRecord =  errorAlarmWorkRecordRepo.findByCode("T001");
			if (errorAlarmWorkRecord.isPresent()) {
				// 2: get(日別実績のエラーアラーム.ID)
				String errorAlarmCheckID = errorAlarmWorkRecord.get().getErrorAlarmCheckID();
				
				Optional<ErrorAlarmCondition> errAlarmCdt = errorAlarmConditionRepo.findConditionByErrorAlamCheckId(errorAlarmCheckID);
				if (errAlarmCdt.isPresent()) {
					String displayMessage = errAlarmCdt.get().getDisplayMessage().v();
					
					String dates = String.join(",", processingDates);
					
					// アラームメッセージ（Msg_2081）を返す
					throw new BusinessException("Msg_2081", dates, displayMessage);
				}
			}
		}

	}
}
