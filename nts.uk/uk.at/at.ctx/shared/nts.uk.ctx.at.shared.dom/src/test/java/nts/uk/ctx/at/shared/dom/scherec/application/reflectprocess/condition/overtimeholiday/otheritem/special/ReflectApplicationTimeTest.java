package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.otheritem.special;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.groups.Tuple;
import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AttendanceTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.reflectbreak.ReflectApplicationTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author thanh_nx
 *
 *特別日加給時間
 */
public class ReflectApplicationTimeTest {

	/*
	 * テストしたい内容
	 * 
	 * →特定日加給NOを保存するとき特定日加給時間の反映
	 * 
	 * 準備するデータ
	 * 
	 * → 「特定日加給枠NO」＝1
	 * 
	 * 
	 */
	@Test
	public void test1() {
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// 残業枠NO= 1

		val applicationTime = ReflectApplicationHelper.createAppSettingShare(1, AttendanceTypeShare.BONUSSPECIALDAYTIME,
				195);// 残業枠NO = 1

		NtsAssert.Invoke.staticMethod(ReflectApplicationTime.class, "processSpecialDaySalaryTime", applicationTime, dailyApp);

		assertThat(dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getAutoCalRaisingSalarySettings())
						.extracting(x -> x.getBonusPayTimeItemNo(), x -> x.getBonusPayTime().v())
						.containsExactly(Tuple.tuple(1, 195));
	}

	/*
	 * テストしたい内容
	 * 
	 * →特定日加給枠NOを保存するとき特定日加給時間の反映
	 * 
	 * 準備するデータ
	 * 
	 * → 「特定日加給枠NO」＝1
	 * 
	 * 
	 */
	@Test
	public void test2() {
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// 残業枠NO= 1

		val applicationTime = ReflectApplicationHelper.createAppSettingShare(2, AttendanceTypeShare.BONUSSPECIALDAYTIME,
				195);// 残業枠NO = 1

		NtsAssert.Invoke.staticMethod(ReflectApplicationTime.class, "processSpecialDaySalaryTime", applicationTime, dailyApp);

		assertThat(dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getAutoCalRaisingSalarySettings())
						.extracting(x -> x.getBonusPayTimeItemNo(), x -> x.getBonusPayTime().v())
						.containsExactly(Tuple.tuple(1, 0), Tuple.tuple(2, 195));
	}

}
