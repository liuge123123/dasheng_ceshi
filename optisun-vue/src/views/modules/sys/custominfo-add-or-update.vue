<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!form.filedId ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="form" :rules="rules" ref="form" @keyup.enter.native="addOrUpdateHandle()"
             label-width="120px" size="small">
      <div class="lTitle lTitles">基本配置</div>
      <el-form-item label="字段编码" prop="filedCode">
        <el-input v-model="form.filedCode" placeholder="请输入字段编码"></el-input>
      </el-form-item>
      <el-form-item label="字段名称" prop="filedName">
        <el-input v-model="form.filedName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="字段场景" prop="modelId">
        <el-checkbox-group v-model="form.modelId">
          <el-checkbox :label="0">客户</el-checkbox>
          <el-checkbox :label="1">订单</el-checkbox>
          <el-checkbox :label="2">产品</el-checkbox>
          <el-checkbox :label="3">工单</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="字段类型" prop="filedType">
        <div style="display: flex">
          <el-select style="flex: 1" v-model="form.filedType" @change="updateFileType(form.filedType)">
            <el-option :value="1" label="文本"></el-option>
            <el-option :value="2" label="数字"></el-option>
            <el-option :value="3" label="日期"></el-option>
            <el-option :value="4" label="文本域"></el-option>
            <el-option :value="5" label="富文本"></el-option>
            <el-option :value="6" label="单选"></el-option>
            <el-option :value="7" label="多选"></el-option>
            <el-option :value="8" label="图片"></el-option>
            <el-option :value="9" label="文件"></el-option>
            <el-option :value="10" label="下拉选项"></el-option>
            <el-option :value="11" label="关联工单分类"></el-option>
<!--            <el-option :value="12" label="关联座席组"></el-option>-->
            <el-option :value="13" label="关联字典"></el-option>
            <el-option :value="17" label="区域"></el-option>
            <el-option :value="18" label="选择客户"></el-option>
<!--            <el-option :value="14" label="关联用户"></el-option>-->
<!--            <el-option :value="15" label="关联"></el-option>-->
<!--            <el-option :value="16" label="关联咨询分类"></el-option>-->
          </el-select>
          <el-input v-model="form.optionalValue" style="width: 200px; margin-left: 10px" v-if="form.filedType == 13" placeholder="请输入字典编码"></el-input>
        </div>
      </el-form-item>
      <el-form-item v-if="form.filedType == 6 || form.filedType == 7 || form.filedType == 10 " label="字段选项">
        <div v-for="(item, index) in form.optJson" :key="index" class="similarQuestion">
          选项{{index+1}}:
          <el-input v-model="form.optJson[index].optional_value" placeholder="选项值" :num="index"
                    style="width: 150px"></el-input>
          <el-input v-model="form.optJson[index].optional_label" placeholder="选项标签" :num="index"
                    style="width: 150px"></el-input>
          <i v-if="form.optJson.length >2 && index >1 " class="el-icon-delete" @click="deleteItem(item, index)"></i>
        </div>
        <p style="color: red" v-show="errShow">至少输入2个非空选项且不能包含空值</p>
        <el-button style="margin-top: 10px" @click="addItem" type="primary" icon="el-icon-circle-plus-outline"
                   size="mini">新增选项
        </el-button>
      </el-form-item>
      <el-form-item label="默认值">
        <el-input v-model="form.defaultValue" placeholder="请输入默认值"></el-input>
      </el-form-item>
      <el-form-item label="字段属性">
        <el-radio-group v-model="form.sysFiled">
          <el-radio :label="1">系统字段</el-radio>
          <el-radio :label="0">自定义</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="备注">
        <el-input v-model="form.remark" autocomplete="off"></el-input>
      </el-form-item>

      <div class="lTitle lTitles">校验规则</div>

      <el-form-item label="是否必填" prop="filedRequired">
        <el-radio-group v-model="form.filedRequired">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="2">否</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="校验规则类型" v-if="form.filedType == 1">
        <el-radio-group v-model="form.filedValidate" @change="chooseFiledValidate(form.filedValidate)">
          <el-radio :label="1">手机号</el-radio>
          <el-radio :label="2">邮箱</el-radio>
          <el-radio :label="3">身份证</el-radio>
          <el-radio :label="9">其他</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="校验错误提示语">
        <el-input v-model="form.filedValidateMsg" autocomplete="off" placeholder="数据格式错误"></el-input>
      </el-form-item>
      <el-form-item v-if="'_1_'.indexOf('_' + form.filedType + '_') != -1 && form.filedValidate == 9" label="校验正则" prop="filedValidateRegular">
        <el-input v-model="form.filedValidateRegular"
                  autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item v-if="'_6_10_11_12_13_14_'.indexOf('_' + form.filedType + '_') == -1" :label="form.filedType | minLengthFilter">
        <el-input v-model="form.minLength" autocomplete="off" placeholder="20"></el-input>
      </el-form-item>
      <el-form-item v-if="'_6_10_11_12_13_14_'.indexOf('_' + form.filedType + '_') == -1" :label="form.filedType | maxLengthFilter">
        <el-input v-model="form.maxLength" autocomplete="off" placeholder="100"></el-input>
      </el-form-item>

      <el-form-item class="formSubmit">
        <el-button type="primary" @click="addOrUpdateHandle()" :loading="loading">确定</el-button>
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
  export default {
    data() {
      let validateCode = (rule, value, callback) => {
        let params = {
          'id': this.form.filedId,
          'code': value
        }
        this.$http({
          url: this.$http.adornUrl(`/servicesys/scustomfield/code/validate`),
          method: 'get',
          params: params
        }).then(({data}) => {
          if (data.code == 0 && data.data) {
            callback()
          } else {
            callback(new Error(data.msg))
          }
        }).catch(() => {
          callback(new Error('服务端异常，请检查网络设置'))
        })
      }

      return {
        loading: false,
        errShow: false,
        dataList: [],
        dataListLoading: false,
        dialogTableVisible: false,
        dialogFormVisible: false,
        dataListSelections: [],
        formLabelWidth: '120px',
        form: {
          optJson: [
            {optional_value: '', optional_label: ''},
            {optional_value: '', optional_label: ''}
          ],
          filedId: 0,
          filedName: '',
          sysFiled: 0,
          filedType: 1,
          modelId: [],
          filedCode: '',
          filedRequired: 2,
          filedValidate: 9,
          filedValidateMsg: '数据格式错误',
          filedValidateRegular: '',
          maxLength: '100',
          minLength: '20',
          defaultValue: '',
          optionalValue: '',
          remark: '',
          value: '',
          desc: ''
        },
        rules: {
          filedName: [{required: true, message: '请输入字段名称', trigger: 'blur'}],
          filedType: [{required: true, message: '请选择字段类型', trigger: 'change'}],
          modelId: [{required: true, message: '请选择字段场景', trigger: 'change'}],
          filedCode: [{required: true, message: '请输入字段编码', trigger: 'blur'}, {
            validator: validateCode,
            trigger: 'blur'
          }],
          filedRequired: [{required: true, message: '请选择', trigger: 'change'}]
        }
      }
    },
    filters: {
      maxLengthFilter(val) {
        if (val == 2) {
          return '最大值'
        } else if (val == 3) {
          return '最大日期'
        } else if (val == 7) {
          return '最多选项'
        } else if (val == 8 || val == 9) {
          return '文件最大KB'
        } else {
          return '最大长度'
        }
      },
      minLengthFilter(val) {
        if (val == 2) {
          return '最小值'
        } else if (val == 3) {
          return '最小日期'
        } else if (val == 7) {
          return '最少选项'
        } else if (val == 8 || val == 9) {
          return '文件最小KB'
        } else {
          return '最小长度'
        }
      }
    },
    methods: {
      goBack() {
        this.$emit('back', {})
      },
      updateFileType(value) {

      },
      addItem() {
        this.form.optJson.push({
          optional_value: ''
        })
      },
      deleteItem(item, index) {
        this.form.optJson.splice(index, 1)
      },
      init(id) {
        this.form.filedId = id || ''
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['form'].resetFields()
          if (this.form.filedId) {
            this.$http({
              url: this.$http.adornUrl(`/servicesys/scustomfield/info/${this.form.filedId}`),
              method: 'get',
              data: id
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.form.filedId = data.sCustomField.filedId
                this.form.filedName = data.sCustomField.filedName
                this.form.filedType = Number(data.sCustomField.filedType)
                this.form.modelId = JSON.parse("[" + data.sCustomField.modelId + "]")
                this.form.filedCode = data.sCustomField.filedCode
                this.form.filedRequired = Number(data.sCustomField.filedRequired)
                this.form.filedValidate = Number(data.sCustomField.filedValidate)
                this.form.sysFiled = Number(data.sCustomField.sysFiled)
                this.form.filedValidateMsg = data.sCustomField.filedValidateMsg
                this.form.maxLength = data.sCustomField.maxLength
                this.form.minLength = data.sCustomField.minLength
                this.form.defaultValue = data.sCustomField.defaultValue
                this.form.optionalValue = data.sCustomField.optionalValue
                this.form.remark = data.sCustomField.remark
                if(this.form.filedType == 6 || this.form.filedType == 7 || this.form.filedType == 10) {
                  let json = JSON.parse(this.form.optionalValue)
                  this.form.optJson = json
                }
                this.dialogFormVisible = true
              }
            })
          }
        })
      },
      chooseFiledValidate(value) {
        if (value == 1) {
          this.form.filedValidateRegular = '/^1[0-9]{10}$/'
        } else if (value == 2) {
          this.form.filedValidateRegular = '/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/'
        } else if (value == 3) {
          this.form.filedValidateRegular = '/^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$/'
        } else if (value == 9) {
          this.form.filedValidateRegular = ''
        }
      },
      // 表单提交
      addOrUpdateHandle() {
        const that = this
        let params = {
          'filedId': this.form.filedId || undefined,
          'filedName': this.form.filedName,
          'modelId':this.form.modelId.toString(),
          'filedCode': this.form.filedCode,
          'filedType': this.form.filedType,
          'filedRequired': this.form.filedRequired,
          'filedValidate': this.form.filedValidate,
          'filedValidateMsg': this.form.filedValidateMsg,
          'filedValidateRegular': this.form.filedValidateRegular,
          'maxLength': this.form.maxLength,
          'minLength': this.form.minLength,
          'defaultValue': this.form.defaultValue,
          'optionalValue': this.form.optionalValue,
          'remark': this.form.remark,
          'sysFiled': this.form.sysFiled
        }
        // 单选、多选、下拉选择 选项值校验及赋值
        if (that.form.filedType == 6 || that.form.filedType == 7 || that.form.filedType == 10) {
          if(that.form.optJson.length < 2){
            this.errShow = true
            return false
          }
          for (var i = 0; i < that.form.optJson.length; i++) {
            if (that.form.optJson[i].optional_value == '' || that.form.optJson[i].optional_label == '') {
              this.errShow = true
              return false
            }
          }
          params.optionalValue = JSON.stringify(that.form.optJson)
        }else if(that.form.filedType != 13){
          params.optionalValue = ''
        }
        // 校验规则的赋值
        if(that.form.filedType != 1){
          params.filedValidate = 9
          params.filedValidateRegular = ''
        }
        if(that.form.filedType == 6
          || that.form.filedType == 10
          || that.form.filedType == 11
          || that.form.filedType == 12
          || that.form.filedType == 13
          || that.form.filedType == 14){
          params.maxLength = 0
          params.minLength = 0
        }
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/servicesys/scustomfield/${!this.form.filedId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData(params)
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false
                    this.$emit('back', {refresh: true})
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
<style scoped>
  .el-radio-group {
    display: inline;
  }
</style>
