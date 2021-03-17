package nts.uk.screen.at.app.kmt009;


import lombok.val;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.shared.app.query.task.GetTaskFrameUsageSettingQuery;
import nts.uk.ctx.at.shared.app.query.task.GetTaskOperationSettingQuery;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.screen.at.app.query.kmt.kmt005.TaskFrameSettingDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScreenQuery: 起動時の情報取得処理
 * UKDesign.UniversalK.就業.KMT_作業.KMT009_下位作業の絞り込み.Ａ：下位作業の絞り込み.Ａ：メニュー別OCD.起動時の情報取得処理.起動時の情報取得処理
 *
 * @author chinh.hm
 */
@Stateless
public class InfoAcquisitionProcessStartupTaskScreenQuery {
    @Inject
    private GetTaskOperationSettingQuery operationSettingQuery;

    @Inject
    private GetTaskFrameUsageSettingQuery frameUsageSettingQuery;

    public List<TaskFrameSettingDto> getDataStart() {
        val cid = AppContexts.user().companyId();
        //1. 取得する(会社ID)
        val optOperationSetting = operationSettingQuery.getTasksOperationSetting(cid);
        //2. not 作業運用設定.isPresent.
        if (!optOperationSetting.isPresent() || optOperationSetting.get().getTaskOperationMethod() == TaskOperationMethod.DO_NOT_USE)
            throw new BusinessException("Msg_2122");
        val operationSetting = optOperationSetting.get();
        //3. 作業運用設定.作業運用方法 == 利用しない
        //4. 作業運用設定.作業運用方法 <> 実績で利用
        if (operationSetting.getTaskOperationMethod().value != TaskOperationMethod.USED_IN_ACHIEVENTS.value) {
            throw new BusinessException("Msg_2122");
        }
        //5.取得(会社ID)
        val usageSetting = frameUsageSettingQuery.getWorkFrameUsageSetting(cid);
        //6.not 作業枠設定.isPresent
        if (usageSetting == null) {
            throw new BusinessException("Msg_2109");
        }
        return usageSetting.getFrameSettingList()
                .stream()
                .map(s -> new TaskFrameSettingDto(
                        s.getTaskFrameNo().v(),
                        s.getTaskFrameName().v(),
                        s.getUseAtr().value
                )).collect(Collectors.toList());
    }
}
