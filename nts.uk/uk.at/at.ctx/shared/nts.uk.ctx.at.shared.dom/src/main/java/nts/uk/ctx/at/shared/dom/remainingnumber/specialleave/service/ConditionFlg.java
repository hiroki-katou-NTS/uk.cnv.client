package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 * 条件フラグ
 * @author do_dt
 *
 */
@AllArgsConstructor
@Setter
@Getter
public class ConditionFlg {
	/**
	 * 雇用条件に一致する
	 */
	private boolean employmentFlg;
	/**
	 * 性別条件に一致する
	 */
	private boolean genderFlg;
	/**
	 * 年齢条件に一致する
	 */
	private boolean ageFlg;
	
	/**
	 * 分類条件に一致する
	 */
	private boolean classFlg;
	
	/**
	 * 利用可能か？
	 * @return
	 */
	public boolean canUse(){
		return (employmentFlg
				&& genderFlg
				&& ageFlg
				&& classFlg);
	}
}
