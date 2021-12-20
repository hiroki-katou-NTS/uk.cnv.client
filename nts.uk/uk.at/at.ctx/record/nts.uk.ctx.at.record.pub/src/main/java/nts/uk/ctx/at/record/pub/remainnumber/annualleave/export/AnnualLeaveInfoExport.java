package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * 年休情報Export
 * @author hayata_maekawa
 *
 */
@Data
@AllArgsConstructor
public class AnnualLeaveInfoExport {

	/**
	 * 年月日
	 */
	private GeneralDate ymd;
	
	/**
	 * 年休残数(マイナスあり)
	 */
	private AnnualLeaveRemainingNumberExport remainNumberWithMinusExport;
	
	/**
	 * 年休残数(マイナスなし)
	 */
	private AnnualLeaveRemainingNumberExport remainNumberNoMinusExport;
	
	/**
	 * 未消化日数
	 */
	private Double leaveUndigestDay;
	
	/**
	 * 未消化時間
	 */
	private Integer leaveUndigesttime;
	
	/**
	 * 付与残数データ
	 */
	private List<AnnualLeaveGrantExport> annualLeaveGrantExports;
	
	/**
	 * 上限データ
	 */
	private AnnualLeaveMaxDataExport annualLeaveMaxDataExport;
	
	/**
	 * 付与情報
	 */
	private AnnualLeaveGrantInfoExport annualLeaveGrantInfoExport;
	
	/**
	 * 使用日数
	 */
	private Double usedDays;
	
	/**
	 * 使用時間
	 */
	private Integer usedTime;
	
	
	public AnnualLeaveInfoExport(String sid){
		this.ymd =  GeneralDate.min();
		this.remainNumberWithMinusExport = new AnnualLeaveRemainingNumberExport();
		this.remainNumberNoMinusExport = new AnnualLeaveRemainingNumberExport();
		this.leaveUndigestDay = 0.00;
		this.leaveUndigesttime = 0;
		this.annualLeaveGrantExports = new ArrayList<>();
		this.annualLeaveMaxDataExport = new AnnualLeaveMaxDataExport(sid);
		this.annualLeaveGrantInfoExport = new AnnualLeaveGrantInfoExport();
		this.usedDays = 0.00;
		this.usedTime = 0;
	}
	
}
