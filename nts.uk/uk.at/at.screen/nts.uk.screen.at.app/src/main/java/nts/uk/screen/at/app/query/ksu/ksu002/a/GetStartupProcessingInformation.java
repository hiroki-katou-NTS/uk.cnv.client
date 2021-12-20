package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyAuthCtrlByPersonRepository;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyAuthCtrlCommonRepository;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControlRepository;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheFunctionControlDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlByPersonDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlCommonDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.EmployeeInformationDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.GetStartupProcessingInformationDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.IndividualDisplayControlDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.TheInitialDisplayDateDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 起動処理情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU002_個人スケジュール修正(個人別).A:メイン画面.メニュー別OCD.起動処理情報を取得する
 * @author chungnt
 *
 */

@Stateless
public class GetStartupProcessingInformation {
	
	@Inject
	private EmployeeInformation employeeInformation;
	
	@Inject
	private TheInitialDisplayDate displayDate;
	
	@Inject
	private ScheModifyAuthCtrlCommonRepository scheModifyAuthCtrlCommonRepository;
	
	@Inject
	private ScheModifyAuthCtrlByPersonRepository scheModifyAuthCtrlByPersonRepository;
	
	@Inject
	private ScheFunctionControlRepository scheFunctionControlRepository;
	
	public GetStartupProcessingInformationDto get() {
		
		EmployeeInformationDto informationDto = this.employeeInformation.getEmployeeInfo();
		
		TheInitialDisplayDateDto ym = this.displayDate.getInitialDisplayDate();
		
		IndividualDisplayControlDto individualDisplayControl = this.getIndividualDisplayControl();
		
		return new GetStartupProcessingInformationDto(individualDisplayControl, AppContexts.user().employeeId(), informationDto.getEmployeeCd(), informationDto.getEmployeeName(), ym.getYearMonth());
	}
	
	/** 個人別の表示制御を取得する */
	public IndividualDisplayControlDto getIndividualDisplayControl() {
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forAttendance();
		
		List<ScheModifyAuthCtrlCommonDto> scheModifyAuthCtrlCommon = scheModifyAuthCtrlCommonRepository.getAllByRoleId(companyId, roleId).stream().map(c->ScheModifyAuthCtrlCommonDto.fromDomain(c)).collect(Collectors.toList());
		
		List<ScheModifyAuthCtrlByPersonDto> scheModifyAuthCtrlByPerson = scheModifyAuthCtrlByPersonRepository.getAllByRoleId(companyId, roleId).stream().map(c->ScheModifyAuthCtrlByPersonDto.fromDomain(c)).collect(Collectors.toList());
		
		ScheFunctionControlDto scheFunctionControl = scheFunctionControlRepository.get(companyId).map(c-> ScheFunctionControlDto.fromDomain(c)).orElse(null);
		
		return new IndividualDisplayControlDto(scheModifyAuthCtrlCommon, scheModifyAuthCtrlByPerson, scheFunctionControl);
				
	} 
}
