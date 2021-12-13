package nts.uk.ctx.at.function.ws.alarm.mailsettings;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.alarm.mailsettings.InsertOrUpdateMailSettingCommand;
import nts.uk.ctx.at.function.app.command.alarm.mailsettings.InsertOrUpdateMailSettingCommandHandler;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.AlarmMailSettingDataDto;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.MailSettingFinder;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("at/function/alarm/kal002/emailSetting")
@Produces("application/json")
public class MailSettingByIndividualWS extends WebService {
    @Inject
    private MailSettingFinder finder;

    @Inject
    private InsertOrUpdateMailSettingCommandHandler command;

    @POST
    @Path("init")
    public AlarmMailSettingDataDto getMailSettingInformation() {
        return this.finder.getMailSettingInfo();
    }

    @POST
    @Path("register")
    public void registerOrUpdate(InsertOrUpdateMailSettingCommand command) {
        this.command.handle(command);
    }
}
