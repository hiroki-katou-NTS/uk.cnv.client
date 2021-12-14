package nts.uk.ctx.sys.assist.infra.repository.deletedata;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.assist.dom.deletedata.DataDeletionPatternSetting;
import nts.uk.ctx.sys.assist.dom.deletedata.DataDeletionPatternSettingRepository;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspmtDataDeletionPatternSetting;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspmtDataDeletionPatternSettingPK;

@Stateless
public class JpaDataDeletionPatternSettingRepository extends JpaRepository implements DataDeletionPatternSettingRepository {

	private static final String SELECT_BY_CONTRACT_CD = "SELECT t FROM SspmtDataDeletionPatternSetting t "
			+ "WHERE t.pk.contractCode = :contractCd";
	
	private static final String SELECT_BY_CONTRACT_CD_AND_PATTERN_CD = "SELECT t FROM SspmtDataDeletionPatternSetting t "
			+ "WHERE t.pk.contractCode = :contractCd AND t.pk.patternCode = :patternCd";
	
	@Override
	public List<DataDeletionPatternSetting> findByContractCd(String contractCd) {
		return this.queryProxy().query(SELECT_BY_CONTRACT_CD, SspmtDataDeletionPatternSetting.class)
				.setParameter("contractCd", contractCd)
				.getList(DataDeletionPatternSetting::createFromMemento);
	}
	
	@Override
	public Optional<DataDeletionPatternSetting> findByContractCdAndPatternCd(String contractCd, String patternCd) {
		return this.queryProxy().query(SELECT_BY_CONTRACT_CD_AND_PATTERN_CD, SspmtDataDeletionPatternSetting.class)
				.setParameter("contractCd", contractCd)
				.setParameter("patternCd", patternCd)
				.getSingle(DataDeletionPatternSetting::createFromMemento);
	}
	
	@Override
	public Optional<DataDeletionPatternSetting> findByPk(String contractCd, String patternCd, int patternAtr) {
		return this.queryProxy().find(
				new SspmtDataDeletionPatternSettingPK(contractCd, patternCd, patternAtr),
				SspmtDataDeletionPatternSetting.class)
				.map(DataDeletionPatternSetting::createFromMemento);
	}

	@Override
	public void add(DataDeletionPatternSetting domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(DataDeletionPatternSetting domain) {
		SspmtDataDeletionPatternSetting entity = this.queryProxy().find(
				new SspmtDataDeletionPatternSettingPK(
						domain.getContractCode().v(), 
						domain.getPatternCode().v(),
						domain.getPatternClassification().value),
				SspmtDataDeletionPatternSetting.class).get();
		domain.setMemento(entity);
		this.commandProxy().update(entity);
	}

	@Override
	public void delete(String contractCd, String patternCd, int patternAtr) {
		this.commandProxy().remove(
				SspmtDataDeletionPatternSetting.class,
				new SspmtDataDeletionPatternSettingPK(contractCd, patternCd, patternAtr));
	}
	
	private SspmtDataDeletionPatternSetting toEntity(DataDeletionPatternSetting domain) {
		SspmtDataDeletionPatternSetting entity = new SspmtDataDeletionPatternSetting();
		domain.setMemento(entity);
		return entity;
	}
}
