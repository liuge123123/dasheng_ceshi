<template>
  <div id="app">
    <el-date-picker
      type="date"
      size="small"
      v-model="selectedValue"
      value-format="yyyy-MM-dd"
      style="width: 100%;"
      @change="onChange()"
    ></el-date-picker>
  </div>
</template>

<script>
import {timeStampDateTimeFormat,getTimestamp} from  "@/utils/index"
export default {
  props: {
    "value": [String, Number, Array],
  },
  data () {
    return {
      selectedValue: ''
    }
  },
  created(){

    if(this.value && this.value != null) {
      this.selectedValue = timeStampDateTimeFormat(this.value)
    }
  },

  watch: {
    value(newVal, oldVal){
      if(newVal && newVal != '') {
        this.selectedValue = timeStampDateTimeFormat(newVal)
      }
    }
  },

  methods: {
    onChange(){
      if(this.selectedValue != null){
      let value = getTimestamp(this.selectedValue)
        this.$emit('input',value.toString())
      }else{
        this.$emit('input', "")
      }
    }
  }
}
</script>

