package nts.uk.ctx.sys.auth.app.command.grant.roleindividual;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CreateRoleIndividualGrantCommandHandler {

	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepository;
	
	public String InsertRoleGrant(CreateRoleIndividualGrantCommand roleGrant){
		if(roleGrant.userID == null)
			return null;
			
		if(this.roleIndividualGrantRepository.findByUserCompanyRoleType(roleGrant.userID, roleGrant.companyID, roleGrant.roleType).isPresent()){
			return null;
		}else{
			this.roleIndividualGrantRepository.add(roleGrant.toDomain());
			return  roleGrant.getCompanyID()+"_"+roleGrant.getUserID()+"_"+roleGrant.getRoleType();
		}
	}
}
