package nts.uk.ctx.at.request.app.command.application.overtime;

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
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeRegisterService;

@Stateless
@Transactional
public class RegisterCommandHandlerMultiple extends CommandHandlerWithResult<RegisterCommand, ProcessResult> {
	
	@Inject
	private OverTimeRegisterService overTimeRegisterService;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<RegisterCommand> context) {
		RegisterCommand param = context.getCommand();
		Application application = param.appOverTime.application.toDomain();
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		
		Map<String, ApprovalRootContentImport_New> approvalRootContentMap = new HashMap<String, ApprovalRootContentImport_New>();
		param.approvalRootContentMap.entrySet().forEach(entry -> {
			approvalRootContentMap.put(entry.getKey(), 
					new ApprovalRootContentImport_New(entry.getValue().toDomain(), ErrorFlagImport.NO_ERROR));
		});
		
		return overTimeRegisterService.registerMultiple(
				param.companyId,
				appOverTime,
				param.appDispInfoStartupDto.toDomain(),
				param.isMail,
				param.appTypeSetting.toDomain(),
				approvalRootContentMap
				);
	}

}
