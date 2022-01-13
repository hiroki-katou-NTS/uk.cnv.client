package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ＜＜Command＞＞ 工数集計表の複製登録する
 */
@Stateless
public class RegisterManHourSummaryTableDuplicateCommandHandler extends CommandHandler<RegisterOrUpdateManHourSummaryTableDuplicateCommand> {
    @Inject
    private ManHourSummaryTableFormatRepository repository;


    @Override
    protected void handle(CommandHandlerContext<RegisterOrUpdateManHourSummaryTableDuplicateCommand> commandHandlerContext) {
        RegisterOrUpdateManHourSummaryTableDuplicateCommand command = commandHandlerContext.getCommand();
        if (command == null) return;

        // Check duplicate
        Optional<ManHourSummaryTableFormat> checkDuplicate = this.repository.get(AppContexts.user().companyId(), command.getDestinationCode());
        if (checkDuplicate.isPresent() && !command.isOverwrite())
            throw new BusinessException("Msg_2166");
        ManHourSummaryTableFormat source = this.repository.get(AppContexts.user().companyId(), command.getSourcecode()).get();
        ManHourSummaryTableFormat copy = new ManHourSummaryTableFormat(
                new ManHourSummaryTableCode(command.getDestinationCode()),
                new ManHourSummaryTableName(command.getName()),
                DetailFormatSetting.create(
                        DisplayFormat.of(source.getDetailFormatSetting().getDisplayFormat().value),
                        TotalUnit.of(source.getDetailFormatSetting().getTotalUnit().value),
                        EnumAdaptor.valueOf(source.getDetailFormatSetting().getDisplayVerticalHorizontalTotal().value, NotUseAtr.class),
                        source.getDetailFormatSetting().getSummaryItemList().stream().map(item -> new SummaryItem(item.getHierarchicalOrder(),
                                SummaryItemType.of(item.getSummaryItemType().value)))
                                .collect(Collectors.toList())
                )
        );
        if (checkDuplicate.isPresent())
            this.repository.update(copy);
        else
            this.repository.insert(copy);
    }


}
