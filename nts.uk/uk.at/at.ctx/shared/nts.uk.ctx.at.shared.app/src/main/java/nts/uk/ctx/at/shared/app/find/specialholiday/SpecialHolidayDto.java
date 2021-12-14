package nts.uk.ctx.at.shared.app.find.specialholiday;

import java.util.Collections;

import lombok.Value;
import nts.uk.ctx.at.shared.app.find.specialholiday.grantcondition.SpecialLeaveRestrictionDto;
import nts.uk.ctx.at.shared.app.find.specialholiday.grantinformation.GrantRegularDto;
import nts.uk.ctx.at.shared.app.find.specialholiday.periodinformation.GrantPeriodicDto;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;

@Value
public class SpecialHolidayDto {
	
	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private int specialHolidayCode;

	/** 特別休暇名称 */
	private String specialHolidayName;

	/** 付与・期限情報 */
	private GrantRegularDto grantRegularDto; 

	/** 特別休暇利用条件 */
	private SpecialLeaveRestrictionDto specialLeaveRestrictionDto;

	/** 対象項目 */
	private TargetItemDto targetItemDto;

	/**自動付与区分 */
	private int autoGrant;
	
	/** 連続で取得する */
	private int continuousAcquisition;

	/** メモ */
	private String memo;

	public static SpecialHolidayDto fromDomain(SpecialHoliday specialHoliday) {
		if(specialHoliday == null) {
			return null;
		}

		GrantRegularDto grantRegular = GrantRegularDto.fromDomain(specialHoliday.getGrantRegular() != null ? specialHoliday.getGrantRegular() : null);

		SpecialLeaveRestrictionDto specialLeaveRestriction = SpecialLeaveRestrictionDto
				.fromDomain(specialHoliday.getSpecialLeaveRestriction() != null ? specialHoliday.getSpecialLeaveRestriction() : null);

		TargetItemDto targetItem = TargetItemDto.fromDomain(specialHoliday.getTargetItem() != null ? specialHoliday.getTargetItem() : null);

		return new SpecialHolidayDto(
				specialHoliday.getCompanyId(),
				specialHoliday.getSpecialHolidayCode().v(),
				specialHoliday.getSpecialHolidayName().v(),
				grantRegular,
				specialLeaveRestriction,
				targetItem,
				specialHoliday.getAutoGrant().value,
				specialHoliday.getContinuousAcquisition().value,
				specialHoliday.getMemo().v()
		);
	}

	public TargetItemDto getTargetItemDto() {
		return targetItemDto != null ? targetItemDto : new TargetItemDto(Collections.emptyList(), Collections.emptyList());
	}
}
