package nts.uk.ctx.at.record.dom.applicationcancel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectStatusResult;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectedState;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.StampRequestModeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppStampShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.EngraveShareAtr;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.RCCreateDailyAfterApplicationeReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.time.AttendanceClock;

@RunWith(JMockit.class)
public class ReflectApplicationWorkRecordTest {

	@Injectable
	private ReflectApplicationWorkRecord.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 打刻申請（NRモード）を反映する
	 * 
	 * → レコーダイメージ申請の対象日を取得する
	 * 
	 * → 勤務実績の更新
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻申請モード = レコーダイメージ申請
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test(@Mocked TimeStampApplicationNRMode nrMode, @Mocked GetTargetDateRecordApplication app) {

		ApplicationShare application = ReflectApplicationHelper.createAppShare(ApplicationTypeShare.STAMP_APPLICATION,
				PrePostAtrShare.POSTERIOR, GeneralDate.ymd(2020, 01, 01));
		AppRecordImageShare appImg = new AppRecordImageShare(EngraveShareAtr.ATTENDANCE, new AttendanceClock(1),
				Optional.empty(), application);

		appImg.setOpStampRequestMode(Optional.of(StampRequestModeShare.STAMP_ONLINE_RECORD));

		RCReflectStatusResult reflectStatus = new RCReflectStatusResult();
		reflectStatus.setReflectStatus(RCReflectedState.WAITREFLECTION);

		new Expectations() {
			{

				require.findDaily(anyString, (GeneralDate)any);
				result = Optional.of(ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.RECORD).getDomain());
				
				// 「 打刻申請（NRモード）を反映する」のテスト呼び出す
				TimeStampApplicationNRMode.process(require, anyString, (GeneralDate) any, (AppRecordImageShare) any,
						(DailyRecordOfApplication) any, (Optional<Stamp>) any, (ChangeDailyAttendance) any);
				times = 1;

				// 「レコーダイメージ申請の対象日を取得する」のテスト呼び出す
				GetTargetDateRecordApplication.getTargetDate(require, anyString, (AppRecordImageShare) any);
				result = Pair.of(Optional.of(GeneralDate.ymd(2020, 01, 01)), Optional.of(createStamp()));
				times = 1;

			}

		};

		val actualResult = ReflectApplicationWorkRecord.process(require, "", appImg,
				GeneralDate.ymd(2020, 01, 01), reflectStatus, GeneralDateTime.FAKED_NOW, "1");

		assertThat(actualResult.getLeft().getReflectStatus()).isEqualTo(RCReflectedState.REFLECTED);

		NtsAssert.atomTask(() -> actualResult.getRight().get(), any -> require.addAllDomain(any.get(), true));

	}

	/*
	 * テストしたい内容
	 * 
	 * → 申請の反映（勤務実績）
	 * 
	 * → 勤務実績の更新
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻申請モード != レコーダイメージ申請
	 * 
	 */
	@Test
	public void testReflectAll(@Mocked RCCreateDailyAfterApplicationeReflect reflect) {

		AppStampShare appImg = ReflectApplicationHelper.createAppStamp(PrePostAtrShare.POSTERIOR);

		appImg.setOpStampRequestMode(Optional.empty());

		RCReflectStatusResult reflectStatus = new RCReflectStatusResult();
		reflectStatus.setReflectStatus(RCReflectedState.WAITREFLECTION);

		new Expectations() {
			{

				require.findDaily(anyString, (GeneralDate)any);
				result = Optional.of(ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.RECORD).getDomain());
				
				// 「[RQ667]申請反映後の日別勤怠(work）を作成する（勤務実績）」のテスト呼び出す
				RCCreateDailyAfterApplicationeReflect.process(require, "", (ApplicationShare) any,
						(DailyRecordOfApplication) any, (GeneralDate) any);
				times = 1;

			}

		};

		val actualResult = ReflectApplicationWorkRecord.process(require, "", appImg,
				GeneralDate.ymd(2020, 01, 01), reflectStatus, GeneralDateTime.FAKED_NOW, "1");
		assertThat(actualResult.getLeft().getReflectStatus()).isEqualTo(RCReflectedState.REFLECTED);

		NtsAssert.atomTask(() -> actualResult.getRight().get(), any -> require.addAllDomain(any.get(), true));

	}

	public Stamp createStamp() {
		return new Stamp(new ContractCode("1"), // 契約コード
				new StampNumber("1"), // 打刻カード
				GeneralDateTime.ymdhms(2020, 01, 01, 10, 0, 0), // 打刻日時
				new Relieve(AuthcMethod.ID_AUTHC, // 打刻する方法. 認証方法
						StampMeans.NAME_SELECTION), /// 打刻する方法. 打刻手段
				new StampType(false, GoingOutReason.valueOf(1), // 外出理由
						SetPreClockArt.NONE, // 所定時刻セット区分
						ChangeClockAtr.GOING_TO_WORK, // 時刻変更区分
						ChangeCalArt.NONE), // 計算区分変更対象
				new RefectActualResult( null, null, null, null), Optional.empty());
	}
}
