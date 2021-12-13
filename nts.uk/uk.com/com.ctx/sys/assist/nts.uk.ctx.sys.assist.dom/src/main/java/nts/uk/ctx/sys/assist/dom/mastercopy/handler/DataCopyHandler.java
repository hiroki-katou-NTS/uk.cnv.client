package nts.uk.ctx.sys.assist.dom.mastercopy.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.assist.dom.mastercopy.CopyMethod;
import nts.uk.shr.com.constants.DefaultSettingKeys;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Interface CopyHandler.
 */
@Setter
@Getter
public class DataCopyHandler {

    /**
     * ゼロ会社コピー用のSELECTで指定する契約コードと会社IDの位置
     * CONTRACT_CDはあとから追加したため、修正の影響を抑えるために会社IDは 0 のまま、契約コードを 1 とする
     */
    private static final int SOURCE_COLUMN_CONTRACT_CD = 1;
    private static final int SOURCE_COLUMN_CID = 0;
    
    /**
     * 上記の2カラムを除いたデータ本体の開始カラム位置
     */
    private static final int SOURCE_START_COLUMN = 2;
    
    /**
     * クエリパラメータの開始インデックス
     */
    private static final int QUERY_PARAM_START_INDEX = 1;

    private static final int INS_DATE_COLL = SOURCE_START_COLUMN;
    private static final int INS_CCD_COLL = INS_DATE_COLL + 1;
    private static final int INS_SCD_COLL = INS_CCD_COLL + 1;
    private static final int INS_PG_COLL = INS_SCD_COLL + 1;
    private static final int UDP_DATE_COLL = INS_PG_COLL + 1;
    private static final int UDP_CCD_COLL = UDP_DATE_COLL + 1;
    private static final int UDP_SCD_COLL = UDP_CCD_COLL + 1;
    private static final int UDP_PG_COLL = UDP_SCD_COLL + 1;
    private static final int EXCLUS_VER_COLL = UDP_PG_COLL + 1;

    /**
     * Logger
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(DataCopyHandler.class);

    /**
     * The entity manager.
     */
    protected EntityManager entityManager;
    /**
     * The copy method.
     */
    protected CopyMethod copyMethod;
    /**
     * The contract code.
     */
    protected String contractCode;
    /**
     * The company Id.
     */
    protected String companyId;
    /**
     * The lít key.
     */
    private List<String> keys = new ArrayList<>();
    /**
     * The table Name.
     */
    private String tableName;
    /**
     * The select by cid query.
     */
    private String selectQuery;
    /**
     * The delete by cid query.
     */
    private String deleteQuery;
    /**
     *
     */
    private boolean isOnlyCid;
    /**
     * Do copy.
     */
    @SuppressWarnings("unchecked")
    public void doCopy() {
        // Get all company zero data
    	// ゼロ契約ゼロ会社から初期値をSELECT
        Query sq = this.entityManager.createNativeQuery(this.selectQuery)
                .setParameter(1, DefaultSettingKeys.COMPANY_ID);
        List<Object> sourceObjects = sq.getResultList();
        if (sourceObjects.isEmpty()) return;

        int sourceSize = sourceObjects.size();
        int keySize = keys.size();
        int keyCheck = keySize - 1;

        // 既存データをSELECT
        Query selectQueryTarget = this.entityManager.createNativeQuery(this.selectQuery)
        		.setParameter(1, this.companyId);
        List<Object> oldDatas = selectQueryTarget.getResultList();

        switch (copyMethod) {
            case REPLACE_ALL:
            	// 既存データをDELETE（会社IDのみ指定で全削除）
                Query dq = this.entityManager.createNativeQuery(this.deleteQuery)
                		.setParameter(1, this.companyId);
                dq.executeUpdate();
            case DO_NOTHING:
                if(copyMethod != CopyMethod.REPLACE_ALL && !oldDatas.isEmpty()){
                    return;
                }
            case ADD_NEW:
                if (copyMethod == CopyMethod.ADD_NEW) {
                    // ignore data existed
                    for (int i = 0; i < sourceSize; i++) {
                        Object[] dataAttr = (Object[]) sourceObjects.get(i);
                        // =1 key
                        if (keyCheck == 0 && !CollectionUtil.isEmpty(oldDatas)) {
                            sourceObjects.remove(i);
                            sourceSize--;
                            break;
                        }
                        for (int j = 0; j < oldDatas.size(); j++) {
                            Object[] targetAttr = (Object[]) oldDatas.get(j);
                            // compare keys and remove
                            int countDiff = 0;
                            for (int k = dataAttr.length - keyCheck; k < dataAttr.length; k++) {
                                if (dataAttr[k] == null && targetAttr[k] == null || (dataAttr[k] != null && dataAttr[k].equals(targetAttr[k]))
                                        || (targetAttr[k] != null && targetAttr[k].equals(dataAttr[k]))) {
                                    countDiff++;
                                }
                                if (countDiff == keyCheck) {
                                    sourceObjects.remove(i);
                                    i--;
                                    sourceSize--;
                                    break;
                                }
                            }
                        }
                    }
                }

                String insertQueryString = "";
                for (int i = 0; i < sourceSize; i++) {

                    Object[] rowData = (Object[]) sourceObjects.get(i);

                    StringJoiner joiner = new StringJoiner(",");
                    for (int k = SOURCE_START_COLUMN; k < rowData.length - keyCheck; k++) {
                        if (rowData[SOURCE_COLUMN_CID].equals(rowData[k])) {
                            rowData[k] = companyId;
                        } else if (rowData[SOURCE_COLUMN_CONTRACT_CD].equals(rowData[k])) {
                            rowData[k] = contractCode;;
                        } else if (k == INS_DATE_COLL || k == UDP_DATE_COLL) {
                            rowData[k] = Timestamp.valueOf(GeneralDateTime.now().localDateTime());
                        } else if (k == INS_CCD_COLL || k == UDP_CCD_COLL) {
                            rowData[k] = AppContexts.user().companyCode();
                        } else if (k == INS_SCD_COLL || k == UDP_SCD_COLL) {
                            rowData[k] = AppContexts.user().employeeCode();
                        } else if (k == INS_PG_COLL || k == UDP_PG_COLL) {
                            rowData[k] = "CMM001";
                        }

                        if (!isOnlyCid && k > EXCLUS_VER_COLL) {
                            for (int n = rowData.length - keyCheck; n < rowData.length; n++) {
                                if (rowData[n].equals(rowData[k])) {
                                    rowData[k] = UUID.randomUUID().toString();
                                }
                            }
                        }
                        joiner.add(param(rowData[k]));
                    }

                    insertQueryString = "INSERT INTO " + this.tableName + " VALUES (" + joiner.toString() + ")";

                    if (StringUtils.isEmpty(insertQueryString)) {
                    	continue;
                    }

                    Query iq = this.entityManager.createNativeQuery(insertQueryString);
                    iq.executeUpdate();
                }

            default:
                break;
        }
    }

    private static String param(Object value) {

        if (value == null) {
            return "null";
        }

        Class<?> type = value.getClass();

        if (type == String.class) {
            return "'" + value + "'";
        }

        if (type == Timestamp.class || type == Date.class) {
            return "'" + value + "'";
        }

        return "" + value;
    }

    public static final class DataCopyHandlerBuilder {
        EntityManager entityManager;
        protected CopyMethod copyMethod;
        protected String contractCode;
        protected String companyId;
        private List<String> keys = new ArrayList<>();
        private String tableName;
        private boolean condKey = false;
        private boolean condTable= false;
        private String selectQuery;
        private String deleteQuery;
        private boolean isOnlyCid;

        private DataCopyHandlerBuilder() {
        }

        public static DataCopyHandlerBuilder aDataCopyHandler() {
            return new DataCopyHandlerBuilder();
        }

        public DataCopyHandlerBuilder withEntityManager(EntityManager entityManager) {
            this.entityManager = entityManager;
            return this;
        }

        public DataCopyHandlerBuilder withCopyMethod(CopyMethod copyMethod) {
            this.copyMethod = copyMethod;
            return this;
        }

        public DataCopyHandlerBuilder withCompanyId(String contractCode, String companyId) {
        	this.contractCode = contractCode;
            this.companyId = companyId;
            return this;
        }

        public DataCopyHandlerBuilder withKeys(List<String> keys) {
            this.keys = keys;
            this.condKey = true;
            return this;
        }

        public DataCopyHandlerBuilder withTableName(String tableName) {
            this.tableName = tableName;
            this.condTable = true;
            return this;
        }

        public DataCopyHandlerBuilder withOnlyCid(boolean isOnlyCid) {
            this.isOnlyCid = isOnlyCid;
            return this;
        }

        public DataCopyHandlerBuilder buildQuery() {
            if (condKey && condTable) {
                StringJoiner joinerTail = new StringJoiner(",");
                String tail = "";
                if (keys.size() > 1) {
                    for (int i=1;i<keys.size();i++){
                        joinerTail.add(keys.get(i));
                    }
                    tail +=", "+joinerTail.toString();
                }

                this.selectQuery = "SELECT " + keys.get(0) + " , CONTRACT_CD, *" + tail + " FROM " + tableName + " WHERE CID = ?";
                this.deleteQuery = "DELETE FROM " + tableName + " WHERE CID  = ?";
            }
            return this;
        }

        public DataCopyHandler build() {
            DataCopyHandler dataCopyHandler = new DataCopyHandler();
            dataCopyHandler.setEntityManager(entityManager);
            dataCopyHandler.setCopyMethod(copyMethod);
            dataCopyHandler.setContractCode(contractCode);
            dataCopyHandler.setCompanyId(companyId);
            dataCopyHandler.setTableName(tableName);
            dataCopyHandler.setKeys(keys);
            dataCopyHandler.setSelectQuery(selectQuery);
            dataCopyHandler.setDeleteQuery(deleteQuery);
            dataCopyHandler.setOnlyCid(isOnlyCid);
            return dataCopyHandler;
        }
    }
}
