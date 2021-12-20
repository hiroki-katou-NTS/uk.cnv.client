package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo.CriterionAmountByNoCommand;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;

// 雇用の目安金額を登録する
@Stateless
@Transactional
public class RegisterEstimatedEmploymentCommandHandler extends CommandHandler<RegisterEstimatedEmploymentCommand> {

	@Inject
	private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterEstimatedEmploymentCommand> context) {
		
		String cid = AppContexts.user().companyId();
		
		RegisterEstimatedEmploymentCommand command = context.getCommand();
		
		Optional<CriterionAmountForEmployment> criOptional = criterionAmountForEmploymentRepository.get(cid, new EmploymentCode(command.getEmploymentCode()));
	
		
		CriterionAmount criterionAmount = new CriterionAmount(
				CriterionAmountList.create(
						command.getYears().stream().map(CriterionAmountByNoCommand::toCriterionAmountByNo).collect(Collectors.toList())),
				CriterionAmountList.create(
						command.getMonths().stream().map(CriterionAmountByNoCommand::toCriterionAmountByNo).collect(Collectors.toList()))
				);
		CriterionAmountForEmployment domain;
		if (criOptional.isPresent()) {
			domain = criOptional.get();
			domain.update(criterionAmount);
			criterionAmountForEmploymentRepository.update(cid, domain);
		} else {
			domain = new CriterionAmountForEmployment(new EmploymentCode(command.getEmploymentCode()), criterionAmount);
			criterionAmountForEmploymentRepository.insert(cid, domain);
		}
	}

	

}
