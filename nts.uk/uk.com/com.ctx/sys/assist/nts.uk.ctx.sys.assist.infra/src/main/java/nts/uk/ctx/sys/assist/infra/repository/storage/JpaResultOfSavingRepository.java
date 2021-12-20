package nts.uk.ctx.sys.assist.infra.repository.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.gul.security.crypt.commonkey.CommonKeyCrypt;
import nts.uk.ctx.sys.assist.dom.storage.ResultOfSaving;
import nts.uk.ctx.sys.assist.dom.storage.ResultOfSavingRepository;
import nts.uk.ctx.sys.assist.dom.storage.SaveStatus;
import nts.uk.ctx.sys.assist.infra.entity.storage.SspmtResultOfLog;
import nts.uk.ctx.sys.assist.infra.entity.storage.SspdtSaveResult;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaResultOfSavingRepository extends JpaRepository implements ResultOfSavingRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM SspdtSaveResult f";
	private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING
			+ " WHERE  f.storeProcessingId IN :storeProcessingId ";
	private static final String SELECT_WITH_NULL_LIST_EMPLOYEE = SELECT_ALL_QUERY_STRING
				+ " WHERE f.cid =:cid "
				+ " AND f.saveStartDatetime >=:startDateOperator "
				+ " AND f.saveStartDatetime <=:endDateOperator ";
	
	private static final String SELECT_WITH_NOT_NULL_LIST_EMPLOYEE = SELECT_ALL_QUERY_STRING
				+ " WHERE f.cid =:cid "
				+ " AND f.saveStartDatetime =:startDateOperator "
				+ " AND f.saveStartDatetime =:endDateOperator "
				+ " AND f.practitioner IN :practitioner ";
	private static final String SELECT_BY_SAVE_SET_CODE = SELECT_ALL_QUERY_STRING
			+ " WHERE f.patternCode IN :saveSetCodes";
	private static final String FIND_RESULTS_BY_STARTDATETIME = "SELECT r FROM SspdtSaveResult r "
			+ "WHERE r.saveStartDatetime >= :start AND r.saveStartDatetime <= :end ";
	private static final String SELECT_BY_FILE_ID = "SELECT f FROM SspdtSaveResult f "
			+ "WHERE f.fileId = :fileId";

	@Override
	public List<ResultOfSaving> getAllResultOfSaving() {
		return this.queryProxy().query(SELECT_ALL_QUERY_STRING, SspdtSaveResult.class)
				.getList(item -> item.toDomain());
	}

	@Override
	public Optional<ResultOfSaving> getResultOfSavingById(String storeProcessingId) {
		return this.queryProxy().query(SELECT_BY_KEY_STRING, SspdtSaveResult.class)
				.setParameter("storeProcessingId", Collections.singletonList(storeProcessingId)).getSingle(c -> c.toDomain());
	}

	@Override
	public void add(ResultOfSaving data) {
		SspdtSaveResult entity = SspdtSaveResult.toEntity(data);
		String password = data.getCompressedPassword().map(i -> i.v()).orElse("");
		entity.compressedPassword = StringUtils.isNotEmpty(password) ? CommonKeyCrypt.encrypt(password) : null;
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(String storeProcessingId, int targetNumberPeople, SaveStatus saveStatus, String fileId,
			NotUseAtr deletedFiles, String compressedFileName) {
		Optional<SspdtSaveResult> resultOfSavingOpt = this.queryProxy().find(storeProcessingId, SspdtSaveResult.class);
		resultOfSavingOpt.ifPresent(data -> {
			data.targetNumberPeople = targetNumberPeople;
			data.saveStatus = saveStatus.value;
			data.fileId = fileId;
			data.deletedFiles = BooleanUtils.toBoolean(deletedFiles.value);
			data.saveFileName = compressedFileName;
			data.saveEndDatetime = GeneralDateTime.now();
			this.commandProxy().update(data);
		});
	}

	@Override
	public void update(String storeProcessingId, Optional<Integer> targetNumberPeople, Optional<SaveStatus> saveStatus) {
		Optional<ResultOfSaving> resultOfSavingOpt = this.getResultOfSavingById(storeProcessingId);
		resultOfSavingOpt.ifPresent(data -> {
			data.setTargetNumberPeople(targetNumberPeople);
			data.setSaveStatus(saveStatus);
			data.setSaveEndDatetime(Optional.of(GeneralDateTime.now()));
			this.commandProxy().update(SspdtSaveResult.toEntity(data));
		});
	}

	@Override
	public void update(ResultOfSaving data) {
		Optional<SspdtSaveResult> op = this.queryProxy().find(data.getStoreProcessingId(), SspdtSaveResult.class);
		op.ifPresent(oldEntity -> {
			oldEntity.pcAccount = data.getLoginInfo().getAccount();
			oldEntity.pcId = data.getLoginInfo().getIpAddress();
			oldEntity.pcName = data.getLoginInfo().getPcName();
			oldEntity.listResultOfLogs = data.getListResultLogSavings().stream()
					.map(SspmtResultOfLog::toEntity).collect(Collectors.toList());
			this.commandProxy().update(oldEntity);
		});
	}

	@Override
	public void update(String storeProcessingId, long fileSize) {
		Optional<SspdtSaveResult> resultOfSavingOpt = this.queryProxy().find(storeProcessingId, SspdtSaveResult.class);
		resultOfSavingOpt.ifPresent(data -> {
			data.fileSize = fileSize;
			this.commandProxy().update(data);
		});
		
	}
	
	@Override
	public void update(String fileId) {
		Optional<SspdtSaveResult> op = this.queryProxy()
												.query(SELECT_BY_FILE_ID, SspdtSaveResult.class)
												.setParameter("fileId", fileId)
												.getSingle();
		op.ifPresent(data -> {
			data.deletedFiles = BooleanUtils.toBoolean(NotUseAtr.USE.value);
			this.commandProxy().update(data);
		});
								
	}

	@Override
	public List<ResultOfSaving> getResultOfSaving(
			String cid,
			GeneralDateTime startDateOperator, 
			GeneralDateTime endDateOperator, 
			List<String> listOperatorEmployeeId) {
		
		List<ResultOfSaving> resultOfSavings = new ArrayList<ResultOfSaving>();
		
		if (!CollectionUtil.isEmpty(listOperatorEmployeeId)) {
			resultOfSavings.addAll(
					this.queryProxy().query(SELECT_WITH_NOT_NULL_LIST_EMPLOYEE, SspdtSaveResult.class)
					.setParameter("cid", cid)
					.setParameter("startDateOperator", startDateOperator)
					.setParameter("endDateOperator", endDateOperator)
					.setParameter("practitioner", listOperatorEmployeeId)
					.getList(item -> item.toDomain()));
		} else {
			resultOfSavings.addAll(
					this.queryProxy().query(SELECT_WITH_NULL_LIST_EMPLOYEE, SspdtSaveResult.class)
					.setParameter("cid", cid)
					.setParameter("startDateOperator", startDateOperator)
					.setParameter("endDateOperator", endDateOperator)
					.getList(item -> item.toDomain()));
		}
		return resultOfSavings;
	}

	@Override
	public List<ResultOfSaving> getResultOfSavingByIds(List<String> storeProcessingIds) {
		return this.queryProxy().query(SELECT_BY_KEY_STRING, SspdtSaveResult.class)
				.setParameter("storeProcessingId", Arrays.asList(storeProcessingIds))
				.getList(SspdtSaveResult::toDomain); 
	}
	
	@Override
	public List<ResultOfSaving> getResultOfSavingBySaveSetCode(List<String> saveSetCodes) {
		return this.queryProxy().query(SELECT_BY_SAVE_SET_CODE, SspdtSaveResult.class)
				.setParameter("saveSetCodes", saveSetCodes)
				.getList(SspdtSaveResult::toDomain);
				
	}
	
	@Override
	public List<ResultOfSaving> getByStartDatetime(GeneralDateTime from, GeneralDateTime to) {
		return this.queryProxy().query(FIND_RESULTS_BY_STARTDATETIME, SspdtSaveResult.class)
				.setParameter("start", from)
				.setParameter("end", to)
				.getList(SspdtSaveResult::toDomain);
	}
}
