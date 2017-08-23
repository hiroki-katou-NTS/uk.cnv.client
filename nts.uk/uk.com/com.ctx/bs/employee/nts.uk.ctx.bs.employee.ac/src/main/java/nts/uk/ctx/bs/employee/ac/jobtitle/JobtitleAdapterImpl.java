/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.ac.jobtitle;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.company.pub.jobtitle.JobtitlePub;
import nts.uk.ctx.bs.employee.dom.access.jobtitle.JobTitleAdapter;
import nts.uk.ctx.bs.employee.dom.access.jobtitle.dto.AcJobTitleDto;

/**
 * The Class JobtitleAdapterImpl.
 */
@Stateless
public class JobtitleAdapterImpl implements JobTitleAdapter {

	/** The jobtitle pub. */
	@Inject
	private JobtitlePub jobtitlePub;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.dom.access.jobtitle.JobTitleAdapter#findAll(java.
	 * lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<AcJobTitleDto> findAll(String companyId, GeneralDate referenceDate) {
		return jobtitlePub.findAll(companyId, referenceDate).stream()
				.map(item -> new AcJobTitleDto(item.getCompanyId(), item.getPositionId(),
						item.getPositionCode(), item.getPositionName(), item.getSequenceCode(),
						item.getStartDate(), item.getEndDate()))
				.collect(Collectors.toList());
	}

}
