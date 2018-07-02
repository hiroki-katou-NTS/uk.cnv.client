package nts.uk.ctx.sys.gateway.ac.find.company;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.company.pub.company.CompanyExport;
import nts.uk.ctx.bs.company.pub.company.ICompanyPub;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoDtoImport;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class CompanyBsAdapterImpl implements CompanyBsAdapter{
	
	@Inject
	private ICompanyPub companyPub;
	
	@Override
	public CompanyBsImport getCompanyByCid(String cid) {
		CompanyExport companyExport = companyPub.getCompanyByCid(cid);
		return new CompanyBsImport(
				companyExport.getCompanyCode(), 
				companyExport.getCompanyName(), 
				companyExport.getCompanyId(), 
				companyExport.getIsAbolition());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter#getAllCompany()
	 */
	@Override
	public List<CompanyBsImport> getAllCompany() {
		return this.companyPub.getAllCompany().stream().map(f -> {
			return new CompanyBsImport(
					f.getCompanyCode(), 
					f.getCompanyName(), 
					f.getCompanyId(), 
					f.getIsAbolition());
		}).collect(Collectors.toList());
	}
}
