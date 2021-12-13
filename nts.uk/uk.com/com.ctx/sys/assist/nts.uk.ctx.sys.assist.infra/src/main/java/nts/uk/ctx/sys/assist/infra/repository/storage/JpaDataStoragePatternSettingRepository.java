package nts.uk.ctx.sys.assist.infra.repository.storage;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.assist.dom.storage.DataStoragePatternSetting;
import nts.uk.ctx.sys.assist.dom.storage.DataStoragePatternSettingRepository;
import nts.uk.ctx.sys.assist.infra.entity.storage.SspmtDataStoragePatternSetting;
import nts.uk.ctx.sys.assist.infra.entity.storage.SspmtDataStoragePatternSettingPk;

@Stateless
public class JpaDataStoragePatternSettingRepository extends JpaRepository
		implements DataStoragePatternSettingRepository {
	private static final String SELECT_ALL = "SELECT t FROM SspmtDataStoragePatternSetting t ";
	private static final String SELECT_PATTERN_BY_CONTRACT_CD = SELECT_ALL
			+ "WHERE t.pk.contractCode = :contractCd";

	private static final String SELECT_BY_CONTRACT_CD_AND_PATTERN_CD = SELECT_ALL
			+ "WHERE t.pk.contractCode = :contractCd AND t.pk.patternCode = :patternCd";

	private static final String SELECT_BY_PATTERN_ATR_AND_PATTERN_CD_AND_CONTRACT_CD = SELECT_ALL
			+ "WHERE t.pk.patternClassification = :patternAtr AND t.pk.patternCode = :patternCd AND t.pk.contractCode = :contractCd";

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public Optional<DataStoragePatternSetting> findByContractCdAndPatternCdAndPatternAtr(String contractCd,
			String patternCd, int patternAtr) {
		return this.queryProxy().find(new SspmtDataStoragePatternSettingPk(contractCd, BooleanUtils.toBoolean(patternAtr), patternCd),
				SspmtDataStoragePatternSetting.class).map(DataStoragePatternSetting::createFromMemento);
	}

	@Override
	public List<DataStoragePatternSetting> findByContractCd(String contractCd) {
		return this.queryProxy().query(SELECT_PATTERN_BY_CONTRACT_CD, SspmtDataStoragePatternSetting.class)
				.setParameter("contractCd", contractCd).getList(DataStoragePatternSetting::createFromMemento);
	}

	@Override
	public Optional<DataStoragePatternSetting> findByContractCdAndPatternCd(String contractCd, String patternCd) {
		return this.queryProxy().query(SELECT_BY_CONTRACT_CD_AND_PATTERN_CD, SspmtDataStoragePatternSetting.class)
				.setParameter("contractCd", contractCd).setParameter("patternCd", patternCd)
				.getSingle(DataStoragePatternSetting::createFromMemento);
	}

	@Override
	public void add(DataStoragePatternSetting domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(DataStoragePatternSetting domain, String contractCd) {
		SspmtDataStoragePatternSetting entity = this.queryProxy()
				.query(SELECT_BY_PATTERN_ATR_AND_PATTERN_CD_AND_CONTRACT_CD, SspmtDataStoragePatternSetting.class)
				.setParameter("patternAtr", domain.getPatternClassification().value)
				.setParameter("patternCd", domain.getPatternCode().v())
				.setParameter("contractCd", contractCd)
				.getSingle().get();
		domain.setMemento(entity);
		this.commandProxy().update(entity);
	}

	@Override
	public void delete(String contractCd, String patternCd, int patternAtr) {
		this.commandProxy().remove(SspmtDataStoragePatternSetting.class,
								   new SspmtDataStoragePatternSettingPk(contractCd, BooleanUtils.toBoolean(patternAtr), patternCd));
	}

	private SspmtDataStoragePatternSetting toEntity(DataStoragePatternSetting domain) {
		SspmtDataStoragePatternSetting entity = new SspmtDataStoragePatternSetting();
		domain.setMemento(entity);
		return entity;
	}
}
