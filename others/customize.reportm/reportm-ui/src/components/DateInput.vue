<template>
  <div class="report-form-row">
    <label class="report-form-label" for="argument.name">{{ label }}</label>
    <Datepicker vertical
                :name="label"
                :teleport="true"
                v-model="date"
                position="left"
                :enable-time-picker="false"
                @update:modelValue="updateDate" />
  </div>
</template>

<script setup>
import Datepicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import { defineEmits, defineProps, ref } from "vue";

defineProps({ label: String, value: Date });
const emits = defineEmits(["update:value"]);

const date = ref();

function updateDate(newDate) {
  if (!newDate) return;

  // Set hours, minutes and seconds to 0
  var correctedDate = new Date(newDate.getFullYear(), newDate.getMonth(), newDate.getDate(), 0, 0, 0, 0);

  date.value = correctedDate;
  emits("update:value", correctedDate.getTime());
}

</script>

<style lang="scss">
.dp__theme_light {
  --dp-border-color: #ccc;
  --dp-primary-color: #5bb75b;
}

.dp__input_wrap {
  width: 203px !important;
}

.dp__input {
  width: 100% !important;
  background-color: #fff !important;

  &:focus-visible {
    outline: none !important;
  }
}

.dp--clear-btn {
  right: -70px;
}
</style>
