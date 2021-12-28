package nts.uk.ctx.sys.portal.infra.repository.toppagealarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.portal.dom.toppagealarm.AlarmClassification;
import nts.uk.ctx.sys.portal.dom.toppagealarm.AlarmListPatternCode;
import nts.uk.ctx.sys.portal.dom.toppagealarm.DisplayAtr;
import nts.uk.ctx.sys.portal.dom.toppagealarm.LinkURL;
import nts.uk.ctx.sys.portal.dom.toppagealarm.NotificationId;
import nts.uk.ctx.sys.portal.dom.toppagealarm.ToppageAlarmData;
import nts.uk.ctx.sys.portal.dom.toppagealarm.ToppageAlarmDataRepository;
import nts.uk.ctx.sys.portal.infra.entity.toppagealarm.SptdtTopAlarmSubSya;
import nts.uk.ctx.sys.portal.infra.entity.toppagealarm.SptdtToppageAlarm;
import nts.uk.ctx.sys.portal.infra.entity.toppagealarm.SptdtToppageAlarmPK;

@Stateless
public class JpaToppageAlarmDataRepository extends JpaRepository implements ToppageAlarmDataRepository {
	
	// Select all
	private static final String QUERY_SELECT_ALL = " SELECT m FROM SptdtToppageAlarm m ";
	
	// Select all by PK without INDEX_NO
	private static final String QUERY_SELECT_ALL_BY_PK = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cid "
			+ " AND m.pk.alarmCls = :alarmCls "
			+ " AND m.pk.dispSid = :dispSid "
			+ " AND m.pk.dispAtr = :dispAtr "
			+ " ORDER BY m.pk.indexNo ";
	
	// Select all by PK without INDEX_NO
	private static final String QUERY_SELECT_ALL_BY_PKs = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId IN :cids "
			+ " AND m.pk.alarmCls IN :alarmCls "
			+ " AND m.pk.dispSid IN :dispSids "
			+ " AND m.pk.dispAtr IN :dispAtrs "
			+ " ORDER BY m.pk.indexNo ";
	
	// Select unread
	private static final String QUERY_SELECT_UNREAD = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cId "
			+ " AND m.pk.dispSid = :sId "
			+ " AND m.resolved = 0 " //解消済みである = false
			+ "	ORDER BY m.pk.alarmCls, m.crtDatetime ASC ";

	// Select auto run alarm
	private static final String QUERY_SELECT_AUTO_RUN_ALARM = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cid "
			+ " AND m.pk.dispSid IN :dispSids "
			+ " AND m.pk.alarmCls = :alarmCls "
			+ " AND m.pk.dispAtr = 1 " //表示社員区分　=　上長
			+ " AND m.resolved = 0 " //解消済みである = false
			+ " ORDER BY m.crtDatetime ASC ";
	
	// Select alarm list
	private static final String QUERY_SELECT_ALARM_LIST = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cid "
			+ " AND m.pk.alarmCls = 0 " //アラーム分類　=　アラームリスト
			+ " AND m.pk.dispSid IN :dispSids "
			+ " AND m.patternCode = :patternCode "
			+ " AND m.pk.dispAtr = :dispAtr "
			+ " AND m.resolved = 0 " //解消済みである = false
			+ " ORDER BY m.crtDatetime ASC ";
	
	// Select alarm list
	private static final String QUERY_SELECT_SINGLE = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cid "
			+ " AND m.pk.dispSid = :dispSid "
			+ " AND m.pk.dispAtr = :dispAtr "
			+ " AND m.pk.alarmCls = :alarmCls "
			+ " AND m.resolved = 0 " //解消済みである = false
			+ " AND ( "
				+ " m.pk.alarmCls = 1 " //更新処理自動実行内部エラー
				+ " OR m.pk.alarmCls = 2 " //更新処理自動実行動作異常
				+ " OR (m.pk.alarmCls = 3 AND m.notificationId = :notificationId) "
				+ " OR (m.pk.alarmCls = 0 AND m.patternCode = :patternCode) "
			+ " ) ";
	
	private static final String QUERY_SELECT_SINGLE_FOR_UPDATE = QUERY_SELECT_ALL
			+ " WHERE m.pk.cId = :cid "
			+ " AND m.pk.dispSid = :dispSid "
			+ " AND m.pk.dispAtr = :dispAtr "
			+ " AND m.pk.alarmCls = :alarmCls "
			+ " AND m.resolved = 0 " //解消済みである = false
			+ " AND ( "
				+ " m.pk.alarmCls = 1 " //更新処理自動実行内部エラー
				+ " OR m.pk.alarmCls = 2 " //更新処理自動実行動作異常
				+ " OR (m.pk.alarmCls = 3 AND m.notificationId = :notificationId) "
				+ " OR (m.pk.alarmCls = 0 AND m.patternCode = :patternCode) "
			+ " ) ";
	
	public SptdtToppageAlarm toEntityWithIndexNo(ToppageAlarmData domain) {
		//get all by PK
		List<SptdtToppageAlarm> entities = this.queryProxy()
		.query(QUERY_SELECT_ALL_BY_PK, SptdtToppageAlarm.class)
		.setParameter("cid", domain.getCid())
		.setParameter("alarmCls", domain.getAlarmClassification().value)
		.setParameter("dispSid", domain.getDisplaySId())
		.setParameter("dispAtr", domain.getDisplayAtr().value)
		.getList();
		
		int indexNo = 0;
		
		if (!entities.isEmpty()) {
			// get lastest index no
			indexNo = entities.get(entities.size() - 1).getPk().getIndexNo() + 1;
		}
		
		// Convert data to entity
		return SptdtToppageAlarm.toEntity(domain, indexNo);
	}

	@Override
	public void insert(ToppageAlarmData domain) {
		
		// Convert data to entity
		SptdtToppageAlarm entity = this.toEntityWithIndexNo(domain);

		// Insert entity
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(ToppageAlarmData domain) {

		Optional<SptdtToppageAlarm> oldEntity = this.queryProxy().query(QUERY_SELECT_SINGLE_FOR_UPDATE, SptdtToppageAlarm.class)
				.setParameter("cid", domain.getCid())
				.setParameter("dispSid", domain.getDisplaySId())
				.setParameter("dispAtr", domain.getDisplayAtr().value)
				.setParameter("alarmCls", domain.getAlarmClassification().value)
				.setParameter("patternCode", domain.getPatternCode().map(AlarmListPatternCode::v).orElse(""))
				.setParameter("notificationId", domain.getNotificationId().map(NotificationId::v).orElse(""))
				.getSingle();
	
		if (oldEntity.isPresent()) {
			
			List<SptdtTopAlarmSubSya> listSubSids = SptdtToppageAlarm.subSidsToEntity(domain.getCid(), domain.getDisplaySId(), domain.getSubSids(),
					domain.getPatternCode().map(AlarmListPatternCode::v).orElse(""));

			//delete subSids
			oldEntity.get().getSubSids().clear();
			this.commandProxy().update(oldEntity.get());
			this.getEntityManager().flush();
			
			oldEntity.get().setPatternCode(domain.getPatternCode().map(AlarmListPatternCode::v).orElse(null));
			oldEntity.get().setNotificationId(domain.getNotificationId().map(NotificationId::v).orElse(null));
			oldEntity.get().setCrtDatetime(domain.getOccurrenceDateTime());
			oldEntity.get().setMessege(domain.getDisplayMessage().v());
			oldEntity.get().setLinkUrl(domain.getLinkUrl().map(LinkURL::v).orElse(null));
			oldEntity.get().setReadDateTime(domain.getReadDateTime().orElse(null));
			oldEntity.get().setResolved(domain.getIsResolved() ? 1 : 0);
			oldEntity.get().setSubSids(listSubSids);
			
			// Update entity
			this.commandProxy().update(oldEntity.get());
		};

	}
	
	@Override
	public List<ToppageAlarmData> getAll(String companyId, String sId) {
		List<ToppageAlarmData> unreadDomains = this.queryProxy()
				.query(QUERY_SELECT_UNREAD, SptdtToppageAlarm.class)
				.setParameter("cId", companyId)
				.setParameter("sId", sId)
				.getList(SptdtToppageAlarm::toDomain);

		return unreadDomains.stream().filter(domain -> this.filterOccurrenceDateTime(domain))
				.collect(Collectors.toList());
	}

	@Override
	public List<ToppageAlarmData> getUnread(String companyId, String sId) {
		 List<ToppageAlarmData> unreadDomains = this.queryProxy()
				.query(QUERY_SELECT_UNREAD, SptdtToppageAlarm.class)
				.setParameter("cId", companyId)
				.setParameter("sId", sId)
				.getList(SptdtToppageAlarm::toDomain);
		 
		 return unreadDomains.stream().filter(domain -> this.filterUnread(domain)).collect(Collectors.toList());
	}
	
	private boolean filterUnread(ToppageAlarmData domain) {
		if (this.filterOccurrenceDateTime(domain) && this.filterReadDateTime(domain)) {
			return true;
		}
		return false;
	}

	private boolean filterOccurrenceDateTime(ToppageAlarmData domain) {
		if (GeneralDateTime.now().addYears(-1).beforeOrEquals(domain.getOccurrenceDateTime())) {
			return true;
		}
		return false;
	}

	private boolean filterReadDateTime(ToppageAlarmData domain) {
		if (!domain.getReadDateTime().isPresent()) {
			return true;
		} else if (domain.getOccurrenceDateTime().after(domain.getReadDateTime().get())) {
			return true;
		}
		return false;
	}

	@Override
	public void updateAll(List<ToppageAlarmData> domains) {
		// Convert data to entity
		List<SptdtToppageAlarm> entities = domains.stream().map(mapper -> this.toEntityWithIndexNo(mapper)).collect(Collectors.toList());
		
		//get all by PK
		List<String> cids = domains.stream().map(ToppageAlarmData::getCid).collect(Collectors.toList());
		List<Integer> alarmCls = domains.stream().map(mapper -> mapper.getAlarmClassification().value).collect(Collectors.toList());
		List<String> dispSids = domains.stream().map(ToppageAlarmData::getDisplaySId).collect(Collectors.toList());
		List<Integer> dispAtrs = domains.stream().map(mapper -> mapper.getDisplayAtr().value).collect(Collectors.toList());
		
		List<SptdtToppageAlarm> oldEntities = this.queryProxy()
		.query(QUERY_SELECT_ALL_BY_PKs, SptdtToppageAlarm.class)
		.setParameter("cids", cids)
		.setParameter("alarmCls", alarmCls)
		.setParameter("dispSids", dispSids)
		.setParameter("dispAtrs", dispAtrs)
		.getList();
		
		List<SptdtToppageAlarm> updateEntities = new ArrayList<>();
		
		oldEntities.stream().forEach(oldEntity -> {
			
			Optional<SptdtToppageAlarm> entity = entities.stream()
					.filter(e -> this.comparePk(e.getPk(), oldEntity.getPk()))
					.findFirst();
			
			entity.ifPresent(updateEntity -> {
				oldEntity.setPatternCode(updateEntity.getPatternCode());
				oldEntity.setNotificationId(updateEntity.getNotificationId());
				oldEntity.setCrtDatetime(updateEntity.getCrtDatetime());
				oldEntity.setMessege(updateEntity.getMessege());
				oldEntity.setLinkUrl(updateEntity.getLinkUrl());
				oldEntity.setReadDateTime(updateEntity.getReadDateTime());
				oldEntity.setResolved(updateEntity.getResolved());
				oldEntity.setSubSids(updateEntity.getSubSids());
				
				// Update entity
				updateEntities.add(oldEntity);
			});
		});
		
		// Update entities
		this.commandProxy().updateAll(updateEntities);
	}
	
	private boolean comparePk(SptdtToppageAlarmPK pk1, SptdtToppageAlarmPK pk2) {
		return pk1.getCId().equals(pk2.getCId())
			&& pk1.getAlarmCls().equals(pk2.getAlarmCls()) 
			&& pk1.getDispSid().equals(pk2.getDispSid())
			&& pk1.getDispAtr().equals(pk2.getDispAtr());
	}

	@Override
	public List<ToppageAlarmData> getAutoRunAlarm(String cid, AlarmClassification alarmCls, List<String> sids) {
		List<ToppageAlarmData> results = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subSids -> {
			results.addAll(this.queryProxy()
					.query(QUERY_SELECT_AUTO_RUN_ALARM, SptdtToppageAlarm.class)
					.setParameter("cid", cid)
					.setParameter("dispSids", subSids)
					.setParameter("alarmCls", alarmCls.value)
					.getList(SptdtToppageAlarm::toDomain));
		});
		return results;
	}

	@Override
	public List<ToppageAlarmData> getAlarmList(String cid, List<String> sids, DisplayAtr displayAtr,
			AlarmListPatternCode patternCode) {
		List<ToppageAlarmData> results = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subSids -> {
			results.addAll(this.queryProxy()
					.query(QUERY_SELECT_ALARM_LIST, SptdtToppageAlarm.class)
					.setParameter("cid", cid)
					.setParameter("dispSids", subSids)
					.setParameter("patternCode", patternCode.v())
					.setParameter("dispAtr", displayAtr.value)
					.getList(SptdtToppageAlarm::toDomain));
		});
		return results;
	}

	@Override
	public Optional<ToppageAlarmData> get(String cid, String sid, int dispAtr, int alarmCls, Optional<String> patternCode, Optional<String> notificationId) {
		return this.queryProxy().query(QUERY_SELECT_SINGLE, SptdtToppageAlarm.class)
				.setParameter("cid", cid)
				.setParameter("dispSid", sid)
				.setParameter("dispAtr", dispAtr)
				.setParameter("alarmCls", alarmCls)
				.setParameter("patternCode", patternCode.orElse(""))
				.setParameter("notificationId", notificationId.orElse(""))
				.getSingle(SptdtToppageAlarm::toDomain);
	}
}
