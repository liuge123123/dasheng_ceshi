<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="父id" prop="pid">
        <el-input v-model="dataForm.pid" placeholder="父id"></el-input>
      </el-form-item>
      <el-form-item label="简称" prop="shortname">
        <el-input v-model="dataForm.shortname" placeholder="简称"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="全称" prop="mergerName">
        <el-input v-model="dataForm.mergerName" placeholder="全称"></el-input>
      </el-form-item>
      <el-form-item label="层级" prop="level">
        <el-input v-model="dataForm.level" placeholder="层级 1 2 3 省市区县"></el-input>
      </el-form-item>
      <el-form-item label="拼音" prop="pinyin">
        <el-input v-model="dataForm.pinyin" placeholder="拼音"></el-input>
      </el-form-item>
      <el-form-item label="长途区号" prop="code">
        <el-input v-model="dataForm.code" placeholder="长途区号"></el-input>
      </el-form-item>
      <el-form-item label="邮编" prop="zipCode">
        <el-input v-model="dataForm.zipCode" placeholder="邮编"></el-input>
      </el-form-item>
      <el-form-item label="首字母" prop="first">
        <el-input v-model="dataForm.first" placeholder="首字母"></el-input>
      </el-form-item>
      <el-form-item label="经度" prop="lng">
        <el-input v-model="dataForm.lng" placeholder="经度"></el-input>
      </el-form-item>
      <el-form-item label="纬度" prop="lat">
        <el-input v-model="dataForm.lat" placeholder="纬度"></el-input>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
  export default {
    data() {
      return {
        loading: false,
        dataForm: {
          id: 0,
          pid: '',
          shortname: '',
          name: '',
          mergerName: '',
          level: '',
          pinyin: '',
          code: '',
          zipCode: '',
          first: '',
          lng: '',
          lat: '',
          isdist: ''
        },
        dataRule: {
          pid: [
            {required: true, message: '父id不能为空', trigger: 'blur'}
          ],
          shortname: [
            {required: true, message: '简称不能为空', trigger: 'blur'}
          ],
          name: [
            {required: true, message: '名称不能为空', trigger: 'blur'}
          ],
          mergerName: [
            {required: true, message: '全称不能为空', trigger: 'blur'}
          ],
          level: [
            {required: true, message: '层级 1 2 3 省市区县不能为空', trigger: 'blur'}
          ],
          pinyin: [
            {required: true, message: '拼音不能为空', trigger: 'blur'}
          ],
          code: [
            {required: true, message: '长途区号不能为空', trigger: 'blur'}
          ],
          zipCode: [
            {required: true, message: '邮编不能为空', trigger: 'blur'}
          ],
          first: [
            {required: true, message: '首字母不能为空', trigger: 'blur'}
          ],
          lng: [
            {required: true, message: '经度不能为空', trigger: 'blur'}
          ],
          lat: [
            {required: true, message: '纬度不能为空', trigger: 'blur'}
          ],
          isdist: [
            {required: true, message: '不能为空', trigger: 'blur'}
          ]
        }
      }
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
              url: this.$http.adornUrl(`/sys/sysregion/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.pid = data.sysRegion.pid
                this.dataForm.shortname = data.sysRegion.shortname
                this.dataForm.name = data.sysRegion.name
                this.dataForm.mergerName = data.sysRegion.mergerName
                this.dataForm.level = data.sysRegion.level
                this.dataForm.pinyin = data.sysRegion.pinyin
                this.dataForm.code = data.sysRegion.code
                this.dataForm.zipCode = data.sysRegion.zipCode
                this.dataForm.first = data.sysRegion.first
                this.dataForm.lng = data.sysRegion.lng
                this.dataForm.lat = data.sysRegion.lat
                this.dataForm.isdist = data.sysRegion.isdist
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
              url: this.$http.adornUrl(`/sys/sysregion/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'pid': this.dataForm.pid,
                'shortname': this.dataForm.shortname,
                'name': this.dataForm.name,
                'mergerName': this.dataForm.mergerName,
                'level': this.dataForm.level,
                'pinyin': this.dataForm.pinyin,
                'code': this.dataForm.code,
                'zipCode': this.dataForm.zipCode,
                'first': this.dataForm.first,
                'lng': this.dataForm.lng,
                'lat': this.dataForm.lat,
                'isdist': this.dataForm.isdist
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
