package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AttendanceBeforeApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.DailyAfterAppReflectResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
@SuppressWarnings("unchecked")
public class CancellationOfApplicationTest {

	@Injectable
	private CancellationOfApplication.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 元に戻した勤怠項目IDがエンプティー
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がエンプティー
	 */

	@Test
	public void test(@Mocked AcquireAppReflectHistForCancel appHist) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.empty();
			}
		};
		val actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, createDomain());

		assertThat(actualResult.getLstItemId()).isEmpty();

	}

	/*
	 * テストしたい内容
	 * 
	 * → 元に戻した勤怠項目IDがエンプティー
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がある
	 * 
	 * → 元に戻した日別実績の勤怠項目はエンプティー(case: 処理中の勤怠項目IDの編集状態が[申請反映]かチェック＝該当なし)
	 */

	@Test
	public void test2(@Mocked AcquireAppReflectHistForCancel appHist) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.of(createAppReflectHist("1", null, Pair.of(28, "002")));// 取得した[元に戻すための申請反映履歴].反映前（List）
			}
		};
		val actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, createDomain(31, 34));// 日別勤怠(work）.編集状態

		assertThat(actualResult.getLstItemId()).isEmpty();

	}

	/*
	 * テストしたい内容
	 * 
	 * →①取消す申請より後の申請反映履歴があるチェックと元に戻する勤怠項目IDがある場合：元に戻しない（case:
	 * 取得した[反映前（List）]に、対象の勤怠項目があるかチェック = 該当あり）
	 * 
	 * →②取消す申請より後の申請反映履歴があるチェックと元に戻する勤怠項目IDがない場合：元に戻する（case:
	 * 取得した[反映前（List）]に、対象の勤怠項目があるかチェック = 該当なし）
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がある
	 * 
	 * →元に戻した日別実績の勤怠項目はある(case: 処理中の勤怠項目IDの編集状態が[申請反映]かチェック＝該当あり)
	 * 
	 */

	@Test
	public void test3(@Mocked AcquireAppReflectHistForCancel appHist) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.of(createAppReflectHist("1", null, Pair.of(28, "002"), Pair.of(29, "003")));// 取得した[元に戻すための申請反映履歴].反映前（List）

				require.findAppReflectHistAfterMaxTime(anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean, (GeneralDateTime) any);
				result = Arrays.asList(
						createAppReflectHist("2", null, Pair.of(28, "002"), Pair.of(34, "600")));
				
			}
		};
		
		val domainBefore = createDomain(28, 29);
		
		DailyAfterAppReflectResult actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, domainBefore);// 日別勤怠(work）.編集状態

		assertThat(actualResult.getLstItemId()).containsExactly(29);//②

	}
	
	/*
	 * テストしたい内容
	 * 
	 * →取消す申請より後の申請反映履歴がない場合元に戻する（case: 取消す申請より後の申請反映履歴があるかチェックする）
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がある
	 * 
	 * →元に戻した日別実績の勤怠項目はある(case: 処理中の勤怠項目IDの編集状態が[申請反映]かチェック＝該当あり)
	 * 
	 */
	@Test
	public void test4(@Mocked AcquireAppReflectHistForCancel appHist) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.of(createAppReflectHist("1", null, Pair.of(28, "002")));// 取得した[元に戻すための申請反映履歴].反映前（List）
				
				require.findAppReflectHistAfterMaxTime(anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean, (GeneralDateTime) any);
				result = Arrays.asList();
				
			}
		};
		
		val domainBefore = createDomain(28);
		assertThat(domainBefore.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("AAA");// item28
		DailyAfterAppReflectResult actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, domainBefore);// 日別勤怠(work）.編集状態

		assertThat(actualResult.getLstItemId()).containsExactly(28);
		
	}

	/*
	 * テストしたい内容
	 * 
	 * →日別勤怠(work）の編集状態を元に戻す
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がある
	 * 
	 * →元に戻した日別実績の勤怠項目はある(case: 処理中の勤怠項目IDの編集状態が[申請反映]かチェック＝該当あり)
	 * 
	 * →処理中の反映前.編集状態をチェックがある
	 */
	@Test
	public void test5(@Mocked AcquireAppReflectHistForCancel appHist, @Mocked DailyRecordToAttendanceItemConverter cv) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.of(
						createAppReflectHistAll("1", EditStateSetting.HAND_CORRECTION_MYSELF, //処理中の反映前.編集状態をチェックがある
								null, Pair.of(28, "002")));// 取得した[元に戻すための申請反映履歴].反映前（List）
				
				require.findAppReflectHistAfterMaxTime(anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean, (GeneralDateTime) any);
				result = Arrays.asList();
				
				cv.toDomain();
				result = createDomain(28);
			}
		};
		
		val domainBefore = createDomain(28);
		assertThat(domainBefore.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("AAA");// item28
		DailyAfterAppReflectResult actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, domainBefore);// 日別勤怠(work）.編集状態

		assertThat(actualResult.getDomainDaily().getEditState())
				.extracting(x -> x.getAttendanceItemId(), x -> x.getEditStateSetting())
				.containsExactly(Tuple.tuple(28, EditStateSetting.HAND_CORRECTION_MYSELF));
		
		assertThat(actualResult.getDomainDaily().getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("AAA");// item28

	}
	
	/*
	 * テストしたい内容
	 * 
	 * →日別勤怠(work）の編集状態から該当の編集状態を削除する
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * →申請反映履歴がある
	 * 
	 * →元に戻した日別実績の勤怠項目はある(case: 処理中の勤怠項目IDの編集状態が[申請反映]かチェック＝該当あり)
	 * 
	 * →処理中の反映前.編集状態をチェックがない
	 */
	@Test
	public void test6(@Mocked AcquireAppReflectHistForCancel appHist, @Mocked DailyRecordToAttendanceItemConverter cv) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				AcquireAppReflectHistForCancel.process(require, app, (GeneralDate) any, (ScheduleRecordClassifi) any);
				result = Optional.of(
						createAppReflectHistAll("1", null, null, Pair.of(28, "002")));// 処理中の反映前.編集状態をチェックがない
				
				require.findAppReflectHistAfterMaxTime(anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean, (GeneralDateTime) any);
				result = Arrays.asList();
				
				cv.toDomain();
				result = createDomain(28);
			}
		};
		DailyAfterAppReflectResult actualResult = CancellationOfApplication.process(require, app, GeneralDate.ymd(2021, 4, 21),
				ScheduleRecordClassifi.RECORD, createDomain(28));// 日別勤怠(work）.編集状態

		assertThat(actualResult.getDomainDaily().getEditState()).isEmpty();

	}
	
	private IntegrationOfDaily createDomain(Integer... lstEdit) {
		val dom = ReflectApplicationHelper.createRCWithTimeLeavOverTime().getDomain();

		List<EditStateOfDailyAttd> lst = new ArrayList<EditStateOfDailyAttd>();
		for (int i = 0; i < lstEdit.length; i++) {
			switch (lstEdit[i]) {
			case 28:
				dom.getWorkInformation().getRecordInfo().setWorkTypeCode("AAA");
				break;
			case 29:
				dom.getWorkInformation().getRecordInfo().setWorkTimeCode("BBB");
				break;
			case 31:
				dom.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getAttendanceStamp().get().getStamp()
						.get().getTimeDay().setTimeWithDay(Optional.of(new TimeWithDayAttr(654)));
				break;
			case 34:
				dom.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getLeaveStamp().get().getStamp().get()
						.getTimeDay().setTimeWithDay(Optional.of(new TimeWithDayAttr(987)));
				break;
			default:
				break;
			}
			lst.add(new EditStateOfDailyAttd(lstEdit[i], EditStateSetting.REFLECT_APPLICATION));
		}
		dom.setEditState(lst);
		return dom;
	}

	private ApplicationReflectHistory createAppReflectHist(String appId, GeneralDateTime maxDate, Pair<Integer, String>... lstItemValue) {
		return createAppReflectHistAll(appId, EditStateSetting.HAND_CORRECTION_MYSELF, maxDate, lstItemValue);
	}
	
	private ApplicationReflectHistory createAppReflectHistAll(String appId, EditStateSetting editState, GeneralDateTime maxDate, Pair<Integer, String>... lstItemValue) {
		List<AttendanceBeforeApplicationReflect> lstAttBeforeAppReflect = Arrays.asList(lstItemValue).stream()
				.map(x -> new AttendanceBeforeApplicationReflect(x.getKey(), Optional.of(x.getValue()),
						editState == null ? Optional.empty() : Optional.of(new EditStateOfDailyAttd(x.getKey(), editState))))
				.collect(Collectors.toList());
		return new ApplicationReflectHistory("1",  GeneralDate.ymd(2021, 4, 21) , appId,
				maxDate == null ? GeneralDateTime.now() : maxDate, ScheduleRecordClassifi.RECORD, true, lstAttBeforeAppReflect);
	}
}
