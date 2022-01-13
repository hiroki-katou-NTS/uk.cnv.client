package nts.uk.ctx.at.record.pub.optitem.application;

import lombok.Builder;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;

@Builder
@Getter
public class OptionalItemAppExport {

    /**
     * The optional item no.
     */
    private int optionalItemNo;

    /**
     * The optional item name.
     */
    private String optionalItemName;

    private String optionalItemUnit;

    private CalcResultRange calcResultRange;

    private OptionalItemAtr optionalItemAtr;

    private String description;

    private boolean inputCheck;
}
