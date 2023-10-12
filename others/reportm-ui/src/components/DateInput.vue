<template>
  <div class="report-form-row">
    <label class="report-form-label" for="argument.name">{{label}}</label>
    <Datepicker vertical :name="label" v-model="date" @update:modelValue="updateDate"/>
  </div>
</template>

<script setup>
import Datepicker from '@vuepic/vue-datepicker';
import {defineProps, defineEmits, ref} from "vue";

defineProps({label: String, value: Date})
const emits = defineEmits(['update:value']);

const date = ref()

function updateDate(newDate){
  if(!newDate) return
  date.value = newDate
  emits('update:value', newDate.getTime())
}

</script>

<style lang="scss">
@import '@vuepic/vue-datepicker/src/VueDatePicker/style/main.scss';

.dp__theme_light {
  --dp-border-color: #ccc;
}

.dp__input {
  width: 100% !important;
  background-color: #fff !important;
}
</style>
