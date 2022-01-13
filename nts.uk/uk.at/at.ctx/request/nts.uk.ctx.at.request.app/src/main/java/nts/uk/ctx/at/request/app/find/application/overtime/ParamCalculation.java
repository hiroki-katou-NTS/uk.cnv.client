package nts.uk.ctx.at.request.app.find.application.overtime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.command.application.overtime.ApplicationTimeCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.WorkContentCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSetCommand;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.MultipleOvertimeContentDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ParamCalculation {
	
	public String companyId;
	
	public String employeeId;
	
	public String dateOp;

	public Integer overtimeAtr;
	
	public Integer prePostInitAtr;
	
	public OvertimeLeaveAppCommonSetCommand overtimeLeaveAppCommonSet;
	
	public ApplicationTimeCommand advanceApplicationTime;
	
	public ApplicationTimeCommand achieveApplicationTime;
	
	public WorkContentCommand workContent;
	
	public OvertimeAppSetCommand overtimeAppSetCommand;
	
	public Boolean agent;

	public List<MultipleOvertimeContentDto> multipleOvertimeContents;

	public AppDispInfoStartupDto appDispInfoStartupDto;
}
