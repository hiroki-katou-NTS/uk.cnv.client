package nts.uk.ctx.at.record.app.find.workrecord.worktype;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmpRepo;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class WorkTypeEmploymentFinder {
	@Inject
	private WorkingTypeChangedByEmpRepo repo;
	
	public List<String> getDistinctEmpCodeByCompanyId() {
		String companyId = AppContexts.user().companyId();
		
		List<String> listEmpCode = repo.getDistinctEmpCodeByCompanyId(companyId);
		return listEmpCode;
	}
}
