package nts.uk.ctx.at.shared.infra.repository.scherec.dailyattendanceitem;

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
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DisplayAndInputControl;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttdItemAuthRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshstDailyServiceTypeControl;
//import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshstDailyServiceTypeControlPK;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaDailyAttdItemAuthRepository extends JpaRepository implements DailyAttdItemAuthRepository {

	private static final String SELECT_BY_AUTHORITY_DAILY_ID = "SELECT c FROM KshstDailyServiceTypeControl c"
			+ " WHERE c.kshstDailyServiceTypeControlPK.companyID = :companyID"
			+ " AND c.kshstDailyServiceTypeControlPK.authorityDailyID = :authorityDailyID"
			+ " ORDER BY c.kshstDailyServiceTypeControlPK.itemDailyID";

	private static final String SELECT_BY_KEY = "SELECT c FROM KshstDailyServiceTypeControl c"
			+ " WHERE c.kshstDailyServiceTypeControlPK.companyID = :companyID"
			+ " AND c.kshstDailyServiceTypeControlPK.authorityDailyID = :authorityDailyID"
			+ " AND c.toUse = :toUse";

	private static final String SELECT_BY_KEY_ATT_ITEM_ID = SELECT_BY_KEY
			+ " AND c.kshstDailyServiceTypeControlPK.itemDailyID IN :itemDailyIDs";
	
	// kdw002 ver7
	private static final String FIND_BY_CID = "SELECT DISTINCT c.AUTHORITY_DAILY_ID" + " FROM KSHMT_DAY_ITEM_DISP_CTR c"
			+ " WHERE c.CID = ? ";
	
	private static final String DELETE = "DELETE FROM KSHMT_DAY_ITEM_DISP_CTR WHERE CID = ? AND AUTHORITY_DAILY_ID = ?";

	@Override
	public Optional<DailyAttendanceItemAuthority> getDailyAttdItem(String companyID, String authorityDailyId) {
		List<DisplayAndInputControl> data = this.queryProxy()
				.query(SELECT_BY_AUTHORITY_DAILY_ID, KshstDailyServiceTypeControl.class)
				.setParameter("companyID", companyID).setParameter("authorityDailyID", authorityDailyId)
				.getList(c -> c.toDomain());
		if (data.isEmpty())
			return Optional.empty();
		DailyAttendanceItemAuthority dailyAttendanceItemAuthority = new DailyAttendanceItemAuthority(companyID,
				authorityDailyId, data);
		return Optional.of(dailyAttendanceItemAuthority);
	}

	@Override
	public void updateDailyAttdItemAuth(DailyAttendanceItemAuthority dailyAttendanceItemAuthority) {
		List<KshstDailyServiceTypeControl> newEntity = dailyAttendanceItemAuthority.getListDisplayAndInputControl()
				.stream().map(c -> KshstDailyServiceTypeControl.toEntity(dailyAttendanceItemAuthority.getCompanyID(),
						dailyAttendanceItemAuthority.getAuthorityDailyId(), c))
				.collect(Collectors.toList());
		List<KshstDailyServiceTypeControl> updateEntity = this.queryProxy()
				.query(SELECT_BY_AUTHORITY_DAILY_ID, KshstDailyServiceTypeControl.class)
				.setParameter("companyID", dailyAttendanceItemAuthority.getCompanyID())
				.setParameter("authorityDailyID", dailyAttendanceItemAuthority.getAuthorityDailyId()).getList();
		 //int minCount = Math.min(updateEntity.size(), newEntity.size());
		
		//update item có và xóa item k có
		for (int i = 0; i < updateEntity.size(); i++) {
			boolean checkExist = false;
			for(int j = 0; j < newEntity.size(); j++) {
				if(updateEntity.get(i).kshstDailyServiceTypeControlPK.itemDailyID == newEntity.get(j).kshstDailyServiceTypeControlPK.itemDailyID ) {
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
			if(!checkExist) {
				this.commandProxy().remove(KshstDailyServiceTypeControl.class,updateEntity.get(i).kshstDailyServiceTypeControlPK);
				
			}
			
		}
		
		//add item có
		for (int i = 0; i < newEntity.size(); i++) {
			boolean checkExist = false;
			for(int j = 0; j < updateEntity.size(); j++) {
				if(newEntity.get(i).kshstDailyServiceTypeControlPK.itemDailyID == updateEntity.get(j).kshstDailyServiceTypeControlPK.itemDailyID ) {
					checkExist = true;
					break;
				}
			}
			if(!checkExist) {
				this.commandProxy().insert(newEntity.get(i));
			}
			
		}
	}

	@Override
	public void addDailyAttdItemAuth(DailyAttendanceItemAuthority dailyAttendanceItemAuthority) {
		List<KshstDailyServiceTypeControl> newEntity = dailyAttendanceItemAuthority.getListDisplayAndInputControl()
				.stream().map(c -> KshstDailyServiceTypeControl.toEntity(dailyAttendanceItemAuthority.getCompanyID(),
						dailyAttendanceItemAuthority.getAuthorityDailyId(), c))
				.collect(Collectors.toList());
		for (KshstDailyServiceTypeControl dailyServiceTypeControl : newEntity) {
			this.commandProxy().insert(dailyServiceTypeControl);
		}

	}
	//HoiDD
	private final String SELECT_ALL_BY_AUTHORITY_DAILY_LIST_ID = "SELECT c FROM KshstDailyServiceTypeControl c"
			+ " WHERE c.kshstDailyServiceTypeControlPK.companyID = :companyID"
			+ " AND c.kshstDailyServiceTypeControlPK.authorityDailyID = :authorityDailyID"
			+ " AND c.toUse = :toUse "
			+ " ORDER BY c.kshstDailyServiceTypeControlPK.itemDailyID";
	
	private final String SELECT_BY_AUTHORITY_DAILY_LIST_ID = "SELECT c FROM KshstDailyServiceTypeControl c"
			+ " WHERE c.kshstDailyServiceTypeControlPK.companyID = :companyID"
			+ " AND c.kshstDailyServiceTypeControlPK.authorityDailyID = :authorityDailyID"
			+ " AND c.kshstDailyServiceTypeControlPK.itemDailyID  IN  :itemDailyIDs"
			+ " AND c.toUse = :toUse "
			+ " ORDER BY c.kshstDailyServiceTypeControlPK.itemDailyID";
	
	@Override
	public Optional<DailyAttendanceItemAuthority> getDailyAttdItemByUse(String companyId,
			String roleId,List<Integer> attendanceItemIds,int toUse) {
		List<DisplayAndInputControl> data = new  ArrayList<>();
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			data.addAll(
					this.queryProxy().query(SELECT_BY_AUTHORITY_DAILY_LIST_ID,KshstDailyServiceTypeControl.class)
					.setParameter("companyID", companyId)
					.setParameter("authorityDailyID", roleId)
					.setParameter("itemDailyIDs", attendanceItemIds)
					.setParameter("toUse", toUse)
					.getList(c->c.toDomain()));
			
		});
		if(CollectionUtil.isEmpty(data))
			return Optional.empty();
		data.sort(Comparator.comparing(DisplayAndInputControl::getItemDailyID));
		DailyAttendanceItemAuthority dailyItemControlByAuthority = new DailyAttendanceItemAuthority(
				companyId,roleId,data
				);
		return Optional.of(dailyItemControlByAuthority);
	}

	@Override
	public Optional<DailyAttendanceItemAuthority> getAllDailyAttdItemByUse(String companyId, String roleId, int toUse) {
		List<DisplayAndInputControl> data = this.queryProxy()
				.query(SELECT_ALL_BY_AUTHORITY_DAILY_LIST_ID, KshstDailyServiceTypeControl.class)
				.setParameter("companyID", companyId).setParameter("authorityDailyID", roleId)
				.setParameter("toUse", toUse).getList(c -> c.toDomain());

		if (CollectionUtil.isEmpty(data))
			return Optional.empty();
		DailyAttendanceItemAuthority monthlyItemControlByAuthority = new DailyAttendanceItemAuthority(companyId, roleId,
				data);
		return Optional.of(monthlyItemControlByAuthority);
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<DailyAttendanceItemAuthority> getDailyAttdItemByAttItemId(String companyID, String authorityDailyId,
			List<Integer> attendanceItemIds) {
		List<DisplayAndInputControl> data = new ArrayList<>();
		if (attendanceItemIds == null || attendanceItemIds.isEmpty()) {
			data.addAll(this.queryProxy().query(SELECT_BY_KEY, KshstDailyServiceTypeControl.class)
					.setParameter("companyID", companyID).setParameter("authorityDailyID", authorityDailyId)
					.setParameter("toUse", NotUseAtr.USE.value)
					.getList(c -> c.toDomain()));
		} else {
			CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				data.addAll(this.queryProxy().query(SELECT_BY_KEY_ATT_ITEM_ID, KshstDailyServiceTypeControl.class)
					.setParameter("companyID", companyID)
					.setParameter("authorityDailyID", authorityDailyId)
					.setParameter("toUse", NotUseAtr.USE.value)
					.setParameter("itemDailyIDs", subList)
					.getList(c -> c.toDomain()));
			});
		}
		if (data.isEmpty())
			return Optional.empty();
		DailyAttendanceItemAuthority dailyAttendanceItemAuthority = new DailyAttendanceItemAuthority(companyID,
				authorityDailyId, data);
		return Optional.of(dailyAttendanceItemAuthority);
	}

	@Override
	public List<String> getDailytRolesByCid(String companyId) {
		List<String> listFullData = new ArrayList<String>();
		try (PreparedStatement statement = this.connection().prepareStatement(FIND_BY_CID)) {	
			statement.setString(1, companyId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String roleID = rs.getString("AUTHORITY_DAILY_ID");
				if(!roleID.isEmpty())
					listFullData.add(roleID);
			}
			if(listFullData.isEmpty())
				return  Collections.emptyList();
			return listFullData;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String companyID, String roleID) {
		try (PreparedStatement statement = this.connection().prepareStatement(DELETE)) {	
			statement.setString(1, companyID);
			statement.setString(2, roleID);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
