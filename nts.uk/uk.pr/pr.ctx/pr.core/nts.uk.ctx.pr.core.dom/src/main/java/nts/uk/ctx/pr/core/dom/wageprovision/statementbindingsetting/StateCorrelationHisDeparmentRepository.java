package nts.uk.ctx.pr.core.dom.wageprovision.statementbindingsetting;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.history.YearMonthHistoryItem;

import java.util.List;
import java.util.Optional;

/**
* 明細書紐付け履歴（部門）
*/
public interface StateCorrelationHisDeparmentRepository {

    Optional<StateCorrelationHisDeparment> getStateCorrelationHisDeparmentById(String cid);

    Optional<StateCorrelationHisDeparment> getStateCorrelationHisDeparmentByDate(String cid,GeneralDate baseDate);

    Optional<StateLinkSettingDate> getStateLinkSettingDateById(String cId, String hisId);

    Optional<StateLinkSettingMaster> getStateLinkSettingMasterById(String cid, String hisId, String masterCode);

    void update (String cid, YearMonthHistoryItem history);

    void updateAll(String cid, List<StateLinkSettingMaster> stateLinkSettingMasters, int startYearMonth, int endYearMonth, GeneralDate baseDate);

    void addAll(String cid, List<StateLinkSettingMaster> stateLinkSettingMasters, int startYearMonth, int endYearMonth, GeneralDate baseDate );

    void removeAll(String cid, String hisId);

}
