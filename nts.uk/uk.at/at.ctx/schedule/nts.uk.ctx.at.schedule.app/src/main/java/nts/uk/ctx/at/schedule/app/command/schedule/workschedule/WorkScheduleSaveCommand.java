package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleSaveCommand<T> {
	
	public String executeId;
	// 社員ID
	public String sid;
	// 年月日
	public GeneralDate ymd;
	// 勤務情報
	public WorkInformationDto workInfor;
	// Map<勤怠項目ID, <T>>
	public Map<Integer, T> mapAttendIdWithTime;
	// 休憩時間帯：List<時間帯>
	public List<TimeSpanForCalc> breakTimeList;
	// ksu001 sử dụng
	public String shiftCode;
	//休憩時間帯が手修正か
	public boolean isBreakByHand;
	
	public WorkScheduleSaveCommand(String sid, GeneralDate ymd, String shiftCode) {
		super();
		this.sid = sid;
		this.ymd = ymd;
		this.shiftCode = shiftCode;
	}

	public WorkScheduleSaveCommand(String sid, GeneralDate ymd, WorkInformationDto workInfor,
			Map<Integer, T> mapAttendIdWithTime, List<TimeSpanForCalc> breakTimeList, boolean isBreakByHand) {
		super();
		this.sid = sid;
		this.ymd = ymd;
		this.workInfor = workInfor;
		this.mapAttendIdWithTime = mapAttendIdWithTime;
		this.breakTimeList = breakTimeList;
		this.isBreakByHand = isBreakByHand;
	}

	public WorkScheduleSaveCommand(String sid, GeneralDate ymd, WorkInformationDto workInfor,
			Map<Integer, T> mapAttendIdWithTime, List<TimeSpanForCalc> breakTimeList, String shiftCode,
			boolean isBreakByHand) {
		super();
		this.sid = sid;
		this.ymd = ymd;
		this.workInfor = workInfor;
		this.mapAttendIdWithTime = mapAttendIdWithTime;
		this.breakTimeList = breakTimeList;
		this.shiftCode = shiftCode;
		this.isBreakByHand = isBreakByHand;
	}
	
	
	
}
