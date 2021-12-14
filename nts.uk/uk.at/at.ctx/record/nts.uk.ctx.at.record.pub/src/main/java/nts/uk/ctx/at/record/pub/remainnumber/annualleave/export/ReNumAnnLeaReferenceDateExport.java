package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReNumAnnLeaReferenceDateExport {
	
	/**
	 * 年休残数(仮)
	 */
	private AnnualLeaveRemainingNumberExport annualLeaveRemainNumberExport;
	
	/**
	 * 年休付与情報(仮)
	 */
	private List<AnnualLeaveGrantExport> annualLeaveGrantExports;
	
	/**
	 * 年休管理情報(仮)
	 */
	private List<AnnualLeaveManageInforExport> annualLeaveManageInforExports;

}
