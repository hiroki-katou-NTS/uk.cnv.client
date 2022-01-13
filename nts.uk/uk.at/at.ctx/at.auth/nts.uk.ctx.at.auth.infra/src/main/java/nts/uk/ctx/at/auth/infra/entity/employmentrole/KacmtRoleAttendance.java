package nts.uk.ctx.at.auth.infra.entity.employmentrole;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KACMT_ROLE_ATTENDANCE")
@Setter
public class KacmtRoleAttendance extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = -5374494134003331017L;

    @Id
    @Column(name = "ROLE_ID")
    public String roleID;

    @Column(name = "CID")
    public String companyID;

    @Column(name = "FUTURE_DATE_REF_PERMIT")
    public boolean futureDateRefPermit;

    @Override
    protected Object getKey() {
        return this.roleID;
    }

    public EmploymentRole toDomain() {
        return new EmploymentRole(
                this.roleID,
                this.companyID,
                this.futureDateRefPermit ? NotUseAtr.USE : NotUseAtr.NOT_USE
        );
    }
    public KacmtRoleAttendance(String roleID) {
        super();
        this.roleID = roleID;
    }
    public static KacmtRoleAttendance toEntity(EmploymentRole domain) {
        return new KacmtRoleAttendance(
                domain.getRoleId(),
                domain.getCompanyId(),
                domain.getFutureDateRefPermit().equals(NotUseAtr.USE)
        );
    }

}
