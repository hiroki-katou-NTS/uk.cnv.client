package nts.uk.screen.at.app.schedule.scheduleteam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.EmpTeamInfor;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.GetScheduleTeamInfoService;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.EmployeeInfoData;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.GetAllEmpWhoBelongWorkplaceGroupService;
import nts.uk.ctx.bs.employee.pub.workplace.ResultRequest597Export;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.shr.com.context.AppContexts;


/**
 * 社員の所属チーム取得
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).L：チーム設定.La:チーム設定.メニュー別OCD.社員の所属チーム取得
 * @author quytb
 *
 */
@Stateless
public class Ksu001LaScreenQuery {	
	@Inject 
	private AffWorkplaceGroupRespository repoAffWorkplaceGroup;
	
	@Inject
	private WorkplacePub workplacePub;
	
	/**	所属スケジュールチームRepository **/
	@Inject
	private BelongScheduleTeamRepository belongScheduleTeamRepository;
	
	/**	スケジュールチームRepository **/
	@Inject
	private ScheduleTeamRepository teamRepository;
	
	public List<EmployeeOrganizationInfoDto> getEmployeesOrganizationInfo(GeneralDate baseDate, String WKPGRID){		
		
		List<String> lstEmpId = new ArrayList<String>();
		List<EmpTeamInfor> empTeamInfors  = new ArrayList<>();
		GetAllEmpWhoBelongWorkplaceGroupImpl getAllEmpWhoBelongWorkplaceGroupImpl = new GetAllEmpWhoBelongWorkplaceGroupImpl(repoAffWorkplaceGroup, workplacePub);
		GetScheduleTeamInfoImpl getScheduleTeamInfoImpl = new GetScheduleTeamInfoImpl(belongScheduleTeamRepository, teamRepository);

		/** 1. 取得する(Require, 年月日, 職場グループID): Map<社員ID、社員コード>**/
		List<EmployeeAffiliation> employeeAffiliations = GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(getAllEmpWhoBelongWorkplaceGroupImpl, baseDate, WKPGRID);
		
		if(!CollectionUtil.isEmpty(employeeAffiliations)) {
			lstEmpId = employeeAffiliations.stream().map(x -> x.getEmployeeID()).collect(Collectors.toList());
		} 
//		else {	
//			return null;
//		}
		if(!CollectionUtil.isEmpty(lstEmpId)) {		
			/** 2. 取得する(Require, List<社員ID>): List<社員所属チーム情報> **/
			empTeamInfors = GetScheduleTeamInfoService.get(getScheduleTeamInfoImpl, lstEmpId);			
		}	
				
		List<EmployeeOrganizationInfoDto> teamDetailDtos = employeeAffiliations.stream().map(x -> new EmployeeOrganizationInfoDto(
				x.getEmployeeID(), x.getEmployeeCode().get().v(), x.getBusinessName().get().toString(), "", "")).collect(Collectors.toList());
		for (int i = 0; i< empTeamInfors.size(); i++) {
			EmpTeamInfor empTeamInfor = empTeamInfors.get(i);			
			for(int j = 0; j< teamDetailDtos.size(); j ++) {				
				EmployeeOrganizationInfoDto dto = teamDetailDtos.get(i);
				if(dto.getEmployeeId() == empTeamInfor.getEmployeeID()) {
					if(empTeamInfor.getOptScheduleTeamCd().isPresent()) {
						dto.setTeamCd(empTeamInfor.getOptScheduleTeamCd().get().v());
					}
					if(empTeamInfor.getOptScheduleTeamName().isPresent()) {
						dto.setTeamName( empTeamInfor.getOptScheduleTeamName().get().v());	
					}									
				}				
			}			
		}
		return teamDetailDtos;		
	}
	
	@AllArgsConstructor
	private static class GetAllEmpWhoBelongWorkplaceGroupImpl implements GetAllEmpWhoBelongWorkplaceGroupService.Require{
		@Inject 
		private AffWorkplaceGroupRespository repoAffWorkplaceGroup;
		
		@Inject
		private WorkplacePub workplacePub;
		
		@Override
		public List<String> getWorkplaceBelongsWorkplaceGroup(String workplaceGroupId) {
			String companyId = AppContexts.user().companyId();
			List<String> data = repoAffWorkplaceGroup.getWKPID(companyId, workplaceGroupId);
			return data;		
		}

		@Override
		public List<EmployeeInfoData> getEmployeesWhoBelongWorkplace(String workplaceId, DatePeriod datePeriod) {
			 //Request 597 職場の所属社員を取得する
			List<ResultRequest597Export> data = workplacePub.getLstEmpByWorkplaceIdsAndPeriod(Arrays.asList(workplaceId), datePeriod);
			List<EmployeeInfoData> result = data.stream().map(c -> new EmployeeInfoData(c.getSid(), c.getEmployeeCode(), c.getEmployeeName())).collect(Collectors.toList());
			return result;		
		}		
	}
	
	@AllArgsConstructor
	private static class GetScheduleTeamInfoImpl implements GetScheduleTeamInfoService.Require{	
		/**	所属スケジュールチームRepository **/
		@Inject
		private BelongScheduleTeamRepository belongScheduleTeamRepository;
		
		/**	スケジュールチームRepository **/
		@Inject
		private ScheduleTeamRepository teamRepository;

		@Override
		public List<BelongScheduleTeam> get(List<String> lstEmpId) {
			String companyId = AppContexts.user().companyId();
			List<BelongScheduleTeam> belongScheduleTeams = belongScheduleTeamRepository.get(companyId, lstEmpId);
			return belongScheduleTeams;
		}

		@Override
		public List<ScheduleTeam> getAllSchedule(List<String> listWKPGRPID) {
			String companyId = AppContexts.user().companyId();
			List<ScheduleTeam> scheduleTeams = teamRepository.getAllSchedule(companyId, listWKPGRPID);
			return scheduleTeams;
		}		
	}
}
