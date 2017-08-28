package nts.uk.ctx.at.shared.infra.entity.relationship;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHST_RELATIONSHIP")
/**
 * @author yennth
 */
public class KshstRelationshipItem extends UkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public KshstRelationshipPK kshstRelationshipPK;
	/* 名称 */
	@Column(name = "RELATIONSHIP_NAME")
	public String relationshipName;
	@Override
	protected Object getKey() {
		return kshstRelationshipPK;
	}
	
}
