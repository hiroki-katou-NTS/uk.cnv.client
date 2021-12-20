package nts.uk.ctx.sys.auth.dom.permission.roleId;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
/**
 * 期間内に特定の職場（List）に所属している社員一覧を取得 Adapter
 * @author hoangnd
 *
 */
public interface SpecificWorkPlaceAdaper {
	/**
	 * 
	 * @param wkpIds
	 * @param dateperiod
	 * @return
	 */
	public List<String> get(
			List<String> wkpIds,
			DatePeriod dateperiod);

}
