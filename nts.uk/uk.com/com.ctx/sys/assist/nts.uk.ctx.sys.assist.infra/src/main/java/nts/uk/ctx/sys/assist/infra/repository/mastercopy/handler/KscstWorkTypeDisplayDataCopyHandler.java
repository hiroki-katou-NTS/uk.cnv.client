package nts.uk.ctx.sys.assist.infra.repository.mastercopy.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.assist.dom.mastercopy.CopyMethod;
import nts.uk.ctx.sys.assist.dom.mastercopy.handler.DataCopyHandler;
import nts.uk.shr.com.context.AppContexts;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * The Class KscstWorkTypeDisplayDataCopyHandler.
 */
@Getter
@Setter
@NoArgsConstructor
public class KscstWorkTypeDisplayDataCopyHandler extends DataCopyHandler {

	/** The insert query. */
	private final String INSERT_QUERY = "INSERT INTO KSCST_WORKTYPE_DISPLAY(CID, USE_ATR) VALUES (?,?)";

	/** The select by cid query. */
	private final String SELECT_BY_CID_QUERY = "SELECT CID, USE_ATR FROM KSCST_WORKTYPE_DISPLAY WHERE CID = ?";

	/** The delete by cid query. */
	private final String DELETE_BY_CID_QUERY = "DELETE FROM KSCST_WORKTYPE_DISPLAY WHERE CID = ?";

	/** The paramater quantity. */
	private final int PARAMATER_QUANTITY = 2;

	/**
	 * Instantiates a new kscst work type display data copy handler.
	 *
	 * @param entityManager
	 *            the entity manager
	 * @param copyMethod
	 *            the copy method
	 * @param companyId
	 *            the company id
	 */
	public KscstWorkTypeDisplayDataCopyHandler(EntityManager entityManager, CopyMethod copyMethod, String companyId) {
		this.entityManager = entityManager;
		this.copyMethod = copyMethod;
		this.companyId = companyId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doCopy() {
		/// Get all company zero data
		Query selectQuery = this.entityManager.createNativeQuery(SELECT_BY_CID_QUERY).setParameter(1,
				AppContexts.user().zeroCompanyIdInContract());
		List<Object> zeroCompanyDatas = selectQuery.getResultList();
		if (zeroCompanyDatas.isEmpty())
			return;
		switch (copyMethod) {
		case REPLACE_ALL:
			Query deleteQuery = this.entityManager.createNativeQuery(DELETE_BY_CID_QUERY).setParameter(1,
					this.companyId);
			deleteQuery.executeUpdate();
		case ADD_NEW:
			//query old data
			Query selectQueryOldData = this.entityManager.createNativeQuery(SELECT_BY_CID_QUERY).setParameter(1,
					this.companyId);
			List<Object> oldDatas = selectQueryOldData.getResultList();
			if(!oldDatas.isEmpty()) return;
			//copy data
			String insertQueryStr = StringUtils.repeat(INSERT_QUERY, zeroCompanyDatas.size());
			Query insertQuery = this.entityManager.createNativeQuery(insertQueryStr);
			for (int i = 0, j = zeroCompanyDatas.size(); i < j; i++) {
				Object[] dataArr = (Object[]) zeroCompanyDatas.get(i);
				insertQuery.setParameter(i * PARAMATER_QUANTITY + 1, this.companyId);
				insertQuery.setParameter(i * PARAMATER_QUANTITY + 2, dataArr[1]);
			}
			// Run insert query
			if(!StringUtils.isEmpty(insertQueryStr)) insertQuery.executeUpdate();
		case DO_NOTHING:
			// Do nothing
		default:
			break;
		}

	}
}