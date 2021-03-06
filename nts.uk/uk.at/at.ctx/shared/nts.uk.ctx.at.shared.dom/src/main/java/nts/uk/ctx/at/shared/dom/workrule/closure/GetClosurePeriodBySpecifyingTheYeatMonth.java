package nts.uk.ctx.at.shared.dom.workrule.closure;

import java.util.List;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting.RequireM2;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;

/**
 * 年月を指定して締め期間を取得する
 * @author hayata_maekawa
 *
 */
public class GetClosurePeriodBySpecifyingTheYeatMonth {
	
	/**
	 * 取得する
	 * @param require
	 * @param employeeId　社員ID
	 * @param criteriaDate　基準日
	 * @param yearMonth　年月
	 * @param cacheCarrier
	 * @return
	 */
	public static Optional<DatePeriod> getPeriod(
			RequireM2 require,
			String employeeId,
			GeneralDate criteriaDate,
			YearMonth yearMonth,
			CacheCarrier cacheCarrier){
		
		Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, employeeId, criteriaDate);
		
		//締めが取得できたか
		if(closure == null){
			return Optional.empty();
		}
		
		//指定した年月の期間をすべて取得する
		List<DatePeriod> datePeriodList = closure.getPeriodByYearMonth(yearMonth);
		
		if(datePeriodList.isEmpty()){
			return Optional.empty();
		}
		
		return datePeriodList.stream()
				.sorted((d1,d2)->d2.end().compareTo(d1.end()))
				.findFirst();
		
	}

}
