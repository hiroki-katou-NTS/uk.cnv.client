package nts.uk.ctx.at.shared.infra.repository.remainingnumber.nursingcareleavemanagement.data;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagementRepository;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.nursingcareleave.childcare.interimdata.KshdtInterimChildCare;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.nursingcareleave.childcare.interimdata.KshdtInterimChildCarePK;
import nts.uk.shr.com.context.AppContexts;

/**
 * リポジトリ実装：暫定子の看護管理データ
 * @author yuri_tamakoshi
 */
@Stateless
public class JpaTempChildCareManagementRepository extends JpaRepository implements TempChildCareManagementRepository{

	private static final String SELECT_BY_PERIOD = "SELECT a FROM KshdtInterimChildCare a "
			+ " WHERE a.pk.sID = :employeeId "
			+ " AND a.pk.ymd >= :startYmd "
			+ " AND a.pk.ymd <= :endYmd "
			+ " ORDER BY a.pk.ymd ";

	private static final String SELECT_BY_EMPLOYEEID_YMD = "SELECT a FROM KshdtInterimChildCare a"
			+ " WHERE a.pk.sID = :employeeID"
			+ " AND a.pk.ymd =  : ymd "
			+ " ORDER BY a.pk.ymd ASC";

	private static final String REMOVE_BY_SID_YMD = "DELETE FROM KshdtInterimChildCare a"
			+ " WHERE a.pk.sID = :sid"
			+ " AND a.pk.ymd =  :ymd";

	
	private static final String REMOVE_BY_SID_PERIOD = "DELETE FROM KshdtInterimChildCare a"
			+ " WHERE a.pk.sID = :sid "
			+ " AND a.pk.ymd >= :startYmd "
			+ " AND a.pk.ymd <= :endYmd ";
	
	/** 検索 */
	@Override
	public List<TempChildCareManagement> find(String employeeId, GeneralDate ymd){

		return this.queryProxy().query(SELECT_BY_EMPLOYEEID_YMD, KshdtInterimChildCare.class)
				.setParameter("employeeID", employeeId)
				.setParameter("ymd",ymd)
				.getList(c -> c.toDomain());
	}

	/** 検索　（期間） */
	@Override
	public List<TempChildCareManagement> findByPeriodOrderByYmd(String employeeId, DatePeriod period) {

		return this.queryProxy().query(SELECT_BY_PERIOD, KshdtInterimChildCare.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startYmd", period.start())
				.setParameter("endYmd", period.end())
				.getList(c -> c.toDomain());
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(TempChildCareManagement domain) {

		KshdtInterimChildCarePK pk = new KshdtInterimChildCarePK(
				AppContexts.user().companyId(),
				domain.getSID(),
				domain.getYmd(),
				domain.getAppTimeType().map(x -> x.isHourlyTimeType() ? 1 : 0).orElse(0),
				domain.getAppTimeType().map(x -> x.getAppTimeType().map(time -> time.value + 1).orElse(0)).orElse(0));

		// 登録・更新
		
		Optional<KshdtInterimChildCare> entityOpt = this.queryProxy().find(pk, KshdtInterimChildCare.class);

		if (entityOpt.isPresent()) {
			entityOpt.get().fromDomainForUpdate(domain);
			this.commandProxy().update(entityOpt.get());
			this.getEntityManager().flush();
			return;
		}

		KshdtInterimChildCare entity = new KshdtInterimChildCare();
		entity.pk = pk;
		entity.fromDomainForUpdate(domain);
		this.commandProxy().insert(entity);
		this.getEntityManager().flush();
	}

	/** 削除 */
	@Override
	public void remove(TempChildCareManagement domain) {

		val key = domain.getRemainManaID();

		this.commandProxy().remove(KshdtInterimChildCare.class, key);
	}



	@Override
	public void removeBySidAndYmd(String sid, GeneralDate ymd) {
		this.getEntityManager().createQuery(REMOVE_BY_SID_YMD)
		.setParameter("sid", sid)
		.setParameter("ymd", ymd)
		.executeUpdate();
	}
	
	public void deleteByPeriod(String sid, DatePeriod period){
		this.getEntityManager().createQuery(REMOVE_BY_SID_PERIOD)
		.setParameter("sid", sid)
		.setParameter("startYmd", period.start())
		.setParameter("endYmd", period.end())
		.executeUpdate();
	}
}
