package nts.uk.ctx.sys.assist.infra.repository.mastercopy.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.assist.dom.mastercopy.CopyMethod;
import nts.uk.ctx.sys.assist.dom.mastercopy.handler.DataCopyHandler;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class KscmtSchePerInfoAtrDataCopyHandler.
 */
@Getter
@Setter
@NoArgsConstructor
public class KscmtSchePerInfoAtrDataCopyHandler extends DataCopyHandler {

	/** The insert query. */
	private final String INSERT_QUERY = "INSERT INTO KSCMT_SCHE_PER_INFO_ATR(CID, PERSON_INFO_ATR) VALUES (?,?)";

	/** The select by cid query. */
	private final String SELECT_BY_CID_QUERY = "SELECT CID, PERSON_INFO_ATR FROM KSCMT_SCHE_PER_INFO_ATR WHERE CID = ?";

	/** The delete by cid query. */
	private final String DELETE_BY_CID_QUERY = "DELETE FROM KSCMT_SCHE_PER_INFO_ATR WHERE CID = ?";

	/** The paramater quantity. */
	private final int PARAMATER_QUANTITY = 2;

	/**
	 * Instantiates a new kscmt sche per info atr data copy handler.
	 *
	 * @param entityManager
	 *            the entity manager
	 * @param copyMethod
	 *            the copy method
	 * @param companyId
	 *            the company id
	 */
	public KscmtSchePerInfoAtrDataCopyHandler(EntityManager entityManager, CopyMethod copyMethod, String companyId) {
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
				if (copyMethod == CopyMethod.ADD_NEW) {
					// get old data target by cid
					Query selectQueryTarget = this.entityManager.createNativeQuery(SELECT_BY_CID_QUERY).setParameter(1,
							this.companyId);
					List<Object> oldDatas = selectQueryTarget.getResultList();
					// ignore data existed
					for (int i = 0; i < zeroCompanyDatas.size(); i++) {
						Object[] dataAttr = (Object[]) zeroCompanyDatas.get(i);
						for (int j = 0; j < oldDatas.size(); j++) {
							Object[] targetAttr = (Object[]) oldDatas.get(j);
							// compare keys and remove
							if (dataAttr[1] == targetAttr[1]) {
								zeroCompanyDatas.remove(i);
								i -= 1;
								break;
							}
						}
					}
				}
				//copy data
				String insertQueryStr = StringUtils.repeat(INSERT_QUERY, zeroCompanyDatas.size());
				Query insertQuery = this.entityManager.createNativeQuery(insertQueryStr);
				for (int i = 0, j = zeroCompanyDatas.size(); i < j; i++) {
					Object[] dataArr = (Object[]) zeroCompanyDatas.get(i);
					insertQuery.setParameter(i * PARAMATER_QUANTITY + 1, this.companyId);
					insertQuery.setParameter(i * PARAMATER_QUANTITY + 2, dataArr[1]);
				}

				// Run insert query
				insertQuery.executeUpdate();
			case DO_NOTHING:
				// Do nothing
			default:
				break;
			}

	}
}