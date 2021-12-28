package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.Value;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 日別実績の入力単位
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).任意項目.日別実績の入力単位
 */
@Value
public class DailyResultInputUnit {
    // 時間項目の入力単位
    private Optional<TimeItemInputUnit> timeItemInputUnit;

    // 回数項目の入力単位
    private Optional<NumberItemInputUnit> numberItemInputUnit;

    // 金額項目の入力単位
    private Optional<AmountItemInputUnit> amountItemInputUnit;
    
    /**
     * 入力単位チェック
     * @param value
     * @param optionalItemAtr
     * @return
     */
    public ValueCheckResult checkInputUnit(BigDecimal value, OptionalItemAtr optionalItemAtr) {
		
    	double resultEnum = 0.0;
    	String valueDisplay ="";
    	switch(optionalItemAtr) {
    	case TIME:
    		resultEnum = timeItemInputUnit.get().valueEnum();
    		valueDisplay = timeItemInputUnit.get().nameId;
    		break;
    	case NUMBER:
    		resultEnum = numberItemInputUnit.get().valueEnum();
    		valueDisplay = numberItemInputUnit.get().nameId;
    		break;
    	default: //金額
    		resultEnum = amountItemInputUnit.get().valueEnum();
    		valueDisplay = amountItemInputUnit.get().nameId;
    	}
    	
    	if(new BigDecimal(String.valueOf(value)).remainder(new BigDecimal(String.valueOf(resultEnum))).doubleValue() == 0) {
    		return new ValueCheckResult(true, Optional.empty());
    	}
    	String errorContens = TextResource.localize("Msg_2290",valueDisplay);
    	return new ValueCheckResult(false, Optional.of(errorContens));
	}
    
}
