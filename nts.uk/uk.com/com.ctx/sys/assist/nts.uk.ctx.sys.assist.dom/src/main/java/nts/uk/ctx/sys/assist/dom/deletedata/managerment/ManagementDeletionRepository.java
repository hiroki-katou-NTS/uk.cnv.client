package nts.uk.ctx.sys.assist.dom.deletedata.managerment;

import java.util.Optional;
import java.util.List;

/**
 * データ削除動作管理
 */

public interface ManagementDeletionRepository {

	List<ManagementDeletion> getAllManagementDeletion();

	Optional<ManagementDeletion> getManagementDeletionById(String delId);

	/**
	 * @author nam.lh
	 *
	 */
	void add(ManagementDeletion domain);

	/**
	 * @param delId
	 * @param operatingCondition
	 * @param categoryTotalCount
	 */
	void updateTotalCatCount(String delId, int categoryTotalCount);
	
	/**
	 * @param delId
	 * @param operatingCondition
	 * @param categoryCount
	 */
	void updateCatCountAnCond(String delId, int categoryCount,
			OperatingCondition operatingCondition);

}
