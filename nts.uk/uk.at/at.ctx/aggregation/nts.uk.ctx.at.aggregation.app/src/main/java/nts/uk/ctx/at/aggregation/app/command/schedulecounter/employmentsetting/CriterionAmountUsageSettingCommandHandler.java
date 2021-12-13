package nts.uk.ctx.at.aggregation.app.command.schedulecounter.employmentsetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
@Transactional
public class CriterionAmountUsageSettingCommandHandler extends CommandHandler<CriterionAmountUsageSettingCommand>{

	
	@Inject
	private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepository;
	
	@Override
	protected void handle(CommandHandlerContext<CriterionAmountUsageSettingCommand> context) {
		String cid = AppContexts.user().companyId();
		
		CriterionAmountUsageSettingCommand command = context.getCommand();
		
		CriterionAmountUsageSetting domain;
		// 1: 
		Optional<CriterionAmountUsageSetting> criOptional = criterionAmountUsageSettingRepository.get(cid);
		
		
		if (criOptional.isPresent()) { // 2:
			domain = criOptional.get();
			domain.update(EnumAdaptor.valueOf(command.getEmploymentUse(), NotUseAtr.class));
			
			criterionAmountUsageSettingRepository.update(domain);
		} else { // 3:
			
			domain = new CriterionAmountUsageSetting(cid, EnumAdaptor.valueOf(command.getEmploymentUse(), NotUseAtr.class));
			criterionAmountUsageSettingRepository.insert(domain);
		}
	}

}
