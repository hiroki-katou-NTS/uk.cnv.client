package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.List;
import java.util.Optional;

/**
 * 1日お気に入り作業セットRepository
 * 
 * @author tutt
 *
 */
public interface OneDayFavoriteTaskSetRepository {

	/**
	 * [1] Insert(1日お気に入り作業セット)
	 * 
	 * @param set 1日お気に入り作業セット
	 */
	void insert(OneDayFavoriteSet set);

	/**
	 * [2] Update(1日お気に入り作業セット)
	 */
	void update(OneDayFavoriteSet set);

	/**
	 * [3] Delete(社員ID,お気に入りID)
	 * @param employee 社員ID
	 * @param favoriteId お気に入りID
	 */
	void delete(String employee, String favoriteId);
	
	/**
	 * [4] Get*
	 * 指定者のすべての1日お気に入り作業セットを取得する
	 * @param employee 社員ID
	 * @return
	 */
	List<OneDayFavoriteSet> getAll(String employee);
	
	/**
	 * [5] Get
	 * 指定IDの1日お気に入り作業セットを取得する
	 * @param favoriteId お気に入りID
	 * @return
	 */
	Optional<OneDayFavoriteSet> getByFavoriteId(String favoriteId);
}
