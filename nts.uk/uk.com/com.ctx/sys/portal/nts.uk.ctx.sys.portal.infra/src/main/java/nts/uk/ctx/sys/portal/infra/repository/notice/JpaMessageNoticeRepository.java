package nts.uk.ctx.sys.portal.infra.repository.notice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.portal.dom.notice.DestinationClassification;
import nts.uk.ctx.sys.portal.dom.notice.MessageNotice;
import nts.uk.ctx.sys.portal.dom.notice.MessageNoticeRepository;
import nts.uk.ctx.sys.portal.infra.entity.notice.SptdtInfoMessage;
import nts.uk.ctx.sys.portal.infra.entity.notice.SptdtInfoMessagePK;
import nts.uk.ctx.sys.portal.infra.entity.notice.SptdtInfoMessageRead;
import nts.uk.ctx.sys.portal.infra.entity.notice.SptdtInfoMessageReadPK;
import nts.uk.ctx.sys.portal.infra.entity.notice.SptdtInfoMessageTgt;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaMessageNoticeRepository extends JpaRepository implements MessageNoticeRepository {
	
	private static final String GET_BY_DEST_CATEGORY = String.join(" "
			, "SELECT m FROM SptdtInfoMessage m WHERE m.pk.sid = :sid"
			, "AND m.startDate <= :endDate"
			, "AND m.endDate >= :startDate"
			, "AND m.destination = :destination"
			, "ORDER BY m.startDate DESC, m.endDate DESC, m.pk.inputDate DESC");
	
	private static final String GET_BY_PERIOD_AND_SID = String.join(" "
			, "SELECT m FROM SptdtInfoMessage m WHERE m.pk.sid = :sid"
			, "AND m.startDate <= :endDate"
			, "AND m.endDate >= :startDate"
			, "ORDER BY m.startDate DESC, m.endDate DESC, m.pk.inputDate DESC");
	
	private static final String GET_FROM_LIST_WORKPLACE_ID = String.join(" "
			, "SELECT m FROM SptdtInfoMessage m JOIN SptdtInfoMessageTgt n"
			, "ON m.pk.sid = n.pk.sid AND m.pk.inputDate = n.pk.inputDate"
			, "WHERE m.startDate <= :endDate"
			, "AND m.endDate >= :startDate"
			, "AND ((m.destination = 0 AND m.companyId = :companyId)"
			, "OR (m.destination = 1 AND n.pk.tgtInfoId IN :tgtInfoId))"
			, "ORDER BY m.destination ASC, m.startDate DESC, m.endDate DESC, m.pk.inputDate DESC");
	
	private static final String GET_MSG_REF_BY_PERIOD = String.join(" "
			, "SELECT m FROM SptdtInfoMessage m"
			, "LEFT JOIN SptdtInfoMessageTgt n ON m.pk.sid = n.pk.sid AND m.pk.inputDate = n.pk.inputDate"
			, "LEFT JOIN SptdtInfoMessageRead s ON m.pk.sid = s.pk.sid AND m.pk.inputDate = s.pk.inputDate"
			, "AND s.pk.readSid = :sid"
			, "WHERE m.companyId = :cid AND m.startDate <= :endDate"
			, "AND m.endDate >= :startDate"
			, "AND (m.destination = 0"
			, "OR (m.destination = 1 AND n.pk.tgtInfoId = :wpId)"
			, "OR (m.destination = 2 AND n.pk.tgtInfoId = :sid))"
			, "ORDER BY m.startDate DESC, m.endDate DESC, m.pk.inputDate DESC");
	
	private static String NATIVE_GET_NEW_MSG_FOR_DAY = String.join(" "
			, "SELECT * FROM ("
			, "SELECT M.*, S.READ_SID FROM SPTDT_INFO_MESSAGE M"
			, "LEFT JOIN SPTDT_INFO_MESSAGE_TGT N ON M.INPUT_DATE = N.INPUT_DATE AND M.SID = N.SID"
			, "LEFT JOIN SPTDT_INFO_MESSAGE_READ S ON M.INPUT_DATE = S.INPUT_DATE AND M.SID = S.SID"
			, "AND S.READ_SID = ?SID"
			, "WHERE M.CID = ?CID AND M.START_DATE <= ?CURRENTDATE AND M.END_DATE >= ?CURRENTDATE"
			, "AND (M.DESTINATION_ATR = 0 or (M.DESTINATION_ATR = 1 and N.TGT_INFO_ID = ?WKPID and ?WKPID IS NOT NULL)"
			, "OR (M.DESTINATION_ATR = 2 AND N.TGT_INFO_ID = ?SID))"
			, ") A"
			, "WHERE READ_SID <> ?SID OR READ_SID IS NULL"
			, "ORDER BY DESTINATION_ATR ASC, START_DATE DESC");
	
	private static final String NATIVE_GET_REF_BY_SID_FOR_PERIOD = String.join(" "
			, "SELECT m.*, s.READ_SID"
			, "FROM SPTDT_INFO_MESSAGE m"
			, "INNER JOIN SPTDT_INFO_MESSAGE_TGT n ON m.SID = n.SID AND m.INPUT_DATE = n.INPUT_DATE"
			, "AND m.START_DATE <= ?endDate"
			, "AND m.END_DATE >= ?startDate"
			, "AND m.DESTINATION_ATR = 2"
			, "AND n.TGT_INFO_ID = ?sid"
			, "LEFT JOIN SPTDT_INFO_MESSAGE_READ s"
			, "ON m.SID = s.SID AND m.INPUT_DATE = s.INPUT_DATE AND s.READ_SID = ?sid"
			, "ORDER BY m.START_DATE DESC, m.END_DATE DESC, m.INPUT_DATE DESC");
	
	private static final String GET_BY_DEST_CATEGORY_AND_CID = String.join(" "
			, "SELECT m FROM SptdtInfoMessage m WHERE m.companyId = :companyId"
			, "AND m.startDate <= :endDate"
			, "AND m.endDate >= :startDate"
			, "AND m.destination = :destination"
			, "ORDER BY m.startDate DESC, m.endDate DESC, m.pk.inputDate DESC");
	
	/**
	 * Convert entity to domain
	 * @param entity
	 * @return MessageNotice
	 */
	public static MessageNotice toDomain(SptdtInfoMessage entity) {
		return MessageNotice.createFromMemento(entity);
	}
	
	/**
	 * Convert domain to entity
	 * @param entity
	 * @return SptdtInfoMessage
	 */
	public static SptdtInfoMessage toEntity(MessageNotice domain) {
		SptdtInfoMessage entity = new SptdtInfoMessage();
		domain.setMemento(entity);
		entity.setCompanyId(AppContexts.user().companyId());
		entity.setContractCd(AppContexts.user().contractCode());
		return entity;
	}

	@Override
	public void insert(MessageNotice msg) {
		this.commandProxy().insert(toEntity(msg));
	}

	@Override
	public void update(MessageNotice msg) {
		SptdtInfoMessage entity = toEntity(msg);
		SptdtInfoMessage oldEntity = this.queryProxy().find(entity.getPk(), SptdtInfoMessage.class).get();

		this.commandProxy().removeAll(SptdtInfoMessageTgt.class
				, oldEntity.getSptdtInfoMessageTgts().stream().map(x -> x.getPk()).collect(Collectors.toList()));

		this.getEntityManager().flush();
		oldEntity.setContractCd(AppContexts.user().contractCode());
		oldEntity.setCompanyId(AppContexts.user().companyId());
		oldEntity.setStartDate(entity.getStartDate());
		oldEntity.setEndDate(entity.getEndDate());
		oldEntity.setUpdateDate(GeneralDateTime.now());
		oldEntity.setMessage(entity.getMessage());
		oldEntity.setDestination(entity.getDestination());
		oldEntity.setTargetInformation(entity.getTargetInformation());

		this.getEntityManager().flush();
		// Update entity
		this.commandProxy().update(oldEntity);
	}

	@Override
	public void delete(MessageNotice msg) {
		SptdtInfoMessage entity = toEntity(msg);
		this.commandProxy().remove(SptdtInfoMessage.class, entity.getPk());
	}

	@Override
	public List<MessageNotice> getMsgInDestinationCategory(DatePeriod period, DestinationClassification destination,
			String sid) {
		return this.queryProxy()
					.query(GET_BY_DEST_CATEGORY, SptdtInfoMessage.class)
					.setParameter("sid", sid)
					.setParameter("endDate", period.end())
					.setParameter("startDate", period.start())
					.setParameter("destination", destination.value)
					.getList(MessageNotice::createFromMemento);
	}

	@Override
	public List<MessageNotice> getMsgByPeriodAndSid(DatePeriod period, String sid) {
		return this.queryProxy()
				.query(GET_BY_PERIOD_AND_SID, SptdtInfoMessage.class)
				.setParameter("sid", sid)
				.setParameter("endDate", period.end())
				.setParameter("startDate", period.start())
				.getList(MessageNotice::createFromMemento);
	}

	@Override
	public List<MessageNotice> getMsgFromWpIdList(DatePeriod period, List<String> wpIds, String cid) {
		return this.queryProxy()
				.query(GET_FROM_LIST_WORKPLACE_ID, SptdtInfoMessage.class)
				.setParameter("endDate", period.end())
				.setParameter("startDate", period.start())
				.setParameter("companyId", cid)
				.setParameter("tgtInfoId", wpIds)
				.getList(MessageNotice::createFromMemento);
	}

	@Override
	public List<MessageNotice> getMsgRefByPeriod(String cid, DatePeriod period, Optional<String> wpId, String sid) {
		List<SptdtInfoMessage> entities = new ArrayList<SptdtInfoMessage>();
		if (wpId.isPresent()) {
			entities = this.queryProxy().query(GET_MSG_REF_BY_PERIOD, SptdtInfoMessage.class)
							.setParameter("cid", cid)
							.setParameter("endDate", period.end())
							.setParameter("startDate", period.start())
							.setParameter("wpId", wpId.get())
							.setParameter("sid", sid)
							.getList();
		} else {
			String queryString = GET_MSG_REF_BY_PERIOD.replace(" OR (m.destination = 1 AND n.pk.tgtInfoId = :wpId)", "");
			entities = this.queryProxy().query(queryString, SptdtInfoMessage.class)
							.setParameter("cid", cid)
							.setParameter("endDate", period.end())
							.setParameter("startDate", period.start())
							.setParameter("sid", sid)
							.getList();
		}
		
		return entities.stream().map(entity -> toDomain(entity)).collect(Collectors.toList());
	}

	@Override
	public List<MessageNotice> getNewMsgForDay(String cid, Optional<String> wpId) {
		String sid = AppContexts.user().employeeId();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = getEntityManager()
										.createNativeQuery(NATIVE_GET_NEW_MSG_FOR_DAY)
											.setParameter("CID", cid)
											.setParameter("SID", sid)
											.setParameter("WKPID", wpId.orElse(null))
											.setParameter("CURRENTDATE", GeneralDate.today().date())
										. getResultList();
		List<MessageNotice> list = resultList.stream()
				.map(item -> {
					SptdtInfoMessage entity = new SptdtInfoMessage();
					SptdtInfoMessagePK entityPk = new SptdtInfoMessagePK();
					entityPk.setSid(item[11].toString());
					entityPk.setInputDate(GeneralDateTime.fromString(item[12].toString().substring(0, 21), "yyyy-MM-dd HH:mm:ss.S"));
					entity.setPk(entityPk);
					entity.setVersion(Long.parseLong(item[8].toString()));
					entity.setContractCd(item[9].toString());
					entity.setCompanyId(item[10].toString());
					entity.setStartDate(GeneralDate.fromString(item[13].toString().substring(0, 21), "yyyy-MM-dd hh:mm:ss.S"));
					entity.setEndDate(GeneralDate.fromString(item[14].toString().substring(0, 21), "yyyy-MM-dd hh:mm:ss.S"));
					entity.setUpdateDate(GeneralDateTime.fromString(item[15].toString().substring(0, 21), "yyyy-MM-dd HH:mm:ss.S"));
					entity.setMessage(item[16].toString());
					entity.setDestination(Integer.parseInt(item[17].toString()));
					MessageNotice domain = new MessageNotice();
					domain.getMemento(entity);
					return domain;
				})
				.collect(Collectors.toList());
		return list;
	}

	@Override
	public List<MessageNotice> getMsgRefBySidForPeriod(DatePeriod period, String sid) {
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = this.getEntityManager().createNativeQuery(NATIVE_GET_REF_BY_SID_FOR_PERIOD)
				.setParameter("endDate", period.end().toString()).setParameter("startDate", period.start().toString())
				.setParameter("sid", sid).getResultList();

		List<MessageNotice> list = resultList.stream().map(item -> {
			String formatDate = "";
			switch (item[12].toString().length()) {
			case 21:
				formatDate = "yyyy-MM-dd HH:mm:ss.S";
				break;
			case 22:
				formatDate = "yyyy-MM-dd HH:mm:ss.SS";
				break;
			case 23:
				formatDate = "yyyy-MM-dd HH:mm:ss.SSS";
				break;
			default:
				formatDate = "yyyy-MM-dd HH:mm:ss.S";
				break;
			}

			SptdtInfoMessage entity = new SptdtInfoMessage();
			SptdtInfoMessagePK entityPk = new SptdtInfoMessagePK();
			entityPk.setSid(item[11].toString());
			entityPk.setInputDate(
					GeneralDateTime.fromString(item[12].toString().substring(0, item[12].toString().length()), formatDate));
			entity.setPk(entityPk);
			entity.setVersion(Long.parseLong(item[8].toString()));
			entity.setContractCd(item[9].toString());
			entity.setCompanyId(item[10].toString());
			entity.setStartDate(GeneralDate.fromString(item[13].toString().substring(0, 21), "yyyy-MM-dd hh:mm:ss.S"));
			entity.setEndDate(GeneralDate.fromString(item[14].toString().substring(0, 21), "yyyy-MM-dd hh:mm:ss.S"));
			entity.setUpdateDate(
					GeneralDateTime.fromString(item[15].toString().substring(0, 21), "yyyy-MM-dd HH:mm:ss.S"));
			entity.setMessage(item[16].toString());
			entity.setDestination(Integer.parseInt(item[17].toString()));

			List<String> sidSeen = new ArrayList<String>();
			if (item[18] != null) {
				sidSeen.add(item[18].toString());
			}
			entity.setEmployeeIdSeen(sidSeen);
			MessageNotice domain = new MessageNotice();
			domain.getMemento(entity);
			return domain;
		}).collect(Collectors.toList());

		return list;
		
	}

	@Override
	public void updateInforSawMessage(MessageNotice msg, String sid) {
		SptdtInfoMessageReadPK pk = SptdtInfoMessageReadPK.builder()
										.sid(msg.getCreatorID())
										.inputDate(msg.getInputDate())
										.readSid(sid)
										.build();
		SptdtInfoMessageRead sptdtInfoMessageRead = new SptdtInfoMessageRead();
		sptdtInfoMessageRead.setPk(pk);
		sptdtInfoMessageRead.setContractCd(AppContexts.user().contractCode());
		sptdtInfoMessageRead.setCompanyId(AppContexts.user().companyId());
		SptdtInfoMessage sptdtInfoMessage = toEntity(msg);
		sptdtInfoMessageRead.setSptdtInfoMessage(sptdtInfoMessage);
		this.commandProxy().insert(sptdtInfoMessageRead);
	}
	
	@Override
	public List<MessageNotice> getByCreatorIdAndInputDate(String creatorId, GeneralDateTime inputDate) {
		String query = "SELECT m FROM SptdtInfoMessage m WHERE m.pk.sid = :sid AND m.pk.inputDate = :inputDate";
		return this.queryProxy().query(query, SptdtInfoMessage.class)
				.setParameter("sid", creatorId)
				.setParameter("inputDate", inputDate)
				.getList(MessageNotice::createFromMemento);
	}

	@Override
	public List<MessageNotice> getMsgInDestinationCategoryAndCid(DatePeriod period,
			DestinationClassification destination, String cid) {
		return this.queryProxy()
				.query(GET_BY_DEST_CATEGORY_AND_CID, SptdtInfoMessage.class)
				.setParameter("companyId", cid)
				.setParameter("endDate", period.end())
				.setParameter("startDate", period.start())
				.setParameter("destination", destination.value)
				.getList(MessageNotice::createFromMemento);
	}

}
