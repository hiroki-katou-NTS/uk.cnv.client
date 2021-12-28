package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.overtime.DivergenceReasonInputMethodDto;
import nts.uk.ctx.at.request.app.find.application.overtime.DivergenceTimeRootDto;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.DivergenceReasonInputMethod;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.DailyAttendanceItemAuthorityDto;
import nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto.WorkTimeSettingDto;
import nts.uk.ctx.at.shared.app.find.worktype.WorkTypeDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.screen.at.app.kdw006.j.DailyAttendanceItemDto;

/**
 * @author thanhpv
 * @description out put for 勤怠項目マスタ情報を取得する
 */
@NoArgsConstructor
@Getter
public class AttendanceItemMasterInformationDto {

	//勤怠項目リスト：List<勤怠項目>
	public List<AttItemNameDto> attItemName;
	
	//日次の勤怠項目リスト：List<日次の勤怠項目>
	public List<DailyAttendanceItemDto> dailyAttendanceItem;
	
	//勤務種類リスト：List<勤務種類>
	public List<WorkTypeDto> workTypes;
	
	//就業時間帯リスト：List<就業時間帯の設定>
	public List<WorkTimeSettingDto> workTimeSettings;
	
	//乖離時間リスト：List<乖離時間>
	public List<DivergenceTimeRootDto> divergenceTimeRoots;
	
	//乖離理由リスト：List<乖離理由の入力方法> 
	public List<DivergenceReasonInputMethodDto> divergenceReasonInputMethods;
	
	//Optional<権限別日次項目制御>
	public DailyAttendanceItemAuthorityDto dailyAttendanceItemAuthority;

	public AttendanceItemMasterInformationDto(List<AttItemName> attItemName,
			List<DailyAttendanceItem> dailyAttendanceItem, List<WorkType> workTypes,
			List<WorkTimeSetting> workTimeSettings, List<DivergenceTimeRoot> divergenceTimeRoots,
			List<DivergenceReasonInputMethod> divergenceReasonInputMethods, Optional<DailyAttendanceItemAuthority> dailyAttendanceItemAuthority) {
		super();
		this.attItemName = attItemName.stream().map(c-> new AttItemNameDto(c)).collect(Collectors.toList());
		this.dailyAttendanceItem = dailyAttendanceItem.stream().map(c-> new DailyAttendanceItemDto(c)).collect(Collectors.toList());
		this.workTypes = workTypes.stream().map(c-> WorkTypeDto.fromDomain(c)).collect(Collectors.toList());
		this.workTimeSettings = workTimeSettings.stream().map(c-> WorkTimeSettingDto.fromDomain(c)).collect(Collectors.toList());
		this.divergenceTimeRoots = divergenceTimeRoots.stream().map(c-> DivergenceTimeRootDto.fromDomain(c)).collect(Collectors.toList());
		this.divergenceReasonInputMethods = divergenceReasonInputMethods.stream().map(c-> DivergenceReasonInputMethodDto.fromDomain(c)).collect(Collectors.toList());
		this.dailyAttendanceItemAuthority = dailyAttendanceItemAuthority.map(c-> DailyAttendanceItemAuthorityDto.fromDomain(c)).orElse(null);
	}
}
