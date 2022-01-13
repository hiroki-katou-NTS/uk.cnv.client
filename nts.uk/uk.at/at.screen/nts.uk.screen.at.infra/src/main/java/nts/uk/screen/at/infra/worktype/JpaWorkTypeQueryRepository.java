package nts.uk.screen.at.infra.worktype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkType;
import nts.uk.screen.at.app.worktype.WorkTypeDto;
import nts.uk.screen.at.app.worktype.WorkTypeSetDto;
import nts.uk.screen.at.app.worktype.WorkTypeQueryRepository;

@Stateless
public class JpaWorkTypeQueryRepository extends JpaRepository implements WorkTypeQueryRepository {
	
	/** use lesser value for nested split WHERE IN parameters to make sure total parameters < 2100 */
	private static final int SPLIT_500 = 500;

	private static final String SELECT_ALL_WORKTYPE;
	private static final String SELECT_BY_WORKTYPE_ATR;
	private static final String SELECT_ALL_WORKTYPE_SPE;
	private static final String SELECT_ALL_WORKTYPE_DISP;
	private static final String SELECT_WORKTYPE_KDW006;
	private static final String SELECT_WORKTYPE_KDW006G;
	private static final String SELECT_OT_KAF022;
	private static final String SELECT_ABSENCE_KAF022;
	private static final String SELECT_ABSENCE_SPECIAL_KAF022;
	private static final String SELECT_ABWKCANGE_KAF022;
	private static final String SELECT_BOUNCE_KAF022;
	private static final String SELECT_HDTIME_KAF022;
	private static final String SELECT_HDSHIP_KAF022;
	private static final String SELECT_BUSINESS_TRIP_KAF022;

	static {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" ORDER BY  CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC ");
		SELECT_ALL_WORKTYPE = stringBuilder.toString();

		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, 0) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.oneDayAtr IN :workTypeAtr ");
		stringBuilder.append("OR c.morningAtr IN :workTypeAtr ");
		stringBuilder.append("OR c.afternoonAtr IN :workTypeAtr ");
		stringBuilder.append("ORDER BY  CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC ");
		SELECT_BY_WORKTYPE_ATR = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, 0) ");
		stringBuilder.append("FROM KshmtWorkType c ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0 ");
		stringBuilder.append("AND c.oneDayAtr = :workTypeAtr ");
//		stringBuilder.append("OR c.morningAtr IN :workTypeAtr ");
//		stringBuilder.append("OR c.afternoonAtr IN :workTypeAtr) ");
		stringBuilder.append("ORDER BY c.kshmtWorkTypePK.workTypeCode ASC ");
		SELECT_WORKTYPE_KDW006 = stringBuilder.toString();   
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT c ");
		stringBuilder.append("FROM KshmtWorkType c ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0 ");
		stringBuilder.append("AND ((c.worktypeAtr = 0 and c.oneDayAtr = :workTypeAtr) ");
		stringBuilder.append("OR (c.worktypeAtr = 1 and (c.morningAtr = :workTypeAtr or c.afternoonAtr = :workTypeAtr))) ");
		stringBuilder.append("ORDER BY c.kshmtWorkTypePK.workTypeCode ASC ");
		SELECT_WORKTYPE_KDW006G = stringBuilder.toString();    
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.deprecateAtr = :abolishAtr ");
		stringBuilder.append("AND c.oneDayAtr = :oneDayAtr1");
		stringBuilder.append(" OR c.oneDayAtr = :oneDayAtr2");
		stringBuilder.append(" OR c.morningAtr = :morningAtr1");
		stringBuilder.append(" OR c.morningAtr = :morningAtr2");
		stringBuilder.append(" OR c.afternoonAtr = :afternoonAtr1");
		stringBuilder.append(" OR c.afternoonAtr = :afternoonAtr2");
		stringBuilder.append(" ORDER BY  CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC ");
		SELECT_ALL_WORKTYPE_SPE = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.deprecateAtr = :abolishAtr ");
		stringBuilder.append(" ORDER BY  CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC ");
		SELECT_ALL_WORKTYPE_DISP= stringBuilder.toString();
		
		//KAF022
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND c.deprecateAtr = 0 ");
		stringBuilder.append(" AND ((c.worktypeAtr = 0 AND c.oneDayAtr IN :oneDayAtr) OR (c.worktypeAtr = 1 AND c.morningAtr IN :halfDay) OR (c.worktypeAtr = 1 AND c.afternoonAtr IN :halfDay))");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_OT_KAF022 = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND ((c.worktypeAtr = 0 AND c.oneDayAtr = :oneDayAtr)");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND ((c.morningAtr = :morningAtr AND (c.afternoonAtr = 0 OR c.afternoonAtr = 7 OR c.afternoonAtr = 6 OR c.afternoonAtr = 1 OR c.afternoonAtr = 8 OR c.afternoonAtr = 4 OR c.afternoonAtr = 5 OR c.afternoonAtr = 9)) "
				+ " OR (c.afternoonAtr = :afternoonAtr AND (c.morningAtr = 0 OR c.morningAtr = 7 OR c.morningAtr = 6 OR c.morningAtr = 1 OR c.morningAtr = 8 OR c.morningAtr = 4 OR c.morningAtr = 5 OR c.morningAtr = 9)))))");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_ABSENCE_KAF022 = stringBuilder.toString();

		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND ((c.worktypeAtr = 0 AND (c.oneDayAtr = 5 OR c.oneDayAtr = 4))");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND (((c.morningAtr = 5 OR c.morningAtr = 4) AND (c.afternoonAtr = 0 OR c.afternoonAtr = 7 OR c.afternoonAtr = 6 OR c.afternoonAtr = 1 OR c.afternoonAtr = 8 OR c.afternoonAtr = 4 OR c.afternoonAtr = 5 OR c.afternoonAtr = 9)) "
				+ " OR ((c.afternoonAtr = 5 OR c.afternoonAtr = 4) AND (c.morningAtr = 0 OR c.morningAtr = 7 OR c.morningAtr = 6 OR c.morningAtr = 1 OR c.morningAtr = 8 OR c.morningAtr = 4 OR c.morningAtr = 5 OR c.morningAtr = 9)))))");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_ABSENCE_SPECIAL_KAF022 = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND c.deprecateAtr = 0");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_ABWKCANGE_KAF022 = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND c.deprecateAtr = 0 ");
		stringBuilder.append(" AND ((c.worktypeAtr = 0 AND (c.oneDayAtr = 0  OR c.oneDayAtr = 11 OR c.oneDayAtr = 7)) OR (c.worktypeAtr = 1 AND (c.morningAtr IN :halfDay OR c.afternoonAtr IN :halfDay)))");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_BOUNCE_KAF022 = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND c.deprecateAtr = 0 AND c.oneDayAtr = 11 ");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_HDTIME_KAF022 = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeDto.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append(" AND ((c.worktypeAtr = 0 AND c.oneDayAtr = :oneDayAtr)");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND c.morningAtr = :morningAtr2 AND c.afternoonAtr IN :afternoon2 )");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND c.morningAtr IN :morningAtr3 AND c.afternoonAtr = :afternoon3 )");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND c.morningAtr = :morningAtr4 AND c.afternoonAtr IN :afternoon4 )");
		stringBuilder.append(" OR (c.worktypeAtr = 1 AND c.morningAtr IN :morningAtr5 AND c.afternoonAtr = :afternoon5 ))");
		stringBuilder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_HDSHIP_KAF022 = stringBuilder.toString();

		SELECT_BUSINESS_TRIP_KAF022 = new StringBuilder()
				.append("SELECT NEW " + WorkTypeDto.class.getName())
				.append("(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ")
				.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ")
				.append("ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ")
				.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ")
				.append(" AND c.worktypeAtr = 0 AND c.oneDayAtr IN :oneDayAtr ")
				.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC")
				.toString();
		
	}
	
	@Override
	public List<WorkTypeDto> findHdShipKaf022(String companyId, int oneDayAtr, int morningAtr2, List<Integer> afternoon2, List<Integer> morningAtr3, int afternoon3, int morningAtr4, List<Integer> afternoon4, List<Integer> morningAtr5, int afternoon5) {
		if(afternoon2.isEmpty() || morningAtr3.isEmpty() || afternoon4.isEmpty() || morningAtr5.isEmpty()){
			return new ArrayList<>();
		}
		List<WorkTypeDto> resultList = new ArrayList<>();
		CollectionUtil.split(afternoon2, SPLIT_500, subAfternoon2 -> {
			CollectionUtil.split(morningAtr3, SPLIT_500, subMorning3 -> {
				CollectionUtil.split(afternoon4, SPLIT_500, subAfternoon4 -> {
					CollectionUtil.split(morningAtr5, SPLIT_500, subMorning5 -> {
						resultList.addAll(this.queryProxy().query(SELECT_HDSHIP_KAF022, WorkTypeDto.class)
											.setParameter("companyId", companyId)
											.setParameter("oneDayAtr", oneDayAtr)
											.setParameter("morningAtr2", morningAtr2)
											.setParameter("afternoon2", subAfternoon2)
											.setParameter("morningAtr3", subMorning3)
											.setParameter("afternoon3", afternoon3)
											.setParameter("morningAtr4", morningAtr4)
											.setParameter("afternoon4", subAfternoon4)
											.setParameter("morningAtr5", subMorning5)
										    .setParameter("afternoon5", afternoon5)
											.getList());
					});
				});
			});
		});
		resultList.sort(Comparator.comparing(WorkTypeDto::getWorkTypeCode));
		return resultList;
	}
	
	@Override
	public List<WorkTypeDto> findHdTimeKaf022(String companyId) {
		return this.queryProxy().query(SELECT_HDTIME_KAF022, WorkTypeDto.class).setParameter("companyId", companyId)
				.getList();
	}
	
	@Override
	public List<WorkTypeDto> findBounceKaf022(String companyId, List<Integer> halfDay) {
		if(halfDay.isEmpty()){
			return Collections.emptyList();
		}else{
			List<WorkTypeDto> resultList = new ArrayList<>();
			CollectionUtil.split(halfDay, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				resultList.addAll(this.queryProxy().query(SELECT_BOUNCE_KAF022, WorkTypeDto.class)
						.setParameter("companyId", companyId)
						.setParameter("halfDay", subList)
						.getList());
			});
			resultList.sort(Comparator.comparing(WorkTypeDto::getWorkTypeCode));
			return resultList;
		}
	}
	
	@Override
	public List<WorkTypeDto> findWkChangeKaf022(String companyId) {
		return this.queryProxy().query(SELECT_ABWKCANGE_KAF022, WorkTypeDto.class).setParameter("companyId", companyId)
				.getList();
	}
	
	@Override
	public List<WorkTypeDto> findAbsenceKaf022(String companyId, Integer oneDayAtr, Integer morningAtr, Integer afternoonAtr) {
		return this.queryProxy().query(SELECT_ABSENCE_KAF022, WorkTypeDto.class).setParameter("companyId", companyId)
				.setParameter("oneDayAtr", oneDayAtr)
				.setParameter("morningAtr", morningAtr)
				.setParameter("afternoonAtr", afternoonAtr)
				.getList();
	}

	@Override
	public List<WorkTypeDto> findAbsenceSpecialKaf022(String companyId) {
		return this.queryProxy().query(SELECT_ABSENCE_SPECIAL_KAF022, WorkTypeDto.class)
				.setParameter("companyId", companyId)
				.getList();
	}

	@Override
	public List<WorkTypeDto> findOtKaf022(String companyId) {
		List<Integer> oneDayAtr = new ArrayList<>();
		List<Integer> halfDay = new ArrayList<>();
		oneDayAtr.add(0);
		oneDayAtr.add(11);
		oneDayAtr.add(7);
		oneDayAtr.add(10);
		halfDay.add(1);
		halfDay.add(7);
		halfDay.add(2);
		halfDay.add(0);
		halfDay.add(4);
		halfDay.add(5);
		halfDay.add(6);
		halfDay.add(9);
		return this.queryProxy().query(SELECT_OT_KAF022, WorkTypeDto.class).setParameter("companyId", companyId)
																			.setParameter("oneDayAtr", oneDayAtr)
																			.setParameter("halfDay", halfDay)
				.getList();
	}
	
	@Override
	public List<WorkTypeDto> findAllWorkType(String companyId) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE, WorkTypeDto.class).setParameter("companyId", companyId)
				.getList();
	}

	@Override
	public List<WorkTypeDto> findAllWorkType(String companyId, List<Integer> workTypeAtrList) {
		List<WorkTypeDto> resultList = new ArrayList<>();
		CollectionUtil.split(workTypeAtrList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_WORKTYPE_ATR, WorkTypeDto.class)
					.setParameter("companyId", companyId)
					.setParameter("workTypeAtr", subList)
					.getList());
		});
		resultList.sort((o1, o2) -> {
			Integer order1 = o1.getDispOrder();
			Integer order2 = o2.getDispOrder();
			if (order1 == null && order2 == null) return 0;
			if (order1 != null && order2 == null) return 1;
			if (order1 == null && order2 != null) return -1;
			return order1.compareTo(order2);
		});
		return resultList;
	}
	
	@Override
	public List<WorkTypeDto> findWorkType(String companyId, List<Integer> workTypeAtrList) {
		List<WorkTypeDto> listNew = new ArrayList<>();
		if(!workTypeAtrList.isEmpty()){
			for (int item: workTypeAtrList) {
				List<WorkTypeDto> typeDto = this.queryProxy().query(SELECT_WORKTYPE_KDW006G, KshmtWorkType.class).setParameter("companyId", companyId)
						.setParameter("workTypeAtr", item)
						.getList()
						.stream()
						.map(c -> {
							WorkTypeDto result = new WorkTypeDto(
									c.kshmtWorkTypePK.workTypeCode,
									c.name,
									c.abbreviationName,
									c.symbolicName,
									c.deprecateAtr,
									c.memo,
									c.worktypeAtr,
									c.oneDayAtr,
									c.morningAtr,
									c.afternoonAtr,
									c.calculatorMethod,
									0);
							List<WorkTypeSetDto> workTypeList = c
									.worktypeSetList
									.stream()
									.map(x -> new WorkTypeSetDto(
											x.kshmtWorkTypeSetPK.workTypeCode,
											x.kshmtWorkTypeSetPK.workAtr,
											x.digestPublicHd,
											x.hodidayAtr,
											x.countHoliday,
											x.closeAtr == null ? 0 : x.closeAtr,
											x.sumAbsenseNo,
											x.sumSpHolidayNo,
											x.timeLeaveWork,
											x.attendanceTime,
											x.genSubHoliday,
											x.dayNightTimeAsk
											))
									.collect(Collectors.toList());
							result.setWorkTypeSet(workTypeList);
							return result;
						})
						.collect(Collectors.toList());
				if(!typeDto.isEmpty()){
					listNew.addAll(typeDto);
				}
			}
		}
		return listNew;
	}

	@Override
	public List<WorkTypeDto> findAllWorkTypeSPE(String companyId, int abolishAtr, int oneDayAtr1, int oneDayAtr2) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE_SPE, WorkTypeDto.class).setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr)
				.setParameter("oneDayAtr1", oneDayAtr1)
				.setParameter("oneDayAtr2", oneDayAtr2)
				.setParameter("morningAtr1", oneDayAtr1)
				.setParameter("morningAtr2", oneDayAtr2)
				.setParameter("afternoonAtr1", oneDayAtr1)
				.setParameter("afternoonAtr2", oneDayAtr2).getList();
	}
	
	@Override
	public List<WorkTypeDto> findAllWorkTypeDisp(String companyId, int abolishAtr) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE_DISP, WorkTypeDto.class).setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr).getList();
	}

	@Override
	public List<WorkTypeDto> findBusinessTripKaf022(String companyId, List<Integer> listOneDayAtr) {
		return this.queryProxy().query(SELECT_BUSINESS_TRIP_KAF022, WorkTypeDto.class)
				.setParameter("companyId", companyId)
				.setParameter("oneDayAtr", listOneDayAtr)
				.getList();
	}

}
