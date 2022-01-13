package nts.uk.screen.at.app.ksus01.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.TimeSpanForCalcDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.screen.at.app.kdw013.a.WorkStampDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeActualStampDto {

	private WorkStampDto actualStamp;
	// 打刻

	private WorkStampDto stamp;

	// 打刻反映回数

	private Integer numberOfReflectionStamp;

	// 時間外の申告

	private OvertimeDeclarationDto overtimeDeclaration;

	// 時間休暇時間帯

	private TimeSpanForCalcDto timeVacation;

	public static TimeActualStampDto fromDomain(Optional<TimeActualStamp> domain) {
		return domain
				.map(x -> new TimeActualStampDto(x.getActualStamp().map(as -> WorkStampDto.fromDomain(as)).orElse(null),
						x.getStamp().map(as -> WorkStampDto.fromDomain(as)).orElse(null),
						x.getNumberOfReflectionStamp(),
						x.getOvertimeDeclaration().map(od -> OvertimeDeclarationDto.fromDomain(od)).orElse(null),
						TimeSpanForCalcDto.fromDomain(x.getTimeVacation())))
				.orElse(null);
	}

}
