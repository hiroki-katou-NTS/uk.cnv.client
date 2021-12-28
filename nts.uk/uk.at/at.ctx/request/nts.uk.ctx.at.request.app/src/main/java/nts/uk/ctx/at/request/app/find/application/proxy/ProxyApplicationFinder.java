package nts.uk.ctx.at.request.app.find.application.proxy;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.adapter.application.AtApplicationAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.application.dto.AtMenuNameQueryImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.application.dto.AtStandardMenuNameImport;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.proxy.ProxyApplicationService;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.AppSetForProxyApp;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.shr.com.context.AppContexts;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class ProxyApplicationFinder {

    @Inject
    private ProxyApplicationService proxyApplicationService;

    @Inject
    private ApplicationSettingRepository appSetRepo;

    @Inject
    private AtApplicationAdapter atApplicationAdapter;

    private final String SCREEN_ID_A = "A";
    private final String SCREEN_ID_B = "B";
    private final String OVERTIME_1_STRING_QUERY = "overworkatr=0";
    private final String OVERTIME_2_STRING_QUERY = "overworkatr=1";
    private final String OVERTIME_3_STRING_QUERY = "overworkatr=2";
    private final String OVERTIME_4_STRING_QUERY = "overworkatr=3";

    /**
     * 申請種類選択
     * @param proxyParamFind
     */
    public void selectApplicationByType(ProxyParamFind proxyParamFind) {
        this.proxyApplicationService.selectApplicationByType(proxyParamFind.getEmployeeIds(), proxyParamFind.getApplicationType());
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF001_代行申請.A：代行申請.アルゴリズム.代行申請起動.代行申請起動
     * @return
     */
    public List<AtStandardMenuNameImport> startProxyApplication() {
        String cid = AppContexts.user().companyId();
        // ドメインモデル「代行申請で利用できる申請設定」より取得する
        //(Lấy domain 「代行申請で利用できる申請設定」)
        Optional<ApplicationSetting> appSet = appSetRepo.findByCompanyId(cid);
        List<AppSetForProxyApp> result = new ArrayList<>();

        if (appSet.isPresent()) {
            result = appSet.get().getAppSetForProxyApps()
                    .stream()
                    .sorted(Comparator.comparing(AppSetForProxyApp::getAppType))
                    .collect(Collectors.toList());
        }

        if (result.isEmpty()) {
            throw new BusinessException("Msg_2071");
        }

        if (!CollectionUtil.isEmpty(result)) {

            List<AtMenuNameQueryImport> queryImports = new ArrayList<>();

            result.forEach(i -> {
                String queryString = Strings.EMPTY;
                String screenId = SCREEN_ID_A;

                if (i.getOpOvertimeAppAtr().isPresent()) {
                    switch (i.getOpOvertimeAppAtr().get().value) {
                        case 0:
                            queryString = OVERTIME_1_STRING_QUERY;
                            break;
                        case 1:
                            queryString = OVERTIME_2_STRING_QUERY;
                            break;
                        case 2:
                            queryString = OVERTIME_3_STRING_QUERY;
                            break;
                        case 3:
                            queryString = OVERTIME_4_STRING_QUERY;
                            break;
                    }
                }

                if (i.getOpStampRequestMode().isPresent() && i.getOpStampRequestMode().get().value == StampRequestMode.STAMP_ONLINE_RECORD.value) {
                    screenId = SCREEN_ID_B;
                }

                AtMenuNameQueryImport item = new AtMenuNameQueryImport(
                        this.getProgramId(i.getAppType().value),
                        screenId,
                        Strings.isEmpty(queryString) ? Optional.empty() : Optional.of(queryString)
                );

                queryImports.add(item);
            });
            // UKDesign.UniversalK.就業.KAF_申請.KAF001_代行申請.A：代行申請.アルゴリズム.代行申請起動.メニューの表示名を取得する
            return new ArrayList<>(atApplicationAdapter.getMenuDisplayName(cid, queryImports));

        }

        return Collections.emptyList();

    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF001_代行申請.A：代行申請.アルゴリズム.選択人数チェック.選択人数チェック
     * @param param
     */
    public void checkEmployeeSelected(CheckEmployeeParam param) {
        List<String> sids = param.getLstEmployeeId();
        int appType = param.getAppType();
        // input.申請日付をチェックする
        if (param.getBaseDate() == null) {
            throw new BusinessException("Msg_1711");
        }
        // input:List<社員ID>をチェックする
        if (CollectionUtil.isEmpty(sids)) {
            throw new BusinessException("Msg_1708");
        }
        // input.List<社員ID>の数＞200がTrueである場合
        if (sids.size() > 200) {
            throw new BusinessException("Msg_1709", "" + sids.size());
        }
        // input.申請種類をチェックする
        if (sids.size() > 1 && ((appType != ApplicationType.OVER_TIME_APPLICATION.value || param.getOvertimeAppAtr() == OvertimeAppAtr.MULTIPLE_OVERTIME.value)
                && appType != ApplicationType.HOLIDAY_WORK_APPLICATION.value)) {
            throw new BusinessException("Msg_1710", EnumAdaptor.valueOf(appType, ApplicationType.class).name);
        }
    }

    private String getProgramId(int appType) {
        switch (appType) {
            case 0:
                return "KAF005";
            case 1:
                return "KAF006";
            case 2:
                return "KAF007";
            case 3:
                return "KAF008";
            case 4:
                return "KAF009";
            case 5:
                return "";
            case 6:
                return "KAF010";
            case 7:
                return "KAF002";
            case 8:
                return "KAF012";
            case 9:
                return "KAF004";
            case 10:
                return "KAF011";
            case 11:
                return "";
            case 12:
                return "";
            case 13:
                return "";
            case 14:
                return "KAF021";
            case 15:
                return "KAF020";
            default:
                return null;
        }
    }

}
