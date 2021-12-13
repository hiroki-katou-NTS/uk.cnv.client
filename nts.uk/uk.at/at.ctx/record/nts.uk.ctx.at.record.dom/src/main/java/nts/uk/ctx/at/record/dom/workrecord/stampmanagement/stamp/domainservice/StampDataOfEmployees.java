package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.List;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * 社員の打刻データ
 * @author tutk
 *
 */
public class StampDataOfEmployees {
	/**
	 * 社員ID
	 */
	@Getter
	private final String employeeId;
	
	/**
	 * 年月日
	 */
	@Getter
	private final GeneralDate date;
	
	
	/**
	 * 打刻リスト
	 */
	@Getter
	private final List<Stamp> listStamp;

	public StampDataOfEmployees(String employeeId, GeneralDate date, List<Stamp> listStamp) {
		super();
		this.employeeId = employeeId;
		this.date = date;
		this.listStamp = listStamp;
	}
}
