package nts.uk.file.at.infra.ot.autocalsetting.wkpjob;


import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.file.at.app.export.ot.autocalsetting.wkpjob.WkpJobAutoCalSettingRepository;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * The Class JpaWkpJobCalSettingRepository.
 */
@Stateless
public class JpaWkpJobCalSettingRepository extends JpaRepository implements WkpJobAutoCalSettingRepository {

	private static final String GET_ALL_WORKPLACE_JOB;


	static {
		StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("     temp.LEGAL_OT_TIME_ATR, ");
        sql.append("     temp.LEGAL_OT_TIME_LIMIT, ");
        sql.append("     temp.LEGAL_MID_OT_TIME_ATR, ");
        sql.append("     temp.LEGAL_MID_OT_TIME_LIMIT, ");
        sql.append("     temp.NORMAL_OT_TIME_ATR, ");
        sql.append("     temp.NORMAL_OT_TIME_LIMIT, ");
        sql.append("     temp.NORMAL_MID_OT_TIME_ATR, ");
        sql.append("     temp.NORMAL_MID_OT_TIME_LIMIT, ");
        sql.append("     temp.EARLY_OT_TIME_ATR, ");
        sql.append("     temp.EARLY_OT_TIME_LIMIT, ");
        sql.append("     temp.EARLY_MID_OT_TIME_ATR, ");
        sql.append("     temp.EARLY_MID_OT_TIME_LIMIT, ");
        sql.append("     temp.FLEX_OT_TIME_ATR, ");
        sql.append("     temp.FLEX_OT_TIME_LIMIT, ");
        sql.append("     temp.REST_TIME_ATR, ");
        sql.append("     temp.REST_TIME_LIMIT, ");
        sql.append("     temp.LATE_NIGHT_TIME_ATR, ");
        sql.append("     temp.LATE_NIGHT_TIME_LIMIT, ");
        sql.append("     temp.LEAVE_LATE, ");
        sql.append("     temp.LEAVE_EARLY, ");
        sql.append("     temp.RAISING_CALC_ATR, ");
        sql.append("     temp.SPECIFIC_RAISING_CALC_ATR, ");
        sql.append("     temp.DIVERGENCE, ");
        sql.append("     CASE WHEN temp.ROW_NUMBER = 1 THEN temp.WKPCD ELSE '-' END, ");
        sql.append("     CASE WHEN temp.ROW_NUMBER = 1 THEN temp.WKP_NAME ELSE '-' END, ");
        sql.append("     temp.JOB_CD, ");
        sql.append("     temp.JOB_NAME ");
        sql.append("   FROM ");
        sql.append("     ( ");
        sql.append("      SELECT  ");
        sql.append("         RROW_NUMBER() OVER (PARTITION BY wj.WPKID ORDER BY CASE WHEN HIERARCHY_CD IS NULL THEN 1 ELSE 0 END ASC, WPKID, HIERARCHY_CD, IIF(JOB_NAME IS NULL, 1, 0) ASC, JOB_CD) AS ROW_NUMBER,  ");
        sql.append("         wj.LEGAL_OT_TIME_ATR, ");
        sql.append("         wj.LEGAL_OT_TIME_LIMIT, ");
        sql.append("         wj.LEGAL_MID_OT_TIME_ATR, ");
        sql.append("         wj.LEGAL_MID_OT_TIME_LIMIT, ");
        sql.append("         wj.NORMAL_OT_TIME_ATR, ");
        sql.append("         wj.NORMAL_OT_TIME_LIMIT, ");
        sql.append("         wj.NORMAL_MID_OT_TIME_ATR, ");
        sql.append("         wj.NORMAL_MID_OT_TIME_LIMIT, ");
        sql.append("         wj.EARLY_OT_TIME_ATR, ");
        sql.append("         wj.EARLY_OT_TIME_LIMIT, ");
        sql.append("         wj.EARLY_MID_OT_TIME_ATR, ");
        sql.append("         wj.EARLY_MID_OT_TIME_LIMIT, ");
        sql.append("         wj.FLEX_OT_TIME_ATR, ");
        sql.append("         wj.FLEX_OT_TIME_LIMIT, ");
        sql.append("         wj.REST_TIME_ATR, ");
        sql.append("         wj.REST_TIME_LIMIT, ");
        sql.append("         wj.LATE_NIGHT_TIME_ATR, ");
        sql.append("         wj.LATE_NIGHT_TIME_LIMIT, ");
        sql.append("         wj.LEAVE_LATE, ");
        sql.append("         wj.LEAVE_EARLY, ");
        sql.append("         wj.RAISING_CALC_ATR, ");
        sql.append("         wj.SPECIFIC_RAISING_CALC_ATR, ");
        sql.append("         wj.DIVERGENCE, ");
        sql.append("         wj.WKPCD, ");
        sql.append("         w.WKP_NAME, ");
        sql.append("         wj.JOB_CD, ");
        sql.append("         jn.JOB_NAME,");
        sql.append("         HIERARCHY_CD,");
        sql.append("         WPKID");
        sql.append("       FROM  (SELECT HIST_ID, WKPID, CID ");
        sql.append("          FROM BSYMT_WORKPLACE_HIST ");
        sql.append("          WHERE END_DATE >= ?baseDate  AND CID = ?cid)  h ");
        sql.append("       INNER JOIN (SELECT HIST_ID, CID, WKPID, WKP_NAME ");
        sql.append("             FROM BSYMT_WORKPLACE_INFO ");
        sql.append("             ) w ");
        sql.append("       ON w.HIST_ID = h.HIST_ID AND w.WKPID = h.WKPID AND h.CID = w.CID ");
        sql.append("       INNER JOIN BSYMT_WKP_CONFIG wc ");
        sql.append("            ON wc.CID = h.CID AND wc.END_DATE = ?baseDate");
        sql.append("       INNER JOIN BSYMT_WKP_CONFIG_INFO wci ");
        sql.append("       ON wci.WKPID = w.WKPID AND wci.CID = wc.CID AND wci.HIST_ID = wc.HIST_ID");
        sql.append("  RIGHT JOIN (SELECT  j.LEGAL_OT_TIME_ATR, ");
        sql.append("            j.LEGAL_OT_TIME_LIMIT, ");
        sql.append("            j.LEGAL_MID_OT_TIME_ATR, ");
        sql.append("            j.LEGAL_MID_OT_TIME_LIMIT, ");
        sql.append("            j.NORMAL_OT_TIME_ATR, ");
        sql.append("            j.NORMAL_OT_TIME_LIMIT, ");
        sql.append("            j.NORMAL_MID_OT_TIME_ATR, ");
        sql.append("            j.NORMAL_MID_OT_TIME_LIMIT, ");
        sql.append("            j.EARLY_OT_TIME_ATR, ");
        sql.append("            j.EARLY_OT_TIME_LIMIT, ");
        sql.append("            j.EARLY_MID_OT_TIME_ATR, ");
        sql.append("            j.EARLY_MID_OT_TIME_LIMIT, ");
        sql.append("            j.FLEX_OT_TIME_ATR, ");
        sql.append("            j.FLEX_OT_TIME_LIMIT, ");
        sql.append("            j.REST_TIME_ATR, ");
        sql.append("            j.REST_TIME_LIMIT, ");
        sql.append("            j.LATE_NIGHT_TIME_ATR, ");
        sql.append("            j.LATE_NIGHT_TIME_LIMIT, ");
        sql.append("            j.LEAVE_LATE, ");
        sql.append("            j.LEAVE_EARLY, ");
        sql.append("            j.RAISING_CALC_ATR, ");
        sql.append("            j.SPECIFIC_RAISING_CALC_ATR, ");
        sql.append("            j.DIVERGENCE, ");
        sql.append("            j.WPKID, ");
        sql.append("            j.CID, ");
        sql.append("            jf.JOB_ID, ");
        sql.append("            wf.WKPCD, ");
        sql.append("            jf.JOB_CD ");
        sql.append("         FROM (SELECT DISTINCT JOB_ID, JOB_CD, CID ");
        sql.append("            FROM BSYMT_JOB_INFO ");
        sql.append("            WHERE CID = ?cid) jf ");
        sql.append("         RIGHT JOIN (SELECT * ");
        sql.append("               FROM KSHMT_AUTO_WKP_JOB_CAL");
        sql.append("               WHERE CID = ?cid) j ");
        sql.append("               ON j.CID = jf.CID AND j.JOBID = jf.JOB_ID ");
        sql.append("         LEFT JOIN (SELECT DISTINCT WKPCD , WKPID, CID ");
        sql.append("               FROM BSYMT_WORKPLACE_INFO ");
        sql.append("               WHERE CID = ?cid)  wf ");
        sql.append("         ON j.WPKID = wf.WKPID AND j.CID = wf.CID  ");
        sql.append("         ) wj ");
        sql.append("    ON  wj.WPKID = w.WKPID AND wj.CID = w.CID ");
        sql.append("  LEFT JOIN (SELECT jh.JOB_ID , jh.HIST_ID, ji.JOB_NAME, jh.CID ");
        sql.append("       FROM (SELECT JOB_ID, HIST_ID, CID ");
        sql.append("          FROM BSYMT_JOB_HIST ");
        sql.append("          WHERE END_DATE >= ?baseDate ) jh ");
        sql.append("       INNER JOIN BSYMT_JOB_INFO ji ");
        sql.append("       ON ji.HIST_ID = jh.HIST_ID AND ji.JOB_ID = jh.JOB_ID AND ji.CID = jh.CID ) jn ");
        sql.append("  ON wj.JOB_ID = jn.JOB_ID  AND wj.CID = jn.CID ");
        sql.append("  ) temp ");
        sql.append("  ORDER BY CASE WHEN temp.HIERARCHY_CD IS NULL THEN 1 ELSE 0 END ASC, temp.WPKID, HIERARCHY_CD, IIF(temp.JOB_NAME IS NULL, 1, 0) ASC, temp.JOB_CD");
		GET_ALL_WORKPLACE_JOB = sql.toString();


	}

	@Override
	public List<Object[]> getWkpJobSettingToExport(String cid, String baseDate) {
		List<Object[]> resultQuery = null;
		try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date sqlDate = null;
            java.util.Date date = null;
            try {
                date = format.parse(baseDate);
                sqlDate = new java.sql.Date(date.getTime());
            } catch (ParseException e) {
                return Collections.emptyList();
            }
			resultQuery = (List<Object[]>) this.getEntityManager().createNativeQuery(GET_ALL_WORKPLACE_JOB)
					.setParameter("cid", cid)
                    .setParameter("baseDate", sqlDate)
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
		return resultQuery;
	}

}
