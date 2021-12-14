package nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 * 
 * @author quytb
 *
 */
@Setter
@Getter
@Entity
@Table(name = "KSHMT_HDPUB_MGT")
public class KshmtHdPublicMgt extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = -625371437825274861L;
	
	@Id
	@Column(name = "CID")
	private String cid;
	
	@Column(name = "MGT_ATR")
	private Integer mgtAtr;
	
	@Column(name = "MGT_PERIOD_ATR")
	private Integer mgtPeriodAtr;
	
	@Column(name = "CARRY_OVER_DEADLINE")
	private Integer carryOverDeadline;
	
	@Column(name = "CARRY_FWD_MINUS_ART")
	private Integer carryFwdMinusArt;

	@Override
	protected Object getKey() {
		return this.cid;
	}
}
