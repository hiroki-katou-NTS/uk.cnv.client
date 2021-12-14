package nts.uk.ctx.sys.portal.app.find.webmenu.webmenulinking;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;

@Stateless
public class RoleByRoleTiesFinder {
	
	@Inject
	private RoleByRoleTiesRepository repo;
	
	public RoleByRoleTiesDto getRoleByRoleTiesByid(String roleId) {
		Optional<RoleByRoleTiesDto> data = repo.getRoleByRoleTiesById(roleId).map(c->RoleByRoleTiesDto.fromDomain(c));
		if(data != null)
			return data.get();
		return null;
		
	}

}
