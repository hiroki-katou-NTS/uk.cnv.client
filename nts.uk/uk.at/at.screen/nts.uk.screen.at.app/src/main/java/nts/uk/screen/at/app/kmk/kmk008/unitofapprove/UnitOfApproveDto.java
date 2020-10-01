package nts.uk.screen.at.app.kmk.kmk008.unitofapprove;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.DoWork;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.approveregister.UnitOfApprover;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfApproveDto {

    /**
     * 会社ID
     */
    private String companyID;

    /**
     * 職場を利用する
     */
    private DoWork useWorkplace;

    public static UnitOfApproveDto setData(UnitOfApprover data){
        if (data == null){
            return new UnitOfApproveDto();
        }
        return new UnitOfApproveDto(data.getCompanyID(),data.getUseWorkplace());
    }
}
