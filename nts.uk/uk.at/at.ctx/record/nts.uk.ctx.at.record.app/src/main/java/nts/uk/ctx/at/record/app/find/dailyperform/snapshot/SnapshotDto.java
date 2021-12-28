package nts.uk.ctx.at.record.app.find.dailyperform.snapshot;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomGeneralDateSerializer;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;

@Data
@EqualsAndHashCode(callSuper = false)
@AttendanceItemRoot(rootName = ItemConst.DAILY_SNAPSHOT_NAME)
public class SnapshotDto extends AttendanceItemCommon {
	
	/***/
	private static final long serialVersionUID = 1L;
	
	private String employeeId;
	
	@JsonDeserialize(using = CustomGeneralDateSerializer.class)
	private GeneralDate ymd;

	/** 勤務種類 */
	@AttendanceItemValue()
	@AttendanceItemLayout(jpPropertyName = WORK_TYPE, layout = LAYOUT_A)
	private String workType;

	/** 就業時間帯コード */
	@AttendanceItemValue()
	@AttendanceItemLayout(jpPropertyName = WORK_TIME, layout = LAYOUT_A)
	private String workTime;

	/** 所定時間: 勤怠時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = TIME, layout = LAYOUT_A)
	private int predetermineTime;

	@Override
	public String employeeId() {
		return employeeId;
	}

	@Override
	public GeneralDate workingDate() {
		return ymd;
	}
	
	public static SnapshotDto from(String sid, GeneralDate ymd, SnapShot snapshot) {
		
		SnapshotDto dto = new SnapshotDto();
		if(snapshot != null){
			dto.setEmployeeId(sid);
			dto.setYmd(ymd);
			dto.setPredetermineTime(snapshot.getPredetermineTime().v());
			dto.setWorkTime(snapshot.getWorkInfo().getWorkTimeCodeNotNull().map(c -> c.v()).orElse(null));
			dto.setWorkType(snapshot.getWorkInfo().getWorkTypeCode().v());
			dto.exsistData();
		}
		return dto;
	}

	@Override
	public SnapShot toDomain(String employeeId, GeneralDate date) {
		
		if (!this.isHaveData()) {
			return null;
		}
		
		return SnapShot.of(new WorkInformation(workType, workTime), new AttendanceTime(predetermineTime));
	}
	
	@Override
	public SnapshotDto clone(){
		SnapshotDto dto = new SnapshotDto();
		dto.setEmployeeId(employeeId());
		dto.setYmd(workingDate());
		dto.setPredetermineTime(predetermineTime);
		dto.setWorkTime(workTime);
		dto.setWorkType(workType);
		if (isHaveData()) {
			dto.exsistData();
		}
		return dto;
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch(path) {
		case WORK_TYPE:
			return Optional.of(ItemValue.builder().value(workType).valueType(ValueType.CODE));
		case WORK_TIME:
			return Optional.of(ItemValue.builder().value(workTime).valueType(ValueType.CODE));
		case TIME:
			return Optional.of(ItemValue.builder().value(predetermineTime).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch(path) {
		case WORK_TYPE:
			this.workType = value.valueOrDefault(null);
			break;
		case WORK_TIME:
			this.workTime = value.valueOrDefault(null);
			break;
		case TIME:
			this.predetermineTime = value.valueOrDefault(0);
			break;
		default:
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch(path) {
		case WORK_TYPE:
		case WORK_TIME:
		case TIME:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}
}
