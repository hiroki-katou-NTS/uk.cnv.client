﻿module nts.uk.ui {
     
    export module windows {

        var MAIN_WINDOW_ID = 'MAIN_WINDOW';

        var DEFAULT_DIALOG_OPTIONS = {
            autoOpen: false,
            draggable: true,
            resizable: false,
            create: function (event) {
                $(event.target).dialog('widget').css({ position: 'fixed' });
            }
        };

        /**
         * Main or Sub Window(dialog)
         */
        export class ScreenWindow {
            id: string;
            isRoot: boolean;
            parent: ScreenWindow;
            global: any = null;
            $dialog: JQuery = null;
            $iframe: JQuery = null;
            onClosedHandler: () => {} = $.noop;

            constructor(id: string, isRoot: boolean, parent: ScreenWindow) {
                this.id = id;
                this.isRoot = isRoot;
                this.parent = parent;
            }

            static createMainWindow() {
                return new ScreenWindow(MAIN_WINDOW_ID, true, null);
            }

            static createSubWindow(parent: ScreenWindow) {
                return new ScreenWindow(util.randomId(), false, parent);
            }
            
            setupAsDialog(path: string, options: any) {
                
                options.close = () => {
                    this.dispose();
                };

                this.build$dialog(options);
                
                this.$iframe.bind('load', () => {
                    this.global.nts.uk.ui.windows.selfId = this.id;

                    this.$dialog.dialog('option', {
                        width: this.global.dialogSize.width,
                        height: this.global.dialogSize.height,
                        title: options.title || "dialog",
                        beforeClose: function () {
                            //return dialogWindow.__viewContext.dialog.beforeClose();
                        }
                    }).dialog('open');
                });
                
                this.global.location.href = request.resolvePath(path);
            }
            
            build$dialog(options: any) {
               this.$dialog = $('<div/>')
                    .css({
                        padding: 'initial',
                        overflow: 'hidden'
                    })
                    .appendTo($('body'))
                    .dialog(options);
                
                this.$iframe = $('<iframe/>').css({
                        width: '100%',
                        height: '100%'
                    })
                    .appendTo(this.$dialog);
                
                this.global = (<any>this.$iframe[0]).contentWindow;
            }
            
            onClosed(callback: () => {}) {
                this.onClosedHandler = callback;
            }
            
            close() {
                if (this.isRoot) {
                    window.close();
                } else {
                    this.$dialog.dialog('close');
                }
            }
            
            dispose() {
                _.defer(() => this.onClosedHandler());
                
                // delay 2 seconds to avoid IE error when any JS is running in destroyed iframe
                setTimeout(() => {
                    this.$iframe.remove();
                    this.$dialog.remove();
                    this.$dialog = null;
                    this.$iframe = null;
                }, 2000);
            }
        }

        /**
         * All ScreenWindows are managed by this container.
         * this instance is singleton in one browser-tab.
         */
        export class ScreenWindowContainer {
            windows: { [key: string]: ScreenWindow };
            shared: { [key: string]: any };

            constructor() {
                this.windows = {};
                this.windows[selfId] = ScreenWindow.createMainWindow();
                this.shared = {};
            }

            /**
             * All dialog object is in MainWindow.
             */
            createDialog(path: string, options: any, parentId: string) {

                var parentwindow = this.windows[parentId];
                var subWindow = ScreenWindow.createSubWindow(parentwindow);
                this.windows[subWindow.id] = subWindow;

                options = $.extend({}, DEFAULT_DIALOG_OPTIONS, options);
                
                subWindow.setupAsDialog(path, options);

                return subWindow;
            }
            
            getShared(key: string): any {
                return this.shared[key];
            }
            
            setShared(key: string, data: any) {
                this.shared[key] = data;
            }
            
            close(id: string) {
                var target = this.windows[id];
                target.close();
            }
        }

        export var selfId: string;
        export var container: ScreenWindowContainer;

        if (util.isInFrame()) {
            var parent: any = window.parent;
            container = <ScreenWindowContainer> (parent.nts.uk.ui.windows.container);
        } else {
            selfId = MAIN_WINDOW_ID;
            container = new ScreenWindowContainer();
        }

        export function getShared(key: string): any {
            return container.getShared(key);
        }
            
        export function setShared(key: string, data: any) {
            container.setShared(key, data);
        }
        
        export function close(windowId?: string) {
            windowId = util.orDefault(windowId, selfId);
            container.close(windowId);
        }

        export module sub {

            export function modal(path: string, options?: any) {
                options = options || {};
                options.modal = true;
                return open(path, options);
            }

            export function modeless(path: string, options?: any) {
                options = options || {};
                options.modal = false;
                return open(path, options);
            }

            export function open(path: string, options?: any) {
                return windows.container.createDialog(path, options, selfId);
            }
        }
    }
    
    export function localize(textId: string): string {
        return textId;
    }
         
    export module dialog {
                
        function createNoticeDialog(text, buttons) {
            var $control = $('<div/>').addClass('control');            
            text = text.replace(/\n/g, '<br />');
            
            var $this = $('<div/>').addClass('notice-dialog')
                .append($('<div/>').addClass('text').append(text))
                .append($control)
                .appendTo('body')
                .dialog({
                    width: 'auto',
                    modal: true,
                    closeOnEscape: false,
                    buttons: buttons,
                    open: function () {
                        $(this).closest('.ui-dialog').css('z-index', 120001);
                        $('.ui-widget-overlay').last().css('z-index', 120000);
                        $(this).parent().find('.ui-dialog-buttonset > button:first-child').focus();
                        $(this).parent().find('.ui-dialog-buttonset > button').removeClass('ui-button ui-corner-all ui-widget');
                    },
                    close: function (event) {
                        $(this).dialog('destroy');
                        $(event.target).remove();
                    }
                });
            
            return $this;
        }
        
        /**
         * Show information dialog.
         * 
         * @param {String}
         *            text information text
         * @returns handler
         */
        export function info(text){
            var $dialog = $('<div/>').hide();
            
            $(function () {
                $dialog.appendTo('body').dialog({
                    autoOpen: false
                });
            })
            
            return function (text) {
        
                var then = $.noop;
                
                setTimeout(function () {
                    var $this = createNoticeDialog(
                        text,
                        [
                            {text: "はい",
                               "class": "large",
                               click: function(){
                                    $this.dialog('close');
                                    then();
                               }
                             }
                        ]);
                }, 0);
                
                return {
                    then: function (callback) {
                        then = callback;
                    }
                };
            };
        };
        
        /**
         * Show alert dialog.
         * 
         * @param {String}
         *            text information text
         * @returns handler
         */
        export function alert(text) {
            return info(text);
        };
        
        /**
         * Show confirm dialog.
         * 
         * @param {String}
         *            text information text
         * @returns handler
         */
        export function confirm(text) {
            var handleYes = $.noop;
            var handleNo = $.noop;
            var handleCancel = $.noop;
            var handleThen = $.noop;
            var hasCancelButton = false;
            
            var handlers = {
                ifYes: function (handler) {
                    handleYes = handler;
                    return handlers;
                },
                ifNo: function (handler) {
                    handleNo = handler;
                    return handlers;
                },
                ifCancel: function (handler) {
                    hasCancelButton = true;
                    handleCancel = handler;
                    return handlers;
                },
                then: function (handler) {
                    handleThen = handler;
                    return handlers;
                }
            };
        
            setTimeout(function () {
                
                var buttons = [];
                // yes button
                buttons.push({text: "はい",
                           "class": "yes large danger",
                           click: function(){
                                $this.dialog('close');
                                handleYes();
                                handleThen();
                           }
                         });
                // no button
                buttons.push({text: "いいえ",
                               "class": "no large",
                               click: function(){
                                    $this.dialog('close');
                                    handleNo();
                                    handleThen();
                               }
                             });
                // cancel button
                if (hasCancelButton) {
                    buttons.push({text: "Cancel",
                               "class": "cancel large",
                               click: function(){
                                    $this.dialog('close');
                                    handleCancel();
                                    handleThen();
                               }
                             });
                }
                
                var $this = createNoticeDialog(text, buttons);
            });
        
            return handlers;
        };
    }
    
    
    export class DirtyChecker {
        
        targetViewModel: KnockoutObservable<any>;
        initialState: string;
        
        constructor(targetViewModelObservable: KnockoutObservable<any>) {
            this.targetViewModel = targetViewModelObservable;
        }
        
        getCurrentState() {
            return ko.mapping.toJSON(this.targetViewModel());
        }
        
        reset() {
            this.initialState = this.getCurrentState();
        }
        
        isDirty() {
            return this.initialState !== this.getCurrentState();
        }
    }
}