package nts.uk.screen.at.app.kdw013.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicateStateAtr;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicationStatusOfTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.RangeOfDayTimeZoneService;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.共通処理.作業時間を計算する <<ScreenQuery>>
 * 作業時間を計算する
 * 
 * @author tutt
 */
@Stateless
public class CalculateWorktime {

	@Inject
	private RangeOfDayTimeZoneService timezoneService;

	/**
	 * 
	 * @param refTimezone 基準時間帯
	 * @param goOutBreakTimeLst 外出休憩時間帯
	 * @return
	 */
	public int calculateWorktime(TimeSpanForCalc refTimezone, List<TimeSpanForCalc> goOutBreakTimeLst) {

		// $控除時間帯
		List<TimeSpanForCalc> deductionTimezones = new ArrayList<>();

		// Loop「外出休憩時間帯」
		for (TimeSpanForCalc time : goOutBreakTimeLst) {

			// 1: <call>() 重複の判断処理
			// 基準となる時間帯
			TimeSpanForCalc destination = refTimezone;

			// 比較したい時間帯
			TimeSpanForCalc source = time;

			// 状態区分
			DuplicateStateAtr state = timezoneService.checkPeriodDuplication(destination, source);
			DuplicationStatusOfTimeZone status = timezoneService.checkStateAtr(state);

			// 1.1 <call>() 取得した「状態区分」によって$控除時間帯に追加する
			switch (status) {

			/** 同じ時間帯 */
			case SAME_WORK_TIME:

				// $控除時間帯.add(処理中の計算用時間帯)
				deductionTimezones.add(source);
				break;

			/** 開始を跨いでいる */
			case BEYOND_THE_START:

				// $追加時間帯．開始時刻 = 処理中の計算用時間帯．開始時刻
				// $追加時間帯．終了時刻 = INPUT「基準時間帯．終了時刻」
				// $控除時間帯.add($追加時間帯)
				deductionTimezones.add(new TimeSpanForCalc(new TimeWithDayAttr(time.getStart().v()),
						new TimeWithDayAttr(refTimezone.getEnd().v())));
				break;

			/** 終了を跨いでいる */
			case BEYOND_THE_END:

				// $追加時間帯．開始時刻 = INPUT「基準時間帯．開始時刻」
				// $追加時間帯．終了時刻 = 処理中の計算用時間帯．終了時刻
				// $控除時間帯.add($追加時間帯)
				deductionTimezones.add(new TimeSpanForCalc(new TimeWithDayAttr(refTimezone.getStart().v()),
						new TimeWithDayAttr(time.getEnd().v())));
				break;

			/** 包含(自分が内側) */
			case INCLUSION_OUTSIDE:

				// $控除時間帯.add(INPUT「基準時間帯」)
				deductionTimezones.add(refTimezone);
				break;

			/** 包含(自分が外側) */
			case INCLUSION_INSIDE:

				// $控除時間帯.add(処理中の計算用時間帯)
				deductionTimezones.add(time);
				break;

			/** 非重複 */
			case NON_OVERLAPPING:

				// 追加しない
				break;
			}

		}

		// 作業時間を計算する
		// ・基準時間 = INPUT「基準時間帯．終了時刻」- INPUT「基準時間帯．開始時刻」
		int refTime = refTimezone.getEnd().v() - refTimezone.getStart().v();

		// ・控除時間リスト = $追加時間帯：map $．終了時刻 - $.開始時刻
		List<Integer> deductionTimes = deductionTimezones.stream().map(m -> m.getEnd().v() - m.getStart().v())
				.collect(Collectors.toList());

		Integer deductionTimeSum = deductionTimes.stream().collect(Collectors.summingInt(Integer::intValue));

		// ⇒作業時間 = 基準時間 - 「控除時間リスト」の合計
		// ※計算結果が0より小さい場合0とする
		int taskTime = refTime - deductionTimeSum;

		return taskTime < 0 ? 0 : taskTime;

	}

}
