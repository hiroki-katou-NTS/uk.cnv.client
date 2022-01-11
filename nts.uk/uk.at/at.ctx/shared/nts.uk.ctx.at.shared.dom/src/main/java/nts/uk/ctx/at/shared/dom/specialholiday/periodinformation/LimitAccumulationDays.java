package nts.uk.ctx.at.shared.dom.specialholiday.periodinformation;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 蓄積上限日数
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LimitAccumulationDays {

	/** 蓄積上限日数を制限する */
	private boolean limit = true;

	/** 繰越上限日数 */
	private Optional<LimitCarryoverDays> limitCarryoverDays;
	
	
	/**
	 * 蓄積上限日数を取得する
	 * @return
	 */
	public Optional<LimitCarryoverDays> getCarryoverDays(){
		if(this.isLimit()){
			return this.getLimitCarryoverDays();
		}
		return Optional.empty();
	}
	
}
