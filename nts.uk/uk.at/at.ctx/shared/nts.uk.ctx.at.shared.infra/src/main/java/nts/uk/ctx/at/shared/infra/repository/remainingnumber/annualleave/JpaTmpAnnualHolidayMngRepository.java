package nts.uk.ctx.at.shared.infra.repository.remainingnumber.annualleave;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
//import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpAnnualHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TmpAnnualHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.DigestionHourlyTimeType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtInterimHdpaid;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtInterimHdpaidPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaTmpAnnualHolidayMngRepository extends JpaRepository implements TmpAnnualHolidayMngRepository{
	
//	@Inject
//	private InterimRemainRepository interRemain;
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<TmpAnnualHolidayMng> getById(String mngId) {
		
		return this.queryProxy().query("SELECT a FROM KrcdtInterimHdpaid a WHERE a.remainMngId = :id", KrcdtInterimHdpaid.class)
					.setParameter("id", mngId)
					.getSingle(x -> toDomain(x));
	}

	private TmpAnnualHolidayMng toDomain(KrcdtInterimHdpaid x) {
		return toDomain(x.remainMngId, x.pk.sid, x.pk.ymd, x.creatorAtr, x.pk.timeDigestiveAtr,
						x.pk.timeHdType, x.workTypeCode, x.useDays, x.useTime);
	}

	private TmpAnnualHolidayMng toDomain(String mngId, String sid, GeneralDate ymd,
			int creatorAtr, int timeDigestAtr, int timeHdType, String workTypeCode,
			Double useDays, Integer useTime) {
		return new TmpAnnualHolidayMng(mngId, sid, ymd, 
				EnumAdaptor.valueOf(creatorAtr, CreateAtr.class), 
				DigestionHourlyTimeType.of(timeDigestAtr == 1, 
											timeHdType == 0 ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(timeHdType, AppTimeType.class))), 
				new WorkTypeCode(workTypeCode), 
				AnnualLeaveUsedNumber.of(useDays == null ? Optional.empty() : Optional.of(new AnnualLeaveUsedDayNumber(useDays)), 
										useTime == null ? Optional.empty() : Optional.of(new UsedMinutes(useTime))));
	}

	@Override
	public void deleteById(String mngId) {
		this.getEntityManager().createQuery("DELETE FROM KrcdtInterimHdpaid a WHERE a.remainMngId = :id", KrcdtInterimHdpaid.class)
			.setParameter("id", mngId).executeUpdate();

	}

	@Override
	public void persistAndUpdate(TmpAnnualHolidayMng dataMng) {
		KrcdtInterimHdpaidPK pk = new KrcdtInterimHdpaidPK(AppContexts.user().companyId(), 
				dataMng.getSID(), dataMng.getYmd(), dataMng.getTimeBreakType().isHourlyTimeType() ? 1 : 0, 
				dataMng.getTimeBreakType().getAppTimeType().map(c -> c.value).orElse(0));
		
		Optional<KrcdtInterimHdpaid> optTmpAnnualHolidayMng = this.queryProxy().find(pk, KrcdtInterimHdpaid.class);
		if(optTmpAnnualHolidayMng.isPresent()) {
			KrcdtInterimHdpaid entity = optTmpAnnualHolidayMng.get();
			entity.update(dataMng);
			this.commandProxy().update(entity);
		} else {
			KrcdtInterimHdpaid entity = new KrcdtInterimHdpaid();
			entity.pk = pk;
			entity.update(dataMng);
			this.getEntityManager().persist(entity);
		}
		this.getEntityManager().flush();
	}
	@SneakyThrows
	@Override
	public List<TmpAnnualHolidayMng> getBySidPeriod(String sid, DatePeriod period) {
		try(PreparedStatement sql = this.connection().prepareStatement("SELECT * FROM KRCMT_INTERIM_ANNUAL_MNG a"
				+ " WHERE a.SID = ? AND a.YMD >= ? and a.YMD <= ? ORDER BY a.YMD")) {
			sql.setString(1, sid);
			sql.setDate(2, Date.valueOf(period.start().localDate()));
			sql.setDate(3, Date.valueOf(period.end().localDate()));
			
			return new NtsResultSet(sql.executeQuery()).getList(x -> toDomain(x));
		}
	}

	private TmpAnnualHolidayMng toDomain(NtsResultRecord x) {
		return toDomain(x.getString("REMAIN_MNG_ID"), x.getString("SID"), x.getGeneralDate("YMD"), 
						x.getInt("CREATOR_ATR"), x.getInt("TIME_DIGESTIVE_ATR"), x.getInt("TIME_HD_TYPE"), 
						x.getString("WORKTYPE_CODE"), x.getDouble("USE_DAYS"), x.getInt("USE_TIME"));
	}

	@Override
	public void deleteSidPeriod(String sid, DatePeriod period) {
		
		this.getEntityManager().createQuery("DELETE FROM KrcdtInterimHdpaid a WHERE a.pk.sid = :id"
					+ " AND a.pk.ymd <= :end AND a.pk.ymd >= :start", KrcdtInterimHdpaid.class)
			.setParameter("id", sid)
			.setParameter("start", period.start())
			.setParameter("end", period.end())
			.executeUpdate();
	}

}
