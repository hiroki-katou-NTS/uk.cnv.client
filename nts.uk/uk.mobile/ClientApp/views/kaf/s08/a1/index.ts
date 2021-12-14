import { _, Vue } from '@app/provider';
import { component, Prop, Watch } from '@app/core/component';
import { KafS00AComponent } from '../../../kaf/s00/a';
import { StepwizardComponent } from '@app/components';
import { KafS00BComponent } from '../../../kaf/s00/b';
import { KafS00CComponent } from '../../../kaf/s00/c';
import { KafS08A2Component } from '../../../kaf/s08/a2';
import { KafS00ShrComponent, AppType, Application } from '../../../kaf/s00/shr';
import * as moment from 'moment';


@component({
    name: 'kafs08a1',
    route: '/kaf/s08/a1',
    template: require('./index.vue'),
    validations: {

    },
    style: require('./style.scss'),
    components: {
        'kafs00-a': KafS00AComponent,
        'step-wizard': StepwizardComponent,
        'kafs00-b': KafS00BComponent,
        'kafs00-c': KafS00CComponent,
        'kafs08-a2': KafS08A2Component,
    }
})
export class KAFS08A1Component extends KafS00ShrComponent {
    //private seen: boolean = true;
    public step: string = 'KAFS08_10';

    @Prop({ default: true })
    public readonly mode: boolean;

    public isVisible: boolean = false;
    public date: Date = null;
    public listDate: any[] = [];
    public isValidateAll: Boolean = true;

    @Prop({ default: null })
    public params?: any;

    public derpartureTime: number = null;

    public returnTime: number = null;

    @Prop({ default: '' }) public readonly appReason: string;

    public user: any;
    public title: String = 'KafS08A1';
    public data: any = 'data';
    public application: Application;

    public created() {
        const vm = this;
        if (vm.params) {
            this.data = vm.params;
            vm.appDispInfoStartupOutput = vm.params.businessTripInfoOutputDto.appDispInfoStartup;
            vm.derpartureTime = vm.params.businessTripDto.departureTime;
            vm.returnTime = vm.params.businessTripDto.returnTime;
            vm.appDispInfoStartupOutput = vm.params.businessTripInfoOutputDto.appDispInfoStartup;
            vm.application = vm.appDispInfoStartupOutput.appDetailScreenInfo.application;
        } else {
            vm.application = vm.createApplicationInsert(AppType.BUSINESS_TRIP_APPLICATION);
        }

        vm.fetchStart();

        vm.$watch('params', (newV, oldV) => {
            if (newV) {
                vm.data.businessTrip = vm.params.businessTripDto;
                vm.data.businessTripInfoOutput = vm.params.businessTripInfoOutputDto;
                vm.appDispInfoStartupOutput = vm.params.businessTripInfoOutputDto.appDispInfoStartup;
                vm.application = vm.appDispInfoStartupOutput.appDetailScreenInfo.application;
            }
            vm.updateKaf000_A_Params(vm.user);
            vm.updateKaf000_B_Params(vm.mode);
            vm.updateKaf000_C_Params(vm.mode);
        });
    }

    public mounted() {
        
    }

    public fetchStart() {
        const vm = this;

        vm.$mask('show');

        vm.$auth.user.then((usr: any) => {
            vm.user = usr;
        }).then(() => {
            if (vm.mode) {
                return vm.loadCommonSetting(AppType.BUSINESS_TRIP_APPLICATION);
            }

            return true;
        }).then((loadData: any) => {
            if (loadData) {
                let params = vm.mode ? {
                    mode: vm.mode,
                    companyId: vm.user.companyId,
                    employeeId: vm.user.employeeId,
                    listDates: [],
                    businessTripInfoOutput: null,
                    businessTrip: null
                } :
                    {
                        mode: vm.mode,
                        companyId: vm.user.companyId,
                        employeeId: vm.user.employeeId,
                        listDates: [],
                        businessTrip: vm.params.businessTripDto,
                        businessTripInfoOutput: vm.params.businessTripInfoOutputDto
                    };

                return vm.$http.post('at', API.startKAFS08, params);
            }
        }).then((res: any) => {
            if (!res) {
                return;
            }
            vm.data = res.data;
            vm.updateKaf000_A_Params(vm.user);
            vm.updateKaf000_B_Params(vm.mode);
            vm.updateKaf000_C_Params(vm.mode);
            vm.$mask('hide');

            setTimeout(function () {
                let focusElem;
                if (vm.mode) {
                    focusElem = document.querySelector('[placeholder=\'yyyy-mm-dd\']');
                } else {
                    focusElem = document.querySelector('[placeholder=\'-- --:--\']');
                }
                (focusElem as HTMLElement).focus();
            }, 200);
        }).catch((err: any) => {
            vm.$modal.error(err);
        });
    }

    public getDateArray = function (startDate, endDate) {
        let dates = [];
        startDate = moment(startDate, 'YYYY/MM/DD');
        dates.push(startDate.format('YYYY/MM/DD'));
        while (!startDate.isSame(endDate)) {
            startDate = startDate.add(1, 'days');
            dates.push(startDate.format('YYYY/MM/DD'));
        }

        return dates;
    };

    //Nhảy đến step tiếp theo
    public nextToStepTwo() {
        const vm = this;

        let validAll: boolean = true;

        for (let child of vm.$children) {
            child.$validate();
            if (!child.$valid) {
                validAll = false;
            }
        }
        vm.isValidateAll = validAll;
        vm.$validate();
        if (!vm.$valid || !validAll) {
            vm.$nextTick(() => {
                vm.$mask('hide');
            });

            window.scrollTo(500, 0);

            return;


        }
        //check date when press next
        if (vm.mode) {
            let Difference_In_Days = moment(vm.application.opAppEndDate).diff(moment(vm.application.opAppStartDate), 'days');
            //check day > 31 days between 2 Dates
            if (Difference_In_Days > 31) {
                vm.$modal.error({ messageId: 'Msg_277' });

                return;
            }

            //mode new
            let achievementDetails = vm.data.businessTripInfoOutput.businessTripActualContent;
            let businessTripInfoOutput = vm.data;
            //gửi comment sang màn hình A2
            let commentSet = vm.data.businessTripInfoOutput.setting.appCommentSet;
            let appReason = vm.application.opAppReason;

            vm.$mask('show');

            vm.$http.post('at', API.changeAppDate, {
                isNewMode: true,
                isError: 0,
                application: vm.application,
                businessTrip: null,
                businessTripInfoOutput: vm.data.businessTripInfoOutput
                //businessTrip: vm.mode ? null : vm.data.appWorkChange
            }).then((res: any) => {
                let response = res.data;
                if (response.result) {
                    // this.data.businessTripInfoOutput = response.businessTripInfoOutputDto;
                    if (response.confirmMsgOutputs.length != 0) {
                        vm.handleConfirmMessage(response.confirmMsgOutputs, response);
                    }
                    vm.data.businessTripInfoOutput = response.businessTripInfoOutputDto;
                }
                vm.$emit('nextToStepTwo', vm.listDate, vm.application, businessTripInfoOutput, vm.derpartureTime, vm.returnTime, achievementDetails, commentSet, appReason, vm.mode);

                vm.$mask('hide');
            }).catch((err) => {
                vm.handleErrorMessage(err);
                vm.$mask('hide');
            });
        }

        //mode edit
        if (!vm.mode) {
            vm.data.businessTrip = vm.params.businessTripDto;
            vm.data.businessTripInfoOutput = vm.params.businessTripInfoOutputDto;
            let achievementDetails = vm.data.businessTrip ? vm.data.businessTrip.tripInfos || [] : [];
            // let businessTripInfoOutput = vm.data;
            //gửi comment sang màn hình A2
            let commentSet = vm.data.businessTripInfoOutput.setting.appCommentSet;
            //let application = vm.data.businessTripInfoOutput.appDispInfoStartup.appDetailScreenInfo.application;
            let appReason = vm.application.opAppReason;
            let startDate = vm.data.businessTripInfoOutput.appDispInfoStartup.appDetailScreenInfo.application.opAppStartDate;
            //let startDateFormat = new Date(startDate);
            let endDate = vm.data.businessTripInfoOutput.appDispInfoStartup.appDetailScreenInfo.application.opAppEndDate;
            //let endDateFormat = new Date(endDate);
            let listDateEditMode = vm.getDateArray(startDate, endDate);
            vm.data.businessTrip.departureTime = vm.derpartureTime;
            vm.data.businessTrip.returnTime = vm.returnTime;
            this.$emit('nextToStepTwo', listDateEditMode, vm.application, vm.data, vm.derpartureTime, vm.returnTime, achievementDetails, commentSet, appReason, vm.mode);
        }
    }

    //scroll to Top
    public scrollToTop() {
        window.scrollTo(500, 0);
    }

    //handle message error
    public handleErrorMessage(res: any) {
        const self = this;
        self.$mask('hide');
        if (res.messageId) {
            return self.$modal.error({ messageId: res.messageId, messageParams: res.parameterIds });
        } else {

            if (_.isArray(res.errors)) {
                return self.$modal.error({ messageId: res.errors[0].messageId, messageParams: res.parameterIds });
            } else {
                return self.$modal.error({ messageId: res.errors.messageId, messageParams: res.parameterIds });
            }
        }
    }

    //thực hiện ẩn/hiện alert error
    public toggleErrorAlert() {
        let x = document.getElementById('error');
        if (x.style.display === 'none') {
            x.style.display = 'block';
        } else {
            x.style.display = 'none';
        }
    }

    public handleConfirmMessage(listMes: any, res: any) {

        if (!_.isEmpty(listMes)) {
            let item = listMes.shift();
            this.$modal.confirm({ messageId: item.messageId }).then((value) => {
                this.$mask('hide');
                if (value == 'yes') {
                    if (_.isEmpty(listMes)) {
                        return;
                    } else {
                        this.handleConfirmMessage(listMes, res);
                    }

                }
            });
        }
    }

    public kaf000BChangeDate(objectDate) {
        const vm = this;
        if (objectDate.startDate) {
            if (vm.mode) {
                vm.application.appDate = vm.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                vm.application.opAppStartDate = vm.$dt.date(objectDate.startDate, 'YYYY/MM/DD');
                vm.application.opAppEndDate = vm.$dt.date(objectDate.endDate, 'YYYY/MM/DD');
                vm.listDate = [];
                let diffDate = moment(vm.application.opAppEndDate).diff(moment(vm.application.opAppStartDate), 'days');
                for (let i = 0; i <= diffDate; i++) {
                    let loopDate = moment(moment(vm.application.opAppStartDate, 'YYYY/MM/DD').add(i, 'day').format('YYYY/MM/DD'));
                    vm.listDate.push(loopDate.format('YYYY/MM/DD'));
                }
            }
        }
    }
    
    public kaf000BChangePrePost(prePostAtr) {
        const vm = this;
        vm.application.prePostAtr = prePostAtr;
    }

    public kaf000CChangeReasonCD(opAppStandardReasonCD) {
        const vm = this;
        vm.application.opAppStandardReasonCD = opAppStandardReasonCD;
    }

    public kaf000CChangeAppReason(opAppReason) {
        const vm = this;
        vm.application.opAppReason = opAppReason;
    }

}

const API = {
    startKAFS08: 'at/request/application/businesstrip/mobile/startMobile',
    checkBeforeRegister: 'at/request/application/businesstrip/mobile/checkBeforeRegister',
    changeAppDate: 'at/request/application/businesstrip/mobile/changeAppDate'
};

