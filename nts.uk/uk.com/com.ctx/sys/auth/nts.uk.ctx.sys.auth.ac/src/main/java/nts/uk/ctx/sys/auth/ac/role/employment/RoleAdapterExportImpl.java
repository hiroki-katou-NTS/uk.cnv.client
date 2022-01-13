package nts.uk.ctx.sys.auth.ac.role.employment;

import nts.uk.ctx.bs.person.pub.person.MailAddress;
import nts.uk.ctx.bs.person.pub.person.PersonPub;
import nts.uk.ctx.sys.auth.dom.adapter.role.employment.RoleAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.role.employment.RoleExport;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class RoleAdapterExportImpl implements RoleAdapter {
    @Inject
    private PersonPub personPub;
    @Override
    public List<RoleExport> getListRole(List<String> personIds) {
        return personPub.findByPids(personIds).stream().map(e->new RoleExport(
                e.getPersonId(), e.getPersonNameGroup().getBusinessName(),
                e.getBirthDate(), MailAddress.class.getName(), e.getGender()

        )).collect(Collectors.toList());
    }
}
