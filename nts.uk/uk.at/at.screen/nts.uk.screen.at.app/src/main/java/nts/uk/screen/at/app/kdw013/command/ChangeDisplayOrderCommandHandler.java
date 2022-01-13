package nts.uk.screen.at.app.kdw013.command;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.App.表示順を入れ替える
 * <<Command>> 表示順を入れ替える
 * 
 * @author tutt
 *
 */
@Stateless
@Transactional
public class ChangeDisplayOrderCommandHandler extends CommandHandler<ChangeDisplayOrderCommand> {

	@Inject
	private FavoriteTaskDisplayOrderRepository repo;

	@Override
	protected void handle(CommandHandlerContext<ChangeDisplayOrderCommand> context) {
		ChangeDisplayOrderCommand command = context.getCommand();

		// 1: Get(ログイン社員ID)
		Optional<FavoriteTaskDisplayOrder> optOrder = repo.get(AppContexts.user().employeeId());

		// 2: お気に入り作業の表示順.isPresent
		if (optOrder.isPresent()) {
			optOrder.get().changeOrder(command.getReorderedId(), command.getBeforeOrder(), command.getAfterOrder());
	
			// 3: persist()
			repo.update(optOrder.get());
		}
		
	}

}
