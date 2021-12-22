/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeDomainObject;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * The Class TimeZone.
 */
// 時間帯
@Getter
@Setter
public class TimeZone extends WorkTimeDomainObject implements Cloneable{

	/** The start. */
	// 開始
	protected TimeWithDayAttr start;

	/** The end. */
	// 終了
	protected TimeWithDayAttr end;

	
	/**
	 * Instantiates a new time zone.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public TimeZone(TimeWithDayAttr start, TimeWithDayAttr end) {
		super();
		this.start = start;
		this.end = end;
		if(start == null && end == null) {
			this.start = new TimeWithDayAttr(0);
			this.end = new TimeWithDayAttr(0);
		}
		else if(start == null){
			this.start = this.end;
		}
		else if(end == null) {
			this.end = this.start;
		}
	}

	/**
	 * Instantiates a new time zone.
	 */
	public TimeZone() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.dom.DomainObject#validate()
	 */
	public void validateRange(String param) {
		if (this.start.greaterThan(this.end)) {
			this.bundledBusinessExceptions.addMessage("Msg_770", param);
		}
	}

	/**
	 * Checks if is overlap.
	 *
	 * @param timezone
	 *            the timezone
	 * @return true, if is overlap
	 */
	public boolean isOverlap(TimeZone timezone) {
		return !(this.end.lessThanOrEqualTo(timezone.getStart())
				|| this.start.greaterThanOrEqualTo(timezone.getEnd()));
	}
	
	/**
	 * Checks if is start less than.
	 *
	 * @param timezone
	 *            the timezone
	 * @return true, if is start less than
	 */
	public boolean isStartLessThan(TimeZone timezone) {
		return this.start.lessThan(timezone.getStart());
	}

	/**
	 * Checks if is between or equal.
	 *
	 * @param timezone
	 *            the timezone
	 * @return true, if is between or equal
	 */
	public boolean isBetweenOrEqual(TimeZone timezone) {
		return this.start.greaterThanOrEqualTo(timezone.getStart())
				&& this.end.lessThanOrEqualTo(timezone.getEnd());
	}

	/**
	 * Consist of.
	 *
	 * @param time
	 *            the time
	 * @return true, if successful
	 */
	public boolean consistOf(TimeWithDayAttr time) {
		return time.greaterThanOrEqualTo(this.start) && time.lessThanOrEqualTo(this.end);
	}
	
	/**
	 * Convert Time Span
	 * @return timeSpanForCalc
	 */
	public TimeSpanForCalc timeSpan() {
		return new TimeSpanForCalc(this.start,this.end);
	}

	/**
	 * 指定時刻が時間帯に含まれているか判断する Returns true if a given time is contained by this time
	 * span.
	 * 
	 * @param time
	 *            指定時刻
	 * @return 含まれていればtrue
	 */
	public boolean contains(TimeWithDayAttr time) {
		return this.start.lessThanOrEqualTo(time) && this.end.greaterThanOrEqualTo(time);
	}
	
	/**
	 * 重複している時間帯を返す
	 * @param other 比較対象
	 * @return 重複部分
	 */
	public Optional<TimeSpanForCalc> getDuplicatedWith(TimeSpanForCalc other) {
		TimeSpanForCalc result;
		
		if (this.timeSpan().contains(other)) {
			result = other;
		} else if (other.getSpan().contains(this.timeSpan())) {
			result = this.timeSpan();
		} else if (this.contains(other.getStart())) {
			result = new TimeSpanForCalc(other.getStart(), this.end);
		} else if (this.contains(other.getEnd())) {
			result = new TimeSpanForCalc(this.start, other.getEnd());
		} else {
			return Optional.empty();
		}
		
		if (result.lengthAsMinutes() == 0) {
			return Optional.empty();
		}
		
		return Optional.of(result);
	}
	
	/**
	 * Update start.
	 *
	 * @param start the start
	 */
	public void updateStart(TimeWithDayAttr start) {
		this.start = start;
	}

	/**
	 * Update end.
	 *
	 * @param end the end
	 */
	public void updateEnd(TimeWithDayAttr end) {
		this.end = end;
	}
	
	@Override
	public TimeZone clone() {
		TimeZone cloned = new TimeZone();
		try {
			cloned.start = new TimeWithDayAttr(this.start.valueAsMinutes());
			cloned.end = new TimeWithDayAttr(this.end.valueAsMinutes());
		}
		catch (Exception e){
			throw new RuntimeException("TimeZone clone error.");
		}
		return cloned;
	}
}
