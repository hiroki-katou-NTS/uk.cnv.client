import { dom } from '@app/utils';
import { Vue, DirectiveBinding, VNode } from '@app/provider';

Vue.directive('float-action', {
    unbind(el: HTMLElement, binding: DirectiveBinding, vnode: VNode, oldVnode: VNode) {
        let $vm = vnode.context;

        $vm.$mask('hide');

        if (document.body.contains(el)) {
            el.remove();
        }
    },
    inserted(el: HTMLElement, binding: DirectiveBinding, vnode: VNode, oldVnode: VNode) {
        let $vm = vnode.context,
            params: { icon?: string; background?: string; forceground?: string; } = binding.value || {};

        el.appendChild(dom.create('a', {
            html: `${params && params.icon ? `<i class="${params.icon} ${params.forceground}"></i>` : ''}<i class="fas fa-plus ${params.forceground}"></i>`,
            'class': `btn btn-danger ${params.background} btn-lg btn-floating`
        }));

        dom.addClass(el, 'fixed-action-btn');

        let ul = el.querySelector('ul');

        if (ul && !dom.hasClass(ul, 'list-unstyled')) {
            dom.addClass(ul, 'list-unstyled');

            [].slice.call(el.querySelectorAll('ul.list-unstyled>li'))
                .forEach((element) => {
                    dom.addClass(element, 'btn btn-floating');
                });

            dom.registerGlobalEventHandler(el, 'click', 'a.btn-floating', () => {
                dom.toggleClass(el, 'active');

                if (!dom.hasClass(el, 'active')) {
                    $vm.$mask('hide');
                } else {
                    $vm.$mask('show', 0.02)
                        .on(() => $vm.$mask('hide'), () => {
                            dom.removeAttr(ul, 'style');
                            dom.removeClass(el, 'active');
                        });
                    let displaySelect = [];
                    ul.querySelectorAll('li.btn-floating').forEach((item: any) => {
                        if (item.style.display == 'none') {
                            return;
                        }
                        displaySelect.push(item);
                    });
                    dom.setAttr(ul, 'style', `height: ${57 * [].slice.call(displaySelect).length}px`);
                }
            });

            dom.registerGlobalEventHandler(el, 'click', 'li.btn-floating', () => $vm.$mask('hide'));
        }

        if (el.closest('.modal.show')) {
            document.body.appendChild(el);
        }
    }
});