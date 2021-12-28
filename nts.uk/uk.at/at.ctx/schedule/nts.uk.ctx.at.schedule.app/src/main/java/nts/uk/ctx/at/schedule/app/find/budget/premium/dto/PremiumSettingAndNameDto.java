package nts.uk.ctx.at.schedule.app.find.budget.premium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumDto;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PremiumSettingAndNameDto {
    private int iD;
    private String name;
    private Integer rate;
    private Integer useAtr;
    private int unitPrice;

    private List<AttendanceNamePriniumDto> attendanceItems;
}
