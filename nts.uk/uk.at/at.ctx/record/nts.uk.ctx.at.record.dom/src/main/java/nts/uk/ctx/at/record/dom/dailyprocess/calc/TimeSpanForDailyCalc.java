package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanDuplication;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZoneRounding;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.TimeSpanForDailyCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 日別計算用時間帯
 * 
 *
 */
@Getter
public class TimeSpanForDailyCalc {
	
	/** 計算時間帯 */
	private TimeSpanForCalc timeSpan;
	
	/**
	 * @param start
	 * @param end
	 */
	public TimeSpanForDailyCalc(TimeWithDayAttr start, TimeWithDayAttr end) {
		this.timeSpan = new TimeSpanForCalc(start, end);
	}
	
	/**
	 * @param timeSpan
	 */
	public TimeSpanForDailyCalc(TimeSpanForCalc timeSpan) {
		this.timeSpan = timeSpan;
	}
	
	/**
	 * getStart to this timeSpan
	 * @return start
	 */
	public TimeWithDayAttr getStart() {
		return this.timeSpan.getStart();
	}
	
	/**
	 * getEnd to this timeSpan
	 * @return end
	 */
	public TimeWithDayAttr getEnd() {
		return this.timeSpan.getEnd();
	}
	
	/**
	 * 開始から終了までの時間を分の整数で返す Returns time as minutes from start to end.
	 * 
	 * @return 分の整数
	 */
	public int lengthAsMinutes() {
		return this.timeSpan.lengthAsMinutes();
	}
	
	/**
	 * @param TimeZoneRounding
	 * @return TimeSpanForDailyCalc
	 */
	public static TimeSpanForDailyCalc toTimeSpanForDailyCalc(TimeZoneRounding timeZoneRounding) {
		return new TimeSpanForDailyCalc(timeZoneRounding.getStart(), timeZoneRounding.getEnd());
	}
	
	/**
	 * @param TimeSpanForDailyCalc
	 * @return TimeSpanForDailyCalc
	 */
	public static TimeSpanForDailyCalc toTimeSpanForDailyCalc(TimeSpanForDailyCalc forCalc) {
		return new TimeSpanForDailyCalc(forCalc.getStart(), forCalc.getEnd());
	}
	
	/**
	 * 指定時刻が時間帯に含まれているか判断する Returns true if a given time is contained by this time
	 * span.
	 * 
	 * @param time 指定時刻
	 * @return 含まれていればtrue
	 */
	public boolean contains(TimeWithDayAttr time) {
		this.timeSpan.contains(this.timeSpan);
		return this.timeSpan.contains(time);
	}
	
	/**
	 * 開始時刻と終了時刻を戻す（前にズラす）
	 * 
	 * @param minutesToShiftBack 移動させる時間（分）
	 * @return ズラした後の開始時刻と終了時刻
	 */
	public TimeSpanForDailyCalc shiftBack(int minutesToShiftBack) {
		return new TimeSpanForDailyCalc(this.timeSpan.shiftBack(minutesToShiftBack));
	}

	/**
	 * 開始時刻と終了時刻を進める（後ろにズラす）
	 * 
	 * @param minutesToMoveBack　移動させる時間（分）
	 * @return ズラした後の開始時刻と終了時刻
	 */
	public TimeSpanForDailyCalc shiftAhead(int minutesToShiftAhead) {
		return new TimeSpanForDailyCalc(this.timeSpan.shiftAhead(minutesToShiftAhead));
	}
	
	/**
	 * 終了時刻だけを進める（後ろにずらす）
	 * @param minutesToShiftAhead
	 * @return
	 */
	public TimeSpanForDailyCalc shiftEndAhead(int minutesToShiftAhead) {
		return new TimeSpanForDailyCalc(this.timeSpan.shiftEndAhead(minutesToShiftAhead));
	}
	
	/**
	 * 比較元と比較したい時間帯の位置関係を判定する
	 * @param other 比較したい時間帯
	 * @return　重複状態区分
	 */
	public TimeSpanDuplication checkDuplication(TimeSpanForDailyCalc other) {
		return TimeSpanDuplication.createFrom(this.getTimeSpan().compare(other.getTimeSpan()));
	}
	
	/**
	 * 比較元と比較対象が接している状態か判定する
	 * @param other　比較したい時間帯
	 * @return　接している
	 */
	public boolean isContinus(TimeSpanForDailyCalc other) {
		val result = this.timeSpan.compare(other.getTimeSpan());
		return result.isContinuousAfterBase() || result.isContinuousBeforeBase();
	}
	
	/**
	 * 開始時刻のみ指定した時刻に移動する
	 * @param newStart 新しい開始時刻
	 * @return
	 */
	public TimeSpanForDailyCalc shiftOnlyStart(TimeWithDayAttr newStart) {
		return new TimeSpanForDailyCalc(this.timeSpan.shiftOnlyStart(newStart));
	}
	
	/**
	 * 終了時刻のみ指定した時刻に移動する
	 * @param newEnd
	 * @return
	 */
	public TimeSpanForDailyCalc shiftOnlyEnd(TimeWithDayAttr newEnd) {
		return new TimeSpanForDailyCalc(this.timeSpan.shiftOnlyEnd(newEnd));
	}
	
	/**
	 * 重複している時間帯を返す
	 * @param other 比較対象
	 * @return 重複部分
	 */
	public Optional<TimeSpanForDailyCalc> getDuplicatedWith(TimeSpanForDailyCalc other) {
		Optional<TimeSpanForCalc> forCalc =  this.timeSpan.getDuplicatedWith(other.getTimeSpan());
		if(!forCalc.isPresent()) return Optional.empty();
		return Optional.of(new TimeSpanForDailyCalc(forCalc.get()));
	}
	
	/**
	 * 基準の時間帯で比較対象の時間帯に重複していない部分を取得する
	 * @param other 比較対象
	 * @return 重複してない部分
	 */
	public List<TimeSpanForDailyCalc> getNotDuplicationWith(TimeSpanForDailyCalc other) {
		return this.getTimeSpan().getNotDuplicationWith(other.getTimeSpan()).stream()
				.map(forCalc -> new TimeSpanForDailyCalc(forCalc))
				.collect(Collectors.toList());
	}
	
	/**
	 *　重複している時間分時間帯をずらす
	 * @param other 比較したい時間帯
	 * @return 時間帯の重複状況
	 */
	public TimeSpanForDailyCalc reviseToAvoidDuplicatingWith(TimeSpanForDailyCalc other) {
		return new TimeSpanForDailyCalc(this.getTimeSpan().reviseToAvoidDuplicatingWith(other.getTimeSpan()));
	}
	
	/**
	 * 控除時間帯分、終了時刻をズラす
	 * @param timeSpan 時間帯
	 * @param timeSheetOfDeductionItems 控除項目の時間帯(List)
	 * @return ズラした後の指定時刻
	 */
	public TimeWithDayAttr forwardByDeductionTime(List<TimeSheetOfDeductionItem> timeSheetOfDeductionItems) {
		//重複している時間帯
		List<TimeSpanForDailyCalc> overlapptingTimes = timeSheetOfDeductionItems.stream()
				.filter(item -> item.getTimeSheet().getDuplicatedWith(this).isPresent())
				.map(item -> item.getTimeSheet().getDuplicatedWith(this).get())
				.collect(Collectors.toList());
		
		//控除時間分、終了時刻を遅くする
		return this.getTimeSpan().getEnd().forwardByMinutes(overlapptingTimes.stream()
				.mapToInt(time -> time.getTimeSpan().lengthAsMinutes())
				.sum());
	}
}
