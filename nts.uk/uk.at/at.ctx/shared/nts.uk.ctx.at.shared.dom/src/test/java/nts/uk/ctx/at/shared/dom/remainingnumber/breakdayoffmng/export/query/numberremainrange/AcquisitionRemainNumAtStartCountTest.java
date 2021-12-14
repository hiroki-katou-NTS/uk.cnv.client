package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.CarryForwardDayTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SettingSubstituteHolidayProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;

@RunWith(JMockit.class)
public class AcquisitionRemainNumAtStartCountTest {

	private static String CID = "000000000000-0117";

	private static String SID = "292ae91c-508c-4c6e-8fe8-3e72277dec16";

	@Injectable
	private AcquisitionRemainNumAtStartCount.Require require;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	/*
	 * テストしたい内容
	 * 
	 * 確定データから「逐次発生の休暇明細」を作成
	 *
	 * 繰越数を計算する
	 * 
	 * 準備するデータ
	 * 
	 * 代休管理データがある
	 * 
	 * 未相殺数がなくて作成しない
	 * 
	 * 
	 * 休出管理データがある
	 * 
	 * 未使用数がなくて作成しない
	 * 
	 * 繰越数がない（代休日数 ＝ 休出日数）
	 */
	@Test
	public void test(@Mocked SettingSubstituteHolidayProcess settingProcess) {

		SubstitutionHolidayOutput setting = new SubstitutionHolidayOutput();
		setting.setTimeOfPeriodFlg(false);
		new Expectations() {
			{
				// 代休管理データ
				require.getBySidYmd(anyString, anyString, (GeneralDate) any);
				result = Arrays.asList(
						createComDayMa("a1", GeneralDate.ymd(2019, 11, 9), 1.0, 1, // 必要数
								1.0, 0), // 未相殺数
						createComDayMa("a2", GeneralDate.ymd(2019, 11, 10), 1.0, 1, // 必要数
								1.0, 0), // 未相殺数
						createComDayMa("a5", GeneralDate.ymd(2019, 11, 11), 1.0, 1, // 必要数
								0.0, 0));// 未相殺数

				// 休出管理データ
				require.getBySidYmd(anyString, anyString, (GeneralDate) any, (DigestionAtr) any);
				result = Arrays.asList(
						createLeav("a3", GeneralDate.ymd(2019, 11, 12), 1.0, 0, // 発生数
								1.0, 0), // 未使用数
						createLeav("a4", GeneralDate.ymd(2019, 11, 13), 1.0, 0, 1.0, // 発生数
								0), // 未使用数
						createLeav("a6", GeneralDate.ymd(2019, 11, 14), 1.0, 0, 0.0, // 発生数
								0));// 未使用数
				
				SettingSubstituteHolidayProcess.getSettingForSubstituteHoliday(require, anyString, anyString,
						(GeneralDate) any);
				result = setting;
				
			}
		};

		List<AccumulationAbsenceDetail> lstAccuAbsenDetail = new ArrayList<>();
		CarryForwardDayTimes resultActual = AcquisitionRemainNumAtStartCount.acquisition(require, CID, SID,
				GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2019, 11, 30), true, lstAccuAbsenDetail,
				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));
		assertThat(resultActual.getCarryForwardDays()).isEqualTo(0.0);
		assertThat(resultActual.getCarryForwardTime()).isEqualTo(0);

		assertThat(lstAccuAbsenDetail)
				.extracting(x -> x.getManageId(), x -> x.getDateOccur().isUnknownDate(),
						x -> x.getDateOccur().getDayoffDate(), x -> x.getNumberOccurren().getDay().v(),
						x -> x.getNumberOccurren().getTime(), x -> x.getOccurrentClass(),
						x -> x.getUnbalanceNumber().getDay().v(), x -> x.getUnbalanceNumber().getTime())
				.containsExactly(

						Tuple.tuple("a1", false,
								Optional.of(GeneralDate.ymd(2019, 11, 9)), 
								1.0, Optional.of(new AttendanceTime(1)),//発生数
								OccurrenceDigClass.DIGESTION, //消化
								1.0, Optional.of(new AttendanceTime(0))),//未相殺数

						Tuple.tuple("a2", false,
								Optional.of(GeneralDate.ymd(2019, 11, 10)), 
								1.0, Optional.of(new AttendanceTime(1)),//発生数
								OccurrenceDigClass.DIGESTION, //消化
								1.0, Optional.of(new AttendanceTime(0))),//未相殺数

						Tuple.tuple("a3", false,
								Optional.of(GeneralDate.ymd(2019, 11, 12)), 
								1.0, Optional.of(new AttendanceTime(0)),//発生数
								OccurrenceDigClass.OCCURRENCE, //発生
								1.0, Optional.of(new AttendanceTime(0))),//未相殺数

						Tuple.tuple("a4", false,
								Optional.of(GeneralDate.ymd(2019, 11, 13)), 
								1.0, Optional.of(new AttendanceTime(0)),//発生数
								OccurrenceDigClass.OCCURRENCE, //発生
								1.0, Optional.of(new AttendanceTime(0))));//未相殺数

	}

	private CompensatoryDayOffManaData createComDayMa(String comDayOffID, GeneralDate dayoffDate, Double days, int time,
			Double remainDays, int remainTimes) {
		return new CompensatoryDayOffManaData(comDayOffID, CID, SID, dayoffDate == null, dayoffDate, days, time,
				remainDays, remainTimes);
	}

	private LeaveManagementData createLeav(String id, GeneralDate dayoffDate, Double occurredDays, int occurredTimes,
			Double unUsedDays, int unUsedTimes) {
		return new LeaveManagementData(id, CID, SID, dayoffDate == null, dayoffDate, GeneralDate.max(), occurredDays,
				occurredTimes, unUsedDays, unUsedTimes, DigestionAtr.UNUSED.value, 0, 0);
	}

}
