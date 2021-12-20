package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;

/**
 * @author thanhpv
 * @name 時間帯	
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.パラメータ.時間帯
 */
@AllArgsConstructor
@Getter
public class TimeZone {

	//開始時刻: 時刻(日区分付き)
	private WorkTimeInformation start;
	
	//終了時刻: 時刻(日区分付き)
	private WorkTimeInformation end;
	
	//作業時間: 勤怠時間
	private Optional<AttendanceTime> workingHours;
}
