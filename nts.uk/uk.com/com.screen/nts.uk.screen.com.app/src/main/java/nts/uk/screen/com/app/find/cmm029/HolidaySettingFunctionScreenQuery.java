package nts.uk.screen.com.app.find.cmm029;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingRepository;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A : 機能の選択.メニュー別OCD.休日の設定機能を取得する.休日の設定機能を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class HolidaySettingFunctionScreenQuery extends AbstractFunctionScreenQuery {

	@Inject
	private PublicHolidaySettingRepository publicHolidaySettingRepository;

	@Override
	protected DisplayDataDto getMainDisplayData(List<StandardMenu> standardMenus) {
		return this.findFromStandardMenu(standardMenus, "CMM029_37", "CMM029_38").build();
	}

	@Override
	protected List<DisplayDataDto> getFunctionSettings(String cid, List<StandardMenu> standardMenus) {
		List<DisplayDataDto> datas = new ArrayList<>();
		// 2. 公休設定を取得する(会社ID＝Input.  会社ID)
		Optional<PublicHolidaySetting> optPublicHolidaySetting = this.publicHolidaySettingRepository.get(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_39")
				.useAtr(optPublicHolidaySetting.map(PublicHolidaySetting::isManagePublicHoliday).orElse(true)).build());
		return datas;
	}
}
