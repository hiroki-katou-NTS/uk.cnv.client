package nts.uk.ctx.at.record.app.command.workrecord.workrecord;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

/**
 * 応援作業別勤怠時間帯を登録する
 * @author tutt
 *
 */
@Getter
@Setter
public class AddAttendanceTimeZoneCommand {
	
	/** 対象者 */
	private String employeeId;
	
	/** 編集状態<Enum.日別勤怠の編集状態> */
	private EditStateSetting editStateSetting;
	
	/** List<年月日,List<作業詳細>> */
	private List<WorkDetail> workDetails;
}
