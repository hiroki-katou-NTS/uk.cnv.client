package nts.uk.ctx.at.request.app.command.application.holidaywork;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ErrorFlagImport;
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
public class HolidayWorkRegisterMultiCommandHandler extends CommandHandlerWithResult<RegisterMultiCommand, ProcessResult>{

	@Inject
	private HolidayWorkRegisterService holidayWorkRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<RegisterMultiCommand> context) {
		RegisterMultiCommand param = context.getCommand();
		Application application = param.getAppHolidayWork().getApplication().toDomain();
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);
		
		Map<String, ApprovalRootContentImport_New> approvalRootContentMap = new HashMap<String, ApprovalRootContentImport_New>();
		param.getApprovalRootContentMap().entrySet().forEach(entry -> {
			approvalRootContentMap.put(entry.getKey(), 
					new ApprovalRootContentImport_New(entry.getValue().toDomain(), ErrorFlagImport.NO_ERROR));
		});
		
		return holidayWorkRegisterService.registerMulti(param.getCompanyId(), param.getEmpList(), param.getAppTypeSetting().toDomain(), 
				param.getAppHdWorkDispInfo().toDomain(), appHolidayWork, approvalRootContentMap);
	}

}
