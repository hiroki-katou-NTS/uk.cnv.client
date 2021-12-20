package nts.uk.ctx.cloud.operate.app.command;

import lombok.SneakyThrows;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.task.AsyncTask;
import nts.arc.task.AsyncTaskError;
import nts.arc.task.AsyncTaskInfo;
import nts.arc.task.AsyncTaskInfoRepository;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyCategoryDto;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataCommand;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataCommandHanlder;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataExecutionRespone;
import nts.uk.ctx.sys.assist.app.find.mastercopy.MasterCopyCategoryFindDto;
import nts.uk.ctx.sys.assist.app.find.mastercopy.MasterCopyCategoryFinder;
import nts.uk.ctx.sys.assist.dom.mastercopy.CopyMethod;
import nts.uk.shr.com.company.CompanyId;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class MasterCopyCommandHandler extends CommandHandlerWithResult<MasterCopyCommand, AsyncTaskInfo> {

    @Inject
    private MasterCopyCategoryFinder finder;

    @Inject
    private MasterCopyDataCommandHanlder mastarCopyHandler;

    @Inject
    private AsyncTaskInfoRepository taskInfoRepo;

    @SneakyThrows
    @Override
    protected AsyncTaskInfo handle(CommandHandlerContext<MasterCopyCommand> context) {
        List<MasterCopyCategoryFindDto> categories = finder.getAllMasterCopyCategory();

        List<MasterCopyCategoryDto> list = categories.stream().map(category -> {
                    MasterCopyCategoryDto dto = new MasterCopyCategoryDto();
                    dto.setMasterCopyId(category.getMasterCopyId());
                    dto.setOrder(category.getOrder());
                    dto.setSystemType(category.getSystemType().toString());
                    dto.setCopyMethod(CopyMethod.REPLACE_ALL.value);
                    return dto;
                })
                .collect(Collectors.toList());

        MasterCopyDataCommand command = new MasterCopyDataCommand();
        command.setCompanyId(CompanyId.create(context.getCommand().getTenantCode(), "0001"));
        command.setMasterDataList(list);
        return this.mastarCopyHandler.handle(command);
    }
}
