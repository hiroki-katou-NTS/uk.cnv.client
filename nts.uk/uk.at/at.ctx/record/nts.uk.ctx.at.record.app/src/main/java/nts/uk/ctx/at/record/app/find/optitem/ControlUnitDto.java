package nts.uk.ctx.at.record.app.find.optitem;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItems;

@AllArgsConstructor
@Getter
public class ControlUnitDto {
    private String companyID;
    
    private int itemID;
    
    private String headerBgColorOfMonthlyPer;
    
    public static ControlUnitDto fromMonthlyDomain(ControlOfMonthlyItems monthlyItem) {
        return new ControlUnitDto(
                monthlyItem.getCompanyId(),
                monthlyItem.getItemMonthlyId(), 
                monthlyItem.getHeaderBgColorOfMonthlyPer().isPresent() ? monthlyItem.getHeaderBgColorOfMonthlyPer().get().v() : null
        );
    }
    
    public static ControlUnitDto fromDailyDomain(ControlOfAttendanceItems dailyItem) {
        return new ControlUnitDto(
                dailyItem.getCompanyID(),
                dailyItem.getItemDailyID(),
                dailyItem.getHeaderBgColorOfDailyPer().isPresent() ? dailyItem.getHeaderBgColorOfDailyPer().get().v() : null
        );
    }
}
