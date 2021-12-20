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
		this.roleIndividualGrantRepository.remove(roleGrant.userID, roleGrant.companyID, roleGrant.roleType);
	}
	
}
