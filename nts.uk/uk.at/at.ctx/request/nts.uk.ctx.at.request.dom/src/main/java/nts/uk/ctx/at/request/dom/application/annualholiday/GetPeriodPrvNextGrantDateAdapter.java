package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface GetPeriodPrvNextGrantDateAdapter {
	/**
	 * 指定した年月日を基準に、前回付与日から次回付与日までの期間を取得
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param ymd 指定年月日
	 * @param periodOutput 対象期間区分 - Target period classification : CURRENT(0), AFTER_1_YEAR(1), PAST(2)
	 * @param １年経過用期間(From-To)
	 * @return 期間
	 */
	Optional<GrantPeriodDto> getPeriodYMDGrant(String cid, String sid, GeneralDate ymd, Integer periodOutput, Optional<DatePeriod> fromTo);
}
