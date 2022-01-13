package nts.uk.ctx.at.request.app.find.application.optitem.optitemdto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OptionalItemPrintDto {
    /**
     * 任意項目の単位
     */
    private String unit;

    private int optionalItemAtr;
    /**
     * 申請種類名
     */
    private String optionalItemName;

    private Integer itemNo;
    /**
     * 回数
     */
    private BigDecimal times;
    /**
     * 金額
     */
    private Integer amount;
    /**
     * 時間
     */
    private Integer time;

    private boolean inputCheckbox;

    private Boolean timesChecked;

}
