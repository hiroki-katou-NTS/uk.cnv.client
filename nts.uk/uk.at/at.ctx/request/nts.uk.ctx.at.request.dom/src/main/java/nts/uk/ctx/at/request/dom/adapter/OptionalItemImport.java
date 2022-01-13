package nts.uk.ctx.at.request.dom.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;

@AllArgsConstructor
@Setter
@Getter
public class OptionalItemImport {
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
