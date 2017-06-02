package nts.uk.ctx.at.record.app.find.stamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.StampItem;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StampDto {
	private String cardNumber;
	private int attendanceTime;
	private int stampCombinationAtr;
	private String stampCombinationName;
	private String workTimeCd;
	private int stampMethod;
	private String stampMethodName;
	private int stampAtr;
	private String stampAtrName;
	private String workLocationCd;
	private int stampReason;
	private String stampReasonName;
	private GeneralDate date;

	public static StampDto fromDomain(StampItem domain) {
		return new StampDto(domain.getCardNumber().v(), 
				domain.getAttendanceTime().v(), 
				domain.getStampCombinationAtr().value,
				domain.getStampCombinationAtr().name,
				domain.getWorkTimeCd().v(), 
				domain.getStampMethod().value,
				domain.getStampMethod().name,
				domain.getStampAtr().value,
				domain.getStampAtr().name,
				domain.getWorkLocationCd().v(), 
				domain.getStampReason().value,
				domain.getStampReason().name,
				domain.getDate());
	}
}
