package nts.uk.ctx.exio.infra.repository.input.setting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtImportSetting;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtImportSettingPK;
import nts.uk.ctx.exio.infra.entity.input.setting.assembly.XimmtItemMapping;
import nts.uk.ctx.exio.infra.entity.input.setting.assembly.XimmtItemMappingPK;

@Stateless
public class JpaExternalImportSettingRepository extends JpaRepository implements ExternalImportSettingRepository {

	@Override
	public void insert(ExternalImportSetting domain) {
		this.commandProxy().insert(toEntitiy(domain));
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
	public List<ExternalImportSetting> getAll(String companyId) {
		String sql 	= " select f "
				+ " from XimmtImportSetting f "
				+ " where f.pk.companyId = :companyID ";

		return this.queryProxy().query(sql, XimmtImportSetting.class)
				.setParameter("companyID", companyId)
				.getList(rec -> rec.toDomain());
	}

	@Override
	public Optional<ExternalImportSetting> get(String companyId, ExternalImportCode settingCode) {
		String sql 	= " select f "
					+ " from XimmtImportSetting f "
					+ " where f.pk.companyId = :companyID "
					+ " and f.pk.code = :settingCD";

		return this.queryProxy().query(sql, XimmtImportSetting.class)
				.setParameter("companyID", companyId)
				.setParameter("settingCD", settingCode.toString())
				.getSingle(rec -> rec.toDomain());
	}

	private XimmtImportSetting toEntitiy(ExternalImportSetting domain) {
		return new XimmtImportSetting(
				new XimmtImportSettingPK(domain.getCompanyId(), domain.getCode().toString()),
				domain.getName().toString(),
				domain.getExternalImportDomainId().value,
				domain.getImportingMode().value,
				domain.getAssembly().getCsvFileInfo().getItemNameRowNumber().v(),
				domain.getAssembly().getCsvFileInfo().getImportStartRowNumber().v(),
				domain.getAssembly().getMapping().getMappings().stream()
				.map(m -> new XimmtItemMapping(
						new XimmtItemMappingPK(
								domain.getCompanyId(),
								domain.getCode().v(),
								m.getItemNo()),
						m.getCsvColumnNo().isPresent()? m.getCsvColumnNo().get(): null,
						m.getFixedValue().isPresent()? m.getFixedValue().get().getValue(): null))
				.collect(Collectors.toList()));
	}

	@Override
	public boolean exist(String companyId, ExternalImportCode settingCode) {
		return this.get(companyId, settingCode).isPresent();
	}
}
