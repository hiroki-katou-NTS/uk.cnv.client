package nts.uk.ctx.at.record.infra.repository.employmentinfoterminal.infoterminal;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.log.TopPageAlarmEmpInfoTer;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.log.TopPgAlTrRepository;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlDetailTr;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlDetailTrPK;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlManagerTr;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlManagerTrPK;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlTr;
import nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal.KrcdtTopPgAlTrPK;

@Stateless
public class JpaTopPgAlTrRepository extends JpaRepository implements TopPgAlTrRepository {

	@Override
	public void insertLogAll(TopPageAlarmEmpInfoTer alEmpInfo) {

		this.commandProxy().insert(toEntity(alEmpInfo));

		this.commandProxy().insertAll(toEntityManagerTr(alEmpInfo));

		this.commandProxy().insertAll(toDetailEntity(alEmpInfo));

	}

	private KrcdtTopPgAlTr toEntity(TopPageAlarmEmpInfoTer domain) {
		return new KrcdtTopPgAlTr(
				new KrcdtTopPgAlTrPK(domain.getCompanyId(), domain.getEmpInfoTerCode().v(), domain.getFinishDateTime()),
				domain.getExistenceError().value, domain.getIsCancelled().value);
	}

	private List<KrcdtTopPgAlDetailTr> toDetailEntity(TopPageAlarmEmpInfoTer domain) {
		return domain.getLstEmpInfoTerDetail().stream()
				.map(x -> new KrcdtTopPgAlDetailTr(
						new KrcdtTopPgAlDetailTrPK(domain.getCompanyId(), domain.getEmpInfoTerCode().v(),
								domain.getFinishDateTime(), x.getSerialNo()),
						x.getTargerEmployee().v(), x.getStampNumber().v(), x.getErrorMessage()))
				.collect(Collectors.toList());

	}

	private List<KrcdtTopPgAlManagerTr> toEntityManagerTr(TopPageAlarmEmpInfoTer domain) {
		return domain.getLstManagerTr().stream()
				.map(x -> new KrcdtTopPgAlManagerTr(new KrcdtTopPgAlManagerTrPK(domain.getCompanyId(),
						domain.getEmpInfoTerCode().v(), domain.getFinishDateTime(), x.getManagerId()),
						x.getRogerFlag().value))
				.collect(Collectors.toList());
	}

}
