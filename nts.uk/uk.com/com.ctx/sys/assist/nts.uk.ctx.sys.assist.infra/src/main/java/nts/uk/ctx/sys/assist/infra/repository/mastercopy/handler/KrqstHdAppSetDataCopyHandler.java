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
 * The Class KrqstHdAppSetDataCopyHandler.
 */
@Getter
@Setter
@NoArgsConstructor
public class KrqstHdAppSetDataCopyHandler extends DataCopyHandler {

	/** The insert query. */
	private final String INSERT_QUERY = "INSERT INTO KRQST_HD_APP_SET(CID, HD_APP_TYPE, DISPLAY_UNSELECTED, USE_60H_HOLIDAY, OBSTACLE_NAME, REGIS_SHORT_LOST_HD, HD_NAME, REGIS_LACK_PUBLIC_HD, CHANGE_WRK_HOURS, "
			+ "CKUPER_LIMIT_HALFDAY_HD, ACTUAL_DISPLAY_ATR, WRK_HOURS_USE_ATR, PRIORITY_DIGESTION_ATR, YEAR_HD_NAME, "
			+ "REGIS_NUM_YEAR_OFF, FURIKYU_NAME, REGIS_INSUFF_NUMOFREST, USE_TIME_GENER_HD, USE_TIME_YEAR_HD, "
			+ "TIME_DIGEST_NAME, ABSENTEEISM_NAME, CONCHECK_OUTLEGAL_LAW, SPECIAL_VACATION_NAME, CONCHECK_DATE_RELEASEDATE, APPLI_DATE_CONTRA_ATR, "
			+ "YEAR_RESIG_NAME, REGIS_SHORT_RESER_YEAR, HD_APPTYPE, DISPLAY_UNSELECT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

	/** The select by cid query. */
	private final String SELECT_BY_CID_QUERY = "SELECT CID, HD_APP_TYPE, DISPLAY_UNSELECTED, USE_60H_HOLIDAY, OBSTACLE_NAME, REGIS_SHORT_LOST_HD, HD_NAME, REGIS_LACK_PUBLIC_HD, CHANGE_WRK_HOURS, "
			+ "CKUPER_LIMIT_HALFDAY_HD, ACTUAL_DISPLAY_ATR, WRK_HOURS_USE_ATR, PRIORITY_DIGESTION_ATR, YEAR_HD_NAME, "
			+ "REGIS_NUM_YEAR_OFF, FURIKYU_NAME, REGIS_INSUFF_NUMOFREST, USE_TIME_GENER_HD, USE_TIME_YEAR_HD, "
			+ "TIME_DIGEST_NAME, ABSENTEEISM_NAME, CONCHECK_OUTLEGAL_LAW, SPECIAL_VACATION_NAME, CONCHECK_DATE_RELEASEDATE, APPLI_DATE_CONTRA_ATR, "
			+ "YEAR_RESIG_NAME, REGIS_SHORT_RESER_YEAR, HD_APPTYPE, DISPLAY_UNSELECT FROM KRQST_HD_APP_SET WHERE CID = ?";

	/** The delete by cid query. */
	private final String DELETE_BY_CID_QUERY = "DELETE FROM KRQST_HD_APP_SET WHERE CID = ?";

	/**
	 * Instantiates a new krqst hd app set data copy handler.
	 *
	 * @param entityManager
	 *            the entity manager
	 * @param copyMethod
	 *            the copy method
	 * @param companyId
	 *            the company id
	 */
	public KrqstHdAppSetDataCopyHandler(EntityManager entityManager, CopyMethod copyMethod, String companyId) {
		super();
		this.entityManager = entityManager;
		this.copyMethod = copyMethod;
		this.companyId = companyId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.request.dom.mastercopy.DataCopyHandler#doCopy()
	 */
	@Override
	public void doCopy() {

		// Get all company zero data
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
						if (dataAttr[1].equals(targetAttr[1])) {
							zeroCompanyDatas.remove(i);
							i -= 1;
							break;
						}
					}
				}
			}
			String insertQueryStr = StringUtils.repeat(INSERT_QUERY, zeroCompanyDatas.size());
			Query insertQuery = this.entityManager.createNativeQuery(insertQueryStr);
			for (int i = 0, j = zeroCompanyDatas.size(); i < j; i++) {
				Object[] dataArr = (Object[]) zeroCompanyDatas.get(i);
				insertQuery.setParameter(i * 29 + 1, this.companyId);
				insertQuery.setParameter(i * 29 + 2, dataArr[1]);
				insertQuery.setParameter(i * 29 + 3, dataArr[2]);
				insertQuery.setParameter(i * 29 + 4, dataArr[3]);
				insertQuery.setParameter(i * 29 + 5, dataArr[4]);
				insertQuery.setParameter(i * 29 + 6, dataArr[5]);
				insertQuery.setParameter(i * 29 + 7, dataArr[6]);
				insertQuery.setParameter(i * 29 + 8, dataArr[7]);
				insertQuery.setParameter(i * 29 + 9, dataArr[8]);
				insertQuery.setParameter(i * 29 + 10, dataArr[9]);
				insertQuery.setParameter(i * 29 + 11, dataArr[10]);
				insertQuery.setParameter(i * 29 + 12, dataArr[11]);
				insertQuery.setParameter(i * 29 + 13, dataArr[12]);
				insertQuery.setParameter(i * 29 + 14, dataArr[13]);
				insertQuery.setParameter(i * 29 + 15, dataArr[14]);
				insertQuery.setParameter(i * 29 + 16, dataArr[15]);
				insertQuery.setParameter(i * 29 + 17, dataArr[16]);
				insertQuery.setParameter(i * 29 + 18, dataArr[17]);
				insertQuery.setParameter(i * 29 + 19, dataArr[18]);
				insertQuery.setParameter(i * 29 + 20, dataArr[19]);
				insertQuery.setParameter(i * 29 + 21, dataArr[20]);
				insertQuery.setParameter(i * 29 + 22, dataArr[21]);
				insertQuery.setParameter(i * 29 + 23, dataArr[22]);
				insertQuery.setParameter(i * 29 + 24, dataArr[23]);
				insertQuery.setParameter(i * 29 + 25, dataArr[24]);
				insertQuery.setParameter(i * 29 + 26, dataArr[25]);
				insertQuery.setParameter(i * 29 + 27, dataArr[26]);
				insertQuery.setParameter(i * 29 + 28, dataArr[27]);
				insertQuery.setParameter(i * 29 + 29, dataArr[28]);
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