package nts.uk.ctx.pr.core.dom.wageprovision.statementbindingsetting;

import java.util.Optional;

/**
* 明細書利用単位設定
*/
public interface StateUseUnitSettingRepository {

    Optional<StateUseUnitSetting> getStateUseUnitSettingById(String cid);

    void add(StateUseUnitSetting domain);

    void update(StateUseUnitSetting domain);

    void remove(String cid);

}
