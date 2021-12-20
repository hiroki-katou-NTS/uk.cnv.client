package nts.uk.ctx.sys.assist.infra.repository.deletedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.gul.security.crypt.commonkey.CommonKeyCrypt;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.assist.dom.deletedata.ManualSetDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.ResultDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.ResultDeletionRepository;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspdtDeletionResult;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspdtResultDeletionPK;
import nts.uk.shr.com.enumcommon.NotUseAtr;


@Stateless
public class JpaResultDeletionRepository extends JpaRepository implements ResultDeletionRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM SspdtDeletionResult f";
	private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING
			+ " WHERE  f.sspdtResultDeletionPK.delId = :delId ";
	private static final String SELECT_WITH_NULL_LIST_EMPLOYEE =
			" SELECT f FROM SspdtDeletionResult f "
			+ " WHERE f.companyID =:cid "
			+ " AND f.startDateTimeDel >=:startDateOperator "
			+ " AND f.startDateTimeDel <=:endDateOperator ";

private static final String SELECT_WITH_NOT_NULL_LIST_EMPLOYEE =
			" SELECT f FROM SspdtDeletionResult f "
			+ " WHERE f.companyID =:cid "
			+ " AND f.startDateTimeDel =:startDateOperator "
			+ " AND f.startDateTimeDel =:endDateOperator "
			+ " AND f.sId IN :practitioner ";
	private static final String FIND_RESULTS_BY_STARTDATETIME = "SELECT r FROM SspdtDeletionResult r "
		+ "WHERE r.startDateTimeDel >= :start AND r.startDateTimeDel <= :end ";
	private static final String FIND_BY_DELCODE_AND_SYSTEM_TYPE = "SELECT t FROM SspdtDeletionResult t "
			+ "WHERE t.delCode IN :delCodes";
	private static final String SELECT_BY_FILE_ID = "SELECT f FROM SspdtDeletionResult f "
			+ "WHERE f.fileId = :fileId";

	@Override
	public List<ResultDeletion> getAllResultDeletion() {
		return this.queryProxy().query(SELECT_ALL_QUERY_STRING, SspdtDeletionResult.class)
				.getList(item -> item.toDomain());
	}

	@Override
	public Optional<ResultDeletion> getResultDeletionById(String delId) {
		return this.queryProxy().query(SELECT_BY_KEY_STRING, SspdtDeletionResult.class)
				.setParameter("delId", delId).getSingle(c -> c.toDomain());
	}

	@Override
	public void add(ResultDeletion data) {
		this.commandProxy().insert(SspdtDeletionResult.toEntity(data));
	}
	
	@Override
	public void update(ResultDeletion data) {
		 this.commandProxy().update(SspdtDeletionResult.toEntity(data));
	}

	@Override
	public void update(ResultDeletion resultDel, ManualSetDeletion manualSetDel) {
		SspdtResultDeletionPK key  = new SspdtResultDeletionPK(resultDel.getDelId());
		Optional<SspdtDeletionResult> resultOfDeleteOpt = this.queryProxy().find(key, SspdtDeletionResult.class);
		resultOfDeleteOpt.ifPresent(data -> {
			data.status = resultDel.getStatus().value;
			data.endDateTimeDel = resultDel.getEndDateTimeDel().orElse(null);
			data.fileSize = resultDel.getFileSize();
			data.fileId = resultDel.getFileId();
			data.isDeletedFilesFlg = resultDel.isDeletedFilesFlg();
			data.numberEmployees = resultDel.getNumberEmployees();
			
			// redmine #108204
			String password = "";
			if (manualSetDel.isExistCompressPassFlg() && !StringUtil.isNullOrEmpty(resultDel.getFileId(), true)) {
				String passwordOpt = manualSetDel.getPasswordCompressFileEncrypt().map(i -> i.v()).orElse("");
				if (StringUtils.isNotEmpty(passwordOpt)) {
					password = CommonKeyCrypt.encrypt(passwordOpt);
				} 
			} 
			
			if(!StringUtil.isNullOrEmpty(resultDel.getFileId(), true)){
				data.fileName  = resultDel.getFileName().toString();
			}else{
				data.fileName = "";
			}
			
			data.passwordCompressFileEncrypt = password;
			this.commandProxy().update(data);
		});
	}

	@Override
	public List<ResultDeletion> getResultOfDeletion(String cid, GeneralDateTime startDateOperator,
			GeneralDateTime endDateOperator, List<String> listOperatorEmployeeId) {
	List<ResultDeletion> list = new ArrayList<ResultDeletion>();
		
		if (!CollectionUtil.isEmpty(listOperatorEmployeeId)) {
			list.addAll(
					this.queryProxy().query(SELECT_WITH_NOT_NULL_LIST_EMPLOYEE, SspdtDeletionResult.class)
					.setParameter("cid", cid)
					.setParameter("startDateOperator", startDateOperator)
					.setParameter("endDateOperator", endDateOperator)
					.setParameter("practitioner", listOperatorEmployeeId)
					.getList(item -> item.toDomain()));
		} else {
			list.addAll(
					this.queryProxy().query(SELECT_WITH_NULL_LIST_EMPLOYEE, SspdtDeletionResult.class)
					.setParameter("cid", cid)
					.setParameter("startDateOperator", startDateOperator)
					.setParameter("endDateOperator", endDateOperator)
					.getList(item -> item.toDomain()));
		}
		return list;
	}
	
	@Override
	public List<ResultDeletion> getByStartDatetimeDel(GeneralDateTime from, GeneralDateTime to) {
		return this.queryProxy().query(FIND_RESULTS_BY_STARTDATETIME, SspdtDeletionResult.class)
				.setParameter("start", from)
				.setParameter("end", to)
				.getList(SspdtDeletionResult::toDomain);
	}

	@Override
	public List<ResultDeletion> getByListCodes(List<String> delCodes) {
		return this.queryProxy().query(FIND_BY_DELCODE_AND_SYSTEM_TYPE, SspdtDeletionResult.class)
				.setParameter("delCodes", delCodes)
				.getList(SspdtDeletionResult::toDomain);
	}

	@Override
	public void update(String fileId) {
		Optional<SspdtDeletionResult> op = this.queryProxy()
				.query(SELECT_BY_FILE_ID, SspdtDeletionResult.class)
				.setParameter("fileId", fileId)
				.getSingle();
		op.ifPresent(data -> {
			data.isDeletedFilesFlg = BooleanUtils.toBoolean(NotUseAtr.USE.value);
			this.commandProxy().update(data);
		});
	}
}
