package nts.uk.ctx.bs.employee.pub.workplace.master;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkplaceInforExport {
    private String workplaceId;
    private String hierarchyCode;
    private String workplaceCode;
    private String workplaceName;
    private String workplaceDisplayName;
    private String workplaceGenericName;
    private String workplaceExternalCode;
}
