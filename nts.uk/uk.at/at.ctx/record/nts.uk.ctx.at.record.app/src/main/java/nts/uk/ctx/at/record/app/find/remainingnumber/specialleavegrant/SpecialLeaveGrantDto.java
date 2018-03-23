package nts.uk.ctx.at.record.app.find.remainingnumber.specialleavegrant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialLeaveGrantDto {
	
	/**
	 * 
	 */
	private String specialid;

	/**
	 * 
	 */
	private String sid;
	
	/**
	 * 
	 */
	private int specialLeaCode;
	/**
	 * 付与日
	 */
	private GeneralDate grantDate;

	/**
	 * 期限日
	 */
	private GeneralDate deadlineDate;

	/**
	 * 期限切れ状態
	 */
	private int expStatus;

	/**
	 * 期限切れ状態
	 */
	private int registerType;

	/**
	 * 付与日数
	 */
	private int numberDayGrant;

	/**
	 * 付与時間
	 */
	private int timeGrant;

	/**
	 * 使用日数
	 */
	private double numberDayUse;

	/**
	 * 使用時間
	 */
	private int timeUse;
	
	/**
	 * 
	 */
	private double useSavingDays;

	/**
	 * 上限超過消滅日数
	 */
	private int numberDaysOver;
	/**
	 * 上限超過消滅時間
	 */
	private int timeOver;

	/**
	 * 残日数
	 */
	private double numberDayRemain;

	/**
	 * 残時間
	 */
	private int timeRemain;

	public static SpecialLeaveGrantDto createFromDomain(SpecialLeaveGrantRemainingData domain) {
		SpecialLeaveGrantDto dto = new SpecialLeaveGrantDto();
		dto.specialid = domain.getSpecialId();
		dto.sid = domain.getEmployeeId();
		dto.specialLeaCode = domain.getSpecialLeaveCode().v();
		dto.grantDate = domain.getGrantDate();
		dto.deadlineDate = domain.getDeadlineDate();
		dto.expStatus = domain.getExpirationStatus().value;
		dto.registerType = domain.getRegisterType().value;
		dto.numberDayGrant = domain.getDetails().getGrantNumber().getDayNumberOfGrant().v();
		dto.timeGrant = domain.getDetails().getGrantNumber().getTimeOfGrant().isPresent()
				? domain.getDetails().getGrantNumber().getTimeOfGrant().get().v()
				: null;
		dto.numberDayRemain = domain.getDetails().getRemainingNumber().getDayNumberOfRemain().v();
		dto.timeRemain = domain.getDetails().getRemainingNumber().getTimeOfRemain().isPresent()
				? domain.getDetails().getRemainingNumber().getTimeOfRemain().get().v()
				: null;
		dto.numberDayUse = domain.getDetails().getUsedNumber().getDayNumberOfUse().v();
		dto.timeUse = domain.getDetails().getUsedNumber().getTimeOfUse().isPresent()
				? domain.getDetails().getUsedNumber().getTimeOfUse().get().v()
				: null;
		dto.useSavingDays = domain.getDetails().getUsedNumber().getUseSavingDays().isPresent()
				? domain.getDetails().getUsedNumber().getUseSavingDays().get().v()
				: null;
		dto.numberDaysOver = domain.getDetails().getUsedNumber().getSpecialLeaveOverLimitNumber().isPresent()
				? domain.getDetails().getUsedNumber().getSpecialLeaveOverLimitNumber().get().getNumberOverDays().v()
				: null;
		dto.timeOver = (domain.getDetails().getUsedNumber().getSpecialLeaveOverLimitNumber().isPresent() && domain
				.getDetails().getUsedNumber().getSpecialLeaveOverLimitNumber().get().getTimeOver().isPresent())
						? domain.getDetails().getUsedNumber().getSpecialLeaveOverLimitNumber().get().getTimeOver()
								.get().v()
						: null;
		return dto;

	}

}
