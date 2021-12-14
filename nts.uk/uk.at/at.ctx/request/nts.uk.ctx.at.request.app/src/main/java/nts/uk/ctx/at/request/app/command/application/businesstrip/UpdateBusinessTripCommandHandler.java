package nts.uk.ctx.at.request.app.command.application.businesstrip;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.app.command.application.common.CreateApplicationCommand;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfoOutput;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.BusinessTripService;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.ResultCheckInputCode;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.AtEmployeeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateBusinessTripCommandHandler extends CommandHandlerWithResult<UpdateBusinessTripCommand, ProcessResult> {

    @Inject
    BusinessTripRepository businessTripRepository;

    @Inject
    private DetailBeforeUpdate detailBeforeProcessRegisterService;

    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private DetailAfterUpdate detailAfterUpdate;

    @Inject
    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

    @Inject
    private BusinessTripService businessTripService;

    @Inject
    private AtEmployeeAdapter atEmployeeAdapter;

    @Inject
    private CommonAlgorithm commonAlgorithm;

    @Override
    protected ProcessResult handle(CommandHandlerContext<UpdateBusinessTripCommand> context) {
        UpdateBusinessTripCommand command = context.getCommand();
        String cid = AppContexts.user().companyId();
        CreateApplicationCommand applicationCommand = command.getApplication();
        Application application = command
                .getBusinessTripInfoOutput().getAppDispInfoStartup().getAppDetailScreenInfo().getApplication().toDomain();
        application.setOpAppReason(Strings.isEmpty(applicationCommand.getOpAppReason()) ? Optional.empty() : Optional.of(new AppReason(applicationCommand.getOpAppReason())));
        application.setOpAppStandardReasonCD(applicationCommand.getOpAppStandardReasonCD() == null
                ? Optional.empty()
                : Optional.of(new AppStandardReasonCode(applicationCommand.getOpAppStandardReasonCD())));
        BusinessTrip businessTrip = command.getBusinessTrip().toDomain(application);
        BusinessTripInfoOutput infoOutput = command.getBusinessTripInfoOutput().toDomain();

        // アルゴリズム「4-1.詳細画面登録前の処理」を実行する
        businessTrip.getInfos().stream().forEach(i -> {
            this.detailBeforeProcessRegisterService.processBeforeDetailScreenRegistration(
                    cid,
                    application.getEmployeeID(),
                    i.getDate(),
                    EmploymentRootAtr.APPLICATION.value,
                    application.getAppID(),
                    application.getPrePostAtr(),
                    application.getVersion(),
                    i.getWorkInformation().getWorkTypeCode().v(),
                    i.getWorkInformation().getWorkTimeCode() == null ? null : i.getWorkInformation().getWorkTimeCode().v(),
                    infoOutput.getAppDispInfoStartup(), 
                    businessTrip.getInfos().stream().map(x -> x.getWorkInformation().getWorkTypeCode().v()).collect(Collectors.toList()), 
                    Optional.empty(), 
                    false, 
                    Optional.of(i.getWorkInformation().getWorkTypeCode().v()), 
                    i.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v)
            );
        });

        this.checkBeforeUpdate(businessTrip, infoOutput.getAppDispInfoStartup());

        //ドメインモデル「申請」の更新をする
        applicationRepository.update(application);

        //ドメインモデル「出張申請」を更新する
        businessTripRepository.update(businessTrip);

        //アルゴリズム「出張申請暫定残数を更新する」を実行する
        //アルゴリズム「暫定データの登録」を実行する
        this.interimRemainDataMngRegisterDateChange.registerDateChange(cid,
                application.getEmployeeID(),
                businessTrip.getInfos().stream().map(i -> i.getDate()).collect(Collectors.toList()));

        // アルゴリズム「4-2.詳細画面登録後の処理」を実行する
        return detailAfterUpdate.processAfterDetailScreenRegistration(cid, application.getAppID(), infoOutput.getAppDispInfoStartup());
    }

    private void checkBeforeUpdate(BusinessTrip businessTrip, AppDispInfoStartupOutput appDispInfoStartupOutput) {

        String inputSid = appDispInfoStartupOutput.getAppDetailScreenInfo().get().getApplication().getEnteredPersonID();
        String cid = AppContexts.user().companyId();
        List<EmployeeInfoImport> employeeInfoImports = atEmployeeAdapter.getByListSID(Arrays.asList(inputSid));

        if (businessTrip.getInfos().isEmpty()) {
            throw new BusinessException("Msg_1703");
        }
        // loop 年月日　in　期間
        businessTrip.getInfos().forEach(i -> {

            String wkTypeCd = i.getWorkInformation().getWorkTypeCode().v();
            String wkTimeCd = i.getWorkInformation().getWorkTimeCode() == null ? null : i.getWorkInformation().getWorkTimeCode().v();
            Integer workTimeStart = null;
            Integer workTimeEnd = null;

            if (!i.getWorkingHours().isEmpty()) {
                workTimeStart = i.getWorkingHours().get(0).getStartDate().isPresent() ? i.getWorkingHours().get(0).getStartDate().get().v() : null;
                workTimeEnd = i.getWorkingHours().get(0).getEndDate().isPresent() ? i.getWorkingHours().get(0).getEndDate().get().v() : null;
            }

            // アルゴリズム「出張申請就業時間帯チェック」を実行する
            ResultCheckInputCode checkRequiredCode = businessTripService.checkRequireWorkTimeCode(wkTypeCd, wkTimeCd, workTimeStart, workTimeEnd, false);
            if (!checkRequiredCode.isResult()) {
                // エラーを「年月日＋メッセージ」とする
                throw new BusinessException(checkRequiredCode.getMsg(), i.getDate().toString());
            }

            // アルゴリズム「申請の矛盾チェック」を実行する
            this.commonAlgorithm.appConflictCheck(
                    cid,
                    employeeInfoImports.get(0),
                    Arrays.asList(i.getDate()),
                    Arrays.asList(i.getWorkInformation().getWorkTypeCode().v()),
                    appDispInfoStartupOutput
                            .getAppDispInfoWithDateOutput()
                            .getOpActualContentDisplayLst().isPresent() ?
                            appDispInfoStartupOutput
                                    .getAppDispInfoWithDateOutput()
                                    .getOpActualContentDisplayLst().get() : Collections.emptyList()
            );
            
            // 勤務種類により出退勤時刻をチェックする
            businessTripService.checkTimeByWorkType(i.getDate(), wkTypeCd, workTimeStart, workTimeEnd);
        });

    }
}
