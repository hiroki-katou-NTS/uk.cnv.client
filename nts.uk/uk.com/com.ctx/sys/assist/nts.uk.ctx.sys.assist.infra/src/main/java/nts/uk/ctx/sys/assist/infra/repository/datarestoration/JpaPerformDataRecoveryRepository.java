package nts.uk.ctx.sys.assist.infra.repository.datarestoration;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.assist.dom.datarestoration.PerformDataRecovery;
import nts.uk.ctx.sys.assist.dom.datarestoration.PerformDataRecoveryRepository;
import nts.uk.ctx.sys.assist.dom.datarestoration.Target;
import nts.uk.ctx.sys.assist.dom.tablelist.TableList;
import nts.uk.ctx.sys.assist.infra.entity.datarestoration.SspmtPerformDataRecovery;
import nts.uk.ctx.sys.assist.infra.entity.datarestoration.SspmtRestorationTarget;
import nts.uk.ctx.sys.assist.infra.entity.datarestoration.SspmtTarget;
import nts.uk.ctx.sys.assist.infra.entity.tablelist.SspmtTableList;

@Stateless
public class JpaPerformDataRecoveryRepository extends JpaRepository implements PerformDataRecoveryRepository {


	private static final String SELECT_ALL_QUERY_STRING = "SELECT t FROM SspmtTableList t WHERE  t.tableListPk.categoryId =:categoryId AND storageRangeSaved =:storageRangeSaved ORDER BY DESC tableNo";

	private static final String SELECT_ALL_TARGET = "SELECT t FROM SspmtTarget t WHERE  t.targetPk.dataRecoveryProcessId =:dataRecoveryProcessId ";

	private static final String SELECT_INTERNAL_FILE_NAME = "SELECT t FROM SspmtTableList t WHERE  t.targetPk.dataRecoveryProcessId =:dataRecoveryProcessId AND t.internalFileName =:internalFileName ";

	private static final StringBuilder COUNT_BY_TABLE_SQL = new StringBuilder("SELECT count(*) from ");

	private static final StringBuilder DELETE_BY_TABLE_SQL = new StringBuilder("DELETE FROM ");

	private static final StringBuilder INSERT_BY_TABLE = new StringBuilder("INSERT INTO ");

	private static final String SELECT_ALL_TABLE_LIST_QUERY_STRING = "SELECT f FROM SspmtTableList f";
	private static final String SELECT_BY_RECOVERY_PROCESSING_ID_QUERY_STRING = "SELECT t FROM SspmtTableList t WHERE  t.dataRecoveryProcessId =:dataRecoveryProcessId";
	private static final String SELECT_TARGET_BY_DATA_RECOVERY_PROCESS_ID  = "SELECT t FROM SspmtTarget t WHERE t.targetPk.dataRecoveryProcessId=:dataRecoveryProcessId";
    private static final String SELECT_RESTORATION_TARGET_BY_DATA_RECOVERY_PROCESS_ID  = "SELECT st FROM SspmtRestorationTarget st WHERE st.restorationTargetPk.dataRecoveryProcessId=:dataRecoveryProcessId";

	@Override
    public Optional<PerformDataRecovery> getPerformDatRecoverById(String dataRecoveryProcessId) {
        List<SspmtTarget> targetData = this.queryProxy().query(SELECT_TARGET_BY_DATA_RECOVERY_PROCESS_ID, SspmtTarget.class).setParameter("dataRecoveryProcessId", dataRecoveryProcessId).getList();
        List<SspmtRestorationTarget> restorationTarget = this.queryProxy().query(SELECT_RESTORATION_TARGET_BY_DATA_RECOVERY_PROCESS_ID, SspmtRestorationTarget.class).setParameter("dataRecoveryProcessId", dataRecoveryProcessId).getList();
        return Optional.ofNullable(
                this.getEntityManager().find(SspmtPerformDataRecovery.class, dataRecoveryProcessId).toDomain(targetData, restorationTarget));
    }

	@Override
	public void add(PerformDataRecovery domain) {
		this.commandProxy().insert(SspmtPerformDataRecovery.toEntity(domain));
	}

	@Override
	public void update(PerformDataRecovery domain) {
		this.commandProxy().update(SspmtPerformDataRecovery.toEntity(domain));
	}

	@Override
	public void remove(String dataRecoveryProcessId) {
		this.commandProxy().remove(SspmtPerformDataRecovery.class, dataRecoveryProcessId);
	}

	@Override
	public List<TableList> getByStorageRangeSaved(String categoryId, String storageRangeSaved) {
		List<SspmtTableList> listTable = this.getEntityManager()
				.createQuery(SELECT_ALL_QUERY_STRING, SspmtTableList.class).setParameter("categoryId", categoryId)
				.setParameter("storageRangeSaved", storageRangeSaved).getResultList();

		return listTable.stream().map(SspmtTableList::toDomain).collect(Collectors.toList());
	}

	@Override
	public List<Target> findByDataRecoveryId(String dataRecoveryProcessId) {
		List<SspmtTarget> listTarget = this.getEntityManager().createQuery(SELECT_ALL_TARGET, SspmtTarget.class)
				.setParameter("dataRecoveryProcessId", dataRecoveryProcessId).getResultList();

		return listTarget.stream().map(SspmtTarget::toDomain).collect(Collectors.toList());
	}

	@Override
	public Optional<TableList> getByInternal(String internalFileName, String dataRecoveryProcessId) {
		return this.queryProxy().query(SELECT_INTERNAL_FILE_NAME, SspmtTableList.class)
				.setParameter("internalFileName", internalFileName)
				.setParameter("dataRecoveryProcessId", dataRecoveryProcessId).getSingle(SspmtTableList::toDomain);
	}

	@Override
	public int countDataExitTableByVKeyUp(Map<String, String> filedWhere, String tableName) {
		if (tableName != null) {
			COUNT_BY_TABLE_SQL.append(tableName).append(" WHERE ");
            COUNT_BY_TABLE_SQL.append(makeWhereClause(filedWhere));
            return (Integer) this.getEntityManager().createNativeQuery(COUNT_BY_TABLE_SQL.toString())
                    .getSingleResult();
		}
		return 0;
	}

    private StringBuilder makeWhereClause(Map<String, String> filedWhere) {
	    StringBuilder whereClause = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> filed : filedWhere.entrySet()) {
            if (!filed.getValue().isEmpty()) {
                i++;
                if (i != 0) {
                    whereClause.append(" AND ").append(filed.getKey()).append(" = ").append(filed.getValue());
                } else {
                    whereClause.append(filed.getKey()).append(" = ").append(filed.getValue());
                }
            }
        }
        return whereClause;
    }

    @Override
	public void deleteDataExitTableByVkey(Map<String, String> filedWhere, String tableName) {

		EntityManager em = this.getEntityManager();

		if (tableName != null) {
			DELETE_BY_TABLE_SQL.append(tableName).append(" WHERE ");
            DELETE_BY_TABLE_SQL.append(makeWhereClause(filedWhere));

            Query query = em.createNativeQuery(DELETE_BY_TABLE_SQL.toString());
            query.executeUpdate();
		}
	}

	@Override
	public void insertDataTable(Map<String, String> dataInsertDB, String tableName) {
		if (tableName != null) {
			INSERT_BY_TABLE.append(tableName);
		}
		EntityManager em = this.getEntityManager();
		List<String> columns = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (Map.Entry<String, String> filed : dataInsertDB.entrySet()) {
			columns.add(filed.getKey());
			values.add(filed.getValue());
		}
		INSERT_BY_TABLE.append(columns);
		INSERT_BY_TABLE.append(" VALUES ");
		INSERT_BY_TABLE.append(values);
		Query query = em.createNativeQuery(INSERT_BY_TABLE.toString());
		query.executeUpdate();

	}
	
	@Override
	public List<TableList> getAllTableList() {
		return this.queryProxy().query(SELECT_ALL_TABLE_LIST_QUERY_STRING, SspmtTableList.class)
				.getList(SspmtTableList::toDomain);
	}

	@Override
	public List<TableList> getByRecoveryProcessingId(String dataRecoveryProcessId) {
		return this.queryProxy().query(SELECT_BY_RECOVERY_PROCESSING_ID_QUERY_STRING, SspmtTableList.class)
				.setParameter("dataRecoveryProcessId", dataRecoveryProcessId).getList(SspmtTableList::toDomain);
	}

	@Override
	public void deleteEmployeeHis(String tableName,String whereCid, String whereSid, String cid,
			String employeeId) {
		EntityManager em = this.getEntityManager();
		DELETE_BY_TABLE_SQL.append(tableName).append(" WHERE ");
		int count = 0;
		if (whereCid != null) {
			DELETE_BY_TABLE_SQL.append(whereCid).append(" = ").append(cid);
			count ++;
		}
		if (whereSid != null) {
			if(count !=0) {
				DELETE_BY_TABLE_SQL.append(" AND ").append(whereSid).append(" = ").append(employeeId);
			} else {
				DELETE_BY_TABLE_SQL.append(whereSid).append(" = ").append(employeeId);
			}
		}
		Query query = em.createNativeQuery(DELETE_BY_TABLE_SQL.toString());
		query.executeUpdate();
	}
	
	public void addTargetEmployee(Target domain){
		this.commandProxy().insert(SspmtTarget.toEntity(domain));
	}
	
}
