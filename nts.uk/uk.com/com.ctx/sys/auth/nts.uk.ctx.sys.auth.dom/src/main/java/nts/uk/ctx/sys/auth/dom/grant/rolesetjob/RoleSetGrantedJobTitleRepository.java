package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import java.util.List;
import java.util.Optional;

/**
 * 
 * @author HungTT
 *
 */

public interface RoleSetGrantedJobTitleRepository {

	public List<RoleSetGrantedJobTitle> getAllByCompanyId(String companyId);
	
	public Optional<RoleSetGrantedJobTitle> getOneByCompanyId(String companyId);
	
	public boolean checkRoleSetCdExist(String roleSetCd, String companyId);
	
	public void insert (RoleSetGrantedJobTitle domain);
	
	public void update (RoleSetGrantedJobTitle domain);
	
	public void delete (String companyId);
	
	public Optional<String> getRoleSetCd (String companyId , String jobTitleId);
	
	public List<String> findJobTitleByRoleCDLst(String companyID, List<String> roleCDLst);
}
