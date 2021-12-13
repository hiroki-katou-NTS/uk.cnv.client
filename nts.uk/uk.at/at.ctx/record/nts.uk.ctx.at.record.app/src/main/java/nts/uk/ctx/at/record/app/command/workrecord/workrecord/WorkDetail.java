package nts.uk.ctx.at.record.app.command.workrecord.workrecord;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.WorkDetailsParam;

/**
 * 年月日, List<作業詳細>
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class WorkDetail {
	
	/** 年月日 */
	private GeneralDate date;
	
	/** List<作業詳細> */
	private List<WorkDetailsParam> lstWorkDetailsParam;
	
}
