package nts.uk.file.at.app.export.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.ManagermentAtr;

import java.util.List;

@AllArgsConstructor
@Getter
public class CreateTraceConfirmationTableFileQuery {
    private int howToPrintDate;
    private int pageBreak;
    private boolean moreSubstituteHolidaysThanHolidays;
    private boolean moreHolidaysThanSubstituteHolidays;
    private List<String> listEmployeeId;
}
