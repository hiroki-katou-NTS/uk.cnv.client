/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.estimate.company;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * The Class KscmtEstDaysComSet_.
 */
@StaticMetamodel(KscmtEstDaysComSet.class)
public class KscmtEstDaysComSet_ {

	/** The kscmt est days com set PK. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, KscmtEstDaysComSetPK> kscmtEstDaysComSetPK;
	
	/** The est condition 1 st days. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, Integer> estCondition1stDays;
	
	/** The est condition 2 nd days. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, Integer> estCondition2ndDays;
	
	/** The est condition 3 rd days. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, Integer> estCondition3rdDays;
	
	/** The est condition 4 th days. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, Integer> estCondition4thDays;
	
	/** The est condition 5 th days. */
	public static volatile SingularAttribute<KscmtEstDaysComSet, Integer> estCondition5thDays;
	
}