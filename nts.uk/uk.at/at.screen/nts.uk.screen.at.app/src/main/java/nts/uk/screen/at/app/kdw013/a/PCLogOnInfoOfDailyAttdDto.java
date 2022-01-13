package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.PCLogOnInfoOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PCLogOnInfoOfDailyAttdDto {
	/** ログオン情報: ログオン情報 */
	private List<LogOnInfoDto> logOnInfo;

	public static PCLogOnInfoOfDailyAttdDto fromDomain(Optional<PCLogOnInfoOfDailyAttd> domain) {
		
		return domain
				.map(x -> new PCLogOnInfoOfDailyAttdDto(
						x.getLogOnInfo().stream().map(li -> LogOnInfoDto.fromDomain(li)).collect(Collectors.toList())))
				.orElse(null);
	}
}
