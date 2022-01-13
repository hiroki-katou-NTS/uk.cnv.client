package nts.uk.screen.com.app.command.cmm029;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A : 機能の選択.メニュー別OCD.その他の設定機能を登録する.その他の設定機能を登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterOtherSettingCommandHandler extends CommandHandler<RegisterFunctionSettingCommand> {

	private static final int SYSTEM_TYPE = 1;

	@Inject
	private TaskOperationSettingRepository taskOperationSettingRepository;

	@Override
	protected void handle(CommandHandlerContext<RegisterFunctionSettingCommand> context) {
		String cid = AppContexts.user().companyId();
		List<DisplayDataDto> datas = context.getCommand().getDatas();
		this.findDisplayData(datas, "CMM029_42").ifPresent(data -> {
			// 1. 取得する(会社ID＝Input. 会社ID)
			Optional<TaskOperationSetting> optTaskOperationSetting = this.taskOperationSettingRepository
					.getTasksOperationSetting(cid);
			// 1.1. <call>
			TaskOperationMethod method = EnumAdaptor.valueOf(data.getTaskOperationMethod(), TaskOperationMethod.class);
			TaskOperationSetting domain = new TaskOperationSetting(method);
			if (optTaskOperationSetting.isPresent()) {
				this.taskOperationSettingRepository.update(domain);
			} else {
				this.taskOperationSettingRepository.insert(domain);
			}
		});
	}

	private Optional<DisplayDataDto> findDisplayData(List<DisplayDataDto> datas, String programId) {
		return datas.stream().filter(data -> data.getProgramId().equals(programId) && data.getSystem() == SYSTEM_TYPE)
				.findFirst();
	}
}
