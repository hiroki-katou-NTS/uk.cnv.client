<template>
  <div class="kafs05step1">
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
      class="card bg-danger top-alert uk-text-danger topError"
    >
      <button class="btn btn-link uk-text-danger">
        <i class="fa fa-exclamation-circle" aria-hidden="true"></i>
        {{ ($appContext.isMsg_1556 ? "Msg_1556" : "KAFS07_1") | i18n($appContext.isMsg_1556 ? $appContext.date : '') }}
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
    <!-- A1_B3 -->
    <div v-if="$appContext.c3" class="card card-label">
      <!--A1_B3_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS05_63" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>

      <div class="card-body">
        <!--A1_B3_2-->
        <span class="textSize uk-text-dark-gray">{{ "KAFS05_64" | i18n }}</span>
        <button
          v-on:click="$appContext.openKDL002('worktype')"
          type="button"
          class="btn btn-selection mt-2 mb-2"
        >
          <span class="badge badge-secondary">{{
            workInfo.workType.code
          }}</span>
          <span>{{ workInfo.workType.name }}</span>
        </button>
        <!--A1_B3_4-->
        <span class="textSize uk-text-dark-gray">{{ "KAFS05_65" | i18n }}</span>
        <button
          type="button"
          v-on:click="$appContext.openKDL002('worktime')"
          class="btn btn-selection mt-2 mb-2"
        >
          <span class="badge badge-secondary">{{
            workInfo.workTime.code
          }}</span>
          <span>{{ workInfo.workTime.name }}</span>
        </button>
      </div>
    </div>
    <!-- Work hours A1_B4-->
    <div v-if="$appContext.c3" class="card card-label">
      <!-- A1_B4_1 -->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS05_66" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>
      <!-- A1_B4_2 -->
      <div class="card-body">
        <nts-time-range-input v-model="workHours1" />
      </div>
    </div>
    <!--A1_B5-->
    <div v-if="$appContext.c3_1" class="card card-label">
      <!--A1_B5_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS05_67" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>
      <!--A1_B5_2-->
      <div class="card-body">
        <nts-time-range-input v-model="workHours2" />
      </div>
    </div>

    <!-- Break A1_B6-->
    <div v-if="$appContext.c3" class="card card-label">
      <!--A1_B6_1-->
      <div class="card-header uk-bg-accordion mt-2 mb-n2">
        <span>{{ "KAFS05_68" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>

      <!-- A1_B6_2 -->
      <div
        v-for="(item, index) in breakTimes.slice(0, displayNumberBreakTime)"
        v-bind:key="index"
        :value="index"
      > 
        <!--A1_B6_2_N1-->      
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
      <!-- A1_B6_3 -->
      <div v-if="isAddFrameBreakTime">
        <div class="text-center position-relative" style="height: 35px">
          <div class="position-absolute w-100">
            <hr />
          </div>
          <div class="position-absolute w-100 mt-1">
            <span
              v-on:click="addBreakHour"
              class="fas fa-2x fa-plus-circle"
              style="color: #33b5e5"
            ></span>
          </div>
        </div>
        <div class="text-center">{{ "KAFS05_84" | i18n }}</div>
      </div>
    </div>
    <div class="card card-label">
      <div class="card-body">
        <!-- A1_C1_1 -->
        <button
          type="button"
          class="btn btn-block btn-success btn-lg text-center"
          v-on:click="$appContext.toStep(2)"
        >
          {{ ($appContext.c3 ? 'KAFS05_16' : 'KAFS05_17') | i18n}}
        </button>
      </div> 

    </div>
  </div>
</template>