module nts.uk.at.view.kaf006.shr.tab2.viewmodel {

    @component({
        name: 'kaf006-shr-tab2',
        template: `
        <div id="kaf006tab2">
            <div class="table mv-13">
                <div class="cell col-1" style="vertical-align: top;">
                    <div class="cell valign-center" data-bind="ntsFormLabel:{ required: true }, text: $i18n('KAF006_28')"></div>
                </div>
                <div class="cell">
                    <div class="table" style="padding-bottom: 5px;" data-bind="visible: $parent.condition19Over60">
                        <div class="cell col-1" data-bind="text: $i18n('Com_ExsessHoliday')"></div>
                        <input id="over60H" style="width: 50px;" class="cell" data-bind="ntsTimeEditor: {
                            name: $i18n('Com_ExsessHoliday'),
                            value: $parent.over60H,
                            constraint: 'TimeOffPrimitive',
                            mode: 'time',
                            enable: $parent.updateMode, 
                            requỉed: true,
                            option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                                textalign: 'center'
                            }))
                        }" />
                    </div>
                    <div class="table" style="padding-bottom: 5px;" data-bind="visible: $parent.condition19Substitute">
                        <div class="cell col-1" data-bind="text: $i18n('KAF006_30')"></div>
                        <input id="timeOff" style="width: 50px;" class="cell" data-bind="ntsTimeEditor: {
                            name: $i18n('KAF006_30'),
                            value: $parent.timeOff,
                            constraint: 'TimeOffPrimitive',
                            mode: 'time',
                            enable: $parent.updateMode,
                            option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                                textalign: 'center'
                            }))
                        }" />
                    </div>
                    <div class="table" style="padding-bottom: 5px;" data-bind="visible: $parent.condition19Annual">
                        <div class="cell col-1" data-bind="text: $i18n('KAF006_29')"></div>
                        <input id="annualTime" style="width: 50px;" class="cell" data-bind="ntsTimeEditor: {
                            name: $i18n('KAF006_29'),
                            value: $parent.annualTime,
                            constraint: 'TimeOffPrimitive',
                            mode: 'time',
                            enable: $parent.updateMode, 
                            requỉed: true,
                            option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                                textalign: 'center'
                            }))
                        }" />
                    </div>
                    <div class="table" style="padding-bottom: 5px;" data-bind="visible: $parent.condition19ChildNursing">
                        <div class="cell col-1" data-bind="text: $i18n('Com_ChildNurseHoliday')"></div>
                        <input id="childNursing" style="width: 50px;" class="cell" data-bind="ntsTimeEditor: {
                            name: $i18n('Com_ChildNurseHoliday'),
                            value: $parent.childNursing,
                            constraint: 'TimeOffPrimitive',
                            mode: 'time',
                            enable: $parent.updateMode, 
                            requỉed: true,
                            option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                                textalign: 'center'
                            }))
                        }" />
                    </div>
                    <div class="table" style="padding-bottom: 5px;" data-bind="visible: $parent.condition19Nursing">
                        <div class="cell col-1" data-bind="text: $i18n('Com_CareHoliday')"></div>
                        <input id="nursing" style="width: 50px;" class="cell" data-bind="ntsTimeEditor: {
                            name: $i18n('Com_CareHoliday'),
                            value: $parent.nursing,
                            constraint: 'TimeOffPrimitive',
                            mode: 'time',
                            enable: $parent.updateMode, 
                            requỉed: true,
                            option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                                textalign: 'center'
                            }))
                        }" />
                    </div>
                    <hr style="width: 250px; text-align: center;"/>
                    <div class="table">
                        <div class="cell col-1" data-bind="text: $i18n('KAF006_31')"></div>
                        <div class="cell" data-bind="text: total" style="width: 85px; text-align: center;"></div>
                        <div class="cell" data-bind="text: $i18n('KAF006_32')"></div>
                        <div class="cell" data-bind="text: $parent.timeRequired"></div>
                    </div>
                </div>
            </div>
            <div data-bind="visible: $parent.condition11">
                <div style="" data-bind="ntsCheckBox: {
                    checked: isChangeWorkHour,
                    text: $i18n('KAF006_18'),
                    enable: $parent.updateMode
                }" style="margin-top: -5px"></div>
                <div style="width: 700px;">
                    <hr data-bind="visible: $parent.condition11" />
                </div>
                <div class="table mv-13">
                    <div class="cell col-1"></div>
                    <div class="cell">
                        <div style="padding-bottom: 10px;">
                            <div class="cell col-1">
                                <div class="valign-center cell" data-bind="ntsFormLabel:{ required: false }, text: $i18n('KAF006_19')"></div>
                            </div>
                            <div class="cell valign-center">
                                <button style="margin-right: 5px;" data-bind="text: $i18n('KAF006_20'), enable: $parent.isChangeWorkHour() && $parent.updateMode(), click: openKDL003"></button>
                            </div>
                            <div class="cell valign-center" data-bind="text: $parent.selectedWorkTimeDisp"></div>
                        </div>
                        <div style="padding-bottom: 10px;">
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
                        <div style="padding-bottom: 10px;" data-bind="visible: $parent.condition12">
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
            <div class="table mv-13" data-bind="visible: $parent.condition15() && $parent.leaveComDayOffManas().length > 0">
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
            <div class="table mv-13" data-bind="visible: $parent.condition14() && $parent.payoutSubofHDManagements().length > 0">
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

    class Kaf006Tab2ViewModel extends ko.ViewModel {
        // workTypeLst: KnockoutObservableArray<any> = ko.observableArray([]);
        // selectedWorkTypeCD: KnockoutObservable<any>;
        isChangeWorkHour: KnockoutObservable<boolean> = ko.observable(false);

        // 60H超休
        over60H: KnockoutObservable<number> = ko.observable();
        // 時間代休
        timeOff: KnockoutObservable<number> = ko.observable();
        // 時間年休
        annualTime: KnockoutObservable<number> = ko.observable();
        // 子の看護
        childNursing: KnockoutObservable<number> = ko.observable();
        // 介護時間
        nursing: KnockoutObservable<number> = ko.observable();

        total: any = ko.observable('0:00');

        created(params: any) {
            const vm = this;

            if (params) {
                // vm.workTypeLst = params.workTypeLst;
                // vm.selectedWorkTypeCD = params.selectedWorkTypeCD;
                vm.isChangeWorkHour = params.isChangeWorkHour;
                vm.over60H = params.over60H;
                vm.timeOff = params.timeOff;
                vm.annualTime = params.annualTime;
                vm.childNursing = params.childNursing;
                vm.nursing = params.nursing;
            }

            // vm.total = ko.observable(nts.uk.time.format.byId("Time_Short_HM", (vm.over60H() ? vm.over60H() : 0) + (vm.timeOff() ? vm.timeOff() : 0) 
            //     + (vm.annualTime() ? vm.annualTime() : 0) + (vm.childNursing() ? vm.childNursing() : 0) + (vm.nursing() ? vm.nursing() : 0)));
            vm.total = ko.computed(() => {
                return nts.uk.time.format.byId("Time_Short_HM", ((vm.over60H() && vm.over60H() <= 2880) ? vm.over60H() : 0) 
                    + ((vm.timeOff() && vm.timeOff() <= 2880) ? vm.timeOff() : 0) 
                    + ((vm.annualTime() && vm.annualTime() <= 2880) ? vm.annualTime() : 0) 
                    + ((vm.childNursing() && vm.childNursing() <= 2880) ? vm.childNursing() : 0) 
                    + ((vm.nursing() && vm.nursing() <= 2880) ? vm.nursing() : 0));
            });
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