package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.ReasonTimeChangeDto;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkTimeInformationDto;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.BreakTimeSheetDto;
import nts.uk.screen.at.app.kdw013.a.ActualContent;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class ActualContentDto {

	// 休憩リスト
	private List<BreakTimeSheetDto> breakTimeSheets;

	// 休憩時間
	private Integer breakHours;

	// 終了時刻
	private WorkTimeInformationDto end;

	// 総労働時間
	private Integer totalWorkingHours;

	// 開始時刻
	private WorkTimeInformationDto start;

	public static ActualContentDto toDto(ActualContent domain) {
		ActualContentDto actualContentDto = new ActualContentDto();

		actualContentDto.setBreakHours(domain.getBreakHours().map(m -> m.v()).orElse(null));
		actualContentDto.setTotalWorkingHours(domain.getTotalWorkingHours().map(m -> m.v()).orElse(null));

		WorkTimeInformationDto start = new WorkTimeInformationDto();
		start.setReasonTimeChange(new ReasonTimeChangeDto(
				domain.getStart().map(m -> m.getReasonTimeChange().getTimeChangeMeans().value).orElse(null),
				domain.getStart().map(m -> m.getReasonTimeChange().getEngravingMethod().map(n -> n.value).orElse(null))
						.orElse(null)));
		start.setTimeWithDay(
				domain.getStart().map(m -> m.getTimeWithDay().map(n -> n.getDayTime()).orElse(null)).orElse(null));

		WorkTimeInformationDto end = new WorkTimeInformationDto();
		end.setReasonTimeChange(new ReasonTimeChangeDto(
				domain.getEnd().map(m -> m.getReasonTimeChange().getTimeChangeMeans().value).orElse(null),
				domain.getEnd().map(m -> m.getReasonTimeChange().getEngravingMethod().map(n -> n.value).orElse(null))
						.orElse(null)));
		end.setTimeWithDay(
				domain.getEnd().map(m -> m.getTimeWithDay().map(n -> n.getDayTime()).orElse(null)).orElse(null));

		actualContentDto.setStart(start);
		actualContentDto.setEnd(end);
		actualContentDto
				.setBreakTimeSheets(domain
						.getBreakTimeSheets().stream().map(m -> new BreakTimeSheetDto(m.getStartTime().v(),
								m.getEndTime().v(), m.getBreakTime().v(), m.getBreakFrameNo().v()))
						.collect(Collectors.toList()));

		return actualContentDto;
	}

}
