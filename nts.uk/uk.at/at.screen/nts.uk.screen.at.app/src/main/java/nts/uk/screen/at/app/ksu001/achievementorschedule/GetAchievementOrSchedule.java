package nts.uk.screen.at.app.ksu001.achievementorschedule;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.FuncCtrlDisplayFormatDto;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.GetScheduleActualOfShift;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.SchedulesbyShiftDataResult;
import nts.uk.screen.at.app.ksu001.scheduleactualworkinfo.GetScheduleActualOfWorkInfo;
import nts.uk.screen.at.app.ksu001.scheduleactualworkinfo.ScheduleActualOfWorkOutput;

/**
 * 実績表示・予定表示を切り替える
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */

@Stateless
public class GetAchievementOrSchedule {
	
	@Inject
	private GetScheduleActualOfShift getScheduleActualOfShift;
	
	@Inject
	private GetScheduleActualOfWorkInfo getScheduleActualOfWorkInfo;
	
	public AchievementOrScheduleDto getAchievementOrSchedule(AchievementOrScheduleParam param) {
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		if (param.unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,
					Optional.of(param.workplaceId),
					Optional.empty());
		} else {
			targetOrgIdenInfor = new TargetOrgIdenInfor(
					TargetOrganizationUnit.WORKPLACE_GROUP,
					Optional.empty(),
					Optional.of(param.workplaceGroupId));
		}
		// Aa:シフト表示の場合
		if (param.getMode() == FuncCtrlDisplayFormatDto.Shift.value) {
			SchedulesbyShiftDataResult schedulesbyShiftDataResult_New =
					getScheduleActualOfShift.getData(
							param.getListShiftMasterNotNeedGetNew(),
							param.getSids(),
							new DatePeriod(param.getStartDate(), param.getEndDate()),
							DateInMonth.of(param.getDay()),
							param.getActualData,
							targetOrgIdenInfor,
							Optional.ofNullable(param.getPersonalCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, PersonalCounterCategory.class))),
							Optional.ofNullable(param.getWorkplaceCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, WorkplaceCounterCategory.class)))
							);
			
			return AchievementOrScheduleDto.builder()
						.listWorkScheduleShift(schedulesbyShiftDataResult_New.getListWorkScheduleShift())
						.mapShiftMasterWithWorkStyle(schedulesbyShiftDataResult_New.getMapShiftMasterWithWorkStyle())
						.aggreratePersonal(schedulesbyShiftDataResult_New.getAggreratePersonal())
						.aggrerateWorkplace(schedulesbyShiftDataResult_New.getAggrerateWorkplace())
						.build();
			
		} else if (param.getMode() == FuncCtrlDisplayFormatDto.WorkInfo.value 
				|| param.getMode() == FuncCtrlDisplayFormatDto.AbbreviatedName.value) {
			
			ScheduleActualOfWorkOutput scheduleActualOfWorkOutput =
					getScheduleActualOfWorkInfo.getDataScheduleAndAactualOfWorkInfo(
							param.getSids(),
							new DatePeriod(param.getStartDate(), param.getEndDate()),
							DateInMonth.of(param.getDay()),
							param.getActualData,
							targetOrgIdenInfor,
							Optional.ofNullable(param.getPersonalCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, PersonalCounterCategory.class))),
							Optional.ofNullable(param.getWorkplaceCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, WorkplaceCounterCategory.class)))
					
					);
			
			return AchievementOrScheduleDto.builder()
						.workScheduleWorkInforDtos(scheduleActualOfWorkOutput.getWorkScheduleWorkInforDtos())
						.aggreratePersonal(scheduleActualOfWorkOutput.getAggreratePersonal())
						.aggrerateWorkplace(scheduleActualOfWorkOutput.getAggrerateWorkplace())
						.build();
		}
		
		return new AchievementOrScheduleDto();
	}

}
