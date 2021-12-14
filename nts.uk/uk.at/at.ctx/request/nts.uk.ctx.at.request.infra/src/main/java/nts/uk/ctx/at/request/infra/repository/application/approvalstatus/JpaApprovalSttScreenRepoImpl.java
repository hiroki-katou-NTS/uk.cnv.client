package nts.uk.ctx.at.request.infra.repository.application.approvalstatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.approvalstatus.service.ApprovalSttScreenRepository;
import nts.uk.ctx.at.request.dom.application.approvalstatus.service.output.EmpPeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

/**
 * refactor 5
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaApprovalSttScreenRepoImpl extends JpaRepository implements ApprovalSttScreenRepository {
	
	@Override
	public List<EmpPeriod> getCountEmp(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		String sql = 
				"SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"FROM ( " +
				"        SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"        FROM ( " +
				"                SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                WHERE        @startDate <= SSR.END_DATE " +
				"                AND          SSR.START_DATE <= @endDate " +
				"                AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                ) SKBSYIN " +
				"        INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"        ON               SKBSYIN.SID = SKR.SID " +
				"        INNER JOIN       BSYMT_SYAIN SDKJ " +
				"        ON               SDKJ.SID = SKR.SID " +
				"        WHERE            @startDate <= SKR.END_DATE " +
				"        AND              SKR.START_DATE <= @endDate " +
				"        AND SDKJ.DEL_STATUS_ATR = '0' " +
				") SKBSYINIO " +
				"INNER JOIN  ( " +
				"        SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"        FROM BSYMT_AFF_EMP_HIST KR " +
				"        INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"        ON KR.HIST_ID = KRK.HIST_ID " +
				"        WHERE KRK.EMP_CD IN @employmentCDLst " +
				"        AND @startDate <= KR.END_DATE " +
				"        AND KR.START_DATE <= @endDate " +
				") KYO " +
				"ON SKBSYINIO.SID = KYO.SID " +
				"ORDER BY SKBSYINIO.WORKPLACE_ID,KYO.EMP_CD;";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					return new EmpPeriod(
							rec.getString("WORKPLACE_ID"), 
							rec.getString("SID"), 
							rec.getString("EMP_CD"), 
							rec.getGeneralDate("WORK_ST"), 
							rec.getGeneralDate("WORK_ED"), 
							rec.getGeneralDate("COMP_ST"), 
							rec.getGeneralDate("COMP_ED"), 
							rec.getGeneralDate("KYO_ST"), 
							rec.getGeneralDate("KYO_ED"), 
							"", 
							"");
		});
	}

	@Override
	public Map<String, Integer> getCountUnApprApp(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		Map<String, Integer> result = new HashMap<>();
		String sql = 
				"SELECT MSNSHIN.WORKPLACE_ID,COUNT(MSNSHIN.SID) AS COUNTSID " +
				"FROM ( " +
				"        SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID " +
				"        FROM ( " +
				"                    SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                    FROM ( " +
				"                            SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                            FROM ( " +
				"                                    SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                    FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                    INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                    ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                    WHERE        @startDate <= SSR.END_DATE " +
				"                                    AND          SSR.START_DATE <= @endDate " +
				"                                            AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                                    ) SKBSYIN " +
				"                            INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                            ON               SKBSYIN.SID = SKR.SID " +
				"                            INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                            ON               SDKJ.SID = SKR.SID " +
				"                            WHERE            @startDate <= SKR.END_DATE " +
				"                            AND              SKR.START_DATE <= @endDate " +
				"                            AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                    ) SKBSYINIO " +
				"                    INNER JOIN  ( " +
				"                            SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                            FROM BSYMT_AFF_EMP_HIST KR " +
				"                            INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                            ON KR.HIST_ID = KRK.HIST_ID " +
				"                            WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                            AND @startDate <= KR.END_DATE " +
				"                            AND KR.START_DATE <= @endDate " +
				"                    ) KYO " +
				"                    ON SKBSYINIO.SID = KYO.SID " +
				"                   ) SKBSYITERM " +
				"        INNER JOIN  ( " +
				"                               SELECT             SRI.EMPLOYEE_ID as APP_SID,SRI.APPROVAL_RECORD_DATE as APP_DATE,SRI.ROOT_STATE_ID " +
				"                               FROM               WWFDT_APP_INST_ROUTE SRI " +
				"                               INNER JOIN         WWFDT_APP_INST_PHASE SFI " +
				"                               ON                 SRI.ROOT_STATE_ID = SFI.ROOT_STATE_ID " +
				"                               WHERE              SFI.APP_PHASE_ATR IN ('0','3','4') " +
				"                               AND                @startDate <= SRI.APPROVAL_RECORD_DATE " +
				"                               AND                SRI.APPROVAL_RECORD_DATE <= @endDate " +
				"                               ) MSNSNS " +
				"        ON              SKBSYITERM.SID = MSNSNS.APP_SID " +
				"        WHERE SKBSYITERM.WORK_ST <= MSNSNS.APP_DATE " +
				"        AND SKBSYITERM.COMP_ST <= MSNSNS.APP_DATE " +
				"        AND SKBSYITERM.KYO_ST <= MSNSNS.APP_DATE " +
				"        AND MSNSNS.APP_DATE <= SKBSYITERM.WORK_ED " +
				"        AND MSNSNS.APP_DATE <= SKBSYITERM.COMP_ED " +
				"        AND MSNSNS.APP_DATE <= SKBSYITERM.KYO_ED " +
				") MSNSHIN " +
				"GROUP BY MSNSHIN.WORKPLACE_ID;";
		List<Pair<String, Integer>> listPair = new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WORKPLACE_ID");
					Integer count = rec.getInt("COUNTSID");
					return Pair.of(wkpID, count);
				});
		for(Pair<String, Integer> pair : listPair) {
			result.put(pair.getLeft(), pair.getRight());
		}
		return result;
	}
	
	@Override
	public Map<String, Integer> getCountUnApprDay(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		Map<String, Integer> result = new HashMap<>();
		String sql = 
				"SELECT MSNSHIN.WORKPLACE_ID,COUNT(MSNSHIN.SID) AS COUNTSID " +
				"FROM ( " +
				"        SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID " +
				"        FROM  ( " +
				"                     SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                     FROM ( " +
				"                             SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                             FROM ( " +
				"                                     SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                     FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                     INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                     ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                     WHERE        @startDate <= SSR.END_DATE " +
				"                                     AND          SSR.START_DATE <= @endDate " +
				"                                             AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                                     ) SKBSYIN " +
				"                             INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                             ON               SKBSYIN.SID = SKR.SID " +
				"                              INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                             ON               SDKJ.SID = SKR.SID " +
				"                             WHERE            @startDate <= SKR.END_DATE " +
				"                             AND              SKR.START_DATE <= @endDate " +
				"                             AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                     ) SKBSYINIO " +
				"                     INNER JOIN  ( " +
				"                             SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                             FROM BSYMT_AFF_EMP_HIST KR " +
				"                             INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                             ON KR.HIST_ID = KRK.HIST_ID " +
				"                             WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                             AND @startDate <= KR.END_DATE " +
				"                             AND KR.START_DATE <= @endDate " +
				"                     ) KYO " +
				"                     ON SKBSYINIO.SID = KYO.SID " +
				"                    )  SKBSYITERM " +
				"        INNER JOIN  ( " +
				"                               SELECT   MIDFOD.EMPLOYEE_ID,SGJFOD.RECORD_DATE,'0' as NONAPP " +  
				"                               FROM   ( " +
				"                                             SELECT     SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF,SNRMID.START_DATE,SNRMID.END_DATE " +
				"                                             FROM       WWFDT_INST_ROUTE SNRMID " +
				"                                             INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                             ON         SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                             WHERE      @startDate <= SNRMID.END_DATE " +
				"                                             AND        SNRMID.START_DATE <= @endDate " +
				"                                             AND        SNRMID.ROOT_TYPE = '1' " +
				"                                             GROUP BY   SNRMID.EMPLOYEE_ID,SNRMID.START_DATE,SNRMID.END_DATE " +
				"                                            )  MIDFOD " +
				"                               INNER JOIN (    SELECT SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE,SYNZMF.PHASE_ORDER,SYNZMF.APP_PHASE_ATR " +
				"                                                               FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                               INNER JOIN WWFDT_CONF_PHASE SYNZMF " +
				"                                                               ON         SGJKNJT.ROOT_ID = SYNZMF.ROOT_ID " +
				"                                                               WHERE      SGJKNJT.ROOT_TYPE = '1' " +
				"                                                               AND        @startDate <= SGJKNJT.RECORD_DATE " +
				"                                                               AND SGJKNJT.RECORD_DATE <= @endDate " +
				"                                               ) SGJFOD " +
				"                               ON                MIDFOD.EMPLOYEE_ID = SGJFOD.EMPLOYEE_ID " +
				"                               WHERE   MIDFOD.FINF  =  SGJFOD.PHASE_ORDER " +
				"                               AND     MIDFOD.START_DATE <= SGJFOD.RECORD_DATE " +
				"                               AND     SGJFOD.RECORD_DATE <= MIDFOD.END_DATE " +
				"                               AND     SGJFOD.APP_PHASE_ATR  IN  ('0','3') " +
				"                               UNION ALL " +
				"                               SELECT         SGJKN.EMPLOYEE_ID,SGJKN.RECORD_DATE,ISNULL(SYNZMF.APP_PHASE_ATR,'0') as NONAPP " +
				"                               FROM          (     SELECT MIDFOD.EMPLOYEE_ID,SGJT.RECORD_DATE,SGJT.ROOT_ID,MIDFOD.FINF " +
				"                                                           FROM   ( " +
				"                                                                         SELECT     SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF,SNRMID.START_DATE,SNRMID.END_DATE " +
				"                                                                         FROM       WWFDT_INST_ROUTE SNRMID " +
				"                                                                         INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                                                         ON         SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                                                         WHERE      @startDate <= SNRMID.END_DATE " +
				"                                                                         AND        SNRMID.START_DATE <= @endDate " +
				"                                                                         AND        SNRMID.ROOT_TYPE = '1' " +
				"                                                                         GROUP BY   SNRMID.EMPLOYEE_ID,SNRMID.START_DATE,SNRMID.END_DATE " +
				"                                                                       ) MIDFOD " +
				"                                                               INNER JOIN (    SELECT SGJKNJT.ROOT_ID,SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE " +
				"                                                                                               FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                                                               WHERE       @startDate <= SGJKNJT.RECORD_DATE " +
				"                                                                                               AND SGJKNJT.RECORD_DATE <= @endDate " +
				"                                                                                               AND SGJKNJT.ROOT_TYPE = '1'                        ) SGJT " +
				"                                                               ON       MIDFOD.EMPLOYEE_ID = SGJT.EMPLOYEE_ID " +
				"                                                               WHERE    MIDFOD.START_DATE <= SGJT.RECORD_DATE " +
				"                                                               AND      SGJT.RECORD_DATE <= MIDFOD.END_DATE                                ) SGJKN " +
				"                               LEFT JOIN       WWFDT_CONF_PHASE SYNZMF " +
				"                               ON      (       SGJKN.ROOT_ID = SYNZMF.ROOT_ID " +
				"                               AND             SGJKN.FINF = SYNZMF.PHASE_ORDER      ) " +
				"                               WHERE           SYNZMF.APP_PHASE_ATR IS NULL " +
				"                              ) JCHOSN " +
				"        ON              SKBSYITERM.SID = JCHOSN.EMPLOYEE_ID " +
				"        WHERE           SKBSYITERM.WORK_ST <= JCHOSN.RECORD_DATE " +
				"        AND             SKBSYITERM.COMP_ST <= JCHOSN.RECORD_DATE " +
				"        AND             SKBSYITERM.KYO_ST <= JCHOSN.RECORD_DATE " +
				"        AND             JCHOSN.RECORD_DATE <= SKBSYITERM.WORK_ED " +
				"        AND             JCHOSN.RECORD_DATE <= SKBSYITERM.COMP_ED " +
				"        AND             JCHOSN.RECORD_DATE <= SKBSYITERM.KYO_ED " +
				"        ) MSNSHIN " +
				"GROUP BY MSNSHIN.WORKPLACE_ID;";
		List<Pair<String, Integer>> listPair = new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WORKPLACE_ID");
					Integer count = rec.getInt("COUNTSID");
					return Pair.of(wkpID, count);
				});
		for(Pair<String, Integer> pair : listPair) {
			result.put(pair.getLeft(), pair.getRight());
		}
		return result;
	}
	
	@Override
	public Map<String, Integer> getCountUnConfirmDay(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		Map<String, Integer> result = new HashMap<>();
		String sql = 
				"SELECT MSNSHIN.WORKPLACE_ID,COUNT(MSNSHIN.SID) AS COUNTSID " +
				"FROM ( " +
				"	SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID " +
				"	FROM        ( " +
				"                                   SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                                   FROM ( " +
				"                                          SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                                          FROM ( " +
				"                                          	SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                          	FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                          	INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                          	ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                          	WHERE        @startDate <= SSR.END_DATE " +
				"                                          	AND          SSR.START_DATE <= @endDate " +
				"                                                          AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                                          	) SKBSYIN " +
				"                                          INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                          ON               SKBSYIN.SID = SKR.SID " +
				"                                          INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                          ON               SDKJ.SID = SKR.SID " +
				"                                          WHERE            @startDate <= SKR.END_DATE " +
				"                                          AND              SKR.START_DATE <= @endDate " +
				"                                          AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                                   ) SKBSYINIO " +
				"                                   INNER JOIN  ( " +
				"                                          SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                                          FROM BSYMT_AFF_EMP_HIST KR " +
				"                                          INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                          ON KR.HIST_ID = KRK.HIST_ID " +
				"                                          WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                                          AND @startDate <= KR.END_DATE " +
				"                                          AND KR.START_DATE <= @endDate " +
				"                                               ) KYO " +
				"                                   ON SKBSYINIO.SID = KYO.SID " +
				"                                  )  SKBSYITERM " +
				"	INNER JOIN   ( " +
				"         SELECT  SGJSKNJT.EMPLOYEE_ID as SYAIN_ID,SGJSKNJT.RECORD_DATE as TG_DAY,'0' as NON_APP " +
				"         FROM WWFDT_CONF_ROUTE SGJSKNJT " +
				"         LEFT JOIN KRCDT_DAY_SELF_CHECK DAYKN " +
				"         ON       ( SGJSKNJT.CID = DAYKN.CID " +
				"	AND SGJSKNJT.EMPLOYEE_ID = DAYKN.SID " +
				"	AND SGJSKNJT.RECORD_DATE = DAYKN.PROCESSING_YMD   ) " +
				"         WHERE @startDate <= SGJSKNJT.RECORD_DATE " +
				"         AND SGJSKNJT.RECORD_DATE <= @endDate " +
				"         AND SGJSKNJT.ROOT_TYPE = '1' " +
				"         AND DAYKN.INDENTIFICATION_YMD IS NULL " +
				"        ) HNKN " +
				"	ON SKBSYITERM.SID = HNKN.SYAIN_ID " +
				"	WHERE SKBSYITERM.WORK_ST <= HNKN.TG_DAY " +
				"	AND SKBSYITERM.COMP_ST <= HNKN.TG_DAY " +
				"	AND SKBSYITERM.KYO_ST <= HNKN.TG_DAY " +
				"	AND HNKN.TG_DAY <= SKBSYITERM.WORK_ED " +
				"	AND HNKN.TG_DAY <= SKBSYITERM.COMP_ED " +
				"	AND HNKN.TG_DAY <= SKBSYITERM.KYO_ED " +
				") MSNSHIN " +
				"GROUP BY MSNSHIN.WORKPLACE_ID;";
		List<Pair<String, Integer>> listPair = new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WORKPLACE_ID");
					Integer count = rec.getInt("COUNTSID");
					return Pair.of(wkpID, count);
				});
		for(Pair<String, Integer> pair : listPair) {
			result.put(pair.getLeft(), pair.getRight());
		}
		return result;
	}
	
	@Override
	public Map<String, Integer> getCountUnApprMonth(GeneralDate endDate, YearMonth yearMonth, List<String> wkpIDLst, List<String> employmentCDLst) {
		Map<String, Integer> result = new HashMap<>();
		String sql = 
				"SELECT MSNSHIN.WORKPLACE_ID,COUNT(MSNSHIN.SID) AS COUNTSID " +
				"FROM ( " +
				"        SELECT DISTINCT SKBSYITERM_MON.WORKPLACE_ID,SKBSYITERM_MON.SID " +
				"        FROM            ( " +
				"                                   SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD " +
				"                                   FROM ( " +
				"                                           SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID " +
				"                                           FROM ( " +
				"                                                   SELECT       SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                                   FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                                   INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                                   ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                                   WHERE        @endDate <= SSR.END_DATE " +
				"                                                   AND          SSR.START_DATE <= @endDate " +
				"                                                   AND          SSRK.WORKPLACE_ID IN  @wkpIDLst " +
				"                                                   ) SKBSYIN " +
				"                                           INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                           ON               SKBSYIN.SID = SKR.SID " +
				"                                           INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                           ON               SDKJ.SID = SKR.SID " +
				"                                           WHERE            @endDate <= SKR.END_DATE " +
				"                                           AND              SKR.START_DATE <= @endDate " +
				"                                           AND              SDKJ.DEL_STATUS_ATR = '0' " +
				"                                   ) SKBSYINIO " +
				"                                   INNER JOIN  ( " +
				"                                           SELECT         KRK.SID,KRK.EMP_CD " +
				"                                           FROM           BSYMT_AFF_EMP_HIST KR " +
				"                                           INNER JOIN     BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                           ON             KR.HIST_ID = KRK.HIST_ID " +
				"                                           WHERE          KRK.EMP_CD IN  @employmentCDLst " +
				"                                           AND            @endDate <= KR.END_DATE " +
				"                                           AND            KR.START_DATE <= @endDate " +
				"                                   ) KYO " +
				"                                   ON             SKBSYINIO.SID = KYO.SID " +
				"                               ) SKBSYITERM_MON " +
				"        INNER JOIN       ( " +
				"                                    SELECT                MIDFOD.EMPLOYEE_ID,SGJFOD.RECORD_DATE,'0' as NONAPP " +
				"                                    FROM       ( " +
				"                                                                    SELECT SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF " +
				"                                                                    FROM WWFDT_INST_ROUTE SNRMID " +
				"                                                                    INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                                                    ON             SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                                                    WHERE          @endDate <= SNRMID.END_DATE " +
				"                                                                    AND SNRMID.START_DATE <= @endDate " +
				"                                                                    AND SNRMID.ROOT_TYPE = '2' " +
				"                                                                    GROUP BY SNRMID.EMPLOYEE_ID " +
				"                                                            ) MIDFOD " +
				"                                    INNER JOIN (    " +
				"                                                                    SELECT SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE,SYNZMF.PHASE_ORDER,SYNZMF.APP_PHASE_ATR " +
				"                                                                    FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                                    INNER JOIN WWFDT_CONF_PHASE SYNZMF " +
				"                                                                    ON SGJKNJT.ROOT_ID = SYNZMF.ROOT_ID " +
				"                                                                    WHERE SGJKNJT.ROOT_TYPE = '2' " +
				"                                                                    AND            @yearMonth = SGJKNJT.YEARMONTH " +
				"                                                            ) SGJFOD " +
				"                                    ON      MIDFOD.EMPLOYEE_ID = SGJFOD.EMPLOYEE_ID " +
				"                                    WHERE   MIDFOD.FINF  = SGJFOD.PHASE_ORDER " +
				"                                    AND     SGJFOD.APP_PHASE_ATR IN ('0','3') " +
				"                                    UNION ALL " +
				"                                    SELECT      SGJKN.EMPLOYEE_ID,SGJKN.RECORD_DATE,ISNULL(SYNZMF.APP_PHASE_ATR,'0') as NONAPP " +
				"                                    FROM        ( " +
				"                                                                    SELECT  MIDFOD.EMPLOYEE_ID,SGJT.RECORD_DATE,SGJT.ROOT_ID,MIDFOD.FINF " +
				"                                                                    FROM        (   SELECT     SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF " +
				"                                                                                                    FROM       WWFDT_INST_ROUTE SNRMID " +
				"                                                                                                    INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                                                                                    ON         SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                                                                                    WHERE      @endDate <= SNRMID.END_DATE " +
				"                                                                                                    AND        SNRMID.START_DATE <= @endDate " +
				"                                                                                                    AND        SNRMID.ROOT_TYPE = '2' " +
				"                                                                                                    GROUP BY   SNRMID.EMPLOYEE_ID " +
				"                                                                                            ) MIDFOD " +
				"                                                                    INNER JOIN  (   SELECT     SGJKNJT.ROOT_ID,SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE " +
				"                                                                                                    FROM       WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                                                                    WHERE      SGJKNJT.ROOT_TYPE = '2' " +
				"                                                                                                    AND        @yearMonth = SGJKNJT.YEARMONTH " +
				"                                                                                            ) SGJT " +
				"                                                                    ON      MIDFOD.EMPLOYEE_ID = SGJT.EMPLOYEE_ID " +
				"                                                            ) SGJKN " +
				"                                    LEFT JOIN   WWFDT_CONF_PHASE SYNZMF " +
				"                                    ON       (        SGJKN.ROOT_ID = SYNZMF.ROOT_ID " + 
				"                                                AND   SGJKN.FINF = SYNZMF.PHASE_ORDER   ) " +
				"                                    WHERE       SYNZMF.APP_PHASE_ATR  IS NULL " +
				"                                     ) JCHOSN_M " +
				"        ON              SKBSYITERM_MON.SID  = JCHOSN_M.EMPLOYEE_ID " +
				") MSNSHIN " +
				"GROUP BY MSNSHIN.WORKPLACE_ID;";
		List<Pair<String, Integer>> listPair = new NtsStatement(sql, this.jdbcProxy())
				.paramDate("endDate", endDate)
				.paramInt("yearMonth", yearMonth.v())
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WORKPLACE_ID");
					Integer count = rec.getInt("COUNTSID");
					return Pair.of(wkpID, count);
				});
		for(Pair<String, Integer> pair : listPair) {
			result.put(pair.getLeft(), pair.getRight());
		}
		return result;
	}
	
	@Override
	public Map<String, Integer> getCountUnConfirmMonth(GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		Map<String, Integer> result = new HashMap<>();
		String sql = 
				"SELECT MSNSHIN.WORKPLACE_ID,COUNT(MSNSHIN.SID) AS COUNTSID " +
				"FROM ( " +
				"        SELECT DISTINCT SKBSYITERM_MON.WORKPLACE_ID,SKBSYITERM_MON.SID " +
				"        FROM   ( " +
				"                            SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD " +
				"                            FROM ( " +
				"                                      SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID " +
				"                                      FROM ( " +
				"                                                SELECT       SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                                FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                                INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                                ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                                WHERE        @endDate <= SSR.END_DATE " +
				"                                                AND          SSR.START_DATE <= @endDate " +
				"                                                AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                                                ) SKBSYIN " +
				"                                      INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                      ON               SKBSYIN.SID = SKR.SID " +
				"                                      INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                      ON               SDKJ.SID = SKR.SID " +
				"                                      WHERE            @endDate <= SKR.END_DATE " +
				"                                      AND              SKR.START_DATE <= @endDate " +
				"                                      AND              SDKJ.DEL_STATUS_ATR = '0' " +
				"                                      ) SKBSYINIO " +
				"                            INNER JOIN  ( " +
				"                                                SELECT         KRK.SID,KRK.EMP_CD " +
				"                                                FROM           BSYMT_AFF_EMP_HIST KR " +
				"                                                INNER JOIN     BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                                ON             KR.HIST_ID = KRK.HIST_ID " +
				"                                                WHERE          KRK.EMP_CD IN   @employmentCDLst " +
				"                                                AND            @endDate <= KR.END_DATE " +
				"                                                AND            KR.START_DATE <= @endDate " +
				"                                                ) KYO " +
				"                             ON SKBSYINIO.SID = KYO.SID " +
				"                     ) SKBSYITERM_MON " +
				"        INNER JOIN  ( " +
				"SELECT SGJSKNJT.EMPLOYEE_ID,SGJSKNJT.RECORD_DATE,'0' as NONAPP " +
				"FROM WWFDT_CONF_ROUTE SGJSKNJT " +
				"LEFT JOIN KRCDT_MON_SELF_CHECK MONKN " +
				"ON       ( SGJSKNJT.CID = MONKN.CID " +
				"  AND SGJSKNJT.EMPLOYEE_ID = MONKN.SID " +
				"  AND SGJSKNJT.CLOSURE_ID  = MONKN.CLOSURE_ID " +
				"  AND SGJSKNJT.YEARMONTH   = MONKN.PROCESS_YM   ) " +
				"WHERE @endDate = SGJSKNJT.RECORD_DATE " +
				"AND SGJSKNJT.ROOT_TYPE = '2' " +
				"AND MONKN.IDENTIFY_DATE IS NULL " +
				") HONKN_MON " +
				"        ON SKBSYITERM_MON.SID  = HONKN_MON.EMPLOYEE_ID " +
				") MSNSHIN " +
				"GROUP BY MSNSHIN.WORKPLACE_ID;";
		List<Pair<String, Integer>> listPair = new NtsStatement(sql, this.jdbcProxy())
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WORKPLACE_ID");
					Integer count = rec.getInt("COUNTSID");
					return Pair.of(wkpID, count);
				});
		for(Pair<String, Integer> pair : listPair) {
			result.put(pair.getLeft(), pair.getRight());
		}
		return result;
	}

	@Override
	public Map<String, Pair<String, GeneralDateTime>> getCountWorkConfirm(ClosureId closureId, YearMonth yearMonth, String companyID, List<String> wkpIDLst) {
		Map<String, Pair<String, GeneralDateTime>> result = new HashMap<>();
		String sql = 
				"SELECT  KWF.WKPID,KWF.CONFIRM_SID,KWF.CONFIRM_DATE_TIME " + 
				"FROM  KRCDT_WORK_FIXED KWF " + 
				"WHERE KWF.CLOSURE_ID = @closureId " +
				"AND   KWF.PROCESS_YM = @yearMonth " +
				"AND   KWF.CID        = @companyID " +
				"AND   KWF.WKPID IN @wkpIDLst ;";
		new NtsStatement(sql, this.jdbcProxy())
				.paramInt("closureId", closureId.value)
				.paramInt("yearMonth", yearMonth.v())
				.paramString("companyID", companyID)
				.paramString("wkpIDLst", wkpIDLst)
				.getList(rec -> {
					String wkpID = rec.getString("WKPID");
					String confirmPID = rec.getString("CONFIRM_SID");
					GeneralDateTime fixedDate = rec.getGeneralDateTime("CONFIRM_DATE_TIME");
					result.put(wkpID, Pair.of(confirmPID, fixedDate));
					return null;
				});
		return result;
	}

	@Override
	public List<EmpPeriod> getMailCountUnApprApp(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		String sql = 
				"SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID,SKBSYITERM.WORK_ST,SKBSYITERM.WORK_ED,SKBSYITERM.KYO_ST,SKBSYITERM.KYO_ED " +
				"FROM ( " +
				"                SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                FROM ( " +
				"                        SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                        FROM ( " +
				"                                        SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                        FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                        INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                        ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                        WHERE        @startDate <= SSR.END_DATE " +
				"                                        AND          SSR.START_DATE <= @endDate " +
				"                                        AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +          
				"                                    ) SKBSYIN " +
				"                        INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                        ON               SKBSYIN.SID = SKR.SID " +
				"                        INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                        ON               SDKJ.SID = SKR.SID " +
				"                        WHERE            @startDate <= SKR.END_DATE " +
				"                        AND              SKR.START_DATE <= @endDate " +
				"                        AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                ) SKBSYINIO " +
				"                LEFT JOIN  ( " +
				"                        SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                        FROM BSYMT_AFF_EMP_HIST KR " +
				"                        INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                        ON KR.HIST_ID = KRK.HIST_ID " +
				"                        WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                        AND @startDate <= KR.END_DATE " +
				"                        AND KR.START_DATE <= @endDate " +
				"                ) KYO " +
				"                ON SKBSYINIO.SID = KYO.SID " +
				"            ) SKBSYITERM " +
				"INNER JOIN  ( " +
				"                        SELECT             SRI.EMPLOYEE_ID as APP_SID,SRI.APPROVAL_RECORD_DATE as APP_DATE,SRI.ROOT_STATE_ID " +
				"                        FROM               WWFDT_APP_INST_ROUTE SRI " +
				"                        INNER JOIN         WWFDT_APP_INST_PHASE SFI " +
				"                        ON                 SRI.ROOT_STATE_ID = SFI.ROOT_STATE_ID " +
				"                        WHERE              SFI.APP_PHASE_ATR IN ('0','3','4') " +
				"                        AND                @startDate <= SRI.APPROVAL_RECORD_DATE " +
				"                        AND                SRI.APPROVAL_RECORD_DATE <= @endDate " +
				"                        ) MSNSNS " +
				"ON              SKBSYITERM.SID  = MSNSNS.APP_SID " +
				"WHERE        SKBSYITERM.WORK_ST <= MSNSNS.APP_DATE " +
				"AND             SKBSYITERM.COMP_ST <= MSNSNS.APP_DATE " +
				"AND             SKBSYITERM.KYO_ST <= MSNSNS.APP_DATE " +
				"AND             MSNSNS.APP_DATE <= SKBSYITERM.WORK_ED " +
				"AND             MSNSNS.APP_DATE <= SKBSYITERM.COMP_ED " +
				"AND             MSNSNS.APP_DATE <= SKBSYITERM.KYO_ED;";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					return new EmpPeriod(
							rec.getString("WORKPLACE_ID"), 
							rec.getString("SID"), 
							"", 
							null, 
							null, 
							rec.getGeneralDate("WORK_ST"), 
							rec.getGeneralDate("WORK_ED"), 
							rec.getGeneralDate("KYO_ST"), 
							rec.getGeneralDate("KYO_ED"), 
							"", 
							"");
		});
	}

	@Override
	public List<Pair<String, String>> getMailCountUnConfirmDay(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		List<Pair<String, String>> result = new ArrayList<>();
		String sql = 
				"SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID " +
				"FROM        ( " +
				"                    SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                    FROM ( " +
				"                                        SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                                        FROM ( " +
				"                                                        SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                                        FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                                        INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                                        ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                                        WHERE        @startDate <= SSR.END_DATE " +
				"                                                        AND          SSR.START_DATE <= @endDate " +
				"　                                              　　　　　　AND          SSRK.WORKPLACE_ID IN @wkpIDLst " +
				"                                                    ) SKBSYIN " +
				"                                        INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                        ON               SKBSYIN.SID = SKR.SID " +
				"                                        INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                        ON               SDKJ.SID = SKR.SID " +
				"                                        WHERE            @startDate <= SKR.END_DATE " +
				"                                        AND              SKR.START_DATE <= @endDate " +
				"                                        AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                            ) SKBSYINIO " +
				"                    INNER JOIN  ( " +
				"                                        SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                                        FROM BSYMT_AFF_EMP_HIST KR " +
				"                                        INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                        ON KR.HIST_ID = KRK.HIST_ID " +
				"                                        WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                                        AND @startDate <= KR.END_DATE " +
				"                                        AND KR.START_DATE <= @endDate " +
				"                                        ) KYO " +
				"                    ON SKBSYINIO.SID = KYO.SID " +
				"                    )  SKBSYITERM " +
				"INNER JOIN   ( " +
				"                        SELECT             SGJSKNJT.EMPLOYEE_ID as SYAIN_ID,SGJSKNJT.RECORD_DATE as TG_DAY,'0' as NON_APP " +
				"                        FROM               WWFDT_CONF_ROUTE SGJSKNJT " +
				"                        LEFT JOIN          KRCDT_DAY_SELF_CHECK DAYKN " +
				"                        ON       (         SGJSKNJT.CID = DAYKN.CID " +
				"                                AND        SGJSKNJT.EMPLOYEE_ID = DAYKN.SID " +
				"                                AND        SGJSKNJT.RECORD_DATE = DAYKN.PROCESSING_YMD   ) " +
				"                        WHERE              @startDate <= SGJSKNJT.RECORD_DATE " +
				"                        AND                SGJSKNJT.RECORD_DATE <= @endDate " +
				"                        AND                SGJSKNJT.ROOT_TYPE = '1' " +
				"                        AND                DAYKN.INDENTIFICATION_YMD IS NULL " +
				"                        ) HNKN " +
				"ON              SKBSYITERM.SID  = HNKN.SYAIN_ID " +
				"WHERE           SKBSYITERM.WORK_ST <= HNKN.TG_DAY " +
				"AND             SKBSYITERM.COMP_ST <= HNKN.TG_DAY " +
				"AND             SKBSYITERM.KYO_ST <= HNKN.TG_DAY " +
				"AND             HNKN.TG_DAY <= SKBSYITERM.WORK_ED " +
				"AND             HNKN.TG_DAY <= SKBSYITERM.COMP_ED " +
				"AND             HNKN.TG_DAY <= SKBSYITERM.KYO_ED;";
		new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					result.add(Pair.of(rec.getString("WORKPLACE_ID"), rec.getString("SID")));
					return null;
		});
		return result;
	}

	@Override
	public List<Pair<String, Pair<String, GeneralDate>>> getMailCountUnApprDay(GeneralDate startDate, GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		List<Pair<String, Pair<String, GeneralDate>>> result = new ArrayList<>();
		String sql = 
				"SELECT DISTINCT SKBSYITERM.WORKPLACE_ID,SKBSYITERM.SID,JCHOSN.RECORD_DATE " +
				"FROM  ( " +
				"                    SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                    FROM ( " +
				"                                SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                                FROM ( " +
				"                                            SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                            FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                            INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                            ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                            WHERE        @startDate <= SSR.END_DATE " +
				"                                            AND          SSR.START_DATE <= @endDate " +
				"                       　          　　　　　　AND          SSRK.WORKPLACE_ID IN @wkpIDLst          " +
				"                                        ) SKBSYIN " +
				"                                INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                ON               SKBSYIN.SID = SKR.SID " +
				"                                INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                ON               SDKJ.SID = SKR.SID " +
				"                                WHERE            @startDate <= SKR.END_DATE " +
				"                                AND              SKR.START_DATE <= @endDate " +
				"                                AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                        ) SKBSYINIO " +
				"                    INNER JOIN  ( " +
				"                            SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                            FROM BSYMT_AFF_EMP_HIST KR " +
				"                            INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                            ON KR.HIST_ID = KRK.HIST_ID " +
				"                            WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                            AND @startDate <= KR.END_DATE " +
				"                            AND KR.START_DATE <= @endDate " +
				"                    ) KYO " +
				"                    ON SKBSYINIO.SID = KYO.SID " +
				"            )  SKBSYITERM " +
				"INNER JOIN  ( " +
				"                        SELECT   MIDFOD.EMPLOYEE_ID,SGJFOD.RECORD_DATE,'0' as NONAPP      " + 
				"                        FROM   ( " +
				"                                        SELECT     SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF,SNRMID.START_DATE,SNRMID.END_DATE " +
				"                                        FROM       WWFDT_INST_ROUTE SNRMID " +
				"                                        INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                        ON         SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                        WHERE      @startDate <= SNRMID.END_DATE " +
				"                                        AND SNRMID.START_DATE <= @endDate " +
				"            AND SNRMID.ROOT_TYPE = '1' " +
				"            GROUP BY SNRMID.EMPLOYEE_ID,SNRMID.START_DATE,SNRMID.END_DATE " +
				"        ) MIDFOD " +
				"INNER JOIN ( SELECT SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE,SYNZMF.PHASE_ORDER,SYNZMF.APP_PHASE_ATR " +
				"FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"INNER JOIN WWFDT_CONF_PHASE SYNZMF " +
				"ON SGJKNJT.ROOT_ID = SYNZMF.ROOT_ID " +
				"WHERE SGJKNJT.ROOT_TYPE = '1' " +
				"AND @startDate <= SGJKNJT.RECORD_DATE " +
				"AND SGJKNJT.RECORD_DATE <= @endDate " +
				"        ) SGJFOD " +
				"ON MIDFOD.EMPLOYEE_ID = SGJFOD.EMPLOYEE_ID " +
				"WHERE MIDFOD.FINF  = SGJFOD.PHASE_ORDER " +
				"AND MIDFOD.START_DATE <= SGJFOD.RECORD_DATE " +
				"AND SGJFOD.RECORD_DATE <= MIDFOD.END_DATE " +
				"AND SGJFOD.APP_PHASE_ATR  IN ('0','3') " +
				"UNION ALL " +
				"SELECT SGJKN.EMPLOYEE_ID,SGJKN.RECORD_DATE,ISNULL(SYNZMF.APP_PHASE_ATR,'0') as NONAPP " +
				"FROM          ( SELECT MIDFOD.EMPLOYEE_ID,SGJT.RECORD_DATE,SGJT.ROOT_ID,MIDFOD.FINF " +
				"FROM      ( " +
				"        SELECT SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF,SNRMID.START_DATE,SNRMID.END_DATE " +
				"        FROM WWFDT_INST_ROUTE SNRMID " +
				"        INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"        ON SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"        WHERE @startDate <= SNRMID.END_DATE " +
				"        AND SNRMID.START_DATE <= @endDate " +
				"        AND SNRMID.ROOT_TYPE = '1' " +
				"        GROUP BY SNRMID.EMPLOYEE_ID,SNRMID.START_DATE,SNRMID.END_DATE " +
				"        ) MIDFOD " +
				"INNER JOIN ( SELECT SGJKNJT.ROOT_ID,SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE " +
				"                                                                            FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                                            WHERE @startDate <= SGJKNJT.RECORD_DATE " +
				"                                                                            AND SGJKNJT.RECORD_DATE <= @endDate " +
				"                                                                            AND SGJKNJT.ROOT_TYPE = '1'      ) SGJT " +
				"ON MIDFOD.EMPLOYEE_ID = SGJT.EMPLOYEE_ID " +
				"WHERE MIDFOD.START_DATE <= SGJT.RECORD_DATE " +
				"AND SGJT.RECORD_DATE <= MIDFOD.END_DATE     ) SGJKN " +
				"LEFT JOIN WWFDT_CONF_PHASE SYNZMF " +
				"ON      ( SGJKN.ROOT_ID = SYNZMF.ROOT_ID " +
				"AND SGJKN.FINF = SYNZMF.PHASE_ORDER      ) " +
				"WHERE SYNZMF.APP_PHASE_ATR IS NULL       " +
				"        ) JCHOSN " +
				"ON SKBSYITERM.SID = JCHOSN.EMPLOYEE_ID " +
				"WHERE SKBSYITERM.WORK_ST <= JCHOSN.RECORD_DATE " +
				"AND SKBSYITERM.COMP_ST <= JCHOSN.RECORD_DATE " +
				"AND SKBSYITERM.KYO_ST <= JCHOSN.RECORD_DATE " +
				"AND JCHOSN.RECORD_DATE <= SKBSYITERM.WORK_ED " +
				"AND JCHOSN.RECORD_DATE <= SKBSYITERM.COMP_ED " +
				"AND JCHOSN.RECORD_DATE <= SKBSYITERM.KYO_ED; ";
		new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					result.add(Pair.of(rec.getString("WORKPLACE_ID"), Pair.of(rec.getString("SID"), rec.getGeneralDate("RECORD_DATE"))));
					return null;
		});
		return result;
	}

	@Override
	public List<Pair<String, String>> getMailCountUnConfirmMonth(GeneralDate endDate, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		List<Pair<String, String>> result = new ArrayList<>();
		String sql = 
				"SELECT DISTINCT SKBSYITERM_MON.WORKPLACE_ID,SKBSYITERM_MON.SID " +
				"FROM   ( " +
				"                    SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD " +
				"                    FROM ( " +
				"                                    SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID " +
				"                                    FROM ( " +
				"                                                SELECT       SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                                FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                                INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                                ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                                WHERE        @endDate <= SSR.END_DATE " +
				"                                                AND          SSR.START_DATE <= @endDate " +
				"                                                AND          SSRK.WORKPLACE_ID IN  @wkpIDLst " +
				"                                            )  SKBSYIN " +
				"                                    INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                    ON               SKBSYIN.SID = SKR.SID " +
				"                                    INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                    ON               SDKJ.SID = SKR.SID " +
				"                                    WHERE            @endDate <= SKR.END_DATE " +
				"                                    AND              SKR.START_DATE <= @endDate " +
				"                                    AND              SDKJ.DEL_STATUS_ATR = '0' " +
				"                            ) SKBSYINIO " +
				"                    INNER JOIN  ( " +
				"                                            SELECT         KRK.SID,KRK.EMP_CD " +
				"                                            FROM           BSYMT_AFF_EMP_HIST KR " +
				"                                            INNER JOIN     BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                            ON             KR.HIST_ID = KRK.HIST_ID " +
				"                                            WHERE          KRK.EMP_CD IN  @employmentCDLst " +
				"                                            AND            @endDate <= KR.END_DATE " +
				"                                            AND            KR.START_DATE <= @endDate " +
				"                                        ) KYO " +
				"                    ON     SKBSYINIO.SID = KYO.SID " +
				"                ) SKBSYITERM_MON " +
				"INNER JOIN  ( " +
				"                                SELECT SGJSKNJT.EMPLOYEE_ID,SGJSKNJT.RECORD_DATE,'0' as NONAPP " +
				"                                FROM               WWFDT_CONF_ROUTE SGJSKNJT " +
				"                                LEFT JOIN        KRCDT_MON_SELF_CHECK MONKN " +
				"ON       (   SGJSKNJT.CID = MONKN.CID " +
				"            AND  SGJSKNJT.EMPLOYEE_ID = MONKN.SID " +
				"            AND  SGJSKNJT.CLOSURE_ID  = MONKN.CLOSURE_ID " +
				"            AND  SGJSKNJT.YEARMONTH   = MONKN.PROCESS_YM   ) " +
				"WHERE @endDate = SGJSKNJT.RECORD_DATE " +
				"AND SGJSKNJT.ROOT_TYPE = '2' " +
				"AND MONKN.IDENTIFY_DATE IS NULL " +
				") HONKN_MON " +
				"ON SKBSYITERM_MON.SID  = HONKN_MON.EMPLOYEE_ID;";
		new NtsStatement(sql, this.jdbcProxy())
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					result.add(Pair.of(rec.getString("WORKPLACE_ID"), rec.getString("SID")));
					return null;
		});
		return result;
	}

	@Override
	public List<Pair<String, String>> getMailCountUnApprMonth(GeneralDate endDate, YearMonth yearMonth, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		List<Pair<String, String>> result = new ArrayList<>();
		String sql = 
				"SELECT DISTINCT SKBSYITERM_MON.WORKPLACE_ID,SKBSYITERM_MON.SID " +
				"FROM                ( " +
				"                        SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD " +
				"                        FROM ( " +
				"                                SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID " +
				"                                FROM ( " +
				"                                        SELECT       SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                        FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                        INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                        ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                        WHERE        @endDate <= SSR.END_DATE " +
				"                                        AND          SSR.START_DATE <= @endDate " +
				"                                        AND          SSRK.WORKPLACE_ID IN  @wkpIDLst " +
				"                                        ) SKBSYIN " +
				"                                INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                ON               SKBSYIN.SID = SKR.SID " +
				"                                INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                ON               SDKJ.SID = SKR.SID " +
				"                                WHERE            @endDate <= SKR.END_DATE " +
				"                                AND              SKR.START_DATE <= @endDate " +
				"                                AND              SDKJ.DEL_STATUS_ATR = '0' " +
				"                        ) SKBSYINIO " +
				"                        INNER JOIN  ( " +
				"                                SELECT         KRK.SID,KRK.EMP_CD " +
				"                                FROM           BSYMT_AFF_EMP_HIST KR " +
				"                                INNER JOIN     BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                ON             KR.HIST_ID = KRK.HIST_ID " +
				"                                WHERE          KRK.EMP_CD IN  @employmentCDLst " +
				"                                AND            @endDate <= KR.END_DATE " +
				"                                AND            KR.START_DATE <= @endDate " +
				"                        ) KYO " +
				"                        ON             SKBSYINIO.SID = KYO.SID " +
				"                ) SKBSYITERM_MON " +
				"INNER JOIN      ( " +
				"                    SELECT                MIDFOD.EMPLOYEE_ID,SGJFOD.RECORD_DATE,'0' as NONAPP " +
				"                    FROM       ( " +
				"                                                    SELECT SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF " +
				"                                                    FROM WWFDT_INST_ROUTE SNRMID " +
				"                                                    INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                                    ON             SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                                    WHERE          @endDate <= SNRMID.END_DATE " +
				"                                                    AND SNRMID.START_DATE <= @endDate " +
				"                                                    AND SNRMID.ROOT_TYPE = '2' " +
				"                                                    GROUP BY SNRMID.EMPLOYEE_ID " +
				"                                            ) MIDFOD " +
				"                    INNER JOIN (    " +
				"                                                    SELECT SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE,SYNZMF.PHASE_ORDER,SYNZMF.APP_PHASE_ATR " +
				"                                                    FROM WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                    INNER JOIN WWFDT_CONF_PHASE SYNZMF " +
				"                                                    ON SGJKNJT.ROOT_ID = SYNZMF.ROOT_ID " +
				"                                                    WHERE SGJKNJT.ROOT_TYPE = '2' " +
				"                                                    AND            @yearMonth = SGJKNJT.YEARMONTH " +
				"                                            ) SGJFOD " +
				"                    ON      MIDFOD.EMPLOYEE_ID = SGJFOD.EMPLOYEE_ID " +
				"                    WHERE   MIDFOD.FINF  = SGJFOD.PHASE_ORDER " +
				"                    AND        SGJFOD.APP_PHASE_ATR IN ('0','3') " +
				"                    UNION ALL " +
				"                    SELECT      SGJKN.EMPLOYEE_ID,SGJKN.RECORD_DATE,ISNULL(SYNZMF.APP_PHASE_ATR,'0') as NONAPP " +
				"                    FROM        ( " +
				"                                                    SELECT  MIDFOD.EMPLOYEE_ID,SGJT.RECORD_DATE,SGJT.ROOT_ID,MIDFOD.FINF " +
				"                                                    FROM        (   SELECT     SNRMID.EMPLOYEE_ID,MAX(SNFMID.PHASE_ORDER) as FINF " +
				"                                                                                    FROM       WWFDT_INST_ROUTE SNRMID " +
				"                                                                                    INNER JOIN WWFDT_INST_PHASE SNFMID " +
				"                                                                                    ON         SNRMID.ROOT_ID = SNFMID.ROOT_ID " +
				"                                                                                    WHERE      @endDate <= SNRMID.END_DATE " +
				"                                                                                    AND        SNRMID.START_DATE <= @endDate " +
				"                                                                                    AND        SNRMID.ROOT_TYPE = '2' " +
				"                                                                                    GROUP BY   SNRMID.EMPLOYEE_ID " +
				"                                                                            ) MIDFOD " +
				"                                                    INNER JOIN  (     SELECT     SGJKNJT.ROOT_ID,SGJKNJT.EMPLOYEE_ID,SGJKNJT.RECORD_DATE " +
				"                                                                                FROM       WWFDT_CONF_ROUTE SGJKNJT " +
				"                                                                                WHERE      SGJKNJT.ROOT_TYPE = '2' " +
				"                                                                                AND        @yearMonth = SGJKNJT.YEARMONTH " +
				"                                                                            ) SGJT " +
				"                                                    ON      MIDFOD.EMPLOYEE_ID = SGJT.EMPLOYEE_ID " +
				"                                        ) SGJKN " +
				"                    LEFT JOIN   WWFDT_CONF_PHASE SYNZMF " +
				"                    ON        (        SGJKN.ROOT_ID = SYNZMF.ROOT_ID " +
				"                    			AND   SGJKN.FINF = SYNZMF.PHASE_ORDER   ) " +
				"                    WHERE       SYNZMF.APP_PHASE_ATR  IS NULL " +
				"                ) JCHOSN_M " +
				"ON              SKBSYITERM_MON.SID  = JCHOSN_M.EMPLOYEE_ID;";
		new NtsStatement(sql, this.jdbcProxy())
				.paramDate("endDate", endDate)
				.paramInt("yearMonth", yearMonth.v())
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.getList(rec -> {
					result.add(Pair.of(rec.getString("WORKPLACE_ID"), rec.getString("SID")));
					return null;
		});
		return result;
	}

	@Override
	public List<String> getMailCountWorkConfirm(GeneralDate startDate, GeneralDate endDate, ClosureId closureId, YearMonth yearMonth, 
			String companyID, List<String> wkpIDLst, List<String> employmentCDLst) {
		if(CollectionUtil.isEmpty(wkpIDLst)) {
			return Collections.emptyList();
		}
		String sql = 
				"SELECT    DISTINCT  SKBSYITERM.WORKPLACE_ID " +
				"FROM       ( " +
				"                   SELECT SKBSYINIO.WORKPLACE_ID,SKBSYINIO.SID,KYO.EMP_CD,SKBSYINIO.WORK_ST,SKBSYINIO.WORK_ED,SKBSYINIO.COMP_ST,SKBSYINIO.COMP_ED,KYO.START_DATE as KYO_ST,KYO.END_DATE as KYO_ED " +
				"                   FROM ( " +
				"                                   SELECT SKBSYIN.WORKPLACE_ID,SKBSYIN.SID,SKBSYIN.START_DATE as WORK_ST,SKBSYIN.END_DATE as WORK_ED,SKR.START_DATE as COMP_ST,SKR.END_DATE as COMP_ED " +
				"                                   FROM ( " +
				"                                                  SELECT SSRK.WORKPLACE_ID,SSRK.SID,SSR.START_DATE,SSR.END_DATE " +
				"                                                  FROM         BSYMT_AFF_WKP_HIST SSR " +
				"                                                  INNER JOIN   BSYMT_AFF_WKP_HIST_ITEM SSRK " +
				"                                                  ON           SSR.HIST_ID = SSRK.HIST_ID " +
				"                                                  WHERE        @startDate <= SSR.END_DATE " +
				"                                                  AND          SSR.START_DATE <= @endDate " +
				"　                                  　　　　　　AND          SSRK.WORKPLACE_ID IN @wkpIDLst        " +
				"                                                ) SKBSYIN " +
				"                                   INNER JOIN       BSYMT_AFF_COM_HIST SKR " +
				"                                   ON               SKBSYIN.SID = SKR.SID " +
				"                                   INNER JOIN       BSYMT_SYAIN SDKJ " +
				"                                   ON               SDKJ.SID = SKR.SID " +
				"                                   WHERE            @startDate <= SKR.END_DATE " +
				"                                   AND              SKR.START_DATE <= @endDate " +
				"                                   AND SDKJ.DEL_STATUS_ATR = '0' " +
				"                             ) SKBSYINIO " +
				"                   INNER JOIN  ( " +
				"                                         SELECT KRK.SID,KRK.EMP_CD,KR.START_DATE,KR.END_DATE " +
				"                                         FROM BSYMT_AFF_EMP_HIST KR " +
				"                                         INNER JOIN BSYMT_AFF_EMP_HIST_ITEM KRK " +
				"                                         ON KR.HIST_ID = KRK.HIST_ID " +
				"                                         WHERE KRK.EMP_CD IN @employmentCDLst " +
				"                                         AND @startDate <= KR.END_DATE " +
				"                                         AND KR.START_DATE <= @endDate " +
				"                                      ) KYO " +
				"                   ON SKBSYINIO.SID = KYO.SID " +
				"                ) SKBSYITERM " +
				"LEFT JOIN  ( " +
				"		SELECT    KWF.WKPID " +
				"		FROM      KRCDT_WORK_FIXED KWF " +
				"		WHERE KWF.CLOSURE_ID = @closureId " +
				"		AND   KWF.PROCESS_YM = @yearMonth " +
				"		AND   KWF.CID        = @companyID " +
				"		) SGK " +
				"ON         SGK.WKPID = SKBSYITERM.WORKPLACE_ID " +
				"WHERE    SGK.WKPID IS NULL;";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramString("wkpIDLst", wkpIDLst)
				.paramString("employmentCDLst", employmentCDLst)
				.paramInt("closureId", closureId.value)
				.paramInt("yearMonth", yearMonth.v())
				.paramString("companyID", companyID)
				.getList(rec -> {
					return rec.getString("WORKPLACE_ID");
		});
	}
}
