package nts.uk.cnv.app.cnv.command;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.cnv.dom.td.schema.snapshot.TableSnapshot;
import nts.uk.cnv.dom.td.schema.tabledesign.ErpTableDesignRepository;
import nts.uk.cnv.dom.td.service.DDLImportService;

@Stateless
public class ErpTableDesignImportCommandHandler extends CommandHandler<ErpTableDesignImportCommand> {

	@Inject
	ErpTableDesignRepository tableDesignRepository;

	@Override
	protected void handle(CommandHandlerContext<ErpTableDesignImportCommand> context) {

		ErpTableDesignImportCommand command = context.getCommand();
		RequireImpl require = new RequireImpl(tableDesignRepository);
		transaction.execute(() -> {
			AtomTask at;
			try {
				at = DDLImportService.regist(
						require, "", command.getCreateTableSql(), command.getCreateIndexSql(), command.getCommentSql(), command.getType());
			} catch (JSQLParserException e) {
				throw new BusinessException(new RawErrorMessage("SQL文解析に失敗しました：" + e.getCause().toString()));
			}
			at.run();
		});
	}

	@RequiredArgsConstructor
	private static class RequireImpl implements DDLImportService.Require {

		private final ErpTableDesignRepository tableDesignRepository;

		@Override
		public void regist(TableSnapshot tableDesign) {
			boolean exists = tableDesignRepository.exists(tableDesign.getName());
			if (exists) {
				tableDesignRepository.update(tableDesign);
			}
			else {
				tableDesignRepository.insert(tableDesign);
			}
		}
	}
}
