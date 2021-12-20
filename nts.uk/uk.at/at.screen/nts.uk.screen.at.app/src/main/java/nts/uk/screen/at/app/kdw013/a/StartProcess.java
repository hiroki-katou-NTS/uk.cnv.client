package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.worklocation.WorkLocationDto;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.初期起動処理
 * 
 * @author tutt
 *
 */
@Stateless
public class StartProcess {

	@Inject
	private StartManHourInputScreenQuery startManHourInputScreenQuery;

	@Inject
	private GetRefWorkplaceAndEmployee getRefWorkplaceAndEmployee;

	public StartProcessDto startProcess() {
		StartProcessDto startProcessDto = new StartProcessDto();

		// 1: <call>()
		StartManHourInputResultDto startManHourInputResultDto = new StartManHourInputResultDto();

		StartManHourInput startManHourInput = startManHourInputScreenQuery.startManHourInput();

		TaskFrameUsageSetting taskFrameUsageSetting = startManHourInput.getTaskFrameUsageSetting();
		List<Task> tasks = startManHourInput.getTasks();
		List<WorkLocation> workLocations = startManHourInput.getWorkLocations();

		// convert to DTO
		TaskFrameUsageSettingDto taskFrameUsageSettingDto = new TaskFrameUsageSettingDto();
		
		if (taskFrameUsageSetting != null) {
			taskFrameUsageSettingDto = new TaskFrameUsageSettingDto(taskFrameUsageSetting
					.getFrameSettingList().stream().map(m -> TaskFrameSettingDto.toDto(m)).collect(Collectors.toList()));
		}
		
		List<TaskDto> taskDtos = new ArrayList<>();
		
		if (!tasks.isEmpty()) {
			taskDtos = tasks.stream().map(m -> TaskDto.toDto(m)).collect(Collectors.toList());
		}
		List<WorkLocationDto> lstWorkLocationDto = new ArrayList<>();
		
		if (!workLocations.isEmpty()) {
			lstWorkLocationDto = workLocations.stream().map(m -> WorkLocationDto.fromDomain(m))
					.collect(Collectors.toList());
		}

		startManHourInputResultDto.setTaskFrameUsageSetting(taskFrameUsageSettingDto);
		startManHourInputResultDto.setTasks(taskDtos);
		startManHourInputResultDto.setWorkLocations(lstWorkLocationDto);

		startProcessDto.setStartManHourInputResultDto(startManHourInputResultDto);
		
		// 2: [画面モード = 確認モード]: <call>()
		GetRefWorkplaceAndEmployeeDto refWorkplaceAndEmployeeDto = getRefWorkplaceAndEmployee.get(GeneralDate.today());
		
		if (refWorkplaceAndEmployeeDto != null) {
			startProcessDto.setRefWorkplaceAndEmployeeDto(new GetRefWorkplaceAndEmployeeResultDto(
					refWorkplaceAndEmployeeDto.getEmployeeInfos().entrySet().stream()
							.map(x -> new RefEmpWkpInfoDto(x.getKey(), x.getValue())).collect(Collectors.toList()), 
					refWorkplaceAndEmployeeDto.getLstEmployeeInfo(),
					refWorkplaceAndEmployeeDto.getWorkplaceInfos().stream().map(m -> 
					new WorkplaceInfoDto(m.getWorkplaceId(), m.getWorkplaceCode().v(), m.getWorkplaceName().v()
							, m.getWkpGenericName().v(), m.getWkpDisplayName().v(), m.getOutsideWkpCode().v())
					).collect(Collectors.toList())));
		}
		
		return startProcessDto;
	}

}
