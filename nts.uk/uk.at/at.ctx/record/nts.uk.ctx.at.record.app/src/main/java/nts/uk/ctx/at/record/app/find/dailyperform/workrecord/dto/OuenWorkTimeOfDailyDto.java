package nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomGeneralDateSerializer;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

/** 日別実績の応援作業別勤怠時間 */
@Data
@EqualsAndHashCode(callSuper = false)
@AttendanceItemRoot(rootName = ItemConst.DAILY_SUPPORT_TIME_NAME)
public class OuenWorkTimeOfDailyDto extends AttendanceItemCommon {
	/***/
	private static final long serialVersionUID = 1L;
	
	/** 社員ID: 社員ID */
	private String employeeId;
	
	/** 年月日: 年月日 */
	@JsonDeserialize(using = CustomGeneralDateSerializer.class)
	private GeneralDate date;

	/** 日別勤怠の応援作業時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = TIME,
			listMaxLength = 20, indexField = DEFAULT_INDEX_FIELD_NAME)
	private List<OuenWorkTimeOfDailyAttendanceDto> workTimes = new ArrayList<>();
	
	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public GeneralDate workingDate() {
		return this.date;
	}
	
	public static OuenWorkTimeOfDailyDto from(String employeeId, GeneralDate date, List<OuenWorkTimeOfDailyAttendance> domain) {
		OuenWorkTimeOfDailyDto dto = new OuenWorkTimeOfDailyDto();
		if(domain != null ) {
			dto.setEmployeeId(employeeId);
			dto.setDate(date);
			dto.setWorkTimes(domain.stream()
					.map(d -> OuenWorkTimeOfDailyAttendanceDto.toDto(d))
					.collect(Collectors.toList()));
		}
		dto.exsistData();
		return dto;
	}
	
	public static OuenWorkTimeOfDailyDto from(OuenWorkTimeOfDaily domain) {
		OuenWorkTimeOfDailyDto dto = new OuenWorkTimeOfDailyDto();
		if(domain != null ) {
			dto.setEmployeeId(domain.getEmpId());
			dto.setDate(domain.getYmd());
			dto.setWorkTimes(domain.getOuenTimes().stream()
					.map(d -> OuenWorkTimeOfDailyAttendanceDto.toDto(d))
					.collect(Collectors.toList()));
		}
		dto.exsistData();
		return dto;
	}
	
	@Override
	public OuenWorkTimeOfDaily toDomain(String employeeId, GeneralDate date) {
		return OuenWorkTimeOfDaily.create(employeeId, date,
				workTimes.stream().map(d -> d.toDomain()).collect(Collectors.toList()));
	}
	
	public OuenWorkTimeOfDailyDto clone() {
		OuenWorkTimeOfDailyDto dto = new OuenWorkTimeOfDailyDto();
		dto.setEmployeeId(employeeId);
		dto.setDate(date);
		dto.setWorkTimes(workTimes);
		
		if(isHaveData()){
			dto.exsistData();
		}
		return dto;
	}
	
	@Override
	public boolean isRoot() { return true; }
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		if (path.equals(TIME)) {
			return (List<T>) this.workTimes;
		}
		return new ArrayList<>();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		if (path.equals(TIME)) {
			this.workTimes = (List<OuenWorkTimeOfDailyAttendanceDto>) value;
		}
	}
	
	@Override
	public int size(String path) {
		return 20;
	}
	
	@Override
	public PropType typeOf(String path) {
		if (path.equals(TIME)) {
			return PropType.IDX_LIST;
		}
		return super.typeOf(path);
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		if (path.equals(TIME)) {
			return new OuenWorkTimeOfDailyAttendanceDto();
		}
		return super.newInstanceOf(path);
	}
}
