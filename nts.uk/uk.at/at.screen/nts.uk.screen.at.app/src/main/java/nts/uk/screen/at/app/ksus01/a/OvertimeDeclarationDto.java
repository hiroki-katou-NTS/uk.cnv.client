package nts.uk.screen.at.app.ksus01.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OvertimeDeclarationDto {
	/**
	 * 時間外時間
	 * 就業時間帯コード old
	 */
	private Integer overTime;

	/**
	 * 時間外深夜時間
	 */
	private Integer overLateNightTime;

	public static OvertimeDeclarationDto fromDomain(OvertimeDeclaration domain) {
		return new OvertimeDeclarationDto(domain.getOverTime().v(), domain.getOverLateNightTime().v());
	}
}
