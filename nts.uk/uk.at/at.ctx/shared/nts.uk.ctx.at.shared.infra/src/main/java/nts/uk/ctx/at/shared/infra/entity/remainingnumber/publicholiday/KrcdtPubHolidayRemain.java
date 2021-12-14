package nts.uk.ctx.at.shared.infra.entity.remainingnumber.publicholiday;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="KRCDT_PUB_HOLIDAY_REMAIN")
public class KrcdtPubHolidayRemain extends ContractUkJpaEntity {
	
	@Column(name = "CID")
    public String cid;
	
	@Id
    @Column(name = "SID")
    public String employeeId;
	
	@Column(name = "REMAIN_NUMBER")
    public BigDecimal remainNumber;
	
	@Override
	protected Object getKey() {
		return this.employeeId;
	}

}
