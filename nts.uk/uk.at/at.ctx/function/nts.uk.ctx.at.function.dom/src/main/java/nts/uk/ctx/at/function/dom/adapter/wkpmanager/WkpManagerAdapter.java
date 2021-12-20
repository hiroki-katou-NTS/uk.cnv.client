package nts.uk.ctx.at.function.dom.adapter.wkpmanager;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.dom.adapter.workplace.WorkplaceManagerImport;

import java.util.List;

public interface WkpManagerAdapter {

    /**
     * 管理者未登録を確認する
     */
    List<WkpManagerImport> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate);

    List<WkpManagerImport> findByWkpIdsAndDate(List<String> wkpIds, GeneralDate baseDate);

}
