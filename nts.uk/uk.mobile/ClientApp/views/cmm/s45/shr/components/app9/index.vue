<template>
  <div class="cmms45componentsapp9">
    <!-- B1 -->
      <div class="row content-div uk-bg-headline border-top uk-border-light-gray">
          <span>{{ "KAFS04_2" | i18n }}</span>
      </div>
    <div class="row content-div border-top uk-border-light-gray">
        <div class="col-12">
            <div class="row pb-2">
                <kaf-s00-p1 class="px-2" style="width: 110px;" v-bind:params="kafS00P1Params1" />
                <span style="width: 125px;" class="px-0" v-if="time.attendanceTime != null">{{'KAFS04_19' | i18n($dt.timewd(time.attendanceTime))}}</span>
                <span style="width: 125px;" class="px-0" v-else>{{'KAFS04_18' | i18n}}</span>
                <span v-if="displayCancelText">{{  }} {{'KAFS04_14' | i18n}}</span>
            </div>
            <div class="row">
                <kaf-s00-p1 class="px-2" style="width: 110px;" v-bind:params="kafS00P1Params2" />
                <span style="width: 125px;" class="px-0" v-if="time.leaveTime != null">{{'KAFS04_20' | i18n($dt.timewd(time.leaveTime))}}</span>
                <span style="width: 125px;" class="px-0" v-else>{{'KAFS04_18' | i18n}}</span>
                <span v-if="displayCancelText">{{  }} {{'KAFS04_15' | i18n}}</span>
            </div>
        </div>
    </div>

    <!-- B2 -->
    <template v-if="managementMultipleWorkCycles">
        <div class="row content-div uk-bg-headline border-top uk-border-light-gray">
            <span>{{ "KAFS04_8" | i18n }}</span>
        </div>
      <div class="row content-div border-top uk-border-light-gray">
          <div class="col-12">
              <div class="row pb-2">
                  <kaf-s00-p1 class="px-2" style="width: 110px;" v-bind:params="kafS00P1Params3" />
                  <span style="width: 125px;" class="px-0" v-if="time.attendanceTime2 != null">{{'KAFS04_19' | i18n($dt.timewd(time.attendanceTime2))}}</span>
                  <span style="width: 125px;" class="px-0" v-else>{{'KAFS04_18' | i18n}}</span>
                  <span v-if="displayCancelText">{{  }} {{'KAFS04_16' | i18n}}</span>
              </div>
              <div class="row">
                  <kaf-s00-p1 class="px-2" style="width: 110px;" v-bind:params="kafS00P1Params4" />
                  <span style="width: 125px;" class="px-0" v-if="time.leaveTime2 != null">{{'KAFS04_20' | i18n($dt.timewd(time.leaveTime2))}}</span>
                  <span style="width: 125px;" class="px-0" v-else>{{'KAFS04_18' | i18n}}</span>
                  <span v-if="displayCancelText">{{ }} {{'KAFS04_17' | i18n}}</span>
              </div>
          </div>
      </div>
    </template>
  </div>
</template>