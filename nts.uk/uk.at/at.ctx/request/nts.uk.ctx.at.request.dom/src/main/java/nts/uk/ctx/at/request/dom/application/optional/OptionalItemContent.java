package nts.uk.ctx.at.request.dom.application.optional;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OptionalItemContent {

    private int optionalItemAtr;

    /**
     * 申請種類名
     */
    private String optionalItemName;
    /**
     * 時間
     */
    private Integer time;

    /**
     * 回数
     */
    private BigDecimal times;

    /**
     * 金額
     */
    private Integer amount;

    /**
     * 任意項目の単位
     */
    private String unit;

    private boolean inputCheckbox;

    private Boolean timesChecked;
}