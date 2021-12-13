package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.workschedulereflected;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workschedule.BreakTimeOfDailyAttdImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.TimeLeavingWorkImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.snapshot.DailySnapshotWorkAdapter;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.worktime.common.AbolishAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 勤務予定反映
 * @author tutk
 *
 */
@Stateless
public class WorkScheduleReflected {
	@Inject
	private WorkScheduleWorkInforAdapter workScheduleWorkInforAdapter;
	
	@Inject
	private WorkTypeRepository wkTypeRepo;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private DailySnapshotWorkAdapter snapshotAdapter;

	public List<ErrorMessageInfo> workScheduleReflected(String companyId, IntegrationOfDaily integrationOfDaily) {
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();
		String employeeId = integrationOfDaily.getEmployeeId();
		GeneralDate ymd = integrationOfDaily.getYmd();
		//「勤務予定」ドメインを取得する
		Optional<WorkScheduleWorkInforImport> scheduleWorkInfor = workScheduleWorkInforAdapter.get(employeeId, ymd);
		if (!scheduleWorkInfor.isPresent() || scheduleWorkInfor.get().getWorkType() == null) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, ymd, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("006"), new ErrMessageContent(TextResource.localize("Msg_431"))));
			return listErrorMessageInfo;
		}
		WorkInfoOfDailyAttendance workInformation = integrationOfDaily.getWorkInformation();
		WorkInformation wi =  scheduleWorkInfor.map(m -> new WorkInformation(m.getWorkType(), m.getWorkTime())).orElse(null);
		
		//勤務情報をコピーする (Copy thông tin 勤務)
		workInformation.setRecordInfo(wi);
		workInformation.setGoStraightAtr(EnumAdaptor.valueOf(scheduleWorkInfor.get().getGoStraightAtr(),NotUseAttribute.class));
		workInformation.setBackStraightAtr(EnumAdaptor.valueOf(scheduleWorkInfor.get().getBackStraightAtr(),NotUseAttribute.class));
		
		/** スナップショットを作成する */
		createSnapshot(integrationOfDaily);
		
		//計算状態を未計算にする
		workInformation.setCalculationState(CalculationState.No_Calculated);
		
		//振休振出として扱う日数をコピーするかを確認する
		if (scheduleWorkInfor.get().getNumberOfDaySuspension().isPresent()
				&& scheduleWorkInfor.get().getNumberOfDaySuspension().get()
						.getDays() != 0
				&& workInformation.getNumberDaySuspension().isPresent()
				&& workInformation.getNumberDaySuspension().get().getDays().v() == 0) {
			//振休振出として扱う日数をコピーする
			workInformation.setNumberDaySuspension(Optional.of(new NumberOfDaySuspension(
					new UsedDays(scheduleWorkInfor.get().getNumberOfDaySuspension().get().getDays()),
					scheduleWorkInfor.get().getNumberOfDaySuspension().get().getClassifiction() == 0? FuriClassifi.SUSPENSION:FuriClassifi.DRAWER
					))); 
		}
		
		//ドメインモデル「勤務種類」を取得する-(Lấy domain 「WorkType」) // trả về list 1 phần tử or empty
		List<WorkType> wkTypeOpt = wkTypeRepo.findAllByListCode(companyId,
				Arrays.asList(workInformation.getRecordInfo().getWorkTypeCode().v()));
		if(wkTypeOpt.isEmpty()) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, ymd, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("015"), new ErrMessageContent(TextResource.localize("Msg_590"))));
			return listErrorMessageInfo;
		}
		//1日半日出勤・1日休日系の判定
		WorkStyle workStyle =  wkTypeOpt.get(0).checkWorkDay();
		if(workStyle == WorkStyle.ONE_DAY_REST) {
//			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, ymd, ExecutionContent.DAILY_CREATION,
//					new ErrMessageResource("016"), new ErrMessageContent(TextResource.localize("Msg_591"))));
			return listErrorMessageInfo;
		}
		//ドメインモデル「就業時間帯の設定」を取得
		Optional<WorkTimeSetting> workTimeOpt = this.workTimeSettingRepository
				.findByCodeAndAbolishCondition(companyId, workInformation
						.getRecordInfo().getWorkTimeCode().v(), AbolishAtr.NOT_ABOLISH);
		//取得できなかった場合
		//勤務予定の出退勤を確認する
		if(!workTimeOpt.isPresent() || !scheduleWorkInfor.get().getTimeLeaving().isPresent() ) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, ymd, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("016"), new ErrMessageContent(TextResource.localize("Msg_591"))));
			return listErrorMessageInfo;
		}
		// 予定時間帯をコピーする(Copy 予定時間帯) (lấy từ Stamp chứ kp actualStamp, do bên schedule k có actualStamp)
		List<ScheduleTimeSheet> listScheduleTimeSheet = new ArrayList<>();
		for (TimeLeavingWorkImport timeLeavingWorkImport : scheduleWorkInfor.get().getTimeLeaving().get()
				.getTimeLeavingWorks()) {
			listScheduleTimeSheet.add(
				new ScheduleTimeSheet(
					timeLeavingWorkImport.getWorkNo(),
					timeLeavingWorkImport.getAttendanceStamp().get().getStamp().isPresent() 
					?timeLeavingWorkImport.getAttendanceStamp().get().getStamp().get().getTimeDay()
							.getTimeWithDay():0,
					timeLeavingWorkImport.getLeaveStamp().get().getStamp().isPresent()?
					timeLeavingWorkImport.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay():0
				)
			);
		}
		workInformation.setScheduleTimeSheets(listScheduleTimeSheet);
		
		//予定休憩時間をコピーする
		BreakTimeOfDailyAttdImport breakTimeWorkSchedule = scheduleWorkInfor.get().getBreakTime();
		
		BreakTimeOfDailyAttd breakTime = new BreakTimeOfDailyAttd(
								breakTimeWorkSchedule.getBreakTimeSheets().stream().map(
									x -> new BreakTimeSheet(
										new BreakFrameNo(x.getBreakFrameNo()), 
										new TimeWithDayAttr(x.getStartTime()),
										new TimeWithDayAttr(x.getEndTime())
									)
							).collect(Collectors.toList()));
		integrationOfDaily.setBreakTime(breakTime);

		return listErrorMessageInfo;
		
	}
	
	/** スナップショットを作成する */
	private void createSnapshot(IntegrationOfDaily integrationOfDaily) {
		
		/** スナップショットを取得する */
		val oldSnapshot = this.snapshotAdapter.find(integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd());
		
		if (!oldSnapshot.isPresent()) {

			/** スナップショットを作成する */
			snapshotAdapter.createFromSchedule(integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd())
				.ifPresent(ss -> {
					
					integrationOfDaily.setSnapshot(ss.getSnapshot().toDomain());
				});
		}
	}

}
