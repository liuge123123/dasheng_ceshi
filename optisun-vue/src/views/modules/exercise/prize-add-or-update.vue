<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="奖品类型" prop="prizeType">
        <el-select v-model="dataForm.prizeType" placeholder="奖品类型">
          <el-option value="SCORE" label="奖金"></el-option>
          <el-option value="PRACTICALITY" label="实物"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="奖品名称" prop="prizeName">
        <el-input v-model="dataForm.prizeName" placeholder="奖品名称"></el-input>
      </el-form-item>
      <el-form-item label="奖品图片" prop="prizeImg">
        <img-upload v-model="dataForm.prizeImg" placeholder="奖品名称"></img-upload>
      </el-form-item>
      <el-form-item label="奖品价值" prop="prizeValue">
        <el-input v-model="dataForm.prizeValue" placeholder="奖品值"></el-input>
      </el-form-item>
      <el-form-item label="奖品数量" prop="prizeNum">
        <el-input v-model="dataForm.prizeNum" placeholder="奖品数量"></el-input>
        <div class="form-tips">中一次奖品数量减1，当奖品数量减到0时，将无法中得改奖品</div>
      </el-form-item>
      <el-form-item label="中奖几率" prop="ratio">
        <el-input v-model="dataForm.ratio" placeholder="中奖几率，值越大中奖概率越高"></el-input>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import ImgUpload from '@/components/upload/img-upload'

export default {
  components: {
    ImgUpload,
  },
  data() {
    return {
      loading: false,
      startTime: '',
      endTime: '',
      dataForm: {
        id: 0,
        prizeType: '',
        prizeName: '',
        prizeImg: '',
        prizeValue: '',
        prizeNum: '',
        ratio: ''
      },
      options: [],
      dataRule: {
        prizeName: [
          {required: true, message: '奖品名称不能为空', trigger: 'blur'}
        ],
        prizeImg: [
          {required: true, message: '奖品图片不能为空', trigger: 'blur'}
        ],
        prizeValue: [
          {required: true, message: '奖品价值不能为空', trigger: 'blur'}
        ],
        prizeNum: [
          {required: true, message: '奖品数量不能为空', trigger: 'blur'}
        ],
        ratio: [
          {required: true, message: '中奖概率不能为空', trigger: 'blur'}
        ],
        prizeType: [
          {required: true, message: '奖品类型不能为空', trigger: 'change'}
        ]
      }
    }
  },
  mounted() {

  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/exercise/prize/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.prizeType = data.prize.prizeType
              this.dataForm.prizeName = data.prize.prizeName
              this.dataForm.prizeImg = data.prize.prizeImg
              this.dataForm.prizeValue = data.prize.prizeValue
              this.dataForm.prizeNum = data.prize.prizeNum
              this.dataForm.ratio = data.prize.ratio
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/exercise/prize/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'prizeType': this.dataForm.prizeType,
              'prizeName': this.dataForm.prizeName,
              'prizeImg': this.dataForm.prizeImg,
              'prizeValue': this.dataForm.prizeValue,
              'prizeNum': this.dataForm.prizeNum,
              'ratio': this.dataForm.ratio,
            })
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
