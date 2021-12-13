package nts.uk.screen.com.app.find.cmm029;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A :機能の選択.メニュー別OCD.表示初期データを取得する.表示初期データを取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class InitialDisplayScreenQuery {

	@Inject
	private StandardMenuRepository standardMenuRepository;

	@Inject
	private WorkSettingFunctionScreenQuery workSettingFunctionScreenQuery;

	@Inject
	private LeaveSettingFunctionScreenQuery leaveSettingFunctionScreenQuery;

	@Inject
	private HolidaySettingFunctionScreenQuery holidaySettingFunctionScreenQuery;

	@Inject
	private OtherSettingFunctionScreenQuery otherSettingFunctionScreenQuery;

	public List<DisplayDataDto> findDisplayData() {
		String cid = AppContexts.user().companyId();
		// 1. 標準メニューを取得する(会社ID＝Input. 会社ID)
		List<StandardMenu> standardMenus = this.standardMenuRepository.findAll1(cid);
		// 2. 勤務の設定機能を取得する
		// 3. 休暇の設定機能を取得する
		// 4. 休日の設定機能を取得する
		// 5. その他の設定機能を取得する
		return Stream
				.of(this.workSettingFunctionScreenQuery.findData(cid, standardMenus),
						this.leaveSettingFunctionScreenQuery.findData(cid, standardMenus),
						this.holidaySettingFunctionScreenQuery.findData(cid, standardMenus),
						this.otherSettingFunctionScreenQuery.findData(cid, standardMenus))
				.flatMap(Collection::stream).collect(Collectors.toList());
	}
}
