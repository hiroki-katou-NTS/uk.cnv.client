/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.entity.roleset;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * Class entity of table SacmtRoleset/SACMT_ROLESET
 * @author Hieu.NV
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SACMT_ROLESET")
public class SacmtRoleSet extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public SacmtRoleSetPK roleSetPK;

    @Basic(optional = false)
    @Column(name = "ROLE_SET_NAME")
    public String roleSetName;

    @Basic(optional = false)
    @Column(name = "APPROVAL_AUTHORITY")
    public int approvalAuthority;

    @Basic(optional = false)
    @Column(name = "OFFICE_HELPER_ROLE")
    public String officeHelperRole;

    @Basic(optional = false)
    @Column(name = "MY_NUMBER_ROLE")
    public String myNumberRole;

    @Basic(optional = false)
    @Column(name = "HR_ROLE")
    public String hRRole;

    @Basic(optional = false)
    @Column(name = "PERSON_INF_ROLE")
    public String personInfRole;

    @Basic(optional = false)
    @Column(name = "EMPLOYMENT_ROLE")
    public String employmentRole;

    @Basic(optional = false)
    @Column(name = "SALARY_ROLE")
    public String salaryRole;

    @Override
    protected Object getKey() {
        return this.roleSetPK;
    }

    /**
     * Build Entity
     * @param roleSetPK
     * @param roleSetName
     * @param approvalAuthority
     * @param officeHelperRole
     * @param myNumberRole
     * @param hRRole
     * @param personInfRole
     * @param employmentRole
     * @param salaryRole
     */
    public void buildEntity(SacmtRoleSetPK roleSetPK
            , String roleSetName
            , int approvalAuthority
            , String officeHelperRoleCd
            , String myNumberRoleCd
            , String hRRoleCd
            , String personInfRoleCd
            , String employmentRoleCd
            , String salaryRoleCd) {
        this.roleSetPK          = roleSetPK;
        this.roleSetName        = roleSetName;
        this.approvalAuthority  = approvalAuthority;
        this.officeHelperRole   = officeHelperRoleCd;
        this.myNumberRole       = myNumberRoleCd;
        this.hRRole             = hRRoleCd;
        this.personInfRole      = personInfRoleCd;
        this.employmentRole     = employmentRoleCd;
        this.salaryRole         = salaryRoleCd;
    }
}
