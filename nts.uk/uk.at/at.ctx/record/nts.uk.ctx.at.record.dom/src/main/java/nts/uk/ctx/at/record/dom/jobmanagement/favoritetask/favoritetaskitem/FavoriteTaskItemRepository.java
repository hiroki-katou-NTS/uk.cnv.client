
package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import java.util.List;
import java.util.Optional;

/**
 * RP: お気に入り作業項目Repository
 * @author tutt
 *
 */
public interface FavoriteTaskItemRepository {
	
	/**
	 * [1] Insert(お気に入り作業項目)
	 */
	void insert(FavoriteTaskItem item);
	
	/**
	 * [2] Update(お気に入り作業項目)
	 */
	void update(FavoriteTaskItem item);
	
	/**
	 * [3] Delete(社員ID,お気に入りID)
	 * @param employeeID 社員ID
	 * @param favoriteId お気に入りID
	 */
	void delete(String employeeID, String favoriteId);
	
	/**
	 * [4] Get*
	 * @param employeeId 社員ID
	 * @return
	 */
	List<FavoriteTaskItem> getAll(String employeeId);
	
	/**
	 * [5] Get
	 * @param favoriteId お気に入りID
	 * @return
	 */
	Optional<FavoriteTaskItem> getByFavoriteId(String favoriteId);
	
	/**
	 * [6] 指定内容が同じお気に入り作業を取得する
	 * @param employeeId 社員ID
	 * @param contents List<作業内容>
	 * @return 	List<お気に入り作業項目>
	 */
	List<FavoriteTaskItem> getBySameSetting(String employeeId, List<TaskContent> contents);
}
