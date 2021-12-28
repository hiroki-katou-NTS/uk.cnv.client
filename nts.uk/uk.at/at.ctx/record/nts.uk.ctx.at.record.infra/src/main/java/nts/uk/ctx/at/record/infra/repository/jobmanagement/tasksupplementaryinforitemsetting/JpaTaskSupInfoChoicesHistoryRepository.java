package nts.uk.ctx.at.record.infra.repository.jobmanagement.tasksupplementaryinforitemsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistory;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.tasksupplementaryinforitemsetting.KrcmtTaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.tasksupplementaryinforitemsetting.KrcmtTaskSupInfoChoicesDetailPk;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.tasksupplementaryinforitemsetting.KrcmtTaskSupInfoChoicesHist;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.tasksupplementaryinforitemsetting.KrcmtTaskSupInfoChoicesHistPK;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaTaskSupInfoChoicesHistoryRepository extends JpaRepository
		implements TaskSupInfoChoicesHistoryRepository {

	@Override
	public void insert(TaskSupInfoChoicesHistory history, TaskSupInfoChoicesDetail detail) {
		List<KrcmtTaskSupInfoChoicesHist> entites = KrcmtTaskSupInfoChoicesHist.toEntities(history);
		entites.stream().forEach(f -> {
			this.commandProxy().insert(f);
		});
		this.commandProxy().insert(new KrcmtTaskSupInfoChoicesDetail(detail));
	}

	@Override
	public void insert(TaskSupInfoChoicesDetail detail) {
		this.commandProxy().insert(new KrcmtTaskSupInfoChoicesDetail(detail));
	}
	
	@Override
	public void insert(TaskSupInfoChoicesHistory history) {
		List<KrcmtTaskSupInfoChoicesHist> entites = KrcmtTaskSupInfoChoicesHist.toEntities(history);
		entites.stream().forEach(f -> {
			this.commandProxy().insert(f);
		});
	}

	@Override
	public void update(TaskSupInfoChoicesHistory history) {
		List<KrcmtTaskSupInfoChoicesHist> entites = KrcmtTaskSupInfoChoicesHist.toEntities(history);
		entites.stream().forEach(f -> {
			this.commandProxy().update(f);
		});

	}

	@Override
	public void update(TaskSupInfoChoicesDetail detail) {
		this.commandProxy().update(new KrcmtTaskSupInfoChoicesDetail(detail));
	}

	@Override
	public void delete(String hisId) {
		List<KrcmtTaskSupInfoChoicesHist> histEntity = this.queryProxy()
				.query("SELECT h FROM KrcmtTaskSupInfoChoicesHist h WHERE h.pk.histId = :hisId",
						KrcmtTaskSupInfoChoicesHist.class)
				.setParameter("hisId", hisId).getList();

		List<KrcmtTaskSupInfoChoicesDetail> detailEntity = this.queryProxy()
				.query("SELECT d FROM KrcmtTaskSupInfoChoicesDetail d WHERE d.pk.hisId = :hisId",
						KrcmtTaskSupInfoChoicesDetail.class)
				.setParameter("hisId", hisId).getList();

		if (!histEntity.isEmpty()) {
			for (KrcmtTaskSupInfoChoicesHist h : histEntity) {
				this.commandProxy().remove(h);
			}
		}

		if (!detailEntity.isEmpty()) {
			for (KrcmtTaskSupInfoChoicesDetail d : detailEntity) {
				this.commandProxy().remove(d);
			}
		}
	}

	@Override
	public void delete(String hisId, ChoiceCode choiceCode) {
		String code = choiceCode.v();
		
		List<KrcmtTaskSupInfoChoicesDetail> detailEntity = this.queryProxy()
				.query("SELECT d FROM KrcmtTaskSupInfoChoicesDetail d WHERE d.pk.hisId = :hisId AND d.pk.code = :code",
						KrcmtTaskSupInfoChoicesDetail.class)
				.setParameter("hisId", hisId)
				.setParameter("code", code)
				.getList();
		
		if (!detailEntity.isEmpty()) {
			for (KrcmtTaskSupInfoChoicesDetail d : detailEntity) {
				this.commandProxy().remove(d);
			}
		}
	}

	@Override
	public List<TaskSupInfoChoicesHistory> getAll(String companyId) {

		return this.queryProxy()
				.query("SELECT h FROM KrcmtTaskSupInfoChoicesHist h WHERE h.companyId = :companyId ORDER BY h.startDate DESC",
						KrcmtTaskSupInfoChoicesHist.class)
				.setParameter("companyId", companyId).getList(m -> m.toDomain());
	}

	@Override
	public List<TaskSupInfoChoicesDetail> get(String hisId) {
		return this.queryProxy().query(
				"SELECT d FROM KrcmtTaskSupInfoChoicesDetail d WHERE d.pk.hisId = :hisId ORDER BY d.pk.code ASC",
				KrcmtTaskSupInfoChoicesDetail.class).setParameter("hisId", hisId).getList(a -> a.toDomain());
	}

	@Override
	public List<TaskSupInfoChoicesDetail> get(String companyId, int itemId, GeneralDate refDate) {
		return this.queryProxy().query(
				"SELECT d FROM KrcmtTaskSupInfoChoicesDetail d JOIN KrcmtTaskSupInfoChoicesHist h WHERE d.pk.hisId = h.pk.histId AND d.companyId = :companyId "
						+ "AND d.pk.manHrItemId = :itemId AND h.startDate <= :refDate AND h.endDate >= :refDate ORDER BY d.pk.code ASC",
				KrcmtTaskSupInfoChoicesDetail.class).setParameter("companyId", companyId).setParameter("itemId", itemId)
				.setParameter("refDate", refDate).getList(a -> a.toDomain());
	}

	@Override
	public Optional<TaskSupInfoChoicesDetail> get(String historyId, int itemId, ChoiceCode code) {
		return this.queryProxy().find(new KrcmtTaskSupInfoChoicesDetailPk(historyId, code.v(), itemId), KrcmtTaskSupInfoChoicesDetail.class).map(m -> m.toDomain());
	}

	@Override
	public List<TaskSupInfoChoicesDetail> get(List<String> historyIds) {
		
		List<TaskSupInfoChoicesDetail> result = new ArrayList<>();
		
		List<KrcmtTaskSupInfoChoicesDetail> details =  this.queryProxy()
				.query("SELECT d FROM KrcmtTaskSupInfoChoicesDetail d WHERE d.pk.hisId in :hisIds ORDER BY d.pk.code ASC",
						KrcmtTaskSupInfoChoicesDetail.class)
				.setParameter("hisIds", historyIds).getList();
		
		return result;
	}

	@Override
	public Optional<TaskSupInfoChoicesHistory> get(String historyId, int itemId) {
		return this.queryProxy().find(new KrcmtTaskSupInfoChoicesHistPK(itemId, historyId), KrcmtTaskSupInfoChoicesHist.class).map(m -> m.toDomain());
	}

	@Override
	public List<TaskSupInfoChoicesDetail> getListForCid(String cid) {
		return this.queryProxy().query(
				"SELECT d FROM KrcmtTaskSupInfoChoicesDetail d WHERE d.companyId = :cid",
				KrcmtTaskSupInfoChoicesDetail.class).setParameter("cid", cid).getList(a -> a.toDomain());
	}

}
