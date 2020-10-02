package nts.uk.ctx.at.record.pub.monthly.vacation.childcarenurse.childcare;

import java.util.List;
import java.util.Optional;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.AggrResultOfChildCareNurse;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.TmpChildCareNurseMngWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;


/**
 * 期間中の子の看護休暇残数を取得
 * @author yuri_tamakoshi
 */

public interface  GetRemainingNumberChildCareNurse {

		/**
		 * 期間中の子の看護休暇残数を取得
		 * @param employeeId 社員ID
		 * @param period 集計期間
		 * @param performReferenceAtr 実績のみ参照区分(月次モード orその他)
		 * @param criteriaDate 基準日
		 * @param isOverWrite 上書きフラグ(Optional)
		 * @param tempChildCareDataforOverWriteList List<上書き用の暫定管理データ>(Optional)
		 * @param prevChildCareLeave 前回の子の看護休暇の集計結果<Optional>
		 * @param createAtr 作成元区分(Optional)
		 * @param periodOverWrite 上書き対象期間(Optional)
		 * @return 子の看護介護休暇集計結果
		 */
		 // RequestList206
		List<ChildCareNursePeriodExport> algorithm(String employeeId,DatePeriod period,
				InterimRemainMngMode performReferenceAtr,
				GeneralDate criteriaDate,
				Optional<Boolean> isOverWrite,
				Optional<List<TmpChildCareNurseMngWork>> tempChildCareDataforOverWriteList,
				Optional<AggrResultOfChildCareNurse> prevChildCareLeave,
				Optional<CreateAtr> createAtr,
				Optional<GeneralDate> periodOverWrite);



		/**
		 *暫定子の看護管理データを取得（社員ID: 社員ID, 期間: 期間, 上書きフラグ: boolean, 上書き暫定管理データ: 暫定子の看護管理データ, 実績のみ参照区分: boolean, 作成元区分: 作成元区分, 対象期間: 期間）
		 * @param employeeId 社員ID
		 * @param period 期間
		 * @param 上書きフラグ
		 * @param 上書き暫定管理データ
		 * @param 実績のみ参照区分
		 * @param 作成元区分
		 * @param 対象期間
		 * @return  期間中の子の看護休暇残数を取得
		 */
		/**List<ChildCareNursePeriodExport> algorithm(String employeeId, YearMonthPeriod period);
		 * */
}
