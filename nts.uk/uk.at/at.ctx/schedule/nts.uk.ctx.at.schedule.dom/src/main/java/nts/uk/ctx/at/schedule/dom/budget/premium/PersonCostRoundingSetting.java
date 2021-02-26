package nts.uk.ctx.at.schedule.dom.budget.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.premiumitem.PersonCostRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.premiumitem.UnitPriceRounding;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.premiumitem.UnitPriceRoundingSetting;

import org.eclipse.persistence.internal.xr.ValueObject;

/**
 * VO: 人件費丸め設定
 */
@AllArgsConstructor
@Getter
public class PersonCostRoundingSetting extends ValueObject {
    //1: 単価＊割増率の丸め:人件費単価丸め
    private UnitPriceRoundingSetting roundingOfPremium;
    //2: 単価＊時間の丸め :
    private AmountRoundingSetting amountRoundingSetting;

	/**
	 * デフォルト値で作成する
	 * @return 人件費丸め設定
	 */
	public static PersonCostRoundingSetting defaultValue() {
		return new PersonCostRoundingSetting(
				new UnitPriceRoundingSetting(UnitPriceRounding.TRUNCATION),
				new AmountRoundingSetting(AmountUnit.ONE_YEN, AmountRounding.TRUNCATION));
	}
}
