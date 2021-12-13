package nts.uk.ctx.at.function.infra.entity.alarm.extractionrange.yearly;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.year.AYear;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlstPtnDeftm;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KFNMT_ALST_PTN_DEFTMYEAR")
@NoArgsConstructor
public class KfnmtAlstPtnDeftmyear extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KfnmtExtractRangeYearPK pk;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	@Column(name = "YEAR")
	public int year;
	
	@Column(name = "THIS_YEAR")
	public boolean thisYear;
	
	@OneToOne(mappedBy = "extractRangeYear", orphanRemoval = true)
	public KfnmtAlstPtnDeftm checkCondition;
	
	public void fromEntity(KfnmtAlstPtnDeftmyear newEntity) {
		this.year = newEntity.year;
		this.thisYear = newEntity.thisYear;
	}
	
	public AYear toDomain() {
		return new AYear(this.pk.extractionId, this.pk.extractionRange, year, thisYear);
	}
	
	public KfnmtAlstPtnDeftmyear(AYear domain) {
		this.pk = new KfnmtExtractRangeYearPK(domain.getExtractionId(), domain.getExtractionRange().value);
		this.year = domain.getYear();
		this.thisYear = domain.isToBeThisYear();
	}
	public static KfnmtAlstPtnDeftmyear toEntity(AYear domain) {
		return new KfnmtAlstPtnDeftmyear(domain);
	}
}
