package nts.uk.screen.at.app.query.ksu.ksu002.a;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.EmployeeInformationDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.GetStartupProcessingInformationDto;
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
	
	public GetStartupProcessingInformationDto get() {
		GetStartupProcessingInformationDto resutl = new GetStartupProcessingInformationDto();
		
		EmployeeInformationDto informationDto = this.employeeInformation.getEmployeeInfo();
		
		TheInitialDisplayDateDto ym = this.displayDate.getInitialDisplayDate();
		
		resutl.setBusinessName(informationDto.getEmployeeName());
		resutl.setEmployeeCode(informationDto.getEmployeeCd());
		resutl.setEmployeeId(AppContexts.user().employeeId());
		resutl.setYearMonth(ym.getYearMonth());
		
		return resutl;
	}
	
}
