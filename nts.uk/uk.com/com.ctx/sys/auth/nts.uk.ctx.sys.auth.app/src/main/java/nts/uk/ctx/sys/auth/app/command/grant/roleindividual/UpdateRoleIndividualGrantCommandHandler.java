package nts.uk.ctx.sys.auth.app.command.grant.roleindividual;
import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.shr.com.context.AppContexts;
@Stateless
public class UpdateRoleIndividualGrantCommandHandler {

	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepository;
	
	public String UpDateRoleGrant(UpdateRoleIndividualGrantCommand roleGrant){
		String companyId = AppContexts.user().companyId();
		if (companyId == null)
			return null;
		roleGrant.setCompanyID(companyId);
		
		if(roleGrant.userID == null)
			return null;
		
		if(this.roleIndividualGrantRepository.findByUserCompanyRoleType(roleGrant.userID, roleGrant.companyID, roleGrant.roleType).isPresent()){
			this.roleIndividualGrantRepository.update(roleGrant.toDomain());
			return roleGrant.getUserID();
		}else{
			return null;
		}
	}
}
