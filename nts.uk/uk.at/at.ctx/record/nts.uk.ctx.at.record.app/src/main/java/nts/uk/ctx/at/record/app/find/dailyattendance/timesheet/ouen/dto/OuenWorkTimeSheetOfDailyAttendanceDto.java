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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkinputRemarks;
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
			dto.setNo(domain.getWorkNo());
			dto.setWorkContent(new WorkContentDto( 
					new WorkplaceOfWorkEachOuenDto(
							domain.getWorkContent().getWorkplace().getWorkplaceId() == null ? null : domain.getWorkContent().getWorkplace().getWorkplaceId().v(),
							!domain.getWorkContent().getWorkplace().getWorkLocationCD().isPresent() ? null :
							domain.getWorkContent().getWorkplace().getWorkLocationCD().get().v()), 
					new WorkGroupDto(
							(domain.getWorkContent().getWork().isPresent() && domain.getWorkContent().getWork().get().getWorkCD1() != null) ? domain.getWorkContent().getWork().get().getWorkCD1().v() : null, 
							(domain.getWorkContent().getWork().isPresent() && domain.getWorkContent().getWork().get().getWorkCD2().isPresent()) ? domain.getWorkContent().getWork().get().getWorkCD2().get().v() : null, 
							(domain.getWorkContent().getWork().isPresent() && domain.getWorkContent().getWork().get().getWorkCD3().isPresent()) ? domain.getWorkContent().getWork().get().getWorkCD3().get().v() : null, 
							(domain.getWorkContent().getWork().isPresent() && domain.getWorkContent().getWork().get().getWorkCD4().isPresent()) ? domain.getWorkContent().getWork().get().getWorkCD4().get().v() : null, 
							(domain.getWorkContent().getWork().isPresent() && domain.getWorkContent().getWork().get().getWorkCD5().isPresent()) ? domain.getWorkContent().getWork().get().getWorkCD5().get().v() : null),
					(domain.getWorkContent().getWorkRemarks().isPresent() ? domain.getWorkContent().getWorkRemarks().get().v() : null)));
			dto.setTimeSheet(new TimeSheetOfAttendanceEachOuenSheetDto(domain.getTimeSheet().getWorkNo().v(), 
					new WorkTimeInformationDto(new ReasonTimeChangeDto(
							(domain.getTimeSheet().getStart().isPresent() && domain.getTimeSheet().getStart().get().getReasonTimeChange() != null && domain.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans() != null) ? domain.getTimeSheet().getStart().get().getReasonTimeChange().getTimeChangeMeans().value : null, 
							(domain.getTimeSheet().getStart().isPresent() && domain.getTimeSheet().getStart().get().getReasonTimeChange() != null && domain.getTimeSheet().getStart().get().getReasonTimeChange().getEngravingMethod().isPresent()) ? domain.getTimeSheet().getStart().get().getReasonTimeChange().getEngravingMethod().get().value : null), 
							(domain.getTimeSheet().getStart().isPresent() && domain.getTimeSheet().getStart().get().getTimeWithDay().isPresent()) ? domain.getTimeSheet().getStart().get().getTimeWithDay().get().v() : null), 
					new WorkTimeInformationDto(new ReasonTimeChangeDto(
							(domain.getTimeSheet().getEnd().isPresent() && domain.getTimeSheet().getEnd().get().getReasonTimeChange() != null && domain.getTimeSheet().getEnd().get().getReasonTimeChange().getTimeChangeMeans() != null) ? domain.getTimeSheet().getEnd().get().getReasonTimeChange().getTimeChangeMeans().value : null, 
							(domain.getTimeSheet().getEnd().isPresent() && domain.getTimeSheet().getEnd().get().getReasonTimeChange() != null && domain.getTimeSheet().getEnd().get().getReasonTimeChange().getEngravingMethod().isPresent()) ? domain.getTimeSheet().getEnd().get().getReasonTimeChange().getEngravingMethod().get().value : null), 
							(domain.getTimeSheet().getEnd().isPresent() && domain.getTimeSheet().getEnd().get().getTimeWithDay().isPresent())? domain.getTimeSheet().getEnd().get().getTimeWithDay().get().v() : null)));
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
		if(this.workContent != null) {
			workContent = WorkContent.create( 
					WorkplaceOfWorkEachOuen.create(
							new WorkplaceId(this.workContent.getWorkplace() == null ? null : this.workContent.getWorkplace().getWorkplaceId()), 
							new WorkLocationCD(this.workContent.getWorkplace() == null ? null : this.workContent.getWorkplace().getWorkLocationCD())), 
					Optional.ofNullable(WorkGroup.create(
							this.workContent.getWork() == null ? null : this.workContent.getWork().getWorkCD1(), 
							this.workContent.getWork() == null ? null : this.workContent.getWork().getWorkCD2(), 
							this.workContent.getWork() == null ? null : this.workContent.getWork().getWorkCD3(), 
							this.workContent.getWork() == null ? null : this.workContent.getWork().getWorkCD4(), 
							this.workContent.getWork() == null ? null : this.workContent.getWork().getWorkCD5())), 
					this.workContent.getWorkRemarks() == null ? Optional.empty() : Optional.of(new WorkinputRemarks(this.workContent.getWorkRemarks())));
		} else {
			workContent = WorkContent.create( 
					WorkplaceOfWorkEachOuen.create(
							null, 
							null), 
					Optional.ofNullable(WorkGroup.create(
							null, 
							Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())), 
					Optional.empty());
		}
		ReasonTimeChange reasonTimeChangeStart = null;
		WorkTimeInformation start = null;
		if(this.timeSheet != null && this.timeSheet.getStart() != null) {
			reasonTimeChangeStart = new ReasonTimeChange(TimeChangeMeans.valueOf(this.timeSheet.getStart().getReasonTimeChange() == null ? null : this.timeSheet.getStart().getReasonTimeChange().getTimeChangeMeans()), 
					 Optional.ofNullable(EngravingMethod.valueOf(this.timeSheet.getStart().getReasonTimeChange() == null ? null : this.timeSheet.getStart().getReasonTimeChange().getEngravingMethod())));	
			start = new WorkTimeInformation(reasonTimeChangeStart, this.timeSheet.getStart().getTimeWithDay() == null ? null : new TimeWithDayAttr(this.timeSheet.getStart().getTimeWithDay()));
		} else {
			reasonTimeChangeStart = new ReasonTimeChange(null, Optional.empty());	
			start = new WorkTimeInformation(reasonTimeChangeStart, null);
		}
		
		ReasonTimeChange reasonTimeChangeEnd = null;
		WorkTimeInformation end = null;
		if(this.timeSheet != null && this.timeSheet.getEnd() != null) {
			reasonTimeChangeEnd = new ReasonTimeChange(TimeChangeMeans.valueOf(this.timeSheet.getEnd().getReasonTimeChange() != null ? this.timeSheet.getEnd().getReasonTimeChange().getTimeChangeMeans() : null), 
					 Optional.ofNullable(EngravingMethod.valueOf(this.timeSheet.getEnd().getReasonTimeChange() != null ? this.timeSheet.getEnd().getReasonTimeChange().getEngravingMethod() : null)));
			end = new WorkTimeInformation(reasonTimeChangeEnd, this.timeSheet.getEnd().getTimeWithDay() == null ? null : new TimeWithDayAttr(this.timeSheet.getEnd().getTimeWithDay()));
		} else {
			reasonTimeChangeEnd = new ReasonTimeChange(null, Optional.empty());	
			end = new WorkTimeInformation(reasonTimeChangeStart, null);
		}
		
		TimeSheetOfAttendanceEachOuenSheet timeSheet = TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(this.timeSheet == null ? 1 : this.timeSheet.getNo()), Optional.ofNullable(start), Optional.ofNullable(end));
		OuenWorkTimeSheetOfDailyAttendance attendance = OuenWorkTimeSheetOfDailyAttendance.create(this.no, workContent, timeSheet);
		return attendance;
	}
}
