package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * シフトマスタを作る
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.シフトマスタ.シフトマスタを作る
 * @author tutk
 *
 */
public class MakeShiftMasterService {

	/**
	 * シフトを作る
	 * @param require Require
	 * @param companyId 会社ID
	 * @param shiftMasterCode コード
	 * @param workTypeCd 勤種コード
	 * @param workTimeCd 就時コード
	 * @param displayInfor 表示情報
	 * @param importCode 取り込みコード
	 * @return AtomTask
	 */
	public static AtomTask makeShiftMaster(Require require
			,	String companyId, ShiftMasterCode shiftMasterCode
			,	WorkTypeCode workTypeCd, Optional<WorkTimeCode> workTimeCd
			,	ShiftMasterDisInfor displayInfor
			,	Optional<ShiftMasterImportCode> importCode
	) {

		// 重複チェック：コード
		if ( require.checkExistsByCode(shiftMasterCode) ) {
			throw new BusinessException("Msg_3");
		}

		// 重複チェック：取り込みコード
		if ( importCode.isPresent() && require.checkDuplicateImportCode(importCode.get()) ) {
			throw new BusinessException("Msg_2163");
		}


		// 作成→エラーチェック
		val shiftMater = ShiftMaster.create(companyId, shiftMasterCode, displayInfor, workTypeCd, workTimeCd, importCode);
		shiftMater.checkError(require);


		// 重複チェック：勤務種類＋就業時間帯
		if ( require.checkExists(workTypeCd, workTimeCd) ) {
			throw new BusinessException("Msg_1610");
		}


		// AtomTaskを返す
		return AtomTask.of(() -> {
			require.insert(shiftMater);
		});

	}



	public static interface Require extends ShiftMaster.Require {
		/**
		 * 既に登録されているか
		 * @param workTypeCd 勤務種類コード
		 * @param workTimeCd 就業時間帯コード
		 * @return
		 */
		boolean checkExists(WorkTypeCode workTypeCd, Optional<WorkTimeCode> workTimeCd);
		/**
		 * コードが重複しているか
		 * @param shiftMasterCd シフトマスタコード
		 * @return
		 */
		boolean checkExistsByCode(ShiftMasterCode shiftMasterCd);
		/**
		 * 登録する
		 * @param shiftMater シフトマスタ
		 */
		void insert(ShiftMaster shiftMater);
		/**
		 * 取り込みコードが重複しているか
		 * @param importCode 取り込みコード
		 * @return
		 */
		boolean checkDuplicateImportCode(ShiftMasterImportCode importCode);
	}

}
