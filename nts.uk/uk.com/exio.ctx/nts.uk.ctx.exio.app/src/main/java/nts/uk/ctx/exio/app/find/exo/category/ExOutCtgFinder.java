package nts.uk.ctx.exio.app.find.exo.category;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.exo.category.CategorySetting;
import nts.uk.ctx.exio.dom.exo.category.ExOutCtgRepository;
import nts.uk.ctx.exio.dom.exo.category.SystemUsability;

/**
 * 外部出力カテゴリ
 */
@Stateless
public class ExOutCtgFinder {

    @Inject
    private ExOutCtgRepository finder;

    public List<ExOutCtgDto> getAllExOutCtg() {
        return finder.getAllExOutCtg().stream().map(item -> ExOutCtgDto.fromDomain(item)).collect(Collectors.toList());
    }

    public List<ExOutCtgDto> get(int roleType) {
        // カテゴリ設定　≠　0（出力しない）
        //・システム使用可否　＝　ロール種類が「使用可」となっている
        return finder.getExOutCtgList(CategorySetting.CATEGORY_SETTING)
                .stream().filter(item -> item.getSysUsability(roleType) == SystemUsability.AVAILABLE)
                .map(ExOutCtgDto::fromDomain)
                .sorted(Comparator.comparing(ExOutCtgDto::getDisplayOrder).thenComparing(ExOutCtgDto::getCategoryId))
                .collect(Collectors.toList());

    }
}
