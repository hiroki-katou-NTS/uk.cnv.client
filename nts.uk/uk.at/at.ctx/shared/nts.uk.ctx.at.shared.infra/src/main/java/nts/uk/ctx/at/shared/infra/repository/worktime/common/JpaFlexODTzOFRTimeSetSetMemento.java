/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.TimezoneOfFixedRestTimeSetSetMemento;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtWtFleBrFiHolTs;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtFlexOdFixRestPK;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtWtFleBrFlHol;

/**
 * The Class JpaFlexOffdayTzOFRTimeSetGetMemento.
 */
public class JpaFlexODTzOFRTimeSetSetMemento implements TimezoneOfFixedRestTimeSetSetMemento{
	
	/** The entitys. */
	private KshmtWtFleBrFlHol entity;

	/**
	 * Instantiates a new jpa flex OD tz OFR time set set memento.
	 *
	 * @param entity the entity
	 */
	public JpaFlexODTzOFRTimeSetSetMemento(KshmtWtFleBrFlHol entity) {
		super();
		this.entity = entity;
		if (CollectionUtil.isEmpty(this.entity.getKshmtFlexOdFixRests())) {
			this.entity.setKshmtFlexOdFixRests(new ArrayList<>());
		}
	}


	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.worktime.common.TimezoneOfFixedRestTimeSetSetMemento#setTimezones(java.util.List)
	 */
	@Override
	public void setTimezones(List<DeductionTime> timzones) {
		if (CollectionUtil.isEmpty(timzones)) {
			this.entity.setKshmtFlexOdFixRests(new ArrayList<>());
		} else {
			if (CollectionUtil.isEmpty(this.entity.getKshmtFlexOdFixRests())) {
				this.entity.setKshmtFlexOdFixRests(new ArrayList<>());
			}
			Map<KshmtFlexOdFixRestPK, KshmtWtFleBrFiHolTs> mapEntity = this.entity.getKshmtFlexOdFixRests().stream()
					.collect(Collectors.toMap(KshmtWtFleBrFiHolTs::getKshmtFlexOdFixRestPK, Function.identity()));

			List<KshmtWtFleBrFiHolTs> lstNew = new ArrayList<>();
			for (int i = 0; i < timzones.size(); i++) {

				KshmtFlexOdFixRestPK newPK = new KshmtFlexOdFixRestPK(this.entity.getKshmtFlexOdRtSetPK().getCid(),
						this.entity.getKshmtFlexOdRtSetPK().getWorktimeCd(), i+1);
				KshmtWtFleBrFiHolTs newEntity = new KshmtWtFleBrFiHolTs(newPK);

				KshmtWtFleBrFiHolTs oldEntity = mapEntity.get(newPK);
				if (oldEntity != null) {
					// update
					newEntity = oldEntity;
				}
				// insert
				timzones.get(i).saveToMemento(new JpaFlexODDeductionTimeSetMemento(newEntity));
				lstNew.add(newEntity);
			}
			this.entity.setKshmtFlexOdFixRests(lstNew);
		}

	}

}
