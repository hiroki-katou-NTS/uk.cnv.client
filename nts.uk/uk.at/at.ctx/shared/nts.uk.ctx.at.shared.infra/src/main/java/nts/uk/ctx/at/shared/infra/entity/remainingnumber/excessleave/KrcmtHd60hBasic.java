package nts.uk.ctx.at.shared.infra.entity.remainingnumber.excessleave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KRCMT_HD60H_BASIC")
public class KrcmtHd60hBasic extends ContractUkJpaEntity{

    @Column(name = "CID")
    public String cID;
	
	@Id
    @Column(name = "SID")
    public String employeeId;
	
	@Column(name="USE_ATR")
	public int useAtr;
	
	@Column(name="OCCURRENCE_UNIT")
	public int occurrenceUnit;
	
	@Column(name ="PAYMENT_METHOD")
	public int paymentMethod;

	@Override
	protected Object getKey() {
		return employeeId;
	}

}
