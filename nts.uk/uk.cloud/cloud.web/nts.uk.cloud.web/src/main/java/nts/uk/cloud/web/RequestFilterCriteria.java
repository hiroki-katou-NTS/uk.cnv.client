package nts.uk.cloud.web;

import lombok.val;
import nts.arc.layer.ws.preprocess.RequestFilterMapping;
import nts.uk.shr.com.security.ipaddress.IpAddressRestrictor;
import nts.uk.shr.infra.web.request.StopUseFilter;
import nts.uk.shr.infra.web.request.UkRequestFilterCollector;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class RequestFilterCriteria implements UkRequestFilterCollector.Criteria {

    @Override
    public List<RequestFilterMapping> select(List<RequestFilterMapping> source) {
        return source.stream()
                .filter(m -> !excludes(m))
                .collect(Collectors.toList());
    }

    private static Set<Class<?>> EXCLUDES = new HashSet<>(Arrays.asList(
            IpAddressRestrictor.class,
            StopUseFilter.class
    ));

    private static boolean excludes(RequestFilterMapping mapping) {
        val filterClass = mapping.getFilter().getClass();
        return EXCLUDES.contains(filterClass);
    }
}
