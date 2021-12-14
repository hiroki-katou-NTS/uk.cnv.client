package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * シフトマスタを更新する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.シフトマスタ.シフトマスタを更新する
 * @author tutk
 *
 */
public class UpdateShiftMasterService {

	/**
	 * 更新する
	 * @param require Require
	 * @param shiftMasterCode コード
	 * @param displayInfor 表示情報
	 * @param workInformation 勤務情報
	 * @param importCode 取り込みコード
	 * @return
	 */
	public static AtomTask update(Require require
			, ShiftMasterCode shiftMasterCode, ShiftMasterDisInfor displayInfor
			, WorkInformation workInformation, Optional<ShiftMasterImportCode> importCode
	) {

		// シフトマスタを取得する
		val shiftMaster = require.getByShiftMaterCd(shiftMasterCode).get();


		// 重複チェック：取り込みコード
		if ( importCode.isPresent()
				&& !shiftMaster.getImportCode().equals(importCode)
				&& require.checkDuplicateImportCode(importCode.get()) ) {
			throw new BusinessException("Msg_2163");
		}


		// 変更→エラーチェック
		shiftMaster.change(displayInfor, importCode, workInformation);
		shiftMaster.checkError(require);


		// 重複チェック：勤務種類＋就業時間帯
		val shiftMasterByWorkInfo = require.getByWorkTypeAndWorkTime(
						workInformation.getWorkTypeCode()
					,	workInformation.getWorkTimeCodeNotNull()
				);
		if ( shiftMasterByWorkInfo.isPresent()
				&& !shiftMasterByWorkInfo.get().getShiftMasterCode().equals( shiftMasterCode ) ) {
			throw new BusinessException("Msg_1610");
		}

		// AtomTaskを返す
		return AtomTask.of(() -> {
			require.update(shiftMaster);
		});

	}



	public static interface Require extends ShiftMaster.Require {
		/**
		 * 取得する
		 * @param shiftMaterCode シフトマスタコード
		 * @return
		 */
		Optional<ShiftMaster> getByShiftMaterCd(ShiftMasterCode shiftMaterCode);
		/**
		 * 勤務情報で取得する
		 * @param workTypeCd 勤務種類コード
		 * @param workTimeCd 就業時間帯コード
		 * @return
		 */
		Optional<ShiftMaster> getByWorkTypeAndWorkTime(WorkTypeCode workTypeCd, Optional<WorkTimeCode> workTimeCd);
		/**
		 * 登録する
		 * @param shiftMater シフトマスタ
		 */
		void update(ShiftMaster shiftMater);
		/**
		 * 取り込みコードが重複しているか
		 * @param importCode
		 * @return
		 */
		boolean checkDuplicateImportCode(ShiftMasterImportCode importCode);
	}

}
