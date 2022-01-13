package nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.stampsetting;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.UseDivision;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.AppCommentSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.AppStampSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.Comment;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.GoOutType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.GoOutTypeDispControl;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.SettingForEachType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.StampAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.SupportFrameDispNO;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflect;
import nts.uk.shr.com.color.ColorCode;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KRQMT_APP_STAMP")
public class KrqmtAppStamp extends ContractUkJpaEntity {
    @Id
    @Column(name = "CID")
    private String companyId;

    @Column(name = "SUP_DISP_CNT")
    private Integer supDispCnt;

    @Column(name = "STAMP_PLACE_DISP_ATR")
    private boolean stampPlaceDispAtr;

    @Column(name = "CANCEL_DISP_ATR")
    private boolean cancelDispAtr;

    @Column(name = "STAMP_OUT_PRI_DISP_ATR")
    private boolean stampOutPriDispAtr;

    @Column(name = "STAMP_OUT_PUB_DISP_ATR")
    private boolean stampOutPubDispAtr;

    @Column(name = "STAMP_OUT_COMP_DISP_ATR")
    private boolean stampOutCompDispAtr;

    @Column(name = "STAMP_OUT_UNION_DISP_ATR")
    private boolean stampOutUnionDispAtr;

    @Column(name = "WK_CMT_FONT_COLOR1")
    private String workCmtFontColor1;

    @Column(name = "WK_CMT_CONTENT1")
    private String workCmtContent1;

    @Column(name = "WK_CMT_FONT_WEIGHT1")
    private Integer workCmtFontWeight1;

    @Column(name = "WK_CMT_FONT_COLOR2")
    private String workCmtFontColor2;

    @Column(name = "WK_CMT_CONTENT2")
    private String workCmtContent2;

    @Column(name = "WK_CMT_FONT_WEIGHT2")
    private Integer workCmtFontWeight2;

    @Column(name = "GOOUT_CMT_FONT_COLOR1")
    private String goOutCmtFontColor1;

    @Column(name = "GOOUT_CMT_CONTENT1")
    private String goOutCmtContent1;

    @Column(name = "GOOUT_CMT_FONT_WEIGHT1")
    private Integer goOutCmtFontWeight1;

    @Column(name = "GOOUT_CMT_FONT_COLOR2")
    private String goOutCmtFontColor2;

    @Column(name = "GOOUT_CMT_CONTENT2")
    private String goOutCmtContent2;

    @Column(name = "GOOUT_CMT_FONT_WEIGHT2")
    private Integer goOutCmtFontWeight2;

    @Column(name = "CHILD_CARE_CMT_FONT_COLOR1")
    private String childCareCmtFontColor1;

    @Column(name = "CHILD_CARE_CMT_CONTENT1")
    private String childCareCmtContent1;

    @Column(name = "CHILD_CARE_CMT_FONT_WEIGHT1")
    private Integer childCareCmtFontWeight1;

    @Column(name = "CHILD_CARE_CMT_FONT_COLOR2")
    private String childCareCmtFontColor2;

    @Column(name = "CHILD_CARE_CMT_CONTENT2")
    private String childCareCmtContent2;

    @Column(name = "CHILD_CARE_CMT_FONT_WEIGHT2")
    private Integer childCareCmtFontWeight2;

    @Column(name = "SUP_CMT_FONT_COLOR1")
    private String supCmtFontColor1;

    @Column(name = "SUP_CMT_CONTENT1")
    private String supCmtContent1;

    @Column(name = "SUP_CMT_FONT_WEIGHT1")
    private Integer supCmtFontWeight1;

    @Column(name = "SUP_CMT_FONT_COLOR2")
    private String supCmtFontColor2;

    @Column(name = "SUP_CMT_CONTENT2")
    private String supCmtContent2;

    @Column(name = "SUP_CMT_FONT_WEIGHT2")
    private Integer supCmtFontWeight2;

    @Column(name = "CARE_CMT_FONT_COLOR1")
    private String careCmtFontColor1;

    @Column(name = "CARE_CMT_CONTENT1")
    private String careCmtContent1;

    @Column(name = "CARE_CMT_FONT_WEIGHT1")
    private Integer careCmtFontWeight1;

    @Column(name = "CARE_CMT_FONT_COLOR2")
    private String careCmtFontColor2;

    @Column(name = "CARE_CMT_CONTENT2")
    private String careCmtContent2;

    @Column(name = "CARE_CMT_FONT_WEIGHT2")
    private Integer careCmtFontWeight2;

    @Column(name = "BREAK_CMT_FONT_COLOR1")
    private String breakCmtFontColor1;

    @Column(name = "BREAK_CMT_CONTENT1")
    private String breakCmtContent1;

    @Column(name = "BREAK_CMT_FONT_WEIGHT1")
    private Integer breakCmtFontWeight1;

    @Column(name = "BREAK_CMT_FONT_COLOR2")
    private String breakCmtFontColor2;

    @Column(name = "BREAK_CMT_CONTENT2")
    private String breakCmtContent2;

    @Column(name = "BREAK_CMT_FONT_WEIGHT2")
    private Integer breakCmtFontWeight2;

    @Column(name = "NR_CMT_FONT_COLOR1")
    private String nrCmtFontColor1;

    @Column(name = "NR_CMT_CONTENT1")
    private String nrCmtContent1;

    @Column(name = "NR_CMT_FONT_WEIGHT1")
    private Integer nrCmtFontWeight1;

    @Column(name = "NR_CMT_FONT_COLOR2")
    private String nrCmtFontColor2;

    @Column(name = "NR_CMT_CONTENT2")
    private String nrCmtContent2;

    @Column(name = "NR_CMT_FONT_WEIGHT2")
    private Integer nrCmtFontWeight2;

    @Column(name = "CHILD_CARE_TIME_REFLECT_ATR")
    private Integer childCareTimeReflectAtr;

    @Column(name = "SUP_TIME_REFLECT_ATR")
    private Integer supTimeReflectAtr;

    @Column(name = "CARE_TIME_REFLECT_ATR")
    private Integer careTimeReflectAtr;

    @Column(name = "GOOUT_TIME_REFLECT_ATR")
    private Integer goOutTimeReflectAtr;

    @Column(name = "BREAK_TIME_REFLECT_ATR")
    private Integer breakTimeReflectAtr;

    @Column(name = "WORK_TIME_REFLECT_ATR")
    private Integer workTimeReflectAtr;

    @Column(name = "EXTRA_WORK_TIME_REFLECT_ATR")
    private Integer extraWorkTimeReflectAtr;

    @Override
    protected Object getKey() {
        return companyId;
    }

    public AppStampSetting toSettingDomain() {
        return new AppStampSetting(
                companyId,
                new SupportFrameDispNO(supDispCnt),
                EnumAdaptor.valueOf(BooleanUtils.toInteger(cancelDispAtr), UseDivision.class),
                Arrays.asList(
                        new SettingForEachType(
                                StampAtr.ATTENDANCE_RETIREMENT,
                                new AppCommentSet(new Comment(workCmtContent1), BooleanUtils.toBoolean(workCmtFontWeight1), new ColorCode(workCmtFontColor1)),
                                new AppCommentSet(new Comment(workCmtContent2), BooleanUtils.toBoolean(workCmtFontWeight2), new ColorCode(workCmtFontColor2))
                        ),
                        new SettingForEachType(
                                StampAtr.GOING_OUT_RETURNING,
                                new AppCommentSet(new Comment(goOutCmtContent1), BooleanUtils.toBoolean(goOutCmtFontWeight1), new ColorCode(goOutCmtFontColor1)),
                                new AppCommentSet(new Comment(goOutCmtContent2), BooleanUtils.toBoolean(goOutCmtFontWeight2), new ColorCode(goOutCmtFontColor2))
                        ),
                        new SettingForEachType(
                                StampAtr.CHILDCARE_OUT_RETURN,
                                new AppCommentSet(new Comment(childCareCmtContent1), BooleanUtils.toBoolean(childCareCmtFontWeight1), new ColorCode(childCareCmtFontColor1)),
                                new AppCommentSet(new Comment(childCareCmtContent2), BooleanUtils.toBoolean(childCareCmtFontWeight2), new ColorCode(childCareCmtFontColor2))
                        ),
                        new SettingForEachType(
                                StampAtr.SUPPORT_IN_SUPPORT_OUT,
                                new AppCommentSet(new Comment(supCmtContent1), BooleanUtils.toBoolean(supCmtFontWeight1), new ColorCode(supCmtFontColor1)),
                                new AppCommentSet(new Comment(supCmtContent2), BooleanUtils.toBoolean(supCmtFontWeight2), new ColorCode(supCmtFontColor2))
                        ),
                        new SettingForEachType(
                                StampAtr.OUT_OF_CARE_RETURN_OF_CARE,
                                new AppCommentSet(new Comment(careCmtContent1), BooleanUtils.toBoolean(careCmtFontWeight1), new ColorCode(careCmtFontColor1)),
                                new AppCommentSet(new Comment(careCmtContent2), BooleanUtils.toBoolean(careCmtFontWeight2), new ColorCode(careCmtFontColor2))
                        ),
                        new SettingForEachType(
                                StampAtr.BREAK,
                                new AppCommentSet(new Comment(breakCmtContent1), BooleanUtils.toBoolean(breakCmtFontWeight1), new ColorCode(breakCmtFontColor1)),
                                new AppCommentSet(new Comment(breakCmtContent2), BooleanUtils.toBoolean(breakCmtFontWeight2), new ColorCode(breakCmtFontColor2))
                        )
                        , new SettingForEachType(
                                StampAtr.RECORDER_IMAGE,
                                new AppCommentSet(new Comment(nrCmtContent1), BooleanUtils.toBoolean(nrCmtFontWeight1), new ColorCode(nrCmtFontColor1)),
                                new AppCommentSet(new Comment(nrCmtContent2), BooleanUtils.toBoolean(nrCmtFontWeight2), new ColorCode(nrCmtFontColor2))
                        )
                ),
                Arrays.asList(
                        new GoOutTypeDispControl(
                                EnumAdaptor.valueOf(BooleanUtils.toInteger(stampOutPriDispAtr), DisplayAtr.class),
                                GoOutType.PRIVATE
                        ),
                        new GoOutTypeDispControl(
                                EnumAdaptor.valueOf(BooleanUtils.toInteger(stampOutPubDispAtr), DisplayAtr.class),
                                GoOutType.OFFICE
                        ),
                        new GoOutTypeDispControl(
                                EnumAdaptor.valueOf(BooleanUtils.toInteger(stampOutCompDispAtr), DisplayAtr.class),
                                GoOutType.COMPENSATION
                        ),
                        new GoOutTypeDispControl(
                                EnumAdaptor.valueOf(BooleanUtils.toInteger(stampOutUnionDispAtr), DisplayAtr.class),
                                GoOutType.UNION
                        )
                )
        );
    }

    public StampAppReflect toReflectDomain() {
        return StampAppReflect.create(
                companyId,
                workTimeReflectAtr,
                extraWorkTimeReflectAtr,
                goOutTimeReflectAtr,
                childCareTimeReflectAtr,
                supTimeReflectAtr,
                careTimeReflectAtr,
                breakTimeReflectAtr
        );
    }

    public static KrqmtAppStamp create(String companyId, AppStampSetting setting, StampAppReflect reflect) {
        KrqmtAppStamp entity = new KrqmtAppStamp();
        entity.companyId = companyId;
        entity.cancelDispAtr = BooleanUtils.toBoolean(setting.getUseCancelFunction().value);
        entity.supDispCnt = setting.getSupportFrameDispNO().v();
        for (GoOutTypeDispControl t : setting.getGoOutTypeDispControl()) {
            switch (t.getGoOutType()) {
                case PRIVATE:
                    entity.stampOutPriDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case OFFICE:
                    entity.stampOutPubDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case COMPENSATION:
                    entity.stampOutCompDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case UNION:
                    entity.stampOutUnionDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                default:
                    break;
            }
        }
        for (SettingForEachType t : setting.getSettingForEachTypeLst()) {
            switch (t.getStampAtr()) {
                case ATTENDANCE_RETIREMENT:
                    entity.workCmtContent1 = t.getTopComment().getComment().v();
                    entity.workCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.workCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.workCmtContent2 = t.getBottomComment().getComment().v();
                    entity.workCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.workCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case GOING_OUT_RETURNING:
                    entity.goOutCmtContent1 = t.getTopComment().getComment().v();
                    entity.goOutCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.goOutCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.goOutCmtContent2 = t.getBottomComment().getComment().v();
                    entity.goOutCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.goOutCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case CHILDCARE_OUT_RETURN:
                    entity.childCareCmtContent1 = t.getTopComment().getComment().v();
                    entity.childCareCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.childCareCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.childCareCmtContent2 = t.getBottomComment().getComment().v();
                    entity.childCareCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.childCareCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case SUPPORT_IN_SUPPORT_OUT:
                    entity.supCmtContent1 = t.getTopComment().getComment().v();
                    entity.supCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.supCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.supCmtContent2 = t.getBottomComment().getComment().v();
                    entity.supCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.supCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case OUT_OF_CARE_RETURN_OF_CARE:
                    entity.careCmtContent1 = t.getTopComment().getComment().v();
                    entity.careCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.careCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.careCmtContent2 = t.getBottomComment().getComment().v();
                    entity.careCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.careCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case BREAK:
                    entity.breakCmtContent1 = t.getTopComment().getComment().v();
                    entity.breakCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.breakCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.breakCmtContent2 = t.getBottomComment().getComment().v();
                    entity.breakCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.breakCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case RECORDER_IMAGE:
                    entity.nrCmtContent1 = t.getTopComment().getComment().v();
                    entity.nrCmtFontColor1 = t.getTopComment().getColorCode().v();
                    entity.nrCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    entity.nrCmtContent2 = t.getBottomComment().getComment().v();
                    entity.nrCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    entity.nrCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                default:
                    break;
            }
        }
        entity.workTimeReflectAtr = reflect.getWorkReflectAtr().value;
        entity.extraWorkTimeReflectAtr = reflect.getExtraWorkReflectAtr().value;
        entity.childCareTimeReflectAtr = reflect.getChildCareReflectAtr().value;
        entity.supTimeReflectAtr = reflect.getSupportReflectAtr().value;
        entity.careTimeReflectAtr = reflect.getCareReflectAtr().value;
        entity.goOutTimeReflectAtr = reflect.getGoOutReflectAtr().value;
        entity.breakTimeReflectAtr = reflect.getBreakReflectAtr().value;
        return entity;
    }

    public void updateSetting(AppStampSetting setting) {
        cancelDispAtr = BooleanUtils.toBoolean(setting.getUseCancelFunction().value);
        supDispCnt = setting.getSupportFrameDispNO().v();
        for (GoOutTypeDispControl t : setting.getGoOutTypeDispControl()) {
            switch (t.getGoOutType()) {
                case PRIVATE:
                    stampOutPriDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case OFFICE:
                    stampOutPubDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case COMPENSATION:
                    stampOutCompDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                case UNION:
                    stampOutUnionDispAtr = BooleanUtils.toBoolean(t.getDisplay().value);
                    break;
                default:
                    break;
            }
        }
        for (SettingForEachType t : setting.getSettingForEachTypeLst()) {
            switch (t.getStampAtr()) {
                case ATTENDANCE_RETIREMENT:
                    workCmtContent1 = t.getTopComment().getComment().v();
                    workCmtFontColor1 = t.getTopComment().getColorCode().v();
                    workCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    workCmtContent2 = t.getBottomComment().getComment().v();
                    workCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    workCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case GOING_OUT_RETURNING:
                    goOutCmtContent1 = t.getTopComment().getComment().v();
                    goOutCmtFontColor1 = t.getTopComment().getColorCode().v();
                    goOutCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    goOutCmtContent2 = t.getBottomComment().getComment().v();
                    goOutCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    goOutCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case CHILDCARE_OUT_RETURN:
                    childCareCmtContent1 = t.getTopComment().getComment().v();
                    childCareCmtFontColor1 = t.getTopComment().getColorCode().v();
                    childCareCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    childCareCmtContent2 = t.getBottomComment().getComment().v();
                    childCareCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    childCareCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case SUPPORT_IN_SUPPORT_OUT:
                    supCmtContent1 = t.getTopComment().getComment().v();
                    supCmtFontColor1 = t.getTopComment().getColorCode().v();
                    supCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    supCmtContent2 = t.getBottomComment().getComment().v();
                    supCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    supCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case OUT_OF_CARE_RETURN_OF_CARE:
                    careCmtContent1 = t.getTopComment().getComment().v();
                    careCmtFontColor1 = t.getTopComment().getColorCode().v();
                    careCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    careCmtContent2 = t.getBottomComment().getComment().v();
                    careCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    careCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case BREAK:
                    breakCmtContent1 = t.getTopComment().getComment().v();
                    breakCmtFontColor1 = t.getTopComment().getColorCode().v();
                    breakCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    breakCmtContent2 = t.getBottomComment().getComment().v();
                    breakCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    breakCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                case RECORDER_IMAGE:
                    nrCmtContent1 = t.getTopComment().getComment().v();
                    nrCmtFontColor1 = t.getTopComment().getColorCode().v();
                    nrCmtFontWeight1 = BooleanUtils.toInteger(t.getTopComment().isBold());
                    nrCmtContent2 = t.getBottomComment().getComment().v();
                    nrCmtFontColor2 = t.getBottomComment().getColorCode().v();
                    nrCmtFontWeight2 = BooleanUtils.toInteger(t.getBottomComment().isBold());
                    break;
                default:
                    break;
            }
        }
    }

    public void updateReflect(StampAppReflect reflect) {
        workTimeReflectAtr = reflect.getWorkReflectAtr().value;
        extraWorkTimeReflectAtr = reflect.getExtraWorkReflectAtr().value;
        childCareTimeReflectAtr = reflect.getChildCareReflectAtr().value;
        supTimeReflectAtr = reflect.getSupportReflectAtr().value;
        careTimeReflectAtr = reflect.getCareReflectAtr().value;
        goOutTimeReflectAtr = reflect.getGoOutReflectAtr().value;
        breakTimeReflectAtr = reflect.getBreakReflectAtr().value;
    }
}
