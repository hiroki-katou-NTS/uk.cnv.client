package nts.uk.ctx.pr.core.app.find.wageprovision.individualwagecontract;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

/**
* 給与個人別金額履歴: DTO
*/
@AllArgsConstructor
@Value
public class SalIndAmountHisDto
{
    
    /**
    * 履歴ID
    */
    private String ;
    
    /**
    * 個人金額コード
    */
    private String perValCode;
    
    /**
    * 社員ID
    */
    private String empId;
    
    /**
    * カテゴリ区分
    */
    private int cateIndicator;
    
    /**
    * 期間
    */
    private String period;
    
    /**
    * 期間
    */
    private String period;
    
    /**
    * 給与賞与区分
    */
    private int salBonusCate;
    
    
    public static SalIndAmountHisDto fromDomain(SalIndAmountHis domain)
    {
        return new SalIndAmountHisDto(domain.get(), domain.getPerValCode(), domain.getEmpId(), domain.getCateIndicator().value, domain.getPeriod(), domain.getPeriod(), domain.getSalBonusCate().value);
    }
    
}
