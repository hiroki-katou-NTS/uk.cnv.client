package nts.uk.ctx.at.shared.dom.employmentrule.hourlate.overtime.overtimeframe;

import java.util.List;

public interface OvertimeFrameRepository {
	List<OvertimeFrame> getOvertimeFrameByFrameNo(List<Integer> frameNo);
	List<OvertimeFrame> getOvertimeFrameByCID(String companyID,int useAtr);
}
