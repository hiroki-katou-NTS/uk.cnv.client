package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.App.お気に入り作業を取得する
 * 
 * @author tutt <<Query>> お気に入り作業を取得する
 */
@Stateless
public class GetFavoriteTask {

	@Inject
	private OneDayFavoriteTaskSetRepository taskSetRepo;

	@Inject
	private OneDayFavoriteTaskDisplayOrderRepository onedayOrderRepo;

	@Inject
	private FavoriteTaskItemRepository itemRepo;

	@Inject
	private FavoriteTaskDisplayOrderRepository orderRepo;

	/**
	 * @return
	 * 
	 */
	public GetFavoriteTaskDto getFavTask() {
		String employeeId = AppContexts.user().employeeId();

		GetFavoriteTaskDto taskDto = new GetFavoriteTaskDto();

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

		// お気に入り作業項目
		List<FavoriteTaskItemDto> favTaskItems = itemRepo.getAll(employeeId).stream()
				.map(m -> new FavoriteTaskItemDto(m)).collect(Collectors.toList());
		taskDto.setFavTaskItems(favTaskItems);

		// お気に入り作業の表示順
		Optional<FavoriteTaskDisplayOrder> optFavTaskDisplayOrder = orderRepo.get(employeeId);
		if (optFavTaskDisplayOrder.isPresent()) {
			FavoriteTaskDisplayOrderDto favTaskDisplayOrder = new FavoriteTaskDisplayOrderDto(
					optFavTaskDisplayOrder.get());
			taskDto.setFavTaskDisplayOrders(favTaskDisplayOrder);
		}

		return taskDto;
	}

}
