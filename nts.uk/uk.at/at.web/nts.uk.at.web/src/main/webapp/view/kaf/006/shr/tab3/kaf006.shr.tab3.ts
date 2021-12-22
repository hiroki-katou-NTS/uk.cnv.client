module nts.uk.at.view.kaf006.shr.tab3.viewmodel {

    @component({
        name: 'kaf006-shr-tab3',
        template: `
        <div id="kaf006tab3">
            <div data-bind="ntsCheckBox: {
                checked: isChangeWorkHour,
                text: $i18n('KAF006_18'),
                enable: $parent.updateMode
            }, visible: $parent.condition11" style="margin-top: -5px"></div>
            <div style="width: 700px;" data-bind="visible: $parent.condition11">
                <hr/>
            </div>
            <div class="table mv-13" data-bind="visible: $parent.condition11">
                <div class="cell col-1"></div>
                <div class="cell">
                    <div>
                        <div class="cell col-1">
                            <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_19')"></div>
                        </div>
                        <div class="cell" style="vertical-align: middle">
                            <button style="margin-right: 5px;" data-bind="text: $i18n('KAF006_20'), enable: $parent.isChangeWorkHour() && $parent.updateMode(), click: openKDL003"></button>
                        </div>
                        <div class="cell valign-center" data-bind="text: $parent.selectedWorkTimeDisp"></div>
                    </div>
                    <div style="padding-top: 10px;">
                        <div class="cell col-1">
                            <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_22')"></div>
                        </div>
                        <div class="cell valign-center">
                            <input style="width: 120px;" data-bind="ntsTimeWithDayEditor: {
                                name: $i18n('KAF006_58'),
                                value: $parent.startTime1,
                                constraint: 'TimeWithDayAttr',
                                option: ko.mapping.fromJS(new nts.uk.ui.option.TimeWithDayAttrEditorOption({
                                    timeWithDay: true,
                                    width: '120',
                                    textalign: 'center'
                                })), enable: $parent.isChangeWorkHour() && $parent.condition30() && $parent.updateMode() && $parent.flowWorkFlag()
                            }" />
                            <span data-bind="text: $i18n('KAF006_47')"></span>
                            <input style="width: 120px;" data-bind="ntsTimeWithDayEditor: {
                                name: $i18n('KAF006_59'),
                                value: $parent.endTime1,
                                constraint: 'TimeWithDayAttr',
                                option: ko.mapping.fromJS(new nts.uk.ui.option.TimeWithDayAttrEditorOption({
                                    timeWithDay: true,
                                    width: '120',
                                    textalign: 'center'
                                })), enable: $parent.isChangeWorkHour() && $parent.condition30() && $parent.updateMode() && $parent.flowWorkFlag()
                            }" />
                        </div>
                    </div>
                    <div style="padding-top: 10px;" data-bind="visible: $parent.condition12">
                        <div class="cell col-1">
                            <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_23')"></div>
                        </div>
                        <div class="cell valign-center">
                            <input style="width: 120px;" data-bind="ntsTimeWithDayEditor: {
                                name: $i18n('KAF006_60'),
                                value: $parent.startTime2,
                                constraint: 'TimeWithDayAttr',
                                option: ko.mapping.fromJS(new nts.uk.ui.option.TimeWithDayAttrEditorOption({
                                    timeWithDay: true,
                                    width: '120',
                                    textalign: 'center'
                                })), enable: $parent.isChangeWorkHour() && $parent.condition30() && $parent.updateMode() && $parent.flowWorkFlag()
                            }" />
                            <span data-bind="text: $i18n('KAF006_47')"></span>
                            <input style="width: 120px;" data-bind="ntsTimeWithDayEditor: {
                                name: $i18n('KAF006_61'),
                                value: $parent.endTime2,
                                constraint: 'TimeWithDayAttr',
                                option: ko.mapping.fromJS(new nts.uk.ui.option.TimeWithDayAttrEditorOption({
                                    timeWithDay: true,
                                    width: '120',
                                    textalign: 'center'
                                })), enable: $parent.isChangeWorkHour() && $parent.condition30() && $parent.updateMode() && $parent.flowWorkFlag()
                            }" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="table" data-bind="visible: $parent.condition9">
                <div class="cell" style="width: 500px">
                    <div class="table" style="padding-bottom: 13px" data-bind="visible: $parent.condition6">
                        <div class="cell col-1" data-bind="visible: $parent.condition6">
                            <div class="cell valign-center required" data-bind="ntsFormLabel:{ required: true }, text: $i18n('KAF006_33')"></div>
                        </div>
                        <div class="cell valign-center" data-bind="visible: $parent.condition6">
                            <div id="relation-list" style="vertical-align: bottom;" data-bind="ntsComboBox: {
                                name: $i18n('KAF006_33'),
                                options: dateSpecHdRelationLst,
                                optionsValue: 'relationCD',
                                optionsText: 'relationName',
                                value: selectedDateSpec,
                                required: true,
                                enable: $parent.updateMode
                            }"></div>
                        </div>
                        <div class="cell valign-center" style="padding-left: 10px;">
                            <div class="cell valign-center" data-bind="ntsCheckBox: {
                                checked: $parent.isCheckMourn,
                                text: $i18n('KAF006_34'),
                                enable: $parent.isDispMourn && $parent.updateMode
                            }, visible: $parent.condition7"></div>
                        </div>
                    </div>
                    <div class="table" data-bind="visible: $parent.condition8">
                        <div class="cell col-1">
                            <div class="cell valign-center required" data-bind="ntsFormLabel:{ required: true }, text: $i18n('KAF006_43')"></div>
                        </div>
                        <div class="cell valign-center">
                            <input id="relaReason" style="width: 350px; vertical-align: bottom;" data-bind="ntsTextEditor: {
                                value: relationshipReason,
                                name: $i18n('KAF006_43'),
                                required: true,
                                option: {
                                    placeholder: $i18n('KAF006_45')
                                },
                                enable: $parent.updateMode
                            }" />
                        </div>
                    </div>
                </div>
                <div class="cell vertical-align" data-bind="visible: $parent.condition9">
                    <div style="margin-left: 10px; white-space: pre-wrap;" class="panel panel-frame" data-bind="text: $parent.maxNumberOfDay"></div>
                </div>
            </div>
            <div class="table mv-13" data-bind="visible: $parent.condition15">
                <div class="cell col-1">
                    <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_88')"></div>
                </div>
                <div class="cell valign-center">
                    <div style="vertical-align: middle; display: inline-block">
                        <button style="width: 60px; margin-right: 5px;" data-bind="text: $i18n('KAF006_50'), enable: !_.isEmpty($parent.application().appDate()) && $parent.updateMode() && $parent.checkAppDate(), click: openKDL036"></button>
                    </div>
                    <div style="vertical-align: middle; display: inline-block">
                        <div style="display: inline-block; vertical-align: top;" data-bind="text: $i18n('KAF006_89')"></div>
                    </div>
                </div>
            </div>
            <div class="table mv-13" style="margin: 10px 0;" data-bind="visible: $parent.condition15() && $parent.leaveComDayOffManas().length > 0">
                <div class="cell col-1"></div>
                <div class="cell">
                    <table data-bind="visible: $parent.leaveComDayOffManas().length > 0">
                        <thead>
                            <tr class="text-center bg-green">
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_53')"></th>
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_87')"></th>
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_55')"></th>
                            </tr>
                        </thead>
                        <tbody data-bind="foreach: ko.toJS($parent.leaveComDayOffManas)">
                            <tr>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.time.formatDate(new Date(outbreakDay), 'yyyy/MM/ddD')"></td>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.time.formatDate(new Date(dateOfUse), 'yyyy/MM/ddD')"></td>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.resource.getText('KAF006_46', [dayNumberUsed])"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="table mv-13" data-bind="visible: $parent.condition14">
                <div class="cell col-1">
                    <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_48')"></div>
                </div>
                <div class="cell valign-center">
                    <div style="vertical-align: middle; display: inline-block">
                        <button style="width: 60px; margin-right: 5px;" data-bind="text: $i18n('KAF006_50'), enable: !_.isEmpty($parent.application().appDate()) && $parent.updateMode() && $parent.checkAppDate(), click: openKDL035"></button>
                    </div>
                    <div style="vertical-align: middle; display: inline-block">
                        <div style="display: inline-block; vertical-align: top;" data-bind="text: $i18n('KAF006_62')"></div>
                    </div>
                </div>
            </div>
            <div class="table mv-13" style="margin: 10px 0;" data-bind="visible: $parent.condition14() && $parent.payoutSubofHDManagements().length > 0">
                <div class="cell col-1"></div>
                <div class="cell">
                    <table data-bind="visible: $parent.payoutSubofHDManagements().length > 0" >
                        <thead>
                            <tr class="bg-green">
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_52')"></th>
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_95')"></th>
                                <th style="width: 120px;" class="py-10 table-border text-center" data-bind="text: $i18n('KAF006_55')"></th>
                            </tr>
                        </thead>
                        <tbody data-bind="foreach: ko.toJS($parent.payoutSubofHDManagements)">
                            <tr>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.time.formatDate(new Date(outbreakDay), 'yyyy/MM/ddD')"></td>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.time.formatDate(new Date(dateOfUse), 'yyyy/MM/ddD')"></td>
                                <td class="py-10 table-border text-center" data-bind="text: nts.uk.resource.getText('KAF006_46', [dayNumberUsed])"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        `
    })

    class Kaf006Tab3ViewModel extends ko.ViewModel {
        // workTypeLst: KnockoutObservableArray<any> = ko.observableArray([]);
        // selectedWorkTypeCD: KnockoutObservable<any>;
        isChangeWorkHour: KnockoutObservable<boolean> = ko.observable(false);
        dateSpecHdRelationLst: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedDateSpec: KnockoutObservable<any> = ko.observable();
        relationshipReason: KnockoutObservable<string> = ko.observable();

        created(params: any) {
            const vm = this;

            if (params) {
                // vm.workTypeLst = params.workTypeLst;
                // vm.selectedWorkTypeCD = params.selectedWorkTypeCD;
                vm.dateSpecHdRelationLst = params.dateSpecHdRelationLst;
                vm.selectedDateSpec = params.selectedDateSpec;
                vm.relationshipReason = params.relationshipReason;
                vm.isChangeWorkHour = params.isChangeWorkHour;
            }
        }

        mounted() {

        }

        public openKDL036() {
            ko.contextFor(this.$el).$data.openKDL036();
        }

        public openKDL035() {
            ko.contextFor(this.$el).$data.openKDL035();
        }

        public openKDL003() {
            ko.contextFor(this.$el).$data.openKDL003();
        }
    }
}