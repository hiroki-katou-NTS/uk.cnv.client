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
public class HolidayWorkRegisterCommandHandler extends CommandHandlerWithResult<RegisterCommand, ProcessResult>{
	
	@Inject
	private HolidayWorkRegisterService holidayWorkRegisterService;

	@Override
	protected ProcessResult handle(CommandHandlerContext<RegisterCommand> context) {
		RegisterCommand param = context.getCommand();
		Application application = param.getAppHolidayWork().getApplication().toDomain();
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);
		
		return holidayWorkRegisterService.register(param.getCompanyId(), 
				appHolidayWork, 
				param.getAppTypeSetting().toDomain(), 
				param.getAppHdWorkDispInfo().toDomain()
				);
	}
	
//	private Application createApplication(ApplicationDto application) {
//		if(application == null) return null;
//		return Application.createFromNew(
//				EnumAdaptor.valueOf(application.getPrePostAtr(), PrePostAtr.class),
//				application.getEmployeeID(),
//				ApplicationType.HOLIDAY_WORK_APPLICATION,
//				new ApplicationDate(GeneralDate.fromString(application.getAppDate(), AppOvertimeFinder.PATTERN_DATE)),
//				application.getEnteredPerson(),
//				application.getOpStampRequestMode() == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(application.getOpStampRequestMode(), StampRequestMode.class)),
//				application.getOpReversionReason() == null ? Optional.empty() : Optional.of(new ReasonForReversion(application.getOpReversionReason())),
//				application.getOpAppStartDate() == null ? Optional.empty() : Optional.of(new ApplicationDate(GeneralDate.fromString(application.getOpAppStartDate(), AppOvertimeFinder.PATTERN_DATE))),
//				application.getOpAppEndDate() == null ? Optional.empty() : Optional.of(new ApplicationDate(GeneralDate.fromString(application.getOpAppEndDate(), AppOvertimeFinder.PATTERN_DATE))),
//				application.getOpAppReason() == null ? Optional.empty() : Optional.of(new AppReason(application.getOpAppReason())),
//				application.getOpAppStandardReasonCD() == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(application.getOpAppStandardReasonCD())));
//	}

}
