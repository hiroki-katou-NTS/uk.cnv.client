/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.entity.roleset;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 * Class entity of table SacmtRolesetDefault/SACMT_ROLESET_DEFAULT
 * @author Hieu.NV
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SACMT_ROLESET_DEFAULT")
public class SacmtRolesetDefault extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 会社ID */
    @Basic(optional = false)
    @Id
    @Column(name = "CID")
    public String companyId;

    /**コード */
    @Basic(optional = false)
    @Column(name = "ROLE_SET_CD")
    public String roleSetCd;

    @Override
    protected Object getKey() {
        return this.companyId;
    }

    public void buildEntity(String companyId, String roleSetCd) {
        this.companyId = companyId;
        this.roleSetCd = roleSetCd;
    }
}
