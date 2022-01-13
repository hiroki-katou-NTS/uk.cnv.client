package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.Optional;

/**
 * 1日お気に入り作業の表示順Repository
 * 
 * @author tutt
 *
 */
public interface OneDayFavoriteTaskDisplayOrderRepository {

	/**
	 * [1] Insert(1日お気に入り作業の表示順)
	 * 
	 * @param order 1日お気に入り作業の表示順
	 */
	void insert(OneDayFavoriteTaskDisplayOrder order);

	/**
	 * [2] Update(1日お気に入り作業の表示順)
	 * 
	 * @param order 1日お気に入り作業の表示順
	 */
	void update(OneDayFavoriteTaskDisplayOrder order);

	/**
	 * [3] Delete(社員ID)
	 * 
	 * @param employeeId 社員ID
	 */
	void delete(String employeeId);

	/**
	 * [4] Get
	 * 
	 * @param employeeId 社員ID
	 * @return
	 */
	Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId);
	
	/**
	 * 
	 * @param favId
	 */
	void deleteByFavId(String favId);

}
