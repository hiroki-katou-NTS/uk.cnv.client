package nts.uk.ctx.sys.portal.app.find.webmenu.webmenulinking;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;

@Stateless
public class RoleByRoleTiesFinder {
	
	@Inject
	private RoleByRoleTiesRepository repo;
	
	public RoleByRoleTiesDto getRoleByRoleTiesByid(String roleId) {
//	TODO　修正お願いいたします。		
/*		Optional<RoleByRoleTiesDto> data = repo.getRoleByRoleTiesById(roleId).map(c->RoleByRoleTiesDto.fromDomain(c));
		if(data != null)
			return data.get();
*/
		return null;
		
	}

}
