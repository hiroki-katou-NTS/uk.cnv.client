package nts.uk.screen.at.app.kdw013.query;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteSet;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.App.1日お気に入り作業セットを取得する
 * <<Query>> 1日お気に入り作業セットを取得する
 * @author tutt
 *
 */
@Stateless
public class GetFavTaskSetForOneDay {

	@Inject
	private OneDayFavoriteTaskSetRepository repo;

	/**
	 * 1日お気に入り作業セットを取得する
	 * 
	 * @param favId
	 * @return
	 */
	public Optional<OneDayFavoriteSetDto> get(String favId) {
		Optional<OneDayFavoriteSet> optset = repo.getByFavoriteId(favId);

		if (optset.isPresent()) {
			return Optional.of(new OneDayFavoriteSetDto(optset.get()));

		} else {
			return Optional.empty();
		}
	}
}
