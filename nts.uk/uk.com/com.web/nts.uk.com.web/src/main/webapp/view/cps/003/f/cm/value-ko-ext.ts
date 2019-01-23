module nts.custombinding {
    // define ITEM_SINGLE_TYPE
    // type of item if it's single item
    enum ITEM_SINGLE_TYPE {
        STRING = 1,
        NUMERIC = 2,
        DATE = 3,
        TIME = 4,
        TIMEPOINT = 5,
        SELECTION = 6,
        SEL_RADIO = 7,
        SEL_BUTTON = 8,
        READONLY = 9,
        RELATE_CATEGORY = 10,
        NUMBERIC_BUTTON = 11,
        READONLY_BUTTON = 12
    }

    interface IItemData {
        constraint: string;
        itemCode: string;
        dataType: ITEM_SINGLE_TYPE;
        amount: number;
        decimalLength?: number;
        selectionItems: Array<any>;
    }

    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    const timewd = window['nts']['uk']['time']['minutesBased']['clock']['dayattr']['create'],
        fetch = (codes?: string[]) => nts.uk.request.ajax('com', 'ctx/pereg/grid-layout/get-item/name', { itemCodes: codes || ['IS00020', 'IS00279', 'IS00253'] });

    export class ValueBoxControl implements KnockoutBindingHandler {
        init = (element: HTMLElement, valueAccessor: () => { itemData: KnockoutObservable<IItemData>; value: KnockoutObservable<any>; enable: KnockoutObservable<boolean>; }, allBindingsAccessor: () => { settingChange: Function }, viewModel: any, bindingContext: KnockoutBindingContext) => {
            let accessor = valueAccessor(),
                itemData: IItemData = ko.toJS(accessor.itemData),
                template = {
                    str: `<input data-bind="ntsTextEditor: { name: i18n('CPS003_81'), value: value, constraint: constraint, enable: true, required: true }" />`,
                    numb: `<input data-bind="ntsNumberEditor: { name: i18n('CPS003_81'), value: value, constraint: constraint, enable: true, required: true, option: options }" />`,
                    amount: `<div class='number-group-box' data-bind="let: { m1: ko.computed(function() { return mode() == '1'; }), m2: ko.computed(function() { return mode() == '2'; }) }">
                                <div class="grant-selection-group" data-bind="ntsRadioBoxGroup: {
                                    options: ko.observableArray([
                                        { id: '1', name: '' },
                                        { id: '2', name: '' }]),
                                    optionsValue: 'id',
                                    optionsText: 'name',
                                    value: mode,
                                    enable: true
                                }"></div>
                                <div class="text-box">
                                    <input data-bind="ntsNumberEditor: { name: '', value: value, constraint: constraint, enable: m1, required: m1, option: options }" />
                                </div>
                                <div class="dropdown-box">
                                    <div id="pk-amount" class="pk-amount" data-bind="ntsComboBox: {
                                        width: '60px',
                                        name: i18n('CPS003_84'),
                                        options: ko.observableArray([{ optionValue: 'plus', optionText: '+' }, { optionValue: 'minus', optionText: '-' }]),
                                        optionsValue: 'optionValue',
                                        visibleItemsCount: 5,
                                        value: value1,
                                        optionsText: 'optionText',
                                        editable: false,
                                        required: m2,
                                        selectFirstIfNull: true,
                                        enable: m2,
                                        columns: [{ prop: 'optionText', length: 10 }]}"></div>
                                    <input data-bind="ntsNumberEditor: { name: '', value: value2, constraint: constraint, enable: m2, required: m2, option: options }" />
                                </div>
                                <div class="" data-bind="text: i18n('CPS003_86')"></div>
                            </div>`,
                    grDate: `<div class='selection-group-box' data-bind="let: { m3: ko.computed(function() { return mode() == '3' }), m4: ko.computed(function() { return mode() == '4' }) }">
                                <div class="grant-selection-group" data-bind="ntsRadioBoxGroup: {
                                    options: ko.observableArray([
                                        { id: '1', name: i18n('CPS003_88', [itemNames()['IS00020']]) },
                                        { id: '2', name: i18n('CPS003_88', [itemNames()['IS00279']]) },
                                        { id: '3', name: ''}, 
                                        { id: '4', name: ''}]),
                                    optionsValue: 'id',
                                    optionsText: 'name',
                                    value: mode,
                                    enable: true
                                }"></div>
                                <div class="dropdown-box">
                                    <div class="pk-year" data-bind="ntsComboBox: {
                                        width: '140px',
                                        name: i18n('CPS003_89'),
                                        options: ko.observableArray([
                                            {optionValue: '1', optionText: i18n('CPS003_131') },
                                            {optionValue: '2', optionText: i18n('CPS003_132') },
                                            {optionValue: '3', optionText: i18n('CPS003_133') }
                                        ]),
                                        optionsValue: 'optionValue',
                                        visibleItemsCount: 5,
                                        value: value,
                                        optionsText: 'optionText',
                                        editable: false,
                                        required: m3,
                                        selectFirstIfNull: true,
                                        enable: m3,
                                        columns: [
                                            { prop: 'optionText', length: 10 },
                                        ]}"></div>
                                    <div id="monthdays" data-bind="ntsMonthDays: { value: value1, enable: m3, required: m3 }"/>
                                </div>
                                <div class="text-box">
                                    <div data-bind="ntsDatePicker: { value: value2, constraint: constraint, enable: m4, required: m4, dateFormat: 'YYYY/MM/DD' }"></div>
                                </div>
                            </div>`,
                    date: `<div data-bind="ntsDatePicker: { value: value, constraint: constraint, enable: true, required: true, dateFormat: 'YYYY/MM/DD' }"></div>`,
                    time: `<input data-bind="ntsTimeEditor: { name: i18n('CPS003_81'), value: value, constraint: constraint, inputFormat: 'time', mode: 'time', enable: true, required: true }" />`,
                    timey: `<div class='stime-group-box' data-bind="let: { m1: ko.computed(function() { return mode() == '1'; }), m2: ko.computed(function() { return mode() == '2'; }) }">
                                <div class="grant-selection-group" data-bind="ntsRadioBoxGroup: {
                                    options: ko.observableArray([
                                        { id: '1', name: i18n('CPS003_91', [itemNames()['IS00253']]) },
                                        { id: '2', name: '' }]),
                                    optionsValue: 'id',
                                    optionsText: 'name',
                                    value: mode,
                                    enable: true
                                }"></div>
                                <div class="text-box-x">
                                    <input data-bind="ntsNumberEditor: {
                                        name: 'Number', 
                                        value: value,
                                        constraint: '',
                                        option: {
                                            width: '80px',
                                            defaultValue: 0,
                                            unitID: 'DAYS'
                                        },
                                        required: m1,
                                        enable: m1
                                    }" />
                                </div>
                                <div class="text-box-a">
                                    <input data-bind="ntsTimeEditor: { value: value1, constraint: constraint, inputFormat: 'time', mode: 'time', enable: m2, required: m2 }" />
                                </div>
                            </div>`,
                    timep: `<input data-bind="ntsTimeWithDayEditor: { name: i18n('CPS003_81'), value: value, constraint: constraint, enable: true, required: true }" />`,
                    radio: `<div id="value-selection" data-bind="ntsComboBox: {
                                name: '',
                                options: itemOptions,
                                optionsValue: 'optionValue',
                                visibleItemsCount: 5,
                                value: value,
                                optionsText: 'optionText',
                                editable: false,
                                required: true,
                                selectFirstIfNull: true,
                                enable: true,
                                columns: [
                                    { prop: 'optionText', length: 10 },
                                ]}"></div>
                            <button data-bind="ntsHelpButton: { textId: 'CPS003_118', position: 'bottom right' }">？</button>`,
                    button: `<button data-bind="text: i18n('CPS001_106'), enable: true, click: openDialog"></button>
                             <label class="value-text readonly" data-bind="html: textValue"></label>`,
                    buttonWt: `<button data-bind="text: i18n('CPS001_106'), enable: true, click: openDialog"></button>
                               <div class="worktime">
                                 <label class="value-text readonly" data-bind="html: textValue"></label>
                                 <label class="value-text readonly" data-bind="html: textWtValue1"></label>
                                 <label class="value-text readonly" data-bind="html: textWtValue2"></label>
                                </div>`
                }, vm = {
                    itemNames: ko.observable({}),
                    i18n: nts.uk.resource.getText,
                    mode: ko.observable('1'),
                    value: ko.observable(),
                    value1: ko.observable(),
                    value2: ko.observable(),
                    constraint: '',
                    options: {
                        decimallength: 4,
                        grouplength: 0,
                    },
                    textValue: ko.observable(''),
                    textWtValue1: ko.observable(''),
                    textWtValue2: ko.observable(''),
                    itemOptions: ko.observableArray([]),
                    openDialog: () => {
                        let itemData = ko.toJS(accessor.itemData);

                        if (['IS00131', 'IS00140',
                            'IS00158', 'IS00167',
                            'IS00176', 'IS00149',
                            'IS00194', 'IS00203',
                            'IS00212', 'IS00221',
                            'IS00230', 'IS00239',
                            'IS00185'].indexOf(itemData.itemCode) > -1) {

                            setShared("kml001multiSelectMode", false);
                            setShared("kml001selectedCodeList", []);
                            setShared("kml001isSelection", true);
                            setShared("kml001selectAbleCodeList", itemData.selectionItems.map(x => x.optionValue), true);

                            modal('at', '/view/kdl/001/a/index.xhtml').onClosed(() => {
                                let childData: Array<any> = getShared('kml001selectedTimes');

                                if (childData) {
                                    if (childData.length > 0) {
                                        let data: any = childData[0];

                                        vm.value(data.selectedWorkTimeCode);
                                        vm.textValue(`${data.selectedWorkTimeCode} ${data.selectedWorkTimeName}`);

                                        if (data.first) {
                                            vm.textWtValue1(`${timewd(data.first.start).fullText} ~ ${timewd(data.first.end).fullText}`);
                                        }

                                        if (data.second) {
                                            vm.textWtValue2(`${timewd(data.second.start).fullText} ~ ${timewd(data.second.end).fullText}`);
                                        }
                                    }
                                }
                            });
                        } else {
                            setShared("KDL002_isShowNoSelectRow", true);
                            setShared("KDL002_Multiple", false, true);
                            setShared('kdl002isSelection', false, true);
                            setShared("KDL002_SelectedItemId", [vm.value()], true);
                            setShared("KDL002_AllItemObj", itemData.selectionItems.map(x => x.optionValue), true);

                            modal('at', '/view/kdl/002/a/index.xhtml').onClosed(() => {
                                let childData: Array<any> = getShared('KDL002_SelectedNewItem');

                                if (childData[0]) {
                                    vm.value(childData[0].code);
                                    vm.textValue(`${childData[0].code} ${childData[0].name}`);
                                }
                            });
                        }
                    }
                };

            element.classList.add('v-top');

            // bind data out
            ko.computed({
                read: () => {
                    let mode = ko.toJS(vm.mode),
                        value = ko.toJS(vm.value),
                        value1 = ko.toJS(vm.value1),
                        value2 = ko.toJS(vm.value2);

                    accessor.value({
                        mode: mode,
                        value0: value,
                        value1: value1,
                        value2: value2
                    });
                }
            });

            vm.mode.subscribe(m => {
                $(element).find('input').ntsError('clear');

                setTimeout(() => {
                    if (m == "1") {
                        let input = document.querySelector('.text-box-x input, .number-group-box .text-box input');
                        if (input) {
                            input.focus();
                        }
                    } else if (m == "2") {
                        let input = document.querySelector('.text-box-a input, .number-group-box .dropdown-box .ntsControl');
                        if (input) {
                            input.focus();
                        }
                    } else if (m == "3") {
                        let combx = document.querySelector('.dropdown-box>.ntsControl');
                        if (combx) {
                            combx.focus();
                        }
                    } else if (m == "4") {
                        let input = document.querySelector('.text-box input');
                        if (input) {
                            input.focus();
                        }
                    }
                }, 5);
            });

            // update value of binding
            ko.computed({
                read: () => {
                    let value = ko.toJS(accessor.value);
                }
            });

            ko.computed({
                read: () => {
                    itemData = ko.toJS(accessor.itemData);

                    // clear old value
                    vm.value('');
                    vm.value1('');
                    vm.value2('');
                    vm.textValue('');
                    vm.textWtValue1('');
                    vm.textWtValue2('');

                    vm.constraint = itemData.constraint;

                    // bind items to dropdownList (if avaiable)
                    vm.itemOptions(itemData.selectionItems || []);

                    // clean binding
                    ko.cleanNode(element);
                    nts.uk.ui.errors.clearAll();

                    switch (itemData.dataType) {
                        default:
                            vm.mode(undefined);
                            ko.utils.setHtml(element, '');
                            break;
                        case ITEM_SINGLE_TYPE.DATE:
                            if ([
                                'IS00279', 'IS00295',
                                'IS00302', 'IS00309',
                                'IS00316', 'IS00323',
                                'IS00330', 'IS00337',
                                'IS00344', 'IS00351',
                                'IS00358', 'IS00559',
                                'IS00566', 'IS00573',
                                'IS00580', 'IS00587',
                                'IS00594', 'IS00601',
                                'IS00608', 'IS00615',
                                'IS00622'].indexOf(itemData.itemCode) > -1) {
                                vm.mode('1');
                                ko.utils.setHtml(element, template.grDate);
                            } else {
                                vm.mode(undefined);
                                ko.utils.setHtml(element, template.date);
                            }
                            break;
                        case ITEM_SINGLE_TYPE.STRING:
                            vm.mode(undefined);
                            ko.utils.setHtml(element, template.str);
                            break;
                        case ITEM_SINGLE_TYPE.TIME:
                            if (itemData.itemCode == 'IS00287') {
                                vm.mode('1');
                                ko.utils.setHtml(element, template.timey);
                            } else {
                                vm.mode(undefined);
                                ko.utils.setHtml(element, template.time);
                            }
                            break;
                        case ITEM_SINGLE_TYPE.TIMEPOINT:
                            ko.utils.setHtml(element, template.timep);
                            break;
                        case ITEM_SINGLE_TYPE.NUMERIC:
                            if (!itemData.amount) {
                                vm.mode(undefined);
                                ko.utils.setHtml(element, template.numb);
                                vm.options.grouplength = 0;
                            } else {
                                vm.mode('1');
                                ko.utils.setHtml(element, template.amount);
                                vm.options.grouplength = 3;
                            }

                            vm.options.decimallength = itemData.decimalLength;
                            break;
                        case ITEM_SINGLE_TYPE.SELECTION:
                        case ITEM_SINGLE_TYPE.SEL_RADIO:
                            vm.mode(undefined);
                            ko.utils.setHtml(element, template.radio);
                            break;
                        case ITEM_SINGLE_TYPE.SEL_BUTTON:
                            vm.mode(undefined);
                            if (['IS00131', 'IS00140',
                                'IS00158', 'IS00167',
                                'IS00176', 'IS00149',
                                'IS00194', 'IS00203',
                                'IS00212', 'IS00221',
                                'IS00230', 'IS00239',
                                'IS00185'].indexOf(itemData.itemCode) > -1) {
                                ko.utils.setHtml(element, template.buttonWt);
                            } else {
                                ko.utils.setHtml(element, template.button);
                            }
                            break;
                    }

                    fetch().done(v => {
                        vm.itemNames(v);
                        // re binding viewModel to view
                        ko.applyBindingsToDescendants(vm, element);
                    });
                }
            });

            return { controlsDescendantBindings: true };
        }
    }
}


ko.bindingHandlers["cps003fValue"] = new nts.custombinding.ValueBoxControl();