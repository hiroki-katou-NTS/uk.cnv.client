<template>
    <div>
        <div v-for="data in (params.appDetail ? params.appDetail.details : [])"
             v-bind:key="data.appTimeType">
            <div class="card-label"
                 v-if="(data.appTimeType === 0 && params.appDetail.reflectSetting.destination.firstBeforeWork == 1)
                        || (data.appTimeType === 1 && params.appDetail.reflectSetting.destination.firstAfterWork == 1)
                        || (data.appTimeType === 2 && params.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles && params.appDetail.reflectSetting.destination.secondBeforeWork == 1)
                        || (data.appTimeType === 3 && params.appDispInfoStartupOutput.appDispInfoNoDateOutput.managementMultipleWorkCycles && params.appDetail.reflectSetting.destination.secondAfterWork == 1)
                        || (data.appTimeType === 4 && params.appDetail.reflectSetting.destination.privateGoingOut == 1 && params.appDetail.reflectSetting.destination.privateGoingOut == 1)
                        || (data.appTimeType === 5 && params.appDetail.reflectSetting.destination.unionGoingOut == 1 && params.appDetail.reflectSetting.destination.unionGoingOut == 1)">
                <div class="card-header uk-bg-accordion">
                    <span>{{title(data.appTimeType) | i18n}}</span>
                </div>
                <div class="card-body">
                    <div v-if="data.appTimeType < 4" class="row">
                        <div class="col-6">
                            <kafs00subp1 v-bind:params="scheduledTime(data.appTimeType)"/>
                        </div>
                        <div class="col-6">
                            <span v-if="data.appTimeType == 0 || data.appTimeType == 2">{{'KAFS12_33' | i18n(getTime(data.timeZones[0].endTime))}}</span>
                            <span v-if="data.appTimeType == 1 || data.appTimeType == 3">{{'KAFS12_37' | i18n(getTime(data.timeZones[0].startTime))}}</span>
                        </div>
                    </div>
                    <div v-else>
                        <div v-for="timeZone in data.timeZones" class="row">
                            <div class="col-6">
                                <span>{{'KAFS12_18' | i18n(timeZone.workNo)}}</span>
                            </div>
                            <div class="col-6">
                                <span>{{'KAFS12_43' | i18n([getTime(timeZone.startTime), getTime(timeZone.endTime)])}}</span>
                            </div>
                        </div>

                    </div>
                    <hr style="margin-top: 0.5rem; margin-bottom: 0.5rem">
                    <div class="row">
                        <div class="col-6">
                            <span>{{'KAFS12_34' | i18n(requiredTime(data.appTimeType))}}</span>
                        </div>
                        <div class="col-6">
                            <span>{{'KAFS12_35' | i18n(totalAppTime(data.applyTime))}}</span>
                        </div>
                    </div>
                    <hr style="margin-top: 0.5rem; margin-bottom: 0.5rem">
                    <div class="uk-text-dark-gray">
                        <span>{{'KAFS12_36' | i18n}}</span>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.superHoliday60H == 1 && params.appDetail.timeLeaveManagement.super60HLeaveMng.super60HLeaveMngAtr">
                            <span>{{'KAFS12_38' | i18n(getTime(data.applyTime.super60AppTime))}}</span>
                        </div>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.substituteLeaveTime == 1 && params.appDetail.timeLeaveManagement.timeSubstituteLeaveMng.timeSubstituteLeaveMngAtr">
                            <span>{{'KAFS12_39' | i18n(getTime(data.applyTime.substituteAppTime))}}</span>
                        </div>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.annualVacationTime == 1 && params.appDetail.timeLeaveManagement.timeAnnualLeaveMng.timeAnnualLeaveMngAtr">
                            <span>{{'KAFS12_40' | i18n(getTime(data.applyTime.annualAppTime))}}</span>
                        </div>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.childNursing == 1">
                            <span>{{'KAFS12_41' | i18n(getTime(data.applyTime.childCareAppTime))}}</span>
                        </div>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.nursing == 1">
                            <span>{{'KAFS12_42' | i18n(getTime(data.applyTime.careAppTime))}}</span>
                        </div>
                        <div class="content-div" v-if="params.appDetail.reflectSetting.condition.specialVacationTime == 1 && params.appDetail.timeLeaveManagement.timeSpecialLeaveMng.timeSpecialLeaveMngAtr">
                            <span v-if="data.applyTime.specialAppTime > 0">{{'KAFS12_45' | i18n([specialFrameName(data.applyTime.specialLeaveFrameNo), getTime(data.applyTime.specialAppTime)])}}</span>
                            <span v-else>{{'KAFS12_51' | i18n(getTime(data.applyTime.specialAppTime))}}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>