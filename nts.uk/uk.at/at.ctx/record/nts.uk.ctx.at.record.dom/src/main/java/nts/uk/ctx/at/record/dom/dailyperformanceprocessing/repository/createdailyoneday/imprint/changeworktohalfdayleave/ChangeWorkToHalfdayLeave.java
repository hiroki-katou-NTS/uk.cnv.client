package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.changeworktohalfdayleave;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 半休により勤務変更する	
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChangeWorkToHalfdayLeave {
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	public List<ErrorMessageInfo> changeWork(Stamp stamp,IntegrationOfDaily integrationOfDaily,
			List<WorkType> listWorkType, ChangeDailyAttendance changeDailyAtt) {
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		//「打刻。勤務種類を半休に変更するを確認する」
		if(!stamp.getType().isChangeHalfDay()) {
			return listErrorMessageInfo;
		}
		//パラメータ。勤務種類一覧を確認する
		if(listWorkType.isEmpty()) {
			//勤務種類マスターを取得する
			listWorkType = workTypeRepository.findByCompanyId(companyId);
		}
		listWorkType = listWorkType.stream().sorted(Comparator.comparing(x -> x.getWorkTypeCode().v()))
				.collect(Collectors.toList());
		Optional<WorkType> workType = Optional.empty();
		// 「打刻。時刻変更区分」を確認する
		if (stamp.getType().getChangeClockArt() == ChangeClockAtr.WORKING_OUT) {
			workType = listWorkType.stream()
					.filter(c -> c.getDailyWork().getMorning() == WorkTypeClassification.Attendance
							&& c.getDailyWork().getAfternoon() == WorkTypeClassification.AnnualHoliday)
					.findFirst();
		} else if (stamp.getType().getChangeClockArt() == ChangeClockAtr.GOING_TO_WORK) {
			workType = listWorkType.stream()
					.filter(c -> c.getDailyWork().getMorning() == WorkTypeClassification.AnnualHoliday
							&& c.getDailyWork().getAfternoon() == WorkTypeClassification.Attendance)
					.findFirst();
		} else {
			return listErrorMessageInfo;
		}
		if(!workType.isPresent()) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(), 
					ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("023"), new ErrMessageContent(TextResource.localize("Msg_1701"))));
			return listErrorMessageInfo;
		}
		//取得できた勤務種類を確認する
		integrationOfDaily.getWorkInformation().getRecordInfo().setWorkTypeCode(workType.get().getWorkTypeCode());
		//日別勤怠の編集状態を追加する
		Optional<EditStateOfDailyAttd> editState = integrationOfDaily.getEditState().stream()
				.filter(c->c.getAttendanceItemId() == 28).findFirst();
		if(!editState.isPresent()) {
			integrationOfDaily.getEditState().add(new EditStateOfDailyAttd(28, EditStateSetting.IMPRINT));
		}
		changeDailyAtt.setCorrectValCopyFromSche(true);
		changeDailyAtt.setWorkInfo(true);
		
		return listErrorMessageInfo;
	}

}
