package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.*;

//import javax.persistence.JoinTable;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Getter
@Table(name="KSRMT_PREMIUM_ITEM")
public class KscmtPremiumItem extends ContractUkJpaEntity {
	@EmbeddedId
	public KmnmpPremiumItemPK kmnmpPremiumItemPK;
	
	@Column(name="PREMIUM_NAME")
	public String name;
	
	@Column(name="USE_ATR")
	public int useAtr;

//	@OneToOne
//	@PrimaryKeyJoinColumns(value = {
//		@PrimaryKeyJoinColumn(name="CID",referencedColumnName="CID"),
//		@PrimaryKeyJoinColumn(name="PREMIUM_NO",referencedColumnName="PREMIUM_NO")
//    })
//	public KscmtPerCostPremiRate kmlstPremiumSet;
	/** The kscst est aggregate set. */
	@Override
	protected Object getKey() {
		return kmnmpPremiumItemPK;
	}
}
