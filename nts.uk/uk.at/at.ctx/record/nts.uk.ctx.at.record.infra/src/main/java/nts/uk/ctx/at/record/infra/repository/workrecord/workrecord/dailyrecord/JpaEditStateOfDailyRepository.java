package nts.uk.ctx.at.record.infra.repository.workrecord.workrecord.dailyrecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.dailyrecord.EditStateOfDailyRepository;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaEditStateOfDailyRepository extends JpaRepository implements EditStateOfDailyRepository {

	@Override
	public void deleteByListItemId(String sId, GeneralDate ymd, List<Integer> itemIdList) {
		CollectionUtil.split(itemIdList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String listItemIdString = "(";
			for (int i = 0; i < subList.size(); i++) {
				listItemIdString += "'" + subList.get(i) + "',";
			}
			
			listItemIdString = listItemIdString.substring(0, listItemIdString.length() - 1) + ")";

			Connection con = this.getEntityManager().unwrap(Connection.class);
			String sqlQuery = "DELETE FROM KRCDT_DAY_EDIT_STATE WHERE SID = " + "'" + sId + "'" + " AND YMD = "
					+ "'" + ymd + "'" + " AND ATTENDANCE_ITEM_ID IN " + listItemIdString;
			try {
				con.createStatement().executeUpdate(sqlQuery);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
