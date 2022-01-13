<template>
<div class="kdws03g p-0">
    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal py-2">
          <h4 class="col-4 modal-title text-white" v-on:click="$close">
              <i class="fas fa-angle-left mr-1"></i>
              <span>{{ 'KDWS03_75' | i18n }}</span>
          </h4>
      </div>
    </div>
    <div class="uk-text-quote py-2 pl-1 uk-bg-white-smoke">
      <!-- G1_2: 対象社員名 -->
      <div class="col-11 pr-2 text-truncate"><span>{{ empName }}</span></div>
    </div>

    <div class="accordion accordion-mn3">
      <!-- G2_1: 休暇残数 -->
      <div class="card border border-left-0 border-right-0" v-if="remainNumber.manageYear || remainNumber.manageReserve 
        || remainNumber.manageCompensatory || remainNumber.manageSubStitute" v-bind:class="{'show':  params.remainDisplay}">
        <div class="card-header uk-bg-accordion">
           <button class="btn btn-link" type="button">{{'KDWS03_70' | i18n}}</button>
        </div>
        <div class="collapse">
          <div class="card-body">
              <div class="row" v-if="remainNumber.manageYear">
                <!-- G3_1: 年休残 -->
                <div class="col-7">{{'KDWS03_47' | i18n}}</div>
                <!-- G3_2: 年休残数 -->
                <div class="col-5 text-right">
                  <div v-if="remainNumber.yearRemainTime">
                    {{'KDWS03_80' | i18n([remainNumber.yearRemain, formathmm(remainNumber.yearRemainTime)])}}   
                  </div>
                  <div v-else>
                    {{'KDWS03_52' | i18n(remainNumber.yearRemain)}}
                  </div>
                </div>
              </div>
              <div class="row" v-if="remainNumber.manageYear" v-bind:class="{'pt-3': remainNumber.manageYear}">
                <!-- G4_1: 次回付与 -->
                <div class="col-5">{{'KDWS03_51' | i18n}}</div>
                <!-- G4_2: 次回付与日 -->
                <div class="col-7 text-right">
                  <div v-if="remainNumber.nextGrantDate">
                    {{nextGrantDateStr}}    
                  </div>
                  <div v-else>
                    {{'KDWS03_79' | i18n}}
                  </div>
                </div>
              </div>
              <div class="row" v-if="remainNumber.manageReserve && remainNumber.manageYear" v-bind:class="{'pt-3': remainNumber.manageYear}">
                <!-- G5_1: 積立年休残 -->
                <div class="col-9">{{'KDWS03_48' | i18n}}</div>
                <!-- G5_2: 積立年休残数 -->
                <div class="col-3 text-right">{{'KDWS03_52' | i18n(remainNumber.reserveRemain)}}</div>
              </div>
              <div class="row" v-if="remainNumber.manageCompensatory" v-bind:class="{'pt-3': remainNumber.manageReserve || remainNumber.manageYear}">
                <!-- G6_1: 代休残 -->
                <div class="col-7">{{'KDWS03_49' | i18n}}</div>
                <!-- G6_2: 代休残数 -->
                <div class="col-5 text-right">
                  <div v-if="remainNumber.manageTimeOff">
                    {{getFormatTime(remainNumber.compensatoryRemainTime)}}   
                  </div>
                  <div v-else>
                    {{'KDWS03_52' | i18n(remainNumber.compensatoryRemain)}}
                  </div>
                </div>
              </div>
              <div class="row" v-if="remainNumber.manageSubStitute" 
                v-bind:class="{'pt-3': remainNumber.manageCompensatory || remainNumber.manageReserve || remainNumber.manageYear}">
                <!-- G7_1: 振休残 -->
                <div class="col-9">{{'KDWS03_50' | i18n}}</div>
                <!-- G7_2: 振休残数 -->
                <div class="col-3 text-right">{{'KDWS03_52' | i18n(remainNumber.substituteRemain)}}</div>
              </div>
              <div class="row pt-3" v-if="remainNumber.manageChildCare">
                <!-- G12_1: 振休残 -->
                <div class="col-7">{{'KDWS03_77' | i18n}}</div>
                <!-- G12_2: 振休残数 -->
                <div class="col-5 text-right">
                  <div v-if="remainNumber.childCareTime">
                    {{'KDWS03_80' | i18n([remainNumber.childCareDay, formathmm(remainNumber.childCareTime)])}}   
                  </div>
                  <div v-else>
                    {{'KDWS03_52' | i18n(remainNumber.childCareDay)}}
                  </div>
                </div>
              </div>
              <div class="row pt-3" v-if="remainNumber.manageLongTermCare">
                <!-- G13_1: 振休残 -->
                <div class="col-7">{{'KDWS03_78' | i18n}}</div>
                <!-- G13_2: 振休残数 -->
                <div class="col-5 text-right">
                  <div v-if="remainNumber.longTermCareTime">
                    {{'KDWS03_80' | i18n([remainNumber.longTermCareDay, formathmm(remainNumber.longTermCareTime)])}}   
                  </div>
                  <div v-else>
                    {{'KDWS03_52' | i18n(remainNumber.longTermCareDay)}}
                  </div>
                </div>
              </div>
          </div>
        </div>
      </div>
      <!-- G2_2: 時間外超過時間 -->
      <div class="card border border-left-0 border-right-0" v-if="time36Display" v-bind:class="{'show':  !params.remainDisplay}">
        <div class="card-header uk-bg-accordion">
          <button class="btn btn-link" type="button">{{'KDWS03_53' | i18n}}</button>
        </div>

        <div class="collapse" style="text-align: center">
          <table class="table text-center">
            <thead>
              <tr class="uk-bg-lighten-gray">
                <!-- G8-1: 超過時間	-->
                <th class="pl-0 pr-0 text-center" colspan="2">{{'KDWS03_54' | i18n}}</th>
                <!-- G10-1: 超過回数	-->
                <th class="pl-0 pr-0 text-center" >{{'KDWS03_56' | i18n}}</th>            
              </tr>
            </thead>
            <tbody>
              <tr>
                <!-- G8-2: 超過時間	-->
                <td class="pl-0 pr-0 text-center" v-bind:class="time36CssAgree">{{ time36.time36 | timedr }}</td>
                <!-- G9-2: 超過上限時間	-->
                <td class="pl-0 pr-0 text-center" v-bind:class="time36CssAgree">{{ time36.maxTime36 | timedr }}</td>
                <!-- G10-2: 超過回数-->
                <td id="agree-excess" class="pl-0 pr-0 text-center"  v-bind:class="time36CssFrequency">{{'KDWS03_57' | i18n(time36.excessNumber)}}</td>          
              </tr>
            </tbody>
          </table>           
        </div>  
      </div>
    </div>
</div>
</template>