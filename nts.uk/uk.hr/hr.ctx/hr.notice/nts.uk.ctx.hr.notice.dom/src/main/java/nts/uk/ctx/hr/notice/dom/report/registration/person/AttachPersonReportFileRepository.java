package nts.uk.ctx.hr.notice.dom.report.registration.person;

import java.util.List;

public interface AttachPersonReportFileRepository {
	
	List<AttachmentPersonReportFile> getListDomainByReportId(String cid, String reprtId);
	
	void add(AttachmentPersonReportFile domain);
	
	void delete(String fileId , String cid);
	
	List<DocumentSampleDto> getListDomainByLayoutId(String cid, int reprtLayoutId, int reportID );

	
}
