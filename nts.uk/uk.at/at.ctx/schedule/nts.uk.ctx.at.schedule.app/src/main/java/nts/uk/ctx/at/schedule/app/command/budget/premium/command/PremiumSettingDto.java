package nts.uk.ctx.at.schedule.app.command.budget.premium.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PremiumSettingDto {
    private int iD;
    private String name;
    private Integer useAtr;
    private Integer rate;
    private int unitPrice;
    private List<Integer> attendanceItems;

    public List<String> getAttendanceItems() {
        if (attendanceItems == null || attendanceItems.size() <= 0){
            return new ArrayList<>();
        }
        return attendanceItems.stream().map(j->j.toString()).collect(Collectors.toList());
    }

    public void setAttendanceItems(List<Integer> attendanceItems) {
        this.attendanceItems = attendanceItems;
    }

    private List<AttendanceNamePriniumDto> attendanceNames;
}
