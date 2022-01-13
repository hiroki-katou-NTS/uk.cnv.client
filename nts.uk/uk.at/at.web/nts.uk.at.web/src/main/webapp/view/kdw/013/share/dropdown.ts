module nts.uk.ui.at.kdw013.share {
    const { randomId } = nts.uk.util;

    export module dropdown {
        const vm = new ko.ViewModel();

        const style = `
            .nts-dropdown {
                position: relative;
            }
            .nts-dropdown>div.dropdown-container {
                height: 31px;
                box-sizing: border-box;
                background-color: #fff;
                border: 1px solid #999;
                border-radius: 2px;
                overflow: hidden;
            }
            .dropdown-container{
                overflow-y: auto;
            }
            .nts-dropdown.error>div.dropdown-container {
                border: 1px solid #ff6666 !important;
            }
            .nts-dropdown>div.dropdown-container:before {
                content: '▾';
                position: absolute;
                top: 5px;
                right: 10px;
            }
            .nts-dropdown.show>div.dropdown-container:before { 
                top: 4px;
                right: 9px;
            }
            .dropdown-container .space {
                height: 38px;
            }
            .dropdown-container .selected {
                color: #fff;
                background-color: #007fff;
            }
            .dropdown-container .highlight , .dropdown-container .highlight div {
                background-color: #91c8ff;
            }
            .nts-dropdown.show {
                border: 0;
                outline: none;
                height: 31px;
            }
            .nts-dropdown.show.error {
                height: 54px;
            }
            .nts-dropdown.show>div.message {
                display: none;
            }
            .nts-dropdown.show>div.dropdown-container {
                height: auto;
                z-index: 3;
                position: absolute;
                overflow-y: auto;
            }
            .nts-dropdown.show>div.dropdown-container {
                background-color: #fff;
                border: 1px solid #999;
                box-shadow: 0 0 1px 1px #0096f2;
            }
            .nts-dropdown:not(.show)>div.dropdown-container input {
                border: none;
                height: 0;
                padding: 0;
                outline: none;
                box-shadow: none;
                position: absolute;
            }
            .nts-dropdown:not(.show)>div.dropdown-container input:focus {
                box-shadow: none;
            }
            .nts-dropdown.show>div.dropdown-container input {
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
            init = (element: HTMLElement, valueAccessor: () => KnockoutObservable<string>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) => {
                element.classList.add('nts-dropdown');
                element.classList.add('ntsControl');

                const selected = valueAccessor();
                const name = allBindingsAccessor.get('name');
                const items = allBindingsAccessor.get('items');
                const visibleItemsCount = allBindingsAccessor.get('visibleItemsCount'); 
                const required = allBindingsAccessor.get('required');
                const hasError: undefined | KnockoutObservable<boolean> = allBindingsAccessor.get('hasError');
				const flagError: undefined | KnockoutObservable<boolean> = allBindingsAccessor.get('flagError');

                const msg = $('<div>', { class: 'message' }).get(0);
                const subscribe = ($selected: string) => {
                    const $required = ko.unwrap(required);

                    if ($required && !$selected) {
                        if (ko.isObservable(hasError)) {
                            hasError(true);
                        }
                    } else {
                        if (ko.isObservable(hasError)) {
                            hasError(false);
                        }
                    }
                };

                $(element)
                    .on('validate', () => subscribe(selected()));

                selected
                    .subscribe(subscribe);

                if (ko.isObservable(hasError)) {
                    hasError
                        .subscribe((has: boolean) => {
                            if (!has) {
                                msg.remove();
                                element.classList.remove('error');
                            } else {
                                $(msg).appendTo(element);
                                element.classList.add('error');
                            }
							if(flagError){
								flagError(!flagError());	
							}
                        });
                }

                const text = ko.computed({
                    read: () => {
                        const $selected = ko.unwrap(selected);
                        const $required = ko.unwrap(required);

                        if ($required && !$selected) {
                            return nts.uk.resource.getMessage('MsgB_2', [ko.unwrap(name)]);
                        }
                        return '';
                    },
                    disposeWhenNodeIsRemoved: element
                });

                ko.applyBindingsToNode(msg, { text }, bindingContext);

                ko.applyBindingsToNode(element, { component: { name: COMPONENT_NAME, params: { selected, items, required, visibleItemsCount } } }, bindingContext);

                element.setAttribute('role', randomId());

                $(element)
                    .on('mouseover', () => {
                        element.classList.add('active');
                    })
                    .on('mouseleave', () => {
                        element.classList.remove('active');
                    });
            }
        }

        @handler({
            bindingName: 'dropdownToggle'
        })
        export class DropdownToggleBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLTableRowElement, valueAccessor: () => KnockoutComputed<boolean>) => {
                const show = valueAccessor();

                ko.computed({
                    read: () => {
                        const $show = ko.unwrap(show);
                        const $ct = $(element).find('div').get(0);

                        if (!$show) {
                            $ct.style.top = '';
                            $ct.style.width = '';
                        } else {
                            const { innerHeight } = window;
                            const { width } = element.getBoundingClientRect();

                            $ct.style.width = width + 'px';

                            ko.tasks
                                .schedule(() => {
                                    const { top, height } = $(element).children().get(0).getBoundingClientRect();
                                    if (top + height < innerHeight - 10) {
										let taskDetails = $('.taskDetails').last();
										let lastTable = $('.taskDetails table').last();
										if(taskDetails.offset().top + taskDetails.outerHeight(true) > lastTable.offset().top + lastTable.outerHeight(true)){
											$ct.style.top = (height - 31) * -1 + 'px';	
										}else{
											$ct.style.top = '0px';
										}
                                        
                                    } else {
                                        $ct.style.top = (height - 31) * -1 + 'px';
                                    }
                                });
                        }
                    },
                    disposeWhenNodeIsRemoved: element
                });
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

                        if (item) {
                            const { code, name } = item;

                            $(element)
                                .html(`<div style='height:31px;' >${code}</div><div style='margin-left:10px;white-space: nowrap;' >${name}</div>`);
                        } else {
                            $(element)
                                .html(`<div style='height:31px;' ></div><div style='margin-left:10px;white-space: nowrap;' >${vm.$i18n('KDW013_41')}</div>`);
                        }
                    },
                    disposeWhenNodeIsRemoved: element
                });
            }
        }

        @handler({
            bindingName: 'scrollTo'
        })
        export class DropdownScrollToBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, valueAccessor: () => KnockoutObservable<number>) => {
                const tbody = $(element).find('tbody').get(0);
                const highlight = valueAccessor();

                ko.computed({
                    read: () => {
                        const $hl = ko.unwrap(highlight);
                        const $tr = $(element).find('tbody>tr').get($hl);

                        if ($tr) {
                            $(element).scrollTop($tr.offsetTop);
                        }
                    },
                    disposeWhenNodeIsRemoved: element
                });
            }
        }

		let template = `
            <div class="dropdown-container">
                <input type="text" class="nts-input" data-bind="
                        value: $component.filter,
                        valueUpdate: 'input',
                        attr: { 
                            readonly: !ko.unwrap($component.show)
                        }
                    " />
                <div style="line-height: 31px; padding-left: 5px;" data-bind="scrollTo: $component.highlight">
                    <div style="display: flex;line-height: 31px;" data-bind="dropdownSelect: $component.selected"> 
                        
                    </div>
                    <div class='space'></div>
                </div>
               
                <div data-bind="scrollTo: $component.highlight">
                    <div style="width: 280px;overflow: auto;" data-bind="foreach: { data: $component.items, as: 'item' }"> 
                        <div style='display: flex;line-height: 31px;padding-left: 5px;' data-bind="click: function(item, evt) { $component.selecteItem(item, evt) }, css: { selected: item.selected, highlight: item.highlight }">  
                                    <div data-bind="text: item.code"></div>
                                    <div style='margin-left:10px;white-space: nowrap;' data-bind="text: item.name"></div> 
                        </div>
                    </div>
                </div>
            </div>
            `

        @component({
            name: COMPONENT_NAME,
            template: template
        })
        export class DropdownViewModel extends ko.ViewModel {
            show: KnockoutObservable<boolean> = ko.observable(false);
            focus: KnockoutObservable<boolean> = ko.observable(false);
            clickEvent!: (evt: JQueryEventObject) => void;

            selected!: KnockoutComputed<DropdownItem>;
            items!: KnockoutComputed<(DropdownItem & { selected: boolean; highlight: boolean; })[]>;

            filter: KnockoutObservable<string> = ko.observable('');
            highlight: KnockoutObservable<number> = ko.observable(-1);
        
            visibleItemsCount: number = 5;

            constructor(private params: { selected: KnockoutObservable<string>; items: KnockoutObservableArray<DropdownItem>; required: undefined | boolean | KnockoutObservable<undefined | boolean>; visibleItemsCount: number }) {
                super();

                const vm = this;

                vm.items = ko.computed({
                    read: () => {
                        const filter = ko.unwrap(vm.filter);
                        const items = ko.unwrap(params.items);
                        const selected = ko.unwrap(params.selected);
                        const highlight = ko.unwrap(vm.highlight);

                        return _.chain(items)
                            .filter(({ name, code }) => name.indexOf(filter) > -1 || code.indexOf(filter) > -1)
                            .map(({ id, name, code }, index) => ({
                                id,
                                code,
                                name,
                                selected: selected === code,
                                highlight: highlight === index
                            }))
                            .value();
                    }
                });

                vm.selected = ko.computed({
                    read: () => {
                        const items = ko.unwrap(params.items);
                        const highlight = ko.unwrap(vm.highlight);
                        const selected = ko.unwrap(params.selected);
                        const exist = _.find(items, ({ id }, index) => highlight === index || id === selected);


                        if (exist) {
                            const { id, code, name } = exist;

                            return { id, code, name };
                        }

                        return null;
                    }
                });
                vm.visibleItemsCount = params.visibleItemsCount;
            }
        
            setvisibleItemCount(show: boolean){
                const vm = this;
                const { visibleItemsCount } = vm;
            
                if (show) {
                    let height = (visibleItemsCount * 31) + 69;
                    $(vm.$el).find('.dropdown-container').css({ "maxHeight": height });
                    let ddlTop = $(vm.$el).find('.dropdown-container').offset().top;

                    let selected = $(vm.$el).find('.dropdown-container').find('.selected');
                    
                    if (selected.offset()) {
                        setTimeout(() => { $(vm.$el).find('.dropdown-container').scrollTop(0 - (ddlTop - selected.offset().top)) }, 50);
                    }
                } else {
                    $(vm.$el).find('.dropdown-container').removeAttr('style');
                    $(vm.$el).find('.dropdown-container').scrollTop(0);
                }
            }

            mounted() {
                const vm = this;
                const { $el, focus, show } = vm;
                const $container = $($el);
                const $input = $container.find('input').first();

                // apply show & focus state to container element
                ko.applyBindingsToNode($el, { css: { show, focus }, dropdownToggle: show }, vm);

                show.subscribe((sh: boolean) => {
                    if (!sh) {
                        vm.filter('');
                        vm.highlight(-1);
                    } else {
                        const items = ko.unwrap(vm.items).map(({ id }) => id);
                        const selected = ko.unwrap(vm.params.selected);
                        const index = _.indexOf(items, selected);
                        
                        if (index > -1) {
                            vm.highlight(index);
                        }
                        
                    }
                    vm.setvisibleItemCount(sh); 
                });
        
                

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
                        $.Deferred()
                            .resolve(true)
                            .then(() => {
                                if (!ko.unwrap(show)) {
									if($('.taskDetails').height() < 500){
										$('.taskDetails').css({ "overflow-y": "unset"});
									}
                                    vm.show(true);
                                }

                                $input.focus();
                            });
                    });

                vm.clickEvent = (evt: JQueryEventObject) => {
                    const $closest = $(evt.target).closest('.nts-dropdown');

                    if (!$closest.is($el)) {
                        vm.show(false);
                        vm.focus(false);
                    }
                };

                $(document)
                    .on('click', vm.clickEvent);

                $input
                    .on('focus', () => {
                        vm.focus(true);
                    })
                    .on('blur', () => {
                        if (!vm.focus()) {
                            vm.show(false);
                        }

                        // trigger validate
                        $(vm.$el).trigger('validate');
                    })
                    .on('keydown', (evt: JQueryEventObject) => {
                        const { keyCode } = evt;
                        const isShow = ko.unwrap(show);

                        if (keyCode === 9) {
                            // tab key
                            vm.focus(false);
                        } else if (keyCode === 13) {
                            // enter key
                            if (!isShow) {
                                vm.show(true);
                            } else {
                                const items = ko.unwrap(vm.items);
                                const highlight = ko.unwrap(vm.highlight);

                                if (highlight > -1) {
                                    const exist = _.find(items, ({ }, index) => index === highlight);

                                    if (exist) {
                                        vm.params.selected(exist.id);
                                    }

                                    vm.show(false);
                                }
                            }
                        } else if (keyCode === 27) {
                            // escape key
                            vm.show(false);
                        } else if (keyCode === 40) {
                            // arrow down key
                            if (!isShow) {
                                vm.show(true);
                            } else {
                                // move down
                                const items = ko.unwrap(vm.items);
                                const highlight = ko.unwrap(vm.highlight);

                                if (highlight < items.length - 1) {
                                    vm.highlight(highlight + 1);
                                } else {
                                    vm.highlight(0);
                                }
                            }
                        } else if (keyCode === 38) {
                            // arrow up key
                            if (isShow) {
                                // move up
                                const items = ko.unwrap(vm.items);
                                const highlight = ko.unwrap(vm.highlight);

                                if (highlight > 0) {
                                    if (highlight > items.length - 1) {
                                        vm.highlight(items.length - 1);
                                    } else {
                                        vm.highlight(highlight - 1);
                                    }
                                }
                            }
                        }
                    });
            
                
            }

            selecteItem(item: DropdownItem, evt: MouseEvent) {
                const vm = this;
                const { params } = vm;

                // emit selected id to parent component
                params.selected(item.id);

                vm.show(false);

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