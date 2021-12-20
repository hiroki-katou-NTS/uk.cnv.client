package nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo;

import java.util.List;

/**
 * 社員所属チーム情報Publish
 *
 * @author rafiqul.islam
 */
public interface EmpRankInfoPub {
    /**
     * @param List<社員ID>
     * @return 	List<社員ランク情報Exported>
     */
    List<EmpRankInfoExport> get(List<String> listEmpId);

}