package nts.uk.screen.at.ws.ksu003;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.RegisterWorkScheduleKsu003;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ResultRegisWorkSchedule;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkScheduleParam;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.TargetOrgIdenInforDto;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu003.changeworktype.ChangeWorkTypeDto;
import nts.uk.screen.at.app.ksu003.changeworktype.ChangeWorkTypeSc;
import nts.uk.screen.at.app.ksu003.changeworktype.CheckWorkType;
import nts.uk.screen.at.app.ksu003.changeworktype.CheckWorkTypeDto;
import nts.uk.screen.at.app.ksu003.getempworkfixedworkkinfo.EmpWorkFixedWorkInfoDto;
import nts.uk.screen.at.app.ksu003.getempworkfixedworkkinfo.GetEmpWorkFixedWorkInfoSc;
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
	public ResultRegisWorkSchedule registerWorkSchedule (List<WorkScheduleParam> param){
		ResultRegisWorkSchedule rs = regWorkSchedule.handle(param);
		return rs;
	}
	
	
}
