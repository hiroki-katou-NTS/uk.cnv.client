package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.Optional;

import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitAccumulationDays;

/**
 * 付与・期限情報
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GrantRegular extends DomainObject {

	/** 付与するタイミングの種類 */
	private TypeTime typeTime;

	/** 	付与基準日 */
	private Optional<GrantDate> grantDate;

	/** 	指定日付与 */
	private Optional<FixGrantDate> fixGrantDate;

	/** 	付与日テーブル参照付与 */
	private Optional<GrantDeadline> grantPeriodic;

	/** 	期間付与 */
	private Optional<PeriodGrantDate> periodGrantDate;

	@Override
	public void validate() {
		super.validate();
	}

	static public GrantRegular of(
		/** 付与するタイミングの種類 */
		TypeTime typeTime
		/** 付与基準日 */
		, Optional<GrantDate> grantDate
		/** 指定日付与 */
		, Optional<FixGrantDate> fixGrantDate
		/** 付与日テーブル参照付与 */
		, Optional<GrantDeadline> grantPeriodic
		/** 期間付与 */
		, Optional<PeriodGrantDate> periodGrantDate
	){
		GrantRegular c = new GrantRegular();
		/** 付与するタイミングの種類 */
		c.typeTime=typeTime;
		/** 付与基準日 */
		c.grantDate=grantDate;
		/** 指定日付与 */
		c.fixGrantDate=fixGrantDate;
		/** 付与日テーブル参照付与 */
		c.grantPeriodic=grantPeriodic;
		/** 期間付与 */
		c.periodGrantDate=periodGrantDate;

		return c;
	}

	/** 「付与日数一覧」の件数をチェックする */
	public int getLimitCarryoverDays() {

		if(this.typeTime==TypeTime.REFER_GRANT_DATE_TBL) {

			return this.getGrantPeriodic().flatMap(c -> c.getLimitAccumulationDays())
						.flatMap(c -> c.getLimitCarryoverDays())
						.map(c -> c.v()).orElse(0);
		}

		if(this.typeTime==TypeTime.GRANT_SPECIFY_DATE) {

			return this.getFixGrantDate().flatMap(c -> c.getGrantPeriodic().getLimitAccumulationDays())
						.flatMap(c -> c.getLimitCarryoverDays())
						.map(c -> c.v()).orElse(0);
		}

		return 0;
	}

	public Optional<GrantDeadline> getDeadline() {
		switch(this.typeTime) {
		case GRANT_SPECIFY_DATE:/** 指定日付与 */
			return Optional.of(this.fixGrantDate.get().getGrantPeriodic());
		case REFER_GRANT_DATE_TBL:
			return this.grantPeriodic;
			default:
				return Optional.empty();
		}
	}
	/**
	 * 繰越上限日数を取得
	 * @return
	 */
	public Optional<LimitAccumulationDays> getLimitAccumulationDays(){
		if(this.typeTime==TypeTime.REFER_GRANT_DATE_TBL) {
			if(this.getGrantPeriodic().isPresent()){
				return this.getGrantPeriodic().get().getLimitAccumulationDays();
			}
		}
		
		if(this.typeTime==TypeTime.GRANT_SPECIFY_DATE) {
			if(this.getFixGrantDate().isPresent()){
				return this.getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays();
			}
		}
		return Optional.empty();
	}

}
