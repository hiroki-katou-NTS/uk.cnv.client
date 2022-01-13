package nts.uk.ctx.at.record.app.command.kdw.kdw006.j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayAttItem;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayManHrRecordItem;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormatRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.RecordColumnDispName;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.RecordColumnDisplayItem;
import nts.uk.shr.com.context.AppContexts;

/**
 * Command: フォーマット設定を新規登録する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.J：工数入力画面フォーマット設定.メニュー別OCD.フォーマット設定を新規登録する
 * 
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterNewFormatSettingsCommandHandler extends CommandHandler<RegisterNewFormatSettingsCommand> {

	@Inject
	private ManHrInputDisplayFormatRepository manHrInputDisplayFormatRepo;

	@Override
	protected void handle(CommandHandlerContext<RegisterNewFormatSettingsCommand> context) {
		RegisterNewFormatSettingsCommand command = context.getCommand();

		Optional<ManHrInputDisplayFormat> manHrInputDisplay = this.manHrInputDisplayFormatRepo
				.get(AppContexts.user().companyId());

		List<RecordColumnDisplayItem> recordColumnDisplayItems = command.getRecordColumnDisplayItems().stream()
				.map(m -> {
					return new RecordColumnDisplayItem(m.getOrder(), m.getAttendanceItemId(),
							new RecordColumnDispName(m.getDisplayName()));
				}).collect(Collectors.toList());

		List<DisplayAttItem> displayAttItems = command.getDisplayAttItems().stream().map(m -> {
			return new DisplayAttItem(m.getAttendanceItemId(), m.getOrder());
		}).collect(Collectors.toList());

		List<DisplayManHrRecordItem> displayManHrRecordItems = command.getDisplayManHrRecordItems().stream().map(m -> {
			return new DisplayManHrRecordItem(m.getAttendanceItemId(), m.getOrder());
		}).collect(Collectors.toList());

		ManHrInputDisplayFormat domain = new ManHrInputDisplayFormat(recordColumnDisplayItems, displayAttItems,
				displayManHrRecordItems);

		if (manHrInputDisplay.isPresent()) {
			ManHrInputDisplayFormat displayFormat = manHrInputDisplay.get();
			if (displayFormat.getDisplayAttItems().isEmpty() && displayFormat.getDisplayManHrRecordItems().isEmpty()
					&& displayFormat.getRecordColumnDisplayItems().isEmpty()) {
				manHrInputDisplayFormatRepo.insert(domain);
			} else {
				manHrInputDisplayFormatRepo.delete(AppContexts.user().companyId());
				manHrInputDisplayFormatRepo.insert(domain);
			}
		} else {
			manHrInputDisplayFormatRepo.insert(domain);
		}
	}
}
