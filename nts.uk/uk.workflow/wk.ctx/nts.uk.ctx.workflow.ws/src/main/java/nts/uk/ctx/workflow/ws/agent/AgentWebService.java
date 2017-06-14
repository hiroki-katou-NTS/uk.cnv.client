package nts.uk.ctx.workflow.ws.agent;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.workflow.app.command.agent.AddAgentCommandHandler;
import nts.uk.ctx.workflow.app.command.agent.AgentCommandBase;
import nts.uk.ctx.workflow.app.command.agent.DeleteAgentCommand;
import nts.uk.ctx.workflow.app.command.agent.DeleteAgentCommandHandler;
import nts.uk.ctx.workflow.app.command.agent.UpdateAgentCommandHandler;
import nts.uk.ctx.workflow.app.find.agent.AgentDto;
import nts.uk.ctx.workflow.app.find.agent.AgentFinder;


@Path("workflow/agent")
@Produces("application/json")
public class AgentWebService extends WebService{

	@Inject
	private AgentFinder agentFinder;

	@Inject
	private AddAgentCommandHandler addAgentCommandHandler;

	@Inject
	private UpdateAgentCommandHandler updateAgentCommandHandler;

	@Inject
	private DeleteAgentCommandHandler deleteAgentCommandHandler;
	
	@Path("find/{employeeId}")
	@POST
	public List<AgentDto> findAll(@PathParam("employeeId") String employeeId) {
		return agentFinder.findAll(employeeId);
		
	}
	
	@Path("add")
	@POST
	public void add(AgentCommandBase command) {
		this.addAgentCommandHandler.handle(command);
	}
	
	@Path("update")
	@POST
	public void update(AgentCommandBase command){
		this.updateAgentCommandHandler.handle(command);
	}
	
	@Path("delete")
	@POST
	public void delete(DeleteAgentCommand command){
		this.deleteAgentCommandHandler.handle(command);
	}
	
}	
