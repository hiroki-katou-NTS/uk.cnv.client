package nts.uk.screen.at.app.kdw013.command;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.DeleteOneDayFavoriteTaskService;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.App.お気に入りを削除する
 * <<Command>> お気に入りを削除する
 * @author tutt 
 */
@Stateless
@Transactional
public class DeleteFavoriteForOneDayCommandHandler extends CommandHandler<DeleteFavoriteForOneDayCommand> {

	@Inject
	private OneDayFavoriteTaskDisplayOrderRepository orderRepo;

	@Inject
	private OneDayFavoriteTaskSetRepository setRepo;

	@Override
	protected void handle(CommandHandlerContext<DeleteFavoriteForOneDayCommand> context) {

		DeleteFavoriteForOneDayCommand command = context.getCommand();

		Require require = new Require(orderRepo, setRepo);

		// 1. 削除する
		AtomTask atom = DeleteOneDayFavoriteTaskService.create(require, AppContexts.user().employeeId(), command.getFavId());

		// 2. persist()
		transaction.execute(() -> atom.run());
	}

	@AllArgsConstructor
	private class Require implements DeleteOneDayFavoriteTaskService.Require {

		private OneDayFavoriteTaskDisplayOrderRepository orderRepo;

		private OneDayFavoriteTaskSetRepository setRepo;

		@Override
		public Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId) {
			return orderRepo.get(employeeId);
		}

		@Override
		public void delete(String employeeId, String favoriteId) {
			setRepo.delete(employeeId, favoriteId);
		}

		@Override
		public void delete(String employeeId) {
			orderRepo.delete(employeeId);

		}

		@Override
		public void update(OneDayFavoriteTaskDisplayOrder favoriteTaskDisplayOrder) {
			orderRepo.update(favoriteTaskDisplayOrder);

		}

		@Override
		public void deleteByFavId(String favoriteId) {
			orderRepo.deleteByFavId(favoriteId);			
		}

	}

}
