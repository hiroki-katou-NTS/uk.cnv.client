package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class EmployeeDisplayInfo {

	// 年月日
	private GeneralDate workStartDate;
	// List＜確認者>
	private List<ConfirmerDto> lstComfirmerDto;
	// List<日別勤怠(Work)>
	private List<IntegrationOfDailyDto> lstIntegrationOfDaily;
	// List<日別実績の実績内容>
	private List<ManHrRecordConvertResultDto> convertRes;
	// List<日別実績の工数実績作業>
	private List<DailyActualManHrActualTaskDto> dailyManHrTasks;
	// List<入力目安時間帯>
	private List<EstimatedTimeZoneDto> estimateZones = new ArrayList<>();
	// List<日別実績のロック状態>
	private List<DailyLockDto> lockInfos;

	public void setDailyPerformanceData(GetDailyPerformanceDataResult domain) {
		this.lstIntegrationOfDaily = domain.getLstIntegrationOfDaily().stream().map(d -> IntegrationOfDailyDto.toDto(d))
				.collect(Collectors.toList());

		this.convertRes = domain.getConvertRes().stream().map(c -> ManHrRecordConvertResultDto.fromDomain(c))
				.collect(Collectors.toList());

		this.dailyManHrTasks = domain.getDailyManHrTasks().stream()
				.map(dh -> DailyActualManHrActualTaskDto.fromDomain(dh)).collect(Collectors.toList());
	}

}
