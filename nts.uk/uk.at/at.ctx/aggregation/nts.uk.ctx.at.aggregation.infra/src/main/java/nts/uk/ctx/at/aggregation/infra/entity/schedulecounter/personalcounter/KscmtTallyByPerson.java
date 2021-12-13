package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.personalcounter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_TALLY_BYPERSON")
public class KscmtTallyByPerson extends ContractUkJpaEntity implements Serializable {

    @EmbeddedId
    public KscmtTallyByPersonPk pk;

    @Column(name = "USE_ATR")
    public boolean useAtr;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public static List<KscmtTallyByPerson> toEntity(String companyId, PersonalCounter domain, List<EnumConstant> listEnum) {

        return listEnum.stream().map(x -> {
            KscmtTallyByPersonPk pk = new KscmtTallyByPersonPk(companyId, x.getValue());
            KscmtTallyByPerson data = new KscmtTallyByPerson(
                pk,
                domain.isUsed(PersonalCounterCategory.of(x.getValue()))
            );
            data.contractCd = AppContexts.user().contractCode();
            return data;
        }).collect(Collectors.toList());
    }

    public static PersonalCounter toDomain(List<KscmtTallyByPerson> entities) {
        //TODO how to map entity to domain with category used ?
        List<PersonalCounterCategory> useCategories = entities.stream().filter(i -> i.useAtr).map(x -> {
            return EnumAdaptor.valueOf(x.pk.category, PersonalCounterCategory.class);
        }).collect(Collectors.toList());

        return PersonalCounter.create(useCategories);
    }
}
