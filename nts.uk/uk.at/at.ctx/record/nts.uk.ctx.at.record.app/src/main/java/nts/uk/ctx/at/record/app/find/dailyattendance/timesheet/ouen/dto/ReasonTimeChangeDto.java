package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonTimeChangeDto {

	// 時刻変更手段
	private int timeChangeMeans;

	// 打刻方法
	private Integer engravingMethod;

	public static ReasonTimeChangeDto fromDomain(ReasonTimeChange domain) {
		return new ReasonTimeChangeDto(domain.getTimeChangeMeans().value,
				domain.getEngravingMethod().map(x -> x.value).orElse(null));
	}

	public ReasonTimeChange domain() {
		
		return new ReasonTimeChange(
				TimeChangeMeans.valueOf(timeChangeMeans), 
				Optional.ofNullable(engravingMethod == null ? null : EngravingMethod.valueOf(engravingMethod)));
	}
}
