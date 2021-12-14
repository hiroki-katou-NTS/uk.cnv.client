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
public class RegisterCommandHandler extends CommandHandlerWithResult<RegisterCommand, ProcessResult> {
	@Inject
	private OverTimeRegisterService overTimeRegisterService;
	@Override
	protected ProcessResult handle(CommandHandlerContext<RegisterCommand> context) {
		RegisterCommand param = context.getCommand();
		Application application = param.appOverTime.application.toDomain();
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		
		return overTimeRegisterService.register(
				param.companyId,
				appOverTime,
				param.appDispInfoStartupDto.toDomain(),
				param.isMail,
				param.appTypeSetting.toDomain());
	}

}
