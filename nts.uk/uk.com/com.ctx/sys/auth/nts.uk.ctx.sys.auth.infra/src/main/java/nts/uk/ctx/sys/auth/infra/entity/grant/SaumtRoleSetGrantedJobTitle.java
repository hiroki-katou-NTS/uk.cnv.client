package nts.uk.ctx.sys.auth.infra.entity.grant;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 
 * @author HungTT
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "SAUMT_ROLESET_JOB")
public class SaumtRoleSetGrantedJobTitle extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CID")
	public String companyId;

	@Basic(optional = false)
	@Column(name = "APPLY_CONCURRENT_PERSON")
	public boolean applyToConcurrentPerson;

	@Override
	protected Object getKey() {
		return this.companyId;
	}

	public SaumtRoleSetGrantedJobTitle(String companyId, boolean applyToConcurrentPerson) {
		super();
		this.companyId = companyId;
		this.applyToConcurrentPerson = applyToConcurrentPerson;
	}

}
