package nts.uk.ctx.at.request.app.find.application.optitem.optitemdto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OptionalItemDto {
    /**
     * The optional item no.
     */
    private int optionalItemNo;

    /**
     * The optional item name.
     */
    private String optionalItemName;

    private boolean inputCheck;

    /**
     * The unit.
     */
    private String unit;

    private String description;

    /**
     * The optional item atr.
     */
    private int optionalItemAtr;

    private CalcResultRangeDto calcResultRange;

    private int dispOrder;
}
