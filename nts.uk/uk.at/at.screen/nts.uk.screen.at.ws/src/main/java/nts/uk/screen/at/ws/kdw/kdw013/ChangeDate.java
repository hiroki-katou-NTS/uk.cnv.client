package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.screen.at.app.kdw013.a.EmployeeDisplayInfo;
import nts.uk.screen.at.app.kdw013.a.GetEmployeeDisplayInfo;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.日付を変更する
 * 
 * @author tutt
 *
 */
@Stateless
public class ChangeDate {

	@Inject
	private GetEmployeeDisplayInfo empDisplayInfo;

	/**
	 * 
	 * @param sid     社員ID
	 * @param refDate 基準日
	 * @param period  表示期間
	 * @return
	 */
	public ChangeDateDto changeDate(String sid, GeneralDate refDate, DatePeriod period) {
		ChangeDateDto changeDateDto = new ChangeDateDto();

		EmployeeDisplayInfo employeeDisplayInfo = empDisplayInfo.getInfo(sid, refDate, period);

		List<WorkGroupDto> workGroupDtos = employeeDisplayInfo.getWorkGroups().stream().map(m -> WorkGroupDto.toDto(m))
				.collect(Collectors.toList());

		changeDateDto.setLstComfirmerDto(employeeDisplayInfo.getLstComfirmerDto());
		changeDateDto.setWorkGroupDtos(workGroupDtos);
		changeDateDto.setWorkCorrectionStartDate(employeeDisplayInfo.getDate());
		changeDateDto.setLstWorkRecordDetailDto(employeeDisplayInfo.getWorkRecordDetails().stream()
				.map(m -> WorkRecordDetailDto.toDto(m)).collect(Collectors.toList()));

		return changeDateDto;
	}

}
