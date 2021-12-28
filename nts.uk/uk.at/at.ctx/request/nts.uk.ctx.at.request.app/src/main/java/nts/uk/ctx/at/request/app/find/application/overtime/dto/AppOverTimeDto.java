package nts.uk.ctx.at.request.app.find.application.overtime.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.overtime.ApplicationTimeDto;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.shared.app.find.common.TimeZoneWithWorkNoDto;

@AllArgsConstructor
@NoArgsConstructor
public class AppOverTimeDto {
	// 残業区分
	public Integer overTimeClf;
	
	// 申請時間
	public ApplicationTimeDto applicationTime;
	
	// 休憩時間帯
	public List<TimeZoneWithWorkNoDto> breakTimeOp;
	
	// 勤務時間帯
	public List<TimeZoneWithWorkNoDto> workHoursOp;
	
	// 勤務情報
	public WorkInformationDto workInfoOp;

	public List<MultipleOvertimeContentDto> multipleOvertimeContents;
	
	public static AppOverTimeDto fromDomain(AppOverTime appOverTime) {
	
		return new AppOverTimeDto(
				appOverTime.getOverTimeClf().value,
				ApplicationTimeDto.fromDomain(appOverTime.getApplicationTime()),
				appOverTime.getBreakTimeOp()
						    .orElse(Collections.emptyList())
							.stream()
							.map(x -> TimeZoneWithWorkNoDto.fromDomain(x))
							.collect(Collectors.toList()),
				appOverTime.getWorkHoursOp().orElse(Collections.emptyList())
							.stream()
							.map(x -> TimeZoneWithWorkNoDto.fromDomain(x))
							.collect(Collectors.toList()),
				WorkInformationDto.fromDomain(appOverTime.getWorkInfoOp().orElse(null)),
				appOverTime.getMultipleTimesOp().isPresent() ? MultipleOvertimeContentDto.toDto(appOverTime.getMultipleTimesOp().get()) : null
		);
	}
}
