<template>
  <div class="search-container">
    <el-form label-width="80px" :model="form" ref="searchForm" size="small">
      <div class="search-panel">
        <div class="search-fileds">
          <el-row :gutter="22">
            <el-col v-for="(item, index) in form.filedList" :key="index" :span="item.filedType == 11 ? 16 : 8" >
              <el-form-item :label="item.filedName" :prop="'filedList.' + index + '.defaultValue'" :rules="item.rules">
                <!--1：文本，2：数字，3：日期，4：文本域，5：富文本，6：字典单选，7：字典多选，8：图片，9：文件，10：时间，11：时间区间，12：字典，13：时间区间带默认时间，14：单选，带选项值 15选择级连 16部门-->
                <el-input v-if="item.filedType == '1'"
                          v-model="item.defaultValue"
                          clearable
                          :placeholder="item.placeholder"
                          :maxlength="item.maxLength"
                          :minlength="item.minLength"></el-input>

                <el-input-number v-if="item.filedType == '2'"
                                 v-model="item.defaultValue"
                                 clearable
                                 :placeholder="item.filedName"
                                 controls-position="right"
                                 :max="item.maxLength"
                                 :min="item.minLength"></el-input-number>

                <el-date-picker v-if="item.filedType == '3'"
                                v-model="item.defaultValue"
                                :clearable="item.clearable"
                                :placeholder="item.filedName"
                                type="date"></el-date-picker>

                <el-input v-if="item.filedType == '4'"
                          type="textarea"
                          v-model="item.defaultValue"
                          clearable
                          :placeholder="item.filedName"
                          :maxlength="item.maxLength"
                          :minlength="item.minLength"></el-input>

                <el-input v-if="item.filedType == '5'"
                          type="textarea"
                          v-model="item.defaultValue"
                          clearable
                          :placeholder="item.filedName"
                          :maxlength="item.maxLength"
                          :minlength="item.minLength"></el-input>

                <dict-input v-if="item.filedType == '6'"
                            :type="item.optionalValue"
                            v-model="item.defaultValue"
                            clearable
                            size="small"
                            :placeholder="item.filedName"></dict-input>

                <dict-input v-if="item.filedType == '7'"
                            multiple
                            :multiple-limit="item.maxLength"
                            :type="item.optionalValue"
                            v-model="item.defaultValue"
                            clearable
                            size="small"
                            :placeholder="item.filedName"></dict-input>

                <el-upload v-if="item.filedType == '8'"
                           v-model="item.defaultValue"
                           :placeholder="item.filedName"
                           clearable
                           action=""></el-upload>

                <el-upload v-if="item.filedType == '9'"
                           v-model="item.defaultValue"
                           clearable
                           :placeholder="item.filedName"
                           action=""></el-upload>

                <el-date-picker v-if="item.filedType == '10'"
                                v-model="item.defaultValue"
                                :placeholder="item.filedName"
                                type="datetime"
                                align="right"
                                :picker-options="pickerOptions1">
                </el-date-picker>

                <el-date-picker v-if="item.filedType == '11'"
                                v-model= "item.defaultValue"
                                type="datetimerange"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                range-separator="至"
                                start-placeholder="开始日期"
                                default-time="['00:00:00', '23:59:59']"
                                end-placeholder="结束日期">
                </el-date-picker>

                <dict-input v-if="item.filedType == '12'"
                            :type="item.optionalValue"
                            v-model="item.defaultValue"
                            clearable
                            size="small"
                            :placeholder="item.filedName"></dict-input>

                <el-date-picker
                  v-if="item.filedType == '13'"
                  v-model= "item.defaultValue"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  type="daterange"
                  :default-time="['00:00:00', '23:59:59']"
                  range-separator="至 "
                  start-placeholder="开始日期"
                  end-placeholder="结束日期">
                </el-date-picker>
                <el-select v-if="item.filedType == '14'"
                           clearable
                          v-model="item.defaultValue"
                          :placeholder="item.placeholder">
                  <el-option v-for="op in item.options"  :key="op.id" :value="op.id" :label="op.name"></el-option>
                </el-select>
                <el-cascader v-if="item.filedType == '15'"
                  :props="{ value: 'id',label: 'name',children: 'children'}"
                  v-model="item.defaultValue"
                  :options="item.options"></el-cascader>

                <dept-form v-if="item.filedType == '16'"   v-model="item.defaultValue"
                           :placeholder="item.placeholder">
                </dept-form>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="search-actions">
          <div class="actions">
            <el-button icon="el-icon-search" type="primary" size="small" @click="search">查询</el-button>
            <el-button icon="el-icon-close" type="danger" size="small" @click="resetForm">重置</el-button>
          </div>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script>
  import deptForm from '@/components/dept/DeptForm'
  export default {
    props: {
      value: {
        type: [Object, String]
      },
      options: {
        required: true,
        type: Object
      }
    },
    components: {
      deptForm
    },
    data() {
      return {
        formOptions: {},
        form: {
          filedList: []
        },
        value1: '' ,
        value2: '',
        value3: '',
        pickerOptions1: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', new Date());
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', date);
            }
          }, {
            text: '一周前',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', date);
            }
          }]
        }
      }
    },
    methods: {
      setForm(){
        this.form.filedList = this.formOptions.filedList
        var filedList = this.form.filedList
        if(filedList && filedList.length > 0){
          filedList.map(item => {
            var rules = []
            //必填项校验规则
            if(item.filedRequired == '1'){
              rules.push({ required: true, message: '该值不能为空', trigger: 'change' })
            }
            //长度校验
            if(item.filedType == '1' || item.filedType == '4' || item.filedType == '5'){
              rules.push({ min: item.minLength || 0, max: item.maxLength || 10000, message: '该值长度在' + item.minLength + '与' + item.maxLength + '字符', trigger: 'change' })
            }
            //其他正则校验
            if(item.filedValidate && item.filedValidate != ''){
              const reg = eval(item.filedValidate)
              const msg = item.filedValidateMsg || '该值格式错误'
              rules.push({ pattern: reg , message: msg})
            }
            item.rules = rules
            return item
          })
        }
      },
      getFormModel(){
        const filedList = this.form.filedList
        const formModel = {}
        if(filedList && filedList.length > 0){
          filedList.forEach(item => {
            formModel[item.filedCode] = item.defaultValue
          })
        }
        return formModel
      },
      formValidate(callback){
        this.$refs.searchForm.validate(callback)
      },
      clearValidate(){
        this.$refs.searchForm.clearValidate()
      },
      resetForm(){
        let _this = this;
        this.form.filedList.map((item, index) => {
          if(item.filedType == 2){
            item.defaultValue = 0
          }else if(item.filedType == 7){
            item.defaultValue = []
          }else if(item.filedType == 14){
            item.defaultValue = item.options[0].value;
          }else{
            item.defaultValue = ''
          }
          Vue.set(_this.form.filedList, index, item)
          return item
        })
        this.search()
      },
      search(){
        this.$emit("search", this.getFormModel())
      }
    },
    created() {
      this.formOptions = this.options
      this.setForm()
    },
    watch: {
      options: {
        deep: true,
        handler(newVal, oldVal){
          this.formOptions = newVal
          this.setForm()
        }
      },
      form: {
        deep: true,
        handler(newVal, oldVal){
          this.$emit('input', this.getFormModel())
        }
      }
    }
  }
</script>

<style lang="scss">
  .search-container {
    .el-select, .el-date-editor.el-input, .el-date-editor.el-input__inner, .el-cascader, .el-input-number {
      width: 100% !important;
    }
    .el-range-separator{
      min-width: 30px;
    }
    .search-panel {
      display: flex;
      .search-fileds{
        flex: 1;
      }
      .search-actions {
        display: flex;
        flex-direction: column;
        justify-content: flex-end;
        .actions {
          display: flex;
          padding-left: 30px;
          padding-bottom: 18px;
          justify-content: flex-end;
        }
      }
    }
  }
</style>
