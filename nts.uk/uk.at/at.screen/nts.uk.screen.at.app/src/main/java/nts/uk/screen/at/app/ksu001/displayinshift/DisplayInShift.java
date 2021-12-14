/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinshift;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.ksu001.getschedulesbyshift.GetScheduleActualOfShift;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.SchedulesbyShiftDataResult;
import nts.uk.screen.at.app.ksu001.getschedulesbyshift.SchedulesbyShiftParam;
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

		// Step 2 call ScreenQuery 予定・実績をシフトで取得する
		SchedulesbyShiftParam paramStep2 = new SchedulesbyShiftParam(resultStep1.listShiftMaster, param.listSid,
				param.startDate, param.endDate, param.getActualData);
		SchedulesbyShiftDataResult resultStep2 = getSchedulesAndAchievementsByShift.getData(paramStep2);

		return new DisplayInShiftResult(resultStep1.listPageInfo, resultStep1.targetShiftPalette,
				resultStep2.listShiftMaster, resultStep2.listWorkScheduleShift);
	}
}
