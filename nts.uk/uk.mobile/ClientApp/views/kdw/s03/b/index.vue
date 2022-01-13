<template>
<div class="kdws03b pt-0">
    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal py-2">
          <h4 class="col-4 modal-title text-white" v-on:click="$close">
              <i class="fas fa-angle-left mr-1"></i>
              <span>{{ 'KDWS03_75' | i18n }}</span>
          </h4>
      </div>
    </div>
    <div class="row uk-text-quote py-2 pl-2 uk-bg-white-smoke">
      <div class="col-7 pl-0 pr-2 text-truncate"><span>{{ params.employeeName }}</span></div>
      <div class="col-5 pl-0 pr-0"><span>{{ params.date  | date('YYYY年MM月DD日') }}</span></div>
    </div>

  <div class="row" v-if="params.rowData.state">
    <div class="accordion w-100">
      <div class="card border-0 pl-0">
        <div class="card-header pl-0 pr-0 uk-bg-light-coral">
          <button class="btn btn-link" type="button">{{'KDWS03_35' | i18n}}</button>
        </div>
        <div class="collapse uk-bg-light-coral" >
          <div class="card-body pt-0 pb-2" v-html="getLockContent()"></div>
        </div>
      </div>
    </div>
  </div>
  <div class="row" v-if="isDisplayError">
    <div class="accordion w-100" v-on:click="openDScreen()">
      <div class="card border-0 pl-0 custom-show">
        <div class="card-header pl-0 pr-0 uk-bg-light-coral db-caret-right">
          <button class="btn btn-link" type="button">{{'KDWS03_36' | i18n}}</button>
        </div>
      </div>
    </div>
  </div>
  <div v-for="(rowData, key) in screenData[0]" v-bind:key="key">
    <div class="row">
      <div class="col-12">
        <div class="row pl-2 mb-1">{{ getItemText(key) }}</div>
        <div class="row mb-1">
          <div class="col-2 p-1 text-right">
            <button class="border-0 bg-transparent" v-on:click="openDScreen(key)"><i :class="`${getIcon(key) }`"></i></button>
          </div>
          <!-- InputStringCode && ButtonDialog -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputStringCode || getItemType(key)==itemType.ButtonDialog">
            <template v-if="isAvaiableDialog(key)">
              <div class="row">
                <div class="w-100">
                  <div class="col-9 d-inline-block align-middle">
                    <h5 class="mb-0"><span class="badge w-100 text-truncate text-left">{{ getItemDialogName(key) }}</span></h5>
                  </div>
                  <div class="d-inline-block">
                    <button type="button" class="btn btn-secondary" v-bind:disabled="getItemLock(key)" v-on:click="openDialog(key)">{{'KDWS03_71' | i18n}}</button>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <nts-dropdown v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key"
                :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)">
                <option v-for="(item, k) in getRowComboBox(key)" v-bind:key="k" :value="item.code">
                  {{item.code}} &nbsp;&nbsp;&nbsp;  {{item.name}}
                </option>
              </nts-dropdown>
            </template>  
          </div>
          <!-- InputNumber -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputNumericValue" >
            <nts-number-editor class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)"/>
          </div>
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputNumber && getCheckboxType(key) != 'Checkbox'">
            <nts-number-editor class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)"/>
          </div>

          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputNumber && getCheckboxType(key) == 'Checkbox'">
            <nts-checkbox class="mb-3" v-model="screenData[0][key]" v-bind:value="1" v-bind:disabled="getItemLock(key)"/>
          </div>

          <!-- InputMoney -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputMoney">  
            <nts-number-editor class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:icons="{ after: '￥' }" v-bind:disabled="getItemLock(key)"/>
          </div>
          <!-- ComboBox -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.ComboBox">
            <nts-dropdown v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key"
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)">
              <option v-for="(item, k) in getRowComboBox(key)" v-bind:key="k" :value="item.code">
                {{item.code}} &nbsp;&nbsp;&nbsp;  {{item.name}}
              </option>
            </nts-dropdown>
          </div>
          <!-- Time -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.Time">
            <nts-time-editor class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" time-input-type="time-duration" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)"/>
          </div>
          <!-- TimeWithDay -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.TimeWithDay">
            <nts-time-editor class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" time-input-type="time-with-day" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)"/>
          </div>
          <!-- InputStringChar -->
          <div class="col-9 pl-0 pr-0" v-if="getItemType(key)==itemType.InputStringChar">
            <nts-text-area class="mb-3" v-model="screenData[0][key]" v-bind:record-name="key" v-bind:key="key" 
              :class-input="`${ getBackGroundColor(key) }`" v-bind:disabled="getItemLock(key)"/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="row mt-4">
    <div class="col-3"></div>
    <div>
      <nts-checkbox v-show="params.paramData.showPrincipal" v-bind:disabled="params.rowData.confirmDisable" 
        v-model="checked1s" v-bind:value="2">{{'KDWS03_37' | i18n}} </nts-checkbox>
    </div>
  </div>
  <div class="card invisible">
    <div class="card-body">
      Hidden Content
    </div>
  </div>
  <div class="fixed-bottom text-center" v-if="canRegister">
    <button type="button" class="btn btn-success btn-block shadow-none" v-bind:disabled="!$valid" v-on:click="register()">{{'KDWS03_38' | i18n}}</button>
  </div>
  
</div>
</template>