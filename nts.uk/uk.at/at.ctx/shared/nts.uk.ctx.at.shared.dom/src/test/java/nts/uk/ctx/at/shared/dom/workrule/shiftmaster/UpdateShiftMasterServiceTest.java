package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.error.BusinessException;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.UpdateShiftMasterService.Require;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

@RunWith(JMockit.class)
public class UpdateShiftMasterServiceTest {

	@Injectable private Require require;



	/**
	 * Target	: update
	 * Pattern	: 同じ取り込みコードが既に存在する
	 * Expect	: throw Msg_2163
	 */
	@Test
	public void test_update_throw_Msg_2163() {

		val newImportCode = new ShiftMasterImportCode("newImportCode");
		val original = ShiftMasterHelper.createDummyWithImportCode("oldImportCode");

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

			// 重複チェック：取り込みコード
			require.checkDuplicateImportCode((ShiftMasterImportCode)any);
			result = true;

		}};

		NtsAssert.businessException("Msg_2163"
				, () -> UpdateShiftMasterService.update(require
							, new ShiftMasterCode("shiftMasterCode")
							, ShiftMasterHelper.DispInfo.createDummy()
							, new WorkInformation("workTypeCode", null)
							, Optional.of(newImportCode)
						)
		);

	}

	/**
	 * Target	: update
	 * Pattern	: 同じ勤務情報が既に存在する
	 * Expect	: throw Msg_1610
	 */
	@Test
	public void test_update_throw_Msg_1610() {

		val original = ShiftMasterHelper.createDummyWithCode("original");
		val duplicated = ShiftMasterHelper.createDummyWithCode("duplicated");

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.getByWorkTypeAndWorkTime((WorkTypeCode)any, workTimeCode);
			result = Optional.of(duplicated);

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.businessException("Msg_1610"
				, () -> UpdateShiftMasterService.update(require
							, original.getShiftMasterCode()
							, ShiftMasterHelper.DispInfo.createDummy()
							, new WorkInformation("workTypeCode", null)
							, original.getImportCode()
						)
		);

	}

	/**
	 * Target	: update
	 * Pattern	: シフトマスタがエラー
	 * Expect	: throw シフトマスタ.エラーチェックする(Require)で発生したException
	 */
	@Test
	public void test_update_shiftMasterIsError() {

		val original = ShiftMasterHelper.createDummyWithCode("original");

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				throw new BusinessException("Msg_435");
			}
		};

		NtsAssert.businessException("Msg_435"
				, () -> UpdateShiftMasterService.update(require
							, original.getShiftMasterCode()
							, ShiftMasterHelper.DispInfo.createDummy()
							, new WorkInformation(original.getWorkTypeCode(), original.getWorkTimeCodeNotNull().orElse(null))
							, Optional.empty()
						)
		);

	}



	/**
	 * Target	: update
	 * Pattern	: エラーなし
	 * 				・新しい取り込みコードがEmpty
	 * 				・勤務情報変更あり
	 * 				・勤務情報での重複なし
	 * Expect	: No exception
	 */
	@Test
	public void test_update_complete_newImportCodeIsEmpty_workInfoIsNotDuplicated() {

		val original = ShiftMasterHelper.create(
						"codeOrg", "name is original"
					,	"workTypeCodeOrg", Optional.of("workTimeCode")
					,	Optional.of("importCodeOrg")
				);
		val changed = ShiftMasterHelper.create(
						original.getShiftMasterCode().v(), "name is changed"
					,	"workTypeCodeChg", Optional.empty()
					,	Optional.empty()
				);

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.getByWorkTypeAndWorkTime((WorkTypeCode)any, workTimeCode);

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.atomTask(
				() -> UpdateShiftMasterService.update(require
							, changed.getShiftMasterCode()
							, changed.getDisplayInfor()
							, new WorkInformation(changed.getWorkTypeCode(), changed.getWorkTimeCodeNotNull().orElse(null))
							, changed.getImportCode()
						)
			,	any -> require.update(changed)
		);

	}

	/**
	 * Target	: update
	 * Pattern	: エラーなし
	 * 				・取り込みコード変更なし
	 * 				・勤務情報変更あり
	 * 				・勤務情報での重複なし
	 * Expect	: No exception
	 */
	@Test
	public void test_update_complete_importCodeIsNotChange_workInfoIsNotDuplicated() {

		val original = ShiftMasterHelper.create(
						"codeOrg", "name is original"
					,	"workTypeCodeOrg", Optional.of("workTimeCode")
					,	Optional.of("importCodeOrg")
				);
		val changed = ShiftMasterHelper.create(
						original.getShiftMasterCode().v(), "name is changed"
					,	"workTypeCodeChg", Optional.empty()
					,	original.getImportCode().map(ShiftMasterImportCode::v)
				);

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.getByWorkTypeAndWorkTime((WorkTypeCode)any, workTimeCode);

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.atomTask(
				() -> UpdateShiftMasterService.update(require
							, changed.getShiftMasterCode()
							, changed.getDisplayInfor()
							, new WorkInformation(changed.getWorkTypeCode(), changed.getWorkTimeCodeNotNull().orElse(null))
							, changed.getImportCode().map( e -> new ShiftMasterImportCode(e.v()) )
						)
			,	any -> require.update(changed)
		);

	}

	/**
	 * Target	: update
	 * Pattern	: エラーなし
	 * 				・取り込みコード変更あり
	 * 				・取り込みコードでの重複なし
	 * 				・勤務情報変更なし
	 * Expect	: No exception
	 */
	@Test
	public void test_update_complete_importCodeIsNotDuplicated_workInfoIsNotChange() {

		val original = ShiftMasterHelper.create(
						"codeOrg", "name is original"
					,	"workTypeCodeOrg", Optional.empty()
					,	Optional.empty()
				);
		val changed = ShiftMasterHelper.create(
						original.getShiftMasterCode().v(), "name is changed"
					,	original.getWorkTypeCode().v(), original.getWorkTimeCodeNotNull().map(WorkTimeCode::v)
					,	Optional.of("importCodeChg")
				);

		new Expectations() {{

			// シフトマスタを取得する
			require.getByShiftMaterCd((ShiftMasterCode)any);
			result = Optional.of(original);

			// 重複チェック：取り込みコード
			require.checkDuplicateImportCode((ShiftMasterImportCode)any);
			result = false;

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.getByWorkTypeAndWorkTime((WorkTypeCode)any, workTimeCode);
			result = Optional.of(original);

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.atomTask(
				() -> UpdateShiftMasterService.update(require
							, changed.getShiftMasterCode()
							, changed.getDisplayInfor()
							, new WorkInformation(changed.getWorkTypeCode(), changed.getWorkTimeCodeNotNull().orElse(null))
							, changed.getImportCode()
						)
			,	any -> require.update(changed)
		);

	}

}
