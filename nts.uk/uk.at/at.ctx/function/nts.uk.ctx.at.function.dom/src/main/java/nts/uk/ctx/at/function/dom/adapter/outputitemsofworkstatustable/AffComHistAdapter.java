package nts.uk.ctx.at.function.dom.adapter.outputitemsofworkstatustable;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.EmpAffInfoExportDto;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.StatusOfEmployee;

import java.util.List;

public interface AffComHistAdapter {
    //[RQ 588] 社員の指定期間中の所属期間を取得する
    List<StatusOfEmployee> getListAffComHist(List<String> sid, DatePeriod datePeriod);

    //[RQ 589] 社員の指定期間中の所属期間を取得する
    EmpAffInfoExportDto getAffiliationPeriod(List<String> listSid, YearMonthPeriod YMPeriod, GeneralDate baseDate);
}
