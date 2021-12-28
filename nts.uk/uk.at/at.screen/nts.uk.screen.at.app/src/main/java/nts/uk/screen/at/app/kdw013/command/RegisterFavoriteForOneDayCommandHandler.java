package nts.uk.screen.at.app.kdw013.command;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteSet;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.RegisterOneDayFavoriteTaskService;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskBlockDetailContent;
import nts.uk.shr.com.context.AppContexts;
import java.util.stream.Collectors;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.App.お気に入りを登録する
 * <<Command>> お気に入りを登録する
 * @author tutt 
 */
@Stateless
@Transactional
public class RegisterFavoriteForOneDayCommandHandler extends CommandHandler<RegisterFavoriteForOneDayCommand> {

	@Inject
	private OneDayFavoriteTaskDisplayOrderRepository orderRepo;

	@Inject
	private OneDayFavoriteTaskSetRepository setRepo;

	@Override
	protected void handle(CommandHandlerContext<RegisterFavoriteForOneDayCommand> context) {
		
		RegisterFavoriteForOneDayCommand command = context.getCommand();
		Require require = new Require(orderRepo, setRepo);

		List<TaskBlockDetailContent> contents = command.getContents().stream().map(m -> m.toDomain())
				.collect(Collectors.toList());

		// 1. 追加する
		AtomTask atom = RegisterOneDayFavoriteTaskService.add(require, AppContexts.user().employeeId(),
				new FavoriteTaskName(command.getTaskName()), contents);

		// 2. persist
		transaction.execute(() -> atom.run());

	}

	@AllArgsConstructor
	private class Require implements RegisterOneDayFavoriteTaskService.Require {

		private OneDayFavoriteTaskDisplayOrderRepository orderRepo;

		private OneDayFavoriteTaskSetRepository setRepo;

		@Override
		public Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId) {
			return orderRepo.get(employeeId);

		}

		@Override
		public void insert(OneDayFavoriteSet set) {
			setRepo.insert(set);

		}

		@Override
		public void insert(OneDayFavoriteTaskDisplayOrder order) {
			orderRepo.insert(order);

		}

		@Override
		public void update(OneDayFavoriteTaskDisplayOrder order) {
			orderRepo.update(order);

		}
	}
}
