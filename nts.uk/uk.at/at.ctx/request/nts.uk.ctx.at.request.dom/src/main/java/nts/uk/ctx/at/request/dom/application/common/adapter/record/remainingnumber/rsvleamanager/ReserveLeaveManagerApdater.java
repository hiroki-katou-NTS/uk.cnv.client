package nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.rsvimport.RsvLeaManagerImport;

public interface ReserveLeaveManagerApdater {
	/**
	 * Import requestList201
	 * @param employeeID
	 * @param referDate
	 * @return
	 */
	Optional<RsvLeaManagerImport> getRsvLeaveManager(String employeeID, GeneralDate referDate);
}
