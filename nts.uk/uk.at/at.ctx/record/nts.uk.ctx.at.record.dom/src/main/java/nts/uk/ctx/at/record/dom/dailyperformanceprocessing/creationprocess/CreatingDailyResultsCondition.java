package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.日別実績を作成する条件
 */
@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class CreatingDailyResultsCondition extends AggregateRoot {

	/**
	 * 会社ID
	 */
	private String cid;

	/**
	 * 未来日を作成する
	 */
	private NotUseAtr isCreatingFutureDay;

	/**
	 * [1] 日別実績を作成するか判断する
	 * 
	 * @param date 処理日
	 * @return 作成するかどうか
	 */
	public boolean isCreatingDailyResults(GeneralDate date) {
		if (this.isCreatingFutureDay.isUse()) {
			return true;
		}
		return date.beforeOrEquals(GeneralDate.today());
	}
	
	/**
	 * [2] 日別実績を作成する期間を補正する
	 * 
	 * @param date 処理日
	 * @return 作成するかどうか
	 */
	public Optional<DatePeriod> correctDailyCreatePeriod(DatePeriod period) {
		
		/** 未来日を作成するかを確認する */
		if (this.isCreatingFutureDay.isUse()) return Optional.of(period);
		
		/** 処理日＝パラメータ。実行期間。終了日 */
		GeneralDate processDate = period.end();
		while(processDate.afterOrEquals(period.start())) {
			
			/** 日別実績を作成するか判断する */
			if (isCreatingDailyResults(processDate)) 
				
				/** 実行期間を返す */
				return Optional.of(new DatePeriod(period.start(), processDate)); 
			
			/** 処理日＝処理日　-　１ */
			processDate = processDate.addDays(-1);
		}
		
		/** 実行期間を返す */
		return Optional.empty();
	}
}
