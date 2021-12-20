package nts.uk.ctx.at.function.infra.entity.alarm.extractionrange.monthly;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.NumberOfMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.PreviousClassification;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.EndMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.ExtractionPeriodMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.SpecifyStartMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.StartMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.YearSpecifiedType;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlstPtnDeftm;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KFNMT_ALST_PTN_DEFTMMON")
@NoArgsConstructor
public class KfnmtExtractPeriodMonth extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KfnmtExtractPeriodMonthPK pk;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	@Column(name = "STR_SPECIFY")
	public int strSpecify;
	
	@Column(name = "YEAR_TYPE")
	public int yearType;
	
	@Column(name = "SPECIFY_MONTH")
	public int specifyMonth;
	
	@Column(name = "STR_MONTH")
	public int strMonth;
	
	@Column(name = "STR_CURRENT_MONTH")
	public boolean strCurrentMonth;
	
	@Column(name = "STR_PREVIOUS_ATR")
	public int strPreviousAtr;
	
	@Column(name = "END_SPECIFY")
	public int endSpecify;
	
	@Column(name = "EXTRACT_PERIOD")
	public int extractPeriod;
	
	@Column(name = "END_MONTH")
	public int endMonth;
	
	@Column(name = "END_CURRENT_MONTH")
	public boolean endCurrentMonth;
	
	@Column(name = "END_PREVIOUS_ATR")
	public int endPreviousAtr;
	

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
		@JoinColumn(name = "ALARM_PATTERN_CD", referencedColumnName = "ALARM_PATTERN_CD", insertable = false, updatable = false), 
		@JoinColumn(name = "ALARM_CATEGORY", referencedColumnName = "ALARM_CATEGORY", insertable = false, updatable = false)
		})
	public KfnmtAlstPtnDeftm checkCondition;
	
	
	public void fromEntity(KfnmtExtractPeriodMonth newEntity) {
		this.strSpecify = newEntity.strSpecify;
		this.yearType = newEntity.yearType;
		this.specifyMonth = newEntity.specifyMonth;
		this.strMonth = newEntity.strMonth;
		this.strCurrentMonth = newEntity.strCurrentMonth;		
		this.strPreviousAtr = newEntity.strPreviousAtr;
		this.endSpecify = newEntity.endSpecify;
		this.extractPeriod = newEntity.extractPeriod;
		this.endMonth = newEntity.endMonth;
		this.endCurrentMonth = newEntity.endCurrentMonth;
		this.endPreviousAtr = newEntity.endPreviousAtr;
	}
	
	
	public ExtractionPeriodMonth toDomain(String extractionId, int extractionRange) {		
		StartMonth startMonth = new StartMonth(strSpecify);
		
		if(this.strSpecify==SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH.value) {
			startMonth.setStartMonth(EnumAdaptor.valueOf(strPreviousAtr, PreviousClassification.class), strMonth, strCurrentMonth);
		}else {
			startMonth.setFixedMonth(EnumAdaptor.valueOf(yearType, YearSpecifiedType.class), specifyMonth);
		}
		
		EndMonth endMonth = new EndMonth(endSpecify, extractPeriod);
		endMonth.setEndMonthNo(EnumAdaptor.valueOf(endPreviousAtr, PreviousClassification.class), this.endMonth, endCurrentMonth);
		
		return new ExtractionPeriodMonth(extractionId, extractionRange, startMonth, endMonth, EnumAdaptor.valueOf(pk.unit, NumberOfMonth.class));
	}
	
	public KfnmtExtractPeriodMonth(String companyId, String alarmPatternCode, int alarmCategory, ExtractionPeriodMonth domain) {
		this.pk = new KfnmtExtractPeriodMonthPK(companyId, alarmPatternCode, alarmCategory, domain.getNumberOfMonth().value);
		this.strSpecify= domain.getStartMonth().getSpecifyStartMonth().value;
		this.yearType = domain.getStartMonth().getFixedMonthly().isPresent() ? domain.getStartMonth().getFixedMonthly().get().getYearSpecifiedType().value: YearSpecifiedType.CURRENT_YEAR.value;
		this.specifyMonth = domain.getStartMonth().getFixedMonthly().isPresent()? domain.getStartMonth().getFixedMonthly().get().getDesignatedMonth(): 0;
		this.strMonth = domain.getStartMonth().getStrMonthNo().isPresent() ? domain.getStartMonth().getStrMonthNo().get().getMonthNo(): 0;
		this.strCurrentMonth = domain.getStartMonth().getStrMonthNo().isPresent() ? (domain.getStartMonth().getStrMonthNo().get().isCurentMonth()) : true;
		this.strPreviousAtr = PreviousClassification.BEFORE.value;
		this.endSpecify = domain.getEndMonth().getSpecifyEndMonth().value;
		this.extractPeriod = domain.getEndMonth().getExtractFromStartMonth().value;
		this.endMonth = domain.getEndMonth().getEndMonthNo() != null && domain.getEndMonth().getEndMonthNo().isPresent()  ? domain.getEndMonth().getEndMonthNo().get().getMonthNo() : 0;
		this.endCurrentMonth = domain.getEndMonth().getEndMonthNo() != null && domain.getEndMonth().getEndMonthNo().isPresent() && domain.getEndMonth().getEndMonthNo().get().isCurentMonth();
		this.endPreviousAtr = domain.getEndMonth().getEndMonthNo() != null && domain.getEndMonth().getEndMonthNo().isPresent() ? domain.getEndMonth().getEndMonthNo().get().getMonthPrevious().value : 0;
	}
	
	public static KfnmtExtractPeriodMonth toEntity(String companyId, String alarmPatternCode, int alarmCategory, ExtractionPeriodMonth domain) {
		return new KfnmtExtractPeriodMonth(companyId, alarmPatternCode, alarmCategory, domain);		
	}
}
