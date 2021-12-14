package nts.uk.file.at.ws.schedule.export.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;

@Getter
@Setter
@AllArgsConstructor
public class PublicHolidayDto {

    private String companyId;

    private String date;

    private String holidayName;
    
    public static PublicHolidayDto fromDomain(PublicHoliday domain) {
        return new PublicHolidayDto(
                domain.getCompanyId(), 
                domain.getDate().toString("yyyy/MM/dd"), 
                domain.getHolidayName().v());
    }
}
