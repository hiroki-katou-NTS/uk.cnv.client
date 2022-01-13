package nts.uk.ctx.exio.infra.repository.input.setting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.FromCsvBaseSettingToDomainRequire;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtDomainImportSetting;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtDomainImportSettingPK;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtImportSetting;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtImportSettingPK;
import nts.uk.ctx.exio.infra.entity.input.setting.assembly.XimmtItemMapping;
import nts.uk.ctx.exio.infra.entity.input.setting.assembly.XimmtItemMappingPK;

@Stateless
public class JpaExternalImportSettingRepository extends JpaRepository implements ExternalImportSettingRepository {

	@Override
	public void insert(ExternalImportSetting domain) {
		this.commandProxy().insert(toEntitiy(domain));
		domain.getDomainSettings().forEach(setting -> {
			this.commandProxy().insert(toEntitiy(domain.getCompanyId(), domain.getCode().v(), setting));
		});
	}

	@Override
	public void update(ExternalImportSetting domain) {
		delete(domain.getCompanyId(), domain.getCode());
		insert(domain);
	}

	@Override
	public void delete(String companyId, ExternalImportCode settingCode) {
		val tables = Arrays.asList(
				Pair.of("XIMMT_IMPORT_SETTING", "CODE"),
				Pair.of("XIMMT_DOMAIN_IMPORT_SETTING", "SETTING_CODE"),
				Pair.of("XIMMT_ITEM_MAPPING", "SETTING_CODE"));
		
		tables.forEach(table -> {
			
			String sql = "delete from " + table.getLeft()
					+ " where CID = @cid"
					+ " and " + table.getRight() + " = @code";

			this.jdbcProxy().query(sql)
				.paramString("cid", companyId)
				.paramString("code", settingCode.v())
				.execute();
		});
	}

	@Override
	public List<ExternalImportSetting> getCsvBase(FromCsvBaseSettingToDomainRequire require, String companyId) {
		return getByBaseType(Optional.of(require), companyId, ImportSettingBaseType.CSV_BASE);
	}

	@Override
	public List<ExternalImportSetting> getDomainBase(String companyId) {
		return getByBaseType(Optional.empty(), companyId, ImportSettingBaseType.DOMAIN_BASE);
	}

	private List<ExternalImportSetting> getByBaseType(Optional<FromCsvBaseSettingToDomainRequire> require, String companyId, ImportSettingBaseType baseType) {
		String sql 	= " select f "
				+ " from XimmtImportSetting f "
				+ " where f.pk.companyId = :companyID "
				+ " and f.baseType = :baseType ";

		List<ExternalImportSetting> domains = this.queryProxy().query(sql, XimmtImportSetting.class)
				.setParameter("companyID", companyId)
				.setParameter("baseType", baseType.value)
				.getList(rec -> rec.toDomain(require));
		
		domains.forEach(d -> {
			List<DomainImportSetting> domainImportSettings = domainImportSettings(
					companyId, d.getCode(), d.getCsvFileInfo());
			domainImportSettings.forEach(domainImportSetting ->{
				d.putDomainSettings(domainImportSetting.getDomainId(), domainImportSetting);
			});
		});
		return domains;
	}
	
	@Override
	public List<ExternalImportSetting> getAll(String companyId) {
		String sql 	= " select f "
				+ " from XimmtImportSetting f "
				+ " where f.pk.companyId = :companyID";

		List<ExternalImportSetting> domains = this.queryProxy().query(sql, XimmtImportSetting.class)
				.setParameter("companyID", companyId)
				.getList(rec -> rec.toDomain(Optional.empty()));
		
		domains.forEach(d -> {
			List<DomainImportSetting> domainImportSettings = domainImportSettings(
					companyId, d.getCode(), d.getCsvFileInfo());
			domainImportSettings.forEach(domainImportSetting ->{
				d.putDomainSettings(domainImportSetting.getDomainId(), domainImportSetting);
			});
		});
		return domains;
	}

	@Override
	public Optional<ExternalImportSetting> get(Optional<FromCsvBaseSettingToDomainRequire> require, String companyId, ExternalImportCode settingCode) {
		
		String sql 	= " select f "
					+ " from XimmtImportSetting f "
					+ " where f.pk.companyId = :companyID "
					+ " and f.pk.code = :settingCD";

		Optional<ExternalImportSetting> domain = this.queryProxy().query(sql, XimmtImportSetting.class)
				.setParameter("companyID", companyId)
				.setParameter("settingCD", settingCode.toString())
				.getSingle(rec -> rec.toDomain(require));
		
		if(domain.isPresent()) {
			List<DomainImportSetting> domainImportSettings = domainImportSettings(companyId, settingCode, domain.get().getCsvFileInfo());
			domainImportSettings.forEach(domainImportSetting -> {
				domain.get().putDomainSettings(domainImportSetting.getDomainId(), domainImportSetting);
			});
		}
		return domain;
	}
	
	private List<DomainImportSetting> domainImportSettings(String companyId, ExternalImportCode settingCode, ExternalImportCsvFileInfo csvFileInfo){
		String sql 	= " select d "
					+ " from XimmtDomainImportSetting d "
					+ " where d.pk.companyId = :companyID "
					+ " and d.pk.code = :settingCD";

		return this.queryProxy().query(sql, XimmtDomainImportSetting.class)
				.setParameter("companyID", companyId)
				.setParameter("settingCD", settingCode.toString())
				.getList(rec -> rec.toDomain(csvFileInfo));
	}

	private XimmtImportSetting toEntitiy(ExternalImportSetting domain) {
		return new XimmtImportSetting(
				new XimmtImportSettingPK(domain.getCompanyId(), domain.getCode().toString()),
				domain.getBaseType().value,
				domain.getName().toString(),
				domain.getCsvFileInfo().getItemNameRowNumber().v(),
				domain.getCsvFileInfo().getImportStartRowNumber().v(),
				domain.getCsvFileInfo().getBaseCsvFileId().orElse(null));
	}

	private XimmtDomainImportSetting toEntitiy(String cid, String settingCode, DomainImportSetting domain) {
		List<XimmtItemMapping> mappings = domain.getAssembly().getMapping().getMappings().stream()
		.map(m -> new XimmtItemMapping(
				new XimmtItemMappingPK(cid, settingCode, domain.getDomainId().value, m.getItemNo()),
				m.isFixedValue(),
				m.getCsvColumnNo().orElse(null),
				m.getFixedValue().map(fv -> fv.getValue()).orElse(null)
		))
		.collect(Collectors.toList());
		
		return new XimmtDomainImportSetting(
				new XimmtDomainImportSettingPK(cid, settingCode, domain.getDomainId().value),
				domain.getImportingMode().value,
				mappings
			);
	}

	@Override
	public boolean exist(String companyId, ExternalImportCode settingCode) {
		return this.get(Optional.empty() , companyId, settingCode).isPresent();
	}

	@Override
	public void registDomain(ExternalImportSetting setting, DomainImportSetting domain) {
		setting.putDomainSettings(domain.getDomainId(), domain);
		update(setting);
	}

	@Override
	public void deleteDomain(String companyId, String code, int domainId) {
		val tables = Arrays.asList(
				Pair.of("XIMMT_DOMAIN_IMPORT_SETTING", "SETTING_CODE"),
				Pair.of("XIMMT_ITEM_MAPPING", "SETTING_CODE"));
		
		tables.forEach(table -> {
			String sql = "delete from " + table.getLeft()
					+ " where CID = @cid"
					+ " and " + table.getRight() + " = @code"
					+ " and DOMAIN_ID = @domainId";

			this.jdbcProxy().query(sql)
				.paramString("cid", companyId)
				.paramString("code", code)
				.paramInt("domainId", domainId)
				.execute();
		});
	}
}
