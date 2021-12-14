package nts.uk.ctx.at.record.infra.repository.stampmanagement.stamp;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampTypeDisplay;
import nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp.KrcdtStampRecord;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author ThanhNX
 *
 *         打刻記録Repository
 */
@Stateless
public class JpaStampRecordRepository extends JpaRepository implements StampRecordRepository {

	private static final String GET_STAMP_RECORD = "select s from KrcdtStampRecord s "
			+ " where s.cardNumber in  :cardNumbers " 
			+ " and s.contractCd = :contractCd" 
			+ " and s.stampDateTime >= :startStampDate "
			+ " and s.stampDateTime <= :endStampDate " 
			+ " order by s.cardNumber asc, s.stampDateTime asc";
	
	private static final String GET_ONE_STAMP_RECORD = "select s from KrcdtStampRecord s "
			+ " where s.cardNumber = :cardNumbers " 
			+ " and s.contractCd = :contractCd" 
			+ " and s.stampDateTime >= :startStampDate "
			+ " and s.stampDateTime <= :endStampDate " 
			+ " order by s.cardNumber asc, s.stampDateTime asc";

	private static final String GET_NOT_STAMP_NUMBER = "select s from KrcdtStampRecord s left join KrcmtStampCard k on s.cardNumber = k.cardNo"
			+ " where k.cardNo is NULL " + "and s.contractCd = :contractCd " + " and s.stampDateTime >= :startStampDate "
			+ " and s.stampDateTime <= :endStampDate " + " order by s.cardNumber asc, s.stampDateTime asc";

	// [1] insert(打刻記録)
	@Override
	public void insert(StampRecord stampRecord) {
		this.commandProxy().insert(toEntity(stampRecord));
		this.getEntityManager().flush();
	}

	// [2] delete(打刻記録)
	@Override
	public void delete(String contractCd, String stampNumber, GeneralDateTime stampDateTime) {
		Optional<KrcdtStampRecord> entity = this.getStampRecord(contractCd, stampNumber, stampDateTime);
		if (entity.isPresent()) {
			this.commandProxy().remove(entity.get());
		}
	}

	// [3] update(打刻記録)
	@Override
	public void update(StampRecord stampRecord) {
		Optional<KrcdtStampRecord> entity = this.getStampRecord(stampRecord.getContractCode().v(), stampRecord.getStampNumber().v(), stampRecord.getStampDateTime());
		if (!entity.isPresent())
			return;
		this.commandProxy().update(entity.get().toUpdateEntity(stampRecord));
	}

	// [4] 取得する
	@Override
	public List<StampRecord> get(String contractCode, List<StampNumber> stampNumbers, GeneralDate stampDateTime) {
		Set<String> lstCard = stampNumbers.stream().map(x -> x.v()).collect(Collectors.toSet());
		GeneralDateTime start = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				23, 59, 59);
		return this.queryProxy().query(GET_STAMP_RECORD, KrcdtStampRecord.class).setParameter("cardNumbers", lstCard)
				.setParameter("contractCd", contractCode)
				.setParameter("startStampDate", start).setParameter("endStampDate", end).getList(x -> toDomain(x));
	}

	// [5] 打刻カード未登録の打刻記録データを取得する
	@Override
	public List<StampRecord> getStempRcNotResgistNumber(String contractCode, DatePeriod period) {
		GeneralDateTime start = GeneralDateTime.ymdhms(period.start().year(), period.start().month(),
				period.start().day(), 0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(period.end().year(), period.end().month(), period.end().day(), 23,
				59, 59);

		return this.queryProxy().query(GET_NOT_STAMP_NUMBER, KrcdtStampRecord.class)
				.setParameter("contractCd", contractCode)
				.setParameter("startStampDate", start).setParameter("endStampDate", end).getList(x -> toDomain(x));
	}
	
	//
	@Override
	public Optional<StampRecord> findByKey(StampNumber stampNumber, GeneralDateTime stampDateTime) {
		Optional<KrcdtStampRecord> entity = this.getStampRecord(AppContexts.user().contractCode(), stampNumber.v(), stampDateTime);
		if (!entity.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(toDomain(entity.get()));
	}

	// [6] 取得する
	@Override
	public Optional<StampRecord> get(String contractCd, String stampNumber, GeneralDateTime stampDateTime) {
		Optional<KrcdtStampRecord> entity = this.getStampRecord(AppContexts.user().contractCode(), stampNumber, stampDateTime);
		if (!entity.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(toDomain(entity.get()));
	}

	public KrcdtStampRecord toEntity(StampRecord domain) {
		return new KrcdtStampRecord(
				domain.getStampRecordId(),
				AppContexts.user().companyId(),
				domain.getStampTypeDisplay() != null ? domain.getStampTypeDisplay().v() : null,
				domain.getContractCode().v(),
				domain.getStampNumber().v(),
				domain.getStampDateTime());
	}

	public StampRecord toDomain(KrcdtStampRecord entity) {
		return new StampRecord(entity.stampRecordId, new ContractCode(entity.contractCd), new StampNumber(entity.cardNumber),
				entity.stampDateTime, new StampTypeDisplay(entity.stampTypeDisplay));
	}
	
	public Optional<KrcdtStampRecord> getStampRecord(String contractCd, String stampNumber, GeneralDateTime stampDateTime) {
		GeneralDateTime start = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				23, 59, 59);
		
		return this.queryProxy().query(GET_ONE_STAMP_RECORD, KrcdtStampRecord.class).setParameter("cardNumbers", stampNumber)
				.setParameter("contractCd", contractCd)
				.setParameter("startStampDate", start).setParameter("endStampDate", end).getSingle();
	}
}
