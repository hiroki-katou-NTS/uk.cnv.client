package nts.uk.ctx.sys.portal.infra.entity.webmenu.webmenulinking;

import java.io.Serializable;

import javax.persistence.*;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "SPTMT_ROLE_BY_ROLE_TIES")
@Setter
public class SptmtRoleByRoleTies extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public SptmtRoleByRoleTiesPK pk;
	
	@Column(name = "WEB_MENU_CD")
	public String webMenuCd;
	
	@Override
	protected Object getKey() {
		return this.pk;
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
				this.pk.roleId,
				this.pk.companyId,
				new WebMenuCode(this.webMenuCd)
				);
	}


	public SptmtRoleByRoleTies(String roleId, String webMenuCd, String companyId) {
		super();
		val pk = new SptmtRoleByRoleTiesPK(roleId,companyId);
		this.pk = pk;
		this.webMenuCd = webMenuCd;
	}
}
