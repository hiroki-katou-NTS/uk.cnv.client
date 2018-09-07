package nts.uk.ctx.sys.assist.infra.repository.mastercopy.handler;

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
 * The Class KshstOvertimeFrameDataCopyHandler.
 */
@Getter
@Setter
@NoArgsConstructor
public class BsymtEmployeeCESettingDataCopyHandler extends DataCopyHandler {

	/** The insert query. */
	private final String INSERT_QUERY = "INSERT INTO BSYMT_EMPLOYEE_CE_SET(CID ,CODE_EDIT_METHOD_ATR ,DIGIT_NUM) VALUES (?, ?, ?);";

	/** The select by cid query. */
	private final String SELECT_BY_CID_QUERY = "SELECT CID ,CODE_EDIT_METHOD_ATR ,DIGIT_NUM FROM BSYMT_EMPLOYEE_CE_SET WHERE CID = ?";

	/** The delete by cid query. */
	private final String DELETE_BY_CID_QUERY = "DELETE FROM BSYMT_EMPLOYEE_CE_SET WHERE CID = ?";

	/**
	 * Instantiates a new kshst overtime frame data copy handler.
	 *
	 * @param copyMethod
	 *            the copy method
	 * @param companyCd
	 *            the company cd
	 */
	public BsymtEmployeeCESettingDataCopyHandler(EntityManager entityManager, CopyMethod copyMethod, String companyId) {
		this.entityManager = entityManager;
		this.copyMethod = copyMethod;
		this.companyId = companyId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.assist.dom.mastercopy.handler.DataCopyHandler#doCopy()
	 */
	@Override
	public void doCopy() {
		// Get all company zero data
		Query selectQuery = this.entityManager.createNativeQuery(SELECT_BY_CID_QUERY).setParameter(1,
				AppContexts.user().zeroCompanyIdInContract());
		Object[] zeroCompanyDatas = selectQuery.getResultList().toArray();
		if (zeroCompanyDatas.length == 0)
			return;
		switch (copyMethod) {
		case REPLACE_ALL:
			Query deleteQuery = this.entityManager.createNativeQuery(DELETE_BY_CID_QUERY).setParameter(1,
					this.companyId);
			deleteQuery.executeUpdate();
		case ADD_NEW:
			Query selectQueryTarget = this.entityManager.createNativeQuery(SELECT_BY_CID_QUERY).setParameter(1,
					this.companyId);
			Object[] targetDatas = selectQueryTarget.getResultList().toArray();
			if(targetDatas.length!=0) return ;
			String insertQueryStr = StringUtils.repeat(INSERT_QUERY, zeroCompanyDatas.length);
			Query insertQuery = this.entityManager.createNativeQuery(insertQueryStr);
			for (int i = 0, j = zeroCompanyDatas.length; i < j; i++) {
				Object[] dataArr = (Object[]) zeroCompanyDatas[i];
				insertQuery.setParameter(i * 3 + 1, this.companyId);
				insertQuery.setParameter(i * 3 + 2, dataArr[1]);
				insertQuery.setParameter(i * 3 + 3, dataArr[2]);
			}
			// Run insert query
			if (!StringUtils.isEmpty(insertQueryStr))
				insertQuery.executeUpdate();
		case DO_NOTHING:
			// Do nothing
		default:
			break;
		}
	}

}