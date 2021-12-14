/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.entity.role;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Getter
@Setter
@Table(name = "SACMT_ROLE")
public class SacmtRole extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ROLE_ID")
	private String roleId;

	@Column(name = "CID")
	private String cid;

	@Column(name = "ROLE_CD")
	private String code;

	@Column(name = "ROLE_TYPE")
	private Integer roleType;

	@Column(name = "REF_RANGE")
	private Integer referenceRange;

	@Column(name = "ROLE_NAME")
	private String name;

	@Column(name = "ASSIGN_ATR")
	private int assignAtr;

	public SacmtRole() {
	}

	public SacmtRole(String id) {
		this.roleId = id;
	}

	public SacmtRole(String id, int exclusVer, String code, Integer roleType, Integer referenceRange, String name,
			String contractCode, int assignAtr) {
		this.roleId = id;
		this.code = code;
		this.roleType = roleType;
		this.referenceRange = referenceRange;
		this.name = name;
		this.assignAtr = assignAtr;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (roleId != null ? roleId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SacmtRole)) {
			return false;
		}
		SacmtRole other = (SacmtRole) object;
		if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "entity.SacmtRole[ id=" + roleId + " ]";
	}

	@Override
	protected Object getKey() {
		return this.roleId;
	}
}
