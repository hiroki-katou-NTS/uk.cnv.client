package nts.uk.ctx.at.function.ac.alarm;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.alarm.AffAtWorkplaceExport;
import nts.uk.ctx.at.function.dom.adapter.alarm.EmployeeWorkplaceAdapter;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <<Adapter>> 社員ID（List）と基準日から所属職場IDを取得
 *
 * @author viet.tx
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmployeeWorkplaceAdapterImpl implements EmployeeWorkplaceAdapter {
    @Inject
    private WorkplacePub workplacePub;

    @Override
    public List<AffAtWorkplaceExport> getWorkplaceId(List<String> sIds, GeneralDate baseDate) {
        // ＄所属職場 ＝ RQ.社員ID（List）と基準日から所属職場IDを取得(社員IDリスト、基準日) //List<所属職場履歴項目>
        return this.workplacePub.findBySIdAndBaseDate(sIds, baseDate).stream()
                .filter(value -> sIds.contains(value.getEmployeeId()))
                .map(x -> new AffAtWorkplaceExport(x.getEmployeeId(), x.getWorkplaceId(), x.getHistoryID()))
                .collect(Collectors.toList());
    }
}
