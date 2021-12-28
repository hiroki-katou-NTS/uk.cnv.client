package nts.uk.ctx.at.request.app.find.application.overtime;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.command.application.common.AppDispInfoStartupCmd;
import nts.uk.ctx.at.request.app.command.application.overtime.ApplicationTimeCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.WorkContentCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSetCommand;

/**
 * Refactor5
 * @author hoangnd
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class ParamOverTimeStart {
	
	public String companyId;
	
	public String dateOp;
	
	public Integer overtimeAppAtr;

	public Integer prePostAtr;

	public AppDispInfoStartupCmd appDispInfoStartupDto;
	
	public Integer startTimeSPR;
	
	public Integer endTimeSPR;
	
	public Boolean agent;
	
	public String employeeId;
	
	public Integer prePostInitAtr;
	
	public OvertimeLeaveAppCommonSetCommand overtimeLeaveAppCommonSet;
	
	public ApplicationTimeCommand advanceApplicationTime;
	
	public ApplicationTimeCommand achieveApplicationTime;
	
	public WorkContentCommand workContent;
	
	public List<String> sids;
	
}
