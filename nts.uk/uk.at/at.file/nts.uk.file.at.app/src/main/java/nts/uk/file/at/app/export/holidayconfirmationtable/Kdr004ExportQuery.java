package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Kdr004ExportQuery {
    private List<String> employeeIds;
    private boolean haveMoreHolidayThanDrawOut;
    private boolean haveMoreDrawOutThanHoliday;
    private int howToPrintDate;
    private int pageBreak;
}
