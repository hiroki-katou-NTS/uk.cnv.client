package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.Optional;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * 取り込みコードで勤務予定を作る
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.取り込みコードで勤務予定を作る
 * @author dan_pv
 */
public class CreateWorkScheduleByImportCode {

	/**
	 * 作る
	 * @param require
	 * @param employeeId 社員ID
	 * @param date 年月日
	 * @param importCode 取り込みコード
	 * @param isOverwrite 上書きするか
	 * @return 勤務予定の登録処理結果
	 */
	public static ResultOfRegisteringWorkSchedule create(
			Require require,
			String employeeId,
			GeneralDate date,
			ShiftMasterImportCode importCode,
			boolean isOverwrite) {

		// 上書き可否判定
		val isScheduleExisted = require.isWorkScheduleExisted(employeeId, date);
		if ( isScheduleExisted && ! isOverwrite ) {

			return ResultOfRegisteringWorkSchedule.createWithError(
					employeeId,
					date,
					new BusinessException("Msg_2183").getMessage());
		}

		// シフトマスタを取得
		val shiftMaster = require.getShiftMaster(importCode);
		if ( ! shiftMaster.isPresent() ) {

			return ResultOfRegisteringWorkSchedule.createWithError(
					employeeId,
					date,
					new BusinessException("Msg_1705").getMessage());
		}

		// 取り込み結果から勤務予定を新規作成
		WorkSchedule newWorkSchedule;
		try {
			newWorkSchedule = WorkSchedule.create(require, employeeId, date, shiftMaster.get());
		} catch (BusinessException e) {

			return ResultOfRegisteringWorkSchedule.createWithError(
					employeeId,
					date,
					e.getMessage() );
		}

		// 勤務予定を補正する
		WorkSchedule correctedResult = require.correctWorkSchedule(newWorkSchedule);

		// 登録処理
		AtomTask atomTask = AtomTask.of( () -> {
			if ( !isScheduleExisted ) {
				require.insertWorkSchedule( correctedResult );
			} else {
				require.updateWorkSchedule( correctedResult );
			}

			require.registerTemporaryData(employeeId, date);
		});

		return ResultOfRegisteringWorkSchedule.create(atomTask);
	}

	public static interface Require extends WorkSchedule.Require {

		/**
		 * 勤務予定が登録されているか
		 * @param employeeId 社員ID
		 * @param ymd 年月日
		 * @return true:登録されている/false:登録されていない
		 */
		boolean isWorkScheduleExisted(String employeeId, GeneralDate date);

		/**
		 * シフトマスタを取得する
		 * @param importCode 取り込みコード
		 * @return Optional<シフトマスタ>
		 */
		Optional<ShiftMaster> getShiftMaster(ShiftMasterImportCode importCode);

		/**
		 * 勤務予定を補正する
		 * @param workSchedule 勤務予定
		 */
		WorkSchedule correctWorkSchedule(WorkSchedule workSchedule);

		/**
		 * 勤務予定を新規登録する
		 * @param workSchedule 勤務予定
		 */
		void insertWorkSchedule(WorkSchedule workSchedule);

		/**
		 * 勤務予定を更新する
		 * @param workSchedule 勤務予定
		 */
		void updateWorkSchedule(WorkSchedule workSchedule);

		/**
		 * 暫定データを登録する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 */
		void registerTemporaryData(String employeeId, GeneralDate date);
	}

}
