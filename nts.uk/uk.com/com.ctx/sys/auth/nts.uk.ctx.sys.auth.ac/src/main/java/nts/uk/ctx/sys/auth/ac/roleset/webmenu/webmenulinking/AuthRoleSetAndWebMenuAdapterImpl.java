/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.ac.roleset.webmenu.webmenulinking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetAndWebMenuImport;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetAndWebMenuAdapter;
import nts.uk.ctx.sys.portal.pub.webmenu.webmenulinking.RoleSetAndWebMenuPub;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WebMenuAdapterImpl.
 * @author HieuNV
 */
@Stateless
public class AuthRoleSetAndWebMenuAdapterImpl implements RoleSetAndWebMenuAdapter {
	
	/** The web menu pub. */
	@Inject
	private RoleSetAndWebMenuPub roleSetAndWebMenuPub;

	@Override
	public List<RoleSetAndWebMenuImport> findAllWebMenuByRoleSetCd(String roleSetCd) {
		//Get company Id
		String companyId = AppContexts.user().companyId();
		if (!StringUtils.isNoneEmpty(companyId)) {
			return this.roleSetAndWebMenuPub.findAllWebMenuByRoleSetCd(companyId, roleSetCd).stream()
					.map(item -> new RoleSetAndWebMenuImport(item.getCompanyId(), item.getWebMenuCd(), item.getRoleSetCd())
					).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public void addRoleSetAndWebMenu(RoleSetAndWebMenuImport roleSetAndWebMenu) {
		this.roleSetAndWebMenuPub.addRoleSetAndWebMenu(
				roleSetAndWebMenu.getRoleSetCd()
				, roleSetAndWebMenu.getWebMenuCd()
				, roleSetAndWebMenu.getCompanyId());
	}

	@Override
	public void updateRoleSetAndWebMenu(RoleSetAndWebMenuImport roleSetAndWebMenu) {
		this.roleSetAndWebMenuPub.updateRoleSetAndWebMenu(
				roleSetAndWebMenu.getRoleSetCd()
				, roleSetAndWebMenu.getWebMenuCd()
				, roleSetAndWebMenu.getCompanyId());
	}

	@Override
	public void deleteAllRoleSetAndWebMenu(String roleSetCd) {
		//Get company Id
		this.roleSetAndWebMenuPub.deleteRoleSetAndWebMenuByRoleSetCd(roleSetCd);
	}
	
}
