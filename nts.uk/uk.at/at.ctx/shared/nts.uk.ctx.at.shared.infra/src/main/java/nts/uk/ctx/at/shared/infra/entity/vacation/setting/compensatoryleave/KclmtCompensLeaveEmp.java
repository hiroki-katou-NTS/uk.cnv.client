/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KclmtCompensLeaveEmp.
 */
@Setter
@Getter
@Entity
@Table(name = "KSHMT_HDCOM_EMP")
public class KclmtCompensLeaveEmp extends ContractUkJpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The kclmt compens leave emp PK. */
    @EmbeddedId
    protected KclmtCompensLeaveEmpPK kclmtCompensLeaveEmpPK;
    
    /** The manage atr. */
    @Basic(optional = false)
    @Column(name = "MANAGE_ATR")
    private boolean manageAtr;
    
 
    
    public static final JpaEntityMapper<KclmtCompensLeaveEmp> MAPPER =
    		new JpaEntityMapper<>(KclmtCompensLeaveEmp.class);
    /**
     * Instantiates a new kclmt compens leave emp.
     */
    public KclmtCompensLeaveEmp() {
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kclmtCompensLeaveEmpPK != null ? kclmtCompensLeaveEmpPK.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        // not set
        if (!(object instanceof KclmtCompensLeaveEmp)) {
            return false;
        }
        KclmtCompensLeaveEmp other = (KclmtCompensLeaveEmp) object;
        if ((this.kclmtCompensLeaveEmpPK == null && other.kclmtCompensLeaveEmpPK != null)
                || (this.kclmtCompensLeaveEmpPK != null
                        && !this.kclmtCompensLeaveEmpPK.equals(other.kclmtCompensLeaveEmpPK))) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
     */
    @Override
    protected Object getKey() {
        return this.kclmtCompensLeaveEmpPK;
    }

	public KclmtCompensLeaveEmp(KclmtCompensLeaveEmpPK kclmtCompensLeaveEmpPK, int manageAtr) {
		super();
		this.kclmtCompensLeaveEmpPK = kclmtCompensLeaveEmpPK;
		this.manageAtr = BooleanUtils.toBoolean(manageAtr);
	}
    
    public static KclmtCompensLeaveEmp toEntity(CompensatoryLeaveEmSetting domain){
    	return new KclmtCompensLeaveEmp(new KclmtCompensLeaveEmpPK(domain.getCompanyId(), domain.getEmploymentCode().v()),
    			domain.getIsManaged().value);
    }
    
    public CompensatoryLeaveEmSetting toDomain(){
    	return new CompensatoryLeaveEmSetting(
    			kclmtCompensLeaveEmpPK.getCid(),
    			new EmploymentCode(kclmtCompensLeaveEmpPK.getEmpcd()) ,
    			EnumAdaptor.valueOf(BooleanUtils.toInteger(this.isManageAtr()), ManageDistinct.class));
    }
}
