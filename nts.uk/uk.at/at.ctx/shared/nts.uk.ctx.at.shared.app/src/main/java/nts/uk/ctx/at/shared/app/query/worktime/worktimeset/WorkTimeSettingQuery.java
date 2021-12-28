package nts.uk.ctx.at.shared.app.query.worktime.worktimeset;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author thanhpv
 * @part UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.App.全ての就業時間帯の設定を取得する
 */
@Stateless
public class WorkTimeSettingQuery {
	
    @Inject
    private WorkTimeSettingRepository workTimeSettingRepo;

    /**
     * @name 全ての就業時間帯の設定を取得する
     */
    public List<WorkTimeSetting> getWorkTimeSettings(){
        return workTimeSettingRepo.findByCId(AppContexts.user().companyId());
    }
}
