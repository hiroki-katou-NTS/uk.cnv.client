<template>
  <div class="cmms45shrcomponentsapp15">
    <!-- B1_4 -->
    <div class="field-set">
      <!-- B1_4_1 -->
      <div class="card card-label">
        <div class="card-header uk-bg-accordion">
          <span>{{ "KAFS20_23" | i18n }}</span>
          <span class="badge badge-warning"></span>
        </div>
      </div>
      <!-- B1_4_2 -->
      <div class="accordion py-2">
        <div class="card">
          <div class="card-header">
            <button class="btn btn-link" type="button">
              <!-- tiêu đề của collapse -->
              <span>{{ settingItems.name }}</span>
            </button>
          </div>
          <div class="collapse">
            <div class="card-body">
              <!-- nội dung của collapse -->
              {{ settingItems.note}}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- B1_5 -->
    <div class="field-set">
      <!-- B1_5_1 -->
      <div class="card card-label">
        <div class="card-header uk-bg-accordion">
          <span>{{ "KAFS20_24" | i18n }}</span>
          <span class="badge badge-warning"></span>
        </div>
      </div>
      <!-- B1_5_2 -->
      <div v-if="!!optionalItemApplication">
        <div
          v-for="(item, index) in optionalItemApplication"
          v-bind:key="index"
        >
          <div class="accordion position-relative">
            <div class="card">
              <div class="card-header">
                <button class="btn btn-link" type="button">
                  {{ item.optionalItemName }}
                </button>
              </div>
              <div class="collapse">
                <div class="card-body">
                  <span style="display: block;">{{item.description}}</span>
                  <span v-if="(item.lowerCheck || item.upperCheck || item.unit || item.optionalItemAtr == 0) && !item.inputCheckbox">
                    <span v-if="item.lowerCheck || item.upperCheck || item.unit || item.optionalItemAtr == 0">
                      {{ 'KAF020_25' | i18n }}
                    </span>
                    <span v-if="item.lowerCheck || item.upperCheck">
                      {{ 'KAF020_26' | i18n}}
                    </span>
                    <span v-if="item.lowerCheck">
                      <span v-if="item.optionalItemAtr == 0 && item.timeLower != null">
                        {{ item.timeLower | timedr }}
                      </span>
                      <span v-if="item.optionalItemAtr == 1">
                        {{ item.numberLower }}
                      </span>
                      <span v-if="item.optionalItemAtr == 2">
                        {{ item.amountLower }}
                      </span>
                    </span>
                    <span v-if="item.lowerCheck || item.upperCheck">
                      {{'KAF020_27' | i18n}}
                    </span>
                    <span v-if="item.upperCheck">
                      <span v-if="item.optionalItemAtr == 0 && item.timeUpper != null">
                        {{ item.timeUpper | timedr }}
                      </span>
                      <span v-if="item.optionalItemAtr == 1">
                        {{ item.numberUpper }}
                      </span>
                      <span v-if="item.optionalItemAtr == 2">
                        {{ item.amountUpper }}
                      </span>
                    </span>
                    <span v-if="(item.lowerCheck || item.upperCheck) && (item.unit || item.optionalItemAtr == 0)">
                      {{'、'}}
                    </span>
                    <span v-if="item.unit || item.optionalItemAtr == 0">
                      <span>
                        {{item.inputUnitOfItem}}
                      </span>
                      <span v-if="item.optionalItemAtr == 0">
                        {{'KAF020_32' | i18n}}
                      </span>
                      <span v-else>
                        {{item.unit}}
                      </span>
                      {{'KAF020_28' | i18n}}
                    </span>
                    <span v-if="item.lowerCheck || item.upperCheck || item.unit || item.optionalItemAtr == 0">
                      {{'KAF020_29' | i18n}}
                    </span>
                  </span>
                </div>
              </div>
            </div>
          </div>
          <!-- A2_6_4_1 -->
          <div class="position-relative mt-2">
            <label class="pl-4" v-if="item.time != null">{{item.time | timewd}}{{item.unit}}</label>
            <label class="pl-4" v-if="item.number != null && item.inputCheckbox">
              <nts-checkbox
                      style="display: inline-block;"
                      v-bind:class="'item-' + item.optionalItemNo"
                      v-model="item.number"
                      v-bind:value="1"
                      v-bind:disable="true"
              />
            </label>
            <label class="pl-4" v-if="item.number != null && !item.inputCheckbox">{{ item.number }}{{item.unit}}</label>
            <label class="pl-4" v-if="item.amount != null">{{ item.amount }}{{item.unit}}</label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>