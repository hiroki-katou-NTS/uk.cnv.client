package nts.uk.ctx.at.shared.app.find.specialholiday;

import java.util.Collections;
import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.at.shared.app.find.specialholiday.grantcondition.SpecialLeaveRestrictionDto;
import nts.uk.ctx.at.shared.app.find.specialholiday.grantinformation.GrantRegularDto;
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
		GrantRegularDto grantRegular = null;
		if(specialHoliday.getGrantRegular()!=null) {
			grantRegular= GrantRegularDto.fromDomain(specialHoliday.getGrantRegular());
		}
		SpecialLeaveRestrictionDto specialLeaveRestriction = null;
		if(specialHoliday.getSpecialLeaveRestriction() != null){
			 specialLeaveRestriction = SpecialLeaveRestrictionDto
					.fromDomain(specialHoliday.getSpecialLeaveRestriction());
		}
		TargetItemDto targetItem = null;
		if(specialHoliday.getTargetItem()!=null){
			targetItem	= TargetItemDto.fromDomain(specialHoliday.getTargetItem());
		}
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
