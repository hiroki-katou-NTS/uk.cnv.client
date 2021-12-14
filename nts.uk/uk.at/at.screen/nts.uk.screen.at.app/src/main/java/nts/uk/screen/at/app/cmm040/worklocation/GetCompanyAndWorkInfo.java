package nts.uk.screen.at.app.cmm040.worklocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.worklocation.WorkplacePossibleCmd;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.sys.auth.app.find.company.CompanyDto;
import nts.uk.query.model.workplace.WorkplaceAdapter;
import nts.uk.query.model.workplace.WorkplaceInfoImport;
import nts.uk.shr.com.context.AppContexts;

/**
 * 会社情報と職場情報を取得する
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM040_勤務場所の登録.A：勤務場所の登録.メニュー別OCD.会社情報と職場情報を取得する
 * 
 * @author tutk
 *
 */
@Stateless
public class GetCompanyAndWorkInfo {
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private WorkplaceAdapter workplaceAdapter;

	@Inject
	private WorkLocationRepository workLocationRepo;

	public CompanyAndWorkInfoOutput get(String contractCd, List<WorkplacePossibleCmd> listCidAndWorkplace,
			List<String> listCid, String workLocationCD) {
		CompanyAndWorkInfoOutput data = new CompanyAndWorkInfoOutput();

		List<String> listWorkplaceId = new ArrayList<>();

		// 1 update 118746
		if (workLocationCD != null) {
			Optional<WorkLocation> workplace = workLocationRepo.findByWorkLocationCd(AppContexts.user().contractCode(),
					AppContexts.user().companyId(), workLocationCD);

			String workplaceId = workplace.map(m -> m.getWorkplace().map(w -> w.getWorkpalceId()).orElse(""))
					.orElse("");

			if (!workplaceId.equals("")) {
				listWorkplaceId.add(workplaceId);
			}
		}

		// 1:
		List<CompanyDto> listCompany = companyRepository.findAllByListCid(contractCd, listCid).stream()
				.map(c -> new CompanyDto(c.getCompanyCode().v(), c.getCompanyName().v(), c.getCompanyId()))
				.collect(Collectors.toList());
		data.setListCompany(listCompany);

		if (listCidAndWorkplace.isEmpty()) {
			return data;
		}
		
		// 2:
		List<CidAndWorkplaceInfoDto> listCidAndWorkplaceInfo = new ArrayList<>();
		for (String cid : listCid) {
			// List<String> listWorkplaceId =
			// listCidAndWorkplace.stream().filter(c->c.getCompanyId().equals(cid)).map(c->c.getWorkpalceId()).collect(Collectors.toList());
			List<WorkplaceInfoImport> listWorkplaceInfo = workplaceAdapter.getWorkplaceInfoByWkpIds(cid,
					listWorkplaceId, GeneralDate.today());
			listCidAndWorkplaceInfo.add(new CidAndWorkplaceInfoDto(cid, listWorkplaceInfo));
		}
		data.setListCidAndWorkplaceInfo(listCidAndWorkplaceInfo);
		return data;

	}
}
