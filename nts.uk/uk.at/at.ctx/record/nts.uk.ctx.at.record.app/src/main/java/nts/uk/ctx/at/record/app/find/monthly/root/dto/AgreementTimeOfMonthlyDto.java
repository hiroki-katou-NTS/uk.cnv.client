package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** 月別実績の36協定時間 */
public class AgreementTimeOfMonthlyDto implements ItemConst, AttendanceItemDataGate {

	/** 36協定時間: 勤怠月間時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = AGREEMENT, layout = LAYOUT_A)
	private int agreementTime;

	/** エラー時間 : ３６協定１ヶ月時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = LIMIT_ALARM, layout = LAYOUT_B)
	private int alarmTime;

	/** アラーム時間: ３６協定１ヶ月時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = LIMIT_ERROR, layout = LAYOUT_C)
	private int errorTime;

	/** 上限時間: ３６協定１ヶ月時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = UPPER_LIMIT, layout = LAYOUT_E)
	private Integer exceptionLimitTime;

	public AgreementTimeOfMonthly toDomain() {
		return AgreementTimeOfMonthly.of(new AttendanceTimeMonth(agreementTime),
										OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(
															new AgreementOneMonthTime(errorTime), 
															new AgreementOneMonthTime(alarmTime)), 
														new AgreementOneMonthTime(exceptionLimitTime)));
	}
	
	public static AgreementTimeOfMonthlyDto from(AgreementTimeOfMonthly domain) {
		AgreementTimeOfMonthlyDto dto = new AgreementTimeOfMonthlyDto();
		if(domain != null) {
			dto.setAgreementTime(domain.getAgreementTime() == null ? 0 : domain.getAgreementTime().valueAsMinutes());
			dto.setAlarmTime(domain.getThreshold().getErAlTime().getAlarm().valueAsMinutes());
			dto.setErrorTime(domain.getThreshold().getErAlTime().getError().valueAsMinutes());
			dto.setExceptionLimitTime(domain.getThreshold().getUpperLimit().v());
		}
		return dto;
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case AGREEMENT:
			return Optional.of(ItemValue.builder().value(agreementTime).valueType(ValueType.TIME));
		case LIMIT_ALARM:
			return Optional.of(ItemValue.builder().value(alarmTime).valueType(ValueType.TIME));
		case LIMIT_ERROR:
			return Optional.of(ItemValue.builder().value(errorTime).valueType(ValueType.TIME));
		case UPPER_LIMIT:
			return Optional.of(ItemValue.builder().value(exceptionLimitTime).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case AGREEMENT:
		case LIMIT_ALARM:
		case LIMIT_ERROR:
		case UPPER_LIMIT:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case AGREEMENT:
			agreementTime = value.valueOrDefault(0);
			break;
		case LIMIT_ALARM:
			alarmTime = value.valueOrDefault(0);
			break;
		case LIMIT_ERROR:
			errorTime = value.valueOrDefault(0);
			break;
		case UPPER_LIMIT:
			exceptionLimitTime = value.valueOrDefault(null);
			break;
		default:
		}
	}
}
