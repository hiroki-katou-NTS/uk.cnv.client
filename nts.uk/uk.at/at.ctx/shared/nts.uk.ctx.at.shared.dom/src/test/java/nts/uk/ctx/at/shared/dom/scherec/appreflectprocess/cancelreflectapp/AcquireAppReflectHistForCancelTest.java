package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AppReflectExecInfo;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;

@RunWith(JMockit.class)
public class AcquireAppReflectHistForCancelTest {

	@Injectable
	private AcquireAppReflectHistForCancel.Require require;

	/*
	 * テストしたい内容
	 * 
	 * →エンプティーの値を返します
	 * 
	 * 準備するデータ
	 * 
	 * →取消す申請の最新の反映履歴がない
	 */
	@Test
	public void test() {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		val actualResult = AcquireAppReflectHistForCancel.process(require, app, GeneralDate.ymd(2021, 4, 22),
				ScheduleRecordClassifi.RECORD);

		assertThat(actualResult).isEmpty();
	}

	/*
	 * テストしたい内容
	 * 
	 * →「元に戻すための申請反映履歴」が最古の申請
	 * 
	 * 準備するデータ
	 * 
	 * →最新の申請反映履歴より前の申請反映履歴データは同じIDの申請だけ
	 */
	@Test
	public void test2() {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				require.findAppReflectHist(anyString, anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean);
				result = Arrays.asList(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0)));

				require.findAppReflectHistDateCond(anyString, (GeneralDate) any,
						(ScheduleRecordClassifi) any, anyBoolean, GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0));
				result = Arrays.asList(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0)),
													 createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 18, 0, 0, 0)),
													 createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 16, 0, 0, 0)));

			}
		};
		val actualResult = AcquireAppReflectHistForCancel.process(require, app, GeneralDate.ymd(2021, 4, 22),
				ScheduleRecordClassifi.RECORD);

		assertHist(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 16, 0, 0, 0)), actualResult.get().getAppHistPrev());
	}

	/*
	 * テストしたい内容
	 * 
	 * →「元に戻すための申請反映履歴」は申請のIDが異なる前の最新の申請
	 * 
	 * 準備するデータ
	 * 
	 * →最新の申請反映履歴より前の申請反映履歴データは他のIDの申請を保存する
	 */
	@Test
	public void test3() {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(PrePostAtrShare.PREDICT);

		new Expectations() {
			{
				require.findAppReflectHist(anyString, anyString, (GeneralDate) any, (ScheduleRecordClassifi) any,
						anyBoolean);
				result = Arrays.asList(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0)));

				require.findAppReflectHistDateCond(anyString, (GeneralDate) any,
						(ScheduleRecordClassifi) any, anyBoolean, GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0));
				result = Arrays.asList(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 19, 0, 0, 0)),
													 createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 18, 0, 0, 0)),
													 createAppReflectHistAll("2", GeneralDateTime.ymdhms(2021, 4, 16, 0, 0, 0)),
													 createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 15, 0, 0, 0)));

			}
		};
		val actualResult = AcquireAppReflectHistForCancel.process(require, app, GeneralDate.ymd(2021, 4, 22),
				ScheduleRecordClassifi.RECORD);

		assertHist(createAppReflectHistAll("1", GeneralDateTime.ymdhms(2021, 4, 15, 0, 0, 0)), actualResult.get().getAppHistPrev());
	}
	
	private ApplicationReflectHistory createAppReflectHistAll(String id, GeneralDateTime date) {
		return new ApplicationReflectHistory("1", GeneralDate.ymd(2021, 4, 01), id, ScheduleRecordClassifi.RECORD,
				true, new ArrayList<>(), new AppReflectExecInfo(false, "1", date));
	}

	private void assertHist(ApplicationReflectHistory before, ApplicationReflectHistory after) {
		assertThat(before.getApplicationId()).isEqualTo(after.getApplicationId());
		assertThat(before.getAppExecInfo().getReflectionTime()).isEqualTo(after.getAppExecInfo().getReflectionTime());
	}
}
