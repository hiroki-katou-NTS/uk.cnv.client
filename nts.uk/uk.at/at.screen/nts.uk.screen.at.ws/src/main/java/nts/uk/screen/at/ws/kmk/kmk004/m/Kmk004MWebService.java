package nts.uk.screen.at.ws.kmk.kmk004.m;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.screen.at.app.command.kmk.kmk004.m.DeleteTransMonthlyWorkTimeSetWkpCommand;
import nts.uk.screen.at.app.command.kmk.kmk004.m.DeleteTransMonthlyWorkTimeSetWkpCommandHandler;
import nts.uk.screen.at.app.command.kmk.kmk004.m.RegisterTransMonthlyWorkTimeSetWkpCommandHandler;
import nts.uk.screen.at.app.command.kmk.kmk004.m.UpdateTransMonthlyWorkTimeSetWkpCommandHandler;
import nts.uk.screen.at.app.command.kmk.kmk004.monthlyworktimesetwkp.SaveMonthlyWorkTimeSetWkpCommand;
import nts.uk.screen.at.app.query.kmk004.common.YearDto;
import nts.uk.screen.at.app.query.kmk004.common.YearlyListByWorkplace;

/**
 * 
 * @author tutt
 *
 */
@Path("screen/at/kmk004")
@Produces("application/json")
public class Kmk004MWebService {

	@Inject
	private YearlyListByWorkplace yearlyListByWorkplace;

	@Inject
	private RegisterTransMonthlyWorkTimeSetWkpCommandHandler registerHandler;

	@Inject
	private UpdateTransMonthlyWorkTimeSetWkpCommandHandler updateHandler;

	@Inject
	private DeleteTransMonthlyWorkTimeSetWkpCommandHandler deleteHandler;

	@POST
	@Path("viewM/getListYear/{wkpId}")
	public List<YearDto> getWkpYearList(@PathParam("wkpId") String wkpId) {
		return yearlyListByWorkplace.get(wkpId, LaborWorkTypeAttr.DEFOR_LABOR);
	}

	@POST
	@Path("viewM/monthlyWorkTimeSet/add")
	public void registerMonthlyWorkTimeSet(CommandHandlerContext<SaveMonthlyWorkTimeSetWkpCommand> command) {
		registerHandler.handle(command);
	}

	@POST
	@Path("viewM/monthlyWorkTimeSet/update")
	public void updateMonthlyWorkTimeSet(CommandHandlerContext<SaveMonthlyWorkTimeSetWkpCommand> command) {
		updateHandler.handle(command);
	}

	@POST
	@Path("viewM/monthlyWorkTimeSet/delete")
	public void deleteMonthlyWorkTimeSet(CommandHandlerContext<DeleteTransMonthlyWorkTimeSetWkpCommand> command) {
		deleteHandler.handle(command);
	}
}
