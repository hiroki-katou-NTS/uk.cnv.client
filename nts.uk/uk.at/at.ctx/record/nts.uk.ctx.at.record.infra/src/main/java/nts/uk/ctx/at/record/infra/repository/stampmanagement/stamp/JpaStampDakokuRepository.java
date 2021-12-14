package nts.uk.ctx.at.record.infra.repository.stampmanagement.stamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.ImprintReflectionState;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.WorkInformationStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp.KrcdtStamp;
import nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp.KrcdtStampPk;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author ThanhNX
 * 打刻Repository
 */
@Stateless
public class JpaStampDakokuRepository extends JpaRepository implements StampDakokuRepository {

	private static final String GET_STAMP_RECORD = "select s from KrcdtStamp s "
			+ " where s.pk.contractCode = :contractCode" + " and s.pk.cardNumber in  :cardNumbers " + " and s.pk.stampDateTime >= :startStampDate "
			+ " and s.pk.stampDateTime <= :endStampDate " + " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";
	
	private static final String GET_STAMP_RECORD_BY_NUMBER = "select s from KrcdtStamp s "
			+ " where s.pk.contractCode = :contractCode" + " and s.pk.cardNumber = :cardNumbers " + " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";
	
	
	private static final String GET_NOT_STAMP_NUMBER = "select s from KrcdtStamp s left join KrcmtStampCard k on s.pk.cardNumber = k.cardNo AND k.contractCd = s.pk.contractCode"
			+ " where k.cardNo is NULL " +" and s.pk.contractCode = :contractCode" + " and s.pk.stampDateTime >= :startStampDate "
			+ " and s.pk.stampDateTime <= :endStampDate " + " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";

	private static final String GET_STAMP_BY_LIST_CARD = "select s from KrcdtStamp s "
			+ " where s.pk.cardNumber in  :cardNumbers AND s.pk.contractCode = :contractCode ";
	
	private static final String GET_STAMP_BY_DATEPERIOD = "select d.workLocationName, s from KrcdtStamp s "
			+ " LEFT JOIN KrcmtWorkLocation d ON s.stampPlace = d.kwlmtWorkLocationPK.workLocationCD"
			+ " AND d.kwlmtWorkLocationPK.companyID = :cid"
			+ " where s.pk.stampDateTime >= :startStampDate "
			+ " and s.pk.stampDateTime <= :endStampDate " 
			+ " and s.cid = :cid"
			+ " AND s.pk.contractCode = :contractCode "
			+ " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";
	
	private static final String GET_STAMP_BY_DATEPERIOD_AND_CARDS = "select  e.sid, d.workLocationName from KrcdtStamp s "
			+ " LEFT JOIN KrcmtWorkLocation d ON s.workLocationCd = d.kwlmtWorkLocationPK.workLocationCD"
			+ " AND d.kwlmtWorkLocationPK.companyID = :cid"
			+ " INNER JOIN KrcmtStampCard e ON e.cardNo = s.pk.cardNumber AND e.contractCd = s.pk.contractCode"
			+ " where s.pk.stampDateTime >= :startStampDate "
			+ " and s.pk.stampDateTime <= :endStampDate " 
			+ " and s.cid = :cid"
			+ " and s.pk.cardNumber in :listCard"
			+ " AND s.pk.contractCode = :contractCode "
			+ " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";
	private static final String GET_STAMP_BY_DATEPERIOD_AND_CARDS_2 = "select s from KrcdtStamp s "
			+ " INNER JOIN KrcmtStampCard e ON e.cardNo = s.pk.cardNumber AND e.contractCd = s.pk.contractCode "
			+ " where s.pk.stampDateTime >= :startStampDate "
			+ " and s.pk.stampDateTime <= :endStampDate " 
			+ " and s.pk.cardNumber in :listCard"
			+ " AND s.pk.contractCode = :contractCode "
			+ " order by s.pk.cardNumber asc, s.pk.stampDateTime asc";
	
	// [1] insert(打刻)
	@Override
	public void insert(Stamp stamp) {
		KrcdtStamp entity = toEntity(stamp);
		this.commandProxy().insert(entity);
	}

	// [2] delete(打刻)
	@Override
	public void delete(String contractCode, String stampNumber, GeneralDateTime stampDateTime, int changeClockArt) {
		this.commandProxy().remove(KrcdtStamp.class, new KrcdtStampPk(contractCode, stampNumber, stampDateTime, changeClockArt));
	}

	// [3] update(打刻)
	@Override
	public void update(Stamp stamp) {
		Optional<KrcdtStamp> entity = this.queryProxy().find(new KrcdtStampPk(stamp.getContractCode().v(), stamp.getCardNumber().v(), stamp.getStampDateTime(), stamp.getType().getChangeClockArt().value), KrcdtStamp.class);
		if(!entity.isPresent()) {
			return;
		}
//		KrcdtStamp entityUpdate = toEntity(stamp);
		this.commandProxy().update(entity.get().toEntityUpdate(stamp));
	}

	// [4] 取得する
	@Override
	public List<Stamp> get(String contractCode, List<StampNumber> stampNumbers, GeneralDate stampDateTime) {
		Set<String> lstCard = stampNumbers.stream().map(x -> x.v()).collect(Collectors.toSet());
		GeneralDateTime start = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(stampDateTime.year(), stampDateTime.month(), stampDateTime.day(),
				23, 59, 59);
		return this.queryProxy().query(GET_STAMP_RECORD, KrcdtStamp.class).setParameter("contractCode", contractCode)
				.setParameter("cardNumbers", lstCard).setParameter("startStampDate", start)
				.setParameter("endStampDate", end).getList(x -> toDomain(x));
	}

	// [5] 打刻カード未登録の打刻データを取得する
	@Override
	public List<Stamp> getStempRcNotResgistNumberStamp(String contractCode, DatePeriod period) {
		GeneralDateTime start = GeneralDateTime.ymdhms(period.start().year(), period.start().month(),
				period.start().day(), 0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(period.end().year(), period.end().month(), period.end().day(), 23,
				59, 59);

		List<Stamp> list = this.queryProxy().query(GET_NOT_STAMP_NUMBER, KrcdtStamp.class)
				.setParameter("contractCode", contractCode).setParameter("startStampDate", start)
				.setParameter("endStampDate", end).getList(x -> toDomain(x));
		return list;
	}

	private KrcdtStamp toEntity(Stamp stamp) {
		String cid = AppContexts.user().companyId();
		GeoCoordinate positionInfor = stamp.getLocationInfor().isPresent() ? stamp.getLocationInfor().get() : null;
		return new KrcdtStamp(new KrcdtStampPk(stamp.getContractCode().v(), stamp.getCardNumber().v(), stamp.getStampDateTime(), stamp.getType().getChangeClockArt().value), 
				cid,
				stamp.getRelieve().getAuthcMethod().value, 
				stamp.getRelieve().getStampMeans().value,
				stamp.getType().getChangeCalArt().value,
				stamp.getType().getSetPreClockArt().value, 
				stamp.getType().isChangeHalfDay(),
				stamp.getType().getGoOutArt().isPresent() ? stamp.getType().getGoOutArt().get().value : null,
				stamp.getImprintReflectionStatus().isReflectedCategory(),
				(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getCardNumberSupport().isPresent())
						? stamp.getRefActualResults().getWorkInforStamp().get().getCardNumberSupport().get().v()
						: null,
				(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD().isPresent())
						? stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD().get().v()
						: null,
				stamp.getRefActualResults().getWorkTimeCode().isPresent()
						? stamp.getRefActualResults().getWorkTimeCode().get().v()
						: null,
				stamp.getRefActualResults().getOvertimeDeclaration().isPresent()
						? stamp.getRefActualResults().getOvertimeDeclaration().get().getOverTime().v()
						: null, // overTime
				stamp.getRefActualResults().getOvertimeDeclaration().isPresent()
						? stamp.getRefActualResults().getOvertimeDeclaration().get().getOverLateNightTime().v()
						: null, // lateNightOverTime
				positionInfor != null? new BigDecimal(positionInfor.getLongitude()).setScale(6, BigDecimal.ROUND_HALF_DOWN): null, // LOCATION_LON
				positionInfor != null? new BigDecimal(positionInfor.getLatitude()).setScale(6, BigDecimal.ROUND_HALF_DOWN): null,  // LOCATION_LAT
				(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getWorkplaceID().isPresent())
						? stamp.getRefActualResults().getWorkInforStamp().get().getWorkplaceID().get().toString()
						: null,  // WORKPLACE_ID
				(stamp.getRefActualResults() != null && stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getEmpInfoTerCode().isPresent())
						? stamp.getRefActualResults().getWorkInforStamp().get().getEmpInfoTerCode().get().toString()
						: null,
				(stamp.getImprintReflectionStatus() != null && stamp.getImprintReflectionStatus().getReflectedDate().isPresent()) ? stamp.getImprintReflectionStatus().getReflectedDate().get() : null, // REFLECTED_INTO_DATE,
				stamp.getStampRecordId()); 
		
		
		
	}

	private Stamp toDomain(KrcdtStamp entity) {
		GeoCoordinate geoLocation = null;
		if (entity.locationLat !=  null && entity.locationLon != null){
			geoLocation = new GeoCoordinate(entity.locationLat.doubleValue(), entity.locationLon.doubleValue()); 
		}
		val stampNumber = new StampNumber(entity.pk.cardNumber);
		val relieve = new Relieve(AuthcMethod.valueOf(entity.autcMethod), StampMeans.valueOf(entity.stampMeans));
		val stampType = StampType.getStampType(entity.changeHalfDay,
				entity.goOutArt == null ? null : GoingOutReason.valueOf(entity.goOutArt),
				SetPreClockArt.valueOf(entity.preClockArt), ChangeClockArt.valueOf(entity.pk.changeClockArt),
				ChangeCalArt.valueOf(entity.changeCalArt));
		
		OvertimeDeclaration overtime = entity.overTime == null ? null
				: new OvertimeDeclaration(new AttendanceTime(entity.overTime),
						new AttendanceTime(entity.lateNightOverTime));
		
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(
				entity.workplaceId ==  null ? Optional.empty():Optional.of(entity.workplaceId), 
				entity.timeRecordCode ==  null ? Optional.empty():Optional.of(new EmpInfoTerminalCode(entity.timeRecordCode)),
				entity.stampPlace == null ? Optional.empty() : Optional.of(new WorkLocationCD(entity.stampPlace)), 
				entity.suportCard == null ? Optional.empty() : Optional.of(new SupportCardNumber(entity.suportCard)));
		
		val refectActualResult = new RefectActualResult(workInformationStamp,
				entity.workTime == null ? null : new WorkTimeCode(entity.workTime),
				overtime);
		
		val imprintReflectionState = new ImprintReflectionState(entity.reflectedAtr, Optional.ofNullable(entity.reflectedIntoDate));
		
		return new Stamp(new ContractCode(entity.pk.contractCode) ,
						stampNumber, 
						entity.pk.stampDateTime,
						relieve, stampType, refectActualResult,
						imprintReflectionState, Optional.ofNullable(geoLocation), Optional.empty(), entity.stampRecordId);
	}
	
	private Stamp toDomainVer2(Object[] object) {
		KrcdtStamp entity = (KrcdtStamp) object[1];
		
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(
				entity.workplaceId ==  null ? Optional.empty():Optional.of(entity.workplaceId), 
				entity.timeRecordCode ==  null ? Optional.empty():Optional.of(new EmpInfoTerminalCode(entity.timeRecordCode)),
				entity.stampPlace == null ? Optional.empty() : Optional.of(new WorkLocationCD(entity.stampPlace)), 
				entity.suportCard == null ? Optional.empty() : Optional.of(new SupportCardNumber(entity.suportCard)));
		
		Stamp stamp = new Stamp(new ContractCode(entity.pk.contractCode), new StampNumber(entity.pk.cardNumber),
				entity.pk.stampDateTime,
				new Relieve(AuthcMethod.valueOf(entity.autcMethod), StampMeans.valueOf(entity.stampMeans)),
				new StampType(entity.changeHalfDay,
						entity.goOutArt == null ? null : GoingOutReason.valueOf(entity.goOutArt),
						SetPreClockArt.valueOf(entity.preClockArt), ChangeClockArt.valueOf(entity.pk.changeClockArt),
						ChangeCalArt.valueOf(entity.changeCalArt)),
				new RefectActualResult(workInformationStamp,
						entity.workTime == null ? null : new WorkTimeCode(entity.workTime),
						entity.overTime == null ? null : new OvertimeDeclaration(new AttendanceTime(entity.overTime),new AttendanceTime(entity.lateNightOverTime))),
				new ImprintReflectionState(entity.reflectedAtr, Optional.ofNullable(entity.reflectedIntoDate)),
				Optional.ofNullable(( entity.locationLat == null && entity.locationLon == null ) ? null
						: new GeoCoordinate(entity.locationLat.doubleValue(), entity.locationLon.doubleValue())), 
				Optional.empty(),
				entity.stampRecordId);
		return stamp;
	}

	private Stamp toDomainVer3(Object[] object) {
		KrcdtStamp entity = (KrcdtStamp) object[2];
		ContractCode contractCd = new ContractCode(AppContexts.user().contractCode());
		
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(
				entity.workplaceId ==  null ? Optional.empty():Optional.of(entity.workplaceId), 
				entity.timeRecordCode ==  null ? Optional.empty():Optional.of(new EmpInfoTerminalCode(entity.timeRecordCode)),
				entity.stampPlace == null ? Optional.empty() : Optional.of(new WorkLocationCD(entity.stampPlace)), 
				entity.suportCard == null ? Optional.empty() : Optional.of(new SupportCardNumber(entity.suportCard)));
		
		Stamp stamp =  new Stamp(contractCd, new StampNumber(entity.pk.cardNumber), entity.pk.stampDateTime,
					new Relieve(AuthcMethod.valueOf(entity.autcMethod), StampMeans.valueOf(entity.stampMeans)),
					StampType.getStampType(entity.changeHalfDay,
							entity.goOutArt == null ? null : GoingOutReason.valueOf(entity.goOutArt),
							SetPreClockArt.valueOf(entity.preClockArt), ChangeClockArt.valueOf(entity.pk.changeClockArt),
							ChangeCalArt.valueOf(entity.changeCalArt)),

					new RefectActualResult(workInformationStamp,
							entity.workTime == null ? null : new WorkTimeCode(entity.workTime),
							entity.overTime == null ? null : new OvertimeDeclaration(new AttendanceTime(entity.overTime),new AttendanceTime(entity.lateNightOverTime))),
					new ImprintReflectionState(entity.reflectedAtr, Optional.ofNullable(entity.reflectedIntoDate)),
					Optional.ofNullable(( entity.locationLat == null && entity.locationLon == null) ? null :
						new GeoCoordinate(entity.locationLat.doubleValue(),entity.locationLon.doubleValue())),
					Optional.empty(),
					entity.stampRecordId
			);
		return stamp;
	}

	@Override
	public List<Stamp> getByListCard(List<String> stampNumbers) {
		if(stampNumbers.isEmpty())
			return Collections.emptyList();
		String contractCode = AppContexts.user().contractCode();
		List<Stamp> data = this.queryProxy().query(GET_STAMP_BY_LIST_CARD, KrcdtStamp.class)
				.setParameter("cardNumbers", stampNumbers)
				.setParameter("contractCode", contractCode)
				.getList(x -> toDomain(x));
		
		return data;
	}

	@Override
	public List<Stamp> getByDateperiod(String companyId, DatePeriod period) {
		GeneralDateTime start = GeneralDateTime.ymdhms(period.start().year(), period.start().month(),
				period.start().day(), 0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(period.end().year(), period.end().month(), period.end().day(), 23,
				59, 59);
		String contractCode = AppContexts.user().contractCode();
		return this.queryProxy().query(GET_STAMP_BY_DATEPERIOD, Object[].class)
				.setParameter("startStampDate", start)
				.setParameter("endStampDate", end)
				.setParameter("cid", companyId)
				.setParameter("contractCode", contractCode)
				.getList(x -> toDomainVer2(x));

	}
	
	@Override
	public List<Stamp> getByDateTimeperiod(List<String> listCard, GeneralDateTime startDate, GeneralDateTime endDate) {
		String contractCode = AppContexts.user().contractCode();
		List<Stamp> data =  this.queryProxy().query(GET_STAMP_BY_DATEPERIOD_AND_CARDS_2, KrcdtStamp.class)
				.setParameter("startStampDate", startDate)
				.setParameter("endStampDate", endDate)
				.setParameter("listCard", listCard)
				.setParameter("contractCode", contractCode)
				.getList(x -> toDomain(x));
		return data;
	}

	@Override
	public List<Stamp> getByCardAndPeriod(String companyId, List<String> listCard, DatePeriod period) {
		if (CollectionUtil.isEmpty(listCard)) {
			return Collections.emptyList();
		}
		GeneralDateTime start = GeneralDateTime.ymdhms(period.start().year(), period.start().month(),
				period.start().day(), 0, 0, 0);

		GeneralDateTime end = GeneralDateTime.ymdhms(period.end().year(), period.end().month(), period.end().day(), 23,
				59, 59);
		List<Stamp> data = new ArrayList<>();
		String contractCode = AppContexts.user().contractCode();
		CollectionUtil.split(listCard, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			data.addAll(this.queryProxy().query(GET_STAMP_BY_DATEPERIOD_AND_CARDS, Object[].class)
				.setParameter("startStampDate", start)
				.setParameter("endStampDate", end)
				.setParameter("cid", companyId)
				.setParameter("listCard", listCard)
				.setParameter("contractCode", contractCode)
				.getList(x -> toDomainVer3(x)));
		});
		return data;
	}

	@Override
	public Optional<Stamp> get(String contractCode, StampNumber stampNumber) {
		
		return this.queryProxy().query(GET_STAMP_RECORD_BY_NUMBER, KrcdtStamp.class).setParameter("contractCode", contractCode)
				.setParameter("cardNumbers", stampNumber).getList().stream().findFirst().map(x -> toDomain(x));
	}

	@Override
	public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber, GeneralDateTime dateTime,
			ChangeClockArt changeClockArt) {
		return this.queryProxy()
				.find(new KrcdtStampPk(contractCode.v(), stampNumber.v(), dateTime, changeClockArt.value),
						KrcdtStamp.class)
				.isPresent();
	}

}
