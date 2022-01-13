package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkinputRemarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * @author thanhpv
 * @name 作業詳細
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.パラメータ.作業詳細
 */
@AllArgsConstructor
@Getter
public class WorkDetailsParam {

	//応援勤務枠No: 応援勤務枠No 
	private SupportFrameNo supportFrameNo;
	//時間帯: 時間帯
	private TimeZone timeZone;
	//作業グループ 
	private Optional<WorkGroup> workGroup;
	//備考: 作業入力備考
	private Optional<WorkinputRemarks> remarks;
	//勤務場所: 勤務場所コード
	private Optional<WorkLocationCD> workLocationCD;
}
