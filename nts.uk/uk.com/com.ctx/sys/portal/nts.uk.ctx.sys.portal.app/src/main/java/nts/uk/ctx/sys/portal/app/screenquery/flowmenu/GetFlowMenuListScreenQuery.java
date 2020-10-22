package nts.uk.ctx.sys.portal.app.screenquery.flowmenu;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.flowmenu.CreateFlowMenuRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CCG_メニュートップページ.CCG034_フローページの作成.A：フローメニューの作成.メニュー別OCD.フローメニュー作成のリストを取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetFlowMenuListScreenQuery {

	@Inject
	private CreateFlowMenuRepository createFlowMenuRepository;
	
	public Map<String, String> getList() {
		return this.createFlowMenuRepository.findByCid(AppContexts.user().companyId())
				.stream()
				.collect(Collectors.toMap(
						d -> (String) d.getFlowMenuCode().v(),
						d -> (String) d.getFlowMenuName().v(),
						(o1, o2) -> o1,
						TreeMap::new
				));
	}
}
