package nts.uk.ctx.at.shared.infra.entity.workrule.deformed;

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
 * The persistent class for the KSHMT_DEFORMED_LABOR_MNG database table.
 * @author HoangNDH
 * 変形労働の集計設定
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="KSHMT_DEFORMED_LABOR_MNG")
public class KshmtDeformedLaborMng extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KshstAggLaborSetPK id;
	
	/** 変形労働を使用する */
	@Column(name="USE_DEFORM_LABOR")
	private boolean useDeformLabor;

	@Override
	protected Object getKey() {
		return id;
	}


}