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
	
	/**
	 * Gets the all company by contract cd.
	 *
	 * @param contractCd the contract cd
	 * @return the all company by contract cd
	 */
	List<CompanyBsImport> getAllCompanyByContractCd(String contractCd);
}
