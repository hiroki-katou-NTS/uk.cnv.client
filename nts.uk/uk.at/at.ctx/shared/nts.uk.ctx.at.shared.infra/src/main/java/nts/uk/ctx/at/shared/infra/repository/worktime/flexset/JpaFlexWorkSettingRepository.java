/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.flexset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezone;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeMethodSet;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtCom;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWorktimeCommonSetPK;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtWtFleStmpRefTs;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtFlexStampReflectPK;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtWtFle;
import nts.uk.ctx.at.shared.infra.entity.worktime.flexset.KshmtFlexWorkSetPK;

/**
 * The Class JpaFlexWorkSettingRepository.
 */
@Stateless
public class JpaFlexWorkSettingRepository extends JpaRepository
		implements FlexWorkSettingRepository {

	private static final String SEL_1 = "SELECT a FROM KshmtWtFle a WHERE a.kshmtFlexWorkSetPK.cid =:cid AND a.kshmtFlexWorkSetPK.worktimeCd IN :worktimeCd";
	
	private static final String SEL_2 = "SELECT a FROM KshmtWtCom a WHERE a.kshmtWorktimeCommonSetPK.cid =:cid AND a.kshmtWorktimeCommonSetPK.workFormAtr =:workFormAtr AND  a.kshmtWorktimeCommonSetPK.worktimeSetMethod =:worktimeSetMethod AND a.kshmtWorktimeCommonSetPK.worktimeCd IN :worktimeCd";
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository#
	 * findById(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<FlexWorkSetting> find(String companyId, String worktimeCode) {
		return this.findWorkSetting(companyId, worktimeCode)
				.map(entity -> this.toDomain(entity, this.findCommonSetting(companyId, worktimeCode).get()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository#
	 * saveFlexWorkSetting(nts.uk.ctx.at.shared.dom.worktime.flexset.
	 * FlexWorkSetting)
	 */
	@Override
	public void add(FlexWorkSetting domain) {
		KshmtWtFle entity = new KshmtWtFle();
		KshmtWtCom entityCommon = new KshmtWtCom(
				new KshmtWorktimeCommonSetPK(domain.getCompanyId(), domain.getWorkTimeCode().v(),
						WorkTimeDailyAtr.FLEX_WORK.value, WorkTimeMethodSet.FIXED_WORK.value));
		domain.saveToMemento(new JpaFlexWorkSettingSetMemento(entity, entityCommon));
		this.commandProxy().insert(entity);
		this.commandProxy().insert(entityCommon);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository#
	 * update(nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting)
	 */
	@Override
	public void update(FlexWorkSetting domain) {
		KshmtWtFle entity = this.findWorkSetting(domain.getCompanyId(), domain.getWorkTimeCode().v()).get();
		KshmtWtCom entityCommon = this
				.findCommonSetting(domain.getCompanyId(), domain.getWorkTimeCode().v()).get();
		domain.saveToMemento(new JpaFlexWorkSettingSetMemento(entity, entityCommon));
		this.commandProxy().update(entity);
		this.commandProxy().update(entityCommon);
		removeRefTimeNo2(entity);
	}
	
	private void removeRefTimeNo2(KshmtWtFle entity) {		
		// this algorithm for remove RefTimeNo2 if not Use
				boolean notUseRefTimeNo2 = !entity.getKshmtFlexStampReflects().stream()
						.filter(x -> x.getKshmtFlexStampReflectPK().getWorkNo() == 2).findAny().isPresent();
				if (notUseRefTimeNo2) {
					entity.getKshmtFlexStampReflects().stream().filter(x -> x.getKshmtFlexStampReflectPK().getWorkNo() == 1)
					.findFirst().ifPresent(x -> {
						KshmtFlexStampReflectPK pk = x.getKshmtFlexStampReflectPK();
						String SEL_REF_TIME_NO_2 = "SELECT a FROM KshmtWtFleStmpRefTs a WHERE "
								+ "a.kshmtFlexStampReflectPK.cid= :cid "
								+ "AND a.kshmtFlexStampReflectPK.worktimeCd = :worktimeCd "
								+ "AND a.kshmtFlexStampReflectPK.workNo = 2";
						// get No 2
						List<KshmtWtFleStmpRefTs> no2Items = this.queryProxy()
								.query(SEL_REF_TIME_NO_2, KshmtWtFleStmpRefTs.class)
								.setParameter("cid", pk.getCid())
								.setParameter("worktimeCd", pk.getWorktimeCd()).getList();

						if (!no2Items.isEmpty()) {
							this.commandProxy().removeAll(no2Items);
						}
					});
				}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository#
	 * remove(java.lang.String, java.lang.String)
	 */
	@Override
	public void remove(String companyId, String workTimeCode) {
		this.commandProxy().remove(KshmtWtFle.class, new KshmtFlexWorkSetPK(companyId, workTimeCode));
	}
	
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @return the flex work setting
	 */
	private FlexWorkSetting toDomain(KshmtWtFle entity, KshmtWtCom entityCommon) {
		return new FlexWorkSetting(new JpaFlexWorkSettingGetMemento(entity, entityCommon));
	}

	
	/**
	 * Find work setting.
	 *
	 * @param companyId the company id
	 * @param worktimeCode the worktime code
	 * @return the optional
	 */
	private Optional<KshmtWtFle> findWorkSetting(String companyId, String worktimeCode) {
		return this.queryProxy().find(new KshmtFlexWorkSetPK(companyId, worktimeCode),
				KshmtWtFle.class);
	}
	
	/**
	 * Find common setting.
	 *
	 * @param companyId the company id
	 * @param worktimeCode the worktime code
	 * @return the optional
	 */
	private Optional<KshmtWtCom> findCommonSetting(String companyId, String worktimeCode ) {
		return this.queryProxy().find(new KshmtWorktimeCommonSetPK(companyId, worktimeCode,
				WorkTimeDailyAtr.FLEX_WORK.value, WorkTimeMethodSet.FIXED_WORK.value), KshmtWtCom.class);
	}

	@Override
	public List<FlexWorkSetting> getAllByCidAndWorkCodes(String cid, List<String> workTimeCodes) {
		List<FlexWorkSetting> result = new ArrayList<>();
		List<KshmtWtFle> kshmtFlexWorkSet = this.queryProxy().query(SEL_1, KshmtWtFle.class)
				.setParameter("cid", cid).setParameter("worktimeCd", workTimeCodes).getList();
		List<KshmtWtCom> kshmtWorktimeCommonSet = this.queryProxy().query(SEL_2, KshmtWtCom.class)
				.setParameter("cid", cid).setParameter("worktimeCd", workTimeCodes)
				.setParameter("workFormAtr", WorkTimeDailyAtr.FLEX_WORK.value).setParameter("worktimeSetMethod",  WorkTimeMethodSet.FIXED_WORK.value)
				.getList();
		kshmtFlexWorkSet.parallelStream().forEach(c ->{
			Optional<KshmtWtCom> kshmtWorktimeCommonSetOpt = kshmtWorktimeCommonSet.parallelStream().filter(item -> item.getKshmtWorktimeCommonSetPK().getWorktimeCd().equals(c.getKshmtFlexWorkSetPK().getWorktimeCd())).findFirst();
			result.add(this.toDomain(c, kshmtWorktimeCommonSetOpt.get()));
		});
		return result;
	}

}
