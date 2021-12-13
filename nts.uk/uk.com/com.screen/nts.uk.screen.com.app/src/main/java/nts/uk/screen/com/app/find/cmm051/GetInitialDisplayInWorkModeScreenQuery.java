package nts.uk.screen.com.app.find.cmm051;


import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.portal.dom.notice.adapter.MessageNoticeAdapter;
import nts.uk.ctx.sys.portal.dom.notice.adapter.WorkplaceInfoImport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Screen Query: UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM051_職場管理者の登録.A:職場管理者の登録.メニュー別OCD.職場モードで初期表示を取得する
 *
 * @author :chinh.hm
 */
@Stateless
public class GetInitialDisplayInWorkModeScreenQuery {
    @Inject
    private MessageNoticeAdapter messageNoticeAdapter;
    @Inject
    private GetListEmployeeInformationScreenQuery informationScreenQuery;

    public Cmm051InitDto getInitDisplayInWorkMode() {
        Cmm051InitDto rs = new Cmm051InitDto();
        String sid = AppContexts.user().employeeId();
        GeneralDate referenceDate = GeneralDate.today();
        Optional<WorkplaceInfoImport> workplaceInfoImportOptional = messageNoticeAdapter
                .getWorkplaceInfo(sid, referenceDate);
        if (workplaceInfoImportOptional.isPresent()) {
            rs.setWorkplaceInfoImport(workplaceInfoImportOptional.get());
            rs.setEmployeeInformation(informationScreenQuery
                    .getLisEmployeeInfo(workplaceInfoImportOptional.get().getWorkplaceId()));
        }
        return rs;
    }
}
