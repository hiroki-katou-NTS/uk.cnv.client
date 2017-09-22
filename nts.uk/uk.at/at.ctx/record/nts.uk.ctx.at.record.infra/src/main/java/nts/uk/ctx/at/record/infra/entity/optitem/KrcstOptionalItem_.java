/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.entity.optitem;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * The Class KrcstOptionalItem_.
 */
@StaticMetamodel(KrcstOptionalItem.class)
public class KrcstOptionalItem_ {

	/** The krcst optional item PK. */
	public static volatile SingularAttribute<KrcstOptionalItem, KrcstOptionalItemPK> krcstOptionalItemPK;

	/** The optional item name. */
	public static volatile SingularAttribute<KrcstOptionalItem, String> optionalItemName;

	/** The optional item atr. */
	public static volatile SingularAttribute<KrcstOptionalItem, Short> optionalItemAtr;

	/** The usage atr. */
	public static volatile SingularAttribute<KrcstOptionalItem, Short> usageAtr;

	/** The performance atr. */
	public static volatile SingularAttribute<KrcstOptionalItem, Short> performanceAtr;

	/** The emp condition atr. */
	public static volatile SingularAttribute<KrcstOptionalItem, Short> empConditionAtr;

}
