package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;

import java.math.BigDecimal;

import org.eclipse.persistence.internal.xr.ValueObject;


/**
 * VO: 人件費単価丸め
 */
@AllArgsConstructor
@Getter
public class UnitPriceRoundingSetting extends ValueObject{
    //1 : 端数処理 :人件費単価端数処理

    private  UnitPriceRounding priceRounding;

	/**
	 * 丸める
	 * @param timeAsMinutes
	 * @return 丸め後の値
	 */
	public BigDecimal round(BigDecimal timeAsMinutes) {
		AmountRoundingSetting amount = new AmountRoundingSetting(AmountUnit.ONE_YEN, this.priceRounding.amountRounding);
		return amount.round(timeAsMinutes);
	}
}
