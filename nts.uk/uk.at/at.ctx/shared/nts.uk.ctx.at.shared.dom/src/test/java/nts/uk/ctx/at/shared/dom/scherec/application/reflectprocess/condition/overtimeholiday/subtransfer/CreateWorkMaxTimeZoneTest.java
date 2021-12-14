package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.CreateWorkMaxTimeZone;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetermineTime;
import nts.uk.ctx.at.shared.dom.worktime.predset.PrescribedTimezoneSetting;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class CreateWorkMaxTimeZoneTest {

	@Injectable
	private CreateWorkMaxTimeZone.Require require;

	private String cid = "1";

	/*
	 * テストしたい内容
	 * 
	 * →最大の時間帯でworkを作成
	 * 
	 * →勤務NO = 1と時間帯を作成するだけです
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 時間帯：勤務NO = 1と２
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test(@Mocked DailyRecordToAttendanceItemConverter converter) {

		DailyRecordOfApplication dailyAppT = 
				ReflectApplicationHelper.addTimeLeavNo(1, 540, 1020, ReflectApplicationHelper.createRCWithTimeLeavOverTime());// No1 (時間帯= 540~1020)
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.addTimeLeavNo(2, 1050, 1200, dailyAppT);//  No2 (時間帯= 1050~1200)
		new Expectations() {
			{
				require.findByWorkTimeCode(anyString, anyString);
				result = Optional.of(create(480, 840));

				converter.setData((IntegrationOfDaily) any).toDomain();
				result = dailyApp.getDomain();

				require.calculateForRecord((CalculateOption) any, (List<IntegrationOfDaily>) any,
						(Optional<ManagePerCompanySet>) any, (ExecutionType) any);
				result = Arrays.asList(ReflectApplicationHelper.afterCalc(dailyApp.getDomain(), 0, 240));
			}
		};

		CreateWorkMaxTimeZone.process(require, cid, dailyApp.getDomain());

		assertThat(dailyApp.getAttendanceLeave().get().getTimeLeavingWorks())
				.extracting(x -> x.getWorkNo().v(),
						x -> x.getStampOfAttendance().get().getTimeDay().getTimeWithDay().get().v(),
						x -> x.getStampOfLeave().get().getTimeDay().getTimeWithDay().get().v())
				.contains(Tuple.tuple(1, 480, 1320));

		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayWorkFrameTime())
								.extracting(x -> x.getHolidayFrameNo().v(), x -> x.getHolidayWorkTime().get().getTime().v(),
										x -> x.getTransferTime().get().getTime().v())
								.containsExactly(Tuple.tuple(1, 0, 240));

	}

	private PredetemineTimeSetting create(int startDateClock, int rangeTimeDay) {

		return new PredetemineTimeSetting(cid, new AttendanceTime(rangeTimeDay), new WorkTimeCode("001"),
				new PredetermineTime(), new PrescribedTimezoneSetting(), new TimeWithDayAttr(startDateClock),
				true);
		
	}

}
