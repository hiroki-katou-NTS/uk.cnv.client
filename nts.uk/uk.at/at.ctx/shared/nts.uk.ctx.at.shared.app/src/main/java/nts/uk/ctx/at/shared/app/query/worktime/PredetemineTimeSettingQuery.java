package nts.uk.ctx.at.shared.app.query.worktime;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author thanhpv
 * @part UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.共通設定.所定時間.App.所定時間設定を取得する
 */
@Stateless
public class PredetemineTimeSettingQuery {
	
    @Inject
    private PredetemineTimeSettingRepository predetemineTimeSettingRepo;

    /**
     * @name 所定時間設定を取得する
     */
    public Optional<PredetemineTimeSetting> getPredetemineTimeSetting(String workTimeCode){
        return predetemineTimeSettingRepo.findByWorkTimeCode(AppContexts.user().companyId() ,workTimeCode);
    }
}
