package nts.uk.ctx.at.shared.infra.entity.workusage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


/**
 * The persistent class for the KSHMT_TEMPORARY_MNG database table.
 * 
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="KSHMT_TEMPORARY_MNG")
public class KshmtTemporaryMng extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KshstTempWkUseManagePK id;

	@Column(name="USE_CLASSIFICATION")
	private boolean useClassification;

	@Override
	protected Object getKey() {
		return id;
	}
}