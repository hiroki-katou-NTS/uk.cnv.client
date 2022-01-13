package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostRoundingSetting;

import java.math.BigDecimal;

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
	
	/**
	 * 就業時間金額を丸める
	 * @param workTimeAmount 就業時間金額
	 * @return 丸めた金額
	 */
	public AttendanceAmountDaily roundWorkTimeAmount(BigDecimal workTimeAmount) {
		return new AttendanceAmountDaily(amountRoundingSetting.round(workTimeAmount).intValue());
	}
}
