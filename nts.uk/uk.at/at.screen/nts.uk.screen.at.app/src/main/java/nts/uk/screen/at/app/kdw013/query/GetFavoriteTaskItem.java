package nts.uk.screen.at.app.kdw013.query;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.App.お気に入り作業項目を取得する
 * <<Query>> お気に入り作業項目を取得する
 * @author tutt 
 */
@Stateless
public class GetFavoriteTaskItem {

	@Inject
	private FavoriteTaskItemRepository repo;

	public Optional<FavoriteTaskItemDto> getFavTaskItem(String favId) {

		Optional<FavoriteTaskItem> optFavTaskItem = repo.getByFavoriteId(favId);

		if (!optFavTaskItem.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(new FavoriteTaskItemDto(optFavTaskItem.get()));
	}
}
