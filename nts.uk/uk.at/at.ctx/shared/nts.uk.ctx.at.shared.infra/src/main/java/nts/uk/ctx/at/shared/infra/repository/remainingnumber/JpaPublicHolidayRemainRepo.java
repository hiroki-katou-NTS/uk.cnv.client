package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemainRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.RemainNumber;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.publicholiday.KrcdtPubHolidayRemain;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaPublicHolidayRemainRepo extends JpaRepository implements PublicHolidayRemainRepository{

	private PublicHolidayRemain toDomain(KrcdtPubHolidayRemain entity){
		return new PublicHolidayRemain(entity.cid, entity.employeeId, entity.remainNumber);
	}
	
	private KrcdtPubHolidayRemain toEntity(PublicHolidayRemain domain){
		KrcdtPubHolidayRemain entity = new KrcdtPubHolidayRemain();
		entity.employeeId = domain.getSID();
		entity.cid = domain.getCID();
		entity.remainNumber = domain.getRemainNumber().v();
		return entity;
	}
	
	@Override
	public Optional<PublicHolidayRemain> get(String sid) {
		Optional<KrcdtPubHolidayRemain> pubHoli = this.queryProxy().find(sid, KrcdtPubHolidayRemain.class);
		
		if (pubHoli.isPresent()){
			return Optional.of(toDomain(pubHoli.get()));
		}
		
		return Optional.empty();
	}

	@Override
	public void add(PublicHolidayRemain domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(PublicHolidayRemain domain) {
		this.commandProxy().update(toEntity(domain));
	}

	@Override
	public void delete(String sid) {
		this.commandProxy().remove(KrcdtPubHolidayRemain.class, sid);
		
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemainRepository#getAll(java.util.List)
	 */
	@Override
	public List<PublicHolidayRemain> getAll(String cid, List<String> sids) {
		List<KrcdtPubHolidayRemain> entities = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_PUB_HOLIDAY_REMAIN WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<KrcdtPubHolidayRemain> result = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtPubHolidayRemain entity = new KrcdtPubHolidayRemain();
					entity.cid = rec.getString("CID");
					entity.employeeId = rec.getString("SID");
					entity.remainNumber = rec.getBigDecimal("REMAIN_NUMBER");
					return entity;
				});
				entities.addAll(result);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return entities.stream()
				.map(x -> PublicHolidayRemain.creatFromJavaType(x.cid, x.employeeId, new RemainNumber(x.remainNumber)))
				.collect(Collectors.toList());
	}

	@Override
	public void addAll(List<PublicHolidayRemain> domains) {
		String INS_SQL = "INSERT INTO KRCDT_PUB_HOLIDAY_REMAIN (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CID, SID, REMAIN_NUMBER)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " CID_VAL, SID_VAL, REMAIN_NUMBER_VAL); ";
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

			sql = sql.replace("CID_VAL", "'" + c.getCID() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			sql = sql.replace("REMAIN_NUMBER_VAL", c.getRemainNumber() == null? "0": ""+c.getRemainNumber().v()+"");
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public void updateAll(List<PublicHolidayRemain> domains) {
		String UP_SQL = "UPDATE KRCDT_PUB_HOLIDAY_REMAIN SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " REMAIN_NUMBER = REMAIN_NUMBER_VAL"
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

			sql = sql.replace("SID_VAL", "'" + c.getSID() + "'");
			sql = sql.replace("CID_VAL", "'" + c.getCID()+ "'");
			sql = sql.replace("REMAIN_NUMBER_VAL",  c.getRemainNumber() == null? "0": "" + c.getRemainNumber().v() + "");
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}
}
