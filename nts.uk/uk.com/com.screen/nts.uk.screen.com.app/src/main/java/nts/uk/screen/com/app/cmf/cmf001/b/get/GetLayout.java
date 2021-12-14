package nts.uk.screen.com.app.cmf.cmf001.b.get;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetLayout {
	
	@Inject
	private GetLayoutRequire require;
	
	public List<Integer> get(GetLayoutParam query) {
		val require = this.require.create();
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
			val setting = settingOpt.get();
			if (query.getImportingDomainId() == setting.getExternalImportDomainId()) {
				// 設定されている項目
				return setting.getAssembly().getMapping().getMappings().stream()
						.map(m -> m.getItemNo())
						.collect(Collectors.toList());
			}
		}
		// デフォルトで全項目
		val importableItems = require.getImportableItems(query.getImportingDomainId());
		return importableItems.stream()
				.map(i -> i.getItemNo())
				.collect(Collectors.toList());
	}
	
	public List<ExternalImportLayoutDto> getDetail(GetLayoutParam query) {
		val require = this.require.create();
		
		if (query.isAllItem()) {
			return getAll(require, query);
		} else {
			return getSpecified(require, query);
		}
	}
	
	// すべての項目のレイアウトを取得する
	private List<ExternalImportLayoutDto> getAll(GetLayout.Require require, GetLayoutParam query) {
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
			return getSaved(require, query, settingOpt.get());
		}
		else {
			return getAllImportables(require, query);
		}
	}
	
	// 指定した項目のレイアウトを取得する
	private List<ExternalImportLayoutDto> getSpecified(GetLayout.Require require, GetLayoutParam query) {
		
		val results = new ArrayList<ExternalImportLayoutDto>();
		
		val settingOpt = require.getSetting(AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
			results.addAll(getSaved(require, query, settingOpt.get()).stream()
					.filter(s -> query.getItemNoList().contains(s.getItemNo()))
					.collect(toList()));
		}
		
		val savedItemNos = results.stream().map(l -> l.getItemNo()).collect(Collectors.toSet());
		
		// 指定した項目のうち未登録の項目	
		val unsavedItemNos = query.getItemNoList().stream()
				.filter(n -> !savedItemNos.contains(n))
				.collect(Collectors.toSet());
		
		val unsavedLayouts = getAllImportables(require, query).stream()
				.filter(l -> unsavedItemNos.contains(l.getItemNo()))
				.collect(toList());
		
		results.addAll(unsavedLayouts);
		
		return results;
	}
	
	private static List<ExternalImportLayoutDto> getAllImportables(GetLayout.Require require, GetLayoutParam query) {
		
		val importableItems = require.getImportableItems(query.getImportingDomainId());
		
		return importableItems.stream()
				.map(i -> ExternalImportLayoutDto.fromDomain(
						require,
						query.getSettingCode(),
						query.getImportingDomainId(),
						ImportingItemMapping.noSetting(i.getItemNo())))
				.collect(Collectors.toList());
	}
	
	private List<ExternalImportLayoutDto> getSaved(
			GetLayout.Require require,
			GetLayoutParam query,
			ExternalImportSetting setting) {
		return toLayouts(require, query, setting.getAssembly().getMapping().getMappings());
	}
	
	private List<ExternalImportLayoutDto> toLayouts(GetLayout.Require require, GetLayoutParam query,
			List<ImportingItemMapping> mappings) {
		
		return mappings.stream()
				.map(i -> ExternalImportLayoutDto.fromDomain(
						require,
						query.getSettingCode(),
						query.getImportingDomainId(),
						new ImportingItemMapping(i.getItemNo(), i.getCsvColumnNo(), i.getFixedValue())))
				.collect(Collectors.toList());
	}
	
	public static interface Require extends ExternalImportLayoutDto.Require {
		Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode);
		
		List<ImportableItem> getImportableItems(ImportingDomainId domainId);
	}
}
