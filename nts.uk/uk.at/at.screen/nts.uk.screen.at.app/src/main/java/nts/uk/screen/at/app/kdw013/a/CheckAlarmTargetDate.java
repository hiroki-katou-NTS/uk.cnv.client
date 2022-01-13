package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;

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
	
	@Inject
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepository;

	public List<BusinessException> checkAlarm(String sId, DatePeriod datePeriod) {

		// イン会社ID,対象社員,対象日リスト,'T001'
		List<String> des = this.employeeDailyPerErrorRepository.findByPeriodOrderByYmd(sId, datePeriod).stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().equals("T001"))
				.map(x -> x.getDate().toString("yyyy/MM/dd")).collect(Collectors.toList());
		
		List<BusinessException> error = new ArrayList<BusinessException>();

		if (!des.isEmpty()) {
			// ログイン会社ID,'T001'
			this.errorAlarmWorkRecordRepo.findByCode("T001").ifPresent(e -> {

				this.errorAlarmConditionRepo.findConditionByErrorAlamCheckId(e.getErrorAlarmCheckID()).ifPresent(ea -> {

					String displayMessage = ea.getDisplayMessage().v();

					// 日別実績のエラーアラーム.ID
					String dateStr = String.join(",", des);

					// アラームメッセージ（Msg_2081）を返す
					error.add(new BusinessException("Msg_2081", dateStr, displayMessage));
				});

			});

		}
		
		return error;

	}
}
