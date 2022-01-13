module nts.uk.ui.at.kdw013.b {
	import getText = nts.uk.resource.getText;
	import ajax = nts.uk.request.ajax;
	import block = nts.uk.ui.block;
	import error = nts.uk.ui.dialog.error;
	const API: API = {
        START: '/screen/at/kdw013/common/start',
        SELECT: '/screen/at/kdw013/c/select',
        START_F: '/screen/at/kdw013/f/start_task_fav_register',
        ADD_FAV_TASK_F: '/screen/at/kdw013/f/create_task_fav',
    };

    const COMPONENT_NAME = 'kdp013b';
    const { getTimeOfDate, number2String } = share;

    @handler({
        bindingName: COMPONENT_NAME,
        validatable: true,
        virtual: false
    })
    export class BindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const name = COMPONENT_NAME;
            const params = valueAccessor();

            ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);

            return { controlsDescendantBindings: true };
        }
    }

	let template = `
        <div class="detail-event">
            <div class="header">
                <div data-bind="i18n: 'KDW013_26'"></div>
                <div class="actions">
                    <button id='edit' data-bind="click: $component.params.update, icon: 204, size: 12"></button>
                    <button data-bind="click: $component.remove, icon: 203, size: 12"></button>
					<!-- ko if: dataSources().length == 1 && inputMode() == '0' -->
						<button class="popupButton-f-from-b" data-bind="icon: 229, size: 12, click:$component.openFDialog"></button>
					<!-- /ko -->
                    <button data-bind="click: $component.params.close, icon: 202, size: 12"></button>
                </div>
            </div>
			<table class="timePeriod">
                <colgroup>
                    <col width="105px" />
                </colgroup>
                <tbody>
                    <tr>
                        <td data-bind="i18n: 'KDW013_27'"></td>
                        <td data-bind="text: time"></td>
                    </tr>
				</tbody>
			</table>
			<div class="taskDetailsB" data-bind="foreach: dataSources">
	            <table>
	                <colgroup>
	                    <col width="105px" />
	                </colgroup>
	                <tbody data-bind="foreach: items">
	                    <tr>
	                        <td ><div data-bind="i18n: key"> </div></td>
	                        <td ><div data-bind="text: value"> </div></td>
	                    </tr>
	                </tbody>
	            </table>
			</div>
        </div>
        <div class="popup-area-f-from-b">
			<!-- F1_2 -->
 			<button class="closeF" data-bind="click: closeFDialog, icon: 202, size: 12"></button>
            <!-- F2_1 -->
            <div class= "pb10 align-left" data-bind="i18n: 'KDW013_70'"></div>

            <!-- F3_2 -->
            <div class="textEditor pb10">
                <!-- F3_1 -->
				<div data-bind="ntsFormLabel: {required: true, constraint: 'FavoriteTaskName'}, text: nts.uk.resource.getText('KDW013_71')"></div>
                <input
                class="input-f-b"
                tabindex="1"
                id="KDW013_71"
                data-bind="ntsTextEditor: {
                    value: favTaskName, 
                    required: true,
                    constraint: 'FavoriteTaskName',
                    name: '#[KDW013_71]',
                    enable: true
                    }"
                />
            </div>

            <!-- F4_1 -->
            <button class= "proceed normal" tabindex = "2" data-bind="i18n: 'KDW013_1', click: addFavTask"></button>
        </div>

        <style>
            .detail-event {
                width: 320px;
            }
            .detail-event .header {
                box-sizing: border-box;
                position: relative;
                padding-bottom: 5px;
                line-height: 35px;
                margin-top: -5px;
            }
            .detail-event .header .actions {
                position: absolute;
                top: 0px;
                right: -5px;
            }
            .detail-event .header .actions button {
                margin: 0;
                padding: 0;
                box-shadow: none;
                border: none;
                border-radius: 50%;
                width: 30px;
            }
            .detail-event .header .actions button:focus, .detail-event .header .actions button:hover {
                background-color:#dddddd;
                margin: 0;
                padding: 0;
                box-shadow: none;
                border: none;
                border-radius: 50%;
                width: 30px;
            }
            .detail-event table {
                width: 100%;
            }
            .detail-event table tr {
                height: 34px;
            }
            .detail-event table tr>td{
                vertical-align: top;
                padding-top: 6px;
				padding-left: 5px;
            }
            .detail-event table tr>td>div {
                max-height: 120px;
                overflow-y: auto;
                word-break: break-all;
            }
			.taskDetailsB table{
				border: 1px solid #999;
				margin-bottom: 5px;
			}
            .popup-area-f-from-b {
                padding: 10px !important;
                text-align: right;
                width: 268px;
            }
            .pb10 {
                padding-bottom: 10px !important;
            }
            .pb20 {
                padding-bottom: 20px !important;
            }           
            .align-left {
                text-align: left;
            }     
            .pr10 {
                padding-right: 10px;
            }
			.closeF {
			    box-shadow: none;
			    border: none;
			    border-radius: 50%;
				width: 30px;
			}
			.taskDetailsB::-webkit-scrollbar{
				width: 8px;
			}
			.taskDetailsB::-webkit-scrollbar-thumb {
				border-radius: 4px;
				background-color: #dededfa6;
			}
			.taskDetailsB::-webkit-scrollbar-thumb:hover
			{
				background-color: #c1c1c1;
			}
        </style>
        `;

    @component({
        name: COMPONENT_NAME,
        template: template
    })
    export class ViewModel extends ko.ViewModel {
        dataSources: KnockoutObservableArray<TaskDetailB> = ko.observableArray([]);
        taskFrameSettings: a.TaskFrameSettingDto[] = [];
		time: KnockoutObservable<string> = ko.observable('');

        // F画面を起動する
        favoriteTaskItem: KnockoutObservable<FavoriteTaskItemDto | null> = ko.observable(null);

        registerFavoriteCommand: KnockoutObservableArray<RegisterFavoriteCommand> = ko.observableArray([]);
        favTaskName: KnockoutObservable<string> = ko.observable('');
        // F画面: add new 
        taskContents: TaskContentDto[] = [];

		inputMode: KnockoutObservable<string> = ko.observable('0');

		position: any;
        constructor(public params: Params) {
            super();
            const vm = this

            // Init popup
        	vm.initPopup();  
			vm.getInputMode();  

        }

		getInputMode() {
			$.urlParam = function (name) {
				var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
				if (results == null) {
					return '0';
				}
				else {
					return decodeURI(results[1]) || 0;
				}
			}
			const vm = this;
			vm.inputMode($.urlParam('mode'));
		}

		initPopup(){
			$(".popup-area-f-from-b").ntsPopup({
                trigger: ".popupButton-f-from-b",
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".popupButton-f-from-b"
                },
                showOnStart: false,
  				dismissible: false
            })			
		}
    
        openFDialog(){
			const vm = this;
			vm.favTaskName('');
            setTimeout(() => { $('.input-f-b').focus(); }, 100);

			nts.uk.ui.errors.clearAll();
			setTimeout(() => {
				jQuery('button.btn-error.small.danger').appendTo('.popup-area-f-from-b .textEditor.pb10');									
			}, 100);
		}

		closeFDialog() {
			$(".popup-area-f-from-b").ntsPopup('hide');
			nts.uk.ui.errors.clearAll();
		}

        mounted() {
            const vm = this;
            const { params } = vm;
            const { data, position } = params;
			vm.position = position;
            ko.computed({
                read: () => {
                    const taskDetails: TaskDetailB[] = [];
                    const event = ko.unwrap(data);

                    if (event && event.extendedProps.status == "update") {
						nts.uk.ui.errors.clearAll();
                        vm.favTaskName('');
                        const { extendedProps, start, end } = event as any as calendar.EventRaw;
						if(extendedProps.taskBlock.taskDetails.length == 1){
							
							if (_.find(extendedProps.taskBlock.taskDetails[0].taskItemValues, i => i.itemId == 3).value == '') {
								_.find(extendedProps.taskBlock.taskDetails[0].taskItemValues, i => i.itemId == 3).value = '0';
							}
							
						}
						
						const startTime = getTimeOfDate(start);
                        const endTime = getTimeOfDate(end);
						vm.time(`${number2String(startTime)}${vm.$i18n('KDW013_30')}${number2String(endTime)}`);

                        let {taskBlock} = extendedProps;
                    	vm.taskFrameSettings = extendedProps.taskFrameUsageSetting.taskFrameUsageSetting.frameSettingList;
						
						//set valua in f screen
						
						vm.taskContents = _.map(_.filter(taskBlock.taskDetails[0].taskItemValues, i => i.itemId > 3 && i.itemId < 9), t => {return { itemId: t.itemId, taskCode: t.value}});
						let workCodeFrameNo: any[] = [];
						_.forEach(taskBlock.taskDetails, t => {
							let frameNo = t.supNo;
							let workCode1 = null, workCode2 = null, workCode3 = null, workCode4 = null, workCode5 = null; 
							_.forEach(t.taskItemValues, i =>{
								if(i.itemId == 4 && i.value != ''){
									workCode1 = i.value;
								}else if(i.itemId == 5 && i.value != ''){
									workCode2 = i.value;
								}else if(i.itemId == 6 && i.value != ''){
									workCode3 = i.value;
								}else if(i.itemId == 7 && i.value != ''){
									workCode4 = i.value;
								}else if(i.itemId == 8 && i.value != ''){
									workCode5 = i.value;
								}
							});
							workCodeFrameNo.push({frameNo, workCode1, workCode2, workCode3, workCode4, workCode5});
						});
						let param ={
							employeeId: extendedProps.employeeId,
							refDate: start,
							itemIds: _.filter(_.map(extendedProps.displayManHrRecordItems, i => i.itemId), t => t > 8),
							workCodeFrameNo
						}
			            ajax('at', API.START, param).done((data: StartWorkInputPanelDto) => {
							_.forEach(taskBlock.taskDetails, taskDetail =>{
								taskDetails.push(vm.setlableValueItems(taskDetail,data, extendedProps.displayManHrRecordItems));
							});
							vm.dataSources(taskDetails);
							setTimeout(() => {
								resetHeightB();
								vm.updatePopupSize();
								// Init popup
        						vm.initPopup();
							}, 150);
						});
                    } else {
                        vm.dataSources(taskDetails);
						// Init popup
						vm.initPopup();
                    }
                },
                disposeWhenNodeIsRemoved: vm.$el
            });
        }
		// update popup size
        updatePopupSize(){
			const vm = this;
//            vm.position.valueHasMutated();
        }
    
		setlableValueItems(taskDetail: IManHrTaskDetail, data: StartWorkInputPanelDto, displayManHrRecordItem: DisplayManHrRecordItem[]): TaskDetailB {
			let vm = this;
			let items: KeyValue[] = [];

			let range = _.find(taskDetail.taskItemValues, i => i.itemId == 3).value;
			items.push({ key: 'KDW013_25', value: number2String(parseInt(range)) });			
			
			let frameNoVsTaskFrameNos = _.find(data.frameNoVsTaskFrameNos, i => i.frameNo == taskDetail.supNo)
			const { taskFrameNo1, taskFrameNo2, taskFrameNo3, taskFrameNo4, taskFrameNo5 } = frameNoVsTaskFrameNos;
			
			const [first, second, thirt, four, five] = vm.taskFrameSettings;
			
			if (first && first.useAtr === 1) {
				let item = _.find(taskDetail.taskItemValues, i => i.itemId == 4);
				if(item && item.value){
					items.push(vm.setTaskData(first, item.value, taskFrameNo1));					
				}
            }
            if (second && second.useAtr === 1) {
                let item = _.find(taskDetail.taskItemValues, i => i.itemId == 5);
				if(item && item.value){
					items.push(vm.setTaskData(second, item.value, taskFrameNo2));					
				}            }
            if (thirt && thirt.useAtr === 1) {
                let item = _.find(taskDetail.taskItemValues, i => i.itemId == 6);
				if(item && item.value){
					items.push(vm.setTaskData(thirt, item.value, taskFrameNo3));					
				}
            }
            if (four && four.useAtr === 1) {
                let item = _.find(taskDetail.taskItemValues, i => i.itemId == 7);
				if(item && item.value){
					items.push(vm.setTaskData(four, item.value, taskFrameNo4));					
				}
            }
            if (five && five.useAtr === 1) {
              let item = _.find(taskDetail.taskItemValues, i => i.itemId == 8);
				if(item && item.value){
					items.push(vm.setTaskData(five, item.value, taskFrameNo5));					
				}
            }
			// cho vao day de sap xep
			let manHrTaskDetail = new ManHrTaskDetail(taskDetail, data, displayManHrRecordItem);
			
			_.forEach(manHrTaskDetail.taskItemValues(), (item: TaskItemValue) => {
				
				let infor : ManHourRecordItemDto = _.find(data.manHourRecordItems, i => i.itemId == item.itemId);
				if(infor && infor.useAtr == 1 && item.value() != null && item.value() != '' && item.itemId > 8){
					if(item.itemId == 9){
						// work plate
						let workLocation = _.find(data.workLocation, w => w.workLocationCD == item.value());
						if(workLocation){
							items.push({key: infor.name, value: item.value() + ' ' + workLocation.workLocationName});	
						}else{
							items.push({key: infor.name, value: item.value() + ' ' + getText('KDW013_40')});
						}
					}else if(item.itemId >= 25 && item.itemId >= 29){
						let taskSupInfoChoicesDetail : TaskSupInfoChoicesDetailDto[] = _.filter(data.taskSupInfoChoicesDetails, i => i.itemId == item.itemId);
						if(taskSupInfoChoicesDetail && taskSupInfoChoicesDetail.length > 0){
							let selected = _.find(taskSupInfoChoicesDetail, w => w.code == item.value());
							if(selected){
								items.push({key: infor.name, value:  item.value() + ' ' +  selected.name});	
							}else{
								items.push({key: infor.name, value:  item.value() + ' ' + getText('KDW013_40')});
							}
						}
					}else{
						items.push({key: infor.name, value:  vm.formatDataShow(item, data) });	
					}
				}
			});
			
			return {items : items};
		}
		
		formatDataShow(item: TaskItemValue, data: StartWorkInputPanelDto):string {
			if(item.type == 0){
				let optionValue = _.find(data.taskSupInfoChoicesDetails, t => t.itemId == item.itemId && t.code == item.value());
				if(optionValue){
					return item.value() + ' ' + optionValue.name;	
				}else{
					return item.value() + ' ' + getText('KDW013_40');
				}
			}else if(item.type == 2){
				
			}else if(item.type == 3){
				
			}else if(item.type == 5){
				return number2String(parseInt(item.value()));
			}else if(item.type == 6){
				return number2String(parseInt(item.value()));
			}else if(item.type == 7 || item.type == 9){
				return item.value();
			}
			return item.value();
		}

        setTaskData(setting: a.TaskFrameSettingDto, value: string, option: TaskDto[]):KeyValue{
			let item: KeyValue = {key: setting.frameName, value: ''};
			if(value){
                const exist = _.find(option, o => o.code === value);
                if (exist) {
					item.value = value + ' ' + exist.displayInfo.taskName;                    
                }else{
					item.value = value + ' ' + getText('KDW013_40');
				}
                
            }
			return item;
        }

        addFavTask() {
            const vm = this;

            const registerFavoriteCommand : RegisterFavoriteCommand = {
                taskName: vm.favTaskName(),
                contents: vm.taskContents
            }

            vm.$blockui('show');
            vm.$validate(".input-f-b").then((valid: boolean) => {
				if (valid) {
					nts.uk.ui.errors.clearAll();
                    vm.$ajax('at', API.ADD_FAV_TASK_F, registerFavoriteCommand)
                    .done(() => {
                        vm.$dialog.info({ messageId: 'Msg_15' }).then(()=>{
								vm.closeFDialog();
                                vm.params.screenA.reloadTaskFav();
                        }); 
                    }).fail((error: any) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });

                } else {
                    vm.$blockui("clear");
                }
            });
        }

        remove() {
            const vm = this;
            $.Deferred()
                .resolve()
                .then(() => {
                    vm.params.remove();
                });
        }
    }
	
	type TaskDetailB = {
        items: KeyValue[];
    }

    type KeyValue = {
        key: string;
        value: string;
    }

    type Params = {
        close: () => void;
        update: () => void;
        remove: () => void;
        mode: KnockoutObservable<boolean>;
        data: KnockoutObservable<FullCalendar.EventApi>;
		position: KnockoutObservable<null | any>;
        $settings: KnockoutObservable<a.StartProcessDto | null>;
        $share: KnockoutObservable<nts.uk.ui.at.kdw013.StartWorkInputPanelDto | null>;
    }

	export function resetHeightB():void {
		let innerHeight = window.innerHeight - 35
		let heightTaskDetails = -5; 
		_.each($('.taskDetailsB table'),(table:any)=>{
			heightTaskDetails = heightTaskDetails + table.offsetHeight + 5;
		});
		
		let aboveBelow = 160;
		if(innerHeight - aboveBelow >= heightTaskDetails){
			$('.taskDetailsB').css({ "overflow-y": "unset"});
			$('.taskDetailsB').css({ "max-height": heightTaskDetails + 'px' });
		}else if(innerHeight - aboveBelow < heightTaskDetails){
			$('.taskDetailsB').css({ "overflow-y": "scroll"});
			$('.taskDetailsB').css({ "max-height": (innerHeight - aboveBelow - 45) + 'px' });
		}
	}
}