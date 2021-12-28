package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 制御範囲値
 * @author tutk
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlRangeValue {
	
	/**
	 * 上限値
	 */
	private Optional<BigDecimal> upperLimit;
	
	/**
	 * 下限値
	 */
	private Optional<BigDecimal> lowerLimit;
	
	/**
	 * 入力範囲チェック
	 * @param inputValue
	 * @return
	 */
	public boolean checkInputRange(BigDecimal inputValue) {
		if(upperLimit.isPresent() && upperLimit.get().doubleValue() < inputValue.doubleValue()) {
			return false;
		}
		if(lowerLimit.isPresent() && inputValue.doubleValue() < lowerLimit.get().doubleValue()) {
			return false;
		}
		return true;
	}

}
