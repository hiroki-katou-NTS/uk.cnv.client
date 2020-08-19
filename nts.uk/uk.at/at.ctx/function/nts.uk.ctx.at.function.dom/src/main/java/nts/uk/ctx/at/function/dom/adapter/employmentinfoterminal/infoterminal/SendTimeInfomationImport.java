package nts.uk.ctx.at.function.dom.adapter.employmentinfoterminal.infoterminal;

import lombok.Value;

/**
 * @author ThanhNX
 *
 *         時刻情報Import
 */
@Value
public class SendTimeInfomationImport {

	/**
	 * Year
	 */
	private final Integer year;

	/**
	 * month
	 */
	private final Integer month;

	/**
	 * day
	 */
	private final Integer day;

	/**
	 * hour
	 */
	private final Integer hour;

	/**
	 * minute
	 */
	private final Integer minute;

	/**
	 * second
	 */
	private final Integer second;

	/**
	 * week
	 */
	private final Integer week;

	public SendTimeInfomationImport(Integer year, Integer month, Integer day, Integer hour, Integer minute,
			Integer second, Integer week) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.week = week;
	}

}
