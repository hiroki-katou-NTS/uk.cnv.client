package nts.uk.screen.com.app.find.cmm051;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@Getter
public class PersonDto {
    /**
     * The pid.
     */
    private String pid;

    /**
     * The business name.
     */
    private String businessName;

    /**
     * The gender.
     */
    private int gender;

    /**
     * The birth day.
     */
    private GeneralDate birthDay;
}
