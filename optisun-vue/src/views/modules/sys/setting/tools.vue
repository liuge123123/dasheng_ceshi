<template>
  <div class="mod-config">
    <el-card class="form" v-for="(item, index) in forms" :key="index" v-loading="item.loading">
      <div slot="header">
        {{item.title}}
      </div>
      <div class="body">
        <div class="field" v-for="(field, findex) in item.fields" :key="findex">
          <div class="label">{{field.label}}</div>
          <input class="input" v-model="forms[index].fields[findex].value" :placeholder="field.label" />
        </div>
      </div>
      <div class="footer">
        <el-button type="primary" @click="submitHandle(index)">确定</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "setting",
  data() {
    return {
      forms: [
        {
          loading: false,
          url: '/cust/cust/changeUp',
          title: '单个客户变线',
          fields: [
            {
              code: 'sid',
              label: '客户ID',
              value: '',
              require: true
            },
            {
              code: 'did',
              label: '合并到客户ID',
              value: '',
              require: true
            }
          ]
        },
        {
          loading: false,
          url: '/cust/cust/changeUpGroup',
          title: '整线客户变更员工',
          fields: [
            {
              code: 'susername',
              label: '客户名称',
              value: '',
              require: true
            },
            {
              code: 'dusername',
              label: '合并到员工名称',
              value: '',
              require: true
            }
          ]
        },
        {
          loading: false,
          url: '/cust/cust/userMerge',
          title: '组内合并员工',
          fields: [
            {
              code: 'susername',
              label: '员工名称',
              value: '',
              require: true
            },
            {
              code: 'dusername',
              label: '合并到员工名称',
              value: '',
              require: true
            }
          ]
        },{
          loading: false,
          url: '/cust/cust/userMergeGroup',
          title: '跨组合并员工',
          fields: [
            {
              code: 'susername',
              label: '员工名称',
              value: '',
              require: true
            },
            {
              code: 'dusername',
              label: '合并到员工名称',
              value: '',
              require: true
            }
          ]
        }

      ]
    }
  },
  filters: {},
  computed: {},
  activated() {

  },
  methods: {
    submitHandle(index){
      let form = this.forms[index]
      let params = {}
      for (let i = 0; i < form.fields.length; i++) {
        let field = form.fields[i]
        field.value = field.value.trim()
        params[field.code] = field.value
        if(field.require && (!field.value || field.value == '')){
          this.$message.error(`${field.label}不能为空值`)
          return
        }
      }
      form.loading = true
      this.$http({
        url: this.$http.adornUrl(form.url),
        method: 'post',
        data: this.$http.adornData(params)
      }).then(({data}) => {
        form.loading = false
        if (data.code == 0) {
          this.$message.success('操作成功')
        } else {
          this.$message.error(data.msg)
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .mod-config{
    display: flex;
    flex-wrap: wrap;
    .form{
      width: 500px;
      margin-right: 15px;
      margin-bottom: 15px;
      .field{
        margin-bottom: 20px;
        .label{
          font-weight: bold;
          line-height: 30px;
        }
        .input{
          border: 1px solid #e1e1e8;
          width: 100%;
          line-height: 40px;
          border-radius: 4px;
          padding: 0 10px;
          &:focus{
            outline: 1px solid $--color-primary;
          }
        }
      }
      .footer{
        text-align: right;
      }
    }
  }
</style>
