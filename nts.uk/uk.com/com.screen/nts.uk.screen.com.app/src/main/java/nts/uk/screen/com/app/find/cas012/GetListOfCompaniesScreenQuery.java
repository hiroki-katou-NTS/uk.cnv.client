package nts.uk.screen.com.app.find.cas012;


import lombok.val;
import nts.arc.error.BusinessException;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScreenQuery: ロール個人別付与を検索する
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS012_管理者ロールの付与.A:管理者ロールの付与.Ａ：メニュー別OCD.会社一覧を取得する
 *
 * @author chinh.hm
 */
@Stateless
public class GetListOfCompaniesScreenQuery {
    @Inject
    private CompanyRepository repo;

    public List<CompanyImport> getListCompany() {
        val user = AppContexts.user();
        val contractCode = user.contractCode();
        if (!user.roles().have().systemAdmin()){
            throw  new BusinessException("Msg_1103");
        }
        return repo.getAllCompany(contractCode).stream()
                .map(e -> new CompanyImport(
                        e.getCompanyCode().v(),
                        e.getCompanyName().v(),
                        e.getCompanyId(),
                        e.getIsAbolition().value)).collect(Collectors.toList());
    }
}
