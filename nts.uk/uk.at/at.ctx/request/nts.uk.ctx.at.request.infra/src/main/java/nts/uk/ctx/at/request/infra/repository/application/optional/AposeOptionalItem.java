package nts.uk.ctx.at.request.infra.repository.application.optional;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.CheckBox;
import com.aspose.cells.Worksheet;
import nts.arc.i18n.I18NText;
import nts.uk.ctx.at.request.dom.application.common.service.print.PrintContentOfApp;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemContent;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemPrintContent;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class AposeOptionalItem {


    public void printOptionalItem(Worksheet worksheet, PrintContentOfApp printContentOfApp) {
        Cells cells = worksheet.getCells();
        Cell cellB8 = cells.get("B8");
        cellB8.setValue(I18NText.getText("KAF020_55"));
        Cell cellB9 = cells.get("B9");
        cellB9.setValue(I18NText.getText("KAF020_56"));
        OptionalItemPrintContent optionalItemPrintContent = printContentOfApp.getOptionalItemPrintContent().get();
        cells.get("D8").setValue(optionalItemPrintContent.getName());
        List<OptionalItemContent> printContent = optionalItemPrintContent.getOptionalItemContents();
        int count = 0;
        for (int i = 0; i < printContent.size(); i++) {
            boolean hasValue = false;
            Cell cellH = cells.get("H" + (9 + count));
            if (printContent.get(i).getOptionalItemAtr() == 0 && printContent.get(i).getTime() != null) {
                cellH.setValue(timeToString(printContent.get(i).getTime()));
                hasValue = true;
            } else if (printContent.get(i).getOptionalItemAtr() == 1) {
                if (printContent.get(i).isInputCheckbox()) {
                    if (printContent.get(i).getTimesChecked() != null) {
                        cellH.setValue("");
                        int checkBoxIndex = worksheet.getCheckBoxes().add(8 + count, 7, 42, 42);
                        CheckBox checkBox = worksheet.getCheckBoxes().get(checkBoxIndex);
                        checkBox.setValue(printContent.get(i).getTimesChecked().booleanValue());
                        checkBox.setLeft(46);
                        hasValue = true;
                    }
                } else {
                    if (printContent.get(i).getTimes() != null) {
                        cellH.setValue(printContent.get(i).getTimes());
                        hasValue = true;
                    }
                }
            } else if (printContent.get(i).getOptionalItemAtr() == 2 && printContent.get(i).getAmount() != null) {
                cellH.setValue(String.format("%,d", printContent.get(i).getAmount()));
                hasValue = true;
            }
            if (hasValue) {
                Cell cellD = cells.get("D" + (9 + count));
                cellD.setValue(printContent.get(i).getOptionalItemName());
                Cell cellI = cells.get("I" + (9 + count));
                if (!printContent.get(i).isInputCheckbox()) cellI.setValue(printContent.get(i).getUnit());
                count++;
            }
        }
    }

    public void deleteEmptyRow(Worksheet worksheet) {
        Cells cells = worksheet.getCells();
        for (int i = 10; i > 0; i--) {
            Cell cell = cells.get("H" + (8 + i));
            if (cell.getValue() == null) {
                worksheet.getCells().deleteRow(7 + i);
            }
        }
    }

    private String timeToString(int value) {
        boolean lowerThanZero = false;
        if (value < 0) {
            lowerThanZero = true;
            value = Math.abs(value);
        }
        if (value % 60 < 10) {
            return (lowerThanZero ? "-" : "") + String.valueOf(value / 60) + ":0" + String.valueOf(value % 60);
        }
        return (lowerThanZero ? "-" : "") + String.valueOf(value / 60) + ":" + String.valueOf(value % 60);
    }
}
