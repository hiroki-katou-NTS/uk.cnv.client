package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.ContinuousCount;

/**
 * 連続期間のカウントを計算
 *
 */
@Stateless
public class CalCountForConsecutivePeriodChecking {
	/**
	 * 連続期間のカウントを計算
	 * 
	 * @param count カウント
	 * @param continuousPeriod 連続期間
	 * @param errorAtr エラー発生区分
	 * @param ymd 年月日
	 * 
	 */
	public CalCountForConsecutivePeriodOutput getContinuousCount(int count, int continuousPeriod, boolean errorAtr) {
		Optional<ContinuousCount> optContinuousCount = Optional.empty();
		
		// エラー発生区分をチェック
		if (errorAtr) {
			count = count + 1;
			return new CalCountForConsecutivePeriodOutput(optContinuousCount, count);
		}
		
		// カウントと連続期間を比べる
		// カウント　＞＝　連続期間
		boolean isCount = count >= continuousPeriod;
		if (isCount) {
			// 連続年月日　＝　カウント
			optContinuousCount = Optional.of(new ContinuousCount(count));
		}
		
		count = 0;
		
		return new CalCountForConsecutivePeriodOutput(optContinuousCount, count);
	}
}
