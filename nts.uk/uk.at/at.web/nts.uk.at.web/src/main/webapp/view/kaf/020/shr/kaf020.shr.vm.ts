module nts.uk.at.view.kaf020.shr.viewmodel {
    @component({
        name: 'kaf020-share',
        template: `<!--B2_3-->
            <div id="kaf020-component">
            <div class="label m1" data-bind="ntsFormLabel : { text: name } "></div>
            <!--B2_4-->
            <div id="ruredLine"></div>
            <table id="kaf020-fixed-table">
                <colgroup>
                    <col width="200px"/>
                    <col width="120px"/>
                    <col width="40px"/>
                    <col width="300px"/>
                </colgroup>
                <thead>
                <tr>
                    <!--B3_1-->
                    <th class="ui-widget-header" data-bind="text: $vm.$i18n('KAF020_19')"></th>
                    <!--B3_2-->
                    <th colspan="2" class="ui-widget-header" data-bind="text: $vm.$i18n('KAF020_20')"></th>
                    <!--B3_3-->
                    <th class="ui-widget-header" data-bind="text: $vm.$i18n('KAF020_21')"></th>
                </tr>
                </thead>
                <tbody data-bind="foreach: applicationContents">
                <tr>
                    <td>
                        <!--B4_1-->
                        <div class="label" style="white-space: normal" data-bind="text: optionalItemName"></div>
                    </td>
                    <td style="vertical-align: top">
                        <!--B4_2-->
                        <!--time-->
                        <div data-bind="if: optionalItemAtr == 0">
                            <input class="input" tabindex="0"
                                   data-bind="ntsTimeEditor: {
                                            name: '#[KAF020_22]',
                                            constraint: 'AnyItemTime',
                                            value: time,
                                            inputFormat: 'time',
                                            mode: 'time',
                                            enable: $parent.enableEdit() 
                                        }, attr: {id: optionalItemNo}"/>
                        </div>
                        <!--number-->
                        <div data-bind="if: optionalItemAtr == 1">
                            <input class="input" tabindex="0"
                                   data-bind="ntsNumberEditor: {
                                            name: '#[KAF020_22]',
                                            value: times,
                                            constraint: 'AnyItemTimes',
                                            option: {grouplength: 3, decimallength: 2},
                                            enable: $parent.enableEdit() 
                                       }, attr: {id: optionalItemNo}"/>
                        </div>
                        <!--amount-->
                        <div data-bind="if: optionalItemAtr == 2">
                            <input class="input" tabindex="0"
                                   data-bind="ntsNumberEditor: {
                                            name: '#[KAF020_22]',
                                            value: amount,
                                            constraint: 'AnyItemAmount',
                                            option: {grouplength: 3},
                                            enable: $parent.enableEdit() 
                                       }, attr: {id: optionalItemNo}"/>
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <!--B4_3-->
                        <div style="margin-top: 10px" class="label" data-bind="text: unit"></div>
                    </td>
                    <td>
                        <div data-bind="text: description" style="white-space: normal; word-break: break-all;"></div>
                        <div data-bind="if: lowerCheck &#124;&#124; upperCheck &#124;&#124; unit &#124;&#124; optionalItemAtr == 0">
                            <span class="label" style="margin-top: 6px">
                                <span data-bind="if:lowerCheck &#124;&#124; upperCheck &#124;&#124; unit &#124;&#124; optionalItemAtr == 0">
                                    <span data-bind="text: $vm.$i18n('KAF020_25')"></span>
                                </span>
                                <span data-bind="if: lowerCheck &#124;&#124; upperCheck">
                                    <span data-bind="text: $vm.$i18n('KAF020_26')"></span>
                                </span>
                                <span data-bind="if: lowerCheck">
                                    <span data-bind="if: optionalItemAtr == 0">
                                        <span data-bind="text: timeLower"></span>
                                    </span>
                                    <span data-bind="if: optionalItemAtr == 1">
                                        <span data-bind="text: numberLower"></span>
                                    </span>
                                    <span data-bind="if: optionalItemAtr == 2">
                                        <span data-bind="text: amountLower"></span>
                                    </span>
                                </span>
                                <span data-bind="if: lowerCheck &#124;&#124; upperCheck">
                                    <span data-bind="text: $vm.$i18n('KAF020_27')"></span>
                                </span>
                                <span data-bind="if: upperCheck">
                                    <span data-bind="if: optionalItemAtr == 0">
                                        <span data-bind="text: timeUpper"></span>
                                    </span>
                                    <span data-bind="if: optionalItemAtr == 1">
                                        <span data-bind="text: numberUpper"></span>
                                    </span>
                                    <span data-bind="if: optionalItemAtr == 2">
                                        <span data-bind="text: amountUpper"></span>
                                    </span>
                                </span>
                                <span data-bind="if: lowerCheck &amp;&amp; upperCheck">
                                    <span data-bind="text: '、'"></span>
                                </span>
                                <span data-bind="if: unit &#124;&#124; optionalItemAtr == 0">
                                    <span data-bind="text: inputUnitOfTimeItem"></span>
                                    <span data-bind="if: optionalItemAtr == 0">
                                        <span data-bind="text: $vm.$i18n('KAF020_32')"></span>
                                    </span>
                                    <span data-bind="ifnot: optionalItemAtr == 0">
                                        <span data-bind="text: unit"></span>
                                    </span>
                                    <span data-bind="text: $vm.$i18n('KAF020_28')"></span>
                                </span>
                                <span data-bind="if:lowerCheck &#124;&#124; upperCheck &#124;&#124; unit &#124;&#124; optionalItemAtr == 0">
                                    <span data-bind="text: $vm.$i18n('KAF020_29')"></span>
                                </span>
                            </span>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
            </div>
           `,
    })

    class Kaf020ShareViewModel extends ko.ViewModel {
        dataFetch: KnockoutObservable<any> = ko.observable(null);
        applicationContents: KnockoutObservableArray<Content>;
        name: KnockoutObservable<string> = ko.observable();
        enableEdit: KnockoutObservable<boolean> = ko.observable(true);

        created(params: any) {
            const vm = this;
            vm.dataFetch = params.dataFetch;
            vm.applicationContents = vm.dataFetch().applicationContents;
            vm.dataFetch.subscribe(value => {
                vm.applicationContents(value.applicationContents());
                vm.name(value.name);

                if (value.appDispInfoStartupOutput() && value.appDispInfoStartupOutput().appDetailScreenInfo) {
                    value.appDispInfoStartupOutput().appDetailScreenInfo.outputMode == 1 ? vm.enableEdit(true) : vm.enableEdit(false);
                }
            });
            $('#kaf020-fixed-table').ntsFixedTable({width: 740});
        }
    }

    export interface Content {
        optionalItemName: string;
        optionalItemNo: number;
        optionalItemAtr: number;
        unit: string;
        inputUnitOfTimeItem: number;
        timeUpper: number;
        timeLower: number;
        numberUpper: number;
        lowerCheck: boolean;
        upperCheck: boolean;
        numberLower: number;
        amountLower: number;
        amountUpper: number;
        description: string;
        time: KnockoutObservable<string>;
        times: KnockoutObservable<number>;
        amount: KnockoutObservable<number>;
        detail: string;
        dispOrder: number;
    }
}