/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinshift;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.GetScheduleActualOfShift;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.SchedulesbyShiftDataResult;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPalette;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPaletteParam;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPaletteResult;

/**
 * @author laitv 
 * <<ScreenQuery>> シフトで表示する 
 * 
 * 【Input】 Excelの項目移送表参照
 * 【Output】 
 * ・List<ページ, 名称> 
 * ※シフトパレット
 * ・対象ページのシフトパレット
 * ・List<シフトマスタ, 出勤休日区分>
 * ・List<勤務予定（シフト）dto>
 *
 * 
 */
@Stateless
public class DisplayInShift {
	
	@Inject
	private GetShiftPalette getShiftPalette; 
	@Inject
	private GetScheduleActualOfShift getSchedulesAndAchievementsByShift;
	

	
	public DisplayInShiftResult getData(DisplayInShiftParam param) {

		// Step 1
		// khởi tạo param để truyền vào ScreenQuery シフトパレットを取得する
		GetShiftPaletteParam paramStep1 = new GetShiftPaletteParam(param.listShiftMasterNotNeedGetNew,
				param.shiftPaletteWantGet, param.workplaceId, param.workplaceGroupId, param.unit);
		// call ScreenQuery シフトパレットを取得する
		GetShiftPaletteResult resultStep1 = getShiftPalette.getDataShiftPallet(paramStep1);

	
		
		// step 2
		TargetOrgIdenInfor targetOrgIdenInfor;
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
		SchedulesbyShiftDataResult schedulesbyShiftDataResult_New = 
				getSchedulesAndAchievementsByShift.getData(
						param.getListShiftMasterNotNeedGetNew(),
						param.getListSid(),
						new DatePeriod(param.getStartDate(), param.getEndDate()),
						param.getDay(),
						param.getActualData,
						targetOrgIdenInfor,
						Optional.ofNullable(param.getPersonalCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, PersonalCounterCategory.class))),
						Optional.ofNullable(param.getWorkplaceCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, WorkplaceCounterCategory.class)))
						);

		return new DisplayInShiftResult(
				resultStep1.listPageInfo,
				resultStep1.targetShiftPalette,
				schedulesbyShiftDataResult_New.getListWorkScheduleShift(),
				schedulesbyShiftDataResult_New.getMapShiftMasterWithWorkStyle(),
				schedulesbyShiftDataResult_New.getAggreratePersonal(),
				schedulesbyShiftDataResult_New.getAggrerateWorkplace());
	}
}
