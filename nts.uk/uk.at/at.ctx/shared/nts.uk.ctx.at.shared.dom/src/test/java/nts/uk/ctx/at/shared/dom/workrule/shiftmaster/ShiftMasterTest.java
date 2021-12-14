package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster.Require;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

@RunWith(JMockit.class)
public class ShiftMasterTest {

	@Injectable
	Require require;

	@Test
	public void test_getters() {
		val instance = ShiftMasterHelper.createDummy();
		NtsAssert.invokeGetters(instance);
	}



	/**
	 * Target	:
	 * 		- create(constructor)
	 * 		- change
	 * Expect	:
	 * 		・指定した情報で正しくインスタンスが生成されること
	 * 		・指定した情報で正しくインスタンスの内容が変更されること
	 */
	@Test
	public void test_create_change() {

		//勤務情報
		val workInfoBefore = new WorkInformation("WorkType01", "WorkTime01");
		val workInfoAfter = new WorkInformation("WorkType02", "WorkTime02");

		//表示情報
		val displayInfoBefore = ShiftMasterHelper.DispInfo.create("Name_01", "fff", "ccc", Optional.of("Note_01"));
		val displayInfoAfter = ShiftMasterHelper.DispInfo.create("Name_02", "000", "999", Optional.of("Note_02"));

		//取込コード
		val impCdBefore = new ShiftMasterImportCode("ImportBefore");
		val impCdAfter = new ShiftMasterImportCode("ImportAfter");

		/* 作成  */
		val shiftMaster = new ShiftMaster("cid"
								,	new ShiftMasterCode("shiftMaster01")
								,	displayInfoBefore
								,	workInfoBefore.getWorkTypeCode().v()
								,	workInfoBefore.getWorkTimeCode().v()
								,	Optional.of(impCdBefore)
							);
		//表示情報
		assertThat(shiftMaster.getDisplayInfor().getName()).isEqualTo(displayInfoBefore.getName());
		assertThat(shiftMaster.getDisplayInfor().getColor()).isEqualTo(displayInfoBefore.getColor());
		assertThat(shiftMaster.getDisplayInfor().getColorSmartPhone()).isEqualTo(displayInfoBefore.getColorSmartPhone());
		assertThat(shiftMaster.getDisplayInfor().getRemarks()).isEqualTo(displayInfoBefore.getRemarks());

		//勤務情報
		assertThat(shiftMaster.getWorkTypeCode()).isEqualTo(workInfoBefore.getWorkTypeCode());
		assertThat(shiftMaster.getWorkTimeCode()).isEqualTo(workInfoBefore.getWorkTimeCode());

		//取込コード
		assertThat(shiftMaster.getImportCode()).isPresent()
				.get().isEqualTo(impCdBefore);


		/* 変更  */
		shiftMaster.change(displayInfoAfter, Optional.of(impCdAfter), workInfoAfter);

		//表示情報
		assertThat(shiftMaster.getDisplayInfor().getName()).isEqualTo(displayInfoAfter.getName());
		assertThat(shiftMaster.getDisplayInfor().getColor()).isEqualTo(displayInfoAfter.getColor());
		assertThat(shiftMaster.getDisplayInfor().getColorSmartPhone()).isEqualTo(displayInfoAfter.getColorSmartPhone());
		assertThat(shiftMaster.getDisplayInfor().getRemarks()).isEqualTo(displayInfoAfter.getRemarks());

		//勤務情報
		assertThat(shiftMaster.getWorkTypeCode()).isEqualTo(workInfoAfter.getWorkTypeCode());
		assertThat(shiftMaster.getWorkTimeCode()).isEqualTo(workInfoAfter.getWorkTimeCode());

		//取込コード
		assertThat(shiftMaster.getImportCode()).isPresent()
				.get().isEqualTo(impCdAfter);

	}


	/**
	 * Target	: change
	 * Pattern	: Optional項目の値変更
	 * 				・就業時間帯コード	： "workTimeCd"→empty
	 * 				・取り込みコード	： empty→"importCode"
	 * Expect	: インスタンスの内容が正しく変更されること
	 */
	@Test
	public void test_change_optionalItems_1() {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCd", Optional.of("workTimeCd"), Optional.empty());

		/* 変更前 */
		// 検証
		assertThat( shiftMaster.getWorkTimeCodeNotNull() ).isPresent().get().isEqualTo(new WorkTimeCode("workTimeCd"));
		assertThat( shiftMaster.getImportCode() ).isEmpty();

		/* 変更後 */
		// 変更
		shiftMaster.change(
					shiftMaster.getDisplayInfor()
				,	Optional.of(new ShiftMasterImportCode("importCode"))
				,	new WorkInformation(shiftMaster.getWorkTypeCode().v(), null)
			);
		// 検証
		assertThat( shiftMaster.getWorkTimeCodeNotNull() ).isEmpty();
		assertThat( shiftMaster.getImportCode() ).isPresent().get().isEqualTo(new ShiftMasterImportCode("importCode"));

	}

	/**
	 * Target	: change
	 * Pattern	: Optional項目の値変更
	 * 				・就業時間帯コード	： empty→"workTimeCd"
	 * 				・取り込みコード	： "importCode"→empty
	 * Expect	: インスタンスの内容が正しく変更されること
	 */
	@Test
	public void test_change_optionalItems_2() {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCd", Optional.empty(), Optional.of("importCode"));

		/* 変更前 */
		// 検証
		assertThat( shiftMaster.getWorkTimeCodeNotNull() ).isEmpty();
		assertThat( shiftMaster.getImportCode() ).isPresent().get().isEqualTo(new ShiftMasterImportCode("importCode"));

		/* 変更後 */
		// 変更
		shiftMaster.change(
					shiftMaster.getDisplayInfor()
				,	Optional.empty()
				,	new WorkInformation(shiftMaster.getWorkTypeCode().v(), "workTimeCd")
			);
		// 検証
		assertThat( shiftMaster.getWorkTimeCodeNotNull() ).isPresent().get().isEqualTo(new WorkTimeCode("workTimeCd"));
		assertThat( shiftMaster.getImportCode() ).isEmpty();

	}



	/**
	 * Target	: checkError
	 * Pattern	: エラー状態＝勤務種類が削除された
	 * 				・勤務種類が取得できない
	 * Expect	: throw Msg_1608
	 */
	@Test
	public void test_checkError_throw_Msg_1608() {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCode", Optional.empty(), Optional.empty());

		new Expectations() {{
			// 勤務種類を取得する
			require.getWorkType(anyString);
		}};

		NtsAssert.businessException("Msg_1608", () -> shiftMaster.checkError(require));

	}

	/**
	 * Target	: checkError
	 * Pattern	: エラー状態＝就業時間帯が削除された
	 * 				・就業時間帯が任意
	 * 				・就業時間帯コードが設定されている
	 * 				・就業時間帯が取得できない
	 * Expect	: throw Msg_1609
	 */
	@Test
	public void test_checkError_throw_Msg_1609(@Injectable WorkType workType) {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCode", Optional.of("workTimeCode"), Optional.empty());

		new Expectations() {{
			// 勤務種類を取得する
			require.getWorkType(anyString);
			result = Optional.of(workType);
			// 勤務種類：就業時間帯が必須か＝任意
			require.checkNeededOfWorkTimeSetting(anyString);
			result = SetupType.OPTIONAL;
			// 就業時間帯を取得する
			require.getWorkTime(anyString);
		}};

		NtsAssert.businessException("Msg_1609", () -> shiftMaster.checkError(require));

	}

	/**
	 * Target	: checkError
	 * Pattern	: エラー状態＝就業時間帯が必須なのに設定されていない
	 * 				・就業時間帯が必須
	 * 				・就業時間帯コードが設定されていない
	 * Expect	: throw Msg_435
	 */
	@Test
	public void test_checkError_throw_Msg_435(@Injectable WorkType workType) {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCode", Optional.empty(), Optional.empty());

		new Expectations() {{
			// 勤務種類を取得する
			require.getWorkType(anyString);
			result = Optional.of(workType);
			// 勤務種類：就業時間帯が必須か＝必須
			require.checkNeededOfWorkTimeSetting(anyString);
			result = SetupType.REQUIRED;
		}};

		NtsAssert.businessException("Msg_435", () -> shiftMaster.checkError(require));

	}

	/**
	 * Target	: checkError
	 * Pattern	: エラー状態＝就業時間帯が不要なのに設定されている
	 * 				・就業時間帯が不要
	 * 				・就業時間帯コードが設定されている
	 * Expect	: throw Msg_434
	 */
	@Test
	public void test_checkError_throw_Msg_434(@Injectable WorkType workType) {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCode", Optional.of("workTimeCode"), Optional.empty());

		new Expectations() {{
			// 勤務種類を取得する
			require.getWorkType(anyString);
			result = Optional.of(workType);
			// 勤務種類：就業時間帯が必須か＝不要
			require.checkNeededOfWorkTimeSetting(anyString);
			result = SetupType.NOT_REQUIRED;
		}};

		NtsAssert.businessException("Msg_434", () -> shiftMaster.checkError(require));

	}

}
