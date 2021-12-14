package nts.uk.ctx.sys.log.infra.repository.logbasicinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.log.dom.logbasicinfo.LogBasicInfoRepository;
import nts.uk.ctx.sys.log.infra.entity.logbasicinfo.SrcdtBasicInfo;
import nts.uk.shr.com.security.audittrail.basic.LogBasicInformation;
import nts.uk.shr.com.security.audittrail.correction.processor.LogBasicInformationWriter;

/**
 *
 * @author HungTT
 *
 */

@Stateless
public class JpaLogBasicInformationRepository extends JpaRepository implements LogBasicInfoRepository, LogBasicInformationWriter {

	@Override
	public Optional<LogBasicInformation> getLogBasicInfo(String companyId, String operationId) {
		String query = "SELECT a FROM SrcdtBasicInfo a WHERE a.companyId = :companyId AND a.operationId = :operationId";
		return this.queryProxy().query(query, SrcdtBasicInfo.class).setParameter("companyId", companyId)
				.setParameter("operationId", operationId).getSingle(item -> item.toDomain());
	}

	@Override
	public List<LogBasicInformation> findByOperatorsAndDate(String companyId, List<String> listEmployeeId,
			GeneralDateTime start, GeneralDateTime end) {
		/*GeneralDateTime start = GeneralDateTime.ymdhms(period.start().year(), period.start().month(),
				period.start().day(), 0, 0, 0);
		GeneralDateTime end = GeneralDateTime.ymdhms(period.end().year(), period.end().month(), period.end().day(), 23,
				59, 59);*/
		List<LogBasicInformation> resultList = new ArrayList<>();
		if (listEmployeeId == null || listEmployeeId.isEmpty()) {
			String query = "SELECT a FROM SrcdtBasicInfo a WHERE a.companyId = :companyId"
					+ " AND a.modifiedDateTime BETWEEN :startPeriod AND :endPeriod ORDER BY a.modifiedDateTime DESC";
			resultList.addAll(this.queryProxy().query(query, SrcdtBasicInfo.class).setParameter("companyId", companyId)
					.setParameter("startPeriod", start)
					.setParameter("endPeriod", end).getList(i -> i.toDomain()));
		} else {
			String query = "SELECT a FROM SrcdtBasicInfo a WHERE a.companyId = :companyId AND a.employeeId IN :employeeId "
					+ "AND a.modifiedDateTime BETWEEN :startPeriod AND :endPeriod ORDER BY a.modifiedDateTime DESC";
			CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				resultList.addAll(this.queryProxy().query(query, SrcdtBasicInfo.class).setParameter("companyId", companyId)
						.setParameter("employeeId", subList).setParameter("startPeriod", start)
						.setParameter("endPeriod", end).getList(i -> i.toDomain()));
			});
			resultList.sort(Comparator.comparing(LogBasicInformation::getModifiedDateTime));
		}
		return resultList;
	}

	@Override
	public void save(LogBasicInformation basicInfo) {
		this.commandProxy().insert(SrcdtBasicInfo.fromDomain(basicInfo));
	}

	@Override
	public List<LogBasicInformation> getLogBasicInfo(String companyId, List<String> operationIds) {
		if (operationIds.isEmpty()) return Collections.emptyList();
		String query = "SELECT a FROM SrcdtBasicInfo a WHERE a.companyId = :companyId AND a.operationId IN :operationIds";
		List<LogBasicInformation> results = new ArrayList<>();
		CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			results.addAll(this.queryProxy().query(query, SrcdtBasicInfo.class).setParameter("companyId", companyId)
					.setParameter("operationIds", splitData).getList(item -> item.toDomain()));
		});
		return results;
	}

}
