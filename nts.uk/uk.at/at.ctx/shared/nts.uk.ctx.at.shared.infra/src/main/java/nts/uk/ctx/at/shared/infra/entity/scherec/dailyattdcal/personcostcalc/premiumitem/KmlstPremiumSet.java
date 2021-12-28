package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name="KMLST_PREMIUM_SET")
public class KmlstPremiumSet extends UkJpaEntity{
	@EmbeddedId
	public KmlspPremiumSetPK kmlspPremiumSet;
	
	@Column(name="PREMIUM_RATE")
	public int premiumRate;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="CID",referencedColumnName="CID"), 
		@PrimaryKeyJoinColumn(name="HIS_ID",referencedColumnName="HIS_ID")
	})
	private KmlmtPersonCostCalculation kmlmtPersonCostCalculation;
	
	@OneToOne(targetEntity = KmnmtPremiumItem.class, mappedBy = "kmlstPremiumSet")
	@JoinTable(name = "KMNMT_PREMIUM_ITEM")
	public KmnmtPremiumItem kmnmtPremiumItem;
	
	@OneToMany(targetEntity = KmldtPremiumAttendance.class, cascade = CascadeType.ALL, mappedBy = "kmlstPremiumSet", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KMLDT_PREMIUM_ATTENDANCE")
	public List<KmldtPremiumAttendance> kmldtPremiumAttendances;
	
	@Override
	protected Object getKey() {
		return kmlspPremiumSet;
	}
}
