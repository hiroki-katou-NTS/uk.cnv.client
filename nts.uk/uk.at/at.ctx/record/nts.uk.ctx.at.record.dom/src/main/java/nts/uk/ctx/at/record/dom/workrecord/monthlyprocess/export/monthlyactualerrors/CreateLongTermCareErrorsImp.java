package nts.uk.ctx.at.record.dom.workrecord.monthlyprocess.export.monthlyactualerrors;

import java.util.List;

import javax.ejb.Stateless;

import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.pererror.CreatePerErrorsFromLeaveErrors;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;

@Stateless
public class CreateLongTermCareErrorsImp implements CreateLongTermCareErrors {

    @Override
    public List<EmployeeMonthlyPerError> createLongTermCareErrors(CreateLongTermCareErrorsParam param) {
        // 介護休暇エラーから月別残数エラー一覧を作成する
        List<EmployeeMonthlyPerError> employeeMonthlyPerError = CreatePerErrorsFromLeaveErrors.fromCareLeave(
                param.getSID(), 
                param.getYearMonth(), 
                param.getClosureId(), 
                param.getClosureDate(), 
                param.getChildCareNurseErrors());
        
        return employeeMonthlyPerError;
    }

}
