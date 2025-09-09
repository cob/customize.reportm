<template>
  <div class="report-form-row">
    <label class="report-form-label" :for="label">{{ label }}</label>
    <textarea id="label" class="report-form-input" :value="value"
              @input="$emit('update:value', $event.target.value)"></textarea>
  </div>
</template>

<script setup>
import {defineEmits, defineProps} from "vue";

defineProps({label: String, value: String})
defineEmits(['update:value'])

</script>

<style lang="scss">
.report-form-row textarea {
  box-sizing: border-box;
  resize: none;
  height: 50px;
}
</style>
