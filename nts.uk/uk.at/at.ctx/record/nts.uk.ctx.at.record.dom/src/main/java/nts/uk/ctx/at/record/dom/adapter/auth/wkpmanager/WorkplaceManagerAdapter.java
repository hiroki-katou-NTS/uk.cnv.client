package nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager;

import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

public interface WorkplaceManagerAdapter {
    /**
     * 管理者未登録を確認する
     */
    List<WorkplaceManagerImport> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod);
}
