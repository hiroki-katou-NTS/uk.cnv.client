package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnLeaMaxDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.HalfdayAnnualLeaveMax;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.TimeAnnualLeaveMax;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcmtAnnLeaMax;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaAnnLeaMaxDataRepo extends JpaRepository implements AnnLeaMaxDataRepository {

	@Override
	public Optional<AnnualLeaveMaxData> get(String employeeId) {
		Optional<KrcmtAnnLeaMax> entityOpt = this.queryProxy().find(employeeId, KrcmtAnnLeaMax.class);
		if (entityOpt.isPresent()) {
			KrcmtAnnLeaMax ent = entityOpt.get();
			return Optional.of(AnnualLeaveMaxData.createFromJavaType(ent.sid, ent.maxTimes, ent.usedTimes,
					ent.maxMinutes, ent.usedMinutes));
		}
		return Optional.empty();
	}

	@Override
	public void add(AnnualLeaveMaxData maxData) {
		KrcmtAnnLeaMax entity = new KrcmtAnnLeaMax();
		entity.sid = maxData.getEmployeeId();
		if ( maxData.getHalfdayAnnualLeaveMax().isPresent()) {
			HalfdayAnnualLeaveMax halfday = maxData.getHalfdayAnnualLeaveMax().get();
			entity.maxTimes = halfday.getMaxTimes().v();
			entity.usedTimes = halfday.getUsedTimes().v();
			entity.remainingTimes = halfday.getRemainingTimes().v();
		}
		if ( maxData.getTimeAnnualLeaveMax().isPresent()) {
			TimeAnnualLeaveMax time = maxData.getTimeAnnualLeaveMax().get();
			entity.maxMinutes = time.getMaxMinutes().v();
			entity.usedMinutes = time.getUsedMinutes().v();
			entity.remainingMinutes = time.getRemainingMinutes().v();
		}
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(AnnualLeaveMaxData maxData) {
		Optional<KrcmtAnnLeaMax> entityOpt = this.queryProxy().find(maxData.getEmployeeId(), KrcmtAnnLeaMax.class);
		if (entityOpt.isPresent()) {
			KrcmtAnnLeaMax entity = entityOpt.get();
			if ( maxData.getHalfdayAnnualLeaveMax().isPresent()) {
				HalfdayAnnualLeaveMax halfday = maxData.getHalfdayAnnualLeaveMax().get();
				entity.maxTimes = halfday.getMaxTimes().v();
				entity.usedTimes = halfday.getUsedTimes().v();
				entity.remainingTimes = halfday.getRemainingTimes().v();
			} else {
				entity.maxTimes = null;
				entity.usedTimes = null;
				entity.remainingTimes = null;
			}
			if ( maxData.getTimeAnnualLeaveMax().isPresent()) {
				TimeAnnualLeaveMax time = maxData.getTimeAnnualLeaveMax().get();
				entity.maxMinutes = time.getMaxMinutes().v();
				entity.usedMinutes = time.getUsedMinutes().v();
				entity.remainingMinutes = time.getRemainingMinutes().v();
			} else {
				entity.maxMinutes = null;
				entity.usedMinutes = null;
				entity.remainingMinutes = null;
			}
			this.commandProxy().update(entity);
		} else {
			add(maxData);
		}
	}

	@Override
	public void delete(String employeeId) {
		Optional<KrcmtAnnLeaMax> entityOpt = this.queryProxy().find(employeeId, KrcmtAnnLeaMax.class);
		if (entityOpt.isPresent()) {
			KrcmtAnnLeaMax entity = entityOpt.get();
			this.commandProxy().remove(entity);
		}
	}

	@Override
	public List<AnnualLeaveMaxData> getAll(List<String> sids) {
		List<AnnualLeaveMaxData> result = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HDPAID_MAX WHERE  SID IN (" + NtsStatement.In.createParamsString(subList) + ")";
			
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString( 1 + i, subList.get(i));
				}
				
				List<AnnualLeaveMaxData> annualLeavelst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					return AnnualLeaveMaxData.createFromJavaType( r.getString("SID"),  r.getInt("MAX_TIMES"), r.getInt("USED_TIMES"),
							r.getInt("MAX_MINUTES"), r.getInt("USED_MINUTES"));
				});
				result.addAll(annualLeavelst);
				
			}catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		
		return result;
	}

	@Override
	public void addAll(List<AnnualLeaveMaxData> domains) {
		String INS_SQL = "INSERT INTO KRCDT_HDPAID_MAX (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," + " SID, MAX_TIMES, USED_TIMES, REMAINING_TIMES,"
				+ " MAX_MINUTES, USED_MINUTES, REMAINING_MINUTES)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL," 
				+ " SID_VAL, MAX_TIMES_VAL, USED_TIMES_VAL, REMAINING_TIMES_VAL,"
				+ " MAX_MINUTES_VAL, USED_MINUTES_VAL,  REMAINING_MINUTES_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c ->{
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");
			
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId() + "'");
			
			Optional<HalfdayAnnualLeaveMax> maxData = c.getHalfdayAnnualLeaveMax();
			if ( maxData.isPresent()) {
				HalfdayAnnualLeaveMax halfday = maxData.get();
				sql = sql.replace("MAX_TIMES_VAL",  ""+ halfday.getMaxTimes().v()+ "");
				sql = sql.replace("USED_TIMES_VAL", "" + halfday.getUsedTimes().v()+ "");
				sql = sql.replace("REMAINING_TIMES_VAL", "" + halfday.getRemainingTimes().v()+ "");
				
			}else {
				sql = sql.replace("MAX_TIMES_VAL",  "null");
				sql = sql.replace("USED_TIMES_VAL", "null");
				sql = sql.replace("REMAINING_TIMES_VAL", "null");
			}
			
			Optional<TimeAnnualLeaveMax> timeAnnualLeaveMax = c.getTimeAnnualLeaveMax();
			if ( timeAnnualLeaveMax.isPresent()) {
				TimeAnnualLeaveMax time = timeAnnualLeaveMax.get();
				sql = sql.replace("MAX_MINUTES_VAL", "" + time.getMaxMinutes().v()+ "");
				sql = sql.replace("USED_MINUTES_VAL", "" + time.getUsedMinutes().v()+ "");
				sql = sql.replace("REMAINING_MINUTES_VAL", "" + time.getRemainingMinutes().v()+ "");
			}else {
				sql = sql.replace("MAX_MINUTES_VAL", "null");
				sql = sql.replace("USED_MINUTES_VAL", "null");
				sql = sql.replace("REMAINING_MINUTES_VAL", "null");
			}
			sb.append(sql);
		});
		
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public void updateAll(List<AnnualLeaveMaxData> domains) {
		String UP_SQL = "UPDATE KRCDT_HDPAID_MAX SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " MAX_TIMES = MAX_TIMES_VAL , USED_TIMES = USED_TIMES_VAL, REMAINING_TIMES = REMAINING_TIMES_VAL, "
				+ " MAX_MINUTES = MAX_MINUTES_VAL, USED_MINUTES = USED_MINUTES_VAL, REMAINING_MINUTES = REMAINING_MINUTES_VAL"
				+ " WHERE SID = SID_VAL;";
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c ->{
			String sql = UP_SQL;
			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");
			
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId() + "'");
			
			Optional<HalfdayAnnualLeaveMax> maxData = c.getHalfdayAnnualLeaveMax();
			if ( maxData.isPresent()) {
				HalfdayAnnualLeaveMax halfday = maxData.get();
				sql = sql.replace("MAX_TIMES_VAL",  ""+ halfday.getMaxTimes().v()+ "");
				sql = sql.replace("USED_TIMES_VAL", "" + halfday.getUsedTimes().v()+ "");
				sql = sql.replace("REMAINING_TIMES_VAL", "" + halfday.getRemainingTimes().v()+ "");
				
			}else {
				sql = sql.replace("MAX_TIMES_VAL",  "null");
				sql = sql.replace("USED_TIMES_VAL", "null");
				sql = sql.replace("REMAINING_TIMES_VAL", "null");
			}
			
			Optional<TimeAnnualLeaveMax> timeAnnualLeaveMax = c.getTimeAnnualLeaveMax();
			if ( timeAnnualLeaveMax.isPresent()) {
				TimeAnnualLeaveMax time = timeAnnualLeaveMax.get();
				sql = sql.replace("MAX_MINUTES_VAL", "" + time.getMaxMinutes().v()+ "");
				sql = sql.replace("USED_MINUTES_VAL", "" + time.getUsedMinutes().v()+ "");
				sql = sql.replace("REMAINING_MINUTES_VAL", "" + time.getRemainingMinutes().v()+ "");
			}else {
				sql = sql.replace("MAX_MINUTES_VAL", "null");
				sql = sql.replace("USED_MINUTES_VAL", "null");
				sql = sql.replace("REMAINING_MINUTES_VAL", "null");
			}
			sb.append(sql);
		});
		
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

}
