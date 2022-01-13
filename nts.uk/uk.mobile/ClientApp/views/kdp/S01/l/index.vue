<template>
  <div cl>
    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal p-2">
        <h4 class="modal-title text-white">
          <span>{{ "KDPS01_15" | i18n }}</span>
        </h4>
      </div>
    </div>
    <!-- L1: 枠名 -->
    <label class="mb-2"
      >{{ "KDPS01_70" | i18n(frameName) }}
    </label>

    <!-- L2_1: 検索作業名 -->
    <div class="row">
        <div class="col-8 pl-0 pr-0">
            <nts-text-editor
            id="taskNameCd"
            ref="L2_1"
            v-focus
            v-bind:tabindex="1"
            v-model="taskNameCd"
            v-bind:show-title="true"
            v-bind:placeholder="$i18n('KDPS01_75')"
            v-bind:columns="{ title: '', input: '' }"
            @keyup.enter.native="onClickSearch" />
        </div>
        <div class="col-4 pl-1 pr-0">
            <!-- L2_2: 検索ボタン -->
            <button
            type="button"
            v-on:click="onClickSearch"
            v-bind:tabindex="2"
            class="btn btn-secondary btn-sm mt-1 ml-1"
            >
            {{ "KDPS01_72" | i18n }}
            </button>

            <!-- L2_3: 解除ボタン -->
            <button
            type="button"
            v-on:click="onClickCancel"
            v-bind:tabindex="3"
            class="btn btn-secondary btn-sm mt-1 ml-1"
            >
            {{ "KDPS01_73" | i18n }}
            </button>
        </div>
    </div>
    <div class="row mb-2 mt-5 pb-2 height-btn">
        <!-- L10_1: 前へ -->
        <div class="col-6 text-left uk-bg-silver pl-0">
            <a class="uk-text-blue" v-show="checkBack" v-on:click="onClickBack">
                <i class="fas fa-arrow-circle-left"></i>
            </a>
        </div> 

        <!-- L10_2: 次へ -->
        <div class="col-6 text-right uk-bg-silver pr-0">
            <a class="uk-text-blue" v-show="checkNext" v-on:click="onClickNext">
            <i class="fas fa-arrow-circle-right"></i>
            </a>
        </div> 
    </div>
    <!-- L3_1 ~ L3_6: 作業内容 -->
    <button
      v-for="button in tasks"
      v-bind:key="button.code"
      v-bind:tabindex="4"
      type="button"
      class=" mb-2 btn btn-success btn-block btn-lg"
      v-click:500="() => onClickTask(button.code)"
    >
      {{ button.displayInfo.taskName | i18n }}
    </button>

    <!-- L20: 戻る -->
    <button
      ref="functionBtn"
      type="button"
      v-show="checkReturn"
      v-on:click="onClickReturn"
      v-bind:tabindex="10"
      class="btn btn-secondary btn-block mt-2"
    >
      {{ "KDPS01_74" | i18n }}
    </button>
  </div>
</template>
