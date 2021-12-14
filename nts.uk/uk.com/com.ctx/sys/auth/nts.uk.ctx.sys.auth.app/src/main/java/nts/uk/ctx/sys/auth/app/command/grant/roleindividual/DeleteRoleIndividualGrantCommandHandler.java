package nts.uk.ctx.sys.auth.app.command.grant.roleindividual;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeleteRoleIndividualGrantCommandHandler {

	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepository;
	
	public void deleteRoleGrant(DeleteRoleIndividualGrantCommand roleGrant){
		String companyId = AppContexts.user().companyId();
		if (companyId == null)
			return;
		
		if(roleGrant.userID == null)
			return;
		
		this.roleIndividualGrantRepository.remove(roleGrant.userID, companyId, roleGrant.roleType);
	}
	
}
