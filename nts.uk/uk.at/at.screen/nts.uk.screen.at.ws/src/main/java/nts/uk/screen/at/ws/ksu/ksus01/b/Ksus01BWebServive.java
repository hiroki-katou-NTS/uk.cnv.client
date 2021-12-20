package nts.uk.screen.at.ws.ksu.ksus01.b;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.screen.at.app.ksus01.b.GetInforInitialStartupKsus01B;
import nts.uk.screen.at.app.ksus01.b.InforInitialDto;
import nts.uk.screen.at.app.ksus01.b.InforInitialInput;

/**
 * 
 * @author huylq
 *
 */
@Path("screen/at/ksus01/b")
@Produces(MediaType.APPLICATION_JSON)
public class Ksus01BWebServive {
	
	@Inject
	private GetInforInitialStartupKsus01B getInforInitialStartup;

	@POST
	@Path("getinforinitial")
	public InforInitialDto getinforinitial(InforInitialInput input) {
		return getInforInitialStartup.handle(input);
	}
}
