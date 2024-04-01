<template>
  <div id="app">
    <el-cascader
      size="small"
      :options="options"
      v-model="selectedOptions"
      @change="addressChange">
    </el-cascader>
  </div>
</template>

<script>
import { regionData,CodeToText } from 'element-china-area-data'
export default {
  props: {
    "value": [String, Number, Array],
  },
  data () {
    return {
      options: regionData,
      selectedOptions: []
    }
  },
  created(){
    this.selectedOptions = this.value
  },
  // mutated(){
  //     this.value = this.selectedOptions
  // },
  watch: {
    value(newVal, oldVal){
      this.selectedOptions = newVal
      console.log(newVal)
    }
  },
  methods: {
    addressChange(arr) {
      this.$emit('input',arr)
      this.selectedOptions = arr
      // this.value = arr
      // console.log(arr);
      // console.log(CodeToText[arr[0]], CodeToText[arr[1]], CodeToText[arr[2]]);
      // console.log( CodeToText[arr[1]]);
      // console.log( CodeToText[arr[2]]);
    },
  }
}
</script>

