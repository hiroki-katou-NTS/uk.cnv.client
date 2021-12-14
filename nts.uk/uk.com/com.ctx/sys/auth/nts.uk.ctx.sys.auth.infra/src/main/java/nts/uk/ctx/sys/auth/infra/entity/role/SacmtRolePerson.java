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
@Table(name = "SACMT_ROLE_PERSON")
public class SacmtRolePerson extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ROLE_ID")
	private String roleId;

	@Column(name = "REFER_FUTURE_DATE")
	private boolean referFutureDate;

	@Override
	protected Object getKey() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result + (referFutureDate ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SacmtRolePerson other = (SacmtRolePerson) obj;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (referFutureDate != other.referFutureDate)
			return false;
		return true;
	}

}
