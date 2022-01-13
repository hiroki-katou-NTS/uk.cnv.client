module nts.uk.ui.at.kdw013.setting {
    
    const storeSetting = (setting?: SettingStore): JQueryPromise<SettingStore | undefined> => {
        const vm = new ko.ViewModel();

        return vm.$window
            .storage('KDW013_SETTING', setting)
            .then((value: any) => value);
    };

    @component({
        name: 'fc-setting-panel',
        template: `
                <div id='fc'>
                    <table>
                        <tbody>
                            <tr>
                                <td style='width:' data-bind="i18n: 'KDW013_13'"></td>
                                <td>
                                    <div style="margin-left: 15px;" data-bind="ntsComboBox: {
                                        width:'85px',
                                        options: $component.firstDays,
                                        optionsValue: 'id',
                                        visibleItemsCount: 20,
                                        value: $component.params.firstDay,
                                        optionsText: 'title',
                                        editable: false,
                                        required: false,
                                        selectFirstIfNull: false,
                                        dropDownAttachedToBody: false,
                                        columns: [{ prop: 'title', length: 2 }]}" >
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td data-bind="i18n: 'KDW013_14'"></td>
                                <td>
                                <div style="margin-left: 15px;" data-bind="ntsComboBox: {
                                        width:'85px',
                                        options: $component.timeList,
                                        optionsValue: 'value',
                                        visibleItemsCount: 7,
                                        value: $component.params.scrollTime,
                                        optionsText: 'text',
                                        editable: false,
                                        required: false,
                                        selectFirstIfNull: false,
                                        dropDownAttachedToBody: false,
                                        columns: [{ prop: 'text', length: 2 }]}" >
                                </td>
                            </tr>
                            <tr>
                                <td data-bind="i18n: 'KDW013_15'"></td>
                                <td>
                                    <div style="margin-left: 15px;" data-bind="ntsComboBox: {
                                        width:'85px',
                                        options: $component.slotDurations,
                                        optionsValue: 'id',
                                        visibleItemsCount: 20,
                                        value: $component.params.slotDuration,
                                        optionsText: 'title',
                                        editable: false,
                                        required: false,
                                        selectFirstIfNull: false,
                                        dropDownAttachedToBody: false,
                                        columns: [{ prop: 'title', length: 2 }]}" >
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <style rel="stylesheet">
                    .fc-popup-setting tr {
                        height: 34px;
                    }
                    .fc-popup-setting tr:not(:first-child) td {
                        padding-top: 5px;
                    }
                    .fc-popup-setting tr input,
                    .fc-popup-setting tr select {
                        width: 85px;
                        height: 34px;
                        margin-left: 15px;
                        box-sizing: border-box;
                    }
                    .fc-popup-setting tr input {
                        text-align: right;
                    }
                </style>
            `
    })
    export class FullCalendarSettingViewmodel extends ko.ViewModel {
        firstDays: KnockoutObservableArray<{ id: number; title: string; }> = ko.observableArray([]);
        slotDurations: KnockoutObservableArray<{ id: number; title: string; }> = ko.observableArray([]);
        timeList: KnockoutObservableArray<{ value: number; text: string; }> = ko.observableArray([]);

        constructor(private params: SettingApi & { position: KnockoutObservable<any | null> }) {
        super();


        const vm = this;

        let times = [];
        for (let i = 0; i < 49; i++) {
            var value = i * 30;
            times.push({ value: value, text: nts.uk.time.format.byId("Clock_Short_HM", value) });
        }

        vm.timeList(times);
        // resource for slotDuration
        const resource = [
            'KDW013_16',
            'KDW013_17',
            'KDW013_18',
            'KDW013_19'
        ];

        const startDate = moment().isoWeekday(1);
        const listDates = _.range(0, 7)
            .map(m => startDate.clone().add(m, 'day'))
            .map(d => ({
                id: d.get('day'),
                title: d.format('dddd')
            }));

        vm.firstDays(listDates);

        vm.slotDurations([5, 10, 15, 30].map((id: number, index: number) => ({ id, title: vm.$i18n(resource[index]) })));
    }

    mounted() {
        const vm = this;
        const { params } = vm;
        const state = { open: false };
        const { firstDay, scrollTime, slotDuration, position, initialView} = params;

        // store all value to charactorgistic domain
        ko.computed({
            read: () => {
                const ps = ko.unwrap(position);
                const fd = ko.unwrap(firstDay);
                const sc = ko.unwrap(scrollTime);
                const sd = ko.unwrap(slotDuration);
                const iv = ko.unwrap(initialView);
                // store when popup opened
                if (state.open) {
                    storeSetting().then((value) => {
                        value = value ? value : {
                            firstDay: fd,
                            scrollTime: sc,
                            slotDuration: sd,
                            initialView: iv
                        };
                        value.firstDay = fd;
                        value.scrollTime = sc;
                        value.slotDuration = sd;
                        value.initialView = value.initialView;

                        storeSetting(value);
                    });
                } else if (ps) {
                    state.open = true;
                }
            },
            disposeWhenNodeIsRemoved: vm.$el
        });

        $(vm.$el)
            .removeAttr('data-bind')
            .find('[data-bind]')
            .removeAttr('data-bind');
    }
}
}