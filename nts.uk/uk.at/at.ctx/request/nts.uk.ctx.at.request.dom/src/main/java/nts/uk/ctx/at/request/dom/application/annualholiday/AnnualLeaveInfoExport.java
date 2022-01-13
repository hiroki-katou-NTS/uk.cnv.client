package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 年休情報Export
 * @author phongtq
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveInfoExport {

	/**
	 * 年月日
	 */
	private GeneralDate ymd;
	
	/**
	 * 年休残数(マイナスあり)
	 */
	//private AnnualLeaveRemainingNumberExport remainNumberWithMinusExport;
	
	/**
	 * 年休残数(マイナスなし)
	 */
	private AnnualLeaveRemainingNumberExport remainNumberNoMinusExport;
	
	/**
	 * 未消化日数
	 */
	// private Double leaveUndigestDay;
	
	/**
	 * 未消化時間
	 */
	// private Integer leaveUndigesttime;
	
	/**
	 * 付与残数データ
	 */
	//private List<AnnualLeaveGrantExport> annualLeaveGrantExports;
	
	/**
	 * 上限データ
	 */
	private AnnualLeaveMaxDataExport annualLeaveMaxDataExport;
	
	/**
	 * 付与情報
	 */
	//private AnnualLeaveGrantInfoExport annualLeaveGrantInfoExport;
	
	/**
	 * 使用日数
	 */
	// private Double usedDays;
	
	/**
	 * 使用時間
	 */
	// private Integer usedTime;
}
