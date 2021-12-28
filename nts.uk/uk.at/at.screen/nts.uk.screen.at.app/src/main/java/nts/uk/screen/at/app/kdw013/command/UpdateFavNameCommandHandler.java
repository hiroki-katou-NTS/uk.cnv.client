package nts.uk.screen.at.app.kdw013.command;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.App.お気に入り名称を変更する
 * <<Command>> お気に入り名称を変更する
 * @author tutt
 *
 */
@Stateless
@Transactional
public class UpdateFavNameCommandHandler extends CommandHandler<UpdateFavNameCommand> {

	@Inject
	private FavoriteTaskItemRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateFavNameCommand> context) {
		UpdateFavNameCommand command = context.getCommand();
		
		// 1. Get(お気に入りID)
		Optional<FavoriteTaskItem> optFavoriteTaskItem = repo.getByFavoriteId(command.getFavId());
		
		// 2. お気に入り作業セット.isPresent
		if (optFavoriteTaskItem.isPresent()) {
			optFavoriteTaskItem.get().setTaskName(new FavoriteTaskName(command.getFavName()));
		
			// 3. persist()
			repo.update(optFavoriteTaskItem.get());
		}
	}

}
