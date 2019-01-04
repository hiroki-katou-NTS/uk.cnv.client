package nts.uk.ctx.pr.core.dom.wageprovision.statebindingset;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.pr.core.dom.wageprovision.statebindingset.service.StateCorreHisEmAndLinkSetMaster;
import nts.uk.shr.com.history.YearMonthHistoryItem;

import java.util.List;
import java.util.Optional;

/**
* 明細書紐付け履歴（雇用）
*/
public interface StateCorreHisEmRepository {

    Optional<StateCorreHisEm> getStateCorrelationHisEmployeeById(String cid, String hisId);

    Optional<StateCorreHisEm> getStateCorrelationHisEmployeeById(String cid);

    List<StateLinkSetMaster> getStateLinkSettingMasterByHisId(String cid, String hisId);

    Optional<StateLinkSetMaster> getStateLinkSettingMasterById(String cid, String hisId, String masterCode);

    List<StateLinkSetMaster> getStateLinkSetMaster(String cid, GeneralDate date);

    void update (String cid, YearMonthHistoryItem history);

    void updateAll(String cid, List<StateLinkSetMaster> stateLinkSetMasters, int startYearMonth, int endYearMonth);

    void addAll(String cid, List<StateLinkSetMaster> stateLinkSetMasters, int startYearMonth, int endYearMonth );

    void removeAll(String cid, String hisId);
}
