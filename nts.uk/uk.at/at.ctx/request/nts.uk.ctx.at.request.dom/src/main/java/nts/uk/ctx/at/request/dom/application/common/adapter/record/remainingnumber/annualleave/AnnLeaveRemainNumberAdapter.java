package nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave;

import nts.arc.time.GeneralDate;

/**
 * @author sonnlb
 *
 */
public interface AnnLeaveRemainNumberAdapter {
    
    /**
     * RequestList198
     * 基準日時点の年休残数を取得する
     * @param employeeID
     * @param date
     * @return ReNumAnnLeaveImport
     */
    ReNumAnnLeaveImport getReferDateAnnualLeaveRemain(String employeeID, GeneralDate date);

	/**
	 * RequestList198
	 * 基準日時点の年休残数を取得する
	 * @param employeeID
	 * @param date
	 * @return ReNumAnnLeaReferenceDateExport
	 */
	ReNumAnnLeaReferenceDateImport getReferDateAnnualLeaveRemainNumber(String employeeID,GeneralDate date);
}
