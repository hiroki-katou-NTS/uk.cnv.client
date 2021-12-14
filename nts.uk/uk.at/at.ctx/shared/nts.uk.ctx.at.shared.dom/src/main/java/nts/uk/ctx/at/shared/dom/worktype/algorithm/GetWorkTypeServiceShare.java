package nts.uk.ctx.at.shared.dom.worktype.algorithm;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetWorkTypeServiceShare {
	@Inject
	private WorkTypeRepository wkTypeRepo;
	
	public Optional<WorkType>getWorkType(String workTypeCode){
		String companyID = AppContexts.user().companyId();
		return wkTypeRepo.findByPK(companyID, workTypeCode);
	}
}
