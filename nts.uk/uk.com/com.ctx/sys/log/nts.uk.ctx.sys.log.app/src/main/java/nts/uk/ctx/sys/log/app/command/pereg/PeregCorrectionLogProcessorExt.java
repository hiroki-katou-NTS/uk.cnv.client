package nts.uk.ctx.sys.log.app.command.pereg;

import javax.ejb.Stateless;

import nts.uk.shr.com.security.audittrail.correction.processor.CorrectionProcessorId;
import nts.uk.shr.com.security.audittrail.correction.processor.pereg.PeregCorrectionLogProcessor;
import nts.uk.shr.com.security.audittrail.correction.processor.pereg.PeregCorrectionLogProcessorContext;

@Stateless
public class PeregCorrectionLogProcessorExt extends PeregCorrectionLogProcessor {

	@Override
	public CorrectionProcessorId getId() {
		return CorrectionProcessorId.PEREG_REGISTER;
	}

	@Override
	protected void buildLogContents(PeregCorrectionLogProcessorContext context) {
		// xử lý PeregCorrectionLogParameter để chuyển thành domain
		// PersonInfoCorrectionLog ở đây
	}
}
