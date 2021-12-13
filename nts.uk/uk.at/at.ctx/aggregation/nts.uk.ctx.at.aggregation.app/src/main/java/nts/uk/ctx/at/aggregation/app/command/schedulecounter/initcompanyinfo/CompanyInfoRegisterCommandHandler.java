package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo;

import java.util.Optional;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompany;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompanyRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountRepository;
import nts.uk.shr.com.context.AppContexts;
/**
 * 会社の目安金額を登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.App.会社の目安金額を登録する.会社の目安金額を登録する
 * @author hoangdd
 *
 */

@Stateless
@Transactional
public class CompanyInfoRegisterCommandHandler extends CommandHandler<CompanyInfoRegisterCommand> {

	@Inject
	private CriterionAmountForCompanyRepository criterionAmountForCompanyRepository;
	
	@Inject
	private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepository;
	
	@Override
	protected void handle(CommandHandlerContext<CompanyInfoRegisterCommand> context) {
		
		String cid = AppContexts.user().companyId();
		CompanyInfoRegisterCommand command = context.getCommand();
		
		CriterionAmount criterionAmount = new CriterionAmount(
				CriterionAmountList.create(
						command.getYears().stream().map(CriterionAmountByNoCommand::toCriterionAmountByNo).collect(Collectors.toList())),
				CriterionAmountList.create(
						command.getMonths().stream().map(CriterionAmountByNoCommand::toCriterionAmountByNo).collect(Collectors.toList()))
				);
		CriterionAmountForCompany domain;
		/** 1. get */
		Optional<CriterionAmountForCompany> criOptional = criterionAmountForCompanyRepository.get(cid);
		/** 1.1 変更する(目安金額)*/
		if (criOptional.isPresent()) {
			domain = criOptional.get();
			domain.update(criterionAmount);
			/** 1.3 persist */
			criterionAmountForCompanyRepository.update(cid, domain);
		/** 1.2 create */ 
		} else {
			domain = new CriterionAmountForCompany(criterionAmount);			
			/** 1.3 persist*/
			criterionAmountForCompanyRepository.insert(cid, domain);
		}
		
		
		HandlingOfCriterionAmount handlingCriterionAmount = new HandlingOfCriterionAmount(
				command.getHandlings().stream().map(HandlingOfCriterionAmountByNoComand::toHandlingOfCriterionAmountByNo).collect(Collectors.toList()));
		
		/** 2. get */
		Optional<HandlingOfCriterionAmount> optionalHandling = handlingOfCriterionAmountRepository.get(cid);
		/** 2.1 update */
		if(optionalHandling.isPresent()) {
			/** 2.3 persist*/
			handlingOfCriterionAmountRepository.update(cid, handlingCriterionAmount);		
		/** 2.2 create */
		} else {
			/** 2.3 persist*/
			handlingOfCriterionAmountRepository.insert(cid, handlingCriterionAmount);			
		}		
	}

}
