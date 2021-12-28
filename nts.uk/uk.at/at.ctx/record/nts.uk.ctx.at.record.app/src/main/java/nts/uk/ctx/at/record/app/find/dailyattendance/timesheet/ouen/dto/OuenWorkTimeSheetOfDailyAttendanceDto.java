/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomGeneralDateSerializer;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 *
 */
@AttendanceItemRoot(rootName = ItemConst.DAILY_SUPPORT_TIMESHEET_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OuenWorkTimeSheetOfDailyAttendanceDto extends AttendanceItemCommon{
	
	private static final long serialVersionUID = 1L;
	
	private String employeeId;

	@JsonDeserialize(using = CustomGeneralDateSerializer.class)
	private GeneralDate date;
	
	/** 応援勤務枠No: 応援勤務枠No */
	private int no;
	
	/** 作業内容: 作業内容 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = WORK_CONTENT)
	private WorkContentDto workContent;
	
	/** 時間帯: 時間帯別勤怠の時間帯 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = TIME_ZONE)
	private TimeSheetOfAttendanceEachOuenSheetDto timeSheet;
	
	public static OuenWorkTimeSheetOfDailyAttendanceDto from(String sid, GeneralDate ymd, OuenWorkTimeSheetOfDailyAttendance domain) {
		OuenWorkTimeSheetOfDailyAttendanceDto dto = new OuenWorkTimeSheetOfDailyAttendanceDto();
		if(domain != null){
			dto.setEmployeeId(sid);
			dto.setDate(ymd);
			dto.setNo(domain.getWorkNo().v());
			dto.setWorkContent(WorkContentDto.from(domain.getWorkContent()));
			dto.setTimeSheet(new TimeSheetOfAttendanceEachOuenSheetDto(domain.getTimeSheet().getWorkNo().v(), 
					domain.getTimeSheet().getStart().map(c -> WorkTimeInformationDto.fromDomain(c)).orElse(null), 
					domain.getTimeSheet().getEnd().map(c -> WorkTimeInformationDto.fromDomain(c)).orElse(null)));
			dto.exsistData();
		}
		return dto;
	}
	
	public static List<OuenWorkTimeSheetOfDailyAttendanceDto> froms(String sid, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> domains){
		List<OuenWorkTimeSheetOfDailyAttendanceDto> rs = new ArrayList<>();
		for (OuenWorkTimeSheetOfDailyAttendance ouen : domains) {
			rs.add(from(sid, ymd, ouen));
		}
		return rs;
	}
	
	@Override
	public String rootName() { return DAILY_SUPPORT_TIMESHEET_NAME; }
	
	@Override
	public boolean isRoot() { return true; }
	
	@Override
	public OuenWorkTimeSheetOfDailyAttendanceDto clone() {
		OuenWorkTimeSheetOfDailyAttendanceDto result = new OuenWorkTimeSheetOfDailyAttendanceDto();
		result.setEmployeeId(employeeId());
		result.setDate(workingDate());
		result.setNo(no);
		result.setWorkContent(workContent == null ? null : workContent.clone());
		result.setTimeSheet(timeSheet == null ? null : timeSheet.clone());
		result.exsistData();
		return result;
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case WORK_CONTENT:
			return Optional.ofNullable(workContent);
		case TIME_ZONE:
			return Optional.ofNullable(timeSheet);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case WORK_CONTENT:
			workContent = (WorkContentDto) value;
			break;
		case TIME_ZONE:
			timeSheet = (TimeSheetOfAttendanceEachOuenSheetDto) value;
			break;
		default:
			break;
		}
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case WORK_CONTENT:
			return new WorkContentDto();
		case TIME_ZONE:
			return new TimeSheetOfAttendanceEachOuenSheetDto();
		default:
			return null;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		if (path.equals(WORK_CONTENT) || path.equals(TIME_ZONE)) {
			return PropType.OBJECT;
		}
		return super.typeOf(path);
	}
	
	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public GeneralDate workingDate() {
		return this.date;
	}

	@Override
	public OuenWorkTimeSheetOfDailyAttendance toDomain(String employeeId, GeneralDate date) {
		WorkContent workContent = null;
		if (this.workContent != null) {
			workContent = this.workContent.domain();
		} else {
			workContent = WorkContent.create( 
					WorkplaceOfWorkEachOuen.create(
							new WorkplaceId(""), 
							null), 
					Optional.empty(), 
					Optional.empty());
		}
		
		TimeSheetOfAttendanceEachOuenSheet timeSheet = this.timeSheet == null ? TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()) : this.timeSheet.domain();
		OuenWorkTimeSheetOfDailyAttendance attendance = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(this.no), workContent, timeSheet, Optional.empty());
		return attendance;
	}
	
	public OuenWorkTimeSheetOfDailyAttendance correctOuen(OuenWorkTimeSheetOfDailyAttendance dto, String workplaceId) {
		if (dto.getWorkContent().getWorkplace().getWorkplaceId() != null && dto.getWorkContent().getWorkplace().getWorkplaceId().v().isEmpty()) {
			WorkplaceOfWorkEachOuen workplace = WorkplaceOfWorkEachOuen.create(new WorkplaceId(workplaceId), dto.getWorkContent().getWorkplace().getWorkLocationCD().map(x -> x).orElse(null));
			WorkContent workContent = WorkContent.create(workplace, dto.getWorkContent().getWork(), dto.getWorkContent().getWorkSuppInfo());
			return new OuenWorkTimeSheetOfDailyAttendance(dto.getWorkNo(), workContent, dto.getTimeSheet(), dto.getInputFlag());
		}
		return dto;
	}
}
