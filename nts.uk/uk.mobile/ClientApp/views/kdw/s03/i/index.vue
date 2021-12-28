<template>
  <div class="kdws03i p-0">
    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal py-2">
        <h4 class="col-4 modal-title text-white" v-on:click="closeDialog()">
          <i class="fas fa-angle-left mr-1"></i>
          <span>{{ "KDWS03_75" | i18n }}</span>
        </h4>
      </div>

      <div class="uk-bg-headline p-2 pb-1">
        <label>{{ 'KDWS03_12' | i18n }}</label>
        <nts-text-search class="bg-grey-100" placeholder="KDWS03_82" v-model="searchText" v-on:search="searchList"/>
      </div>
    </div>
    
    <div>
      <ul class="list-group list-group-single-select">
        <li class="list-group-item " v-for="(item, k) of data" v-bind:key="k" 
        :class="(item.dailyPerformanceFormatCode == selectedCode  || (selectedCode == '' && item.setFormatToDefault)) ? 'active' : ''"
         v-on:click="editFormat(item.dailyPerformanceFormatCode)">
          <div class="text-truncate mr-3" v-if="item.dailyPerformanceFormatCode != ''"><span>{{item.dailyPerformanceFormatCode}} &nbsp;&nbsp;&nbsp; {{item.dailyPerformanceFormatName}}</span></div>
          <!-- <div class="text-truncate mr-3" v-if="item.dailyPerformanceFormatCode == ''"><span >{{ 'CDLS03_5' | i18n }}</span></div>  -->
        </li>
      </ul>
    </div>
  </div>
</template>
