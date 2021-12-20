package nts.uk.screen.com.app.find.cmm051;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;

@Data
public class WorkplaceManagerDto {
    /** ID */
    private  String workplaceManagerId;

    /** 職場ID */
    private  String workplaceId;

    /** 社員ID */
    private  String employeeId;

    private GeneralDate startDate;

    private GeneralDate endDate;

    public WorkplaceManagerDto(WorkplaceManager domain){
        this.employeeId = domain.getEmployeeId();
        this.endDate = domain.getHistoryPeriod().end();
        this.startDate = domain.getHistoryPeriod().start();
        this.workplaceId = domain.getWorkplaceId();
        this.workplaceManagerId = domain.getWorkplaceManagerId();
    }
}
