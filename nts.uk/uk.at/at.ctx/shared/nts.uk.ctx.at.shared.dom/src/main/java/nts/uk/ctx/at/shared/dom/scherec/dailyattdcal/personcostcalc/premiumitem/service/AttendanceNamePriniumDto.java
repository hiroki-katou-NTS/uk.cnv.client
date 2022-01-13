package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
@AllArgsConstructor
@Getter
public class AttendanceNamePriniumDto {
	private int attendanceItemId;

	private String attendanceItemName;

	private int attendanceItemDisplayNumber;
}
