package nts.uk.ctx.sys.portal.infra.repository.smartphonetoppageset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.NotificationDetailSet;

import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.NotificationDisplayItem;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSet;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageType;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.TimeStatusDetailsSet;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.TimeStatusDisplayItem;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.Type;
import nts.uk.ctx.sys.portal.infra.entity.smartphonetoppageset.SptmtSPTopPage;
import nts.uk.ctx.sys.portal.infra.entity.smartphonetoppageset.SptmtSPTopPageDetail;
import nts.uk.ctx.sys.portal.infra.entity.smartphonetoppageset.SptmtSPTopPageDetailPK;
import nts.uk.ctx.sys.portal.infra.entity.smartphonetoppageset.SptmtSPTopPagePK;;

/**
 * 
 * @author sonnh1
 *
 */
@Stateless
public class JpaSPTopPageSetRepository extends JpaRepository implements SPTopPageSetRepository {

	private final String SEL = "select s from SptmtSPTopPage s ";
	private final String SEL_CID_SYSTEM = SEL + "where s.pk.companyId = :companyId and s.pk.systemAtr = :system";

	private final String SEL_CID_SYSTEM_VALUE = SEL + "where s.pk.companyId = :companyId and s.pk.type = :type";

	private final String SEL_DETAIL = "select d from SptmtSPTopPageDetail d ";
	private final String SEL_DETAIL_BY_CID_TYPE = SEL_DETAIL
			+ "where d.pk.companyId = :companyId and d.pk.type = :type order by d.pk.detailType asc, d.pk.type asc";
	private final String SEL_ALL_DETAIL = SEL_DETAIL
			+ "where d.pk.companyId = :companyId"
			+ "order by d.pk.detailType asc"; 

	@Override
	public List<SPTopPageSet> getTopPageSet(String companyId, int system) {
		return this.queryProxy().query(SEL_CID_SYSTEM, SptmtSPTopPage.class).setParameter("companyId", companyId)
				.setParameter("system", system).getList(t -> toDomainTopPageSet(t));
	}
	
	@Override
	public SPTopPageSet getTopPageSetByCompanyAndType(String companyId, int type) {
		return toDomainTopPageSet(this.queryProxy()
				.query(SEL_CID_SYSTEM_VALUE, SptmtSPTopPage.class)
				.setParameter("companyId", companyId)
				.setParameter("type", type)
				.getSingleOrNull());
	}

	@Override
	public void update(List<SPTopPageSet> lstSPTopPageSet) {
		this.commandProxy()
				.updateAll(lstSPTopPageSet.stream().map(x -> toEntityTopPageSet(x)).collect(Collectors.toList()));
	}

	@Override
	public void updateNoti(NotificationDetailSet domain) {
		List<SptmtSPTopPageDetail> listEntity = new ArrayList<>();
		domain.getDetailedItem().forEach(x -> {
			int type = Type.NOTIFICATION.value;
			SptmtSPTopPageDetailPK pk = new SptmtSPTopPageDetailPK(domain.getCompanyId(), type,
					x.getDetailType().value);
			SptmtSPTopPageDetail entity = new SptmtSPTopPageDetail(pk, BooleanUtils.toBoolean(x.getDisplayAtr().value));
			listEntity.add(entity);
		});
		this.commandProxy().updateAll(listEntity);

	}

	@Override
	public void updateStatus(TimeStatusDetailsSet domain) {
		List<SptmtSPTopPageDetail> listEntity = new ArrayList<>();
		domain.getDetailedItem().forEach(x -> {
			int type = Type.TIME_STATUS.value;
			SptmtSPTopPageDetailPK pk = new SptmtSPTopPageDetailPK(domain.getCompanyId(), type,
					x.getDetailType().value);
			SptmtSPTopPageDetail entity = new SptmtSPTopPageDetail(pk, BooleanUtils.toBoolean(x.getDisplayAtr().value));
			listEntity.add(entity);
		});
		this.commandProxy().updateAll(listEntity);

	}

	@Override
	public Optional<TimeStatusDetailsSet> getTimeStatusDetailsSet(String companyId, int type) {
		List<SptmtSPTopPageDetail> lstEntity = this.queryProxy()
				.query(SEL_DETAIL_BY_CID_TYPE, SptmtSPTopPageDetail.class).setParameter("companyId", companyId)
				.setParameter("type", type).getList();

		return lstEntity.isEmpty() ? Optional.empty()
				: Optional.of(new TimeStatusDetailsSet(companyId,
						lstEntity.stream()
								.map(x -> TimeStatusDisplayItem.createFromJavaType(x.pk.detailType, BooleanUtils.toInteger(x.dispAtr)))
								.collect(Collectors.toList())));
	}

	@Override
	public Optional<NotificationDetailSet> getNotificationDetailSet(String companyId, int type) {
		List<SptmtSPTopPageDetail> lstEntity = this.queryProxy()
				.query(SEL_DETAIL_BY_CID_TYPE, SptmtSPTopPageDetail.class).setParameter("companyId", companyId)
				.setParameter("type", type).getList();

		return lstEntity.isEmpty() ? Optional.empty()
				: Optional.of(new NotificationDetailSet(companyId,
						lstEntity.stream()
								.map(x -> NotificationDisplayItem.createFromJavaType(x.pk.detailType, BooleanUtils.toInteger(x.dispAtr)))
								.collect(Collectors.toList())));
	}

	private SptmtSPTopPage toEntityTopPageSet(SPTopPageSet domain) {
		return new SptmtSPTopPage(new SptmtSPTopPagePK(domain.getCompanyId(),
				domain.getSmartPhoneTopPageType().getSystem().value, domain.getSmartPhoneTopPageType().getType().value),
				BooleanUtils.toBoolean(domain.getDisplayAtr().value));
	}

	private SPTopPageSet toDomainTopPageSet(SptmtSPTopPage entity) {
		if(entity == null) {
			return null;
		}
		return SPTopPageSet.createFromJavaType(entity.pk.companyId,
				SPTopPageType.createFromJavaType(entity.pk.systemAtr, entity.pk.type), BooleanUtils.toInteger(entity.dispAtr));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository#
	 * findAll(java.lang.String)
	 */
	private final String SEL_CID = SEL + "where s.pk.companyId = :companyId";

	@Override
	public List<SPTopPageSet> findAll(String companyID) {
		return this.queryProxy().query(SEL_CID, SptmtSPTopPage.class).setParameter("companyId", companyID)
				.getList(t -> toDomainTopPageSet(t));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository#
	 * findNotifi(java.lang.String)
	 */
	private final String SEL_DETAIL_BY_CID = SEL_DETAIL
			+ "where d.pk.companyId = :companyId "
			+ "order by d.pk.detailType asc"; 
	@Override
	public Optional<NotificationDetailSet> findNotifi(String companyID) {
		List<SptmtSPTopPageDetail> lstEntity = this.queryProxy().query(SEL_DETAIL_BY_CID, SptmtSPTopPageDetail.class)
				.setParameter("companyId", companyID).getList();

		return lstEntity.isEmpty() ? Optional.empty()
				: Optional.of(new NotificationDetailSet(companyID,
						lstEntity.stream()
								.map(x -> NotificationDisplayItem.createFromJavaType(x.pk.detailType, BooleanUtils.toInteger(x.dispAtr)))
								.collect(Collectors.toList())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository#
	 * findTimeStatus()
	 */
	@Override
	public Optional<TimeStatusDetailsSet> findTimeStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository#
	 * findAllTimeStatusDetailsSet(java.lang.String)
	 */

	@Override
	public Optional<TimeStatusDetailsSet> findAllTimeStatusDetailsSet(String companyID) {
		List<SptmtSPTopPageDetail> lstEntity = this.queryProxy().query(SEL_ALL_DETAIL, SptmtSPTopPageDetail.class)
				.setParameter("companyId", companyID).getList();

		return lstEntity.isEmpty() ? Optional.empty()
				: Optional.of(new TimeStatusDetailsSet(companyID,
						lstEntity.stream()
								.map(x -> TimeStatusDisplayItem.createFromJavaType(x.pk.detailType, BooleanUtils.toInteger(x.dispAtr)))
								.collect(Collectors.toList())));
	}

}
