package nts.uk.ctx.at.function.infra.entity.alarm.extractionrange.daily;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.EndDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.PreviousClassification;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.StartDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.EndSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.ExtractionPeriodDaily;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.StartSpecify;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlstPtnDeftm;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KFNMT_ALST_PTN_DEFTMDAY")
@NoArgsConstructor

public class KfnmtExtractionPeriodDaily extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KfnmtExtractionPeriodDailyPK kfnmtExtractionPeriodDailyPK;

	@Override
	protected Object getKey() {
		return kfnmtExtractionPeriodDailyPK;
	}

	@Column(name = "STR_SPECIFY")
	public int strSpecify;

	@Column(name = "STR_PREVIOUS_DAY", nullable = true)
	public Integer strPreviousDay;

	@Column(name = "STR_MAKE_TO_DAY", nullable = true)
	public Integer strMakeToDay;

	@Column(name = "STR_DAY", nullable = true)
	public Integer strDay;

	@Column(name = "STR_PREVIOUS_MONTH", nullable = true)
	public Integer strPreviousMonth;

	@Column(name = "STR_CURRENT_MONTH", nullable = true)
	public Integer strCurrentMonth;

	@Column(name = "STR_MONTH", nullable = true)
	public Integer strMonth;

	@Column(name = "END_SPECIFY")
	public int endSpecify;

	@Column(name = "END_PREVIOUS_DAY", nullable = true)
	public Integer endPreviousDay;

	@Column(name = "END_MAKE_TO_DAY", nullable = true)
	public Integer endMakeToDay;

	@Column(name = "END_DAY", nullable = true)
	public Integer endDay;

	@Column(name = "END_PREVIOUS_MONTH", nullable = true)
	public Integer endPreviousMonth;

	@Column(name = "END_CURRENT_MONTH", nullable = true)
	public Integer endCurrentMonth;

	@Column(name = "END_MONTH", nullable = true)
	public Integer endMonth;

	@OneToOne(mappedBy = "extractionPeriodDaily", orphanRemoval = true)
	public KfnmtAlstPtnDeftm checkCondition;

	public ExtractionPeriodDaily toDomain() {
		// StartDate
		StartDate startDate = new StartDate(this.strSpecify);
		StartSpecify strPrev = EnumAdaptor.valueOf(strSpecify, StartSpecify.class);
		if (strPrev == StartSpecify.DAYS) {
			startDate.setStartDay(EnumAdaptor.valueOf(strPreviousDay, PreviousClassification.class), strDay,
					strMakeToDay == 0 ? false : true);
		} else if (strPrev == StartSpecify.MONTH) {
			startDate.setStartMonth(EnumAdaptor.valueOf(strPreviousMonth, PreviousClassification.class), strMonth,
					strCurrentMonth == 0 ? false : true);
		}

		// EndDate
		EndDate endDate = new EndDate(this.endSpecify);
		EndSpecify endPrev = EnumAdaptor.valueOf(endSpecify, EndSpecify.class);
		if (endPrev == EndSpecify.DAYS) {
			endDate.setEndDay(EnumAdaptor.valueOf(endPreviousDay, PreviousClassification.class), endDay,
					endMakeToDay == 0 ? false : true);
		} else if (endPrev == EndSpecify.MONTH) {
			endDate.setEndMonth(EnumAdaptor.valueOf(endPreviousMonth, PreviousClassification.class), endMonth,
					endCurrentMonth == 0 ? false : true);
		} 

		ExtractionPeriodDaily periodDaily = new ExtractionPeriodDaily(this.kfnmtExtractionPeriodDailyPK.extractionId,
				this.kfnmtExtractionPeriodDailyPK.extractionRange, startDate, endDate);
		return periodDaily;
	}

	public static KfnmtExtractionPeriodDaily toEntity(ExtractionPeriodDaily domain) {
		return new KfnmtExtractionPeriodDaily(domain.getExtractionId(), domain.getExtractionRange().value,
				domain.getStartDate(), domain.getEndDate());
	}

	public KfnmtExtractionPeriodDaily(String extractionId, int extractionRange, StartDate startDate, EndDate endDate) {
		this.kfnmtExtractionPeriodDailyPK = new KfnmtExtractionPeriodDailyPK(extractionId, extractionRange);
		// set start date
		StartSpecify strPrev = EnumAdaptor.valueOf(startDate.getStartSpecify().value, StartSpecify.class);
		this.strSpecify = startDate.getStartSpecify().value;
		if (strPrev == StartSpecify.DAYS) {
			this.strPreviousDay = startDate.getStrDays().get().getDayPrevious().value;
			this.strMakeToDay = startDate.getStrDays().get().isMakeToDay() == true ? 1 : 0;
			this.strDay = startDate.getStrDays().get().getDay().v();
		} else if (strPrev == StartSpecify.MONTH) {
			this.strPreviousMonth = startDate.getStrMonth().get().getMonthPrevious().value;
			this.strCurrentMonth = startDate.getStrMonth().get().isCurentMonth() == true ? 1 : 0;
			this.strMonth = startDate.getStrMonth().get().getMonth();
		}

		// set end date
		EndSpecify endPrev = EnumAdaptor.valueOf(endDate.getEndSpecify().value, EndSpecify.class);
		this.endSpecify = endDate.getEndSpecify().value;
		if (endPrev == EndSpecify.DAYS) {
			this.endPreviousDay = endDate.getEndDays().get().getDayPrevious().value;
			this.endMakeToDay = endDate.getEndDays().get().isMakeToDay() == true ? 1 : 0;
			this.endDay = endDate.getEndDays().get().getDay().v();
		} else if (endPrev == EndSpecify.MONTH) {
			this.endPreviousMonth = endDate.getEndMonth().get().getMonthPrevious().value;
			this.endCurrentMonth = endDate.getEndMonth().get().isCurentMonth() == true ? 1 : 0;
			this.endMonth = endDate.getEndMonth().get().getMonth();
		}
	}
	
	public void fromEntity(KfnmtExtractionPeriodDaily newEntity) {
		this.strSpecify = newEntity.strSpecify;
		this.strPreviousDay = newEntity.strPreviousDay;
		this.strMakeToDay = newEntity.strMakeToDay;
		this.strDay = newEntity.strDay;
		this.strPreviousMonth = newEntity.strPreviousMonth;
		this.strCurrentMonth = newEntity.strCurrentMonth;		
		this.strMonth = newEntity.strMonth;
		this.endSpecify = newEntity.endSpecify;
		this.endPreviousDay = newEntity.endPreviousDay;
		this.endMakeToDay = newEntity.endMakeToDay;
		this.endDay = newEntity.endDay;
		this.endPreviousMonth = newEntity.endPreviousMonth;
		this.endCurrentMonth = newEntity.endCurrentMonth;		
		this.endMonth = newEntity.endMonth;	
	}
}
