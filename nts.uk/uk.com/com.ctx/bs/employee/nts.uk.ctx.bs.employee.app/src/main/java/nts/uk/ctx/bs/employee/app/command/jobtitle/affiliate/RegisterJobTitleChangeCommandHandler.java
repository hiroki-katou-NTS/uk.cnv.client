package nts.uk.ctx.bs.employee.app.command.jobtitle.affiliate;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.shr.pereg.app.command.PeregCommandHandlerCollector;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職位.職務職位.アルゴリズム.Command.職位異動の登録をする.職位異動の登録をする
 * @author NWS-DungDV
 *
 */
@Stateless
public class RegisterJobTitleChangeCommandHandler extends CommandHandler<Object>{

	@Inject
	private PeregCommandHandlerCollector peregCommandHandlerCollector;
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	protected void handle(CommandHandlerContext<Object> context) {
		val handler = this.peregCommandHandlerCollector
				.collectAddHandlers()
				.stream()
				.collect(Collectors.toMap(h -> h.targetCategoryCd(), h -> h))
				.get("CS00016");
		
		handler.handlePeregCommand(context.getCommand());
		
	}

	
}
