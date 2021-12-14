/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.scherec.dailyattendanceitem;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.query.TypedQueryWrapper;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KrcmtDailyAttendanceItem;

@Stateless
public class JpaDailyAttendanceItemRepository extends JpaRepository implements DailyAttendanceItemRepository {

	private static final String FIND;
	private static final String FIND_ALL;

	private static final String FIND_SINGLE;

	private static final String FIND_BY_ID;

	private static final String FIND_BY_ATR;
	
	private static final String FIND_BY_ATRS;

	private static final String FIND_BY_ATTENDANCE_IDS;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		FIND = builderString.toString();
		
		
		StringBuilder b = new StringBuilder();
		b.append("SELECT a ");
		b.append("FROM KrcmtDailyAttendanceItem a ");
		b.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		FIND_ALL = b.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("AND  a.krcmtDailyAttendanceItemPK.attendanceItemId = :attendanceItemId ");
		FIND_SINGLE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("AND a.krcmtDailyAttendanceItemPK.attendanceItemId IN :dailyAttendanceItemIds ");
		FIND_BY_ID = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("AND a.dailyAttendanceAtr = :dailyAttendanceAtr ");
		builderString.append("ORDER BY a.displayNumber ASC");
		FIND_BY_ATR = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("AND a.dailyAttendanceAtr IN :dailyAttendanceAtrs ");
		FIND_BY_ATRS = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtDailyAttendanceItem a ");
		builderString.append("WHERE a.krcmtDailyAttendanceItemPK.attendanceItemId IN :dailyAttendanceItemIds ");
		builderString.append("AND a.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("ORDER BY a.displayNumber ASC ");
		FIND_BY_ATTENDANCE_IDS = builderString.toString();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> getListTobeUsed(String companyId, int userCanUpdateAtr) {
		return this.queryProxy().query(FIND, KrcmtDailyAttendanceItem.class).setParameter("companyId", companyId)
				.getList(f -> toDomain(f));
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> getListById(String companyId, List<Integer> dailyAttendanceItemIds) {
		if(dailyAttendanceItemIds.isEmpty())
			return Collections.emptyList();
		List<DailyAttendanceItem> resultList = new ArrayList<>();
		CollectionUtil.split(dailyAttendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_ID, KrcmtDailyAttendanceItem.class)
								.setParameter("companyId", companyId)
								.setParameter("dailyAttendanceItemIds", subList)
								.getList(f -> toDomain(f)));
		});
		return resultList;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> getList(String companyId) {
		return this.queryProxy().query(FIND_ALL, KrcmtDailyAttendanceItem.class).setParameter("companyId", companyId)
				.getList(f -> toDomain(f));
	}

	private static DailyAttendanceItem toDomain(KrcmtDailyAttendanceItem krcmtDailyAttendanceItem) {
		DailyAttendanceItem dailyAttendanceItem = DailyAttendanceItem.createFromJavaType(
				krcmtDailyAttendanceItem.krcmtDailyAttendanceItemPK.companyId,
				krcmtDailyAttendanceItem.krcmtDailyAttendanceItemPK.attendanceItemId,
				krcmtDailyAttendanceItem.attendanceItemName,
				krcmtDailyAttendanceItem.displayNumber,
				krcmtDailyAttendanceItem.userCanSet,
				krcmtDailyAttendanceItem.dailyAttendanceAtr,
				krcmtDailyAttendanceItem.nameLineFeedPosition,
				krcmtDailyAttendanceItem.typeOfMaster,
				krcmtDailyAttendanceItem.primitiveValue);
		return dailyAttendanceItem;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<DailyAttendanceItem> getDailyAttendanceItem(String companyId, int attendanceItemId) {
		return this.queryProxy().query(FIND_SINGLE, KrcmtDailyAttendanceItem.class).setParameter("companyId", companyId)
				.setParameter("attendanceItemId", attendanceItemId).getSingle(f -> toDomain(f));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.dailyattendanceitem.repository.
	 * DailyAttendanceItemRepository#findByAtr(java.lang.String, int)
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> findByAtr(String companyId, DailyAttendanceAtr itemAtr) {
		return this.queryProxy().query(FIND_BY_ATR, KrcmtDailyAttendanceItem.class).setParameter("companyId", companyId)
				.setParameter("dailyAttendanceAtr", itemAtr.value).getList(f -> toDomain(f));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.dailyattendanceitem.repository.
	 * DailyAttendanceItemRepository#findByAtr(java.lang.String, List<Integer>)
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> findByAtr(String companyId, List<Integer> dailyAttendanceAtrs) {
		List<DailyAttendanceItem> resultList = new ArrayList<>();
		CollectionUtil.split(dailyAttendanceAtrs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_ATRS, KrcmtDailyAttendanceItem.class)
					.setParameter("companyId", companyId)
					.setParameter("dailyAttendanceAtrs", subList)
					.getList(f -> toDomain(f)));
		});
		return resultList;
	}

	/**
	 * add by HoiDD
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DailyAttendanceItem> findByAttendanceItemIdAndAtr(String companyId, List<Integer> attendanceItemIds,
			List<Integer> dailyAttendanceAtr) {
		
		List<Integer> listAttIds = new ArrayList<>(Optional.ofNullable(attendanceItemIds).orElse(Collections.emptyList()));
		List<Integer> listAttAtrs = new ArrayList<>(Optional.ofNullable(dailyAttendanceAtr).orElse(Collections.emptyList()));
		
		boolean checkAttId = !CollectionUtil.isEmpty(listAttIds);
		boolean checkAttAtr = !CollectionUtil.isEmpty(listAttAtrs);
		if (!checkAttId) listAttIds.add(0);
		if (!checkAttAtr) listAttAtrs.add(0);
		
		StringBuilder builderString = new StringBuilder();	
		builderString.append("SELECT b");
		builderString.append(" FROM KrcmtDailyAttendanceItem b");
		builderString.append(" WHERE");
		builderString.append(" b.krcmtDailyAttendanceItemPK.companyId = :companyId");
		if (checkAttId) {
			builderString.append(" AND b.krcmtDailyAttendanceItemPK.attendanceItemId IN :attendanceItemIds");
		}
		if (checkAttAtr) {
			builderString.append(" AND b.dailyAttendanceAtr IN :itemAtrs");
		}
		List<DailyAttendanceItem> resultList = new ArrayList<>();
		CollectionUtil.split(listAttIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstIds -> {
			CollectionUtil.split(listAttAtrs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstAtrs -> {
				TypedQueryWrapper<KrcmtDailyAttendanceItem> query = this.queryProxy().query(builderString.toString(), KrcmtDailyAttendanceItem.class);
				query.setParameter("companyId", companyId);
				if (checkAttId) {
					query.setParameter("attendanceItemIds", lstIds);
				}
				if (checkAttAtr) {
					query.setParameter("itemAtrs", lstAtrs);
				}
				resultList.addAll(query.getList(f -> toDomain(f)));
			});
		});
		return resultList;
	}

	@Override
	public void update(DailyAttendanceItem domain) {
		this.commandProxy().update(KrcmtDailyAttendanceItem.toEntity(domain));
	}

	@Override
	public List<DailyAttendanceItem> findByADailyAttendanceItems(List<Integer> attendanceItemIds, String companyId) {
		List<DailyAttendanceItem> result = new ArrayList<>();
		if (!attendanceItemIds.isEmpty()) {
			CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				result.addAll(this.queryProxy().query(FIND_BY_ATTENDANCE_IDS, KrcmtDailyAttendanceItem.class)
						.setParameter("dailyAttendanceItemIds", subList)
						.setParameter("companyId", companyId)
						.getList(f -> toDomain(f)));
			});
		}
		return result;
	}
}
