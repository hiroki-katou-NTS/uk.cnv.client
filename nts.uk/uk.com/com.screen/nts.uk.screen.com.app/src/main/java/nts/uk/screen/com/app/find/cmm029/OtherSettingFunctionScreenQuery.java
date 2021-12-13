package nts.uk.screen.com.app.find.cmm029;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A :機能の選択.メニュー別OCD.その他の設定機能を取得する.その他の設定機能を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OtherSettingFunctionScreenQuery extends AbstractFunctionScreenQuery {

	@Inject
	private TaskOperationSettingRepository taskOperationSettingRepository;

	@Override
	protected DisplayDataDto getMainDisplayData(List<StandardMenu> standardMenus) {
		return this.findFromStandardMenu(standardMenus, "CMM029_40", "CMM029_41").build();
	}

	@Override
	protected List<DisplayDataDto> getFunctionSettings(String cid, List<StandardMenu> standardMenus) {
		List<DisplayDataDto> datas = new ArrayList<>();
		Optional<TaskOperationSetting> optTaskOperationSetting = this.taskOperationSettingRepository
				.getTasksOperationSetting(cid);
		datas.add(DisplayDataDto
				.builder().system(SYSTEM_TYPE).programId("CMM029_42").taskOperationMethod(optTaskOperationSetting
						.map(data -> data.getTaskOperationMethod().value).orElse(TaskOperationMethod.DO_NOT_USE.value))
				.build());
		return datas;
	}
}
