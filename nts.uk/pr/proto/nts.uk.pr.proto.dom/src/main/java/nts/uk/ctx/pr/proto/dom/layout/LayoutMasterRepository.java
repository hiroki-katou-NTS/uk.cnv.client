package nts.uk.ctx.pr.proto.dom.layout;

import java.util.List;
import java.util.Optional;



public interface LayoutMasterRepository {
	// layout master
	/**
	 * find layout master by company code, layout master(name or code), start
	 * date
	 * 
	 * @param companyCode
	 * @param stmtCode
	 * @param strYm
	 * @return
	 */
	Optional<LayoutMaster> getLayout(String companyCode, String historyId, String stmtCode);
	
	Optional<LayoutMaster> getHistoryBefore(String companyCode, String stmtCode, int startYm);
	/**
	 * find all layout master by company code, start date
	 * 
	 * @return layout master
	 */
	List<LayoutMaster> getLayouts(String companyCode);

	/**
	 * Add a new layout master
	 * 
	 * @param layoutMaster
	 */
	void add(LayoutMaster layoutMaster);

	/**
	 * update a layout master
	 * 
	 * @param layoutMaster
	 */
	void update(LayoutMaster layoutMaster);

	/**
	 * delete a layout master
	 * 
	 * @param companyCode
	 * @param stmtCode
	 * @param startYm
	 */
	void remove(String companyCode, String stmtCode, String historyId);

	/**
	 * 
	 * @param companyCode
	 * @param stmtCode
	 * @return
	 */
	boolean isExist(String companyCode, String stmtCode);

//	Optional<LayoutMaster> getPreviosTarget(String companyCode, String stmtCode);
	/**
	 * find all layouts with max startYM
	 * @param companyCode
	 * @return
	 */
	List<LayoutMaster> getLayoutsWithMaxStartYm(String companyCode);
	
	List<LayoutMaster> findAll(String companyCode, String stmtCode, int baseYearMonth);
}
