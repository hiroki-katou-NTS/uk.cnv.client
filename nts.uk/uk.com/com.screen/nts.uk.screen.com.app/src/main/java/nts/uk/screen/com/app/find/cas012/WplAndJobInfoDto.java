package nts.uk.screen.com.app.find.cas012;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WplAndJobInfoDto {

    private String cid;
    /**
     * The employee id.
     */
    // 社員ID
    private String employeeId;

    /**
     * The workplace id.
     */
    // 職場ID
    private String workplaceId;

    /**
     * The workplace code.
     */
    private String workplaceCode;

    /**
     * The workplace name.
     */
    private String workplaceName;

    /**
     * The wkp display name.
     */
    // 職場表示名
    private String wkpDisplayName;
    // 職位ID
    private String jobTitleId;

    private String JobTitleCode;

    private String JobTitleName;

}
