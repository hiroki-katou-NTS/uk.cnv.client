package nts.uk.screen.com.app.find.cas012;

import lombok.Value;

@Value
public class EmployeeBasicInfoDto {

    public String personId;

    public String userId;

    public String employeeId;

    public String employeeCode;

    public String businessName;

    /** The job title id. */
    // 職位ID
    private String jobTitleID;

    private String jobTitleCode;

    /** The job title name. */
    // 職位名称
    private String jobTitleName;
    // 職場ID
    private String workplaceId;

    /** The workplace code. */
    private String workplaceCode;

    /** The workplace name. */
    private String workplaceName;

    /** The wkp display name. */
    // 職場表示名
    private String wkpDisplayName;



}
