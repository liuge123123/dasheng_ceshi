<template>
  <el-select
    class="dict-input"
    v-model="selectedVal"
    :placeholder="placeholder"
    :multiple="multiple"
    :disabled="disabled"
    :size="size"
    :clearable="clearable"
    :collapseTags="collapseTags"
    :multipleLimit="multipleLimit"
    :filterable="filterable"
    :allowCreate="allowCreate"
    :reserveKeyword="reserveKeyword"
    :defaultFirstOption="defaultFirstOption"
    :popperAppendToBody="popperAppendToBody"
    :automaticDropdown="automaticDropdown"
    @change="changeHandle">
    <el-option v-for="item in codeList" :value="item.value" :key="item.value" :label="item.label"></el-option>
  </el-select>
</template>

<script>

  export default {
    props: {
      "value": [String, Number, Array],
      "type": { type: String, required: true },
      "placeholder": { type: String,  default: '请选择' },
      "multiple": { type: Boolean,  default: false },
      "disabled": { type: Boolean,  default: false },
      "size": { type: String,  default: 'small' },
      "clearable": { type: Boolean,  default: false },
      "collapseTags": { type: Boolean,  default: false },
      "multipleLimit": { type: Number,  default: 0 },
      "filterable": { type: Boolean,  default: false },
      "allowCreate": { type: Boolean,  default: false },
      "reserveKeyword": { type: Boolean,  default: false },
      "defaultFirstOption": { type: Boolean,  default: false },
      "popperAppendToBody": { type: Boolean,  default: true },
      "automaticDropdown": { type: Boolean,  default: false }
    },
    data() {
      return {
        codeList: [],
        selectedVal: null
      }
    },
    methods: {
      getDictList(){
        if(this.type == "user"){
          // usersimpleList({type:this.type}).then(response => {
          //   this.codeList = response.data.data;
          // })
        }else if(this.type == "thirdApp"){
            // getAppList().then(res => {
            //   this.codeList = res.data.data.map(item => {
            //     return {
            //       value: item.id,
            //       label: item.appName
            //     }
            //   });
            //   if(!this.value && this.codeList != null && this.codeList.length > 0){
            //     this.selectedVal = this.codeList[0].value;
            //     this.$emit('input', this.selectedVal);
            //   }
            // });
        }else{
          this.$http({
            url: this.$http.adornUrl('/sys/dictionary/codeList'),
            method: 'get',
            params: this.$http.adornParams({
                      code: this.type
                    })
          }).then(({data}) => {
            if(data.code == 0){
              this.codeList = data.list.map(item => {
                return {
                  label: item.value,
                  value: item.code
                }
              });
            }else{
              this.$message.error(data.msg);
            }
          })
        }
      },
      changeHandle(value){
        this.$emit('input', value)
      }
    },
    created(){
      this.getDictList()
      this.selectedVal = this.value
    },
    watch: {
      value(newVal, oldVal){
        this.selectedVal = newVal
      }
    }
  }
</script>

<style scoped lang="scss">
  .dict-input{
    display: block;
  }
</style>
