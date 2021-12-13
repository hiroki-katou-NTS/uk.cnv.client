package nts.uk.ctx.at.shared.app.find.remainingnumber.otherhdinfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessHolidayManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;
import nts.uk.shr.pereg.app.find.dto.DataClassification;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainBySidDto;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainDto;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Stateless
public class OtherHolidayInfoFinder implements PeregFinder<OtherHolidayInfoDto> {

	@Inject
	private PublicHolidayCarryForwardDataRepository publicHolidayCarryForwardDataRepository;

	@Inject
	private ExcessLeaveInfoRepository excessLeaveInfoRepository;

	@Inject
	private LeaveManaDataRepository leaveManaDataRepository;

	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepository;

	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepository;

	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;

	@Inject
	private ExcessHolidayManaDataRepository excessHolidayManaDataRepository;

	@Override
	public String targetCategoryCode() {
		return "CS00035";
	}

	@Override
	public Class<OtherHolidayInfoDto> dtoClass() {
		return OtherHolidayInfoDto.class;
	}

	@Override
	public DataClassification dataType() {
		return DataClassification.EMPLOYEE;
	}

	@Override
	public PeregDomainDto getSingleData(PeregQuery query) {
		String cid = AppContexts.user().companyId();

		Optional<PublicHolidayCarryForwardData> holidayRemain = publicHolidayCarryForwardDataRepository.get(query.getEmployeeId());
		Optional<ExcessLeaveInfo> excessLeave = excessLeaveInfoRepository.get(query.getEmployeeId());

		OtherHolidayInfoDto dto = OtherHolidayInfoDto.createFromDomain(holidayRemain, excessLeave);

		// Item IS00366 --------------
		// 取得した「休出管理データ」の未使用日数を合計
		Double sumUnUsedDay = leaveManaDataRepository
				.getBySidWithsubHDAtr(cid, query.getEmployeeId(), DigestionAtr.UNUSED.value).stream()
				.mapToDouble(i -> i.getUnUsedDays().v()).sum();
		// 取得した「代休管理データ」の未相殺日数を合計
		Double sumRemain = comDayOffManaDataRepository.getBySidWithReDay(cid, query.getEmployeeId()).stream()
				.mapToDouble(i -> i.getRemainDays().v()).sum();
		dto.setRemainNumber(new BigDecimal(sumUnUsedDay - sumRemain));
		// ----------------------------

		// Item IS00368 ---------------
		// 取得した「振出管理データ」の未使用日数を合計
		sumUnUsedDay = payoutManagementDataRepository
				.getSidWithCod(cid, query.getEmployeeId(), DigestionAtr.UNUSED.value).stream()
				.mapToDouble(i -> i.getUnUsedDays().v()).sum();
		// 取得した「振休管理データ」の未相殺日数を合計
		sumRemain = substitutionOfHDManaDataRepository.getBysiDRemCod(cid, query.getEmployeeId()).stream()
				.mapToDouble(i -> i.getRemainDays().v()).sum();
		dto.setRemainsLeft(new BigDecimal(sumUnUsedDay - sumRemain));
		// ----------------------------

		// Item IS00374 ---------------
		// 月初の超過有休残数を取得
		sumRemain = excessHolidayManaDataRepository
				.getBySidWithExpCond(cid, query.getEmployeeId(), LeaveExpirationStatus.AVAILABLE.value).stream()
				.mapToDouble(i -> i.getInfo().getRemainNumer().minute()).sum();
		dto.setExtraHours(OtherHolidayInfoDto.convertTime(sumRemain.intValue()));
		// ----------------------------

		return dto;
	}

	@Override
	public List<PeregDomainDto> getListData(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComboBoxObject> getListFirstItems(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.shr.pereg.app.find.PeregFinder#getAllData(nts.uk.shr.pereg.app.
	 * find.PeregQueryByListEmp)
	 */
	@Override
	public List<GridPeregDomainDto> getAllData(PeregQueryByListEmp query) {
		String cid = AppContexts.user().companyId();
		List<GridPeregDomainDto> result = new ArrayList<>();
		List<String> listEmp = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		Map<String, List<PublicHolidayCarryForwardData>> listHolidayRemainMap = publicHolidayCarryForwardDataRepository.getAll(listEmp)
				.parallelStream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		Map<String, List<ExcessLeaveInfo>> listExcessLeaveMap = excessLeaveInfoRepository.getAll(listEmp, cid)
				.parallelStream().collect(Collectors.groupingBy(c -> c.getSID()));
		Map<String, Double> leaveMaDataMap = leaveManaDataRepository.getAllBySidWithsubHDAtr(cid, listEmp,
				DigestionAtr.UNUSED.value);
		Map<String, Double> comDayManaData = comDayOffManaDataRepository.getAllBySidWithReDay(cid, listEmp);
		Map<String, Double> payoutMangement = payoutManagementDataRepository.getAllSidWithCod(cid, listEmp,
				DigestionAtr.UNUSED.value);
		Map<String, Double> subOfHDManagementData = substitutionOfHDManaDataRepository.getAllBysiDRemCod(cid, listEmp);
		Map<String, Double> exHolidayManagement = excessHolidayManaDataRepository.getAllBySidWithExpCond(cid, listEmp,
				LeaveExpirationStatus.AVAILABLE.value);
		query.getEmpInfos().stream().forEach(c -> {
			List<PublicHolidayCarryForwardData> listHolidayRemain = listHolidayRemainMap.get(c.getEmployeeId());
			List<ExcessLeaveInfo> listExcessLeave = listExcessLeaveMap.get(c.getEmployeeId());
			PublicHolidayCarryForwardData publicHolidayRemain = null;
			ExcessLeaveInfo excessLeaveInfo = null;
			if (listHolidayRemain != null) {
				publicHolidayRemain = listHolidayRemain.get(0);
			}
			if (listExcessLeave != null) {
				excessLeaveInfo = listExcessLeave.get(0);
			}

			OtherHolidayInfoDto dto = OtherHolidayInfoDto.createFromDomain(Optional.ofNullable(publicHolidayRemain),
					Optional.ofNullable(excessLeaveInfo));
			// Item IS00366 --------------
			// 取得した「休出管理データ」の未使用日数を合計
			Double sumUnUsedDay = leaveMaDataMap.get(c.getEmployeeId());
			Double sumRemain = comDayManaData.get(c.getEmployeeId());
			Double result_1 = subTractObject(sumUnUsedDay, sumRemain);
			dto.setRemainNumber(result_1 == null? null: new BigDecimal(result_1));
			
			// Item IS00366 --------------
			// 取得した「休出管理データ」の未使用日数を合計
			Double payoutData = payoutMangement.get(c.getEmployeeId());
			Double subOfHdData  = subOfHDManagementData.get(c.getEmployeeId());
			Double result_2 = subTractObject(payoutData, subOfHdData);
			dto.setRemainsLeft(result_2 == null? null: new BigDecimal(result_2));
			
			if (!exHolidayManagement.isEmpty()) {
				dto.setExtraHours(
						OtherHolidayInfoDto.convertTime(exHolidayManagement.get(c.getEmployeeId()).intValue()));
			}
			result.add(new GridPeregDomainDto(c.getEmployeeId(), c.getPersonId(), dto));

		});
		return result;
	}

	@Override
	public List<GridPeregDomainBySidDto> getListData(PeregQueryByListEmp query) {
		String cid = AppContexts.user().companyId();
		List<GridPeregDomainBySidDto> result = new ArrayList<>();
		List<String> listEmp = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		
		Map<String, List<PublicHolidayCarryForwardData>> listHolidayRemainMap = publicHolidayCarryForwardDataRepository.getAll(listEmp)
				.parallelStream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		
		Map<String, Object> enums = new HashMap<>();
		
		Map<String, List<ExcessLeaveInfo>> listExcessLeaveMap = excessLeaveInfoRepository.getAllForCPS013(listEmp, cid, enums)
				.parallelStream().collect(Collectors.groupingBy(c -> c.getSID()));
		
		Map<String, Double> leaveMaDataMap = leaveManaDataRepository.getAllBySidWithsubHDAtr(cid, listEmp,
				DigestionAtr.UNUSED.value);
		Map<String, Double> comDayManaData = comDayOffManaDataRepository.getAllBySidWithReDay(cid, listEmp);
		Map<String, Double> payoutMangement = payoutManagementDataRepository.getAllSidWithCod(cid, listEmp,
				DigestionAtr.UNUSED.value);
		Map<String, Double> subOfHDManagementData = substitutionOfHDManaDataRepository.getAllBysiDRemCod(cid, listEmp);
		Map<String, Double> exHolidayManagement = excessHolidayManaDataRepository.getAllBySidWithExpCond(cid, listEmp,
				LeaveExpirationStatus.AVAILABLE.value);
		query.getEmpInfos().stream().forEach(c -> {
			List<PublicHolidayCarryForwardData> listHolidayRemain = listHolidayRemainMap.get(c.getEmployeeId());
			List<ExcessLeaveInfo> listExcessLeave = listExcessLeaveMap.get(c.getEmployeeId());
			PublicHolidayCarryForwardData publicHolidayCarryForwardData = null;
			ExcessLeaveInfo excessLeaveInfo = null;
			if (listHolidayRemain != null) {
				publicHolidayCarryForwardData = listHolidayRemain.get(0);
			}
			if (listExcessLeave != null) {
				excessLeaveInfo = listExcessLeave.get(0);
			}

			OtherHolidayInfoDto dto = OtherHolidayInfoDto.createFromDomainCPS013(Optional.ofNullable(publicHolidayCarryForwardData),
					Optional.ofNullable(excessLeaveInfo), enums);
			// Item IS00366 --------------
			// 取得した「休出管理データ」の未使用日数を合計
			Double sumUnUsedDay = leaveMaDataMap.get(c.getEmployeeId());
			Double sumRemain = comDayManaData.get(c.getEmployeeId());
			Double result_1 = subTractObject(sumUnUsedDay, sumRemain);
			dto.setRemainNumber(result_1 == null? null: new BigDecimal(result_1));
			
			// Item IS00366 --------------
			// 取得した「休出管理データ」の未使用日数を合計
			Double payoutData = payoutMangement.get(c.getEmployeeId());
			Double subOfHdData  = subOfHDManagementData.get(c.getEmployeeId());
			Double result_2 = subTractObject(payoutData, subOfHdData);
			dto.setRemainsLeft(result_2 == null? null: new BigDecimal(result_2));
			
			if (!exHolidayManagement.isEmpty()) {
				dto.setExtraHours(
						OtherHolidayInfoDto.convertTime(exHolidayManagement.get(c.getEmployeeId()).intValue()));
			}
			result.add(new GridPeregDomainBySidDto(c.getEmployeeId(), c.getPersonId(), Arrays.asList(dto)));
		});
		return result;
	}
	
	
	private Double subTractObject(Double sobitru, Double sotru) {
		if(sobitru == null && sotru != null) {
			return -sotru;
		}else if(sobitru != null && sotru == null) {
			return sobitru;
		}else if(sobitru != null && sotru != null) {
			return sobitru - sotru;
		}
		
		return null;
		
	}


}
