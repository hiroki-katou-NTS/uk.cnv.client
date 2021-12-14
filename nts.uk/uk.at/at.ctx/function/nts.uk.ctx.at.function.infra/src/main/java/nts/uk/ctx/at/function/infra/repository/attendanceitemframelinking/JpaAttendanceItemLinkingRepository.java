/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.repository.attendanceitemframelinking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.AttendanceItemLinking;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.repository.AttendanceItemLinkingRepository;
import nts.uk.ctx.at.function.infra.entity.attendanceItemAndFrameLinking.KfnmtAttendanceLink;

@Stateless
public class JpaAttendanceItemLinkingRepository extends JpaRepository implements AttendanceItemLinkingRepository {
	private static final String FIND;
	private static final String FIND_BY_ANY_ITEM;
	private static final String FIND_BY_ITEM_ID_AND_TYPE;

	private static final String FIND_BY_FRAME_NO = "SELECT a FROM KfnmtAttendanceLink a "
			+ "WHERE a.kfnmtAttendanceLinkPK.frameNo IN :frameNos "
			+ "AND a.kfnmtAttendanceLinkPK.typeOfItem = :typeOfItem "
			+ "AND a.kfnmtAttendanceLinkPK.frameCategory = :frameCategory";
	
	private static final String FIND_BY_FRAME_NO_FRAME_CATEGORY = "SELECT a FROM KfnmtAttendanceLink a "
			+ "WHERE a.kfnmtAttendanceLinkPK.frameNo IN :frameNos "
			+ "AND a.kfnmtAttendanceLinkPK.typeOfItem = :typeOfItem "
			+ "AND a.kfnmtAttendanceLinkPK.frameCategory IN :frameCategories";
	
	private static final String FIND_BY_FRAME_NO_FRAME_CATEGORY_AND_PRELIMINARY_FRAME_NO = "SELECT a FROM KfnmtAttendanceLink a "
			+ "WHERE a.kfnmtAttendanceLinkPK.frameNo IN :frameNos "
			+ "AND a.kfnmtAttendanceLinkPK.typeOfItem = :typeOfItem "
			+ "AND a.kfnmtAttendanceLinkPK.frameCategory IN :frameCategories "
			+ "AND a.preliminaryFrameNO IN :preliminaryFrameNO";

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAttendanceLink a ");
		builderString.append("WHERE a.kfnmtAttendanceLinkPK.attendanceItemId IN :attendanceItemIds ");
		FIND = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAttendanceLink a ");
		builderString.append("WHERE a.kfnmtAttendanceLinkPK.typeOfItem = :typeOfItem ");
		builderString.append("AND a.kfnmtAttendanceLinkPK.frameCategory = 8"); //任意項目
		FIND_BY_ANY_ITEM = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAttendanceLink a ");
		builderString.append("WHERE a.kfnmtAttendanceLinkPK.attendanceItemId IN :attendanceItemIds ");
		builderString.append("AND a.kfnmtAttendanceLinkPK.typeOfItem = :typeOfItem ");
		FIND_BY_ITEM_ID_AND_TYPE = builderString.toString();
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttendanceItemLinking> getByAttendanceId(List<Integer> attendanceItemIds) {
		if(attendanceItemIds.isEmpty())
			return Collections.emptyList();
		List<KfnmtAttendanceLink> results  = new ArrayList<>();
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(FIND, KfnmtAttendanceLink.class)
					.setParameter("attendanceItemIds", subList).getList());
		});
		return results.stream().map(f -> toDomain(f)).collect(Collectors.toList());
	}

	private static AttendanceItemLinking toDomain(KfnmtAttendanceLink kfnmtAttendanceLink) {
		AttendanceItemLinking attendanceItemLinking = AttendanceItemLinking.createFromJavaType(
				kfnmtAttendanceLink.kfnmtAttendanceLinkPK.attendanceItemId,
				kfnmtAttendanceLink.kfnmtAttendanceLinkPK.frameNo.intValue(),
				kfnmtAttendanceLink.kfnmtAttendanceLinkPK.typeOfItem.intValue(),
				kfnmtAttendanceLink.kfnmtAttendanceLinkPK.frameCategory.intValue());
		return attendanceItemLinking;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendanceItemAndFrameLinking.repository.
	 * AttendanceItemLinkingRepository#getByAnyItemCategory(nts.uk.ctx.at.
	 * function.dom.attendanceItemAndFrameLinking.enums.TypeOfItem)
	 */
	@Override
	public List<AttendanceItemLinking> getByAnyItemCategory(TypeOfItem type) {
		return this.queryProxy().query(FIND_BY_ANY_ITEM, KfnmtAttendanceLink.class)
				.setParameter("typeOfItem", type.value).getList(f -> toDomain(f));
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttendanceItemLinking> getByAttendanceIdAndType(List<Integer> attendanceItemIds, TypeOfItem type) {
		if(attendanceItemIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<KfnmtAttendanceLink> results  = new ArrayList<>();
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(FIND_BY_ITEM_ID_AND_TYPE, KfnmtAttendanceLink.class)
					.setParameter("attendanceItemIds", subList)
					.setParameter("typeOfItem", type.value).getList());
		});
		return results.stream().map(f -> toDomain(f)).collect(Collectors.toList());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttendanceItemLinking> getFullDataByListAttdaId(List<Integer> attendanceItemIds) {
		if(attendanceItemIds.isEmpty())
			return Collections.emptyList();
		List<KfnmtAttendanceLink> results  = new ArrayList<>();
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(FIND, KfnmtAttendanceLink.class)
					.setParameter("attendanceItemIds", subList).getList());
		});
		return results.stream().map(f -> f.toDomain()).collect(Collectors.toList());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttendanceItemLinking> getFullDataByAttdIdAndType(List<Integer> attendanceItemIds, TypeOfItem type) {
		if(attendanceItemIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<KfnmtAttendanceLink> results  = new ArrayList<>();
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(FIND_BY_ITEM_ID_AND_TYPE, KfnmtAttendanceLink.class)
					.setParameter("attendanceItemIds", subList)
					.setParameter("typeOfItem", type.value).getList());
		});
		return results.stream().map(f -> f.toDomain()).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.function.dom.attendanceitemframelinking.repository.
	 * AttendanceItemLinkingRepository#findByFrameNos(java.util.List)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttendanceItemLinking> findByFrameNos(List<Integer> frameNos, int typeOfItem, int frameCategory) {
		if (CollectionUtil.isEmpty(frameNos)) {
			return Collections.emptyList();
		}

		BigDecimal bTypeOfItem = BigDecimal.valueOf(typeOfItem);
		BigDecimal bFrameCategory = BigDecimal.valueOf(frameCategory);
		List<AttendanceItemLinking> resultList = new ArrayList<>();
		CollectionUtil.split(
				frameNos.stream().map(BigDecimal::valueOf).collect(Collectors.toList()), 
				DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, 
				subList -> {
					resultList.addAll(this.queryProxy().query(FIND_BY_FRAME_NO, KfnmtAttendanceLink.class)
							.setParameter("frameNos", subList)
							.setParameter("typeOfItem", bTypeOfItem)
							.setParameter("frameCategory", bFrameCategory)
							.getList(KfnmtAttendanceLink::toDomain));
		});
		
		return resultList;
	}

	@Override
	public List<AttendanceItemLinking> findByFrameNoTypeAndFramCategory(List<BigDecimal> frameNos, int typeOfItem,
			List<Integer> frameCategory) {
		if (CollectionUtil.isEmpty(frameNos)) {
			return Collections.emptyList();
		}

		BigDecimal bTypeOfItem = BigDecimal.valueOf(typeOfItem);
		List<BigDecimal> categories = frameCategory.stream().map(BigDecimal::valueOf).collect(Collectors.toList());
		List<AttendanceItemLinking> resultList = new ArrayList<>();
		CollectionUtil.split(frameNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			CollectionUtil.split(categories, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subFrameCategories -> {
				resultList.addAll(this.queryProxy().query(FIND_BY_FRAME_NO_FRAME_CATEGORY, KfnmtAttendanceLink.class)
						.setParameter("frameNos", subList)
						.setParameter("typeOfItem", bTypeOfItem)
						.setParameter("frameCategories", subFrameCategories)
						.getList(KfnmtAttendanceLink::toDomain));
			});
		});
		
		return resultList;
	}

	@Override
	public List<AttendanceItemLinking> findByFrameNoTypeAndFramCategoryAndBreakdownItemNo(List<BigDecimal> frameNos,
			int typeOfItem, List<Integer> frameCategory, List<Integer> breakdownItemNo) {

		if (CollectionUtil.isEmpty(frameNos)) {
			return Collections.emptyList();
		}

		BigDecimal bTypeOfItem = BigDecimal.valueOf(typeOfItem);
		List<BigDecimal> categories = frameCategory.stream().map(BigDecimal::valueOf).collect(Collectors.toList());
		List<AttendanceItemLinking> resultList = new ArrayList<>();
		CollectionUtil.split(frameNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			CollectionUtil.split(categories, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subFrameCategories -> {
				if (breakdownItemNo.isEmpty()) {
					resultList.addAll(this.queryProxy().query(FIND_BY_FRAME_NO_FRAME_CATEGORY, KfnmtAttendanceLink.class)
							.setParameter("frameNos", subList)
							.setParameter("typeOfItem", bTypeOfItem)
							.setParameter("frameCategories", subFrameCategories)
							.getList(KfnmtAttendanceLink::toDomain));
				} else {
					CollectionUtil.split(breakdownItemNo
							, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT
							, subBreakdownItemNo -> {
								resultList.addAll(this.queryProxy()
										.query(FIND_BY_FRAME_NO_FRAME_CATEGORY_AND_PRELIMINARY_FRAME_NO,
												KfnmtAttendanceLink.class)
										.setParameter("frameNos", subList)
										.setParameter("typeOfItem", bTypeOfItem)
										.setParameter("frameCategories", subFrameCategories)
										.setParameter("preliminaryFrameNO", breakdownItemNo)
										.getList(KfnmtAttendanceLink::toDomain));
							});
				}
			});
		});
		
		return resultList;
	}

}
