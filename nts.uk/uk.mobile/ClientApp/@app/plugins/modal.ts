import { Vue, VueConstructor, ComponentOptions } from '@app/provider';
import { obj, dom, browser } from '@app/utils';
import { IModalOptions } from 'declarations';
import { ErrorHandler } from 'vue-router/types/router';

const modal = {
    install(vue: VueConstructor<Vue>) {
        // prevent error
        vue.mixin({
            methods: {
                $close() { }
            },
            beforeMount() {
                let self = this;

                obj.extend(self.$router, {
                    goto(location: { name: string; params: { [key: string]: any } }, onComplete?: Function, onAbort?: ErrorHandler) {
                        (<any>self.$router).push({
                            name: location.name,
                            params: {
                                params: location.params
                            }
                        }, onComplete, onAbort);
                    }
                })
            }
        });

        vue.prototype.$goto = function (nameOrLocation: { name: string; params: { [key: string]: any; }; }, params?: { [key: string]: any; }) {
            let self = this;

            if (typeof nameOrLocation !== 'string') {
                self.$router.push({
                    name: nameOrLocation.name,
                    params: {
                        params: nameOrLocation.params
                    }
                });
            } else {
                self.$router.push({
                    name: nameOrLocation,
                    params
                });
            }
        };

        vue.prototype.$modal = function (name: string | ComponentOptions<Vue>, params?: any, options?: IModalOptions) {
            let self = this,
                $options = self.$options,
                components = $options.components,
                focused = document.querySelector(':focus') as HTMLElement;

            params = obj.toJS(params || {});

            options = options || <IModalOptions>{
                title: name,
                size: 'md',
                type: 'modal'
            };

            return new Promise(resolve => {
                if (typeof name === 'string' ? components : true) {
                    let component = typeof name === 'string' ? components[name] : name;

                    if (component) {
                        // remove old mixin methods
                        [].slice.call(component.mixins || (component.mixins = [])).forEach(mixin => {
                            if (obj.has(mixin, 'methods')) {
                                let methods = mixin.methods;

                                if (methods && obj.has(methods, '$close') && obj.has(mixin, 'mounted')) {
                                    delete mixin.methods;
                                    delete mixin.mounted;
                                }
                            }

                            if (obj.isEmpty(mixin)) {
                                component.mixins.splice(component.mixins.indexOf(mixin), 1);
                            }
                        });

                        // add new mixin methods
                        component.mixins.push({
                            methods: {
                                $close: function (data?: any) {
                                    this.$emit('callback', data);

                                    this.$destroy(true);
                                }
                            },
                            mounted() {
                                let el = this.$el as HTMLElement,
                                    footer = el.querySelector('.modal-footer') as HTMLElement;

                                // move footer element from body to modal content
                                if (footer) {
                                    let mcontent = el.closest('.modal-content');

                                    if (mcontent) {
                                        if (!dom.hasClass(footer, 'top')) {
                                            mcontent.append(footer);
                                        } else {
                                            let body = mcontent.querySelector('.modal-body') as HTMLElement;

                                            if (body) {
                                                mcontent.insertBefore(footer, body);
                                            } else {
                                                mcontent.append(footer);
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        let dlg = dom.create('div'),
                            vm = Vue.extend({
                                components: {
                                    'nts-dialog': component
                                },
                                data: () => ({ name: 'nts-dialog', params, show: false }),
                                computed: {
                                    title: {
                                        get() {
                                            return options.title || (typeof name === 'string' ? name : 'nts-dialog');
                                        }
                                    },
                                    $class: {
                                        get() {
                                            let classNames: Array<string> = [];

                                            if (!options.size) {
                                                classNames.push(`modal-md`);
                                            } else {
                                                classNames.push(`modal-${options.size}`);
                                            }

                                            if (options.type === 'modal' || options.type === undefined) {
                                                classNames.push('modal-dialog-scrollable');
                                            } else {
                                                classNames.push('modal-popup modal-dialog-centered');
                                            }

                                            return classNames.join(' ');
                                        }
                                    },
                                    $enter: {
                                        get() {
                                            if (options.animate && options.animate.show) {
                                                return options.animate.show;
                                            }

                                            return browser.mobile && !browser.landscapse ? 'slideInRight' : 'slideInDown';
                                        }
                                    },
                                    $leave: {
                                        get() {
                                            if (options.animate && options.animate.hide) {
                                                return options.animate.hide;
                                            }

                                            return browser.mobile && !browser.landscapse ? 'slideOutRight' : 'slideOutUp';
                                        }
                                    }
                                },
                                methods: {
                                    callback(data) {
                                        resolve(data);

                                        this.show = false;
                                    },
                                    leave() {
                                        // remove modal-open class
                                        if (document.querySelectorAll('.modal').length == 1) {
                                            dom.removeClass(document.body, 'modal-open');
                                        }
                                    },
                                    afterLeave() {
                                        // destroy modal app
                                        this.$destroy(true);
                                    }
                                },
                                mounted() {
                                    this.show = true;

                                    dom.addClass(document.body, 'modal-open');
                                },
                                beforeMount() {
                                    // remove all tabindex of item below modal-backdrop
                                    let inputs = document.querySelectorAll('a, input, select, button, textarea');

                                    [].slice.call(inputs).forEach((element: HTMLElement) => {
                                        if (!element.getAttribute('data-tabindex')) {
                                            let tabindex = element.getAttribute('tabindex');

                                            element.setAttribute('tabindex', '-1');

                                            if (tabindex) {
                                                element.setAttribute('data-tabindex', tabindex);
                                            }
                                        }
                                    });
                                },
                                destroyed() {
                                    // remove own element on body
                                    document.body.removeChild(this.$el);

                                    //restore all tabindex of item below modal-backdrop
                                    let inputs = document.querySelectorAll('a, input, select, button, textarea');

                                    [].slice.call(inputs).forEach((element: HTMLElement) => {
                                        let tabindex = element.getAttribute('data-tabindex');

                                        element.removeAttribute('data-tabindex');

                                        if (!tabindex) {
                                            element.removeAttribute('tabindex');
                                        } else {
                                            element.setAttribute('tabindex', tabindex);
                                        }
                                    });

                                    // focus to preview item (caller of modal)
                                    if (focused) {
                                        focused.focus();
                                    }
                                },
                                template: `<div class="modal fade show">
                                        <transition apear 
                                                v-on:leave="leave"
                                                v-on:after-leave="afterLeave"
                                                v-bind:enter-active-class="$enter"
                                                v-bind:leave-active-class="$leave">
                                            <div class="modal-dialog animated" v-bind:class="$class" v-if="show">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h4 class="modal-title">
                                                            <span>{{title | i18n}}</span>
                                                        </h4>
                                                        <button tabindex="-1" type="button" v-on:click="show = false" class="close">&times;</button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <component v-bind:is="name" v-bind:params="params" v-on:callback="callback" />
                                                    </div>
                                                </div>
                                            </div>
                                        </transition>
                                    </div>`
                            });

                        document.body.appendChild(dlg);

                        new vm().$mount(dlg);
                    }
                }
            });
        };

        // hack view height for modal
        window.addEventListener('resize', () => {
            // We execute the same script as before
            let vh = window.innerHeight * 0.01;
            document.documentElement.style.setProperty('--vh', `${vh}px`);
        });

        window.dispatchEvent(new Event('resize'));
    }
};

export { modal };