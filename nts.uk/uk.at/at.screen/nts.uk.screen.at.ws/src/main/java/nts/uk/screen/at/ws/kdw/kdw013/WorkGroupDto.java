package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * 作業グループ
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
@Setter
public class WorkGroupDto {
	
	/** 作業CD1 */
	private String workCD1;
	
	/** 作業CD2 */
	private String workCD2;
	
	/** 作業CD3 */
	private String workCD3;
	
	/** 作業CD4 */
	private String workCD4;
	
	/** 作業CD5 */
	private String workCD5;
	
	public static WorkGroupDto toDto(WorkGroup workGroup) {
		
		return new WorkGroupDto(workGroup.getWorkCD1().v(), workGroup.getWorkCD2().map(m -> m.v()).orElse(null),
				workGroup.getWorkCD3().map(m -> m.v()).orElse(null),
				workGroup.getWorkCD4().map(m -> m.v()).orElse(null),
				workGroup.getWorkCD5().map(m -> m.v()).orElse(null)
				);
	}
}
