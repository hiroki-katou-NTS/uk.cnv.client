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
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.MakeShiftMasterService.Require;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

@RunWith(JMockit.class)
public class MakeShiftMasterServiceTest {

	@Injectable private Require require;



	/**
	 * Target	: makeShiftMaster
	 * Pattern	: 同じコードが既に存在する
	 * Expect	: throw Msg_3
	 */
	@Test
	public void testMakeShiftMaster_throw_Msg_3() {

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = true;

		}};

		NtsAssert.businessException("Msg_3"
				, () -> MakeShiftMasterService.makeShiftMaster(require
							, "companyId"
							, new ShiftMasterCode("shiftMasterCode")
							, new WorkTypeCode("workTypeCode")
							, Optional.of(new WorkTimeCode("workTimeCode"))
							, ShiftMasterHelper.DispInfo.createDummy()
							, Optional.of(new ShiftMasterImportCode("importCode"))
						)
		);

	}

	/**
	 * Target	: makeShiftMaster
	 * Pattern	: 同じ取り込みコードが既に存在する
	 * Expect	: throw Msg_2163
	 */
	@Test
	public void test_makeShiftMaster_throw_Msg_2163() {

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = false;

			// 重複チェック：取り込みコード
			require.checkDuplicateImportCode((ShiftMasterImportCode)any);
			result = true;

		}};

		NtsAssert.businessException("Msg_2163"
				, () -> MakeShiftMasterService.makeShiftMaster(require
							, "companyId"
							, new ShiftMasterCode("shiftMasterCode")
							, new WorkTypeCode("workTypeCode")
							, Optional.empty()
							, ShiftMasterHelper.DispInfo.createDummy()
							, Optional.of(new ShiftMasterImportCode("importCode"))
						)
		);

	}

	/**
	 * Target	: makeShiftMaster
	 * Pattern	: 同じ勤務情報が既に存在する
	 * Expect	: throw Msg_1610
	 */
	@Test
	public void test_makeShiftMaster_throw_Msg_1610(@Injectable WorkType workType) {

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = false;
			// 重複チェック：取り込みコード
			require.checkDuplicateImportCode((ShiftMasterImportCode)any);
			result = false;

			// 重複チェック：勤務種類コード＋就業時間帯コード
			require.checkExists((WorkTypeCode)any, Optional.empty());
			result = true;

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.businessException("Msg_1610"
				, () -> MakeShiftMasterService.makeShiftMaster(require
							, "companyId"
							, new ShiftMasterCode("shiftMasterCode")
							, new WorkTypeCode("workTypeCode")
							, Optional.empty()
							, ShiftMasterHelper.DispInfo.createDummy()
							, Optional.of(new ShiftMasterImportCode("importCode"))
						)
		);

	}

	/**
	 * Target	: makeShiftMsater
	 * Pattern	: シフトマスタがエラー
	 * Expect	: throw シフトマスタ.エラーチェックする(Require)で発生したException
	 */
	@Test
	public void test_makeShiftMsater_shiftMasterIsError() {

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = false;

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				throw new BusinessException("Msg_434");
			}
		};

		NtsAssert.businessException("Msg_434"
				, () -> MakeShiftMasterService.makeShiftMaster(require
							, "companyId"
							, new ShiftMasterCode("shiftMasterCode")
							, new WorkTypeCode("workTypeCode")
							, Optional.empty()
							, ShiftMasterHelper.DispInfo.createDummy()
							, Optional.empty()
						)
		);

	}



	/**
	 * Target	: makeShiftMaster
	 * Pattern	: エラーなし※取り込みコード指定なし
	 * Expect	: No exception
	 */
	@Test
	public void test_makeShiftMsater_complete_importCodeIsEmpty() {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCd", Optional.of("workTimeCd"), Optional.empty());

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = false;

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.checkExists((WorkTypeCode)any, workTimeCode);
			result = false;

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.atomTask(
				() -> MakeShiftMasterService.makeShiftMaster(require
							, shiftMaster.getCompanyId()
							, shiftMaster.getShiftMasterCode()
							, shiftMaster.getWorkTypeCode()
							, shiftMaster.getWorkTimeCodeNotNull()
							, shiftMaster.getDisplayInfor()
							, shiftMaster.getImportCode()
						)
			,	any -> require.insert(shiftMaster)
		);

	}

	/**
	 * Target	: makeShiftMaster
	 * Pattern	: エラーなし※取り込みコード指定あり
	 * Expect	: No exception
	 */
	@Test
	public void test_makeShiftMsater_complete_importCodeIsNotEmpty() {

		val shiftMaster = ShiftMasterHelper.create("code", "name", "workTypeCd", Optional.empty(), Optional.of("importCode"));

		new Expectations() {{

			// 重複チェック：コード
			require.checkExistsByCode((ShiftMasterCode)any);
			result = false;

			// 重複チェック：取り込みコード
			require.checkDuplicateImportCode((ShiftMasterImportCode)any);
			result = false;

			// 重複チェック：勤務種類コード＋就業時間帯コード
			@SuppressWarnings("unchecked") val workTimeCode = (Optional<WorkTimeCode>)any;
			require.checkExists((WorkTypeCode)any, workTimeCode);
			result = false;

		}};
		new MockUp<ShiftMaster>() {
			/** [Mock] エラーチェックする **/
			@Mock public void checkError(@SuppressWarnings("unused") ShiftMaster.Require require) {
				// エラーなし
			}
		};

		NtsAssert.atomTask(
				() -> MakeShiftMasterService.makeShiftMaster(require
							, shiftMaster.getCompanyId()
							, shiftMaster.getShiftMasterCode()
							, shiftMaster.getWorkTypeCode()
							, shiftMaster.getWorkTimeCodeNotNull()
							, shiftMaster.getDisplayInfor()
							, shiftMaster.getImportCode()
						)
			,	any -> require.insert(shiftMaster)
		);

	}

}
