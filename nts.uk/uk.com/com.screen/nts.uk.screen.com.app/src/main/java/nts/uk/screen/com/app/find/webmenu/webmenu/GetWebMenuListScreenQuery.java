package nts.uk.screen.com.app.find.webmenu.webmenu;

import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuSimpleDto;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScreenQuery: Webメニューリストを取得する
 */
@Stateless
public class GetWebMenuListScreenQuery {
    @Inject
    private WebMenuRepository webMenuRepository;
    public List<WebMenuSimpleDto> findAllMenu() {
        String companyId = AppContexts.user().companyId();
        return webMenuRepository.findAllSimpleValue(companyId).stream().map(webMenuItem -> new WebMenuSimpleDto(webMenuItem.getCode(), webMenuItem.getName())).collect(Collectors.toList());
    }
}
