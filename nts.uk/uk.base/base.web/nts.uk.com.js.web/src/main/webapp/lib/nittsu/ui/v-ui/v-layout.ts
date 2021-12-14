/// <reference path="../viewcontext.d.ts" />

module nts.uk.ui.layout {

    @handler({
        bindingName: 'vm'
    })
    export class MasterUIViewModelBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const content = valueAccessor();

            element.removeAttribute('data-bind');

            element.setAttribute('id', 'master-wrapper');

            ko.applyBindingsToDescendants(bindingContext.extend({ $vm: content, $data: content }), element);

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: 'kb'
    })
    export class KibanUIViewModelBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const content = valueAccessor();
            const { errorDialogViewModel } = content;

            element.removeAttribute('data-bind');

            element.classList.add('view');
            element.setAttribute('id', 'kiban-wrapper');

            ko.applyBindingsToDescendants(bindingContext.extend({ $vm: errorDialogViewModel, $data: errorDialogViewModel }), element);

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: 'ui-master-notification',
        validatable: true,
        virtual: false
    })
    export class MasterUINotificationBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            element.classList.add('hidden');
            element.removeAttribute('data-bind');

            element.setAttribute('id', 'operation-info');

            if (ko.components.isRegistered('ui-notification')) {
                ko.applyBindingsToNode(element, { component: 'ui-notification' });
            }

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: 'ui-master-header',
        validatable: true,
        virtual: false
    })
    export class MasterUIHeaderBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            element.id = "header";
            element.classList.add('header');
            element.classList.add('hidden');
            element.removeAttribute('data-bind');

            if (ko.components.isRegistered('ui-header')) {
                ko.applyBindingsToNode(element, { component: 'ui-header' });
            }

            return { controlsDescendantBindings: true };
        }
    }

    ko.bindingHandlers.stopBinding = {
        init: function() {
            return { controlsDescendantBindings: true }
        }
    };

    @handler({
        bindingName: 'ui-master-content',
        validatable: true,
        virtual: false
    })
    export class MasterUIBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            element.classList.add('master-content');
            element.removeAttribute('data-bind');

            if (!element.id) {
                element.id = 'master-content';
            }

            ko.applyBindingsToDescendants(bindingContext, element);

            let $functionsArea = $(element).find('div[id^=functions-area]'); 
            if ($functionsArea.length > 0) {
                $functionsArea.each((__: number, e: HTMLElement) => {
                    ko.applyBindingsToNode(e,
                        {
                            'ui-function-bar': e.id.match(/bottom$/) ? 'bottom' : 'top',
                            title: e.getAttribute('data-title') || true,
                            back: e.getAttribute('data-url')
                        }, bindingContext);

                    e.removeAttribute('data-url');
                    e.removeAttribute('data-title');
                });
            } else {
                $(element)
                    .find('.sidebar-content-header')
                    .each((__: number, e: HTMLElement) => {
                        ko.applyBindingsToNode(e, { 'ui-function-bar': 'top' }, bindingContext);
                    });
            }

            $(element)
                .find('div[id^=contents-area]')
                .each((__: number, e: HTMLElement) => {
                    ko.applyBindingsToNode(e, { 'ui-contents': 0 }, bindingContext);
                });

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: 'pg-name'
    })
    export class PGNameBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservable<string | boolean>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext & { $vm: nts.uk.ui.vm.ViewModel }): { controlsDescendantBindings: boolean; } {
            const vm = new ko.ViewModel();
            const pgName = valueAccessor();
            const title = ko.observable('');
            const back = allBindingsAccessor.get('back');

            const $title = $(element);
            const $span = $('<span>').get(0);

            const text = ko.computed({
                read: () => {
                    const $pg = ko.unwrap(pgName);
                    const $title = ko.unwrap(title);

                    if (_.isString($pg)) {
                        return vm.$i18n($pg);
                    }

                    if ($pg) {
                        return $title.trim();
                    }

                    return '';
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.applyBindingsToNode($span, { text }, bindingContext);

            vm
                .$ajax('com', '/sys/portal/webmenu/program')
                .then((response: { name: string }[]) => {
                    const [first] = response;

                    if (first) {
                        const { name } = first;

                        if (name) {
                            title(name);
                        }
                    }
                });

            $title
                .append($span)
                .addClass('pg-name')
                .removeAttr('data-bind');

            if (back) {
                $title.addClass('navigator');

                const svg = document.createElement('svg');

                ko.applyBindingsToNode(svg, { 'svg-icon': 'ARROW_LEFT_SQUARE', size: 20 });

                $($title)
                    .prepend(svg)
                    .on('click', () => vm.$jump(back));
            }

            return { controlsDescendantBindings: false };
        }




    }

    // Handler for fixed functional area on top or bottom page
    @handler({
        bindingName: 'ui-function-bar',
        validatable: true,
        virtual: false
    })
    export class MasterUIFunctionalBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => 'top' | 'bottom', allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext & { $vm: nts.uk.ui.vm.ViewModel }): { controlsDescendantBindings: boolean; } {
            const position: 'top' | 'bottom' = valueAccessor();
            const back: string = allBindingsAccessor.get('back');
            const title: boolean | string = allBindingsAccessor.get('title');
            const root: nts.uk.ui.RootViewModel = bindingContext.$root;
            const mode = ko.unwrap<'view' | 'modal'>(root.kiban.mode);

            if (!element.classList.contains('sidebar-content-header')) {
                element.classList.add('functions-area');
            }

            // top area
            if (!$(element).prev().length && position === 'top') {
                if (!element.id && !element.classList.contains('sidebar-content-header')) {
                    element.id = "functions-area";
                }

                /*if (title && mode === 'view') {
                    const pgName = $(element).find('.pg-name');
                    const $title = pgName.get(0) || document.createElement('div');

                    if (!pgName.length) {
                        ko.applyBindingsToNode($title, { 'pg-name': title, back }, bindingContext);
                    }

                    $(element).prepend($title);

                    if (element.childNodes.length > 1) {
                        const $btnGroup = document.createElement('div');
                        $btnGroup.classList.add('button-group');
                        const $pgName = $(element).find('.pg-name');

                        $(element).children().each((__: null, e: HTMLElement) => {
                            if (!e.classList.contains('pg-name') && !e.classList.contains('floating-btn')) {
                                $($btnGroup).append(e);
                            }
                        });

                        $($btnGroup).insertAfter($pgName);

                        ko.applyBindingsToNode($btnGroup, null, bindingContext);
                    }
                }*/

                // button error in function bar
                ko.applyBindingsToNode($('<button>').appendTo(element).get(0), { 'c-error': '' }, bindingContext);
            } else {
                if (!element.id) {
                    element.id = "functions-area-bottom";
                }

                if (!ko.dataFor(element)) {
                    ko.applyBindingsToDescendants(bindingContext, element);
                }

                // button error in function bar
                ko.applyBindingsToNode($('<button>').prependTo(element).get(0), { 'c-error': '' }, bindingContext);
            }

            element.removeAttribute('data-bind');

            let ntsFunctionPanel = $(element).find(".ntsFunctionPanel");
            if (ntsFunctionPanel.length > 0) {
                $("#master-content").addClass("overflow-visible");
            }

            return { controlsDescendantBindings: false };
        }
    }


    @handler({
        bindingName: 'ui-contents',
        validatable: true,
        virtual: false
    })
    export class MasterUIContentBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => number, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            if (!element.id) {
                element.id = 'contents-area';
            }

            element.classList.add('contents-area');
            element.removeAttribute('data-bind');

            // element.style.height = `calc(100vh - ${element.getBoundingClientRect().top + (valueAccessor() || ($(element).parent().hasClass('master-content') ? 0 : 20))}px)`;

            return { controlsDescendantBindings: false };
        }
        update(element: HTMLElement, valueAccessor: () => number, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) {
            const root: nts.uk.ui.RootViewModel = bindingContext.$root;
            const size = ko.unwrap<nts.uk.ui.WindowSize>(root.kiban.size);

            $.Deferred()
                .resolve({ size })
                .then(() => {
                    element.classList.add('padding-0');
                    element.classList.add('overflow-hidden');
                    element.style.height = '1px';
                })
                .then(() => {
                    const mb = $(element).next();
                    const md = $(element).closest('.modal');
                    const zero = $(element).closest('#master-wrapper.modal').length || $(element).parent().hasClass('master-content');

                    if (!mb.length) {
                        const height = element.getBoundingClientRect().top + (valueAccessor() || (zero ? 0 : (md.length ? 0 : 20))) - 2;

                        element.style.height = `calc(100vh - ${Math.floor(Math.max(0, height))}px)`;
                    } else {
                        const bd = mb.get(0).getBoundingClientRect();
                        const height = element.getBoundingClientRect().top + (valueAccessor() || (zero ? (bd.height || 0) : 20)) - 2;

                        element.style.height = `calc(100vh - ${Math.floor(Math.max(0, height))}px)`;
                    }
                })
                .always(() => {
                    element.classList.remove('hidden');
                    element.classList.remove('padding-0');
                    element.classList.remove('overflow-hidden');

                    element.style.display = '';
                });
        }
    }

    @handler({
        bindingName: 'floating'
    })
    export class FloatingButtonsBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservable<boolean>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
            const float = valueAccessor();
            const top = allBindingsAccessor.get('top');
            const left = allBindingsAccessor.get('left');
            const right = allBindingsAccessor.get('right');
            const bottom = allBindingsAccessor.get('bottom');
            const replacer = ($t: string | number) => `${$t}px`.replace(/(px){2,}/, 'px').replace(/ptpx/, 'pt').replace(/%px/, '%');

            ko.computed({
                read: () => {
                    const $f = ko.unwrap(float);

                    if ($f !== false) {
                        element.classList.add('floating-btn');
                    } else {
                        element.classList.remove('floating-btn');
                    }
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.computed({
                read: () => {
                    const $t = ko.unwrap(top);

                    if (_.isNil($t)) {
                        element.style.top = '';
                    } else {
                        element.style.top = replacer($t);
                    }
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.computed({
                read: () => {
                    const $l = ko.unwrap(left);

                    if (_.isNil($l)) {
                        element.style.left = '';
                    } else {
                        element.style.left = replacer($l);
                    }
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.computed({
                read: () => {
                    const $r = ko.unwrap(right);

                    if (_.isNil($r)) {
                        element.style.right = '';
                    } else {
                        element.style.right = replacer($r);
                    }
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.computed({
                read: () => {
                    const $b = ko.unwrap(bottom);

                    if (_.isNil($b)) {
                        element.style.bottom = '';
                    } else {
                        element.style.bottom = replacer($b);
                    }
                },
                disposeWhenNodeIsRemoved: element
            });

            element.removeAttribute('data-bind');
        }
    }

}