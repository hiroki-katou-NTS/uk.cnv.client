/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.workflow.ac.workplace;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub;
import nts.uk.ctx.bs.employee.pub.workplace.WkpCdNameExport;
import nts.uk.ctx.workflow.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.workflow.dom.adapter.workplace.WorkplaceImport;

/**
 * The Class WorkplaceApproverAdaptorImpl.
 */
@Stateless
public class WorkplaceApproverAdaptorImpl implements WorkplaceAdapter {

	/** The wp pub. */
	@Inject
	private SyWorkplacePub wpPub;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.workflow.dom.approvermanagement.workroot.workplace.
	 * WorkplaceApproverAdaptor#findByWkpId(java.lang.String, java.lang.String,
	 * nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<WorkplaceImport> findByWkpId(String workplaceId, GeneralDate baseDate) {
		Optional<WkpCdNameExport> optWkpCdNameExport = this.wpPub.findByWkpId(workplaceId,
				baseDate);

		// Check exist
		if (!optWkpCdNameExport.isPresent()) {
			return Optional.empty();
		}

		// Return
		WkpCdNameExport x = optWkpCdNameExport.get();
		return Optional.of(new WorkplaceImport(x.getWkpCode(), x.getWkpName()));
	}

}
