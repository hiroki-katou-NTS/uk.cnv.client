package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.groupappabsence.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.groupappabsence.algorithm.DeleteAttendanceProcess;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

@RunWith(JMockit.class)
public class DeleteAttendanceProcessTest {

	@Injectable
	private DeleteAttendanceProcess.Require require;

	/*
	 * テストしたい内容
	 * 
	 * →出退勤の削除
	 * 
	 * 準備するデータ
	 * 
	 * →休暇系申請の反映.1日休暇の場合は出退勤を削除 = する;
	 * 
	 * →出勤日区分＝1日休日系
	 */
	@Test
	public void test1() {
		val dailyApp = ReflectApplicationHelper.createSimpleAttLeav(ScheduleRecordClassifi.RECORD, 2, no -> {
			return Arrays.asList(Pair.of(600, 800), // no 1 出勤 , 退勤
					Pair.of(900, 1200));// no2 出勤 , 退勤
		}, "001");// 勤務場所コード

		new Expectations() {
			{
				require.getWorkType(anyString);
				result = Optional.of(WorkType.createSimpleFromJavaType("003", "", "", "", "", 0, WorkTypeClassification.Holiday.value, // 休日
						0, 0));
				;
			}
		};
		val resultActual = DeleteAttendanceProcess.process(require, Optional.of(new WorkTypeCode("003")), dailyApp);

		assertThat(resultActual.getDomainDaily().getAttendanceLeave().get().getTimeLeavingWorks())
				.extracting(x -> x.getWorkNo().v(), // No
						x -> x.getStampOfAttendance().get().getTimeDay().getTimeWithDay(), // 出勤 .時刻
						x -> x.getStampOfAttendance().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 出勤.時刻変更手段
						x -> x.getStampOfAttendance().get().getLocationCode(), // 勤務場所コード
						x -> x.getStampOfLeave().get().getTimeDay().getTimeWithDay(), // 退勤 .時刻
						x -> x.getStampOfLeave().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 退勤.時刻変更手段
						x -> x.getStampOfLeave().get().getLocationCode())// 勤務場所コード
				.contains(Tuple.tuple(1, // No
						Optional.empty(), TimeChangeMeans.APPLICATION, Optional.empty(), // 出勤
						Optional.empty(), TimeChangeMeans.APPLICATION, Optional.empty()), // 退勤
						Tuple.tuple(2, // No
								Optional.empty(), TimeChangeMeans.APPLICATION, Optional.empty(), // 出勤
								Optional.empty(), TimeChangeMeans.APPLICATION, Optional.empty()));// 退勤
	}

	/*
	 * テストしたい内容
	 * 
	 * →出退勤の削除がない
	 * 
	 * 準備するデータ
	 * 
	 * →休暇系申請の反映.1日休暇の場合は出退勤を削除 = する;
	 * 
	 * →出勤日区分＜＞1日休日系
	 */
	@Test
	public void test2() {
		val dailyApp = ReflectApplicationHelper.createSimpleAttLeav(ScheduleRecordClassifi.RECORD, 2, no -> {
			return Arrays.asList(Pair.of(600, 800), // no 1 出勤 , 退勤
					Pair.of(900, 1200));// no2 出勤 , 退勤
		}, "001");// 勤務場所コード

		new Expectations() {
			{
				require.getWorkType(anyString);
				result = Optional.of(WorkType.createSimpleFromJavaType("003", "", "", "", "", 0, WorkTypeClassification.Attendance.value, // 出勤
						0, 0));
				;
			}
		};
		val resultActual = DeleteAttendanceProcess.process(require, Optional.of(new WorkTypeCode("003")), dailyApp);

		assertThat(resultActual.getDomainDaily().getAttendanceLeave().get().getTimeLeavingWorks())
				.extracting(x -> x.getWorkNo().v(), // No
						x -> x.getStampOfAttendance().get().getTimeDay().getTimeWithDay().get().v(), // 出勤 .時刻
						x -> x.getStampOfAttendance().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 出勤.時刻変更手段
						x -> x.getStampOfAttendance().get().getLocationCode().get().v(), // 勤務場所コード
						x -> x.getStampOfLeave().get().getTimeDay().getTimeWithDay().get().v(), // 退勤 .時刻
						x -> x.getStampOfLeave().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 退勤.時刻変更手段
						x -> x.getStampOfLeave().get().getLocationCode().get().v())// 勤務場所コード
				.contains(Tuple.tuple(1, // No
						600, TimeChangeMeans.AUTOMATIC_SET, "001", // 出勤退勤
						800, TimeChangeMeans.AUTOMATIC_SET, "001"),
						Tuple.tuple(2, // No
								900, TimeChangeMeans.AUTOMATIC_SET, "001", // 出勤退勤
								1200, TimeChangeMeans.AUTOMATIC_SET, "001"));
	}
}
