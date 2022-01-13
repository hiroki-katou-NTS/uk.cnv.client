package nts.uk.ctx.at.record.app.find.workrecord.operationsetting;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;

/**
* 承認処理の利用設定
*/
@AllArgsConstructor
@Value
public class ApprovalProcessDto
{
    
    /**
    * 会社ID
    */
    private String cid;
    
//    /**
//     * 職位ID
//     */
//     private String jobTitleId;
     
    /**
    * 日の承認者確認を利用する
    */
    private int useDailyBossChk;
    
    /**
    * 月の承認者確認を利用する
    */
    private int useMonthBossChk;
    
    /**
    * エラーがある場合の上司確認
    */
    private Integer supervisorConfirmError;
    
    
    public static ApprovalProcessDto fromDomain(ApprovalProcess domain){
        return new ApprovalProcessDto(domain.getCid(), 
						        		domain.getUseDailyBossChk(), domain.getUseMonthBossChk(), 
						        		domain.getSupervisorConfirmError() == null ? null : domain.getSupervisorConfirmError().value);
    }
    
}
