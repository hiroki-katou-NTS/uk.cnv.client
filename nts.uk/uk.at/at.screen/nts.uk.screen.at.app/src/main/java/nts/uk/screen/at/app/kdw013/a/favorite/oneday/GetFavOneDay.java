package nts.uk.screen.at.app.kdw013.a.favorite.oneday;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteSetDto;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteTaskDisplayOrderDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 */

@Stateless
public class GetFavOneDay {

	@Inject
	private OneDayFavoriteTaskSetRepository taskSetRepo;

	@Inject
	private OneDayFavoriteTaskDisplayOrderRepository onedayOrderRepo;

	public FavOneDayDto get() {

		String employeeId = AppContexts.user().employeeId();
		
		FavOneDayDto taskDto = new FavOneDayDto();
		// 1日お気に入りセット
		List<OneDayFavoriteSetDto> oneDayFavSets = taskSetRepo.getAll(employeeId).stream()
				.map(m -> new OneDayFavoriteSetDto(m)).collect(Collectors.toList());
		taskDto.setOneDayFavSets(oneDayFavSets);

		// 1日お気に入り作業の表示順
		Optional<OneDayFavoriteTaskDisplayOrder> optOneDayFavTaskDisplayOrder = onedayOrderRepo.get(employeeId);

		if (optOneDayFavTaskDisplayOrder.isPresent()) {
			OneDayFavoriteTaskDisplayOrderDto oneDayFavTaskDisplayOrder = new OneDayFavoriteTaskDisplayOrderDto(
					optOneDayFavTaskDisplayOrder.get());
			taskDto.setOneDayFavTaskDisplayOrders(oneDayFavTaskDisplayOrder);
		}
		
		return taskDto;
	}
}
