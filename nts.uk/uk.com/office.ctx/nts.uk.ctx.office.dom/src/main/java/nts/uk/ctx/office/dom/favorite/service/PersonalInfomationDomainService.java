package nts.uk.ctx.office.dom.favorite.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.favorite.adapter.EmployeeBasicImport;
import nts.uk.ctx.office.dom.favorite.adapter.EmployeeJobHistImport;
import nts.uk.ctx.office.dom.favorite.adapter.SequenceMasterImport;
import nts.uk.ctx.office.dom.favorite.adapter.WorkplaceInforImport;

/*
 * 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.在席照会.お気に入り.個人情報を取得
 * 
 * 
 * 個人情報を取得
 */
public class PersonalInfomationDomainService {

	/**
	 * [1] 個人情報を取得
	 * 
	 * 選択したお気に入りの個人情報を取得する
	 * 
	 * @param require
	 * @param sids     List<社員ID>
	 * @param baseDate 基準日
	 * @return List<PersonalInfomationObj> List<個人基本情報>
	 */
	public static List<EmployeeBasicImport> getPersonalInfomation(Require require, List<String> sids, GeneralDate baseDate) {
		// 社員の職場IDを取得する
		Map<String, String> employeesWorkplaceId = require.getEmployeesWorkplaceId(sids, baseDate);
		// 社員の職位を取得する
		Map<String, EmployeeJobHistImport> positionBySidsAndBaseDate = require.getPositionBySidsAndBaseDate(sids,
				baseDate);
		// 職位の序列を取得する
		List<SequenceMasterImport> rankOfPosition = require.getRankOfPosition();

		List<String> workSplaceId = (List<String>) employeesWorkplaceId.values();
		workSplaceId.stream().distinct().collect(Collectors.toList());

		// 職場情報を取得する
		Map<String, WorkplaceInforImport> workplaceInfor = require.getWorkplaceInfor(workSplaceId, baseDate);

		// create $職場情報SIDMap ：Map<社員ID、職場情報>
		Map<String, WorkplaceInforImport> workplaceInforSidMap = sids.stream()
				.collect(Collectors.toMap((sid -> sid), (sid -> workplaceInfor.get(sid))));

		// 個人情報を取得する
		Map<String, EmployeeBasicImport> personalInformation = require.getPersonalInformation(sids);

		// Sort
		List<EmployeeBasicImport> sortedList = sortingProcess(sids, workplaceInforSidMap,
				positionBySidsAndBaseDate, rankOfPosition, personalInformation);
		return sortedList;
	}

	/**
	 * [pvt-1]ソートする処理
	 * 
	 * ソート：職場 → 職位 → 社員コード
	 * 
	 * @param sIds          List<社員ID>
	 * @param workplaceInfo Map<社員ID、職場情報>
	 * @param positionId    Map<社員ID、職位情報>
	 * @param positionOrder List<序列>
	 * @param personalInfo  Map<社員ID、個人基本情報>
	 * @return List<個人基本情報>
	 */
	private static List<EmployeeBasicImport> sortingProcess(
			List<String> sIds,
			Map<String, WorkplaceInforImport> workplaceInfo, 
			Map<String, EmployeeJobHistImport> positionId,
			List<SequenceMasterImport> positionOrder, 
			Map<String, EmployeeBasicImport> personalInfo) {
		// create ソート情報：｛社員ID、階層コード、Optional<並び順>、職位コード、社員コード｝
		List<PersonalInfomationObj> sortInfomation = sIds.stream().map(v -> {
			return PersonalInfomationObj.builder().sid(v).hierarchyCode(workplaceInfo.get(v).getHierarchyCode())
					.order(getOrder(positionId.get(v).getSequenceCode(), positionOrder))
					.positionCode(positionId.get(v).getSequenceCode())
					.employeeCode(personalInfo.get(v).getEmployeeCode()).build();
		}).collect(Collectors.toList());

		// ソート実行
		Comparator<PersonalInfomationObj> compare = Comparator.comparing(PersonalInfomationObj::getOrderOptional)
				.thenComparing(PersonalInfomationObj::getPositionCode)
				.thenComparing(PersonalInfomationObj::getEmployeeCode);
		if (!workplaceInfo.isEmpty()) {
			compare = Comparator.comparing(PersonalInfomationObj::getHierarchyCode)
					.thenComparing(PersonalInfomationObj::getOrderOptional)
					.thenComparing(PersonalInfomationObj::getPositionCode)
					.thenComparing(PersonalInfomationObj::getEmployeeCode);
		}
		sortInfomation.sort(compare);
		return sortInfomation.stream()
				.map(item -> personalInfo.get(item.getSid()))
				.collect(Collectors.toList());
	}

	private static Optional<Integer> getOrder(String sequenceCode, List<SequenceMasterImport> positionOrder) {
		List<SequenceMasterImport> filterList = positionOrder.stream()
				.filter(position -> position.getSequenceCode().equalsIgnoreCase(sequenceCode))
				.collect(Collectors.toList());
		return filterList.isEmpty() ? Optional.empty() : Optional.ofNullable(filterList.get(0).getOrder());
	}

	public static interface Require {

		/**
		 * [R-1] 社員の職場IDを取得する
		 * 
		 * 社員の職場IDを取得するAdapter.自分の職場IDを取得する(社員IDリスト、基準日)
		 * 
		 */
		public Map<String, String> getEmployeesWorkplaceId(List<String> sIds, GeneralDate baseDate);

		/**
		 * [R-2]職場情報を取得する
		 * 
		 * 職場情報を取得するAdapter.職場情報を取得する(職場IDリスト,基準日)
		 * 
		 */
		public Map<String, WorkplaceInforImport> getWorkplaceInfor(List<String> lstWorkplaceID, GeneralDate baseDate);

		/**
		 * [R-3] 社員の職位を取得する
		 * 
		 * 社員の職位を取得するAdapter.社員の職位を取得する(社員IDリスト,基準日)
		 * 
		 */
		public Map<String, EmployeeJobHistImport> getPositionBySidsAndBaseDate(List<String> sIds, GeneralDate baseDate);

		/**
		 * [R-4]職位の序列を取得する
		 * 
		 * 職位の序列を取得するAdapter.職位の序列を取得する()
		 * 
		 */
		public List<SequenceMasterImport> getRankOfPosition();

		/**
		 * [R-5] 個人情報を取得する
		 * 
		 * 個人情報を取得するAdapter.個人情報を取得する(社員IDリスト)
		 * 
		 */
		public Map<String, EmployeeBasicImport> getPersonalInformation(List<String> lstSid);
	}
}
