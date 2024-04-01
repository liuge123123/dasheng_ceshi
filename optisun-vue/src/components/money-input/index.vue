<template>
  <el-input v-model="displayValue" @input="change" :size="size" :disabled="ifDisabled">
    <template slot="suffix"><slot name="suffix"></slot> </template>
  </el-input>
</template>

<script>
  import { create, all } from 'mathjs';

  const math = create(all);
  math.config({
    number: 'BigNumber',      // Default type of number:
    precision: 64             // Number of significant digits for BigNumbers
  })

  export default {
    name: "MoneyInput",
    props: {
      value: {
        type: [Number, String],
        default: 0
      },
      digit: {
        type: Number,
        default: 2
      },
      multiple: {
        type: Number,
        default: 1
      },
      size: {
        type: String
      },
      ifDisabled:{
        type:Boolean,
        default: false
      }
    },
    data(){
      return {
        displayValue: 0,
        init: false
      }
    },
    mounted(){
      console.log(this.value)
      this.displayValue = math.divide(Number(this.value), this.multiple)
    },
    methods: {
      change(){
        if(this.digit == 0) {
          // 小数位数位0
          this.displayValue= this.displayValue.replace(/[^0-9]/g,'')
        }else{
          this.displayValue= this.displayValue.replace(/[^0-9.]/g,'')
          // 防止输入多个小数点
          if(this.displayValue != '' && isNaN(this.displayValue)){
            this.displayValue = this.displayValue.substring(0, this.displayValue.length - 1)
          }
          // 小数位数限制
          if(this.displayValue.indexOf('.') != -1){
            let digitStr = this.displayValue.substring(this.displayValue.indexOf('.') + 1, this.displayValue.length)
            if(digitStr.length > this.digit){
              this.displayValue = this.displayValue.substring(0, this.displayValue.length - 1)
            }
          }
        }
        // 解决首位直接输入 '0开头的数字'问题
        if(this.displayValue.length == 2 && this.displayValue.charAt(0)==0 && this.displayValue.charAt(1)!='.'){
          this.displayValue = this.displayValue.charAt(1);
        }
        // 实际值为输入值的100倍
        let val = 0
        if(this.displayValue != ''){
          val = math.multiply(math.bignumber(this.displayValue) , math.bignumber(this.multiple))
        }
        this.$emit('input', val.toString())
      }
    },
    watch: {
      value(newVal, oldVal){
        this.displayValue = math.divide(Number(newVal), this.multiple)
      },
      multiple(newVal, oldVal){
        let val = 0
        if(this.displayValue != ''){
          val = math.multiply(math.bignumber(this.displayValue) , math.bignumber(this.multiple))
        }
        this.$emit('input', val.toString())
      }
    }
  }
</script>

<style scoped>

</style>
