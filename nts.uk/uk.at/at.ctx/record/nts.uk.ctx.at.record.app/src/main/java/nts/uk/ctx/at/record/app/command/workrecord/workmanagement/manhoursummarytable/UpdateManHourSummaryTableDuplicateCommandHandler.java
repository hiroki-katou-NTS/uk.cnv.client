package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.DetailFormatSetting;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.DisplayFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableCode;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableName;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItem;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItemType;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.TotalUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * ＜＜Command＞＞  工数集計表の上書き登録する
 */
@Stateless
public class UpdateManHourSummaryTableDuplicateCommandHandler extends CommandHandler<RegisterOrUpdateManHourSummaryTableDuplicateCommand> {
    @Inject
    private ManHourSummaryTableFormatRepository repository;

    @Override
    protected void handle(CommandHandlerContext<RegisterOrUpdateManHourSummaryTableDuplicateCommand> commandHandlerContext) {
//        RegisterOrUpdateManHourSummaryTableDuplicateCommand command = commandHandlerContext.getCommand();
//        if (command == null){ return ;}
//        val copy = this.repository.get(AppContexts.user().companyId(), command.getDestinationCode()).get();
//        this.repository.update(command.toDomain(copy));
    }
}
