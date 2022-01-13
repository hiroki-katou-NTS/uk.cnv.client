package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import java.util.Optional;

/**
 * RP: お気に入り作業の表示順Repository
 * @author tutt
 *
 */
public interface FavoriteTaskDisplayOrderRepository {
	
	/**
	 * [1] Insert(お気に入り作業の表示順)
	 * @param order お気に入り作業の表示順
	 */
	void insert(FavoriteTaskDisplayOrder order);
	
	/**
	 * [2] Update(お気に入り作業の表示順)
	 * @param order お気に入り作業の表示順
	 */
	void update(FavoriteTaskDisplayOrder order);
	
	/**
	 * [3] Delete(社員ID)
	 * @param employeeId 社員ID
	 */
	void delete(String employeeId);
	
	/**
	 * [4] Get
	 * @param employeeId 社員ID
	 * @return お気に入り表示順
	 */
	Optional<FavoriteTaskDisplayOrder> get(String employeeId);
	
	/**
	 * 
	 * @param favoriteId お気に入りID
	 */
	void deleteByFavId(String favoriteId);
}
