module nts.uk.ui.at.kdp013.share {
    const { randomId } = nts.uk.util;

    export module dropdown {
        const style = `
            .nts-dropdown {
                height: 31px;
                position: relative;
            }
            .nts-dropdown>div {
                height: 31px;
                box-sizing: border-box;
                background-color: #fff;
                border: 1px solid #999;
                border-radius: 2px;
                overflow: hidden;
            }
            .nts-dropdown>div:before {
                content: '▼';
                position: absolute;
                top: 5px;
                right: 10px;
            }
            .nts-dropdown.show>div:before { 
                top: 4px;
                right: 9px;
            }
            .nts-dropdown.focus>div{
                box-shadow: 0 0 1px 1px #0096f2;
            }
            .nts-dropdown>div>table {
                width: 100%;
                display: block;
            }
            .nts-dropdown>div>table tr {
                height: 30px;
            }
            .nts-dropdown>div>table>tbody>tr.space {
                height: 38px;
            }
            .nts-dropdown>div>table tr td:first-child,
            .nts-dropdown>div>table+div>table tr td:first-child {
                padding-left: 10px;
                padding-right: 5px;
            }
            .nts-dropdown>div>table tr td:first-child,
            .nts-dropdown>div>table+div>table tr td:first-child {
                padding-left: 5px;
                padding-right: 10px;
            }
            .nts-dropdown>div>table+div {
                overflow-y: auto;
                max-height: 135px;
            }
            .nts-dropdown>div>table+div>table {
                width: 100%;
            }
            .nts-dropdown>div>table+div>table tr {
                height: 27px;
            }
            .nts-dropdown>div>table+div>table tr.selected {
                background-color: #ccc;
            }
            .nts-dropdown.show {
                border: 0;
                outline: none;
            }
            .nts-dropdown.show>div {
                height: auto;
                z-index: 3;
                position: fixed;
            }
            .nts-dropdown.show>div {
                background-color: #fff;
                border: 1px solid #999;
                box-shadow: 0 0 1px 1px #0096f2;
            }
            .nts-dropdown:not(.show)>div input {
                border: none;
                height: 0;
                padding: 0;
                outline: none;
                box-shadow: none;
                position: absolute;
            }
            .nts-dropdown:not(.show)>div input:focus {
                box-shadow: none;
            }
            .nts-dropdown.show>div input {
                position: absolute;
                top: 33px;
                width: calc(100% - 6px) !important;
                box-sizing: border-box;
                margin: 0 3px;
            }
        `;

        const COMPONENT_NAME = 'dropdown';

        interface DropdownItem {
            id: string;
            name: string;
            code: string;
        }

        @handler({
            bindingName: COMPONENT_NAME
        })
        export class DropdownBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, valueAccessor: () => KnockoutObservable<string>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext) => {
                element.classList.add('nts-dropdown');
                element.classList.add('ntsControl');

                const selected = valueAccessor();
                const items = allBindingsAccessor.get('items');

                ko.applyBindingsToNode(element, { component: { name: COMPONENT_NAME, params: { selected, items } } });

                element.removeAttribute('data-bind');

                element.setAttribute('role', randomId());
            }
        }

        @handler({
            bindingName: 'dropdownSelect'
        })
        export class DropdownSelectedBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLTableRowElement, valueAccessor: () => KnockoutComputed<DropdownItem>) => {
                const selected = valueAccessor();

                ko.computed({
                    read: () => {
                        const item = ko.unwrap(selected);
                        const { code, name } = item;

                        $(element)
                            .html('')
                            .append($('<td>', { text: code }))
                            .append($('<td>', { text: name }));
                    },
                    disposeWhenNodeIsRemoved: element
                });
            }
        }

        @component({
            name: COMPONENT_NAME,
            template: `
            <div>
                <input type="text" class="nts-input" data-bind="value: $component.filter, valueUpdate: 'input'" />
                <table>
                    <colgroup>
                        <col width="180px" />
                    </colgroup>
                    <thead>
                        <tr data-bind="dropdownSelect: $component.selected"></tr>
                    </thead>
                    <tbody>
                        <tr class="space">
                            <td colspan="2">&nbsp;</td>
                        </tr>
                    </tbody>
                </table>
                <div>
                    <table>
                        <colgroup>
                            <col width="180px" />
                        </colgroup>
                        <tbody data-bind="foreach: { data: $component.items, as: 'item' }">
                            <tr data-bind="click: function(item, evt) { $component.selecteItem(item, evt) }, css: { selected: item.selected }">
                                <td data-bind="text: item.name"></td>
                                <td data-bind="text: item.code"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            `
        })
        export class DropdownViewModel extends ko.ViewModel {
            focus: KnockoutObservable<boolean> = ko.observable(false);
            clickEvent!: (evt: JQueryEventObject) => void;

            selected!: KnockoutComputed<DropdownItem>;
            items!: KnockoutComputed<(DropdownItem & { selected: boolean; })[]>;

            filter: KnockoutObservable<string> = ko.observable('');
            highlight: KnockoutObservable<number> = ko.observable(-1);

            hideDropdown!: () => void;
            showDropdown!: () => void;

            constructor(private params: { selected: KnockoutObservable<string>; items: KnockoutObservableArray<DropdownItem> }) {
                super();

                const vm = this;

                vm.items = ko.computed({
                    read: () => {
                        const filter = ko.unwrap(vm.filter);
                        const items = ko.unwrap(params.items);
                        const selected = ko.unwrap(params.selected);

                        return _.chain(items)
                            .filter(({ name, code }) => name.indexOf(filter) > -1 || code.indexOf(filter) > -1)
                            .map(({ name, id, code }) => ({ id, code, name, selected: selected === id }))
                            .value();
                    }
                });

                vm.selected = ko.computed({
                    read: () => {
                        const items = ko.unwrap(params.items);
                        const selected = ko.unwrap(params.selected);
                        const exist = _.find(items, ({ id }) => id === selected);


                        if (exist) {
                            const { code, id, name } = exist;

                            return { code, id, name };
                        }

                        return { code: '', id: '', name: '' };
                    }
                });
            }

            mounted() {
                const vm = this;
                const { $el } = vm;
                const $container = $($el);
                const $input = $container.find('input').first();

                vm.showDropdown = () => {
                    const $ct = $container.find('div').get(0);
                    const bound = $el.getBoundingClientRect();

                    $ct.style.top = bound.top + 'px';
                    $ct.style.width = bound.width + 'px';

                    vm.filter('');
                    $el.classList.add('show');
                };
                vm.hideDropdown = () => {
                    const $ct = $container.find('div').get(0);

                    $ct.style.top = '';
                    $ct.style.width = '';

                    $el.classList.remove('show');

                    vm.filter('');
                };

                ko.computed({
                    read: () => {
                        const items = ko.unwrap(vm.items);

                        vm.$nextTick(() => {
                            $(vm.$el)
                                .find('[data-bind]')
                                .removeAttr('data-bind');
                        });
                    }
                });

                if (!$('style#dropdown').length) {
                    $('<style>', { id: "dropdown", html: style }).appendTo('head');
                }

                $container
                    .on('click', () => {
                        if (!$el.classList.contains('show')) {
                            vm.showDropdown();
                        }

                        vm.focus(true);

                        $input.focus();
                    });

                vm.clickEvent = (evt: JQueryEventObject) => {
                    const $closest = $(evt.target).closest('.nts-dropdown');

                    if (!$closest.is($el)) {
                        vm.hideDropdown();

                        vm.focus(false);

                        $el.classList.remove('focus');
                    }
                };

                $(document)
                    .on('click', vm.clickEvent);

                $input
                    .on('focus', () => {
                        $el.classList.add('focus');

                        vm.focus(true);
                    })
                    .on('blur', () => {
                        if (!vm.focus()) {
                            $el.classList.remove('focus');

                            vm.hideDropdown();
                        }
                    })
                    .on('keydown', (evt: JQueryEventObject) => {
                        const { keyCode } = evt;

                        if (keyCode === 9) {
                            // tabkey
                            vm.focus(false);
                            $el.classList.remove('focus');
                        } else if ([13, 40].indexOf(keyCode) > -1) {
                            // enter or arrow down
                            if (!$el.classList.contains('show')) {
                                vm.showDropdown();
                            }
                        } else if (keyCode === 27) {
                            // escape key
                            vm.hideDropdown();
                        } else if (keyCode !== 9 && !$el.classList.contains('show')) {
                            // other key
                            evt.preventDefault();
                            evt.stopImmediatePropagation();
                        }
                    });
            }

            selecteItem(item: DropdownItem, evt: MouseEvent) {
                const vm = this;
                const { params } = vm;

                // emit selected id to parent component
                params.selected(item.id);

                vm.hideDropdown();

                evt.preventDefault();
                evt.stopPropagation();
                evt.stopImmediatePropagation();

                // focus to first input (searchbox)
                $(vm.$el).find('input').first().focus();
            }

            destroyed() {
                const vm = this;

                vm.items.dispose();

                $(document).off('click', vm.clickEvent);
            }
        }
    }
}