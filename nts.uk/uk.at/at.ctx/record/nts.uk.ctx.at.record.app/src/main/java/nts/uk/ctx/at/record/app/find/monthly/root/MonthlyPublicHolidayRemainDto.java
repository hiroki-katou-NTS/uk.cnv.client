package nts.uk.ctx.at.record.app.find.monthly.root;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 公休月別残数データ
 * @author hayata_maekawa
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttendanceItemRoot(rootName = ItemConst.MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME, itemType = AttendanceItemType.MONTHLY_ITEM)
public class MonthlyPublicHolidayRemainDto  extends MonthlyItemCommon  implements ItemConst, AttendanceItemDataGate  {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 社員ID: 社員ID */
	private String employeeId;

	/** 年月: 年月 */
	private YearMonth ym;

	/** 締めID: 締めID */
	private int closureID = 1;

	/** 締め日: 日付 */
	private ClosureDateDto closureDate;

	private ClosureStatus closureStatus = ClosureStatus.UNTREATED;
	/** 公休日数 */
	private Double grantDays;
	/** 繰越数 */
	private Double carryforwardDays;
	/** 取得数 */
	private Double usedDays;
	/** 翌月繰越数 */
	private Double nextmonthCarryforwardDays;
	/** 未消化数 */
	private Double unusedDays;
	
	
	/**
	 * ドメインへ変換
	 */
	public PublicHolidayRemNumEachMonth toDomain(String employeeId, YearMonth ym, int closureID, ClosureDateDto closureDate) {
		return new PublicHolidayRemNumEachMonth(
				employeeId, /** 社員ID */
				ym, /** 年月 */
				ConvertHelper.getEnum(closureID, ClosureId.class), /** 締めID */
				new ClosureDate(closureDate.getClosureDay(), closureDate.getLastDayOfMonth()), /** 締め日付 */
				ClosureStatus.UNTREATED, // 締め処理状態←未締め
				toLeaveGrantDayNumber(grantDays),
				toLeaveRemainingDayNumber(carryforwardDays),
				toLeaveUsedDayNumber(usedDays),
				toLeaveRemainingDayNumber(nextmonthCarryforwardDays),
				toLeaveRemainingDayNumber(unusedDays)
				);
	}
	
	public LeaveGrantDayNumber toLeaveGrantDayNumber(Double number){
		return new LeaveGrantDayNumber(number == null ? 0.0 : number);
	}
	
	public LeaveRemainingDayNumber toLeaveRemainingDayNumber(Double number){
		return new LeaveRemainingDayNumber(number == null ? 0.0 : number);
	}
	
	public LeaveUsedDayNumber toLeaveUsedDayNumber(Double number){
		return new LeaveUsedDayNumber(number == null ? 0.0 : number);
	}
	
	
	public static MonthlyPublicHolidayRemainDto from(PublicHolidayRemNumEachMonth domain){
		MonthlyPublicHolidayRemainDto dto = new MonthlyPublicHolidayRemainDto();
		
		if (domain != null) {
			dto.setEmployeeId(domain.getEmployeeId());
			dto.setYm(domain.getYearMonth());
			dto.setClosureID(domain.getClosureId().value);
			dto.setClosureStatus(domain.getClosureStatus());
			dto.setClosureDate(new ClosureDateDto(domain.getClosureDate().getClosureDay().v(), domain.getClosureDate().getLastDayOfMonth()));
			dto.setGrantDays(domain.getPublicHolidayday().v());
			dto.setCarryforwardDays(domain.getCarryForwardNumber().v());
			dto.setUsedDays(domain.getNumberOfAcquisitions().v());
			dto.setNextmonthCarryforwardDays(domain.getNumberCarriedOverToTheNextMonth().v());
			dto.setUnusedDays(domain.getUnDegestionNumber().v());
		}
		return dto;
	}

	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case GRANT:
			return Optional.of(ItemValue.builder().value(grantDays).valueType(ValueType.DAYS));
		case CARRIED_FORWARD:
			return Optional.of(ItemValue.builder().value(carryforwardDays).valueType(ValueType.DAYS));
		case USAGE:
			return Optional.of(ItemValue.builder().value(usedDays).valueType(ValueType.DAYS));
		case NEXT_MONTH + CARRIED_FORWARD:
			return Optional.of(ItemValue.builder().value(nextmonthCarryforwardDays).valueType(ValueType.DAYS));
		case NOT_DIGESTION:
			return Optional.of(ItemValue.builder().value(unusedDays).valueType(ValueType.DAYS));
			
		default:
			return AttendanceItemDataGate.super.valueOf(path);
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case GRANT:
		case CARRIED_FORWARD:
		case USAGE:
		case NEXT_MONTH + CARRIED_FORWARD:
		case NOT_DIGESTION:
			return PropType.VALUE;
		default:
			return AttendanceItemDataGate.super.typeOf(path);
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case GRANT:
			this.grantDays = value.valueOrDefault(0d); return;
		case CARRIED_FORWARD:
			this.carryforwardDays = value.valueOrDefault(0d); return;
		case USAGE:	
			this.usedDays = value.valueOrDefault(0d); return;
		case NEXT_MONTH + CARRIED_FORWARD:
			this.nextmonthCarryforwardDays = value.valueOrDefault(0d); return;
		case NOT_DIGESTION:
			this.unusedDays = value.valueOrDefault(0d); return;
		default:
		}
	}

	@Override
	public String employeeId() {
		return employeeId;
	}

	@Override
	public YearMonth yearMonth() {
		return ym;
	}


}
