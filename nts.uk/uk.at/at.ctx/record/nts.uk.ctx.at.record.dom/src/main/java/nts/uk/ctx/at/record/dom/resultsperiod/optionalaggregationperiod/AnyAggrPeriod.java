package nts.uk.ctx.at.record.dom.resultsperiod.optionalaggregationperiod;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.resultsperiod.optionalaggregationperiod.primitivevalue.AnyAggrFrameCode;
import nts.uk.ctx.at.record.dom.resultsperiod.optionalaggregationperiod.primitivevalue.AnyAggrName;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).任意期間の勤怠計算.任意集計期間.任意集計期間
 *
 * @author nws-minhnb
 */
@Getter
public class AnyAggrPeriod extends AggregateRoot {

	/** 会社ID */
	private String companyId;

	/** 任意集計枠コード */
	private AnyAggrFrameCode aggrFrameCode;

	/** 任意集計名称 */
	private AnyAggrName optionalAggrName;

	/** 対象期間 */
	private DatePeriod period;

	/**
	 * No args constructor of domain AnyAggrPeriod.
	 */
	private AnyAggrPeriod() {
	}

	/**
	 * Creates domain from memento.
	 *
	 * @param companyId the company id
	 * @param memento the Memento getter
	 * @return the any aggr period
	 */
	public static AnyAggrPeriod createFromMemento(String companyId, MementoGetter memento) {
		AnyAggrPeriod domain = new AnyAggrPeriod();
		domain.getMemento(memento);
		domain.companyId = companyId;
		return domain;
	}

	/**
	 * Gets memento.
	 *
	 * @param memento the Memento getter
	 */
	public void getMemento(MementoGetter memento) {
		this.aggrFrameCode = new AnyAggrFrameCode(memento.getAggrFrameCode());
		this.optionalAggrName = new AnyAggrName(memento.getOptionalAggrName());
		this.period = memento.getPeriod();
	}

	/**
	 * Sets memento.
	 *
	 * @param memento the Memento setter
	 */
	public void setMemento(MementoSetter memento) {
		memento.setCompanyId(this.companyId);
		memento.setAggrFrameCode(this.aggrFrameCode.v());
		memento.setOptionalAggrName(this.optionalAggrName.v());
		memento.setPeriod(this.period);
	}

	/**
	 * The interface Memento setter.
	 */
	public static interface MementoSetter {

		/**
		 * Sets company id.
		 *
		 * @param companyId the company id
		 */
		void setCompanyId(String companyId);

		/**
		 * Sets aggr frame code.
		 *
		 * @param aggrFrameCode the aggr frame code
		 */
		void setAggrFrameCode(String aggrFrameCode);

		/**
		 * Sets optional aggr name.
		 *
		 * @param optionalAggrName the optional aggr name
		 */
		void setOptionalAggrName(String optionalAggrName);

		/**
		 * Sets period.
		 *
		 * @param period the period
		 */
		void setPeriod(DatePeriod period);
	}

	/**
	 * The interface Memento getter.
	 */
	public static interface MementoGetter {

		/**
		 * Gets company id.
		 *
		 * @return the company id
		 */
		String getCompanyId();

		/**
		 * Gets aggr frame code.
		 *
		 * @return the aggr frame code
		 */
		String getAggrFrameCode();

		/**
		 * Gets optional aggr name.
		 *
		 * @return the optional aggr name
		 */
		String getOptionalAggrName();

		/**
		 * Gets period.
		 *
		 * @return the period
		 */
		DatePeriod getPeriod();
	}

}
