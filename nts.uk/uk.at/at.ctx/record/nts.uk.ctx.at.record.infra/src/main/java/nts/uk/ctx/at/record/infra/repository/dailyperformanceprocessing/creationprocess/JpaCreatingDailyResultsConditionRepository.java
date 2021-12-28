package nts.uk.ctx.at.record.infra.repository.dailyperformanceprocessing.creationprocess;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsCondition;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionRepository;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceprocessing.creationprocess.KrcmtCreateDailyDataCondition;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaCreatingDailyResultsConditionRepository extends JpaRepository
		implements CreatingDailyResultsConditionRepository {

	private CreatingDailyResultsCondition toDomain(KrcmtCreateDailyDataCondition entity) {
		return new CreatingDailyResultsCondition(entity.cid, NotUseAtr.valueOf(entity.isFutureDay));
	}

	private KrcmtCreateDailyDataCondition toEntity(CreatingDailyResultsCondition domain) {
		return new KrcmtCreateDailyDataCondition(domain.getCid(), domain.getIsCreatingFutureDay().isUse());
	}

	@Override
	public void insert(CreatingDailyResultsCondition domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void delete(CreatingDailyResultsCondition domain) {
		this.commandProxy().remove(KrcmtCreateDailyDataCondition.class, domain.getCid());
	}

	@Override
	public Optional<CreatingDailyResultsCondition> findByCid(String cid) {
		return this.queryProxy().find(cid, KrcmtCreateDailyDataCondition.class).map(this::toDomain);
	}

}
