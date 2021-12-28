package nts.uk.ctx.at.record.ws.kdl.kdl013.a;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.query.tasksupinforitemsetting.Kdl013Request;
import nts.uk.ctx.at.record.app.query.tasksupinforitemsetting.TaskSupInfoChoiceDetailsQuery;
import nts.uk.ctx.at.record.app.query.tasksupinforitemsetting.TaskSupInfoItemAndSelectInforQueryDto;

@Path("at/record/task/management/supplementinfo")
@Produces("application/json")
public class TaskSupInfoChoiceDetailWs extends WebService{
	@Inject
	TaskSupInfoChoiceDetailsQuery finder;
	
	@POST
	@Path("getByAtdIdAndDate")
	public List<TaskSupInfoItemAndSelectInforQueryDto> get(Kdl013Request request){
		return this.finder.get(request.toDate(), request.getAtdId());
	}
}
