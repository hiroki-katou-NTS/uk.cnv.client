package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.a.EmployeeDisplayInfo;
import nts.uk.screen.at.app.kdw013.a.GetEmployeeDisplayInfo;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.対象社員を選択する
 * 
 * @author tutt
 *
 */
@Stateless
public class SelectTargetEmployee {

	@Inject
	private GetEmployeeDisplayInfo getEmployeeDisplayInfo;

	public SelectTargetEmployeeDto select(SelectTargetEmployeeParam param) {
		SelectTargetEmployeeDto result = new SelectTargetEmployeeDto();

//		// call 対象社員の表示情報を取得する
//		EmployeeDisplayInfo employeeDisplayInfo = getEmployeeDisplayInfo.getInfo(param.getEmployeeId(),
//				param.getRefDate(), param.getDisplayPeriod().toDomain());
//
//		List<WorkGroupDto> workGroupDtos = employeeDisplayInfo.getWorkGroups().stream().map(m -> WorkGroupDto.toDto(m))
//				.collect(Collectors.toList());
//
//		result.setLstComfirmerDto(employeeDisplayInfo.getLstComfirmerDto());
//		result.setWorkGroupDtos(workGroupDtos);
//		result.setWorkCorrectionStartDate(employeeDisplayInfo.getDate());
//		result.setLstWorkRecordDetailDto(employeeDisplayInfo.getWorkRecordDetails().stream()
//				.map(m -> WorkRecordDetailDto.toDto(m)).collect(Collectors.toList()));

		return result;
	}

}
