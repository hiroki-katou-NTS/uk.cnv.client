package nts.uk.ctx.pr.proto.dom.layout.detail;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;


public interface LayoutMasterDetailRepository {

	/**
	 * find layout master detail by company code, layout code, start date,
	 * category code
	 * 
	 * @param companyCode
	 * @param layoutCode
	 * @param startYm
	 * @param categoryAtr
	 * @param autoLineID
	 * @param itemCode
	 * @return
	 */
	Optional<LayoutMasterDetail> find(String companyCode, String layoutCode, int startYm, String stmtCode,
			String categoryAtr, String autoLineID, String itemCode);

	/**
	 * add a layout master detail
	 * 
	 * @param companyCode
	 * @param startYm
	 * @param stmtCode
	 */
	void add(String companyCode, int startYm, String stmtCode);

	/**
	 * update a layout master detail
	 * 
	 * @param companyCode
	 * @param startYm
	 * @param stmtCode
	 * @param categoryAtr
	 * @param autoLineID
	 * @param itemCode
	 */
	void update(String companyCode, int startYm, String stmtCode, int categoryAtr, String autoLineID, String itemCode);

	/**
	 * delete a layout master detail
	 * 
	 * @param companyCode
	 * @param layoutCode
	 * @param startYm
	 * @param categoryAtr
	 * @param itemCode
	 */
	void remove(String companyCode, int startYm, String stmtCode, int categoryAtr, String itemCode);

	/**
	 * get Detail
	 * 
	 * @param companyCode
	 * @param startYM
	 * @param stmtCode
	 * @return list Detail
	 */
	List<LayoutMasterDetail> getDetails(String companyCode, int startYM, String stmtCode);

	/**
	 * get detail
	 * 
	 * @param companyCode
	 * @param layoutCode
	 * @param startYM
	 * @param categoryAtr
	 * @param calculationMethod
	 * @return
	 */
	List<LayoutMasterDetail> getDetail(String companyCode, String layoutCode, int startYM, int categoryAtr,
			String calculationMethod);

	/**
	 * get detail by calculation
	 * 
	 * @param calculationMethod
	 * @return
	 */
	List<LayoutMasterDetail> getDetailByCalculation(String calculationMethod);

}
