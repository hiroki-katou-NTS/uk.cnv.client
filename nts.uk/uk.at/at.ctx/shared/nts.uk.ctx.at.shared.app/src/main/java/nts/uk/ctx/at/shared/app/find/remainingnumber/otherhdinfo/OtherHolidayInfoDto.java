package nts.uk.ctx.at.shared.app.find.remainingnumber.otherhdinfo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtherHolidayInfoDto extends PeregDomainDto{
	
	// 代休残数
	@PeregItem("IS00366")
	private BigDecimal remainNumber;
	
	// 振休残数
	@PeregItem("IS00368")
	private BigDecimal remainsLeft;
	
	// 公休残数
	@PeregItem("IS00369")
	private BigDecimal pubHdremainNumber;
	
	// 60H超休管理
	@PeregItem("IS00370")
	private int useAtr;
	
	// 発生単位
	@PeregItem("IS00371")
	private int occurrenceUnit;
	
	// 精算方法
	@PeregItem("IS00372")
	private int paymentMethod;
		
	// 60H超休残数
	@PeregItem("IS00374")
	private String extraHours;
	
	public static OtherHolidayInfoDto createFromDomain(Optional<PublicHolidayCarryForwardData> pubHDRemain, Optional<ExcessLeaveInfo> exLeavInfo){
		OtherHolidayInfoDto dto = new OtherHolidayInfoDto();
		if (pubHDRemain.isPresent()){
			dto.pubHdremainNumber = BigDecimal.valueOf(pubHDRemain.get().getNumberCarriedForward().v());
			dto.setRecordId(pubHDRemain.get().getEmployeeId());
		}
		if (exLeavInfo.isPresent()){
			dto.useAtr = exLeavInfo.get().getUseAtr().value;
			dto.occurrenceUnit = exLeavInfo.get().getOccurrenceUnit().v();
			dto.paymentMethod = exLeavInfo.get().getPaymentMethod().value;
			if (StringUtils.isEmpty(dto.getRecordId())){
				dto.setRecordId(exLeavInfo.get().getSID());
			}
		}
		return dto;
	}
	
	public static OtherHolidayInfoDto createFromDomainCPS013(Optional<PublicHolidayCarryForwardData> publicHolidayCarryForwardData, Optional<ExcessLeaveInfo> exLeavInfo, Map<String, Object> enums){
		OtherHolidayInfoDto dto = new OtherHolidayInfoDto();
		if (publicHolidayCarryForwardData.isPresent()){
			dto.pubHdremainNumber = BigDecimal.valueOf(publicHolidayCarryForwardData.get().getNumberCarriedForward().v());
			dto.setRecordId(publicHolidayCarryForwardData.get().getEmployeeId());
		}
		if (exLeavInfo.isPresent()){
			
			Integer useAtr = (Integer)enums.get("IS00370");
			Integer paymentMethod = (Integer)enums.get("IS00372");
			
			dto.useAtr = useAtr;
			dto.occurrenceUnit = exLeavInfo.get().getOccurrenceUnit().v();
			dto.paymentMethod = paymentMethod;
			if (StringUtils.isEmpty(dto.getRecordId())){
				dto.setRecordId(exLeavInfo.get().getSID());
			}
		}
		return dto;
	}
	
	public static String convertTime(int remainingMinutes) {
		int remainingHours = remainingMinutes / 60;
		remainingMinutes -= remainingHours * 60;
		return remainingHours + ":"+ convertWithMinutes(remainingMinutes);
	}
	
	public  static String convertWithMinutes(int minutes) {
		if ( Math.abs(minutes) < 10) {
			return "0" + Math.abs(minutes);
		}
		return "" +  Math.abs(minutes);
	}

}
