package nts.uk.screen.at.ws.kdp.kdp004.a;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.kdp.kdp004.a.RegisterFingerStampCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdp004.a.RegisterFingerStampCommandHandler;
import nts.uk.screen.at.app.query.kdp.kdp004.a.GetFingerStampSettingDto;
import nts.uk.screen.at.app.query.kdp.kdp004.a.GetFingerStampSetting;

@Path("at/record/stamp/finger")
@Produces("application/json")
public class FingerStampWebService extends WebService {

	@Inject
	private GetFingerStampSetting finder;

	@Inject
	private RegisterFingerStampCommandHandler commandHanler;

	@POST
	@Path("get-finger-stamp-setting")
	public GetFingerStampSettingDto getFingerStampSetting() {
		return this.finder.getFingerStampSetting();
	}

	@POST
	@Path("register-finger-stamp")
	public GeneralDate registerFingerStamp(RegisterFingerStampCommand command) {
		return this.commandHanler.handle(command);
	}
}
