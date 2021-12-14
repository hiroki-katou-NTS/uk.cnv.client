package nts.uk.ctx.at.request.infra.repository.application.holidaywork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeApplicationSetting;
import nts.uk.ctx.at.request.dom.application.overtime.ReasonDivergence;
import nts.uk.ctx.at.request.infra.entity.application.holidaywork.KrqdtAppHdWork;
import nts.uk.ctx.at.request.infra.entity.application.holidaywork.KrqdtAppHdWorkTime;
import nts.uk.ctx.at.request.infra.entity.application.holidaywork.KrqdtAppHolidayWorkPK;
import nts.uk.ctx.at.request.infra.entity.application.holidaywork.KrqdtHolidayWorkInputPK;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.shr.com.context.AppContexts;

/**
 * Refactor5
 * @author huylq
 *
 */
@Stateless
public class JpaAppHolidayWorkRepository extends JpaRepository implements AppHolidayWorkRepository{
	
	public static final String FIND_BY_APPID = "SELECT a FROM KrqdtAppHdWork as a WHERE a.krqdtAppHolidayWorkPK.cid = :companyId and a.krqdtAppHolidayWorkPK.appId = :appId";
	public static final String FIND_BY_APPID_IN = "SELECT a FROM KrqdtAppHdWork as a WHERE a.krqdtAppHolidayWorkPK.cid = :companyId and a.krqdtAppHolidayWorkPK.appId in :appIds";

	@Override
	public Optional<AppHolidayWork> find(String companyId, String applicationId) {
		return this.queryProxy().query(FIND_BY_APPID, KrqdtAppHdWork.class)
				.setParameter("companyId", companyId).setParameter("appId", applicationId).getSingle(x -> x.toDomain());
	}
	
	@Override
	public void add(AppHolidayWork appHolidayWork) {
		this.commandProxy().insert(toEntity(appHolidayWork));
		this.getEntityManager().flush();
	}

	@Override
	public void update(AppHolidayWork appHolidayWork) {
		KrqdtAppHdWork entity = toEntity(appHolidayWork);
		Optional<KrqdtAppHdWork> updateEntityOp = this.queryProxy().find(entity.getKrqdtAppHolidayWorkPK(), KrqdtAppHdWork.class);
		if(!updateEntityOp.isPresent()) return;
		KrqdtAppHdWork updateEntity = updateEntityOp.get();
		updateEntity.workTypeCode = entity.workTypeCode;
		updateEntity.workTimeCode = entity.workTimeCode;
		updateEntity.workTimeStart1 = entity.workTimeStart1;
		updateEntity.workTimeEnd1 = entity.workTimeEnd1;
		updateEntity.goWorkAtr = entity.goWorkAtr;
		updateEntity.backHomeAtr = entity.backHomeAtr;
		updateEntity.workTimeStart2 = entity.workTimeStart2;
		updateEntity.workTimeEnd2 = entity.workTimeEnd2;
		
		updateEntity.divergenceTimeNo = entity.divergenceTimeNo;
		updateEntity.divergenceCode = entity.divergenceCode;
		updateEntity.divergenceReason = entity.divergenceReason;
		updateEntity.overtimeNight = entity.overtimeNight;
		updateEntity.totalNight = entity.totalNight;
		updateEntity.legalHdNight = entity.legalHdNight;
		updateEntity.nonLegalHdNight = entity.nonLegalHdNight;
		updateEntity.nonLegalPublicHdNight = entity.nonLegalPublicHdNight;
		
		updateEntity.breakTimeStart1 = entity.breakTimeStart1;
		updateEntity.breakTimeEnd1 = entity.breakTimeEnd1;
		updateEntity.breakTimeStart2 = entity.breakTimeStart2;
		updateEntity.breakTimeEnd2 = entity.breakTimeEnd2;
		updateEntity.breakTimeStart3 = entity.breakTimeStart3;
		updateEntity.breakTimeEnd3 = entity.breakTimeEnd3;
		updateEntity.breakTimeStart4 = entity.breakTimeStart4;
		updateEntity.breakTimeEnd4 = entity.breakTimeEnd4;
		updateEntity.breakTimeStart5 = entity.breakTimeStart5;
		updateEntity.breakTimeEnd5 = entity.breakTimeEnd5;
		updateEntity.breakTimeStart6 = entity.breakTimeStart6;
		updateEntity.breakTimeEnd6 = entity.breakTimeEnd6;
		updateEntity.breakTimeStart7 = entity.breakTimeStart7;
		updateEntity.breakTimeEnd7 = entity.breakTimeEnd7;
		updateEntity.breakTimeStart8 = entity.breakTimeStart8;
		updateEntity.breakTimeEnd8 = entity.breakTimeEnd8;
		updateEntity.breakTimeStart9 = entity.breakTimeStart9;
		updateEntity.breakTimeEnd9 = entity.breakTimeEnd9;
		updateEntity.breakTimeStart10 = entity.breakTimeStart10;
		updateEntity.breakTimeEnd10 = entity.breakTimeEnd10;
		
		List<KrqdtAppHdWorkTime> holidayWorkInputs = new ArrayList<KrqdtAppHdWorkTime>();
		
		entity.getHolidayWorkInputs().stream().forEach(x -> {
			Optional<KrqdtAppHdWorkTime> result = updateEntity.getHolidayWorkInputs().stream().filter(
					a -> a.getKrqdtHolidayWorkInputPK().getAttendanceType() == x.getKrqdtHolidayWorkInputPK().getAttendanceType()
							&& a.getKrqdtHolidayWorkInputPK().getAppId() == x.getKrqdtHolidayWorkInputPK().getAppId()
							&& a.getKrqdtHolidayWorkInputPK().getCid() == x.getKrqdtHolidayWorkInputPK().getCid()
							&& a.getKrqdtHolidayWorkInputPK().getFrameNo() == x.getKrqdtHolidayWorkInputPK().getFrameNo()
					).findFirst();
			KrqdtAppHdWorkTime krqdtHolidayWorkInput;
			if (result.isPresent()) {
				result.get().applicationTime = x.applicationTime;
				krqdtHolidayWorkInput = result.get();
			} else {
				krqdtHolidayWorkInput = x;
				krqdtHolidayWorkInput.contractCd = AppContexts.user().contractCode();
			}
			holidayWorkInputs.add(krqdtHolidayWorkInput);
		});
		updateEntity.setHolidayWorkInputs(holidayWorkInputs);
		
		this.commandProxy().update(updateEntity);
		this.getEntityManager().flush();
		
		
	}
	
	private KrqdtAppHdWork toEntity(AppHolidayWork domain) {
		String cid = AppContexts.user().companyId();
		KrqdtAppHdWork entity = new KrqdtAppHdWork();
		KrqdtAppHolidayWorkPK entityPK = new KrqdtAppHolidayWorkPK(cid, domain.getAppID());			
		entity.setKrqdtAppHolidayWorkPK(entityPK);
		
		entity.workTypeCode = domain.getWorkInformation().getWorkTypeCode().v();
		entity.workTimeCode = domain.getWorkInformation().getWorkTimeCode().v();
		
		
		List<ReasonDivergence> reasonDivergenceList = domain.getApplicationTime().getReasonDissociation().orElse(Collections.emptyList());
		if(!reasonDivergenceList.isEmpty()) {
			ReasonDivergence reasonDivergence = reasonDivergenceList.get(0);
			entity.divergenceTimeNo = reasonDivergence.getDiviationTime();
			entity.divergenceCode = reasonDivergence.getReasonCode() != null ? reasonDivergence.getReasonCode().v() : null;
			entity.divergenceReason = reasonDivergence.getReason() != null ? reasonDivergence.getReason().v() : null;	
		}
		
		if (domain.getApplicationTime().getOverTimeShiftNight().isPresent()) {
			entity.overtimeNight = domain.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ?
					domain.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : null;
			entity.totalNight = domain.getApplicationTime().getOverTimeShiftNight().get().getMidNightOutSide() != null ?
					domain.getApplicationTime().getOverTimeShiftNight().get().getMidNightOutSide().v() : null;
			if (!CollectionUtil.isEmpty(domain.getApplicationTime().getOverTimeShiftNight().get().getMidNightHolidayTimes())) {
				domain.getApplicationTime()
							.getOverTimeShiftNight()
							.get()
							.getMidNightHolidayTimes()
							.stream()
							.forEach(i -> {
								if (i.getLegalClf() == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
									entity.legalHdNight = i.getAttendanceTime().v();
								} else if (i.getLegalClf() == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
									entity.nonLegalHdNight = i.getAttendanceTime().v();
								} else if (i.getLegalClf() == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
									entity.nonLegalPublicHdNight = i.getAttendanceTime().v();
								}
							});
			}
		}
		
		entity.backHomeAtr = domain.getBackHomeAtr().value;
		entity.goWorkAtr = domain.getGoWorkAtr().value;
		
		List<TimeZoneWithWorkNo> breakTimes = domain.getBreakTimeList().orElse(Collections.emptyList());
		breakTimes.stream().forEach(item -> {
			if (item.getWorkNo().v() == 1) {
				entity.breakTimeStart1 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd1 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 2) {
				entity.breakTimeStart2 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd2 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 3) {
				entity.breakTimeStart3 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd3 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 4) {
				entity.breakTimeStart4 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd4 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 5) {
				entity.breakTimeStart5 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd5 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 6) {
				entity.breakTimeStart6 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd6 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 7) {
				entity.breakTimeStart7 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd7 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 8) {
				entity.breakTimeStart8 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd8 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 9) {
				entity.breakTimeStart9 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd9 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 10) {
				entity.breakTimeStart10 = item.getTimeZone().getStartTime().v();
				entity.breakTimeEnd10 = item.getTimeZone().getEndTime().v();
			}
		});	
		
		List<TimeZoneWithWorkNo> workHours = domain.getWorkingTimeList().orElse(Collections.emptyList());
		workHours.stream().forEach(item -> {
			if (item.getWorkNo().v() == 1) {
				entity.workTimeStart1 = item.getTimeZone().getStartTime().v();
				entity.workTimeEnd1 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 2) {
				entity.workTimeStart2 = item.getTimeZone().getStartTime().v();
				entity.workTimeEnd2 = item.getTimeZone().getEndTime().v();
			}
		});
		
		entity.holidayWorkInputs = new ArrayList<KrqdtAppHdWorkTime>();
		List<OvertimeApplicationSetting> overtimeApplicationSettings = domain.getApplicationTime().getApplicationTime();
		if (!CollectionUtil.isEmpty(overtimeApplicationSettings)) {
			entity.holidayWorkInputs = overtimeApplicationSettings
				.stream()
				.map(x -> new KrqdtAppHdWorkTime(
										new KrqdtHolidayWorkInputPK(
												AppContexts.user().companyId(),
												domain.getAppID(),
												x.getAttendanceType().value,
												x.getFrameNo().v()),
										x.getApplicationTime().v(),
										null))
				.collect(Collectors.toList());				
		}
		
		return entity;
	}

	@Override
	public void delete(String companyId, String applicationId) {
		Optional<KrqdtAppHdWork> opEntity = this.queryProxy().find(new KrqdtAppHolidayWorkPK(companyId, applicationId), KrqdtAppHdWork.class);
		if(!opEntity.isPresent()){
			throw new RuntimeException("khong ton tai doi tuong de update");
		}
		this.commandProxy().remove(KrqdtAppHdWork.class, new KrqdtAppHolidayWorkPK(companyId, applicationId));
	}

	@Override
	public Map<String, AppHolidayWork> getListAppHdWorkFrame(String companyId, List<String> lstAppId) {
		Map<String, AppHolidayWork> result = new HashMap<>();
		this.queryProxy()
				.query(FIND_BY_APPID_IN, KrqdtAppHdWork.class)
				.setParameter("companyId", companyId)
				.setParameter("appIds", lstAppId)
				.getList(x -> result.put(x.getKrqdtAppHolidayWorkPK().getAppId(), x.toDomain()));
		return result;
	}
}
