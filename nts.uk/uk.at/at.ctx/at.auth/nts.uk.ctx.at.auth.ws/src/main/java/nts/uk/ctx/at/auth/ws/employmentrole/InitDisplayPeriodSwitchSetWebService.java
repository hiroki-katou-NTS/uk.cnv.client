package nts.uk.ctx.at.auth.ws.employmentrole;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.ctx.at.auth.app.command.initswitchsetting.DeleteInitDisplayPeriodSwitchSetCmd;
import nts.uk.ctx.at.auth.app.command.initswitchsetting.DeleteInitDisplayPeriodSwitchSetHandler;
import nts.uk.ctx.at.auth.app.command.initswitchsetting.SaveInitDisplayPeriodSwitchSetCmd;
import nts.uk.ctx.at.auth.app.command.initswitchsetting.SaveInitDisplayPeriodSwitchSetCmdHandler;
import nts.uk.ctx.at.auth.app.find.employmentrole.InitDisplayPeriodSwitchSetFinder;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.DateProcessedDto;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.DisplayPeriodDto;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDataDto;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDto;
import nts.uk.shr.com.context.AppContexts;

@Path("at/auth/workplace/initdisplayperiod")
@Produces(MediaType.APPLICATION_JSON)
public class InitDisplayPeriodSwitchSetWebService {
	@Inject
	private InitDisplayPeriodSwitchSetFinder initDisplayFinder;
	
	@Inject
	private SaveInitDisplayPeriodSwitchSetCmdHandler saveInitDisplayCmdHandler;
	
	@Inject
	private DeleteInitDisplayPeriodSwitchSetHandler deleteInitDisplayCmdHandler;
	
	@POST
	@Path("get-request-list-609")
	public DisplayPeriodDto getRq609(){
		InitDisplayPeriodSwitchSetDto data = this.initDisplayFinder.targetDateFromLogin();
		return new DisplayPeriodDto(
				data.getCurrentOrNextMonth(), 
				data.getListDateProcessed().stream().map(x -> new DateProcessedDto(
							x.getClosureID(),
							x.getTargetDate().v(),
							x.getDatePeriod().start().toString(),
							x.getDatePeriod().end().toString()
						)).collect(Collectors.toList()));
	}
	
	@POST
	@Path("/get")
	public InitDisplayPeriodSwitchSetDataDto get(String roleID) {
		String companyID = AppContexts.user().companyId();
		return initDisplayFinder.getInitDisplayPeriodSwitchSetData(companyID, roleID);
	}
	
	@POST
	@Path("/get-by-cid")
	public List<InitDisplayPeriodSwitchSetDataDto> getByCid() {
		String companyID = AppContexts.user().companyId();
		return initDisplayFinder.getInitDisplayPeriodSwitchSetByCid(companyID);
	}
	
	@POST
	@Path("/save")
	public void save(SaveInitDisplayPeriodSwitchSetCmd command) {
		saveInitDisplayCmdHandler.handle(command);
	}
	
	@POST
	@Path("/delete")
	public void delete(DeleteInitDisplayPeriodSwitchSetCmd command) {
		deleteInitDisplayCmdHandler.handle(command);
	}
}
