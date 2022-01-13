package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;


// 雇用の目安金額を削除する
@Stateless
@Transactional
public class DeleteEstimatedEmploymentCommandHandler extends CommandHandler<DeleteEstimatedEmploymentCommand>{
	
	@Inject
	private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteEstimatedEmploymentCommand> context) {
		String cid = AppContexts.user().companyId();
		
		DeleteEstimatedEmploymentCommand command = context.getCommand();
		
		criterionAmountForEmploymentRepository.delete(cid, new EmploymentCode(command.getEmploymentCode()));
	}

}
