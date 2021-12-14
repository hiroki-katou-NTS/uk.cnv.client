package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfoRepository;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.excessleave.KrcmtHd60hBasic;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaExcessLeaveInfoRepo extends JpaRepository  implements ExcessLeaveInfoRepository{

	/**
	 * Convert to domain
	 * @param entity
	 * @return
	 */
	private ExcessLeaveInfo toDomain(KrcmtHd60hBasic entity){
		return new ExcessLeaveInfo(entity.cID, entity.employeeId, entity.useAtr, entity.occurrenceUnit, entity.paymentMethod);
	}
	
	/**
	 * Convert to entity
	 * @param domain
	 * @return
	 */
	private KrcmtHd60hBasic toEntity(ExcessLeaveInfo domain){
		KrcmtHd60hBasic entity = new KrcmtHd60hBasic();
		entity.cID = domain.getCid();
		entity.employeeId = domain.getSID();
		entity.useAtr = domain.getUseAtr().value;
		entity.occurrenceUnit = domain.getOccurrenceUnit().v();
		entity.paymentMethod = domain.getPaymentMethod().value;
		return entity;
	}
	@Override
	public Optional<ExcessLeaveInfo> get(String sid) {
		Optional<KrcmtHd60hBasic> leaveInfo = this.queryProxy().find(sid, KrcmtHd60hBasic.class);
		if (leaveInfo.isPresent()){
			return Optional.of(toDomain(leaveInfo.get()));
		}
		
		return Optional.empty();
	}

	@Override
	public void add(ExcessLeaveInfo domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(ExcessLeaveInfo domain) {
		this.commandProxy().update(toEntity(domain));
	}

	@Override
	public void delete(String sid) {
		this.commandProxy().remove(KrcmtHd60hBasic.class, sid);
 	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfoRepository#getAll(java.util.List, java.lang.String)
	 */
	@Override
	public List<ExcessLeaveInfo> getAll(List<String> sids, String cid) {
		List<KrcmtHd60hBasic> entities = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCMT_HD60H_BASIC WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<KrcmtHd60hBasic> result = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcmtHd60hBasic entity = new KrcmtHd60hBasic();
					entity.cID = rec.getString("CID");
					entity.employeeId = rec.getString("SID");
					entity.useAtr = rec.getInt("USE_ATR");
					entity.occurrenceUnit = rec.getInt("OCCURRENCE_UNIT");
					entity.paymentMethod = rec.getInt("PAYMENT_METHOD");
					
					return entity;
				});
				entities.addAll(result);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return entities.stream()
				.map(x -> ExcessLeaveInfo.createDomain(
							    x.cID,
								x.employeeId,
								new BigDecimal(x.useAtr),
								new BigDecimal(x.occurrenceUnit),
								new BigDecimal(x.paymentMethod))).collect(Collectors.toList());
	
	}
	
	@Override
	public List<ExcessLeaveInfo> getAllForCPS013(List<String> sids, String cid, Map<String, Object> enums) {
		List<KrcmtHd60hBasic> entities = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCMT_HD60H_BASIC WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<KrcmtHd60hBasic> result = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcmtHd60hBasic entity = new KrcmtHd60hBasic();
					entity.cID = rec.getString("CID");
					entity.employeeId = rec.getString("SID");
					entity.useAtr = rec.getInt("USE_ATR");
					entity.occurrenceUnit = rec.getInt("OCCURRENCE_UNIT");
					entity.paymentMethod = rec.getInt("PAYMENT_METHOD");
					
					//  60H超休管理
					enums.put("IS00370", rec.getInt("USE_ATR"));
					//  精算方法
					enums.put("IS00372", rec.getInt("PAYMENT_METHOD"));
					return entity;
				});
				entities.addAll(result);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return entities.stream()
				.map(x -> ExcessLeaveInfo.createDomainforcps013(
							    x.cID,
								x.employeeId,
								new BigDecimal(x.useAtr),
								new BigDecimal(x.occurrenceUnit),
								new BigDecimal(x.paymentMethod))).collect(Collectors.toList());
	}

	@Override
	public void addAll(List<ExcessLeaveInfo> domains) {
		String INS_SQL = "INSERT INTO KRCMT_HD60H_BASIC (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CID, SID, USE_ATR, OCCURRENCE_UNIT, PAYMENT_METHOD)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " CID_VAL, SID_VAL, VAL_USE_ATR, "
				+ " OCCURRENCE_UNIT_VAL, PAYMENT_METHOD_VAL); ";
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

			sql = sql.replace("CID_VAL", "'" + c.getCid() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			sql = sql.replace("VAL_USE_ATR", "" + c.getUseAtr().value + "");
			sql = sql.replace("OCCURRENCE_UNIT_VAL", "" + c.getOccurrenceUnit().v()+ "");
			sql = sql.replace("PAYMENT_METHOD_VAL", "" + c.getPaymentMethod().value+ "");
			
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public void updateAll(List<ExcessLeaveInfo> domains) {
		String UP_SQL = "UPDATE KRCMT_HD60H_BASIC SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " USE_ATR = VAL_USE_ATR , OCCURRENCE_UNIT = OCCURRENCE_UNIT_VAL, PAYMENT_METHOD = PAYMENT_METHOD_VAL"
				+ " WHERE SID = SID_VAL AND CID = CID_VAL;";
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

			sql = sql.replace("CID_VAL", "'" + c.getCid() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			sql = sql.replace("VAL_USE_ATR", "" + c.getUseAtr().value + "");
			sql = sql.replace("OCCURRENCE_UNIT_VAL", "" + c.getOccurrenceUnit().v()+ "");
			sql = sql.replace("PAYMENT_METHOD_VAL", "" + c.getPaymentMethod().value+ "");
			
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

}
