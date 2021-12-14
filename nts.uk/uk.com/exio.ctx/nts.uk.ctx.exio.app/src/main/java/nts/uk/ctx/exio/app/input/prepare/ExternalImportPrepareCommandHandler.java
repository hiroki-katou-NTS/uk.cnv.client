package nts.uk.ctx.exio.app.input.prepare;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.dom.input.PrepareImporting;
import nts.uk.ctx.exio.dom.input.manage.ExternalImportStateException;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExternalImportPrepareCommandHandler extends AsyncCommandHandler<ExternalImportPrepareCommand>{

	@Inject
	private FileStorage fileStorage;
	
	@Inject
	private ExternalImportPrepareRequire require;
	
	@Override
	protected void handle(CommandHandlerContext<ExternalImportPrepareCommand> context) {
		
		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();

		val taskData = context.asAsync().getDataSetter();
		try {
			val require = this.require.create(companyId);
			val setting = require.getExternalImportSetting(companyId, command.getExternalImportCode());
			val currentState = require.getExternalImportCurrentState(companyId);
			
			currentState.prepare(require, setting, () -> {
				run(require, command.getUploadedCsvFileId(), setting);
			});
			
			taskData.setData("process", "done");
			
		} catch (ExternalImportStateException ex) {
			
			taskData.setData("process", "failed");
			taskData.setData("message", ex.getMessage());
			
		} finally {
			
			// 成功失敗問わず、ファイルは必ず削除する。ゴミを残さない。
			fileStorage.delete(command.getUploadedCsvFileId());
		}
	}

	@SneakyThrows
	private void run(
			ExternalImportPrepareRequire.Require require,
			String fileId,
			ExternalImportSetting setting) {
		
		try (val inputStream = fileStorage.getStream(fileId)
				.orElseThrow(() -> new RuntimeException("file not found: " + fileId))) {
			
			PrepareImporting.prepare(require, setting, inputStream);
		}
	}
}
