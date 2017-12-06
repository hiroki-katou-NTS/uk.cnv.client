/**
 * 
 */
package nts.uk.ctx.bs.employee.dom.temporaryabsence.state;

import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHisItem;

/**
 * @author danpv Domain Name : 休職
 *
 */
public class Leave extends TempAbsenceHisItem {

	/**
	 * @param historyId
	 * @param employeeId
	 * @param remarks
	 * @param soInsPayCategory
	 */
	private Leave(String historyId, String employeeId, GenericString remarks, Integer soInsPayCategory) {
		super(LeaveHolidayType.LEAVE_OF_ABSENCE, historyId, employeeId, remarks, soInsPayCategory);
	}

	public static Leave init(String historyId, String employeeId, String remarks, Integer soInsPayCategory) {
		return new Leave(historyId, employeeId, new GenericString(remarks), soInsPayCategory);
	}

}
