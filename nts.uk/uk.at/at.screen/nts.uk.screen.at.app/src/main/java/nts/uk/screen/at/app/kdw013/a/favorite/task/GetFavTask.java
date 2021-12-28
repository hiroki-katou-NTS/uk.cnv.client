package nts.uk.screen.at.app.kdw013.a.favorite.task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.screen.at.app.kdw013.query.FavoriteTaskDisplayOrderDto;
import nts.uk.screen.at.app.kdw013.query.FavoriteTaskItemDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 */
@Stateless
public class GetFavTask {

	@Inject
	private FavoriteTaskItemRepository itemRepo;

	@Inject
	private FavoriteTaskDisplayOrderRepository orderRepo;

	public FavTaskDto get() {
		
		FavTaskDto result = new FavTaskDto();
		String employeeId = AppContexts.user().employeeId();
		// お気に入り作業項目
		List<FavoriteTaskItemDto> favTaskItems = itemRepo.getAll(employeeId).stream()
				.map(m -> new FavoriteTaskItemDto(m)).collect(Collectors.toList());
		result.setFavTaskItems(favTaskItems);

		// お気に入り作業の表示順
		Optional<FavoriteTaskDisplayOrder> optFavTaskDisplayOrder = orderRepo.get(employeeId);
		if (optFavTaskDisplayOrder.isPresent()) {
			FavoriteTaskDisplayOrderDto favTaskDisplayOrder = new FavoriteTaskDisplayOrderDto(
					optFavTaskDisplayOrder.get());
			result.setFavTaskDisplayOrders(favTaskDisplayOrder);
		}

		return result;
	}
}
