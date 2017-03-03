/**
 * 
 */
package nts.uk.ctx.pr.formula.dom.repository;

import java.util.Optional;

import nts.uk.ctx.pr.formula.dom.enums.ReferenceMasterNo;
import nts.uk.ctx.pr.formula.dom.formula.FormulaEasyHead;
import nts.uk.ctx.pr.formula.dom.primitive.FormulaCode;

/**
 * @author hungnm
 *
 */
public interface FormulaEasyHeaderRepository {
	/**
	 * @param companyCode
	 * @param formulaCode
	 * @param historyId
	 * @param referenceMasterNo
	 * @return a formulaEasyHead
	 */
	Optional<FormulaEasyHead> findByPriKey(String companyCode, FormulaCode formulaCode, String historyId, ReferenceMasterNo referenceMasterNo);
	
	void add(FormulaEasyHead formulaEasyHead);
}
