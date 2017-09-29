package nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.repository;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.HoriTotalCategory;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.TotalEvalItem;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.TotalEvalOrder;
public interface HoriTotalCategoryRepository {
	
	/**
	 * get all hori total category data
	 * @param companyId
	 * @return
	 */
	List<HoriTotalCategory> findAllCate(String companyId);
	
	/**
	 * update a hori total category 
	 * @param aggregateCategory
	 */
	void updateCate(HoriTotalCategory horiTotalCategory);
	
	/**
	 * insert a hori total category
	 * @param aggregateCategory
	 */
	void insertCate(HoriTotalCategory horiTotalCategory);
	
	/**
	 * delete a hori total category
	 * @param companyId
	 * @param categoryCode
	 */
	void deleteCate(String companyId, String categoryCode);
	
	/**
	 * find a hori total category by categoryCode
	 * @param company
	 * @param categoryCode
	 * @return
	 */
	Optional<HoriTotalCategory> findCateByCode(String companyId, String categoryCode);
	
	/**
	 * find all total eval order
	 * @param companyId
	 * @return
	 */
	List<TotalEvalOrder> findAllOrder (String companyId);
	
	/**
	 * update list total eval order
	 * @param totalEvalOrders
	 */
	void updateOrder(List<TotalEvalOrder> totalEvalOrders);
	
	/**
	 * insert list total eval order
	 * @param totalEvalOrders
	 */
	void insertOrder(List<TotalEvalOrder> totalEvalOrders);
	
	/**
	 * find list total eval order by code
	 * @param companyId
	 * @param categoryCode
	 * @param totalItemNo
	 * @return
	 */
	List<TotalEvalOrder> findOrder(String companyId, String categoryCode, Integer totalItemNo);
	
	/**
	 * find all total eval item
	 * @param companyId
	 * @return
	 */
	List<TotalEvalItem> findAllItem (String companyId);
	
	/**
	 * find total eval item by code
	 * @param companyId
	 * @param totalItemNo
	 * @return
	 */
	List<TotalEvalItem> findEvalItem(String companyId, int totalItemNo);
}
