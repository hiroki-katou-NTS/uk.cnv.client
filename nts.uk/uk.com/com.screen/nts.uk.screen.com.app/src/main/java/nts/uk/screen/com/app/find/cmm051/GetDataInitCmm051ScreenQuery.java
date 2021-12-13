package nts.uk.screen.com.app.find.cmm051;


import nts.arc.error.BusinessException;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Screen query: UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM051_職場管理者の登録.A:職場管理者の登録.メニュー別OCD.初期表示を取得する
 *
 * @author chinh.hm
 */
@Stateless
public class GetDataInitCmm051ScreenQuery {
    @Inject
    private GetInitialDisplayInWorkModeScreenQuery inWorkModeScreenQuery;

    public Cmm051InitDto getDataInit() {
        String attendance = AppContexts.user().roles().forAttendance();
        if (attendance == null) {
            throw new BusinessException("Msg_1103");
        }
        return inWorkModeScreenQuery.getInitDisplayInWorkMode();
    }
}
