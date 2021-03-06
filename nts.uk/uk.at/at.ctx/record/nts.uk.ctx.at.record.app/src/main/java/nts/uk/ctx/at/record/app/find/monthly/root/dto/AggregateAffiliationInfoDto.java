package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.JobTitleId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AggregateAffiliationInfo;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

@Data
/** 集計所属情報 */
@NoArgsConstructor
@AllArgsConstructor
public class AggregateAffiliationInfoDto implements ItemConst, AttendanceItemDataGate {

	/** 分類コード: 分類コード */
	@AttendanceItemValue
	@AttendanceItemLayout(jpPropertyName = CLASSIFICATION, layout = LAYOUT_A)
	private String classificationCode;

	/** 勤務種別コード: 勤務種別コード */
	@AttendanceItemValue
	@AttendanceItemLayout(jpPropertyName = BUSINESS_TYPE, layout = LAYOUT_B)
	private String businessTypeCode;

	/** 職位ID: 職位ID */
	@AttendanceItemValue
	@AttendanceItemLayout(jpPropertyName = JOB_TITLE, layout = LAYOUT_C)
	private String jobTitle;

	/** 職場ID: 職場ID (work place ID) */
	@AttendanceItemValue
	@AttendanceItemLayout(jpPropertyName = WORKPLACE, layout = LAYOUT_D)
	private String workPlaceCode;

	/** 雇用コード: 雇用コード */
	@AttendanceItemValue
	@AttendanceItemLayout(jpPropertyName = EMPLOYEMENT, layout = LAYOUT_E)
	private String employmentCode;

	public static AggregateAffiliationInfoDto from(AggregateAffiliationInfo domain) {
		AggregateAffiliationInfoDto dto = new AggregateAffiliationInfoDto();
		if (domain != null) {
			dto.setClassificationCode(domain.getClassCd() == null ? null : domain.getClassCd().v());
			dto.setEmploymentCode(domain.getEmploymentCd() == null ? null : domain.getEmploymentCd().v());
			dto.setJobTitle(domain.getJobTitleId() == null ? null : domain.getJobTitleId().v());
			dto.setWorkPlaceCode(domain.getWorkplaceId() == null ? null : domain.getWorkplaceId().v());
			dto.setBusinessTypeCode(domain.getBusinessTypeCd() == null 
					? null : domain.getBusinessTypeCd().map(c -> c.v()).orElse(null));
		}
		return dto;
	}

	public AggregateAffiliationInfo toDomain() {
		return AggregateAffiliationInfo.of(new EmploymentCode(employmentCode),
				new WorkplaceId(workPlaceCode),
				new JobTitleId(jobTitle),
				new ClassificationCode(classificationCode),
				Optional.ofNullable(businessTypeCode == null ? null : new BusinessTypeCode(businessTypeCode)));
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case EMPLOYEMENT:
			return Optional.of(ItemValue.builder().value(employmentCode).valueType(ValueType.CODE));
		case JOB_TITLE:
			return Optional.of(ItemValue.builder().value(jobTitle).valueType(ValueType.CODE));
		case WORKPLACE:
			return Optional.of(ItemValue.builder().value(workPlaceCode).valueType(ValueType.CODE));
		case CLASSIFICATION:
			return Optional.of(ItemValue.builder().value(classificationCode).valueType(ValueType.CODE));
		case BUSINESS_TYPE:
			return Optional.of(ItemValue.builder().value(businessTypeCode).valueType(ValueType.CODE));
		default:
			return Optional.empty();
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case EMPLOYEMENT:
			this.employmentCode = value.valueOrDefault(null);
			break;
		case JOB_TITLE:
			this.jobTitle = value.valueOrDefault(null);
			break;
		case WORKPLACE:
			this.workPlaceCode = value.valueOrDefault(null);
			break;
		case CLASSIFICATION:
			this.classificationCode = value.valueOrDefault(null);
			break;
		case BUSINESS_TYPE:
			this.businessTypeCode = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case EMPLOYEMENT:
		case JOB_TITLE:
		case WORKPLACE:
		case CLASSIFICATION:
		case BUSINESS_TYPE:
			return PropType.VALUE;
		default:
			break;
		}
		return PropType.OBJECT;
	}
}
