/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.ksu001.processcommon.GetListWorkTypeAvailable;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.ksu001.scheduleactualworkinfo.GetScheduleActualOfWorkInfo;

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
	
	public DisplayInWorkInfoResult getDataWorkInfo(DisplayInWorkInfoParam param) {

		DisplayInWorkInfoResult result = new DisplayInWorkInfoResult();

		List<WorkTypeInfomation> listWorkTypeInfo = new ArrayList<>();

		listWorkTypeInfo = getListWorkTypeAvailable.getData();
		
		result.setListWorkTypeInfo(listWorkTypeInfo);
		
		// Lấy data Schedule
		// Get schedule/actual results with work information
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = getScheduleActualOfWorkInfo.getDataScheduleAndAactualOfWorkInfo(param);

		result.setListWorkScheduleWorkInfor(listWorkScheduleWorkInfor);
		return result;
	}
}
