package nts.uk.screen.at.ws.ksu003;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.val;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.Range;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.AddScheduleByDisplaySettingCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.AddScheduleByDisplaySettingCommandHandler;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.AddWorkScheduleCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.AddWorkScheduleCommandHandler;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.RegisterWorkScheduleKsu003;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ResultRegisWorkSchedule;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.TaskScheduleDetailDto;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.TaskScheduleDetailEmp;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.TimeSpanForCalcDto;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkScheduleParam;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.GetTaskPaletteDisplayInfor;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.GetTaskPalletQuery;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.TaskPaletteDto;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.WorkPaletteDisplayInforDto;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.TargetOrgIdenInforDto;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu003.changeworktype.ChangeWorkTypeDto;
import nts.uk.screen.at.app.ksu003.changeworktype.ChangeWorkTypeSc;
import nts.uk.screen.at.app.ksu003.changeworktype.CheckWorkType;
import nts.uk.screen.at.app.ksu003.changeworktype.CheckWorkTypeDto;
import nts.uk.screen.at.app.ksu003.d.GetInfoInitScreenKsu003D;
import nts.uk.screen.at.app.ksu003.d.InitKsu003DInfoDto;
import nts.uk.screen.at.app.ksu003.checkempattendancesystem.CheckEmpAttParam;
import nts.uk.screen.at.app.ksu003.checkempattendancesystem.CheckEmpAttendanceSystem;
import nts.uk.screen.at.app.ksu003.getempworkfixedworkkinfo.EmpWorkFixedWorkInfoDto;
import nts.uk.screen.at.app.ksu003.getempworkfixedworkkinfo.GetEmpWorkFixedWorkInfoSc;
import nts.uk.screen.at.app.ksu003.getlistempworkhours.EmpTaskInfoDto;
import nts.uk.screen.at.app.ksu003.getworkscheduleinfor.GetWorkScheduleInfor;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.GetTaskParam;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.GetWorkSelectionInfor;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.GetWorkSelectionInforDto;
import nts.uk.screen.at.app.ksu003.sortemployee.SortEmployeeParam;
import nts.uk.screen.at.app.ksu003.sortemployee.SortEmployeesSc;
import nts.uk.screen.at.app.ksu003.start.DisplayWorkInfoByDateSc;
import nts.uk.screen.at.app.ksu003.start.GetFixedWorkInformation;
import nts.uk.screen.at.app.ksu003.start.GetInfoInitStartKsu003;
import nts.uk.screen.at.app.ksu003.start.dto.DisplayWorkInfoByDateDto;
import nts.uk.screen.at.app.ksu003.start.dto.DisplayWorkInfoParam;
import nts.uk.screen.at.app.ksu003.start.dto.FixedWorkInformationDto;
import nts.uk.screen.at.app.ksu003.start.dto.GetInfoInitStartKsu003Dto;
import nts.uk.screen.at.app.ksu003.start.dto.WorkInforDto;

/**
 * 
 * @author phongtq
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Path("screen/at/schedule")
@Produces("application/json")
public class KSU003WebService extends WebService{
	@Inject
	private GetInfoInitStartKsu003 infoInitStartKsu003;
	
	@Inject
	private GetFixedWorkInformation fixedWorkInformation;
	
	@Inject
	private DisplayWorkInfoByDateSc displayWorkInfoByDateSc;
	
	@Inject
	private SortEmployeesSc sortEmployeesSc;
	
	@Inject
	private GetEmpWorkFixedWorkInfoSc fixedWorkInfoSc;
	
	@Inject
	private CheckWorkType checkWorkType;
	
	@Inject 
	private ChangeWorkTypeSc changeWorkType;
	
	@Inject
	private RegisterWorkScheduleKsu003 regWorkSchedule;

	@Inject
	private GetInfoInitScreenKsu003D getInfoInitScreenKsu003D;
	
	@Inject 
	private GetWorkSelectionInfor selectTaskInfor;
	
	@Inject
	private GetTaskPalletQuery taskPalletQuery;
	
	@Inject
	private GetTaskPaletteDisplayInfor taskPaletteDisplay;
	
	@Inject
	private CheckEmpAttendanceSystem checkEmpAttendance;
	
	@Inject
	private GetWorkScheduleInfor workScheduleInfor;
	
	@Inject
	private AddWorkScheduleCommandHandler commandHandler;
	
	@Inject
	private AddScheduleByDisplaySettingCommandHandler settingCommandHandler;
	
	@POST
	@Path("getinfo-initstart")
	// 初期起動の情報取得
	public GetInfoInitStartKsu003Dto getDataStartScreen(TargetOrgIdenInforDto targetOrgDto){
		TargetOrgIdenInfor targetOrg = new TargetOrgIdenInfor(TargetOrganizationUnit.valueOf(targetOrgDto.getUnit()), 
				Optional.ofNullable(targetOrgDto.getWorkplaceId()), Optional.ofNullable(targetOrgDto.getWorkplaceGroupId()));
		GetInfoInitStartKsu003Dto data = infoInitStartKsu003.getData(targetOrg);
		return data;
	}
	
	@POST
	@Path("getfixedworkinfo")
	// 勤務固定情報を取得する
	public FixedWorkInformationDto getFixedWorkInformation(List<WorkInforDto> information){
		List<WorkInformation> workInformation = information.stream().map(mapper -> new WorkInformation(mapper.getWorkTypeCode(), mapper.getWorkTimeCode())).collect(Collectors.toList());
		FixedWorkInformationDto data = fixedWorkInformation.getFixedWorkInfo(workInformation);
		return data;
	}
	
	@POST
	@Path("displayDataKsu003")
	// 日付別勤務情報で表示する
	public List<DisplayWorkInfoByDateDto> displayDataKsu003(DisplayWorkInfoParam param){
		List<DisplayWorkInfoByDateDto> data = displayWorkInfoByDateSc.displayDataKsu003(param);
		return data;
	}
	
	@POST
	@Path("sortEmployee")
	// 社員を並び替える
	public List<String> sortEmployee(SortEmployeeParam param){
		List<String> data = sortEmployeesSc.sortEmployee(param);
		return data;
	}
	
	@POST
	@Path("getEmpWorkFixedWorkInfo")
	// 社員勤務予定と勤務固定情報を取得する
	public EmpWorkFixedWorkInfoDto getEmpWorkFixedWorkInfo(List<WorkInforDto> information){
		List<WorkInformation> workInformation = information.stream().map(mapper -> new WorkInformation(mapper.getWorkTypeCode(), mapper.getWorkTimeCode())).collect(Collectors.toList());
		EmpWorkFixedWorkInfoDto data = fixedWorkInfoSc.getEmpWorkFixedWorkInfo(workInformation);
		return data;
	}
	
	@POST
	@Path("changeWorkType")
	public ChangeWorkTypeDto changeWorkType(WorkInforDto information){
		WorkInformation workInformation = new WorkInformation(information.getWorkTypeCode(), information.getWorkTimeCode());
		ChangeWorkTypeDto data = changeWorkType.changeWorkType(workInformation);
		return data;
	}
	
	@POST
	@Path("checkWorkType")
	// 勤務種類を変更する
	public CheckWorkTypeDto checkWorkType(WorkInforDto information){
		String workTimeSetting = checkWorkType.checkWorkType(information.getWorkTypeCode());
		CheckWorkTypeDto dto = new CheckWorkTypeDto(workTimeSetting); 
		return dto;
	}
	
	@POST
	@Path("registerKSU003")
	// 勤務予定を登録する
	public ExecutionInfor registerWorkSchedule (List<WorkScheduleParam> param){
		ExecutionInfor rs = regWorkSchedule.handle(param);
		return rs;
	}
	
	@POST
	@Path("getTaskInfo")
	// 作業選択準備情報を取得する
	public GetWorkSelectionInforDto getTaskInfo (GetTaskParam param){
		GetWorkSelectionInforDto rs = selectTaskInfor.get(GeneralDate.fromString(param.getBaseDate(), "yyyy/MM/dd"), param.getTargetUnit(), param.getPage(), param.getOrganizationID());
		return rs;
	}
	
	@POST
	@Path("getTaskPallet")
	// 作業パレットを取得する
	public TaskPaletteDto getTaskPallet (GetTaskParam param){
		TaskPaletteDto rs = taskPalletQuery.get(GeneralDate.fromString(param.getBaseDate(), "yyyy/MM/dd"), param.getPage(), param.getTargetUnit(),  param.getOrganizationID());
		return rs;
	}

	@POST
	@Path("ksu003/d/init")
	// 画面初期起動
	public InitKsu003DInfoDto getDataStartScreenD () {
		return getInfoInitScreenKsu003D.getData();
	}
	@POST
	@Path("getTaskPaletteDisplay")
	// 作業パレット表示情報を取得する taskPaletteDisplay
	public WorkPaletteDisplayInforDto getTaskPaletteDisplay (GetTaskParam param){
		WorkPaletteDisplayInforDto rs = taskPaletteDisplay.get(GeneralDate.fromString(param.getBaseDate(), "yyyy/MM/dd"),param.getPage(), param.getTargetUnit(),  param.getOrganizationID());
		return rs;
	}
	@POST
	@Path("checkEmpAttendance")
	// 社員の出勤系をチェックする 
	public List<String> checkEmpAttendance (CheckEmpAttParam param){
		DatePeriod date = new DatePeriod(GeneralDate.fromString(param.getStartDate(), "yyyy/MM/dd"), GeneralDate.fromString(param.getEndDate(), "yyyy/MM/dd"));
		List<String> rs = checkEmpAttendance.get(param.getLstEmpId(), date, param.getDisplayMode());
		return rs;
	}
	
	@POST
	@Path("getTaskWorkSchedule")
	// 作業予定情報を取得する 
	public List<EmpTaskInfoDto> getTaskWorkSchedule (CheckEmpAttParam param){
		DatePeriod date = new DatePeriod(GeneralDate.fromString(param.getStartDate(), "yyyy/MM/dd"), GeneralDate.fromString(param.getEndDate(), "yyyy/MM/dd"));
		List<EmpTaskInfoDto> rs = workScheduleInfor.get(param.getLstEmpId(), date);
		return rs;
	}
	
	
	@POST
	@Path("addTaskWorkSchedule")
	// 作業予定を登録する
	public void addTaskPaletteDisplay (AddWorkScheduleCommand param){
		//param.setLstTaskScheduleDetailEmp(this.checkDuplicate(param.getLstTaskScheduleDetailEmp(), param.getLstTaskScheduleDetailEmp().stream().collect(Collectors.toList())));
		commandHandler.handle(param);
	}
	
	@POST
	@Path("addScheduleByDisplaySet")
	// 組織別スケジュール修正日付別の表示設定を登録する  
	public void addScheduleByDisplaySet (AddScheduleByDisplaySettingCommand param){
		settingCommandHandler.handle(param);
	}
}
