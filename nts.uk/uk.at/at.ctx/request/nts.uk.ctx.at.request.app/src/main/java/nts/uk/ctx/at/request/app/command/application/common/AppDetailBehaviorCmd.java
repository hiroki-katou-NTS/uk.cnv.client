package nts.uk.ctx.at.request.app.command.application.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AppDetailBehaviorCmd {
	
	private String memo;
	
	private AppDispInfoStartupDto appDispInfoStartupOutput;
	
	private HdsubRecLinkDataDto hdsubRecLinkData;
	
}
