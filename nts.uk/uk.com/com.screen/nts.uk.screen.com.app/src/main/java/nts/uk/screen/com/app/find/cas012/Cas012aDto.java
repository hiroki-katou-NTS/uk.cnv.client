package nts.uk.screen.com.app.find.cas012;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.Cas013aDto;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;


@AllArgsConstructor
@Data
public class Cas012aDto {
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

    public static Cas012aDto fromDomain(RoleIndividualGrant domain ,
                                        String companyID,
                                        String companyCode,
                                        String companyName,
                                        String employeeId,
                                        String employeeCode,
                                        String employeeName) {
        return new Cas012aDto(
                domain.getUserId(),
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
