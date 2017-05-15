package nts.uk.ctx.pr.core.app.command.rule.employment.allot;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pr.core.dom.rule.employment.layout.allot.ClassificationAllotSetting;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class InsertClassificationAllotSettingCommandHandler  extends CommandHandler<InsertClassificationAllotSettingCommand>{

	@Override
	protected void handle(CommandHandlerContext<InsertClassificationAllotSettingCommand> context) {
		InsertClassificationAllotSettingCommand command = context.getCommand();
		String companyCode = AppContexts.user().companyCode();
		ClassificationAllotSetting domain = ClassificationAllotSetting.createFromJavaType(companyCode, command.getHistoryId(), command.getClassificationCode(), command.getBonusDetailCode(), command.getPaymentDetailCode());
		
		
	}
	
	
}
