<template>
  <div class="kafs10step1">
    <div>
      <kafs00-a
        v-if="$appContext.kaf000_A_Params != null"
        v-bind:params="$appContext.kaf000_A_Params"
      />
      <div class="accordion mb-2" v-if="$appContext.overTimeWorkHoursDto != null">
          <div class="card" v-if="true">
            <div class="card-header uk-bg-accordion">
              <button class="btn btn-link" type="button">
                  {{'KAFS05_41' | i18n}}
              </button>
            </div>
            <div class="collapse">
              <div class="card-body">
                  <kafs00subp2 v-bind:params="$appContext.overTimeWorkHoursDto" />      
              </div>
            </div>
          </div>
      </div>
    </div>
    <div
      v-if="!$appContext.$valid || !$appContext.isValidateAll || $appContext.isMsg_1556"
      class="card bg-danger top-alert uk-text-danger topError" style="margin-bottom: 10px"
    >
      <button class="btn btn-link uk-text-danger">
        <i class="fa fa-exclamation-circle" aria-hidden="true"></i>
        {{ ($appContext.isMsg_1556 ? "Msg_1556" : "KAFS07_1") | i18n($appContext.isMsg_1556 ? ($appContext.modeNew ? $appContext.date : $appContext.appDispInfoStartupOutput.appDetailScreenInfo.application.appDate) : '') }}
      </button>
    </div>
    <div>
      <kafs00-b
        v-if="$appContext.kaf000_B_Params != null"
        v-bind:params="$appContext.kaf000_B_Params"
        v-on:kaf000BChangeDate="$appContext.kaf000BChangeDate"
        v-on:kaf000BChangePrePost="$appContext.kaf000BChangePrePost"
      />
    </div>

    <!-- WorkType WorkTime -->
    <div class="card card-label">
      <!--A1_4_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS10_4" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>
      <div class="card-body">
        <!--A1_4_2-->
        <span class="textSize uk-text-dark-gray">{{ "KAFS10_5" | i18n }}</span>
        <!--A1_4_3-->
        <button
          v-on:click="$appContext.openKDLmodal('worktype')"
          type="button"
          class="btn btn-selection mt-2 mb-2"
        >
          <span class="badge badge-secondary">{{
            workInfo.workType.code
          }}</span>
          <span>{{ workInfo.workType.name }}</span>
        </button>
        <!--A1_4_4-->
        <span class="textSize uk-text-dark-gray">{{ "KAFS10_6" | i18n }}</span>
        <!--A1_4_5-->
        <button
          v-on:click="$appContext.openKDLmodal('worktime')"
          type="button"
          class="btn btn-selection mt-2 mb-2"
        >
          <span class="badge badge-secondary">{{
            workInfo.workTime.code
          }}</span>
          <span>{{ workInfo.workTime.name }}</span>
        </button>
      </div>
    </div>

  <!-- Work hours --> 
    <div class="card card-label">
      <!-- A1_5_1 -->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS10_7" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>
      <!-- A1_5_2 -->
      <div class="card-body">
        <nts-time-range-input v-model="workHours1" />
      </div>
    </div>

    <div v-if="$appContext.c3" class="card card-label">
      <!--A1_6_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS10_8" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>
      <!--A1_6_2-->
      <div class="card-body">
        <nts-time-range-input v-model="workHours2" />
      </div>
    </div>

    <!-- GoWork BackHome SwitchBox -->
    <div class="card card-label" v-if="$appContext.c14">
        <!-- A1_9_1 -->
        <div class="card-header uk-bg-accordion" style="align-items: center">
            <v-label class="border-0 pl-0 my-n3">
                {{'KAFS07_5' | i18n}}</v-label>
            <span class="badge badge-warning" style="height: 30%">必須</span>
        </div>
        <div class="card-body">
            <div style="width: 100%" id="isGoWorkSelect">
                <nts-switchbox v-for="(option, optionIndex) in isGoWorkResource" v-bind:key="optionIndex"
                    v-model="goWorkAtr"
                    v-bind:value="option.code">
                        {{option.text | i18n}}
                </nts-switchbox>
            </div>
            <div style="width: 100%" id="isBackHomeSelect">
                <nts-switchbox v-for="(option, optionIndex) in isBackHomeResource" v-bind:key="optionIndex"
                    v-model="backHomeAtr"
                    v-bind:value="option.code">
                        {{option.text | i18n}}
                </nts-switchbox>
            </div>
        </div>
    </div>    

    <!-- Break -->
    <div v-if="$appContext.c15" class="card card-label">
      <!--A1_7_1-->
      <div class="card-header uk-bg-accordion mt-2 mb-n2">
        <span>{{ "KAFS10_9" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>

      <!-- A1_7_3 -->
      <div
        v-for="(item, index) in breakTimes.slice(0, displayNumberBreakTime)"
        v-bind:key="index"
        :value="index"
      > 
        <!--A1_7_2-->      
        <div class="card-body">
          <div class="row mt-3">
            <div class="col-6">{{item.title}}</div>
          </div>
          <div class="card-body">
            <nts-time-range-input
              class="mb-1"
              v-model="item.valueHours"
            />
          </div>
        </div>
      </div>
      <!-- A1_7_4 -->
      <div v-if="isAddFrameBreakTime">
        <div class="text-center position-relative" style="height: 35px">
          <div class="position-absolute w-100">
            <hr />
          </div>
          <div class="position-absolute w-100 mt-1">
            <span
              v-on:click="addBreakHour"
              class="fas fa-2x fa-plus-circle"
              style="color: #6CC0E5"
            ></span>
          </div>
        </div>
        <div class="text-center">{{ "KAFS10_11" | i18n }}</div>
      </div>
    </div>

    <div class="card card-label">
      <div class="card-body">
        <!-- A1_8_1 -->
        <button
          type="button"
          class="btn btn-block btn-success btn-lg text-center"
          v-on:click="$appContext.toStep(2)"
        >
          {{ ($appContext.timeCalUse ? 'KAFS10_12' : 'KAFS10_30') | i18n}}
        </button>
      </div> 
    </div>

  </div>
</template>