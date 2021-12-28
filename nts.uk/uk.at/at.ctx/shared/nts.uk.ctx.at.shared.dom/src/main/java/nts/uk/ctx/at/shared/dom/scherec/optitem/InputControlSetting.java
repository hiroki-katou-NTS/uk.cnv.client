package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Value;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.DomainObject;

/**
 * 入力制御設定
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).任意項目.入力制御設定
 */
@Value
public class InputControlSetting extends DomainObject {
    // チェックボックスで入力する
    private boolean inputWithCheckbox;

    // 計算結果の範囲
    private CalcResultRange calcResultRange;

    // 日別実績の入力単位
    private Optional<DailyResultInputUnit> dailyInputUnit;

    @Override
    public void validate() {
        super.validate();
        if (this.inputWithCheckbox) {
            if (!this.calcResultRange.getNumberRange().isPresent()
                    || !this.calcResultRange.getNumberRange().get().getDailyTimesRange().isPresent()
                    || !this.calcResultRange.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().isPresent()
                    || this.calcResultRange.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().get().v().compareTo(BigDecimal.ZERO) != 0
                    || !this.calcResultRange.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().isPresent()
                    || this.calcResultRange.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().get().v().compareTo(BigDecimal.ONE) != 0) {
                throw new BusinessException("Msg_2308");
            }
        }
    }
    /**
     * 入力値が正しいか
     * @param inputValue 入力値
     * @param performanceAtr 実績区分
     * @param optionalItemAtr 任意項目の属性
     */
    public CheckValueInputCorrectOuput checkValueInputCorrect(BigDecimal inputValue,PerformanceAtr performanceAtr, OptionalItemAtr optionalItemAtr) {
    	List<String> errorContents = new ArrayList<>();
    	boolean isError = false;
    	//「@チェックボックスで入力する」とINPUT「実績区分」をチェックする
    	if(!inputWithCheckbox && performanceAtr == PerformanceAtr.DAILY_PERFORMANCE && dailyInputUnit.isPresent() ) {
    		//入力単位チェック
    		ValueCheckResult valueCheckResult = dailyInputUnit.get().checkInputUnit(inputValue, optionalItemAtr);
    		if(!valueCheckResult.isCheckResult()) {
    			isError = true;
    			errorContents.add(valueCheckResult.getErrorContent().get());
    		}
    	}
    	//入力範囲チェック
    	ValueCheckResult valueCheckResult = calcResultRange.checkInputRange(inputValue, performanceAtr, optionalItemAtr);
    	if(!valueCheckResult.isCheckResult()) {
    		errorContents.add(valueCheckResult.getErrorContent().get());
    	}
    	
    	if(!isError && valueCheckResult.isCheckResult()) {
    		return new CheckValueInputCorrectOuput(true, errorContents);
    	}
    	
    	return new CheckValueInputCorrectOuput(false,errorContents);
    }
}
