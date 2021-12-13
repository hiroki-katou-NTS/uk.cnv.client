/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.screen.at.app.ksu001.processcommon.GetListWorkTypeAvailable;
import nts.uk.screen.at.app.ksu001.scheduleactualworkinfo.GetScheduleActualOfWorkInfo;
import nts.uk.screen.at.app.ksu001.scheduleactualworkinfo.ScheduleActualOfWorkOutput;

/**
 * @author laitv 
 * ScreenQuery 勤務情報で表示する
 */
@Stateless
public class DisplayInWorkInformation {

	@Inject
	private GetScheduleActualOfWorkInfo getScheduleActualOfWorkInfo;
	@Inject
	private GetListWorkTypeAvailable getListWorkTypeAvailable;
	
	public DisplayInWorkInfoResult getDataWorkInfo(DisplayInWorkInfoParam_New param) {
		// 1: <call>
		List<WorkTypeInfomation> listWorkTypeInfo = getListWorkTypeAvailable.getData();
		
		// 2: <call>
		ScheduleActualOfWorkOutput scheduleActualOfWorkOutput = 
				
			getScheduleActualOfWorkInfo.getDataScheduleAndAactualOfWorkInfo(
					param.getListSid(),
					new DatePeriod(param.getStartDate(), param.getEndDate()),
					param.getDay(),
					param.getActualData,
					param.getTargetOrgIdenInforDto().convertFromDomain(),
					Optional.ofNullable(param.getPersonalCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, PersonalCounterCategory.class))),
					Optional.ofNullable(param.getWorkplaceCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, WorkplaceCounterCategory.class)))
					);
		
		return new DisplayInWorkInfoResult(
				listWorkTypeInfo,
				scheduleActualOfWorkOutput.getWorkScheduleWorkInforDtos(),
				scheduleActualOfWorkOutput.getAggreratePersonal(),
				scheduleActualOfWorkOutput.getAggrerateWorkplace()
				);
		
		
	}
}
