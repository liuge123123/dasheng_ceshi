<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.userId ? '新增' : '修改'"></el-page-header>
    <el-form
      class="cpf"
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="100px">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="dataForm.userName" placeholder="登录帐号" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!dataForm.userId"
                    :class="{ 'is-required': !dataForm.userId }">
        <el-input v-model="dataForm.password" type="password" placeholder="密码" maxlength="100"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="comfirmPassword" v-if="!dataForm.userId"
                    :class="{ 'is-required': !dataForm.userId }">
        <el-input v-model="dataForm.comfirmPassword" type="password" placeholder="确认密码" maxlength="100"></el-input>
      </el-form-item>
      <el-form-item label="可登录设备数" prop="loginLimit">
        <el-input-number controls-position="right" v-model="dataForm.loginLimit" placeholder="可登录设备数" :step-strictly="1" :precision="0" :min="0"></el-input-number>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="dataForm.name" placeholder="真实姓名" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="手机号" maxlength="20"></el-input>
      </el-form-item>
      <el-form-item label="客服地址" prop="customerUrl">
        <el-input v-model="dataForm.customerUrl" placeholder="客服地址" maxlength="20"></el-input>
      </el-form-item>

<!--      <el-form-item label="部门" prop="deptId">-->
<!--        <el-popover ref="menuListPopover" placement="bottom-start" trigger="click">-->
<!--          <el-tree :data="dataList" :props="defaultProps"-->
<!--                   ref="deptListTree"-->
<!--                   @current-change="menuListTreeCurrentChangeHandle"-->
<!--                   :default-expand-all="true"-->
<!--                   :highlight-current="true"-->
<!--                   :expand-on-click-node="false"-->
<!--                   node-key="id" @node-click="handleNodeClick"></el-tree>-->
<!--        </el-popover>-->
<!--        <el-input-->
<!--          v-model="dataForm.deptName"-->
<!--          v-popover:menuListPopover-->
<!--          :readonly="true"-->
<!--          placeholder="点击选择部门"-->
<!--          class="menu-list__input"-->
<!--        ></el-input>-->
<!--      </el-form-item>-->
      <el-form-item label="头像" prop="avatar">
        <el-upload
          ref="upload"
          action
          class="avatar-uploader"
          :http-request="uploadSectionFile"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeUploadHandle">
          <img v-if="dataForm.avatar" :src="dataForm.avatar" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
<!--      <el-form-item label="员工工号">-->
<!--        <el-input v-model="dataForm.jobNumber" placeholder="员工工号" maxlength="20"></el-input>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="邮箱" prop="email">-->
<!--        <el-input v-model="dataForm.email" placeholder="邮箱" maxlength="100"></el-input>-->
<!--      </el-form-item>-->
      <el-form-item label="角色" size="mini" prop="roleIdList">
        <el-checkbox-group v-model="dataForm.roleIdList">
          <el-checkbox
            v-for="role in roleList"
            :key="role.roleId"
            :label="role.roleId"
          >{{ role.roleName }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
<!--      <el-form-item label="数据权限" size="mini" prop="dataPermission">-->
<!--        <dept-person-mutil2 v-model="dataForm.dataPermission"></dept-person-mutil2>-->
<!--      </el-form-item>-->
      <el-form-item label="账号类型">
        <el-radio-group v-model="dataForm.type">
          <el-radio :label="1" v-if="userInfo.type==0">平台账号</el-radio>
          <el-radio :label="2" v-if="userInfo.type==0 || userInfo.type==1">总代理账号</el-radio>
          <el-radio :label="3" v-if="userInfo.type==0 || userInfo.type==2">分代理账号</el-radio>
          <el-radio :label="4" v-if="userInfo.type==0 || userInfo.type==3">普通员工账号</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态" size="mini" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="0">禁用</el-radio>
          <el-radio :label="1">正常</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
  import { isEmail, isMobile } from "@/utils/validate";
  import DeptPersonMutil2 from "@/components/dept/DeptPersonMutil2";
  export default {
    components: {DeptPersonMutil2},
    data () {
      var validatePassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error("密码不能为空"));
        } else {
          callback();
        }
      };
      var validateComfirmPassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error("确认密码不能为空"));
        } else if (this.dataForm.password !== value) {
          callback(new Error("确认密码与密码输入不一致"));
        } else {
          callback();
        }
      };
      var validateEmail = (rule, value, callback) => {
        if(value==null || value ==''){
          callback();
        }
        if (!isEmail(value)) {
          callback(new Error("邮箱格式错误"));
        } else {
          callback();
        }
      };
      var validateMobile = (rule, value, callback) => {
        if (!isMobile(value)) {
          callback(new Error("手机号格式错误"));
        } else {
          callback();
        }
      };
      return {
        loading: false,
        dataForm: {
          id: 0,
          userName: "",
          password: "",
          comfirmPassword: "",
          salt: "",
          email: "",
          mobile: "",
          avatar:'',
          name:'',
          roleIdList: [],
          status: 1,
          orgId:'',
          deptId: '',
          deptName:'',
          jobNumber: '',
          type:1,
          dataPermission: [],
          customerUrl:'',
          relationId:0,
          loginLimit: 1
        },
        dataRule: {
          deptId: [
            { required: true, message: '部门不能为空', trigger: 'blur' }
          ],
          jobNumber: [
            { required: true, message: '员工工号不能为空', trigger: 'blur' }
          ],
          userName: [
            { required: true, message: "用户名不能为空", trigger: "blur" }
          ],
          name: [
            { required: true, message: '姓名不能为空', trigger: 'blur' }
          ],
          password: [{ validator: validatePassword, trigger: "blur" }],
          comfirmPassword: [
            { validator: validateComfirmPassword, trigger: "blur" }
          ],
          email: [
            { validator: validateEmail, trigger: "blur" }
          ],
          // mobile: [
          //   { required: true, message: "手机号不能为空", trigger: "blur" },
          //   { validator: validateMobile, trigger: "blur" }
          // ]
        },
        roleList: [],
        dataList:[],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      }
    },
    computed:{
      userInfo(){
        return this.$store.state.user.userInfo;
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.userId = id || 0

        this.$http({
          url: this.$http.adornUrl("/sys/role/select"),
          method: "get",
          params: this.$http.adornParams()
        })
          .then(({ data }) => {
            this.roleList = data && data.code === 0 ? data.list : [];
          })
          .then(() => {
            this.$nextTick(() => {
              this.$refs["dataForm"].resetFields();
            });
          }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/sysdept/tree'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataList = data.data
            } else {
              this.dataList = []
            }
          })
            .then(()=>{
              this.$nextTick(() => {
                this.$refs['dataForm'].resetFields()
                if (this.dataForm.userId) {
                  this.loading = true
                  this.$http({
                    url: this.$http.adornUrl(`/sys/user/info/${this.dataForm.userId}`),
                    method: 'get',
                    params: this.$http.adornParams()
                  }).then(({data}) => {
                    this.loading = false
                    if (data && data.code === 0) {
                      this.dataForm.deptId = data.user.deptId
                      this.dataForm.jobNumber = data.user.jobNumber
                      this.dataForm.userName = data.user.username;
                      this.dataForm.salt = data.user.salt;
                      this.dataForm.email = data.user.email;
                      this.dataForm.mobile = data.user.mobile;
                      this.dataForm.roleIdList = data.user.roleIdList;
                      this.dataForm.status = data.user.status;
                      this.dataForm.name =  data.user.name;
                      this.dataForm.avatar =  data.user.avatar;
                      this.dataForm.orgId = data.user.orgId;
                      this.dataForm.userId  = data.user.userId;
                      this.dataForm.dataPermission = data.user.dataPermission
                      this.dataForm.type = data.user.type
                      this.dataForm.customerUrl = data.user.customerUrl
                      this.dataForm.relationId = data.user.relationId
                      this.dataForm.loginLimit = data.user.loginLimit
                      this.menuListTreeSetCurrentNode();
                    }else{
                      this.$message.error(data.msg)
                    }
                  })
                }
              })
          })
      })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/sys/user/${!this.dataForm.userId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'username': this.dataForm.userName,
                'password': this.dataForm.password,
                'salt': this.dataForm.salt,
                'email': this.dataForm.email,
                'mobile': this.dataForm.mobile,
                'status': this.dataForm.status,
                'roleIdList': this.dataForm.roleIdList,
                'name':this.dataForm.name,
                'avatar':this.dataForm.avatar,
                'orgId':this.dataForm.orgId,
                'userId': this.dataForm.userId || undefined,
                'deptId': this.dataForm.deptId,
                'jobNumber': this.dataForm.jobNumber,
                'type':this.dataForm.type,
                'dataPermission': this.dataForm.dataPermission,
                'customerUrl':this.dataForm.customerUrl,
                'relationId':this.dataForm.relationId,
                'loginLimit': this.dataForm.loginLimit
              })
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.$emit('back', {refresh: true})
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 上传之前
      beforeUploadHandle(file) {
        if (
          file.type !== "image/jpg" &&
          file.type !== "image/jpeg" &&
          file.type !== "image/png" &&
          file.type !== "image/gif"
        ) {
          this.$message.error("只支持jpg、png、gif格式的图片！");
          return false;
        }
        this.num++;
      },
      uploadSectionFile: function(param) {
        let fileObj = param.file;
        let form = new FormData();
        form.append("file", fileObj);
        let headers = { headers: { "Content-Type": "multipart/form-data" } };
        this.$http({
          url: this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`),
          method: "post",
          data: form,
          headers: headers
        }).then(({ data }) => {
          param.onSuccess(data);
        });
      },
      // 图片上传成功
      handleAvatarSuccess(response, file, fileList) {
        //  this.fileList = fileList;
        this.dataForm.avatar = response.url
      },
      //节点选择
      handleNodeClick(data) {
        this.dataForm.deptId = data.id
      },
      // 菜单树选中
      menuListTreeCurrentChangeHandle(data, node) {
        console.log(data)
        this.dataForm.deptId = data.id;
        this.dataForm.deptName = data.name;
      },
      // 菜单树设置当前选中节点
      menuListTreeSetCurrentNode() {
        this.$refs.deptListTree.setCurrentKey(this.dataForm.deptId);
        this.dataForm.deptName = (this.$refs.deptListTree.getCurrentNode() ||
          {})["name"];
      },
    }
  }
</script>
<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409eff;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
