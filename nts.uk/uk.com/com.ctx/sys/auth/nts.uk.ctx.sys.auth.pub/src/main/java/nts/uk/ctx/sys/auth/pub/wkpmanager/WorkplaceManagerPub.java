package nts.uk.ctx.sys.auth.pub.wkpmanager;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

/**
 * The Interface WorkplaceManagerPub.
 */
public interface WorkplaceManagerPub {

	/**
	 * 管理者未登録を確認する
	 */
	List<WorkplaceManagerExport> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod);

	/**
	 * ドメインモデル「職場管理者」を取得する
	 */
	List<WorkplaceManagerExport> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate);

	// Get workplace manager list by workplace id
	List<WorkplaceManagerExport> getWkpManagerListByWkpId(String workplaceId);

	List<WorkplaceManagerExport> findListWkpManagerByWkpIdsAndBaseDate(List<String> wkpIDLst, GeneralDate baseDate);
}



