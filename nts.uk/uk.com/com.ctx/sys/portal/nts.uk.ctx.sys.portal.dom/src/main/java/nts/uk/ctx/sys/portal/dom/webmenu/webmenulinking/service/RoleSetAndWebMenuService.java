/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.service;

import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleSetLinkWebMenu;

/**
 * 既�?�?�ロールセット - Interface DefaultRoleSetRepository
 * @author HieuNV
 *
 */
public interface RoleSetAndWebMenuService {
	
	/**
	 * Insert a RoleSetAndWebMenu - ロールセット別紐付け新規登録
	 * @param domain
	 */
	void createRoleSetWebMenuLink(RoleSetLinkWebMenu domain);
	
	/**
	 * Update the RoleSetAndWebMenu - ロールセット別紐付け更新登録
	 * @param domain
	 */
	void updateRoleSetWebMenuLink(RoleSetLinkWebMenu domain);
	
	/**
	 * Delete the RoleSetAndWebMenu - ロールセット別紐付け削除
	 * Company Id is login user's company id
	 * @param roleSetCd
	 */
	void deleteRoleSetWebMenuLinkByRoleCd(String roleSetCd);
}
