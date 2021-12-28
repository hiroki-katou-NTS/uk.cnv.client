package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;


import lombok.AllArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;

/**
 * Enum: 人件費単価端数処理
 */
@AllArgsConstructor
public enum  UnitPriceRounding {
    // 切り上げ
    ROUND_UP(0, AmountRounding.ROUND_UP),

    //切り捨て
    TRUNCATION(1, AmountRounding.TRUNCATION),

    //四捨五入
    DOWN_4_UP_5(2, AmountRounding.DOWN_4_UP_5);


    public final int value;

	public AmountRounding amountRounding;
}
