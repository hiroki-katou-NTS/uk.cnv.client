package nts.uk.ctx.at.record.dom.monthly.holidaymanagement.publicholiday;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.arc.time.YearMonth;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday.PublicHolidayProcess;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.AggregationPeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting;

/**
 * 公休の集計期間を取得する
 * @author hayata_maekawa
 *
 */
public class GetAggregationPeriodForPublicHoliday {
	public static Optional<DatePeriod> getAggregationPeriodForPublicHoliday(PublicHolidayProcess.Require require,
			CacheCarrier cacheCarrier, String companyId, String employeeId, YearMonth yearMonth, DatePeriod period) {

		Optional<PublicHolidaySetting> publicHolidaySetting = require.publicHolidaySetting(companyId);
		if (!publicHolidaySetting.isPresent())
			return Optional.empty();

		List<AggregationPeriod> aggPeriods = publicHolidaySetting.get().createPeriod(employeeId,
				Arrays.asList(yearMonth), period.end(), cacheCarrier, require);
		return aggPeriods.stream().findFirst().map(c -> c.getPeriod());
	}
}
