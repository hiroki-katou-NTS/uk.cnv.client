package nts.uk.ctx.at.request.app.command.application.holidaywork;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.HolidayWorkRegisterService;

/**
 * Refactor5
 * @author huylq
 *
 */
@Stateless
@Transactional
public class HolidayWorkUpdateCommandHandler extends CommandHandlerWithResult<UpdateCommand, ProcessResult>{

	@Inject
	private HolidayWorkRegisterService holidayWorkRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<UpdateCommand> context) {
		UpdateCommand param = context.getCommand();
		Application application = param.getAppHolidayWork().getApplication().toDomain(param.getAppDispInfoStartupDto().getAppDetailScreenInfo().getApplication());
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);

		return holidayWorkRegisterService.update(param.getCompanyId(), appHolidayWork, param.getAppDispInfoStartupDto().toDomain());
	}

}
