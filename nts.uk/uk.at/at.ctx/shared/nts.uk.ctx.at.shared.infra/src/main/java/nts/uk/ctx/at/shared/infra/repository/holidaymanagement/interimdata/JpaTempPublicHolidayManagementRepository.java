package nts.uk.ctx.at.shared.infra.repository.holidaymanagement.interimdata;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagementRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.interimdata.KshdtInterimHdpub;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.interimdata.KshdtInterimHdpubPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaTempPublicHolidayManagementRepository  extends JpaRepository implements TempPublicHolidayManagementRepository{

	private static final String SELECT_BY_EMPLOYEEID_YMD = "SELECT a FROM KshdtInterimHdpub a"
			+ " WHERE a.pk.sid = :employeeId"
			+ "AND a.pk.ymd =  : ymd "
			+ " ORDER BY a.pk.ymd ASC";
	
	private static final String SELECT_BY_PERIOD = "SELECT a FROM KshdtInterimHdpub a "
			+ "WHERE a.pk.sid = :employeeId "
			+ "AND a.pk.ymd >= :startYmd "
			+ "AND a.pk.ymd <= :endYmd "
			+ "ORDER BY a.pk.ymd ";
	
	private static final String REMOVE_BY_SID_PERIOD = "DELETE FROM KshdtInterimHdpub a"
			+ " WHERE a.pk.sid = :employeeId"
			+ " AND a.pk.ymd >= :startYmd "
			+ " AND a.pk.ymd <= :endYmd ";
	
	/** 検索 */
	@Override
	public List<TempPublicHolidayManagement> find(String employeeId, GeneralDate ymd){

		return this.queryProxy().query(SELECT_BY_EMPLOYEEID_YMD, KshdtInterimHdpub.class)
				.setParameter("employeeId", employeeId)
				.setParameter("ymd",ymd)
				.getList(c -> c.toDomain());
	}
	
	/** 検索　（期間） */
	@Override
	public List<TempPublicHolidayManagement> findByPeriodOrderByYmd(String employeeId, DatePeriod period) {

		return this.queryProxy().query(SELECT_BY_PERIOD, KshdtInterimHdpub.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startYmd", period.start())
				.setParameter("endYmd", period.end())
				.getList(c -> c.toDomain());
	}
	
	/** 削除 （期間） */
	public void deleteByPeriod(String employeeId, DatePeriod period){
		 this.queryProxy().query(REMOVE_BY_SID_PERIOD, KshdtInterimHdpub.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startYmd", period.start())
				.setParameter("endYmd", period.end());
	}
	
	/** 削除 （年月日） */
	public void deleteByDate(String employeeId, GeneralDate date){
		 this.deleteByPeriod(employeeId, new DatePeriod(date,date));
	}
	
	
	/**登録もしくは更新 */
	@Override
	public void persistAndUpdate(TempPublicHolidayManagement domain) {

		KshdtInterimHdpubPK pk = new KshdtInterimHdpubPK(AppContexts.user().companyId(), domain.getSID(),
				domain.getYmd());

		KshdtInterimHdpub entity = this.getEntityManager().find(KshdtInterimHdpub.class, pk);

		if (entity == null) {
			this.getEntityManager().persist(KshdtInterimHdpub.toEntity(domain));

		} else {
			this.commandProxy().update(KshdtInterimHdpub.toEntity(domain));
		}

		this.getEntityManager().flush();

	}
	
	
}
