package nts.uk.ctx.at.record.infra.repository.stamp.stampcard;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.infra.entity.stamp.stampcard.KrcmtStampCard;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaStampCardRepository extends JpaRepository implements StampCardRepository {


	private static final String GET_ALL_BY_SID = "SELECT a FROM KrcmtStampCard a WHERE a.sid = :sid ORDER BY a.registerDate, a.cardNo ASC";

	private static final String GET_ALL_BY_CONTRACT_CD = "SELECT a FROM KrcmtStampCard a WHERE a.contractCd = :contractCode ";
	
//	private static final String GET_LST_STAMPCARD_BY_LST_SID= "SELECT a FROM KrcmtStampCard a WHERE a.sid IN :sids";
	
	private static final String GET_BY_SID_AND_CARD_NO = "SELECT a FROM KrcmtStampCard a WHERE a.sid = :sid AND a.cardNo = :cardNo";

	private static final String GET_LST_STAMPCARD_BY_LST_SID_CONTRACT_CD= "SELECT a FROM KrcmtStampCard a WHERE a.sid IN :sids AND a.contractCd = :contractCode ";

	private static final String GET_BY_CARD_ID = "SELECT a FROM KrcmtStampCard a WHERE a.cardId = :cardid";
	
	private static final String GET_BY_CONTRACT_CD = "SELECT a.cardNo FROM KrcmtStampCard a WHERE a.contractCd = :contractCd";

	private static final String GET_BY_CARD_NO_AND_CONTRACT_CD = "SELECT a FROM KrcmtStampCard a"
			+ " WHERE a.cardNo = :cardNo and a.contractCd = :contractCd";
	
	private static final String GET_BY_CARD_NO_AND_CONTRACT_CD_AND_EMPLOYEE_ID = "SELECT a FROM KrcmtStampCard a"
			+ " WHERE a.cardNo = :cardNo and a.contractCd = :contractCd and a.sid = :sid";
	
	public static final String GET_LAST_CARD_NO = "SELECT c.cardNo FROM KrcmtStampCard c"
			+ " WHERE c.contractCd = :contractCode AND c.cardNo LIKE CONCAT(:cardNo, '%')"
			+ " ORDER BY c.cardNo DESC";
	
	private static final String GET_LST_STAMP_BY_SIDS = "SELECT sc.CARD_ID, sc.SID, sc.CARD_NUMBER, sc.REGISTER_DATE, sc.CONTRACT_CD FROM KRCMT_STAMP_CARD sc WHERE sc.SID IN ('{sids}') ORDER BY sc.SID, sc.REGISTER_DATE ASC, sc.CARD_NUMBER ASC";

	private static final String GET_ALL_BY_SID_CONTRACT_CD = "SELECT a FROM KrcmtStampCard a WHERE a.sid = :sid and a.contractCd = :contractCd ORDER BY a.insDate DESC, a.registerDate DESC";

	private static final String GET_BY_STAMPCARD = "SELECT a FROM KrcmtStampCard a WHERE a.cardNo = :cardNo";


	private static final String GET_LST_STAMPCARD_BY_LST_CODE= "SELECT a FROM KrcmtStampCard a WHERE a.cardNo IN :cardNos ";

	private static final String GET_LST_STAMPCARD_BY_LST_CODE_CONTRACT = "SELECT a FROM KrcmtStampCard a WHERE a.cardNo IN :cardNos and a.contractCd = :contractCd";

	@Override
	public List<StampCard> getListStampCard(String sid) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_ALL_BY_SID, KrcmtStampCard.class)
				.setParameter("sid", sid).getList();
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<StampCard> getLstStampCardBySidAndContractCd(String contractCd, String sid) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_ALL_BY_SID_CONTRACT_CD, KrcmtStampCard.class)
				.setParameter("sid", sid)
				.setParameter("contractCd", contractCd).getList();
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<String> getListStampCardByContractCode(String contractCd) {
		List<String> lstCardNo = this.queryProxy().query(GET_BY_CONTRACT_CD, String.class)
				.setParameter("contractCd", contractCd).getList();
		if (lstCardNo.isEmpty())
			return Collections.emptyList();

		return lstCardNo;
	}

	@Override
	public Optional<StampCard> getByStampCardId(String stampCardId) {
		Optional<StampCard> entity = this.queryProxy().query(GET_BY_CARD_ID, KrcmtStampCard.class)
				.setParameter("cardid", stampCardId).getSingle(x -> toDomain(x));
		if (entity.isPresent())
			return entity;
		else
			return Optional.empty();
	}

	@Override
	public Optional<StampCard> getByCardNoAndContractCode(String cardNo, String contractCd) {
		Optional<StampCard> domain = this.queryProxy().query(GET_BY_CARD_NO_AND_CONTRACT_CD, KrcmtStampCard.class)
				.setParameter("cardNo", cardNo).setParameter("contractCd", contractCd).getSingle(x -> toDomain(x));
		if (domain.isPresent())
			return domain;
		else
			return Optional.empty();
	}

	@Override
	public void add(StampCard domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(StampCard domain) {
		Optional<KrcmtStampCard> entityOpt = this.queryProxy().find(domain.getStampCardId(), KrcmtStampCard.class);
		if (entityOpt.isPresent()) {
			KrcmtStampCard entity = entityOpt.get();
			updateDetail(entity, domain);
			this.commandProxy().update(entity);
		}
	}

	@Override
	public void delete(String stampCardId) {
		Optional<KrcmtStampCard> entityOpt = this.queryProxy().find(stampCardId, KrcmtStampCard.class);
		if (entityOpt.isPresent()) {
			KrcmtStampCard entity = entityOpt.get();
			this.commandProxy().remove(entity);
		}

	}
	
	@Override
	public void delete(StampCard domain) {
		Optional<KrcmtStampCard> entityOpt = this.queryProxy().find(domain.getStampCardId(), KrcmtStampCard.class);
		if (entityOpt.isPresent()) {
			KrcmtStampCard entity = entityOpt.get();
			this.commandProxy().remove(entity);
		}
		
	}

	@Override
	public void deleteByTenantCode(String tenantCode) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_ALL_BY_CONTRACT_CD, KrcmtStampCard.class)
				.setParameter("contractCode", tenantCode).getList();
		if (!entities.isEmpty())
			this.commandProxy().removeAll(entities);
		
	}
	
	@Override
	public void deleteBySid(String sid) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_ALL_BY_SID, KrcmtStampCard.class)
				.setParameter("sid", sid).getList();
		if (!entities.isEmpty())
			this.commandProxy().removeAll(entities);
	}

	private StampCard toDomain(KrcmtStampCard ent) {
		return new StampCard(new ContractCode(ent.contractCd), new StampNumber(ent.cardNo), ent.sid, ent.registerDate,
				ent.cardId);
	}

	private KrcmtStampCard toEntity(StampCard domain) {
		KrcmtStampCard entity = new KrcmtStampCard();
		entity.cardId = domain.getStampCardId();
		entity.sid = domain.getEmployeeId();
		entity.cardNo = domain.getStampNumber().v();
		entity.registerDate = domain.getRegisterDate();
		entity.contractCd = domain.getContractCd().v();
		return entity;
	}

	private void updateDetail(KrcmtStampCard entity, StampCard data) {
		entity.cardId = data.getStampCardId();
		entity.sid = data.getEmployeeId();
		entity.cardNo = data.getStampNumber().v();
		entity.registerDate = data.getRegisterDate();
		entity.contractCd = data.getContractCd().v();
	}

	@Override
	public Optional<String> getLastCardNo(String contractCode, String startCardNoLetters, int length) {
		List<String> cardNoList = this.queryProxy().query(GET_LAST_CARD_NO, String.class)
				.setParameter("contractCode", contractCode)
				.setParameter("cardNo", startCardNoLetters).getList()
				.stream().filter(cardNo -> cardNo.length() == length)
				.collect(Collectors.toList()); 
		if (cardNoList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(cardNoList.get(0));
	}

	@Override
	public List<StampCard> getLstStampCardByContractCode(String contractCode) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_ALL_BY_CONTRACT_CD, KrcmtStampCard.class)
				.setParameter("contractCode", contractCode).getList();
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<StampCard> getLstStampCardByLstSid(List<String> sids) {
		List<StampCard> domains = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String params = String.join("', '", subList),
					query = GET_LST_STAMP_BY_SIDS.replaceAll("\\{sids\\}", params);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = this.getEntityManager().createNativeQuery(query).getResultList();
			result.forEach(f -> {
				domains.add(new StampCard(new ContractCode(f[4].toString()), new StampNumber(f[2].toString()), f[1].toString(),
						GeneralDate.fromString(f[3].toString(), "yyyy-MM-dd"), f[0].toString()));
				// domains.add(new StampCard(f[4].toString(), f[2].toString(), f[1].toString()));
			});
		});

		return domains;
	}

	@Override
	public List<StampCard> getLstStampCardByLstSidAndContractCd(List<String> sids, String contractCode) {
		List<KrcmtStampCard> entities = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			entities.addAll(this.queryProxy().query(GET_LST_STAMPCARD_BY_LST_SID_CONTRACT_CD, KrcmtStampCard.class)
				.setParameter("sids", subList)
				.setParameter("contractCode", contractCode).getList());
		});
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<StampCard> getListStampCardByCardNumbers(List<String> cardNos) {

		List<KrcmtStampCard> entities = new ArrayList<>();
		CollectionUtil.split(cardNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, listCardNo -> {
			entities.addAll(this.queryProxy().query(GET_LST_STAMPCARD_BY_LST_CODE, KrcmtStampCard.class)
				.setParameter("cardNos", listCardNo).getList());
		});
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
			.map(x -> toDomain(x))
			.collect(Collectors.toList());
	}

	@Override
	public Map<String, StampCard> getByCardNoAndContractCode(Map<String, String> cardNos, String contractCd) {
		// check exist input
		if (!cardNos.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, StampCard> result = new HashMap<>();
		Map<String, List<StampCard>> stampCards = new HashMap<>();
		CollectionUtil.split(new ArrayList<>(cardNos.entrySet()), DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * from KRCMT_STAMP_CARD" + " WHERE CONTRACT_CD = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, contractCd);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i).getValue());
				}
				List<StampCard> subResults = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					KrcmtStampCard e = new KrcmtStampCard();
					e.cardId = r.getString("CARD_ID");
					e.sid = r.getString("SID");
					e.cardNo = r.getString("CARD_NUMBER");
					e.registerDate = r.getGeneralDate("REGISTER_DATE");
					e.contractCd = r.getString("CONTRACT_CD");
					return toDomain(e);
				});

				stampCards.putAll(subResults.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId())));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		if (!stampCards.isEmpty()) {
			stampCards.entrySet().stream().forEach(c -> {
				if (cardNos.containsKey(c.getKey())) {
					Optional<StampCard> opt = c.getValue().stream()
							.filter(item -> item.getStampNumber().v().equals(cardNos.get(c.getKey()))).findFirst();
					if (opt.isPresent()) {
						result.put(c.getKey(), opt.get());
					}
				}
			});
		}

		return result;
	}

	@Override
	public void addAll(List<StampCard> domains) {
		String INS_SQL = "INSERT INTO KRCMT_STAMP_CARD (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CARD_ID, SID, CARD_NUMBER, REGISTER_DATE, CONTRACT_CD)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " CARD_ID_VAL, SID_VAL, CARD_NUMBER_VAL, "
				+ " REGISTER_DATE_VAL, CONTRACT_CD_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c -> {
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

			sql = sql.replace("CARD_ID_VAL", "'" + c.getStampCardId() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId()+ "'");
			sql = sql.replace("CARD_NUMBER_VAL", c.getStampNumber() == null? "null": "'" +c.getStampNumber().v()+ "'");
			sql = sql.replace("REGISTER_DATE_VAL", c.getRegisterDate() == null? "null": "'" + c.getRegisterDate() + "'");
			sql = sql.replace("CONTRACT_CD_VAL", "'" + c.getContractCd().v()+ "'");
			
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public void updateAll(List<StampCard> domains) {
		String UP_SQL = "UPDATE KRCMT_STAMP_CARD SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " CARD_NUMBER = CARD_NUMBER_VAL, REGISTER_DATE = REGISTER_DATE_VAL"
				+ " WHERE CARD_ID = CARD_ID_VAL AND SID = SID_VAL AND CONTRACT_CD = CONTRACT_CD_VAL;";
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c -> {
			String sql = UP_SQL;

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

			sql = sql.replace("CARD_ID_VAL", "'" + c.getStampCardId() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId()+ "'");
			sql = sql.replace("CARD_NUMBER_VAL", c.getStampNumber() == null? "null": "'" +c.getStampNumber().v()+ "'");
			sql = sql.replace("REGISTER_DATE_VAL", c.getRegisterDate() == null? "null": "'" + c.getRegisterDate() + "'");
			sql = sql.replace("CONTRACT_CD_VAL", "'" + c.getContractCd().v()+ "'");
			
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public Map<String, List<StampCard>> getByStampCardId(String contractCd, List<String> stampCardId) {
		// check exist input
		if (!stampCardId.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, List<StampCard>> stampCards = new HashMap<>();
		CollectionUtil.split(stampCardId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * from KRCMT_STAMP_CARD" + " WHERE CONTRACT_CD = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, contractCd);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<StampCard> subResults = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					KrcmtStampCard e = new KrcmtStampCard();
					e.cardId = r.getString("CARD_ID");
					e.sid = r.getString("SID");
					e.cardNo = r.getString("CARD_NUMBER");
					e.registerDate = r.getGeneralDate("REGISTER_DATE");
					e.contractCd = r.getString("CONTRACT_CD");
					return toDomain(e);
				});

				stampCards.putAll(subResults.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId())));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		return stampCards;
	}

	@Override
	public Optional<StampCard> getStampCardByEmployeeCardNumber(String employeeId, String cardNumber) {
		Optional<StampCard> domain = this.queryProxy().query(GET_BY_SID_AND_CARD_NO, KrcmtStampCard.class)
				.setParameter("sid", employeeId).setParameter("cardNo", cardNumber).getSingle(x -> toDomain(x));
		if (domain.isPresent())
			return domain;
		else
			return Optional.empty();
	}

	@Override
	public Optional<StampCard> getStampCardByContractCdEmployeeCardNumber(String contractCd, String employeeId,
			String cardNumber) {
		Optional<StampCard> domain = this.queryProxy().query(GET_BY_CARD_NO_AND_CONTRACT_CD_AND_EMPLOYEE_ID, KrcmtStampCard.class)
				.setParameter("cardNo", cardNumber).setParameter("contractCd", contractCd).setParameter("sid", employeeId).getSingle(x -> toDomain(x));
		if (domain.isPresent())
			return domain;
		else
			return Optional.empty();
	}

	@Override
	public List<StampCard> getListStampCardByCardNumber(String cardNos) {
		List<KrcmtStampCard> entities = this.queryProxy().query(GET_BY_STAMPCARD, KrcmtStampCard.class)
				.setParameter("cardNo", cardNos).getList();
		if (entities.isEmpty())
			return Collections.emptyList();
		
		return entities.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<StampCard> getListStampCardByCardNumbersAndContract(List<String> cardNos, String contractCode) {

		List<KrcmtStampCard> entities = new ArrayList<>();
		CollectionUtil.split(cardNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, listCardNo -> {
			entities.addAll(this.queryProxy().query(GET_LST_STAMPCARD_BY_LST_CODE_CONTRACT, KrcmtStampCard.class)
				.setParameter("cardNos", listCardNo)
				.setParameter("contractCd", contractCode)
				.getList());
		});
		if (entities.isEmpty())
			return Collections.emptyList();

		return entities.stream()
			.map(x -> toDomain(x))
			.collect(Collectors.toList());
	}
}
