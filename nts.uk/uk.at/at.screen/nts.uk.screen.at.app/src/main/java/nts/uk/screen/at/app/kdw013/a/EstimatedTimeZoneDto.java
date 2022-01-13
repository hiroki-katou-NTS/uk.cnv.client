package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.BreakTimeSheetDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.TimeZoneDto;
import nts.uk.screen.at.app.kdw013.query.EstimatedTimeZone;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EstimatedTimeZoneDto {
	// 年月日
	private GeneralDate ymd;

	// 開始時刻
	private Integer startTime;

	// 終了時刻
	private Integer endTime;

	// 休憩時間帯

	private List<BreakTimeSheetDto> breakTimeSheets;
	
	// 残業時間帯

	private List<TimeZoneDto> overTimeZones;

	public static EstimatedTimeZoneDto fromDomain(EstimatedTimeZone et) {

		return new EstimatedTimeZoneDto(
						et.getYmd(), 
						et.getStartTime() == null ? null : et.getStartTime().v(), 
						et.getEndTime()	== null ? null : et.getEndTime().v(),
						et.getBreakTimeSheets().stream().map(bt -> new BreakTimeSheetDto(bt.getStartTime().v(), bt.getEndTime().v(),bt.getBreakTime().v(), bt.getBreakFrameNo().v())).collect(Collectors.toList()),
						et.getOverTimeZones().stream().map(x-> new TimeZoneDto(x.getStartTime().v(), x.getEndTime().v())).collect(Collectors.toList())
				);
	}
}
