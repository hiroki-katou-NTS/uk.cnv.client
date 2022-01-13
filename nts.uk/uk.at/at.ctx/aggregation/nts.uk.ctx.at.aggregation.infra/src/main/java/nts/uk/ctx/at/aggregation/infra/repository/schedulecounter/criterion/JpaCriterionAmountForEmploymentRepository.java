package nts.uk.ctx.at.aggregation.infra.repository.schedulecounter.criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion.KagmtCriterionMoneyEmp;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;

/**
 * 
 * @author TU-TK
 *
 */
@Stateless
public class JpaCriterionAmountForEmploymentRepository extends JpaRepository
		implements CriterionAmountForEmploymentRepository {

	private static final String GET_BY_CID = "SELECT a FROM KagmtCriterionMoneyEmp a WHERE a.pk.companyId = :companyId ";
	private static final String GET_BY_CID_AND_CODE = GET_BY_CID + " AND a.pk.employmentCode = :employmentCode ";

	@Override
	public void insert(String cid, CriterionAmountForEmployment criterion) {
		this.commandProxy().insertAll(KagmtCriterionMoneyEmp.toEntity(cid, criterion));

	}

	@Override
	public void update(String cid, CriterionAmountForEmployment criterion) {
		List<KagmtCriterionMoneyEmp> entities = this.queryProxy()
				.query(GET_BY_CID_AND_CODE, KagmtCriterionMoneyEmp.class)
				.setParameter("companyId", cid)
				.setParameter("employmentCode", criterion.getEmploymentCode())
				.getList();
		this.commandProxy().removeAll(entities);
		this.getEntityManager().flush();
		this.commandProxy().insertAll(KagmtCriterionMoneyEmp.toEntity(cid, criterion));
		this.getEntityManager().flush();
//		
//		List<KagmtCriterionMoneyEmp> entities = this.queryProxy()
//				.query(FIND_BY_CID_AND_EMPLOYMENT_CODE, KagmtCriterionMoneyEmp.class)
//				.setParameter("companyId", cid)
//				.setParameter("employmentCd", criterion.getEmploymentCode())
//				.getList();
		
//		KagmtCriterionMoneyEmp.toEntity(criterion)
//			.forEach(entity -> {
//				this.commandProxy().insert(entity);
//				this.getEntityManager().flush();
//			});
		
		
			
//		List<KagmtCriterionMoneyEmp> dataNew = KagmtCriterionMoneyEmp.toEntity(cid, criterion);

//		for (KagmtCriterionMoneyEmp temp : dataOld) {
//			dataNew.stream().forEach(c -> {
//				if (c.pk.companyId == temp.pk.companyId && c.pk.yearMonthAtr == temp.pk.yearMonthAtr
//						&& c.pk.frameNo == temp.pk.frameNo && c.pk.employmentCode == temp.pk.employmentCode) {
//					temp.amount = c.amount;
//				}
//			});
//		}
//
//		val dataYearlyOld = dataOld.stream().filter(c -> c.pk.yearMonthAtr == 0).collect(Collectors.toList());
//		val dataYearlyNew = dataNew.stream().filter(c -> c.pk.yearMonthAtr == 0).collect(Collectors.toList());
//		if (dataYearlyNew.size() > dataYearlyOld.size()) {
//			val oldNos = dataYearlyOld.stream().map(c -> c.pk.frameNo).collect(Collectors.toList());
//			val newEnt = dataYearlyNew.stream().filter(c -> !oldNos.contains(c.pk.frameNo))
//					.collect(Collectors.toList());
//			dataOld.addAll(newEnt);
//		} else if (dataYearlyNew.size() < dataYearlyOld.size()) {
//			val newNos = dataYearlyNew.stream().map(c -> c.pk.frameNo).collect(Collectors.toList());
//			dataOld.stream().filter(c -> c.pk.yearMonthAtr == 0).collect(Collectors.toList())
//					.removeIf(x -> !newNos.contains(x.pk.frameNo));
//		}
//
//		val dataMonthlyOld = dataOld.stream().filter(c -> c.pk.yearMonthAtr == 1).collect(Collectors.toList());
//		val dataMonthlyNew = dataNew.stream().filter(c -> c.pk.yearMonthAtr == 1).collect(Collectors.toList());
//		if (dataMonthlyNew.size() > dataMonthlyOld.size()) {
//			val oldNos = dataMonthlyOld.stream().map(c -> c.pk.frameNo).collect(Collectors.toList());
//			val newEnt = dataMonthlyNew.stream().filter(c -> !oldNos.contains(c.pk.frameNo))
//					.collect(Collectors.toList());
//			dataOld.addAll(newEnt);
//		} else if (dataMonthlyNew.size() < dataMonthlyOld.size()) {
//			val newNos = dataMonthlyNew.stream().map(c -> c.pk.frameNo).collect(Collectors.toList());
//			dataOld.stream().filter(c -> c.pk.yearMonthAtr == 1).collect(Collectors.toList())
//					.removeIf(x -> !newNos.contains(x.pk.frameNo));
//		}
//		this.commandProxy().updateAll(dataOld);

	}

	@Override
	public void delete(String cid, EmploymentCode employmentCd) {
		List<KagmtCriterionMoneyEmp> result = this.queryProxy()
				.query(GET_BY_CID_AND_CODE, KagmtCriterionMoneyEmp.class).setParameter("companyId", cid)
				.setParameter("employmentCode", employmentCd.v()).getList();
		if (!result.isEmpty()) {
			this.commandProxy().removeAll(result);
		}
	}

	@Override
	public boolean exist(String cid, EmploymentCode employmentCd) {
		return this.get(cid, employmentCd).isPresent();
	}

	@Override
	public Optional<CriterionAmountForEmployment> get(String cid, EmploymentCode employmentCd) {
		List<KagmtCriterionMoneyEmp> result = this.queryProxy()
				.query(GET_BY_CID_AND_CODE, KagmtCriterionMoneyEmp.class)
				.setParameter("companyId", cid)
				.setParameter("employmentCode", employmentCd.v()).getList();
		if (result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(KagmtCriterionMoneyEmp.toDomain(employmentCd.v(), result));
	}

	@Override
	public List<CriterionAmountForEmployment> getAll(String cid) {
		List<CriterionAmountForEmployment> datas = new ArrayList<>();
		List<KagmtCriterionMoneyEmp> result = this.queryProxy()
				.query(GET_BY_CID, KagmtCriterionMoneyEmp.class)
				.setParameter("companyId", cid).getList();
		Map<String, List<KagmtCriterionMoneyEmp>> mapData = result.stream()
				.collect(Collectors.groupingBy(c -> c.pk.employmentCode));
		mapData.forEach((k, v) -> {
			datas.add(KagmtCriterionMoneyEmp.toDomain(k, v));
		});
		return datas;
	}

}
