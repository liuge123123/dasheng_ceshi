<template>
  <div>
    <div class="container">
      <div ref="toolbar" class="toolbar">
      </div>
      <div ref="text" id="div2" class="text">
      </div>
    </div>
    <div style="z-index: 10003">
          <img-list-picker ref="imgListPicker" @selected="imgSelected"></img-list-picker>
         <video-upload-dialog  ref="videoListPicker" @success="videoSelected"></video-upload-dialog>
    </div>
  </div>
</template>
<script>
import E from 'wangeditor'
const { $, BtnMenu, DropListMenu, PanelMenu, DropList, Panel, Tooltip } = E
import videoUploadDialog from "../upload/video-upload-dialog";

export default {
  name: "weditor",
  components:{
    videoUploadDialog
  },
  props: {
    value: {
      //回显数据
      type: String,
      default: ""
    }
  }, //回调方法
  data() {
    return {
      editor: null,
      content: ''
    }
  },
  watch: {
    value(val) {
      if (val && val != this.content) {
        this.editor.txt.html(val);
      }
      if(!val){
        this.editor.txt.html('');
      }
    }
  },
  computed: {},
  mounted() {
    let _this = this
    class MyUploadVideo extends BtnMenu {
      constructor(editor) {
        // data-title属性表示当鼠标悬停在该按钮上时提示该按钮的功能简述
        const $elem = E.$(
          `<div class="w-e-menu" data-title="视频"><i class="w-e-icon-play"></i></div>`
        )
        super($elem, editor)
      }
      // 菜单点击事件
      clickHandler() {
        _this.$refs.videoListPicker.pickFileHandle()
      }
      tryChangeActive() {
        this.active()
      }
    }
    const menuKey = 'MyUploadVideo'
     this.editor = new E(this.$refs.toolbar, this.$refs.text)
    this.editor.config.pasteFilterStyle = true //从其他地方（如网页、word 等）复制文本到编辑器中，编辑器会默认过滤掉复制文本的样式，这样可以让编辑器内容更加简洁。因为复制过来的文本样式，可能会比较混乱，且不可控。
    this.editor.menus.extend(menuKey, MyUploadVideo)
    this.editor.config.menus.splice(18, 0, menuKey)

    //增加源码菜单
    // 第一，菜单 class ，Button 菜单继承 BtnMenu class 增加源码菜单
    class AlertMenu extends BtnMenu {
      constructor(editor) {
        const $elem = E.$(
          `<div class="w-e-menu"  data-title="源码">
               <i class="el-icon-edit"></i>
            </div>`
        )
        super($elem, editor)
      }
      // 菜单点击事件
      clickHandler() {
        // 做任何你想做的事情
        // 可参考【常用 API】文档，来操作编辑器
        _this.showSource()
        this.tryChangeActive()
      }
      // 菜单是否被激活（如果不需要，这个函数可以空着）
      // 1. 激活是什么？光标放在一段加粗、下划线的文本时，菜单栏里的 B 和 U 被激活，如下图
      // 2. 什么时候执行这个函数？每次编辑器区域的选区变化（如鼠标操作、键盘操作等），都会触发各个菜单的 tryChangeActive 函数，重新计算菜单的激活状态
      tryChangeActive() {
        if (_this.isHTML) this.active()
        else this.unActive()
      }
    }
     // 第二步注册菜单
    const almenuKey = 'alertMenuKey' // 菜单 key ，各个菜单不能重复
    this.editor.menus.extend('alertMenuKey', AlertMenu)
    // 将菜单加入到 editor.config.menus 中
    // 也可以通过配置 menus 调整菜单的顺序，参考【配置菜单】部分的文档
    this.editor.config.menus = this.editor.config.menus.concat(almenuKey)

    this.editor.config.uploadImgFromMedia = () => {
      this.$refs.imgListPicker.pickFileHandle()
    }
    this.editor.config.excludeMenus = [
      'video'
    ]
    this.editor.config.customUploadVideo=()=>{
       this.$refs.videoListPicker.pickFileHandle()
    }
    this.editor.config.onchange = (newHtml) => {
      this.content = newHtml
      this.$emit('input', this.content)
    }
    this.editor.create()

    this.content = this.value;
    if ( this.content &&  this.content != '') {
      this.editor.txt.html(this.content);
    }
  },
  methods: {
    imgSelected(val){
      val.forEach(item => {
        this.editor.cmd.do( 'insertHTML', `<img src="${item}" style="max-width:100%;"/>` )
      })
    },
    videoSelected(val){
      this.editor.cmd.do('insertHTML', `<video src="${val}" controls style="max-width:100%;width:100%"/>` );
    },
    showSource () {
      let _this = this
      let _editor = _this.editor
      _this.isHTML = !_this.isHTML
      let _source = _editor.txt.html()
      if (_this.isHTML) {
        _source = _source.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/ /g, "&nbsp;")
      } else {
        _source = _editor.txt.text().replace(/&lt;/ig, "<").replace(/&gt;/ig, ">").replace(/&nbsp;/ig, " ")
      }
      _editor.txt.html(_source)
    }
  }
}
</script>

<style scoped lang="scss">
.container {
  border: 1px solid #EEEEEE;
  z-index: 1;
  position: relative;
  .toolbar {
    border-bottom: 1px solid #EEEEEE;
  }

  .text {
    min-height: 650px;
    /deep/ img{
      max-width: 100%;
    }
  }
}
</style>
