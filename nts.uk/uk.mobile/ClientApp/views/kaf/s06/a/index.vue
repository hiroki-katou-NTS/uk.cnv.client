<template>
<div class="kafs06a">
    <div>
      <kafs00-a
        v-if="kaf000_A_Params != null"
        v-bind:params="kaf000_A_Params"
      />
    </div>
    <!--A2_1-->
    <div class="accordion mb-2" v-if="c1">
          <div class="card" v-if="true">
            <div class="card-header uk-bg-accordion">
              <button class="btn btn-link" type="button">
                  {{'KAFS06_1' | i18n}}
              </button>
            </div>
            <div class="collapse">
              <!--A2_2-->
              <table class="table mt-2" style="width: 97%; margin: auto; border: 1px solid #dee2e6;">
                  <thead>
                    <tr>
                      <!--A2_3-->
                      <th v-if="c2" scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_2' | i18n}}</th>
                      <!--A2_5-->
                      <th v-if="c3" scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_3' | i18n}}</th>
                      <!--A2_7-->
                      <th v-if="c4" scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_4' | i18n}}</th>
                      <!--A2_9-->
                      <th v-if="c5" scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_5' | i18n}}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr 
                      v-for="(item, index) in remainDays"
                      v-bind:key="index"
                      :value="index"
                    >
                      <td class="text-center" v-if="c2 && c2_1" style="padding-bottom: 0">{{formatTimeFromMinute(item.subVacaHourRemain)}}</td>
                      <td class="text-center" v-if="c2 && !c2_1" style="padding-bottom: 0">{{'KAFS06_40' | i18n([item.subHdRemain])}}</td>
                      <td class="text-center" v-if="c3" style="padding-bottom: 0">{{'KAFS06_40' | i18n([item.subVacaRemain])}}</td>
                      <td class="text-center" v-if="c4" style="padding-bottom: 0">{{'KAFS06_40' | i18n([item.yearRemain])}}</td>
                      <td class="text-center" v-if="c5" style="padding-bottom: 0">{{'KAFS06_40' | i18n([item.remainingHours])}}</td>
                    </tr>
                    <tr 
                      v-for="(item, index) in remainDays"
                      v-bind:key="index + 1"
                      :value="index"
                    >
                      <!-- A2_12 -->
                      <!-- <td style="border-top: none; padding-top: 5px;"><div class="text-center" v-if="c2 && c2_1" >{{formatTimeFromMinute(item.subVacaHourRemain)}}</div></td> -->
                      <td class="text-center" v-if="c2" style="border-top: none; padding-top: 5px;">{{''}}</td>
                      <td class="text-center" v-if="c3" style="border-top: none; padding-top: 5px;">{{''}}</td>
                      <!-- A2_13 -->
                      <td style="border-top: none; padding-top: 5px;"><div class="text-center" v-if="c4 && c4_1" >{{formatTimeFromMinute(item.yearHourRemain)}}</div></td>
                      <td class="text-center" v-if="c5" style="border-top: none; padding-top: 5px;">{{''}}</td>
                    </tr>
                  </tbody>
              </table>   
              <div v-if="c4" style="margin: 5px 15px; font-size: 80%">{{grantDays}}</div> 
            </div>
          </div>
    </div>

    <div v-if="!$valid || !isValidateAll" class="card bg-danger top-alert uk-text-danger topError">
              <button class="btn btn-link uk-text-danger">
                <i class="fa fa-exclamation-circle" aria-hidden="true" ></i>
                {{ 'KAFS07_1' | i18n }}
              </button>
    </div>
    <kafs00-b
        v-if="kaf000_B_Params != null"
        v-bind:params="kaf000_B_Params"
        v-on:kaf000BChangeDate="kaf000BChangeDate"
        v-on:kaf000BChangePrePost="kaf000BChangePrePost"
    />
    <div class="card card-label" v-if="true">
        <!-- A4_1 -->
        <div class="card-header uk-bg-accordion">
            <span>{{ "KAFS06_7" | i18n }}</span>
            <span class="badge badge-warning">必須</span>
        </div>
        <!-- A4_2 -->
        <div class="mt-3">
            <!-- A4_3 -->
            <nts-dropdown v-model="selectedValueHolidayType">
                <option v-for="(item, index) in dropdownList" :key="index" :value="item.code">
                    {{item.text}}
                </option>
            </nts-dropdown>

        </div>
    </div>

    <div v-if="true" class="card card-label">
      <!--A6_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ (c2_textResource ? 'KAFS06_12' : 'KAFS06_14') | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>

      <div class="card-body">
        <!--A6_2-->
        <span class="textSize uk-text-dark-gray">{{ "KAFS06_12" | i18n }}</span>
        <button
          v-bind:disabled="false"
          v-on:click="openKDL002('worktype')"
          type="button"
          class="btn btn-selection mt-2 mb-2"
        >
        <!--A6_3-->
          <span class="badge badge-secondary">{{workType.code}}</span>
          <span>{{workType.name}}</span>
        </button>
        <!--A6_7-->
        <div v-if="c9">
            <nts-checkbox v-model="checkBoxC7" v-bind:value="true">{{'KAFS06_54' | i18n}}</nts-checkbox>
        </div>
        <!--A6_4-->
        <span v-if="c9" class="textSize uk-text-dark-gray">{{ "KAFS06_13" | i18n }}</span>
        <!--A6_5-->
        <button
          type="button"
          v-on:click="openKDL002('worktime')"
          v-if="c9"
          v-bind:disabled="false"
          class="btn btn-selection mt-2 mb-2"
        >
          <span class="badge badge-secondary">{{workTime.code}}</span>
          <span>{{workTime.name}}</span>
          <!--A6_6-->
          <span class="textSize d-block mt-1">{{workTime.time}}</span>
        </button>
      </div>
    </div>




    <div v-if="c9" class="card card-label">
      <!-- A7_1 -->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS06_15" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>
      <!-- A7_2 -->
      <div class="card-body">
        <nts-time-range-input v-model="workHours1" v-bind:disabled="!c11" />
      </div>
    </div>
    <div v-if="c10" class="card card-label">
      <!-- A8_1 -->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS06_16" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>
      <!-- A8_2 -->
      <div class="card-body">
        <nts-time-range-input v-model="workHours2" v-bind:disabled="!c11" />
      </div>
    </div>

    <div v-if="true" class="card card-label">
        <!--A9_1-->
        <div v-if="c12" class="card-header uk-bg-accordion">
            <span>{{ "KAFS06_17" | i18n }}</span>
            <span class="badge badge-warning">必須</span>
        </div>
        <div v-if="c12" class="row px-2">
            <!--A9_2-->
            <span
                class="border-customer col p-2 m-2 text-center">
                {{"KAFS06_18" | i18n([A9_2])}}
            </span>

            <!--A9_3-->
            <span 
                class="border-customer col p-2 m-2 text-center">
                {{"KAFS06_19" | i18n([A9_3])}}
            </span>


        </div>
        <div v-if="c13">
            <!--A9_5-->
            <div class="row">
                <div class="float_left col text-left textSize">{{'Com_ExsessHoliday' | i18n}}</div>
                <div class="float_right col text-right textSize colorHours">{{'KAFS06_22' | i18n([A9_5])}}</div>
            </div>
            <!--A9_4-->
            <div class="card-body">
                <nts-time-editor
                    v-model="inputA9_5"
                    name="'Com_ExsessHoliday'"
                    showTitle="false"
                    v-bind:disabled="false"
                    time-input-type="time-duration"
                />
            </div>
        </div>

        <div v-if="c14">
            <!--A9_7-->
            <div class="row">
                <div class="float_left col text-left textSize">{{'KAFS06_20' | i18n}}</div>
                <div class="float_right col text-right textSize colorHours">{{'KAFS06_22' | i18n([A9_7])}}</div>
            </div>
            <!--A9_6-->
            <div class="card-body">
                <nts-time-editor
                    v-model="inputA9_7"
                    name="'KAFS06_20'"
                    showTitle="false"
                    v-bind:disabled="false"
                    time-input-type="time-duration"
                />
            </div>
        </div>

        <div v-if="c15">
            <!--A9_9-->
            <div class="row">
                <div class="float_left col text-left textSize">{{'KAFS06_21' | i18n}}</div>
                <div class="float_right col text-right textSize colorHours">{{'KAFS06_22' | i18n([A9_9])}}</div>
            </div>
            <!-- A9_14 -->
            <div class="row" style="margin: 0 15px; font-size: 80%">{{grantDays}}</div> 
            <!--A9_8-->
            <div class="card-body">
                <nts-time-editor
                    v-model="inputA9_9"
                    name="'KAFS06_21'"
                    showTitle="false"
                    v-bind:disabled="false"
                    time-input-type="time-duration"
                />
            </div>
        </div>


        <div v-if="c16">
            <!--A9_11-->
            <div class="row">
                <div class="float_left col text-left textSize">{{'Com_ChildNurseHoliday' | i18n}}</div>
                <div class="float_right col text-right textSize colorHours">{{'KAFS06_22' | i18n([A9_11])}}</div>
            </div>
            <!--A9_10-->
            <div class="card-body">
                <nts-time-editor
                    v-model="inputA9_11"
                    name="'Com_ChildNurseHoliday'"
                    showTitle="false"
                    v-bind:disabled="false"
                    time-input-type="time-duration"
                />
            </div>
        </div>


        <div v-if="c17">
            <!--A9_13-->
            <div class="row">
                <div class="float_left col text-left textSize">{{'Com_CareHoliday' | i18n}}</div>
                <div class="float_right col text-right textSize colorHours">{{'KAFS06_22' | i18n([A9_13])}}</div>
            </div>
            <!--A9_12-->
            <div class="card-body">
                <nts-time-editor
                    v-model="inputA9_13"
                    name="'Com_CareHoliday'"
                    showTitle="false"
                    v-bind:disabled="false"
                    time-input-type="time-duration"
                />
            </div>
        </div>
        
    </div>


    <div v-if="true" class="card card-label">
      <!-- A10_1 -->
      <div v-if="c18" class="card-header uk-bg-accordion">
        <span>{{ "KAFS06_23" | i18n }}</span>
        <span class="badge badge-warning">必須</span>
      </div>
      <div>
        <!--A10_3-->
        <div v-if="c18" class="row">
            <div class="float_left col text-left textSize">{{'KAFS06_24' | i18n}}</div>
            <div class="float_right col text-right textSize colorHours">{{'KAFS06_39' | i18n(A10_3)}}</div>
        </div>
        <!--A10_2-->
        <div v-if="c18" class="mt-1">
            <nts-dropdown v-model="selectedRelationship">
                <option v-for="(item, index) in dropdownRelationship" :key="index" :value="item.code">
                    {{item.text}}
                </option>
            </nts-dropdown>

        </div>
        <!--A10_4-->
        <div v-if="c19">
            <nts-checkbox v-model="mournerFlag" v-bind:value="true">{{'KAFS06_25' | i18n}}</nts-checkbox>
        </div>
      </div>

      <!--A10_5-->
      <div v-if="c20">
        <div class="row">
            <div class="float_left col text-left textSize">{{'KAFS06_26' | i18n}}</div>
        </div>
        <div class="mt-1">
            <nts-text-editor
              v-model="relationshipReason"
              name='relationshipReason' 
              showTitle="false"
              v-bind:placeholder="KAFS06_27"
            />

        </div>
      </div>
      
    </div>
    
    <div v-if="c21" class="card card-label mb-3">
      <!--A11_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS06_28" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>
      <!--A11_2-->
      <div class="px-3 mt-3">
        <table class="table">
          <thead>
            <tr>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_29' | i18n}}</th>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_30' | i18n}}</th>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_31' | i18n}}</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(item, index) in linkWithVacation"
              v-bind:key="index"
              :value="index"
            > 
              <td class="text-center px-0">{{$dt(new Date(item.outbreakDay), 'YY/MM/DD(dd)')}}</td>
              <td class="text-center px-0">{{$dt(new Date(item.dateOfUse), 'YY/MM/DD(dd)')}}</td>
              <td class="text-center px-0">{{item.dayNumberUsed}}</td>
            </tr>
          </tbody>
        </table>
        <!--A11_9-->
        <div align="center">
            <button 
            type="button" 
            v-bind:disabled="!(c23 || !modeNew)"
            v-on:click="openKDLS36()"
            class="shadow-none btn rounded-pill btn-info">
              {{'KAFS06_32' | i18n}}
              <i class="fas fa-angle-double-right" aria-hidden="true"></i>
            </button>
        </div>
      </div>

      
    </div>






    <div v-if="c22" class="card card-label mb-3">
      <!--A12_1-->
      <div class="card-header uk-bg-accordion">
        <span>{{ "KAFS06_33" | i18n }}</span>
        <span class="badge badge-info">任意</span>
      </div>
      <!--A12_2-->
      <div class="px-3 mt-3">
        <table class="table">
          <thead>
            <tr>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_34' | i18n}}</th>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_35' | i18n}}</th>
              <th scope="col" class="bg-grey-200 px-1 text-center">{{'KAFS06_31' | i18n}}</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(item, index) in linkWithDraw"
              v-bind:key="index"
              :value="index"
            >
              <td class="text-center px-0">{{$dt(new Date(item.outbreakDay), 'YY/MM/DD(dd)')}}</td>
              <td class="text-center px-0">{{$dt(new Date(item.dateOfUse), 'YY/MM/DD(dd)')}}</td>
              <td class="text-center px-0">{{item.dayNumberUsed}}</td>
            </tr>
            
          </tbody>
        </table>
        <!--A12_9-->
        <div align="center">
            <button 
            type="button" 
            v-bind:disabled="!(c23 || !modeNew)"
            v-on:click="openKDLS35()"
            class="shadow-none btn rounded-pill btn-info">
              {{'KAFS06_36' | i18n}}
              <i class="fas fa-angle-double-right" aria-hidden="true"></i>
            </button>
        </div>
      </div>

      
    </div>




    <div>
      <kafs00-c
        v-if="kaf000_C_Params != null"
        v-bind:params="kaf000_C_Params"
        v-on:kaf000CChangeReasonCD="kaf000CChangeReasonCD"
        v-on:kaf000CChangeAppReason="kaf000CChangeAppReason"
      />
    </div>

    <div class="card card-label py-3">
         <button
            type="button"
            class="btn btn-block btn-primary btn-lg"
            v-on:click="register">
            {{(modeNew ? 'KAFS06_37' : 'KAFS06_38') | i18n}}
          </button>
    </div>

</div>
</template>