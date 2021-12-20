package nts.uk.ctx.at.record.dom.applicationcancel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
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
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

@RunWith(JMockit.class)
public class GetTargetDateRecordApplicationTest {

	@Injectable
	private GetTargetDateRecordApplication.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 打刻を作る、対象日を取得する
	 * 
	 * 準備するデータ
	 * 
	 * →   レコーダイメージ申請
	 * 
	 */
	@Test
	public void test() {
		AppRecordImageShare appShare = ReflectApplicationHelper.createAppRecord();
		WorkGroup group = new WorkGroup(new WorkCode("DUMMY"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()); //dummy

		new Expectations() {
			{
				require.getLstStampCardBySidAndContractCd(anyString);
				result = Arrays.asList(new StampCard("1", "1", "1"));

			}

		};

		Pair<Optional<GeneralDate>, Optional<Stamp>> actualResult = GetTargetDateRecordApplication
				.getTargetDate(require, "", appShare);

		Stamp expectedResult = new Stamp(new ContractCode("1"), //契約コード
				new StampNumber("1"),//打刻カード
				GeneralDateTime.ymdhms(2020, 01, 01, 10, 0, 0),//打刻日時
				new Relieve(AuthcMethod.ID_AUTHC, //打刻する方法. 認証方法
						StampMeans.NAME_SELECTION),///打刻する方法. 打刻手段
				new StampType(false, 
						GoingOutReason.valueOf(1), //外出理由
						SetPreClockArt.NONE, // 所定時刻セット区分
						ChangeClockAtr.GOING_TO_WORK,//時刻変更区分
						ChangeCalArt.NONE),//計算区分変更対象
				new RefectActualResult(null, null, null, group), Optional.empty());

		assertStamp(actualResult.getRight().get(), expectedResult);

	}


	
	private void assertStamp(Stamp stampActual, Stamp stampExpected) {

		assertThat(stampActual.getCardNumber().v()).isEqualTo(stampExpected.getCardNumber().v());

		assertThat(stampActual.getStampDateTime()).isEqualTo(stampExpected.getStampDateTime());

		assertThat(stampActual.getRelieve().getAuthcMethod()).isEqualTo(stampExpected.getRelieve().getAuthcMethod());

		assertThat(stampActual.getRelieve().getStampMeans()).isEqualTo(stampExpected.getRelieve().getStampMeans());

		assertThat(stampActual.getType().getGoOutArt().get()).isEqualTo(stampExpected.getType().getGoOutArt().get());

		assertThat(stampActual.getType().getChangeCalArt()).isEqualTo(stampExpected.getType().getChangeCalArt());

		assertThat(stampActual.getType().getChangeClockArt()).isEqualTo(stampExpected.getType().getChangeClockArt());

		assertThat(stampActual.getType().getSetPreClockArt()).isEqualTo(stampExpected.getType().getSetPreClockArt());

		assertThat(stampActual.getCardNumber().v()).isEqualTo(stampExpected.getCardNumber().v());

	}
}
