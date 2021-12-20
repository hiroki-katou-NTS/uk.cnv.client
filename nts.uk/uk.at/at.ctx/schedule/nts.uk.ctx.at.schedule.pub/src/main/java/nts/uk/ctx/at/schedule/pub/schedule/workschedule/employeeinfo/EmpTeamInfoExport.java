package nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.arc.layer.dom.DomainObject;

/**
 * [C-0] 社員所属チーム情報Export(社員ID, チームコード, チーム名称)
 * 	社員所属チーム情報Export
 * @author rafiqul.islam
 */
@Getter
@AllArgsConstructor
public class EmpTeamInfoExport extends DomainObject {
    /**
     * 社員ID
     **/
    private String employeeID;

    /**
     * チームコード
     **/
    private String scheduleTeamCd;

    /**
     * チーム名称
     **/
    private String scheduleTeamName;
}