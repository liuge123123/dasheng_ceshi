<template>
  <el-dialog :visible.sync="visible" v-loading="loading" title="退单" width="500px">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="100px" label-position="left" size="small">
      <!-- <el-form-item label="锁定天数" prop="lockDay">
        <el-input-number v-model="dataForm.lockDay" :min="0" :precision="0"></el-input-number>
        <div class="form-tips">从操作退单时间开始开始计算，至锁定天数到期后系统自动退还本金</div>
      </el-form-item> -->
      <el-form-item label="退单类型" >
        <el-radio-group v-model="dataForm.quitType">
          <el-radio :label="1">退还本金和领取收益</el-radio>
          <el-radio :label="0">退还本金</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注信息" prop="cancelRemark">
        <el-input v-model="dataForm.cancelRemark" placeholder="请输入退单备注" type="textarea" :rows="5"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      loading: false,
      dataForm: {
        orderId: null,
        cancelRemark: '',
        lockDay: 14,
        quitType:0
      },
      dataRule: {
        cancelRemark: [
          {required: true, message: '备注不能为空', trigger: 'blur'}
        ],
        lockDay: [
          {required: true, message: '锁定天数不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  created() {

  },
  methods: {
    init(id) {
      this.dataForm.orderId = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/l/lorder/quitnew`),
            method: 'post',
            data: this.$http.adornData(this.dataForm)
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
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
