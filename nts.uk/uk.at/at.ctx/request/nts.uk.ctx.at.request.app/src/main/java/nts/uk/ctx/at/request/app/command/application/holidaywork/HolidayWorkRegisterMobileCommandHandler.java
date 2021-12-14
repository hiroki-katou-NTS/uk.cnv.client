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
public class HolidayWorkRegisterMobileCommandHandler extends CommandHandlerWithResult<RegisterMobileCommand, ProcessResult>{

	@Inject
	private HolidayWorkRegisterService holidayWorkRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<RegisterMobileCommand> context) {
		RegisterMobileCommand param = context.getCommand();
		
		Application application;
		AppHolidayWork appHolidayWork;
		if (param.getMode()) {
			application = param.getAppHolidayWorkInsert().getApplication().toDomain();
			appHolidayWork = param.getAppHolidayWorkInsert().toDomain();
		} else {
			application = param.getAppHolidayWorkUpdate().getApplication().toDomain(param.getAppHdWorkDispInfo().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication());
			appHolidayWork = param.getAppHolidayWorkUpdate().toDomain();
		}
		
		appHolidayWork.setApplication(application);
		
		return holidayWorkRegisterService.registerMobile(param.getMode(), 
				param.getCompanyId(),
				param.getAppHdWorkDispInfo().toDomain(), 
				appHolidayWork,
				param.getAppTypeSetting().toDomain());
	}

}
