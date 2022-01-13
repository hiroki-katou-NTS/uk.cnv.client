/**
 * 1:49:26 PM Jul 24, 2017
 */
package nts.uk.ctx.at.record.ws.workrecord.erroralarm;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.record.app.command.workrecord.erroralarm.DeleteErAlCommandHandler;
import nts.uk.ctx.at.record.app.command.workrecord.erroralarm.UpdateErrorAlarmWrCommand;
import nts.uk.ctx.at.record.app.command.workrecord.erroralarm.UpdateErrorAlarmWrCommandHandler;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmDto;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmWorkRecordDto;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmWorkRecordFinder;

/**
 * @author hungnm
 *
 */
@Path("ctx/at/record/workrecord/erroralarm/")
@Produces("application/json")
public class ErrorAlarmWrWebservice {

	@Inject
	private ErrorAlarmWorkRecordFinder finder;

	@Inject
	private UpdateErrorAlarmWrCommandHandler updateCommandHandler;

	@Inject
	private DeleteErAlCommandHandler deleteErAlCommandHandler;

	@POST
	@Path("getall/{type}")
	public ErrorAlarmDto getAll(@PathParam(value = "type") int type) {
		return this.finder.getListErrorAlarmWorkRecord(type);
	}
	
	@POST
	@Path("findbylistalarmcheckid")
	public List<ErrorAlarmWorkRecordDto> findByListErrorAlamCheckId(List<String> listEralCheckId) {
		return this.finder.findByListErrorAlamCheckId(listEralCheckId);
	}

	@POST
	@Path("update")
	public void update(UpdateErrorAlarmWrCommand command) {
		this.updateCommandHandler.handle(command);
	}

	@POST
	@Path("remove")
	public void remove(String code) {
		this.deleteErAlCommandHandler.handle(code);
	}
}
