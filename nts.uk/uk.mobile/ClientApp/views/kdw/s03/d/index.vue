<template>
<div class="kdws03d py-0">
    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal py-2">
          <h4 class="col-4 modal-title text-white" v-on:click="$close">
              <i class="fas fa-angle-left mr-1"></i>
              <span>{{ 'KDWS03_75' | i18n }}</span>
          </h4>
      </div>
    </div>
    <div class="row uk-text-quote py-2 uk-bg-white-smoke">
      <div class="text-truncate pl-2"><span>{{ params.employeeName }}</span></div>
    </div>

  <!-- D3_1: エラー表示 -->
  <div class="row uk-bg-light-coral py-2 pl-2">
    {{'KDWS03_40' | i18n}}    
  </div>  
  <div class="row">
    <!-- D3_1: エラー一覧 -->
  <table class="table table-bordered border-left-0 border-right-0">
    <thead>
      <tr class="uk-bg-green">
        <!-- D3_2: 日付（ヘッダ）-->
        <th class="pl-0 pr-0 text-center">{{'KDWS03_41' | i18n}}</th>
         <!-- D3_8: エラー区分（ヘッダ)-->
        <th class="pl-0 pr-0 text-center">{{'KDWS03_81' | i18n}}</th>
        <!-- D3_3: コード（ヘッダ）-->
        <th class="pl-0 pr-0 text-center">{{'KDWS03_42' | i18n}}</th>
        <!-- D3_4:エラー内容（ヘッダ）-->
        <th>{{'KDWS03_43' | i18n}}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(rowData, index) in rowDatas" v-bind:key="index" v-on:click="openDialogE(rowData)">
        <!-- D3_5: 日付-->
        <td class="pl-0 pr-0 text-center">{{ rowData.date | date('DD日(dd)') }}</td>
        <!-- D3_9: 日付-->
        <td class="pl-0 pr-0 text-center">{{ rowData.type }}</td>
        <!-- D3_6: 日付-->
        <td class="pl-0 pr-0 text-center">{{ rowData.code }}</td>
        <td class="pl-0 pr-0">
          <div class="w-100">
            <!-- D3_7: 日付-->
            <div class="col-11 pl-2 pr-2 d-inline-block text-truncate">{{ rowData.name }}</div>
            <div class="col-1 pl-0 pr-0 ml-n2 d-inline-block fas fa-angle-right text-right align-text-top"></div>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
  </div>
</div>
</template>