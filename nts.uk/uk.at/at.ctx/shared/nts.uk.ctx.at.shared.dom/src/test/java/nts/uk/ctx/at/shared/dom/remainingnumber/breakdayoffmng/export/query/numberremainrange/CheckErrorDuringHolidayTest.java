package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUnUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainCarryForward;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;

public class CheckErrorDuringHolidayTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	 /* 　テストしたい内容
	 * 　　残日数, 残時間 <0の場合はエラー
	 *        残日数 <0 →日単位代休残数不足エラー
	 *        残時間 <0→　 相殺できないエラー
	 */
	@Test
	public void test() {

		List<DayOffError> lstError = new ArrayList<>();
		SubstituteHolidayAggrResult param = new SubstituteHolidayAggrResult(new VacationDetails(new ArrayList<>()),
				new DayOffRemainDayAndTimes(new LeaveRemainingDayNumber(-1.0), Optional.of(new LeaveRemainingTime(-480))),//残日数, 残時間
				new DayOffDayTimeUse(new LeaveUsedDayNumber(1.0), Optional.of(new LeaveUsedTime(480))),
				new DayOffDayAndTimes(new MonthVacationGrantDay(4.0), Optional.of(new MonthVacationGrantTime(960))),
				new DayOffRemainCarryForward(new LeaveRemainingDayNumber(1.0), Optional.of(new LeaveRemainingTime(0))),
				new DayOffDayTimeUnUse(new LeaveRemainingDayNumber(2.0), Optional.of(new LeaveRemainingTime(960))),
				lstError,
				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());

		CheckErrorDuringHoliday.check(param);

		assertThat(param.getDayOffErrors()).isEqualTo(Arrays.asList(DayOffError.DAYERROR, DayOffError.TIMEERROR));
	}

}
