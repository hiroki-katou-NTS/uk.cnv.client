package nts.uk.ctx.exio.app.find.exo.authset;

import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSetRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ExOutCtgAuthSetFinder {
    @Inject
    private ExOutCtgAuthSetRepository authSetRepo;

    public List<ExOutCtgAuthSetDTO> get(String roleId) {

        return authSetRepo.findByCidAndRoleId(AppContexts.user().companyId(), roleId).stream()
                .map(ExOutCtgAuthSetDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
