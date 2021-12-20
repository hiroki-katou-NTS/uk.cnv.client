package nts.uk.ctx.at.record.infra.repository.workrecord.closurestatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.query.TypedQueryWrapper;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.workrecord.closurestatus.KrcdtClosureSts;
import nts.uk.ctx.at.record.infra.entity.workrecord.closurestatus.KrcdtClosureSttMngPk;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class JpaClosureStatusManagementRepository extends JpaRepository implements ClosureStatusManagementRepository {

	@Override
	public void add(ClosureStatusManagement domain) {
		this.commandProxy().insert(KrcdtClosureSts.fromDomain(domain));
	}

	@Override
	public Optional<ClosureStatusManagement> getById(String employeeId, YearMonth ym, int closureId,
			ClosureDate closureDate) {
		Optional<KrcdtClosureSts> opt = this.queryProxy().find(new KrcdtClosureSttMngPk(ym.v(), employeeId,
				closureId, closureDate.getClosureDay().v(), closureDate.getLastDayOfMonth()),
				KrcdtClosureSts.class);
		if (opt.isPresent())
			return Optional.of(opt.get().toDomain());
		return Optional.empty();
	}

	@Override
	public Optional<ClosureStatusManagement> getLatestByEmpId(String employeeId) {
		String sql = "SELECT a FROM KrcdtClosureSts a WHERE a.pk.employeeId = :employeeId ORDER BY a.end DESC";
		List<KrcdtClosureSts> lstEntity = this.queryProxy().query(sql, KrcdtClosureSts.class)
				.setParameter("employeeId", employeeId).getList();
		if (lstEntity.isEmpty())
			return Optional.empty();
		return Optional.of(lstEntity.get(0).toDomain());
	}
	
	@Override
	public List<ClosureStatusManagement> getByIdListAndDatePeriod(List<String> employeeIds, DatePeriod span){
		
		List<KrcdtClosureSts> result = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT a FROM KrcdtClosureSts a ");
		query.append("WHERE a.pk.employeeId IN :employeeId ");
		query.append("AND a.start <= :endDate ");
		query.append("AND a.end >= :startDate ");
		TypedQueryWrapper<KrcdtClosureSts> tQuery=  this.queryProxy().query(query.toString(), KrcdtClosureSts.class);
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			result.addAll(tQuery
					.setParameter("employeeId", empIds)
					.setParameter("startDate", span.start())
					.setParameter("endDate", span.end())
					.getList());
		});
		return toDomainFromJoin(result);
	}

	private List<ClosureStatusManagement> toDomainFromJoin(List<KrcdtClosureSts> result) {
		return result.stream().map(tc -> tc.toDomain()).collect(Collectors.toList());		
	}

	@Override
	public Map<String, ClosureStatusManagement> getLatestBySids(List<String> sids) {
		Map<String, ClosureStatusManagement> result = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT a FROM KrcdtClosureSts a ");
		query.append("WHERE a.pk.employeeId IN :employeeId ");
		TypedQueryWrapper<KrcdtClosureSts> tQuery=  this.queryProxy().query(query.toString(), KrcdtClosureSts.class);
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			List<KrcdtClosureSts> closureEntityLst = tQuery
					.setParameter("employeeId", sids)
					.getList();
			Map<String,List<ClosureStatusManagement>>  closureStatus = toDomainFromJoin(closureEntityLst).stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
			closureStatus.entrySet().forEach(c ->{
				ClosureStatusManagement closeStatusLast = c.getValue().stream().sorted((d1, d2) -> d2.getPeriod().end().compareTo(d1.getPeriod().end())).collect(Collectors.toList()).get(0);
				result.put(c.getKey(), closeStatusLast);
			});
		});
		return result;
	}

	@Override
	public List<ClosureStatusManagement> getAllByEmpId(String employeeId) {
		String sql = "SELECT a FROM KrcdtClosureSts a WHERE a.pk.employeeId = :employeeId ORDER BY a.end DESC";
		List<KrcdtClosureSts> lstEntity = this.queryProxy().query(sql, KrcdtClosureSts.class)
				.setParameter("employeeId", employeeId).getList();
		return lstEntity.stream().map(x -> x.toDomain()).collect(Collectors.toList());
	}

}
