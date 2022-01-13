package nts.uk.ctx.at.shared.dom.remainingnumber.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;

/**
 * 休暇残数シフトリストWORK
 * 
 * @author masaaki_jinno
 */
@Getter
public class RemNumShiftListWork {

	/**
	 * 休暇残数シフトのリスト
	 */
	private List<RemNumShiftWork> remNumShiftWorkListOpt;

	/**
	 * 消化できなかった休暇使用数
	 */
	private LeaveUsedNumber unusedNumber;

	public RemNumShiftListWork() {
		remNumShiftWorkListOpt = new ArrayList<RemNumShiftWork>();
		unusedNumber = new LeaveUsedNumber();
	}

	/**
	 * 休暇付与残数データを追加する
	 * 
	 * @param leaveGrantRemainingData
	 *            休暇付与残数データ
	 */
	public void AddLeaveGrantRemainingData(LeaveGrantRemainingData leaveGrantRemainingData) {

		if (remNumShiftWorkListOpt.isEmpty()) { // リストが存在しないとき
			// 作成
			remNumShiftWorkListOpt = new ArrayList<RemNumShiftWork>();
		}

		// 休暇残数シフトのオブジェクトを作成
		RemNumShiftWork remNumShiftWork = new RemNumShiftWork(leaveGrantRemainingData);

		// リストへ追加
		remNumShiftWorkListOpt.add(remNumShiftWork);
	}

	/**
	 * 合計残数を取得する
	 * 
	 * @return
	 */
	public Optional<LeaveRemainingNumber> GetTotalRemNum() {
		if (remNumShiftWorkListOpt.isEmpty()) { // リストが存在しないとき
			return Optional.empty();
		}

		// 残数計算用
		LeaveRemainingNumber totalLeaveRemainingNumber = new LeaveRemainingNumber();

		List<RemNumShiftWork> list = remNumShiftWorkListOpt;

		for (RemNumShiftWork aRemNumShiftWork : list) {
			if (aRemNumShiftWork.getLeaveRemainingNumber().isPresent()) {
				totalLeaveRemainingNumber.add(aRemNumShiftWork.getLeaveRemainingNumber().get());
			}
		}

		return Optional.of(totalLeaveRemainingNumber);
	}

	/**
	 * 休暇使用数を消化できるかチェックする
	 * 
	 * @param repositoriesRequiredByRemNum
	 *            残数処理 キャッシュクラス
	 * @param leaveUsedNumber
	 *            休暇使用数
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param baseDate
	 *            基準日
	 */
	public boolean canDigest(LeaveRemainingNumber.RequireM3 require, LeaveUsedNumber leaveUsedNumber, String companyId,
			String employeeId, GeneralDate baseDate) {

		// 一時変数「休暇残数合計」の変数を作成し、合計残数を取得してセット
		Optional<LeaveRemainingNumber> leaveRemainingNumberOpt = this.GetTotalRemNum();

		// 合計残数が取得できないとき
		if (!leaveRemainingNumberOpt.isPresent()) {
			return false;
		}

		// 一時変数「休暇残数合計」に対して、休暇使用数を消化する
		// 結果をメンバー変数へセット（メンバ変数.消化できなかった休暇使用数← 消化できなかった休暇使用数）
		unusedNumber = leaveRemainingNumberOpt.get().digestLeaveUsedNumber(require, leaveUsedNumber, companyId,
				employeeId, baseDate);

		if (unusedNumber.isZero()) { // 消化できなかった休暇使用数が０のとき
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 休暇使用数を消化する
	 * 
	 * @param repositoriesRequiredByRemNum
	 *            ロードデータ
	 * @param leaveUsedNumber
	 *            休暇使用数
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param baseDate
	 *            基準日
	 */
	public void digest(LeaveRemainingNumber.RequireM3 require, LeaveUsedNumber leaveUsedNumber, String companyId,
			String employeeId, GeneralDate baseDate) {

		// 一時変数「休暇残数合計」の変数を作成し、合計残数を取得してセット
		Optional<LeaveRemainingNumber> leaveRemainingNumberOpt = this.GetTotalRemNum();

		// 合計残数が取得できないとき
		if (!leaveRemainingNumberOpt.isPresent()) {
			// 終了
			if (leaveUsedNumber.isLargerThanZero())
				this.unusedNumber = leaveUsedNumber.clone();
			return;
		}

		// 一時変数「休暇残数合計」に対して、休暇使用数を消化する
		// 結果をメンバー変数へセット（メンバ変数.消化できなかった休暇使用数← 消化できなかった休暇使用数）
		unusedNumber = leaveRemainingNumberOpt.get().digestLeaveUsedNumber(require, leaveUsedNumber, companyId,
				employeeId, baseDate);

		// 休暇残数をすべて消化する
		this.remNumShiftWorkListOpt.forEach(x -> {
			x.digestAll();
		});

		// リスト最後の「休暇残数シフトWORK」の残数へ、休暇残数合計の内容をセットする
		int size = remNumShiftWorkListOpt.size();
		RemNumShiftWork remNumShiftWorkLast = remNumShiftWorkListOpt.get(size - 1);
		if (remNumShiftWorkLast.getRefLeaveGrantRemainingData() != null) {
			remNumShiftWorkLast.getRefLeaveGrantRemainingData().getDetails()
					.setRemainingNumber(require, companyId, employeeId, baseDate,leaveRemainingNumberOpt.get());
		}
	}

	public LeaveNumberInfo toDetails() {

		LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();

		// 付与残数ダミーデータ.使用数.日数
		// ←休暇残数シフトリストWORK.処理できなかった休暇使用数.日数
		// 付与残数ダミーデータ.使用数.時間
		// ←休暇残数シフトリストWORK.処理できなかった休暇使用数.時間
		LeaveUsedNumber leaveUsedNumberTmp = new LeaveUsedNumber();
		leaveUsedNumberTmp.setDays(getUnusedNumber().getDays());
		leaveUsedNumberTmp.setMinutes(getUnusedNumber().getMinutes());
		leaveNumberInfo.setUsedNumber(leaveUsedNumberTmp);

		// 付与残数ダミーデータ.残数.日数
		// ← 付与残数ダミーデータ.使用数.日数＊ー１
		// 付与残数ダミーデータ.残数.時間
		// ← 付与残数ダミーデータ.使用数.時間＊ー１
		LeaveRemainingNumber leaveRemainingNumberTmp = new LeaveRemainingNumber();
		leaveRemainingNumberTmp.setDays(new LeaveRemainingDayNumber(getUnusedNumber().getDays().v() * -1));
		if (getUnusedNumber().getMinutes().isPresent()) {
			leaveRemainingNumberTmp
					.setMinutes(Optional.of(new LeaveRemainingTime(getUnusedNumber().getMinutes().get().v() * -1)));
		}
		leaveNumberInfo.setRemainingNumber(leaveRemainingNumberTmp);

		return leaveNumberInfo;
	}
}
