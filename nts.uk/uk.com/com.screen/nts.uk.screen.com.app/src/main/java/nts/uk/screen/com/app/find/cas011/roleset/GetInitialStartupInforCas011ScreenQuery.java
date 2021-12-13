package nts.uk.screen.com.app.find.cas011.roleset;


import lombok.val;
import nts.arc.error.BusinessException;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuFinder;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuSimpleDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS011_ロールセットの登録.ロールセットの登録.メニュー別OCD.初期起動の情報取得する.初期起動の情報取得する
 * ScreenQuery: Start Cas011 : 初期起動の情報取得する
 *
 * @author chinh.hm
 */


@Stateless
public class GetInitialStartupInforCas011ScreenQuery {
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private GetListOfRoleSetScreenQuery getListOfRoleSetScreenQuery;

    @Inject
    private WebMenuFinder webMenuFinder;


    public Cas011Dto getDataForStartUp() {
        // 1 : AppContexts.user().roles().forCompanyAdmin()
        val forCompanyAdmin = AppContexts.user().roles().forCompanyAdmin();
        // 2 : 会社管理者ではない場合
        if (forCompanyAdmin == null) {
            throw new BusinessException("Msg_1103");
        }
        val rs = new Cas011Dto();
        val cid = AppContexts.user().companyId();
        // 3 : ロール種類、ロール区分に一致するロールをすべて取得する
        // 個人情報, 　一般
        RoleType roleTypePersonalInfo =  RoleType.PERSONAL_INFO;
        RoleType roleTypeEmployment =  RoleType.EMPLOYMENT;
        RoleAtr roleAtr = RoleAtr.GENERAL;
        List<Role> rolesPersonalInfo = roleRepository.findByTypeAtr(cid, roleTypePersonalInfo.value, roleAtr.value);
        if (!rolesPersonalInfo.isEmpty()) {
            rs.setRolesPersonalInfo(rolesPersonalInfo.stream().map(RoleDto::fromDomain).collect(Collectors.toList()));
        }
        // 4 : ロール種類、ロール区分に一致するロールをすべて取得する
        List<Role> rolesEmployment = roleRepository.findByTypeAtr(cid, roleTypeEmployment.value, roleAtr.value);
        if (!rolesEmployment.isEmpty()) {
            rs.setRolesEmployment(rolesEmployment.stream().map(RoleDto::fromDomain).collect(Collectors.toList()));
        }
        // 5: すべてのWebメニューを取得する
        List<WebMenuSimpleDto> webMenuSimpleDtos = webMenuFinder.findAllWithNoMenuBar();
        rs.setWebMenuSimpleDtos(webMenuSimpleDtos);
        RoleSetAndRoleDefaultDto roleDefaultDto = getListOfRoleSetScreenQuery.getListRoleSet();
        rs.setRoleDefaultDto(roleDefaultDto);
        return rs;
    }
}
