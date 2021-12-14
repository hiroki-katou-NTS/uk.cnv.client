package nts.uk.ctx.sys.gateway.dom.adapter.company;

import java.util.List;

/**
 * 
 * @author Doan Duy Hung
 *
 */
public interface CompanyBsAdapter {
	
	public CompanyBsImport getCompanyByCid(String cid);
	
	/**
	 * Gets the all company.
	 *
	 * @return the all company
	 */
	List<CompanyBsImport> getAllCompany();
}
