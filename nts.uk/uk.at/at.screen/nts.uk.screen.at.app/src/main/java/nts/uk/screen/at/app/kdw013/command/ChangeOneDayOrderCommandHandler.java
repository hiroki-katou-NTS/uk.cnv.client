package nts.uk.screen.at.app.kdw013.command;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.App.表示順を入れ替える
 * 
 * @author sonnlb
 *
 */
@Stateless
@Transactional
public class ChangeOneDayOrderCommandHandler extends CommandHandler<ChangeDisplayOrderCommand> {

	@Inject
	private OneDayFavoriteTaskDisplayOrderRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<ChangeDisplayOrderCommand> context) {
		ChangeDisplayOrderCommand command = context.getCommand();

		// 1: Get(ログイン社員ID)
		Optional<OneDayFavoriteTaskDisplayOrder> optOrder = repo.get(AppContexts.user().employeeId());

		// 2: お気に入り作業の表示順.isPresent
		if (optOrder.isPresent()) {
			optOrder.get().changeOrder(command.getReorderedId(), command.getBeforeOrder(), command.getAfterOrder());
	
			// 3: persist()
			repo.update(optOrder.get());
		}
	}

}
