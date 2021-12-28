module nts.uk.ui.at.kdw013.datepicker {
    @handler({
        bindingName: 'kdw013-date-picker'
    })
    export class Kdw013DatePickerBindingHandler implements KnockoutBindingHandler {
        init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
            const name = componentName();
            const params = { ...allBindingsAccessor() };
            ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);

            return { controlsDescendantBindings: true };
        }
    }

    type Kdw013DatePickerParams = {
        initialDate: Date | KnockoutObservable<Date>;
        firstDay: KnockoutObservable<number>;
    };

    @component({
        name: 'kdw013-date-picker',
        
        template: `
            <div class='ac-date-picker' data-bind="ntsAccordion: {}">
                <h3>
                    <label data-bind="i18n: 'KDW013_97'"></label>
                </h3>
                <div>
                    <div id='fc-date-picker'></div>
                </div>
            </div>
            <style rel="stylesheet">
                .fc-date-picker .nts-input {
                    display: none;
                }
                .ac-date-picker .ui-accordion-header{
                        margin-top: 4px !important;
                }
            </style>
        `
    })
    export class Kdw013DatePickerComponent extends ko.ViewModel {
        constructor(public params: Kdw013DatePickerParams) {
            $('#fc-date-picker').fcdatepicker({
                language: 'ja-JP',
                format: "YYYY/MM/DD",
                date: params.initialDate(),
                autoShow: true,
                weekStart: params.firstDay(),
                inline: true
            });
            params.firstDay.subscribe(value => {
                if (value == undefined) {
                    return;
                }
                $('#fc-date-picker').fcdatepicker('destroy');
                $('#fc-date-picker').fcdatepicker({
                    language: 'ja-JP',
                    format: "YYYY/MM/DD",
                    date: params.initialDate(),
                    autoShow: true,
                    weekStart: value,
                    inline: true
                });
            });
            ko.utils.registerEventHandler('#fc-date-picker', "change", function(event) {
                if (!moment(vm.initialDate()).isSame(moment($('#fc-date-picker').fcdatepicker('getDate'))))
                    vm.initialDate($('#fc-date-picker').fcdatepicker('getDate'));
            });
            params.initialDate.subscribe((value) => {
                $('#fc-date-picker').fcdatepicker('setDate', value);
            });
            super();
        }
    }
}