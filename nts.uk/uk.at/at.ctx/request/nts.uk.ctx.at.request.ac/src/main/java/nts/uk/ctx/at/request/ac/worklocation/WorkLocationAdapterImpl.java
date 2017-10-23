package nts.uk.ctx.at.request.ac.worklocation;

import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.pub.worklocation.WorkLocationPub;
import nts.uk.ctx.at.record.pub.worklocation.WorkLocationPubExport;
import nts.uk.ctx.at.request.dom.applicationapproval.application.gobackdirectly.adapter.WorkLocationAdapter;
import nts.uk.ctx.at.request.dom.applicationapproval.application.gobackdirectly.adapter.dto.WorkLocationImport;
/**
 * 
 * @author ducpm
 *
 */
@Stateless
public class WorkLocationAdapterImpl implements WorkLocationAdapter {

	@Inject
	WorkLocationPub workLocationPub;

	/**
	 * 
	 */
	@Override
	public WorkLocationImport getByWorkLocationCD(String companyID, String workLocationCD) {
		WorkLocationPubExport workLocation = workLocationPub.getLocationName(companyID, workLocationCD);
		if (Objects.isNull(workLocation)) {
			return null;
		}
		return toImport(workLocation);
	}

	/**
	 * 
	 * @param export
	 * @return
	 */
	public WorkLocationImport toImport(WorkLocationPubExport export) {
		return new WorkLocationImport(export.getWorkLocationCD(), 
				export.getWorkLocationName(),
				export.getHoriDistance(), 
				export.getVertiDistance(), 
				export.getLatitude(), 
				export.getLongitude());
	}
}
