package nts.uk.ctx.at.shared.dom.scherec.optitem.calculation;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemNo;

/**
 * 任意項目の計算結果
 * @author keisuke_hoshina
 *
 */
@Getter
@AllArgsConstructor
public class CalcResultOfAnyItem {
	private OptionalItemNo optionalItemNo;
	Optional<BigDecimal> count;
	Optional<BigDecimal> time;
	Optional<BigDecimal> money;
	
	
	/**
	 * 指定された属性の項目のみ渡された値で更新するx
	 * @param value
	 * @param optionalItemAtr
	 * @return
	 */
	public CalcResultOfAnyItem reCreateCalcResultOfAnyItem(Optional<BigDecimal> value, OptionalItemAtr optionalItemAtr) {
		switch (optionalItemAtr) {
		case NUMBER:
			return new CalcResultOfAnyItem(this.optionalItemNo, value, Optional.empty(), Optional.empty());
		case AMOUNT:
			return new CalcResultOfAnyItem(this.optionalItemNo, Optional.empty(), Optional.empty(), value);
		case TIME:
			return new CalcResultOfAnyItem(this.optionalItemNo, Optional.empty(), value, Optional.empty());
		default:
			throw new RuntimeException("unknown value of enum OptionalItemAtr");
		}
	}
	
}
