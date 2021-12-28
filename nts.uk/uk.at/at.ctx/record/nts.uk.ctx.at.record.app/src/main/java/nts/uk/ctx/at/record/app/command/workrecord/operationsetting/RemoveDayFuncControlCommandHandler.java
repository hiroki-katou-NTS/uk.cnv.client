package nts.uk.ctx.at.record.app.command.workrecord.operationsetting;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DayFuncControlRepository;

@Stateless
@Transactional
public class RemoveDayFuncControlCommandHandler extends CommandHandler<DayFuncControlCommand> {

	@Inject
	private DayFuncControlRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<DayFuncControlCommand> context) {
		String cid = context.getCommand().getCid();
        repository.remove(cid);
	}

}
