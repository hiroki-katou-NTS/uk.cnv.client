package nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare;

import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.ReferenceAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;

public class GetUsageDetailChildCareService {
	/**
	 * 期間内の子の看護使用明細を取得する
	 * @param companyId　会社ID
	 * @param employeeId　社員ID
	 * @param period　期間
	 * @param referenceAtr　参照先区分
	 * @param require
	 * @return
	 */
	public static List<TempChildCareManagement> getUsageDetailCareService(String companyId, String employeeId,
			DatePeriod period, ReferenceAtr referenceAtr, Require require) {
		
		
		List<DailyInterimRemainMngData> interimRemainMng = require.getHolidayDetailByPeriod(
				companyId, employeeId, period, referenceAtr);
		interimRemainMng = interimRemainMng.stream().sorted((x,y)->x.getYmd().compareTo(y.getYmd())).collect(Collectors.toList());
		return interimRemainMng.stream()
				.filter(c -> !c.getRecAbsData().isEmpty() && !c.getChildCareData().isEmpty())
				.map(c -> {
					return c.getChildCareData();
				}).flatMap(List::stream).collect(Collectors.toList());
	}

	public static interface Require {
		List<DailyInterimRemainMngData> getHolidayDetailByPeriod(String companyId, String employeeId, 
				DatePeriod period, ReferenceAtr referenceAtr);
	}
	
}
