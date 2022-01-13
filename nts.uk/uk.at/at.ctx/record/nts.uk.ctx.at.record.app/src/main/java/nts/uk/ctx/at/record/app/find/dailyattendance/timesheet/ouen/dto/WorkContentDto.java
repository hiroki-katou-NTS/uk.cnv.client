/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;

/**
 * @author laitv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 作業内容 */
public class WorkContentDto implements  ItemConst, AttendanceItemDataGate {
	
		/** 勤務先: 応援別勤務の勤務先 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = WORKPLACE_BYSUPPORT)
	private WorkplaceOfWorkEachOuenDto workplace;
	
	
	/** 作業: 作業グループ */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = WORKGROUP)
	private Optional<WorkGroupDto> workOpt = Optional.empty();

	/** 作業補足情報 */
	private Optional<WorkSuppInfoDto> workSuppInfo = Optional.empty();
	
	public static WorkContentDto from(WorkContent domain) {
		if (domain == null) return null;
	
		return new WorkContentDto(
					WorkplaceOfWorkEachOuenDto.from(domain.getWorkplace()),
					Optional.ofNullable(WorkGroupDto.from(domain.getWork().isPresent() ? domain.getWork().get() : null)),
					Optional.ofNullable(WorkSuppInfoDto.from(domain.getWorkSuppInfo().isPresent() ? domain.getWorkSuppInfo().get() : null)));
	}
	
	public WorkContent domain() {
		return WorkContent.create(
				workplace == null ? WorkplaceOfWorkEachOuen.create(new WorkplaceId(""), null) : workplace.domain(), 
				Optional.ofNullable((workOpt != null && workOpt.isPresent())?workOpt.get().domain():null),
				Optional.ofNullable((workSuppInfo != null && workSuppInfo.isPresent())?workSuppInfo.get().domain():null)); 
	} 
	
	@Override
	public WorkContentDto clone() {
		WorkContentDto result = new WorkContentDto();
		result.setWorkplace(workplace == null ? null : workplace.clone());
		result.setWorkOpt(!workOpt.isPresent() ? Optional.empty() : Optional.of(workOpt.get().clone()));
		result.setWorkSuppInfo(!workSuppInfo.isPresent() ? Optional.empty() : Optional.of(workSuppInfo.get().clone()));
		return result;
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case WORKPLACE_BYSUPPORT:
			return new WorkplaceOfWorkEachOuenDto();
		case WORKGROUP:
			return new WorkGroupDto();
		case SUPP:
			return new WorkSuppInfoDto();
		default:
			return null;
		}
	}
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case WORKPLACE_BYSUPPORT:
			workplace = (WorkplaceOfWorkEachOuenDto) value;
			break;
		case WORKGROUP:
			workOpt = Optional.ofNullable((WorkGroupDto) value);
			break;
		case SUPP:
			workSuppInfo = Optional.ofNullable((WorkSuppInfoDto) value);
			break;
		default:
			break;
		}
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case WORKPLACE_BYSUPPORT:
			return Optional.ofNullable(workplace);
		case WORKGROUP:
			return Optional.ofNullable(workOpt == null || !workOpt.isPresent() ? null : workOpt.get());
		case SUPP:
			return Optional.ofNullable(workSuppInfo == null || !workSuppInfo.isPresent() ? null : workSuppInfo.get());
		default:
			return Optional.empty();
		}
	}

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case WORKPLACE_BYSUPPORT:
		case WORKGROUP:
			return PropType.OBJECT;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}

}
