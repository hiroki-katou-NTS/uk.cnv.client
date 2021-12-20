package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.command.workrecord.workrecord.AddAttendanceTimeZoneCommand;
import nts.uk.ctx.at.record.app.command.workrecord.workrecord.RegisterWorkManHoursCommandHandler;
import nts.uk.ctx.at.record.app.command.workrecord.workrecord.WorkDetail;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.daily.ouen.TimeZone;
import nts.uk.ctx.at.record.dom.daily.ouen.WorkDetailsParam;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.output.ExecutionAttr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.CreateDailyResultDomainServiceNew;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkinputRemarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.作業内容を登録する
 * 
 * @author tutt
 *
 */
@Stateless
public class RegisterWorkContentHandler {

	@Inject
	private RegisterWorkManHoursCommandHandler handler;

	@Inject
	private GetTargetTime getTargetTime;
	
	@Inject
	private CheckAlarmTargetDate checkAlarmTargetDate;

	@Inject
	private CreateDailyResultDomainServiceNew createDailyResultDomainServiceNew;

	public RegisterWorkContentDto registerWorkContent(RegisterWorkContentCommand command) {

		RegisterWorkContentDto registerWorkContentDto = new RegisterWorkContentDto();
		
		// 1: <call>() 日別実績の作成
		OutputCreateDailyResult outputCreateDailyOneDay = createDailyResultDomainServiceNew.createDataNewNotAsync(
				command.getEmployeeId(), 
				new DatePeriod(command.getChangedDate(), 
				command.getChangedDate()), 
				ExecutionAttr.MANUAL, 
				AppContexts.user().companyId(),
				ExecutionTypeDaily.CREATE, 
				Optional.empty(), Optional.empty());

		// 変更があった日付分をループする
		List<ErrorMessageInfoDto> lstErrorMessageInfoDto = new ArrayList<>();

		if (outputCreateDailyOneDay != null) {
			lstErrorMessageInfoDto = outputCreateDailyOneDay.getListErrorMessageInfo().stream()
					.map(m -> ErrorMessageInfoDto.toDto(m)).collect(Collectors.toList());
		}

		// 2: 登録する(対象者, 編集状態, 作業詳細リスト):List<日別勤怠(Work)>
		// 応援作業別勤怠時間帯を登録する
		AddAttendanceTimeZoneCommand param = new AddAttendanceTimeZoneCommand();

		param.setEmployeeId(command.getEmployeeId());
		param.setEditStateSetting(EnumAdaptor.valueOf(command.getEditStateSetting(), EditStateSetting.class));

		List<WorkDetailCommand> lstWorkDetailCmd = command.getWorkDetails();
		
		List<WorkDetail> workDetails = lstWorkDetailCmd.stream()
				.map(workDetail -> {
					 AtomicInteger count = new AtomicInteger(1);
					return new WorkDetail(workDetail.getDate(),
							
							workDetail.getLstWorkDetailsParamCommand().stream()
									.map(workDetailParam -> new WorkDetailsParam(new SupportFrameNo(getSupNo(workDetail.getLstWorkDetailsParamCommand(),workDetailParam.getSupportFrameNo(),count)
											),
											new TimeZone(
													new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.HAND_CORRECTION_PERSON, Optional.empty()),
															new TimeWithDayAttr(workDetailParam.getTimeZone().getStart())),
													new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.HAND_CORRECTION_PERSON, Optional.empty()),
															new TimeWithDayAttr(workDetailParam.getTimeZone().getEnd())),
													Optional.empty()),
											Optional.ofNullable(WorkGroup.create(workDetailParam.getWorkGroup().getWorkCD1(),
													workDetailParam.getWorkGroup().getWorkCD2(), workDetailParam.getWorkGroup().getWorkCD3(),
													workDetailParam.getWorkGroup().getWorkCD4(), workDetailParam.getWorkGroup().getWorkCD5())),
											Optional.ofNullable(new WorkinputRemarks(workDetailParam.getRemarks())),
											Optional.ofNullable(workDetailParam.getWorkLocationCD() == null ? null
													: new WorkLocationCD(workDetailParam.getWorkLocationCD()))))
									.collect(Collectors.toList()));
					
				})
				.collect(Collectors.toList());

		param.setWorkDetails(workDetails);

		List<IntegrationOfDaily> integrationOfDailyList = handler.handle(param);
		
		//3: <call> List<日別勤怠(Work)>.エラー一覧
		List<EmployeeDailyPerError> employeeError = new ArrayList<>();
		
		for (IntegrationOfDaily i : integrationOfDailyList ) {
			for (EmployeeDailyPerError e : i.getEmployeeError()) {
				employeeError.add(e);
			}
		}
		
		if(!employeeError.isEmpty()) {
			checkAlarmTargetDate.checkAlarm(employeeError);
		}
		
		//4: [List<日別勤怠(Work)>.isPresent]:<call>(対象者,画面モードList<日別勤怠(Work)>)
		// 残業申請・休出時間申請の対象時間を取得する
		//5: [List<残業休出時間>.isPresent]:<call>
		List<OvertimeLeaveTime> lstOvertimeLeaveTime = new ArrayList<>();
		
		if (!integrationOfDailyList.isEmpty()) {
			lstOvertimeLeaveTime = getTargetTime.get(command.getEmployeeId(), command.getMode(), integrationOfDailyList);
		}
		
		registerWorkContentDto.setLstErrorMessageInfo(lstErrorMessageInfoDto);
		registerWorkContentDto.setLstOvertimeLeaveTime(lstOvertimeLeaveTime);
		
		return registerWorkContentDto;
	}

	private Integer getSupNo(List<WorkDetailsParamCommand> lstWorks, int currentNo, AtomicInteger count) {

		Integer result = 0;
		if (currentNo != 0) {
			return currentNo;
		}

		List<Integer> noLst = lstWorks.stream().filter(x -> x.getSupportFrameNo() != 0).map(x -> x.getSupportFrameNo())
				.collect(Collectors.toList());

		if (noLst.isEmpty()) {
			return count.getAndIncrement();
		} else {
			for (int i = 0; i < noLst.size(); i++) {
				Integer newNo =  noLst.get(i) + 1;
				if (noLst.indexOf(newNo) == -1) {
					if(count.get() == 1){
						count.set(newNo);
					}
					result = count.getAndIncrement();
				}
			}
		}
		return result;
	}
}
