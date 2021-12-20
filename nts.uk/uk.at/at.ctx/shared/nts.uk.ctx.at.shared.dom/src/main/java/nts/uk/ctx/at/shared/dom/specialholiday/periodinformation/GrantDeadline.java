package nts.uk.ctx.at.shared.dom.specialholiday.periodinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYear;

/**
 * 期限情報
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GrantDeadline extends DomainObject {

	/** 期限指定方法 */
	private TimeLimitSpecification timeSpecifyMethod;

	/** 有効期限 */
	private Optional<SpecialVacationDeadline> expirationDate;

	/** 蓄積上限 */
	private Optional<LimitAccumulationDays> limitAccumulationDays;


	@Override
	public void validate() {
		super.validate();
	}

	/**
	 * Validate input data
	 */
	public List<String> validateInput() {
		List<String> errors = new ArrayList<>();

		// 0年0ヶ月は登録不可
		if(this.getTimeSpecifyMethod() == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
			if (this.expirationDate.isPresent()) {
				if (this.expirationDate.get().getMonths().v() == 0 && this.expirationDate.get().getYears().v() == 0) {
					errors.add("Msg_95");
				}
			}
		}

		return errors;
	}

	/**
	 * Create from Java Type
	 * @return
	 */
	public static GrantDeadline createFromJavaType(
			int timeSpecifyMethod,
			Optional<SpecialVacationDeadline> deadline,
			Integer limitCarryoverDays) {

		Optional<LimitAccumulationDays> accumulationDays = Optional.empty();
		Optional<LimitCarryoverDays> carryOverDays = Optional.empty();
		if(limitCarryoverDays != null)
			carryOverDays = Optional.of(new LimitCarryoverDays(limitCarryoverDays));

		if(limitCarryoverDays != null)
			accumulationDays = Optional.of(new LimitAccumulationDays(true, carryOverDays));

		return new GrantDeadline(
				EnumAdaptor.valueOf(timeSpecifyMethod, TimeLimitSpecification.class),
				deadline,
				accumulationDays);

	}
	
	/**
	 * 期限日を取得する
	 * @param grantDate
	 * @param grantReferenceDate
	 * @param elapseNo
	 * @param elapseYear
	 * @return
	 */
	public GeneralDate calcDeadLine(GeneralDate grantDate, Optional<GeneralDate> grantReferenceDate,
			Optional<Integer> elapseNo, Optional<ElapseYear> elapseYear) {

		// 無期限
		if (this.getTimeSpecifyMethod().equals(TimeLimitSpecification.INDEFINITE_PERIOD)) {

			// パラメータ「次回特別休暇付与」の期限を求める
			// ・期限日 ← 9999/12/31
			return (GeneralDate.ymd(9999, 12, 31));
		}
		// 有効期限を指定する
		else if (this.getTimeSpecifyMethod().equals(TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE)) {

			if(this.getExpirationDate().isPresent()){
				return this.getExpirationDate().get().calcDeadline(grantDate);
			}else {
				throw new RuntimeException();
			}

		}
		// 次回付与日まで使用可能
		else if (this.getTimeSpecifyMethod().equals(TimeLimitSpecification.AVAILABLE_UNTIL_NEXT_GRANT_DATE)) {

			// パラメータ「次回特別休暇付与」の期限を求める
			// ・期限日 ← パラメータ「次回付与年月日」の前日

			return this.calcDeadLineNextGrantDate(grantDate, grantReferenceDate, elapseNo, elapseYear);
		} else {
			throw new RuntimeException();
		}
	}
	
	
	/**
	 * 次回付与日まで使用可能の場合の期限日を求める
	 * @param grantDate
	 * @param grantReferenceDate
	 * @param elapseNo
	 * @param elapseYear
	 * @return
	 */
	public GeneralDate calcDeadLineNextGrantDate(GeneralDate grantDate, Optional<GeneralDate> grantReferenceDate,
			Optional<Integer> elapseNo, Optional<ElapseYear> elapseYear) {
		
		GeneralDate nextGrantDate = grantDate.addYears(1);
		
		if(grantReferenceDate.isPresent() && elapseNo.isPresent() && elapseYear.isPresent()){
			Optional<GeneralDate> nextGrantDateOp = elapseYear.get().getGrantDate(grantReferenceDate.get(), elapseNo.get());
			
			if(nextGrantDateOp.isPresent()){
				nextGrantDate = nextGrantDateOp.get();
			}	
		}
		
		return nextGrantDate.addDays(-1);
	}
	
	/**
	 * 蓄積上限日数を取得する
	 * @return
	 */
	public Optional<LimitCarryoverDays> getLimitCarryoverDays(){
		if(this.limitAccumulationDays.isPresent()){
			return this.limitAccumulationDays.get().getCarryoverDays();
		}
		return Optional.empty();
	}
}
	
