/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.basic.infra.entity.company.organization.workplace;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import nts.arc.time.GeneralDate;

/**
 * The Class KwpmtWorkHistPK_.
 */
@StaticMetamodel(KwpmtWorkplacePK.class)
public class KwpmtWorkplacePK_ {

	/** The cid. */
	public static volatile SingularAttribute<KwpmtWorkplacePK, String> cid;
	
	/** The wplid. */
	public static volatile SingularAttribute<KwpmtWorkplacePK, String> wplid;
	
	/** The str D. */
	public static volatile SingularAttribute<KwpmtWorkplacePK, GeneralDate> strD;
	
	/** The end D. */
	public static volatile SingularAttribute<KwpmtWorkplacePK, GeneralDate> endD;
}