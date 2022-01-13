package nts.uk.ctx.at.aggregation.infra.repository.schedulecounter.criterion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompany;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompanyRepository;
import nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion.KagmtCriterionMoneyCmp;

/**
 * 
 * @author TU-TK
 *
 */
@Stateless
public class JpaCriterionAmountForCompanyRepository extends JpaRepository implements CriterionAmountForCompanyRepository {

	private static final String GET_BY_CID = "SELECT a FROM KagmtCriterionMoneyCmp a WHERE a.pk.companyId = :companyId ";

	@Override
	public void insert(String cid, CriterionAmountForCompany criterion) {
		this.commandProxy().insertAll(KagmtCriterionMoneyCmp.toEntity(cid, criterion));

	}

	@Override
	public void update(String cid, CriterionAmountForCompany criterion) {
		List<KagmtCriterionMoneyCmp> entities = this.queryProxy()
				.query(GET_BY_CID, KagmtCriterionMoneyCmp.class).setParameter("companyId", cid).getList();
//		List<KagmtCriterionMoneyCmp> dataNew = KagmtCriterionMoneyCmp.toEntity(cid, criterion);
		
		this.commandProxy().removeAll(entities);
		this.getEntityManager().flush();
		
		this.commandProxy().insertAll(KagmtCriterionMoneyCmp.toEntity(cid, criterion));
		this.getEntityManager().flush();

		

//		for (KagmtCriterionMoneyCmp temp : dataOld) {
//			dataNew.stream().forEach(c -> {
//				if (c.pk.companyId == temp.pk.companyId && c.pk.yearMonthAtr == temp.pk.yearMonthAtr
//						&& c.pk.frameNo == temp.pk.frameNo) {
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
	public boolean exist(String cid) {
		return this.get(cid).isPresent();
	}

	@Override
	public Optional<CriterionAmountForCompany> get(String cid) {
		List<KagmtCriterionMoneyCmp> result = this.queryProxy()
				.query(GET_BY_CID, KagmtCriterionMoneyCmp.class).setParameter("companyId", cid).getList();

		if (result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(KagmtCriterionMoneyCmp.toDomain(result));
	}

}
