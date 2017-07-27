package entity.roles.auth.item;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PPEMT_PERSON_ITEM_AUTH")
@Entity
public class PpemtPersonItemAuth extends UkJpaEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public  PpemtPersonItemAuthPk ppemtPersonItemAuthPk;

	@Basic(optional = false)
	@Column(name = "OTHER_PERSON_AUTH_TYPE")
	public int otherPersonAuth;
	
	@Basic(optional = false)
	@Column(name = "SELF_AUTH_TYPE")
	public int selfAuthType;
	

	@Override
	protected Object getKey() {
		return this.ppemtPersonItemAuthPk;
	}

}
