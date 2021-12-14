package nts.uk.ctx.at.shared.infra.repository.remainingnumber.breakdayoffmng.interim;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.DataManagementAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.OccurrenceDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.OccurrenceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RequiredDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RequiredTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.SelectedAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnOffsetDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnOffsetTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnUsedDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UseDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UseTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.DigestionHourlyTimeType;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcdtInterimHdwkMng;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcdtInterimHdwkMngPk;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcmtInterimBreakDayOff;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcmtInterimBreakDayOffPK;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcmtInterimDayOffMng;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim.KrcmtInterimDayOffMngPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaInterimBreakDayOffMngRepository extends JpaRepository implements InterimBreakDayOffMngRepository{
	private static final String QUERY_BREAK_MNG = "SELECT c FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.breakMngId = :mngId"
			+ " AND c.breakMngAtr = :mngAtr";
	private static final String QUERY_DAYOFF_MNG = "SELECT c FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.dayOffMngId = :mngId"
			+ " AND c.dayOffMngAtr = :mngAtr";
	private static final String DELETE_BREAK_MNG = "DELETE FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.breakMngId = :mngId"
			+ " AND c.breakMngAtr = :mngAtr";
	private static final String DELETE_DAYOFF_MNG = "DELETE FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.dayOffMngId = :mngId"
			+ " AND c.dayOffMngAtr = :mngAtr";
	
	private static final String QUERY_BY_EXPIRATIONDATE = "SELECT c FROM KrcdtInterimHdwkMng c"
			+ " WHERE c.breakMngId IN :breakMngIds"
			+ " AND c.unUsedDays > :unUsedDays"
			+ " AND c.expirationDate >= :startDate"
			+ " AND c.expirationDate <= :endDate";
	private static final String DELETE_BREAK_BYID = "DELETE FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.breakMngId = :mngId";
	private static final String DELETE_DAYOFF_BY_ID = "DELETE FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.dayOffMngId = :mngId";
	
	private static final String QUERY_BYID_AND_ATR = "SELECT c FROM KrcmtInterimBreakDayOff c"
			+ " WHERE c.breakDayOffKey.breakMngId = :breakId"
			+ " AND c.breakDayOffKey.dayOffMngId = :dayOffId"
			+ " AND c.breakMngAtr = :breakAtr"
			+ " AND c.dayOffMngAtr = :dayOffAtr";
	private static final String QUERY_DAYOFF_ID_ATR = QUERY_DAYOFF_MNG + " AND c.breakMngAtr = :breakMngAtr";
	private static final String QUERY_BREAK_ID_ATR = QUERY_BREAK_MNG + " AND c.dayOffMngAtr = :dayOffMngAtr";
	private static final String DELETE_DAYOFFMNG_BYID = "DELETE FROM KrcmtInterimDayOffMng c WHERE c.dayOffMngId = :dayOffMngId";
	private static final String DELETE_DAYOFFMNG_BY_SID_AND_YMD = "DELETE FROM KrcmtInterimDayOffMng c WHERE c.pk.sid = :sid AND c.pk.ymd = :ymd";
	private static final String DELETE_BREAKMNG_BYID = "DELETE FROM KrcdtInterimHdwkMng c WHERE c.breakMngId = :breakMngId";
	private static final String DELETE_BREAKMNG_BY_SID_AND_YMD = "DELETE FROM KrcdtInterimHdwkMng c WHERE c.pk.sid = :sid AND c.pk.ymd = :ymd";

	private static final String SELECT_DAY_OFF_BY_SID_DATE = "SELECT t FROM KrcmtInterimDayOffMng t "
			+ "WHERE t.pk.sid = :sid "
			+ "AND t.pk.ymd = :date";
	private static final String SELECT_DAY_OFF_BY_IDS = "SELECT t FROM KrcmtInterimDayOffMng t "
			+ "WHERE t.remainMngId IN :remainMngIds";
	
	@Override
	public Optional<InterimBreakMng> getBreakManaBybreakMngId(String breakManaId) {
		return this.queryProxy().find(breakManaId, KrcdtInterimHdwkMng.class)
				.map(x -> toDomainBreakMng(x));
	}
	private InterimBreakMng toDomainBreakMng(KrcdtInterimHdwkMng x) {		
		return new InterimBreakMng(x.remainMngId, 
				x.pk.sid,
				x.pk.ymd,
				EnumAdaptor.valueOf(x.createAtr, CreateAtr.class),
				RemainType.BREAK,
				new AttendanceTime(x.occurrenceTimes),
				x.expirationDate,
				new OccurrenceTime(x.occurrenceTimes), 
				new OccurrenceDay(x.occurrenceDays),
				new AttendanceTime(x.haftDayEquiTime),
				new UnUsedTime(x.unUsedTimes),
				new UnUsedDay(x.unUsedDays));
	}
	
	@Override
	public Optional<InterimDayOffMng> getDayoffById(String dayOffManaId) {
		return this.queryProxy().find(dayOffManaId, KrcmtInterimDayOffMng.class)
				.map(x -> toDomainDayoffMng(x));
	}
	private InterimDayOffMng toDomainDayoffMng(KrcmtInterimDayOffMng x) {
		return new InterimDayOffMng(
				x.remainMngId,
				x.pk.sid,
				x.pk.ymd,
				EnumAdaptor.valueOf(x.createAtr, CreateAtr.class),
				RemainType.SUBHOLIDAY,
				new RequiredTime(x.requiredTimes),
				new RequiredDay(x.requiredDays),
				new UnOffsetTime(x.unOffSetTimes),
				new UnOffsetDay(x.unOffsetDays),
				Optional.ofNullable(
						DigestionHourlyTimeType.of(x.pk.timeDigestiveAtr == 1, x.pk.timeHdType == 0 ? Optional.empty()
								:
						Optional.ofNullable(EnumAdaptor.valueOf(x.pk.timeHdType -1 , AppTimeType.class))))
				);
	}
	
	@Override
	public List<InterimBreakDayOffMng> getBreakDayOffMng(String mngId, boolean breakDay, DataManagementAtr mngAtr) {
		return this.queryProxy().query(breakDay ? QUERY_BREAK_MNG : QUERY_DAYOFF_MNG, KrcmtInterimBreakDayOff.class)
				.setParameter("mngId", mngId)
				.setParameter("mngAtr", mngAtr.value)
				.getList(x -> toDomainBreakDayoffMng(x));
				
	}
	private InterimBreakDayOffMng toDomainBreakDayoffMng(KrcmtInterimBreakDayOff x) {		
		return new InterimBreakDayOffMng(x.breakDayOffKey.breakMngId,
				EnumAdaptor.valueOf(x.breakMngAtr, DataManagementAtr.class),
				x.breakDayOffKey.dayOffMngId,
				EnumAdaptor.valueOf(x.dayOffMngAtr, DataManagementAtr.class),
				new UseTime(x.userTimes),
				new UseDay(x.userDays),
				EnumAdaptor.valueOf(x.selectedAtr, SelectedAtr.class));
	}
	@SneakyThrows
	@Override
	public List<InterimBreakMng> getByPeriod(String sid, DatePeriod ymd, double unUseDays, DatePeriod dateData) {
		/*if(mngId.isEmpty()) {
			return Collections.emptyList();
		}
		List<InterimBreakMng> resultList = new ArrayList<>();
		CollectionUtil.split(mngId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(QUERY_BY_EXPIRATIONDATE, KrcdtInterimHdwkMng.class)
								.setParameter("breakMngIds", subList)
								.setParameter("unUsedDays", unUseDays)
								.setParameter("startDate", dateData.start())
								.setParameter("endDate", dateData.end())
								.getList(c -> toDomainBreakMng(c)));
		});
		return resultList;*/
		try(PreparedStatement sql = this.connection().prepareStatement("SELECT * FROM KSHDT_INTERIM_HDWK a1"
				+ " WHERE a1.SID = ?"
				+ " AND a1.YMD >= ? and a1.YMD <= ?"
				+ " AND a1.UNUSED_DAYS > ?"
				+ " AND a1.EXPIRATION_DAYS >= ? and a1.EXPIRATION_DAYS <= ?"
				+ " ORDER BY a1.YMD");
				)
		{
			sql.setString(1, sid);
			sql.setDate(2, Date.valueOf(ymd.start().localDate()));
			sql.setDate(3, Date.valueOf(ymd.end().localDate()));
			sql.setDouble(4, unUseDays);
			sql.setDate(5, Date.valueOf(dateData.start().localDate()));
			sql.setDate(6, Date.valueOf(dateData.end().localDate()));
			List<InterimBreakMng> lstOutput = new NtsResultSet(sql.executeQuery())
					.getList(x -> toDomain(x));
			return lstOutput;
		}
	}

	@Override
	public List<InterimBreakMng> getBreakByIds(List<String> mngIds) {
		if (mngIds == null || mngIds.isEmpty()) return Collections.emptyList();
		return this.queryProxy().query("SELECT a FROM KrcdtInterimHdwkMng a, KrcdtInterimRemainMng b " +
				"WHERE a.breakMngId = b.remainMngId " +
				"AND b.remainMngId IN :mngIds", KrcdtInterimHdwkMng.class)
				.setParameter("mngIds", mngIds)
				.getList(i -> toDomainBreakMng(i));
	}

	@Override
	public void persistAndUpdateInterimBreakMng(InterimBreakMng domain) {

		// キー
		 KrcdtInterimHdwkMngPk pk = new KrcdtInterimHdwkMngPk(AppContexts.user().companyId(), 
					domain.getSID(), 
					domain.getYmd());
		
		// 登録・更新
		KrcdtInterimHdwkMng entity = this.getEntityManager().find(KrcdtInterimHdwkMng.class, pk);
		if (entity == null){
			entity = new KrcdtInterimHdwkMng();
			
			entity.pk = pk;
			entity.createAtr = domain.getCreatorAtr().value;
			entity.remainMngId = domain.getRemainManaID();
			entity.oneDayEquivalentTime = domain.getOnedayTime().v();
			entity.expirationDate = domain.getExpirationDate();
			entity.occurrenceTimes = domain.getOccurrenceTimes().v();
			entity.occurrenceDays = domain.getOccurrenceDays().v();
			entity.haftDayEquiTime = domain.getHaftDayTime().v();
			entity.unUsedTimes = domain.getUnUsedTimes().v();
			entity.unUsedDays = domain.getUnUsedDays().v();
			this.getEntityManager().persist(entity);
		}
		else {
			entity.createAtr = domain.getCreatorAtr().value;
			entity.remainMngId = domain.getRemainManaID();
			entity.oneDayEquivalentTime = domain.getOnedayTime().v();
			entity.expirationDate = domain.getExpirationDate();
			entity.occurrenceTimes = domain.getOccurrenceTimes().v();
			entity.occurrenceDays = domain.getOccurrenceDays().v();
			entity.haftDayEquiTime = domain.getHaftDayTime().v();
			entity.unUsedTimes = domain.getUnUsedTimes().v();
			entity.unUsedDays = domain.getUnUsedDays().v();
			this.commandProxy().update(entity);
		}
		this.getEntityManager().flush();
	}

	@Override
	public void deleteInterimBreakMng(String mngId) {
		this.getEntityManager().createQuery(DELETE_BREAKMNG_BYID, KrcdtInterimHdwkMng.class)
		.setParameter("breakMngId", mngId).executeUpdate();
	}
	
	@Override
	public void persistAndUpdateInterimDayOffMng(InterimDayOffMng domain) {
		
		// キー
		KrcmtInterimDayOffMngPK pk = new KrcmtInterimDayOffMngPK(
				AppContexts.user().companyId(), 
				domain.getSID(), 
				domain.getYmd(),
				domain.getAppTimeType().map(x -> x.isHourlyTimeType() ? 1 : 0).orElse(0),
				domain.getAppTimeType().map(x -> x.getAppTimeType().map(appTime -> appTime.value + 1).orElse(0)).orElse(0)
				);
		
		// 登録・更新
		KrcmtInterimDayOffMng entity = this.getEntityManager().find(KrcmtInterimDayOffMng.class, pk);
		if (entity == null){
			entity = new KrcmtInterimDayOffMng();
			entity.pk = pk;
			entity.createAtr = domain.getCreatorAtr().value;
			entity.remainMngId = domain.getRemainManaID();
			entity.requiredTimes = domain.getRequiredTime().v();
			entity.requiredDays = domain.getRequiredDay().v();
			entity.unOffSetTimes = domain.getUnOffsetTimes().v();
			entity.unOffsetDays = domain.getUnOffsetDay().v();
			this.getEntityManager().persist(entity);
		}
		else {
			entity.remainMngId = domain.getRemainManaID();
			entity.createAtr = domain.getCreatorAtr().value;
			entity.requiredTimes = domain.getRequiredTime().v();
			entity.requiredDays = domain.getRequiredDay().v();
			entity.unOffSetTimes = domain.getUnOffsetTimes().v();
			entity.unOffsetDays = domain.getUnOffsetDay().v();
			this.commandProxy().update(entity);
		}
		this.getEntityManager().flush();
	}

	@Override
	public void deleteInterimDayOffMng(String mngId) {
		this.getEntityManager().createQuery(DELETE_DAYOFFMNG_BYID, KrcmtInterimDayOffMng.class)
		.setParameter("dayOffMngId", mngId).executeUpdate();
	}

	@Override
	public void persistAndUpdateInterimBreakDayOffMng(InterimBreakDayOffMng domain) {

		// キー
		val key = new KrcmtInterimBreakDayOffPK(domain.getBreakManaId(), domain.getDayOffManaId());
		
		// 登録・更新
		KrcmtInterimBreakDayOff entity = this.getEntityManager().find(KrcmtInterimBreakDayOff.class, key);
		if (entity == null){
			entity = new KrcmtInterimBreakDayOff();
			entity.breakDayOffKey = new KrcmtInterimBreakDayOffPK();
			entity.breakDayOffKey.breakMngId = domain.getBreakManaId();
			entity.breakDayOffKey.dayOffMngId = domain.getDayOffManaId();
			entity.breakMngAtr = domain.getBreakManaAtr().value;
			entity.dayOffMngAtr = domain.getDayOffManaAtr().value;
			entity.userTimes = domain.getUseTimes().v();
			entity.userDays = domain.getUseDays().v();
			entity.selectedAtr = domain.getSelectedAtr().value;
			this.getEntityManager().persist(entity);
		}
		else {
			entity.breakMngAtr = domain.getBreakManaAtr().value;
			entity.dayOffMngAtr = domain.getDayOffManaAtr().value;
			entity.userTimes = domain.getUseTimes().v();
			entity.userDays = domain.getUseDays().v();
			entity.selectedAtr = domain.getSelectedAtr().value;
			this.commandProxy().update(entity);
		}
		this.getEntityManager().flush();
	}

	@Override
	public void deleteBreakDayOffById(String mngId, boolean isBreak) {
		this.getEntityManager().createQuery(isBreak ? DELETE_BREAK_BYID : DELETE_DAYOFF_BY_ID, KrcmtInterimBreakDayOff.class)
		.setParameter("mngId", mngId)
		.executeUpdate();	
	}

	@Override
	public void deleteBreakDayOfByIdAndAtr(String breakId, String dayOffId, DataManagementAtr breakAtr,
			DataManagementAtr dayOffAtr) {
		this.getEntityManager().createQuery(QUERY_BYID_AND_ATR,KrcmtInterimBreakDayOff.class)
				.setParameter("breakId", breakId)
				.setParameter("dayOffId", dayOffId)
				.setParameter("breakAtr", breakAtr)
				.setParameter("dayOffAtr", dayOffAtr)
				.executeUpdate();
	}

	@Override
	public void deleteBreakDayOfById(String mngId, DataManagementAtr mngAtr, boolean isBreak) {
		this.getEntityManager().createQuery(isBreak ? DELETE_BREAK_MNG : DELETE_DAYOFF_MNG, KrcmtInterimBreakDayOff.class)
			.setParameter("mngId", mngId)
			.setParameter("mngAtr", mngAtr.value)
			.executeUpdate();		
	}
	@Override
	public List<InterimBreakDayOffMng> getDayOffByIdAndDataAtr(DataManagementAtr breakAtr, DataManagementAtr dayOffAtr,
			String dayOffId) {
		return this.queryProxy().query(QUERY_DAYOFF_ID_ATR, KrcmtInterimBreakDayOff.class)
				.setParameter("mngId", dayOffId)
				.setParameter("mngAtr", dayOffAtr.value)
				.setParameter("breakMngAtr", breakAtr.value)
				.getList(x -> toDomainBreakDayoffMng(x));
	}
	@Override
	public List<InterimBreakDayOffMng> getBreakByIdAndDataAtr(DataManagementAtr breakAtr, DataManagementAtr dayOffAtr,
			String breakId) {
		return this.queryProxy().query(QUERY_BREAK_ID_ATR, KrcmtInterimBreakDayOff.class)
				.setParameter("mngId", breakId)
				.setParameter("mngAtr", breakAtr.value)
				.setParameter("dayOffMngAtr", dayOffAtr.value)
				.getList(x -> toDomainBreakDayoffMng(x));
	}
	
	@Override
	public void deleteInterimBreakMng(List<String> mngIds) {
		if(!mngIds.isEmpty()) {
			String sql = "delete  from KrcdtInterimHdwkMng a where a.breakMngId IN :mngIds";
			CollectionUtil.split(mngIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				this.getEntityManager().createQuery(sql).setParameter("mngIds", subList).executeUpdate();
			});
			this.getEntityManager().flush();
		}
	}
	
	@Override
	public void deleteInterimDayOffMng(List<String> mngIds) {
		if(!mngIds.isEmpty()) {
			String sql = "delete  from KrcmtInterimDayOffMng a where a.dayOffMngId IN :mngIds";
			CollectionUtil.split(mngIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				this.getEntityManager().createQuery(sql).setParameter("mngIds", subList).executeUpdate();
			});
			this.getEntityManager().flush();
		}
	}
	@SneakyThrows
	@Override
	public List<InterimBreakMng> getBySidPeriod(String sid, DatePeriod period) {
		try (PreparedStatement sql = this.connection().prepareStatement("SELECT * FROM KSHDT_INTERIM_HDWK a1"
				+ " WHERE a1.SID = ?"
				+ " AND a1.YMD >= ? and a1.YMD <= ?"
				+ " ORDER BY a1.YMD");
				)
		{
			sql.setString(1, sid);
			sql.setDate(2, Date.valueOf(period.start().localDate()));
			sql.setDate(3, Date.valueOf(period.end().localDate()));
			List<InterimBreakMng> lstOutput = new NtsResultSet(sql.executeQuery())
					.getList(x -> toDomain(x));
			return lstOutput;
		}
	}
	private InterimBreakMng toDomain(NtsResultRecord x) {
		return new InterimBreakMng(
				x.getString("REMAIN_MNG_ID"),
				x.getString("SID"),
				x.getGeneralDate("YMD"),
				EnumAdaptor.valueOf(x.getInt("CREATOR_ATR"), CreateAtr.class),
				RemainType.BREAK ,
				new AttendanceTime(x.getInt("ONEDAY_EQUIVALENT_TIME")),
				x.getGeneralDate("EXPIRATION_DAYS"), 
				new OccurrenceTime(x.getInt("OCCURRENCE_TIMES")),
				new OccurrenceDay(x.getDouble("OCCURRENCE_DAYS")),
				new AttendanceTime(x.getInt("HAFTDAY_EQUI_TIME")),
				new UnUsedTime(x.getInt("UNUSED_TIMES")),
				new UnUsedDay(x.getDouble("UNUSED_DAYS")));
	}
	@SneakyThrows
	@Override
	public List<InterimDayOffMng> getDayOffBySidPeriod(String sid, DatePeriod period) {
		try (PreparedStatement sql = this.connection().prepareStatement("SELECT * FROM KSHDT_INTERIM_HDCOM a1"
				+ " WHERE a1.SID = ?"
				+ " AND a1.YMD >= ? and a1.YMD <= ?"
				+ " ORDER BY a1.YMD");
				)
		{
			sql.setString(1, sid);
			sql.setDate(2, Date.valueOf(period.start().localDate()));
			sql.setDate(3, Date.valueOf(period.end().localDate()));
			List<InterimDayOffMng> lstOutput = new NtsResultSet(sql.executeQuery())
					.getList(x -> toDomainDayOff(x));
			return lstOutput;
		}
	}
	private InterimDayOffMng toDomainDayOff(NtsResultRecord x) {
		return new InterimDayOffMng(
				x.getString("REMAIN_MNG_ID"),
				x.getString("SID"),
				x.getGeneralDate("YMD"),
				EnumAdaptor.valueOf(x.getInt("CREATOR_ATR"), CreateAtr.class),
				RemainType.SUBHOLIDAY,
				new RequiredTime(x.getInt("REQUIRED_TIMES")),
				new RequiredDay(x.getDouble("REQUIRED_DAYS")),
				new UnOffsetTime(x.getInt("UNOFFSET_TIMES")),
				new UnOffsetDay(x.getDouble("UNOFFSET_DAYS")),
				Optional.ofNullable(DigestionHourlyTimeType.of(x.getInt("TIME_DIGESTIVE_ATR") == 1,x.getInt("TIME_HD_TYPE") == 0 ?Optional.empty():
						Optional.ofNullable(EnumAdaptor.valueOf(x.getInt("TIME_HD_TYPE") -1 , AppTimeType.class))))
				);
	}
	@Override
	public void deleteInterimDayOffMngBySidAndYmd(String sid, GeneralDate ymd) {
		this.getEntityManager().createQuery(DELETE_DAYOFFMNG_BY_SID_AND_YMD)
		.setParameter("sid", sid)
		.setParameter("ymd", ymd)
		.executeUpdate();
		
	}
	@Override
	public void deleteInterimBreakMngBySidAndYmd(String sid, GeneralDate ymd) {
		this.getEntityManager().createQuery(DELETE_BREAKMNG_BY_SID_AND_YMD)
		.setParameter("sid", sid)
		.setParameter("ymd", ymd)
		.executeUpdate();
	}
	
	private static final String DELETE_KYUSYUTSU_PERIOD = "DELETE FROM KrcdtInterimHdwkMng c WHERE c.pk.sid = :sid AND c.pk.ymd between :startDate and :endDate";

	@Override
	public void deleteBreakoffWithPeriod(String sid, DatePeriod period) {
		this.getEntityManager().createQuery(DELETE_KYUSYUTSU_PERIOD).setParameter("sid", sid)
				.setParameter("startDate", period.start()).setParameter("endDate", period.end()).executeUpdate();
	}
	
	@Override
	public void insertBreakoffList(List<InterimBreakMng> lstDomain) {
		this.commandProxy().insertAll(lstDomain.stream().map(x -> toEntityBreakoff(x)).collect(Collectors.toList()));
	}
	
	private KrcdtInterimHdwkMng toEntityBreakoff(InterimBreakMng domain) {
		KrcdtInterimHdwkMng entity = new KrcdtInterimHdwkMng();
		KrcdtInterimHdwkMngPk pk = new KrcdtInterimHdwkMngPk(AppContexts.user().companyId(), domain.getSID(),
				domain.getYmd());
		entity.pk = pk;
		entity.createAtr = domain.getCreatorAtr().value;
		entity.remainMngId = domain.getRemainManaID();
		entity.oneDayEquivalentTime = domain.getOnedayTime().v();
		entity.expirationDate = domain.getExpirationDate();
		entity.occurrenceTimes = domain.getOccurrenceTimes().v();
		entity.occurrenceDays = domain.getOccurrenceDays().v();
		entity.haftDayEquiTime = domain.getHaftDayTime().v();
		entity.unUsedTimes = domain.getUnUsedTimes().v();
		entity.unUsedDays = domain.getUnUsedDays().v();
		return entity;
	}
	
	private static final String DELETE_DAIKYU_PERIOD = "DELETE FROM KrcmtInterimDayOffMng c WHERE c.pk.sid = :sid AND c.pk.ymd between :startDate and :endDate";
	@Override
	public void deleteDayoffWithPeriod(String sid, DatePeriod period) {
		this.getEntityManager().createQuery(DELETE_DAIKYU_PERIOD).setParameter("sid", sid)
		.setParameter("startDate", period.start()).setParameter("endDate", period.end()).executeUpdate();
	}
	
	@Override
	public void insertDayoffList(List<InterimDayOffMng> lstDomain) {
		this.commandProxy().insertAll(lstDomain.stream().map(x -> toEntityDayoff(x)).collect(Collectors.toList()));
	}
	
	private KrcmtInterimDayOffMng toEntityDayoff(InterimDayOffMng domain) {
		KrcmtInterimDayOffMngPK pk = new KrcmtInterimDayOffMngPK(AppContexts.user().companyId(), domain.getSID(),
				domain.getYmd(), domain.getAppTimeType().map(x -> x.isHourlyTimeType() ? 1 : 0).orElse(0),
				domain.getAppTimeType().map(x -> x.getAppTimeType().map(appTime -> appTime.value + 1).orElse(0))
						.orElse(0));

		KrcmtInterimDayOffMng entity = new KrcmtInterimDayOffMng();
		entity.pk = pk;
		entity.createAtr = domain.getCreatorAtr().value;
		entity.remainMngId = domain.getRemainManaID();
		entity.requiredTimes = domain.getRequiredTime().v();
		entity.requiredDays = domain.getRequiredDay().v();
		entity.unOffSetTimes = domain.getUnOffsetTimes().v();
		entity.unOffsetDays = domain.getUnOffsetDay().v();
		return entity;
	}
	
	private static final String DELETE_KYUSYUTSU_DATE = "DELETE FROM KrcdtInterimHdwkMng c WHERE c.pk.sid = :sid AND c.pk.ymd IN :lstDate";

	@Override
	public void deleteBreakoffWithDateList(String sid, List<GeneralDate> lstDate) {
		if (lstDate.isEmpty())
			return;
		this.getEntityManager().createQuery(DELETE_KYUSYUTSU_DATE).setParameter("sid", sid)
				.setParameter("lstDate", lstDate).executeUpdate();
	}
	
	private static final String DELETE_DAIKYU_DATE = "DELETE FROM KrcmtInterimDayOffMng c WHERE c.pk.sid = :sid AND c.pk.ymd IN :lstDate";
	@Override
	public void deleteDayoffWithDateList(String sid, List<GeneralDate> lstDate) {
		if (lstDate.isEmpty())
			return;
		this.getEntityManager().createQuery(DELETE_DAIKYU_DATE).setParameter("sid", sid)
		.setParameter("lstDate", lstDate).executeUpdate();
		
	}
	
	@Override
	public List<InterimBreakMng> getBreakBySidDateList(String sid, List<GeneralDate> lstDate) {
		if(lstDate.isEmpty()) return new ArrayList<>();
		return this.queryProxy().query("SELECT a FROM KrcdtInterimHdwkMng a " +
				" WHERE a.pk.sid = :sid " +
				"AND a.pk.ymd IN :ymd ", KrcdtInterimHdwkMng.class)
				.setParameter("sid", sid)
				.setParameter("ymd", lstDate)
				.getList(i -> toDomainBreakMng(i));
	}
	
	@Override
	public List<InterimDayOffMng> getDayOffDateList(String sid, List<GeneralDate> lstDate) {
		if(lstDate.isEmpty()) return new ArrayList<>();
		return this.queryProxy().query("SELECT a FROM KrcmtInterimDayOffMng a " +
				" WHERE a.pk.sid = :sid " +
				"AND a.pk.ymd IN :ymd ", KrcmtInterimDayOffMng.class)
				.setParameter("sid", sid)
				.setParameter("ymd", lstDate)
				.getList(i -> toDomainDayoffMng(i));
	}
	
	@Override
	public Optional<InterimDayOffMng> getDayOffByDate(String sid, GeneralDate date) {
		return this.queryProxy().query(SELECT_DAY_OFF_BY_SID_DATE, KrcmtInterimDayOffMng.class)
				.setParameter("sid", sid).setParameter("date", date)
				.getSingle(this::toDomainDayoffMng);
	}

	@Override
	public List<InterimDayOffMng> getDayOffByIds(List<String> remainManaIds) {
		if (remainManaIds.isEmpty()) {
			return Collections.emptyList();
		}
		return this.queryProxy().query(SELECT_DAY_OFF_BY_IDS, KrcmtInterimDayOffMng.class)
				.setParameter("remainMngIds", remainManaIds)
				.getList(this::toDomainDayoffMng);
	}
}
