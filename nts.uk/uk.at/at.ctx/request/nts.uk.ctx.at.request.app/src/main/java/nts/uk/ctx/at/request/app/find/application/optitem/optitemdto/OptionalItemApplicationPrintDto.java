package nts.uk.ctx.at.request.app.find.application.optitem.optitemdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemContent;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemPrintContent;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class OptionalItemApplicationPrintDto {
    private String code;
    /**
     * 申請種類名
     */
    private String name;

    private List<OptionalItemPrintDto> optionalItems;

    public OptionalItemPrintContent toPrintContentOutput() {
        List<OptionalItemContent> optionalItemContents = new ArrayList<>();
        for (OptionalItemPrintDto itemPrint : this.optionalItems) {
            OptionalItemContent optionalItemContent = OptionalItemContent.builder()
                    .unit(itemPrint.getUnit())
                    .optionalItemName(itemPrint.getOptionalItemName())
                    .optionalItemAtr(itemPrint.getOptionalItemAtr())
                    .times(itemPrint.getTimes())
                    .amount(itemPrint.getAmount())
                    .time(itemPrint.getTime())
                    .inputCheckbox(itemPrint.isInputCheckbox())
                    .timesChecked(itemPrint.getTimesChecked())
                    .build();
            optionalItemContents.add(optionalItemContent);
        }
        return new OptionalItemPrintContent(this.code, this.name, optionalItemContents);
    }
}
