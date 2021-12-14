package nts.uk.ctx.at.request.app.command.application.overtime;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeRegisterService;

@Stateless
@Transactional
public class UpdateCommandHandler extends CommandHandlerWithResult<UpdateCommand, ProcessResult> {
	
	@Inject
	private OverTimeRegisterService overTimeRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<UpdateCommand> context) {
		UpdateCommand param = context.getCommand();
		Application application = param.appOverTime.application.toDomain(param.appDispInfoStartupDto.getAppDetailScreenInfo().getApplication());
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		
		return overTimeRegisterService.update(
				param.companyId,
				appOverTime,
				param.appDispInfoStartupDto.toDomain());
	}

}
