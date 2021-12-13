package nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name ="KRCDT_HDPAID_MAX")
public class KrcmtAnnLeaMax extends ContractUkJpaEntity{
	
	@Id
    @Column(name = "SID")
    public String sid;
	
	@Column(name = "MAX_TIMES")
    public Integer maxTimes;
	
	@Column(name = "USED_TIMES")
    public Integer usedTimes;
	
	@Column(name = "REMAINING_TIMES")
    public Integer remainingTimes;
	
	@Column(name = "MAX_MINUTES")
    public Integer maxMinutes;
	
	@Column(name = "USED_MINUTES")
    public Integer usedMinutes;
	
	@Column(name = "REMAINING_MINUTES")
    public Integer remainingMinutes;
	
	@Override
	protected Object getKey() {
		return sid;
	}

}
