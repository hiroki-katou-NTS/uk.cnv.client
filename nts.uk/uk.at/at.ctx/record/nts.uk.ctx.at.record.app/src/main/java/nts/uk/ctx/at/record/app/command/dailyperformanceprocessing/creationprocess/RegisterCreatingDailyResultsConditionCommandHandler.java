package nts.uk.ctx.at.record.app.command.dailyperformanceprocessing.creationprocess;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsCondition;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * ドメインモデル「日別実績を作成する条件」を登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterCreatingDailyResultsConditionCommandHandler extends CommandHandler<Integer> {
	
	@Inject
	private CreatingDailyResultsConditionRepository repository;

	@Override
	protected void handle(CommandHandlerContext<Integer> context) {
		Integer value = context.getCommand();
		String cid = AppContexts.user().companyId();
		Optional<CreatingDailyResultsCondition> optDomain = this.repository.findByCid(cid);
		optDomain.ifPresent(this.repository::delete);
		CreatingDailyResultsCondition domain = new CreatingDailyResultsCondition(cid, NotUseAtr.valueOf(value));
		this.repository.insert(domain);
	}

}
