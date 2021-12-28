package nts.uk.ctx.at.shared.infra.entity.worktime.filtercondition;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KSHMT_WT_SEARCH_CONDITION")
@NoArgsConstructor
@AllArgsConstructor
public class KshmtWtSearchCondition extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KshmtWtSearchConditionPK pk;
	
	/**
	 * 使用区分
	 */
	@NotNull
	@Column(name = "USE_ATR")
	public boolean useAtr;
	
	/**
	 * 絞り込み条件名称
	 */
	@Basic(optional = true)
	@Column(name = "NAME")
	public String name;

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
