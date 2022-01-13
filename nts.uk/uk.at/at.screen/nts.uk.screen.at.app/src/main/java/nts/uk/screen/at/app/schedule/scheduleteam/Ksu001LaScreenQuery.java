package nts.uk.screen.at.app.schedule.scheduleteam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.EmpTeamInfor;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.domainservice.GetScheduleTeamInfoService;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;
import nts.uk.shr.com.context.AppContexts;


/**
 * 社員の所属チーム取得
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).L：チーム設定.La:チーム設定.メニュー別OCD.社員の所属チーム取得
 * @author quytb
 *
 */
@Stateless
public class Ksu001LaScreenQuery {

	/**	所属スケジュールチームRepository **/
	@Inject
	private BelongScheduleTeamRepository belongScheduleTeamRepository;

	/**	スケジュールチームRepository **/
	@Inject
	private ScheduleTeamRepository teamRepository;

	public List<EmployeeOrganizationInfoDto> getEmployeesOrganizationInfo(List<String> sids){
		
		if (sids.isEmpty()) {
			return new ArrayList<>();
		}
		
		// 1.取得する(Require, List<社員ID>)
		Require require = new Require();
		List<EmpTeamInfor> empTeamInfors = GetScheduleTeamInfoService.get(require, sids);
		
		return empTeamInfors.stream().map(e -> {
			EmployeeOrganizationInfoDto dto = new EmployeeOrganizationInfoDto();
			dto.setEmployeeId(e.getEmployeeID());
			if(e.getOptScheduleTeamCd().isPresent()) {
				dto.setTeamCd(e.getOptScheduleTeamCd().get().v());
			}
			if(e.getOptScheduleTeamName().isPresent()) {
				dto.setTeamName( e.getOptScheduleTeamName().get().v());
			}
			dto.setBusinessName("");
			dto.setEmployeeCd("");
			return dto;
		}).collect(Collectors.toList());
	}

	@AllArgsConstructor
	private class Require implements GetScheduleTeamInfoService.Require{
		
		@Override
		public List<BelongScheduleTeam> get(List<String> lstEmpId) {
			List<BelongScheduleTeam> belongScheduleTeams = belongScheduleTeamRepository.get(AppContexts.user().companyId(), lstEmpId);
			return belongScheduleTeams;
		}

		@Override
		public List<ScheduleTeam> getAllSchedule(List<String> listWKPGRPID) {
			List<ScheduleTeam> scheduleTeams = teamRepository.getAllSchedule(AppContexts.user().companyId(), listWKPGRPID);
			return scheduleTeams;
		}
	}
}
