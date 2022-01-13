package nts.uk.screen.at.app.ksu003.getworkselectioninfor;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.GetWorkSelectionInforDto;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.TaskData;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.WorkPaletteDisplayInforDto;

/**
 * 作業選択準備情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).A：個人スケジュール修正(日付別).メニュー別OCD.作業選択準備情報を取得する
 * 
 * @author HieuLT
 *
 */
@Stateless
public class GetWorkSelectionInfor {


	@Inject
	private GetWorkPaletteDisplay palette;
	
	@Inject GetListTask getTask;

	public GetWorkSelectionInforDto get(GeneralDate baseDate, int targetUnit, int page, String organizationID) {
		//1 Get <<Query>> 使用できる作業マスタを取得する
		List<TaskData> lstTask = getTask.getListTask(baseDate, 1);
		//2 Get <<>> 作業パレット表示情報を取得する
		WorkPaletteDisplayInforDto workPaletteDisplayInforDto = palette.getWorkPaletteDisplayInfo(targetUnit, organizationID, baseDate, page);
		GetWorkSelectionInforDto result = new GetWorkSelectionInforDto(lstTask, workPaletteDisplayInforDto);
		return result;
	}




}
