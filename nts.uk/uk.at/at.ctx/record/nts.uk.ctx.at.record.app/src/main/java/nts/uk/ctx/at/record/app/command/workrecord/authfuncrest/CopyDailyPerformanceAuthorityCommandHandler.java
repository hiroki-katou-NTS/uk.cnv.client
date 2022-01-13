package nts.uk.ctx.at.record.app.command.workrecord.authfuncrest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformAuthorRepo;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceAuthority;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class CopyDailyPerformanceAuthorityCommandHandler extends CommandHandler<CopyDailyPerformanceAuthorityCommand>{
	
	@Inject
	private DailyPerformAuthorRepo repo;

	@Override
	protected void handle(CommandHandlerContext<CopyDailyPerformanceAuthorityCommand> context) {
		String companyId = AppContexts.user().companyId();
		CopyDailyPerformanceAuthorityCommand copyCommand = context.getCommand();
		//複写元のドメインモデル「勤務実績の権限」を取得する
		List<DailyPerformanceAuthority> sourceData = repo.findByCidAndRole(companyId, copyCommand.getSelectedRole());
		
		repo.copy(companyId, sourceData, copyCommand.getTargetRoleList());
	}

}
