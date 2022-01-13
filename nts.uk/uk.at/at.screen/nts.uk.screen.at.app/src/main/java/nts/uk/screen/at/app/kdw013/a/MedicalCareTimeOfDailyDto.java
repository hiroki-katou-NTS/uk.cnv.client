package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.secondorder.medical.MedicalCareTimeOfDaily;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalCareTimeOfDailyDto {
	/** 日勤夜勤区分: 日勤夜勤区分 */
	private Integer dayNightAtr;

	/** 申送時間: 勤怠時間 */
	private Integer takeOverTime;

	/** 控除時間: 勤怠時間 */
	private Integer deductionTime;

	/** 勤務時間: 勤怠時間 */
	private Integer workTime;

	public static MedicalCareTimeOfDailyDto fromDomain(MedicalCareTimeOfDaily domain) {
		return new MedicalCareTimeOfDailyDto(domain.getDayNightAtr().value, domain.getTakeOverTime().v(),
				domain.getDeductionTime().v(), domain.getWorkTime().v());
	}
}
