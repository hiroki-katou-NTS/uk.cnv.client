/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.roleset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetAndWebMenu;

/**
 * ロールセット - Class RoleSet.
 * @author HieuNV
 */
@Getter
public class RoleSet extends AggregateRoot {

	/** ロールセットコード. */
	private RoleSetCode roleSetCd;

	/** 会社ID */
	private String companyId;

	/** ロールセット名称*/
	private RoleSetName roleSetName;

	/** 承認権限*/
	private ApprovalAuthority approvalAuthority;

	/** ロールID: オフィスヘルパーロール */
	private String officeHelperRoleId;

	/** ロールID: マイナンバーロール */
	private String myNumberRoleId;

	/** ロールID: 人事ロール */
	private String hRRoleId;

	/** ロールID: 個人情報ロール */
	private String personInfRoleId;

	/** ロールID: 就業ロール */
	private String employmentRoleId;

	/** ロールID: 給与ロール */
	private String salaryRoleId;

	/** list of Web menu code link */
	List<String> roleSetAndWebMenuCds;
	/**
	 * Instantiates a new role set.
	 *
	 * @param roleSetCd
	 * @param companyId
	 * @param roleSetName
	 * @param approvalAuthority
	 * @param officeHelperRoleId
	 * @param myNumberRoleId
	 * @param hRRoleId
	 * @param personInfRoleId
	 * @param employmentRoleId
	 * @param salaryRoleId
	 */
	public RoleSet(String roleSetCd
			, String companyId
			, String roleSetName
			, ApprovalAuthority approvalAuthority
			, String officeHelperRoleId
			, String myNumberRoleId
			, String hRRoleId
			, String personInfRoleId
			, String employmentRoleId
			, String salaryRoleId) {
		super();
		this.roleSetCd 			= new RoleSetCode(roleSetCd);
		this.companyId 			= companyId;
		this.roleSetName 		= new RoleSetName(roleSetName);
		this.approvalAuthority 	= approvalAuthority;
		this.officeHelperRoleId = officeHelperRoleId;
		this.myNumberRoleId 	= myNumberRoleId;
		this.hRRoleId 			= hRRoleId;
		this.personInfRoleId 	= personInfRoleId;
		this.employmentRoleId 	= employmentRoleId;
		this.salaryRoleId 		= salaryRoleId;
		this.roleSetAndWebMenuCds = RoleSetUtils.buildRoleSetAndWebMenu(roleSetCd);
	}
	
	
	/**
	 * Initial a new RoleSet with list of WebMenu code.
	 * 
	 * @param roleSetCd
	 * @param companyId
	 * @param roleSetName
	 * @param approvalAuthority
	 * @param officeHelperRoleId
	 * @param myNumberRoleId
	 * @param hRRoleId
	 * @param personInfRoleId
	 * @param employmentRoleId
	 * @param salaryRoleId
	 * @param webMenuCds
	 */
	public RoleSet(String roleSetCd
			, String companyId
			, String roleSetName
			, ApprovalAuthority approvalAuthority
			, String officeHelperRoleId
			, String myNumberRoleId
			, String hRRoleId
			, String personInfRoleId
			, String employmentRoleId
			, String salaryRoleId
			, List<String> webMenuCds) {
		this.roleSetCd 			= new RoleSetCode(roleSetCd);
		this.companyId 			= companyId;
		this.roleSetName 		= new RoleSetName(roleSetName);
		this.approvalAuthority 	= approvalAuthority;
		this.officeHelperRoleId = (officeHelperRoleId);
		this.myNumberRoleId 	= myNumberRoleId;
		this.hRRoleId 			= hRRoleId;
		this.personInfRoleId 	= personInfRoleId;
		this.employmentRoleId 	= employmentRoleId;
		this.salaryRoleId 		= salaryRoleId;
		this.roleSetAndWebMenuCds = webMenuCds;		
	}
	
	/**
	 * If has approval Authority right.
	 *
	 * @return true
	 */
	public boolean hasApprovalAuthority() {
		return this.approvalAuthority == ApprovalAuthority.HasRight;
	}

	/**
	 * If hasn't approval Authority right.
	 *
	 * @return true
	 */
	public boolean hasntApprovalAuthority() {
		return this.approvalAuthority == ApprovalAuthority.HasntRight;
	}

	/**
	 * set approval authority
	 */
	public void setApprovalAuthority() {
		this.approvalAuthority = ApprovalAuthority.HasRight;
	}

	/**
	 * Remove approval authority
	 */
	public void removeApprovalAuthority() {
		this.approvalAuthority = ApprovalAuthority.HasntRight;
	}

	/**
	 * remove value of PersonInfRole field
	 */
	public void removePersonInfRole() {
		this.personInfRoleId = null;
	}
}
