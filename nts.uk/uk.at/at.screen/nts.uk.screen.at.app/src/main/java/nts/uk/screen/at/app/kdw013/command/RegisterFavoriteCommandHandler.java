package nts.uk.screen.at.app.kdw013.command;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.RegisterFavoriteTaskService;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.App.お気に入りを登録する
 * <<Command>> お気に入りを登録する
 * @author tutt
 * 
 */
@Stateless
@Transactional
public class RegisterFavoriteCommandHandler extends CommandHandler<RegisterFavoriteCommand> {
	
	@Inject
	private FavoriteTaskDisplayOrderRepository orderRepo;
	
	@Inject
	private FavoriteTaskItemRepository itemRepo;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterFavoriteCommand> context) {

		RegisterFavoriteCommand command = context.getCommand();
		Require require = new Require(orderRepo, itemRepo);
		
		List<TaskContent> contents = command.getContents().stream().map(m -> m.toDomain()).collect(Collectors.toList());
	
		// 1. 追加する
		AtomTask atom = RegisterFavoriteTaskService.add(require, AppContexts.user().employeeId(), new FavoriteTaskName(command.getTaskName()), contents);
	
		// 2. persist
		transaction.execute(() -> atom.run());
	
	}
	
	@AllArgsConstructor
	private class Require implements RegisterFavoriteTaskService.Require {
		
		private FavoriteTaskDisplayOrderRepository orderRepo;
		
		private FavoriteTaskItemRepository itemRepo;
		
		@Override
		public Optional<FavoriteTaskDisplayOrder> get(String employeeId) {
			return orderRepo.get(employeeId);
		}

		@Override
		public void insert(FavoriteTaskItem item) {
			itemRepo.insert(item);
		}

		@Override
		public void insert(FavoriteTaskDisplayOrder order) {
			orderRepo.insert(order);
		}

		@Override
		public void update(FavoriteTaskDisplayOrder order) {
			orderRepo.update(order);
		}

		@Override
		public List<FavoriteTaskItem> getBySameSetting(String employeeId, List<TaskContent> contents) {
			return itemRepo.getBySameSetting(employeeId, contents);
		}
		
	}

}
