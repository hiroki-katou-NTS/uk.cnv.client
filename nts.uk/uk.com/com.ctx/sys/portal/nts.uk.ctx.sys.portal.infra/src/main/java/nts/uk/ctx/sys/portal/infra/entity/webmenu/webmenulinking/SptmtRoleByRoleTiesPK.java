package nts.uk.ctx.sys.portal.infra.entity.webmenu.webmenulinking;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SptmtRoleByRoleTiesPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "ROLE_ID")
	public String roleId;

	@Column(name = "CID")
	public String companyId;

}
