package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.RemNumShiftListWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;

/**
 * 実装：付与残数データから使用数を消化する
 * @author yuri_tamakoshi
 */
public class GetAnnualLeaveUsedNumberFromRemDataService {

	/**
	 * 付与残数データから使用数を消化する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param remainingData 年休付与残数データ
	 * @param usedNumber 使用数
	 * @return 年休付与残数データ(List)
	 */
	public static List<LeaveGrantRemainingData> getAnnualLeaveGrantRemainingData(
			String companyId,
			String employeeId,
			List<LeaveGrantRemainingData> remainingData,
			LeaveUsedNumber usedNumber,
			RequireM3 require) {

		// 暫定年休管理データ
		List<TempAnnualLeaveMngs> tempAnnualLeaveMngs = new ArrayList<>();


		// 使用数を暫定年休管理データに変換する
		tempAnnualLeaveMngs = GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// 暫定年休管理データの件数ループ
		for (int idx = 0; idx < tempAnnualLeaveMngs.size(); idx++){
			val currentProcess = tempAnnualLeaveMngs.get(idx);

			// 休暇残数シフトリストWORK
			RemNumShiftListWork remNumShiftListWork = new RemNumShiftListWork();

			// 年休消化する(休暇残数を指定使用数消化する)
			//			社員ID←パラメータ「社員ID」
			//			年月日←作成した「暫定休暇管理データ．対象日」
			//			休暇使用数←作成した「暫定休暇管理データ．年休使用数」
			//			付与残数データ←年休付与残数データ
			LeaveGrantRemainingData.digest(
					require, 							// WorkingConditionItem:労働条件取得
					remainingData, 				//年休付与残数データ
					remNumShiftListWork, 	// 複数の付与残数の消化処理を行う一時変数
					new LeaveUsedNumber(currentProcess), // 作成した「暫定休暇管理データ．年休使用数」
					companyId,
					employeeId,
					currentProcess.getYmd()); 				// 作成した「暫定休暇管理データ．対象日」
		}

		return remainingData;

	}
	// require
	public static interface RequireM3 extends LeaveRemainingNumber.RequireM3 {

		// 労働条件取得
		Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate);
	}

}
