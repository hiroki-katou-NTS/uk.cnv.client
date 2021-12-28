package nts.uk.screen.at.ws.ktg.ktg005;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.screen.at.app.command.ktg.ktg005.b.RegisterSettingInfoCommand;
import nts.uk.screen.at.app.command.ktg.ktg005.b.RegisterSettingInfoCommandHandler;
import nts.uk.screen.at.app.find.ktg.ktg005.a.ExecutionResultNumberOfApplicationDto;
import nts.uk.screen.at.app.find.ktg.ktg005.a.StartScreenA;
import nts.uk.screen.at.app.find.ktg.ktg005.b.StartScreenB;
import nts.uk.screen.at.app.find.ktg.ktg005.b.StartScreenBResult;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 */
@Path("screen/at/ktg/ktg005")
@Produces("application/json")
public class KTG005WebServices extends WebService {

	@Inject
	private StartScreenA sreenAFinder;

	@Inject
	private StartScreenB sreenBFinder;

	@Inject
	private RegisterSettingInfoCommandHandler regAppSetting;

	/**
	 * 起動する
	 */
	@POST
	@Path("start_screen_a")
	public ExecutionResultNumberOfApplicationDto startScreenA() {
		return this.sreenAFinder.startScreenA(
				AppContexts.user().companyId(), 
				AppContexts.user().employeeId());
	}

	/**
	 * 起動する
	 */
	@POST
	@Path("start_screen_b")
	public StartScreenBResult startScreenB(StartQuery query) {
		return this.sreenBFinder.startScreenB();

	}

	/**
	 * 設定情報を登録する
	 */
	@POST
	@Path("reg_setting_info")
	public void registerSettingInfo(RegisterSettingInfoCommand command) {
		this.regAppSetting.handle(command);

	}

}

@NoArgsConstructor
@AllArgsConstructor
@Data
class StartQuery {
	String companyId;
	String startDate;
	String endDate;
	String employeeId;

	public DatePeriod getPeriod() {
		return new DatePeriod(GeneralDate.fromString(this.startDate, "yyyy/MM/dd"),
				GeneralDate.fromString(this.endDate, "yyyy/MM/dd"));

	}
}
