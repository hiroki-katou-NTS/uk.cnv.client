package nts.uk.ctx.at.request.app.command.application.common;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.request.app.find.application.common.ApplicationDto;
import nts.uk.ctx.at.request.dom.application.common.Application;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.AfterDenialProcess;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.BeforeProcessDenial;

import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class UpdateApplicationDenyHandler extends CommandHandler<ApplicationDto> {

	@Inject
	private BeforeProcessDenial beforeProcessDenialRepo;
	@Inject 
	private AfterDenialProcess afterDenialProcessRepo;
	

	@Override
	protected void handle(CommandHandlerContext<ApplicationDto> context) {
		String companyID = AppContexts.user().companyId();
		Application application =  ApplicationDto.toEntity(context.getCommand());
		//9-1 .詳細画面否認前の処理
		beforeProcessDenialRepo.detailedScreenProcessBeforeDenial();
		//9.2 
		afterDenialProcessRepo.detailedScreenAfterDenialProcess(application);
	}

}
