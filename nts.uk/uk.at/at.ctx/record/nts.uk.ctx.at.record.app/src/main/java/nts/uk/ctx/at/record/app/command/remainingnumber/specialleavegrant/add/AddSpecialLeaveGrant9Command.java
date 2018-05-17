package nts.uk.ctx.at.record.app.command.remainingnumber.specialleavegrant.add;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;

@Getter
public class AddSpecialLeaveGrant9Command {

	private String specialid;
	private String cid;
	private int specialLeaCode;
	
	@PeregEmployeeId
	private String sid;

	// 付与日
	@PeregItem("IS00529")
	private GeneralDate grantDate;

	// 期限日
	@PeregItem("IS00530")
	private GeneralDate deadlineDate;

	// 期限切れ状態
	@PeregItem("IS00531")
	private BigDecimal expStatus;

	// 使用状況
	@PeregItem("IS00532")
	private BigDecimal registerType;

	// 付与日数
	@PeregItem("IS00534")
	private BigDecimal numberDayGrant;

	// 付与時間
	@PeregItem("IS00535")
	private BigDecimal timeGrant;

	// 使用日数
	@PeregItem("IS00537")
	private BigDecimal numberDayUse;

	// 使用時間
	@PeregItem("IS00538")
	private BigDecimal timeUse;

	//
	private double useSavingDays;

	// 上限超過消滅日数
	@PeregItem("IS00539")
	private BigDecimal numberDaysOver;

	// 上限超過消滅時間
	@PeregItem("IS00540")
	private BigDecimal timeOver;

	// 残日数
	@PeregItem("IS00542")
	private BigDecimal numberDayRemain;

	// 残時間
	@PeregItem("IS00543")
	private BigDecimal timeRemain;
}
