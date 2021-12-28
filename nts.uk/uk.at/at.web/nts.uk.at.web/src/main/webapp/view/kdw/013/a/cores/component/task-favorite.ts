module nts.uk.ui.at.kdw013.taskfavorite {
        @handler({
            bindingName: 'kdw013-task-events'
        })
        export class Kdw013TaskEventBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
                const name = componentName();
                const mode = allBindingsAccessor.get('mode');
                const items = allBindingsAccessor.get('items');
                const screenA = allBindingsAccessor.get('screenA');
                const params = { mode, items ,screenA};
                
                
                const subscribe = (mode: boolean) => {

                    if (!mode) {
                        ko.cleanNode(element);

                        element.innerHTML = '';
                    } else {
                        ko.applyBindingsToNode(element, { component: { name, params } });
                    }
                };

                mode.subscribe(subscribe);

                subscribe(mode());

                

                return { controlsDescendantBindings: true };
            }
        }
    
        
        
        
        @component({
            name: 'kdw013-task-events',
            template: `
            <div class='edit-popup'>
                    <ul>
                        <li class='popupButton-f' data-bind="i18n: 'KDW013_77' ,click:$component.openFdialog"></li>
                        <li data-bind="i18n: 'KDW013_78' ,click:$component.removeFav"></li>
                    </ul>
            </div>
            <div data-bind="ntsAccordion: {active: 0}">
                <h3>
                    <label data-bind="i18n: 'KDW013_75'"></label>
                </h3>
                <div class='fc-events fc-task-events'>
                    <ul id='task-fav' data-bind="foreach: { data: $component.params.items, as: 'item' }">
                        <li class="title" data-bind="attr: {
                            'data-id': _.get(item.extendedProps, 'relateId', ''),
                            'data-color': item.backgroundColor,
                            'data-order': _.get(item.extendedProps, 'order', ''),
                            'data-favId': _.get(item.extendedProps, 'favId', '')
                        }">
                            <div data-bind="style: {
                                'background-color': item.backgroundColor
                            }"></div>
                            <div style="display: flex;">
                                <label  class='limited-label' style='width:90%;cursor: pointer;'  data-bind='text: item.title'>
                                </label>
                                <i class='fav-icon img-icon' style='width: 20px; height: 25px;' data-bind="click: function(item,evn) { $component.editFav(evn,_.get(item.extendedProps, 'favId', '')) }">
                                </i>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <style rel="stylesheet">
               .fc-task-events .fc-events>ul>li>div:first-child {
                    float: left;
                    width: 22px;
                    height: 22px;
                    margin-right: 3px;
                    border-radius: 50%;
                }
                .fc-container .fc-task-events .edit-popup{
                    visibility: hidden;
                    position: fixed;
                    z-index: 99;
                    box-shadow: 1px 1px 5px 2px #888;
                    background-color: #fff;
                    padding: 0 10px;
                }
                .fc-container .fc-task-events .edit-popup ul li{
                    cursor: pointer;
                    padding: 5px 5px;
                }
                .fc-container .fc-task-events .edit-popup ul li:hover{
                    background: #CDE2CD;
                    color: #0086EA ;
                }
                .fc-container .fc-task-events .show {
                    visibility: visible;
                }
                
            </style>
           `
        })
        export class Kdw013TaskEventComponent extends ko.ViewModel {
            constructor(public params: EventParams) {
                super();
            }
            
            editFav(e,id) {
                let editPopup = $('.fc-task-events .edit-popup');
                let pst = e.target;
                if(!pst){
                   editPopup.removeClass('show'); 
                }
                const { top, left, height, width } = pst.getBoundingClientRect();
                editPopup.addClass('show');
                editPopup.css({ "top": top, "left": left+width });
                editPopup.data('favId', id);
                $(".popup-area-f").ntsPopup({
                    trigger: ".popupButton-f",
                    position: {
                        my: "left top",
                        at: "left bottom",
                        of: ".popupButton-f"
                    },
                    showOnStart: false,
                    dismissible: false
                });
            }
            
            removeFav(data) {
                const vm = this;
                let id = $('.fc-task-events .edit-popup').data('favId');
                //A: お気に入り作業を削除する
                vm.$blockui('grayout').then(() => vm.$ajax('at', '/screen/at/kdw013/a/delete_task_set', { favId: id }))
                    .done(() => {
                        vm.$dialog.info({ messageId: 'Msg_16' }).then(() => {
                            vm.params.screenA.reloadTaskFav();
                        });
                    }).always(() => vm.$blockui('clear'));
                
                $('.fc-task-events .edit-popup').removeClass('show');
            }
            
            openFdialog(data) {
                const vm = this;
                $('.fc-task-events .edit-popup').removeClass('show');
                let id = $('.fc-task-events .edit-popup').data('favId');
                let item = _.find(vm.params.items(), item => _.get(item, 'extendedProps.favId') == id);
                //gọi màn F
                vm.params.screenA.favTaskName(item.title);
                vm.params.screenA.favoriteTaskItem({
                    // 社員ID
                    employeeId: vm.$user.employeeId,
                    // お気に入りID
                    favoriteId: id,
                    // お気に入り作業名称
                    taskName: item.title,
                    // お気に入り内容
                    favoriteContents: item.extendedProps.dropInfo.favoriteContents
                });
				setTimeout(() => {
					$('.input-f').focus();
				}, 100)
			
				jQuery('button.btn-error.small.danger').appendTo('.popup-area-f .textEditor.pb10');
            }
        }
        
         type EventParams = {
            items: KnockoutObservableArray<any>;
            mode: KnockoutComputed<boolean>;
            screenA: nts.uk.ui.at.kdw013.a.ViewModel;
        };
    
    
      
    }
