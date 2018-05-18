/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.infra.entity.securitypolicy.lockoutdata;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import nts.arc.time.GeneralDateTime;


/**
 * The Class SgwdtContract_.
 */
@StaticMetamodel(SgwmtLockoutData.class)
public class SgwmtLockoutData_ {
    
    /** The sgwmt logout data PK. */
    public static volatile SingularAttribute<SgwmtLockoutData, SgwmtLockoutDataPK> sgwmtLockoutDataPK;
    
    /** The lock type. */
    public static volatile SingularAttribute<SgwmtLockoutData, Integer> lockType;
    
    /** The lockout date time. */
    public static volatile SingularAttribute<SgwmtLockoutData, GeneralDateTime> lockoutDateTime;
    
    /** The login method. */
    public static volatile SingularAttribute<SgwmtLockoutData, Integer> loginMethod;
}
