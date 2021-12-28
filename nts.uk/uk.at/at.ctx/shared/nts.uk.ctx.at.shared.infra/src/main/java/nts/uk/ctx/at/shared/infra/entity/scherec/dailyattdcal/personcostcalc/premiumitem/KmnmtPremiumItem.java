package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
//import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name="KMNMT_PREMIUM_ITEM")
public class KmnmtPremiumItem extends UkJpaEntity {
	@EmbeddedId
	public KmnmpPremiumItemPK kmnmpPremiumItemPK;
	
	@Column(name="PREMIUM_NAME")
	public String name;
	
	@Column(name="USE_ATR")
	public int useAtr;
	
	@OneToOne
	@PrimaryKeyJoinColumns(value = {
		@PrimaryKeyJoinColumn(name="CID",referencedColumnName="CID"),
		@PrimaryKeyJoinColumn(name="PREMIUM_NO",referencedColumnName="PREMIUM_NO")
    })
	public KmlstPremiumSet kmlstPremiumSet;

	@Override
	protected Object getKey() {
		return kmnmpPremiumItemPK;
	}
}
