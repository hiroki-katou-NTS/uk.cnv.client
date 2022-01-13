package nts.uk.ctx.at.shared.infra.repository.scherec.monthlyattendanceitem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.DisplayAndInputMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.InputControlMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthority;
import nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattendanceitem.KrcstDisplayAndInputMonthly;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaMonthlyItemControlByAuthRepository extends JpaRepository implements MonthlyItemControlByAuthRepository {

	private static final String SELECT_BY_AUTHORITY_MONTHLY_ID = "SELECT c FROM KrcstDisplayAndInputMonthly c"
			+ " WHERE c.krcstDisplayAndInputMonthlyPK.companyID = :companyID"
			+ " AND c.krcstDisplayAndInputMonthlyPK.authorityMonthlyID = :authorityMonthlyID"
			+ " ORDER BY c.krcstDisplayAndInputMonthlyPK.itemMonthlyID";

	private static final String SELECT_BY_KEY = "SELECT c FROM KrcstDisplayAndInputMonthly c"
			+ " WHERE c.krcstDisplayAndInputMonthlyPK.companyID = :companyID"
			+ " AND c.krcstDisplayAndInputMonthlyPK.authorityMonthlyID = :authorityMonthlyID" + " AND c.toUse = :toUse";

	private static final String SELECT_BY_KEY_ATT_ITEM_ID = SELECT_BY_KEY
			+ " AND c.krcstDisplayAndInputMonthlyPK.itemMonthlyID IN :itemMonthlyIDs";

	// kdw002 ver7
	private static final String FIND_BY_CID = "SELECT DISTINCT c.AUTHORITY_MON_ID" + " FROM KSHMT_MON_ITEM_DISP_CTR c"
			+ " WHERE c.CID = ? ";
	
	private static final String DELETE = "DELETE FROM KSHMT_MON_ITEM_DISP_CTR WHERE CID = ? AND AUTHORITY_MON_ID = ?";

//	private static final String SELECT_BY_AUTHORITY_MONTHLY_LIST_ID = "SELECT c FROM KrcstDisplayAndInputMonthly c"
//			+ " WHERE c.krcstDisplayAndInputMonthlyPK.companyID = :companyID"
//			+ " AND c.krcstDisplayAndInputMonthlyPK.authorityMonthlyID = :authorityMonthlyID"
//			+ " AND c.krcstDisplayAndInputMonthlyPK.itemMonthlyID  IN  :itemMonthlyIDs"
//			+ " AND c.toUse = :toUse "
//			+ " ORDER BY c.krcstDisplayAndInputMonthlyPK.itemMonthlyID";

//	
//	private final String SELECT_BY_AUTHORITY_MONTHLY_ID_AND_TO_USE = "SELECT c FROM KrcstDisplayAndInputMonthly c"
//			+ " WHERE c.krcstDisplayAndInputMonthlyPK.companyID = :companyID"
//			+ " AND c.krcstDisplayAndInputMonthlyPK.authorityMonthlyID = :authorityMonthlyID"
//			+ " AND c.krcstDisplayAndInputMonthlyPK.itemMonthlyID  = :itemMonthlyID "
//			+ " AND c.toUse = :toUse "
//			+ " ORDER BY c.krcstDisplayAndInputMonthlyPK.itemMonthlyID";

	
	// ver7 ドメインモデル「権限別月次項目制御」を取得する
	@Override
	public List<String> getMonthlytRolesByCid(String companyId) {
		
		List<String> listFullData = new ArrayList<String>();
		try (PreparedStatement statement = this.connection().prepareStatement(FIND_BY_CID)) {	
			statement.setString(1, companyId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String roleID = rs.getString("AUTHORITY_MON_ID");
				if(!roleID.isEmpty())
					listFullData.add(roleID);
			}
			if(listFullData.isEmpty())
				return Collections.emptyList();
			return listFullData;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<MonthlyItemControlByAuthority> getMonthlyAttdItem(String companyID, String authorityMonthlyId) {
		List<DisplayAndInputMonthly> data = this.queryProxy()
				.query(SELECT_BY_AUTHORITY_MONTHLY_ID, KrcstDisplayAndInputMonthly.class)
				.setParameter("companyID", companyID).setParameter("authorityMonthlyID", authorityMonthlyId)
				.getList(c -> c.toDomain());
		if (data.isEmpty())
			return Optional.empty();
		MonthlyItemControlByAuthority monthlyItemControlByAuthority = new MonthlyItemControlByAuthority(companyID,
				authorityMonthlyId, data);
		return Optional.of(monthlyItemControlByAuthority);
	}

	@Override
	public void updateMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority) {
		List<KrcstDisplayAndInputMonthly> newEntity = monthlyItemControlByAuthority.getListDisplayAndInputMonthly()
				.stream().map(c -> KrcstDisplayAndInputMonthly.toEntity(monthlyItemControlByAuthority.getCompanyId(),
						monthlyItemControlByAuthority.getAuthorityMonthlyId(), c))
				.collect(Collectors.toList());
		List<KrcstDisplayAndInputMonthly> updateEntity = this.queryProxy()
				.query(SELECT_BY_AUTHORITY_MONTHLY_ID, KrcstDisplayAndInputMonthly.class)
				.setParameter("companyID", monthlyItemControlByAuthority.getCompanyId())
				.setParameter("authorityMonthlyID", monthlyItemControlByAuthority.getAuthorityMonthlyId()).getList();

		for (int i = 0; i < updateEntity.size(); i++) {
			boolean checkExist = false;
			for (int j = 0; j < newEntity.size(); j++) {
				if (updateEntity.get(i).krcstDisplayAndInputMonthlyPK.itemMonthlyID == newEntity
						.get(j).krcstDisplayAndInputMonthlyPK.itemMonthlyID) {
					updateEntity.get(i).toUse = newEntity.get(j).toUse;
					if (newEntity.get(j).toUse == 1) {
						updateEntity.get(i).canBeChangedByOthers = newEntity.get(j).canBeChangedByOthers;
						updateEntity.get(i).youCanChangeIt = newEntity.get(j).youCanChangeIt;
					}
					this.commandProxy().update(updateEntity.get(i));
					checkExist = true;
					break;
				}
			}
			if (!checkExist) {
				this.commandProxy().remove(KrcstDisplayAndInputMonthly.class,
						updateEntity.get(i).krcstDisplayAndInputMonthlyPK);
			}
		}

		for (int i = 0; i < newEntity.size(); i++) {
			boolean checkExist = false;
			for (int j = 0; j < updateEntity.size(); j++) {
				if (newEntity.get(i).krcstDisplayAndInputMonthlyPK.itemMonthlyID == updateEntity
						.get(j).krcstDisplayAndInputMonthlyPK.itemMonthlyID) {
					checkExist = true;
					break;
				}
			}
			if (!checkExist) {
				this.commandProxy().insert(newEntity.get(i));
			}
		}
	}

	@Override
	public void addMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority) {
		List<KrcstDisplayAndInputMonthly> newEntity = monthlyItemControlByAuthority.getListDisplayAndInputMonthly()
				.stream().map(c -> KrcstDisplayAndInputMonthly.toEntity(monthlyItemControlByAuthority.getCompanyId(),
						monthlyItemControlByAuthority.getAuthorityMonthlyId(), c))
				.collect(Collectors.toList());
		for (KrcstDisplayAndInputMonthly krcstDisplayAndInputMonthly : newEntity) {
			this.commandProxy().insert(krcstDisplayAndInputMonthly);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<MonthlyItemControlByAuthority> getMonthlyAttdItemByUse(String companyID, String authorityMonthlyId,
			List<Integer> itemMonthlyIDs, int toUse) {
		List<DisplayAndInputMonthly> data = new ArrayList<>();
		CollectionUtil.split(itemMonthlyIDs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection()
					.prepareStatement("SELECT * from KSHMT_MON_ITEM_DISP_CTR h" + " WHERE h.CID = ?"
							+ " and h.AUTHORITY_MON_ID = ?" + " AND h.USE_ATR = ?" + " AND h.ITEM_MONTHLY_ID IN ("
							+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
				statement.setString(1, companyID);
				statement.setString(2, authorityMonthlyId);
				statement.setInt(3, toUse);
				for (int i = 0; i < subList.size(); i++) {
					statement.setInt(i + 4, subList.get(i));
				}

				data.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
					return new DisplayAndInputMonthly(rec.getInt("ITEM_MONTHLY_ID"),
							rec.getInt("USE_ATR") == 1 ? true : false,
							new InputControlMonthly(rec.getInt("CHANGED_BY_YOU") == 1 ? true : false,
									rec.getInt("CHANGED_BY_OTHERS") == 1 ? true : false));
				}));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		if (CollectionUtil.isEmpty(data))
			return Optional.empty();
		data.sort(Comparator.comparing(DisplayAndInputMonthly::getItemMonthlyId));

		return Optional.of(new MonthlyItemControlByAuthority(companyID, authorityMonthlyId, data));
	}

	private final String SELECT_ALL_BY_AUTHORITY_MONTHLY_LIST_ID = "SELECT c FROM KrcstDisplayAndInputMonthly c"
			+ " WHERE c.krcstDisplayAndInputMonthlyPK.companyID = :companyID"
			+ " AND c.krcstDisplayAndInputMonthlyPK.authorityMonthlyID = :authorityMonthlyID" + " AND c.toUse = :toUse "
			+ " ORDER BY c.krcstDisplayAndInputMonthlyPK.itemMonthlyID";

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<MonthlyItemControlByAuthority> getAllMonthlyAttdItemByUse(String companyID,
			String authorityMonthlyId, int toUse) {
		List<DisplayAndInputMonthly> data = this.queryProxy()
				.query(SELECT_ALL_BY_AUTHORITY_MONTHLY_LIST_ID, KrcstDisplayAndInputMonthly.class)
				.setParameter("companyID", companyID).setParameter("authorityMonthlyID", authorityMonthlyId)
				.setParameter("toUse", toUse).getList(c -> c.toDomain());

		if (CollectionUtil.isEmpty(data))
			return Optional.empty();
		MonthlyItemControlByAuthority monthlyItemControlByAuthority = new MonthlyItemControlByAuthority(companyID,
				authorityMonthlyId, data);
		return Optional.of(monthlyItemControlByAuthority);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<MonthlyItemControlByAuthority> getMonthlyAttdItemByAttItemId(String companyID,
			String authorityMonthlyId, List<Integer> attendanceItemIds) {
		List<DisplayAndInputMonthly> data = new ArrayList<>();
		if (attendanceItemIds == null || attendanceItemIds.isEmpty()) {
			data.addAll(this.queryProxy().query(SELECT_BY_KEY, KrcstDisplayAndInputMonthly.class)
					.setParameter("companyID", companyID).setParameter("authorityMonthlyID", authorityMonthlyId)
					.setParameter("toUse", NotUseAtr.USE.value).getList(c -> c.toDomain()));
		} else {
			CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				data.addAll(this.queryProxy().query(SELECT_BY_KEY_ATT_ITEM_ID, KrcstDisplayAndInputMonthly.class)
						.setParameter("companyID", companyID).setParameter("authorityMonthlyID", authorityMonthlyId)
						.setParameter("toUse", NotUseAtr.USE.value).setParameter("itemMonthlyIDs", subList)
						.getList(c -> c.toDomain()));
			});
		}
		if (data.isEmpty())
			return Optional.empty();
		MonthlyItemControlByAuthority monthlyAttendanceItemAuthority = new MonthlyItemControlByAuthority(companyID,
				authorityMonthlyId, data);
		return Optional.of(monthlyAttendanceItemAuthority);
	}

	@Override
	public void delete(String companyId, String roleId) {
		try (PreparedStatement statement = this.connection().prepareStatement(DELETE)) {	
			statement.setString(1, companyId);
			statement.setString(2, roleId);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<MonthlyItemControlByAuthority> getListMonthlyAttendanceItemAuthority(String companyId) {
		// TODO Auto-generated method stub
		return null;
	}

}
