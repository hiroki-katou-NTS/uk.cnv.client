package nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking;

import java.util.List;

/**
 * The Interface WebMenuAdapter.
 * @author HieuNV
 */
public interface RoleSetAndWebMenuAdapter {

	/**
	 * Get all Web Menu that link by RoleSet. 
	 * CompanyId is companyId of login user
	 * @return
	 */
	// RequestList #???
	List<RoleSetAndWebMenu> findAllWebMenuByRoleSetCd(String roleSetCd);
	
	/**
	 * add list of Role Set and Web menu link
	 * @param roleSetAndWebMenus
	 */
	void addListOfRoleSetAndWebMenu(List<RoleSetAndWebMenu> roleSetAndWebMenus);

	/**
	 * update list of Role Set and Web menu link
	 * @param roleSetAndWebMenus
	 */	
	void updateListOfRoleSetAndWebMenu(List<RoleSetAndWebMenu> roleSetAndWebMenus);

	/**
	 * delete by roleSetCd 
	 * @param companyId: CompanyId is companyId of login user
	 * @param roleSetCd
	 */
	void deleteAllRoleSetAndWebMenu(String roleSetCd);
}
