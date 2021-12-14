package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import javax.ejb.Stateless;

@Stateless
public interface CreateFlowMenuFileService {

	/**
	 * Perform copy an uploaded file
	 * @param fileId fileId of the file to be copied
	 * @return fileId of the copied file
	 */
	String copyFile(String fileId);

	/**
	 * Delete the layout fileId and all the uploaded files
	 * @param layout
	 */
	void deleteUploadedFiles(FlowMenuLayout layout);
	
	/**
	 * Delete the layout fileId only
	 * @param layout
	 */
	void deleteLayout(FlowMenuLayout layout);
}
