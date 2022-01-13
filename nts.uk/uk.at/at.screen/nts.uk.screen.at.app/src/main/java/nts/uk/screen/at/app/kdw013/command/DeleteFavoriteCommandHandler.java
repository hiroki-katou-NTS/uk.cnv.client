package nts.uk.screen.at.app.kdw013.command;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.DeleteFavoriteTaskService;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.App.お気に入りを削除する
 * @author tutt
 * <<Command>> お気に入りを削除する
 */
@Stateless
@Transactional
public class DeleteFavoriteCommandHandler extends CommandHandler<DeleteFavoriteCommand> {
	
	@Inject
	private FavoriteTaskDisplayOrderRepository orderRepo;

	@Inject
	private FavoriteTaskItemRepository itemRepo;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteFavoriteCommand> context) {

		DeleteFavoriteCommand command = context.getCommand();
		
		Require require = new Require(orderRepo, itemRepo);
		
		// 1. 削除する
		AtomTask atom = DeleteFavoriteTaskService.create(require, AppContexts.user().employeeId(), command.getFavId());
		
		// 2. persist()
		transaction.execute(() -> atom.run());
	}
	
	@AllArgsConstructor
	private class Require implements DeleteFavoriteTaskService.Require {
		
		private FavoriteTaskDisplayOrderRepository orderRepo;

		private FavoriteTaskItemRepository itemRepo;

		@Override
		public Optional<FavoriteTaskDisplayOrder> get(String employeeId) {
			return orderRepo.get(employeeId);
		}

		@Override
		public void delete(String employeeId, String favoriteId) {
			itemRepo.delete(employeeId, favoriteId);
		}

		@Override
		public void delete(String employeeId) {
			orderRepo.delete(employeeId);
		}

		@Override
		public void update(FavoriteTaskDisplayOrder favoriteTaskDisplayOrder) {
			orderRepo.update(favoriteTaskDisplayOrder);
		}

		@Override
		public void deleteByFavId(String favoriteId) {
			orderRepo.deleteByFavId(favoriteId);
			
		}
		
	}

}
