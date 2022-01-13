package nts.uk.ctx.sys.auth.app.find.grant.roleindividual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.RoleIndividualGrantDto;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;

@AllArgsConstructor
@Getter
public class Cas013aDto {
    private String id;

    private String companyID;

    private String companyCode;

    private String companyName;

    private String userID;

    private String employeeId;

    private String employeeCode;

    private String employeeName;
    private GeneralDate startValidPeriod;

    private GeneralDate endValidPeriod;


    public static Cas013aDto fromDomain(RoleIndividualGrant domain ,
                                         String companyID,
                                        String companyCode,
                                        String companyName,
                                        String employeeId,
                                        String employeeCode,
                                        String employeeName) {
        return new Cas013aDto(
                domain.getCompanyId()+"_"+domain.getUserId()+"_"+domain.getRoleType().value,
                companyID,
                companyCode,
                companyName,
                domain.getUserId(),
                employeeId,
                employeeCode,
                employeeName,
                domain.getValidPeriod().start(),
                domain.getValidPeriod().end());
    }
}
