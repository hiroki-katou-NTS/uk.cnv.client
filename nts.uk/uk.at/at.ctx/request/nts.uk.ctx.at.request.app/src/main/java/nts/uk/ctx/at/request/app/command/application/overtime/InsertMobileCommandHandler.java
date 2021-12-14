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
public class InsertMobileCommandHandler extends CommandHandlerWithResult<InsertCommand, ProcessResult> {

	@Inject
	private OverTimeRegisterService overTimeRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<InsertCommand> context) {
		InsertCommand param = context.getCommand();
		
		Application application;
		AppOverTime appOverTime;
		if (param.mode) {
			application = param.appOverTimeInsert.application.toDomain();
			appOverTime = param.appOverTimeInsert.toDomain();
		} else {
			application = param.appOverTimeUpdate.application.toDomain(param.appDispInfoStartupOutput.getAppDetailScreenInfo().getApplication());
			appOverTime = param.appOverTimeUpdate.toDomain();
		}
		
		appOverTime.setApplication(application);
		return overTimeRegisterService.insertMobile(
				param.companyId,
				param.mode,
				appOverTime,
				param.isMailServer,
				param.appDispInfoStartupOutput.toDomain());
	}

}
