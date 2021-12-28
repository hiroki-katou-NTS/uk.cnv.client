package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

import java.util.Optional;
import java.util.List;

/**
* 日別実績の修正の機能
*/
public interface DaiPerformanceFunRepository
{

    List<DaiPerformanceFun> getAllDaiPerformanceFun();

    Optional<DaiPerformanceFun> getDaiPerformanceFunById(String cid);

}
