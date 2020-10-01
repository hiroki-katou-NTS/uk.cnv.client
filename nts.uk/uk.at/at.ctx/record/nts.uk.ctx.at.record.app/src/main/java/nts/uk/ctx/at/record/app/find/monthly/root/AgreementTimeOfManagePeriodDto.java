package nts.uk.ctx.at.record.app.find.monthly.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.record.app.find.monthly.root.dto.AgreementTimeBreakdownDto;
import nts.uk.ctx.at.record.app.find.monthly.root.dto.AgreementTimeOfMonthlyDto;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.attendance.util.ItemConst;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeBreakdown;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
/** 管理期間の36協定時間 */
@AttendanceItemRoot(rootName = ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME, itemType = AttendanceItemType.MONTHLY_ITEM)
public class AgreementTimeOfManagePeriodDto extends MonthlyItemCommon{

	/***/
	private static final long serialVersionUID = 1L;
	
	/** 社員ID */
	private String employeeId;
	/** 月度 */
	private YearMonth yearMonth;
	
	/** 36協定対象時間  */
	@AttendanceItemLayout(jpPropertyName = AGREEMENT, layout = LAYOUT_A)
	private AgreementTimeOfMonthlyDto agreementTime;
	
	/** 内訳 */
	@AttendanceItemLayout(jpPropertyName = BREAK_DOWN, layout = LAYOUT_B)
	private AgreementTimeBreakdownDto breakdown;

	/** 法定上限対象時間 */
	@AttendanceItemLayout(jpPropertyName = UPPER_LIMIT + AGREEMENT, layout = LAYOUT_C)
	private AgreementTimeOfMonthlyDto agreMax;
	
	/** 36協定上限時間管理 -> 内訳 */
	@AttendanceItemLayout(jpPropertyName = UPPER_LIMIT + BREAK_DOWN, layout = LAYOUT_D)
	private AgreementTimeBreakdownDto maxBreakdown;


	/** アラーム時間: ３６協定１ヶ月時間 */
	@AttendanceItemValue(type = ValueType.ATTR)
	@AttendanceItemLayout(jpPropertyName = STATE, layout = LAYOUT_C)
	private int state;

	@Override
	public String employeeId() {
		return employeeId;
	}

	@Override
	public AgreementTimeOfManagePeriod toDomain(String employeeId, YearMonth ym, int closureID, ClosureDateDto closureDate) {
		return AgreementTimeOfManagePeriod.of(employeeId, ym,
						agreementTime == null ? new AgreementTimeOfMonthly() : agreementTime.toDomain(), 
						agreMax == null ? new AgreementTimeOfMonthly() : agreMax.toDomain(),
						breakdown == null ? new AgreementTimeBreakdown() : breakdown.toDomain(),
						EnumAdaptor.valueOf(state, AgreementTimeStatusOfMonthly.class));
	}

	@Override
	public YearMonth yearMonth() {
		return yearMonth;
	}

	@Override
	public int getClosureID() {
		return 1;
	}

	@Override
	public ClosureDateDto getClosureDate() {
		return null;
	}
	
	public static AgreementTimeOfManagePeriodDto from(AgreementTimeOfManagePeriod domain){
		AgreementTimeOfManagePeriodDto dto = new AgreementTimeOfManagePeriodDto();
		if(domain != null){
			dto.setEmployeeId(domain.getSid());
			dto.setAgreementTime(AgreementTimeOfMonthlyDto.from(domain.getAgreementTime()));
			dto.setBreakdown(AgreementTimeBreakdownDto.from(domain.getBreakdown()));
			dto.setAgreMax(AgreementTimeOfMonthlyDto.from(domain.getLegalMaxTime()));
			dto.setState(domain.getStatus().value);
			dto.setYearMonth(domain.getYm());
			dto.exsistData();
		}
		return dto;
	}
}
