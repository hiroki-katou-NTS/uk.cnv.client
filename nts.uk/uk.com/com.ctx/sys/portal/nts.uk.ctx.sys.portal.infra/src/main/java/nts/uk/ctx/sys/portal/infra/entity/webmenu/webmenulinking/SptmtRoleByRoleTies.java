package nts.uk.ctx.sys.portal.infra.entity.webmenu.webmenulinking;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "SPTMT_ROLE_BY_ROLE_TIES")
@Setter
public class SptmtRoleByRoleTies extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ROLE_ID")
	public String roleId;
	
	@Column(name = "WEB_MENU_CD")
	public String webMenuCd;
	
	@Column(name = "CID")
	public String companyId;
	
	@Override
	protected Object getKey() {
		return this.roleId;
	}

	
	public static SptmtRoleByRoleTies toEntity(RoleByRoleTies domain) {
		return new SptmtRoleByRoleTies(
				domain.getRoleId(),
				domain.getWebMenuCd().v(),
				domain.getCompanyId()
				);
	}
	
	public RoleByRoleTies toDomain() {
		return new RoleByRoleTies(
				this.roleId,
				new WebMenuCode(this.webMenuCd),
				this.companyId
				);
	}


	public SptmtRoleByRoleTies(String roleId,String webMenuCd, String companyId) {
		super();
		this.roleId = roleId;
		this.webMenuCd = webMenuCd;
		this.companyId = companyId;
	}
}
