<template>
  <div class="kdl001">

    <div class="modal-header rounded-0 d-block p-0">
      <div class="uk-bg-teal p-2">
        <h4 class="modal-title text-white">
          <i class="fas fa-angle-double-left mr-1" v-on:click="back()"></i>
          <span>{{ 'KDL012_12' | i18n }}</span>
        </h4>
      </div>
      <div class="uk-bg-headline p-2 pb-1">
        <label>{{ 'KDL012_10' | i18n }}</label>
        <nts-text-search class="bg-grey-100" v-on:search="searchEvent" placeholder="KDL012_11" />
      </div>
    </div>

    <ul class="list-group list-group-single-select mb-5">
      <li
        class="list-group-item"
        v-for="(item, i) of listTaskDisplay"
        v-bind:key="i"
        v-bind:class="{ selected: selectedTask == item }"
        v-on:click="chooseTask(item)"
        v-bind:id="item.code"
      >
        <div class="row">
          <div class="col-11">{{item.taskName}}</div>
        </div>
      </li>
    </ul>
  </div>
</template>