package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/** Helper: シフトマスタ **/
public class ShiftMasterHelper {

	/** シフトマスタ(Dummy) **/
	private static final ShiftMaster dummy = ShiftMasterHelper.create("code", "name", "workTypeCode", Optional.of("workTimeCode"), Optional.of("importCode"));

	/**
	 * シフトマスタを作成する
	 * @param code コード
	 * @param name 名称
	 * @param workTypeCode 勤務種類コード
	 * @param workTimeCode 就業時間帯コード
	 * @param importCode 取り込みコード
	 * @return シフトマスタ
	 */
	public static ShiftMaster create(String code, String name
			, String workTypeCode, Optional<String> workTimeCode
			, Optional<String> importCode
	) {

		return ShiftMaster.create(
					"companyId", new ShiftMasterCode(code)
				,	DispInfo.create(name, "ffffff", "000000", Optional.of("remarks"))
				,	new WorkTypeCode(workTypeCode), workTimeCode.map(WorkTimeCode::new)
				,	importCode.map(ShiftMasterImportCode::new)
			);
	}

	/**
	 * シフトマスタ(Dummy)を作成する
	 * @return シフトマスタ(Dummy)
	 */
	public static ShiftMaster createDummy() {
		return ShiftMasterHelper.dummy;
	}

	/**
	 * コードを指定してシフトマスタ(Dummy)を作成する
	 * @param code シフトマスタコード
	 * @return シフトマスタ(Dummy)
	 */
	public static ShiftMaster createDummyWithCode(String code) {
		return ShiftMasterHelper.create(
				code
			,	ShiftMasterHelper.dummy.getDisplayInfor().getName().v()
			,	ShiftMasterHelper.dummy.getWorkTypeCode().v()
			,	ShiftMasterHelper.dummy.getWorkTimeCodeNotNull().map(WorkTimeCode::v)
			,	ShiftMasterHelper.dummy.getImportCode().map(ShiftMasterImportCode::v)
		);
	}

	/**
	 * 取り込みコードを指定してシフトマスタ(Dummy)を作成する
	 * @param importCode 取り込みコード
	 * @return シフトマスタ(Dummy)
	 */
	public static ShiftMaster createDummyWithImportCode(String importCode) {
		return ShiftMasterHelper.create(
						ShiftMasterHelper.dummy.getShiftMasterCode().v()
					,	ShiftMasterHelper.dummy.getDisplayInfor().getName().v()
					,	ShiftMasterHelper.dummy.getWorkTypeCode().v()
					,	ShiftMasterHelper.dummy.getWorkTimeCodeNotNull().map(WorkTimeCode::v)
					,	Optional.of(importCode)
				);
	}


	/** Helper: シフトマスタ表示情報 **/
	public static class DispInfo {

		private static final ShiftMasterDisInfor dummy = DispInfo.create("name", "ffffff", "000000", Optional.of("remarks"));

		/**
		 * 表示情報を作成する
		 * @param shiftMasterName 名称
		 * @param colorPC カラーコード(PC)
		 * @param colorSP カラーコード(スマホ)
		 * @param remarks 備考
		 * @return シフトマスタ表示情報
		 */
		public static ShiftMasterDisInfor create(
				String shiftMasterName, String colorPC, String colorSP, Optional<String> remarks
		) {
			return new ShiftMasterDisInfor(
						new ShiftMasterName(shiftMasterName)
					,	new ColorCodeChar6(colorPC)
					,	new ColorCodeChar6(colorSP)
					,	remarks.map(Remarks::new)
				);
		}

		/**
		 * 表示情報(Dummy)を作成する
		 * @return シフトマスタ表示情報(Dummy)
		 */
		public static ShiftMasterDisInfor createDummy() {
			return DispInfo.dummy;
		}
	}

}
