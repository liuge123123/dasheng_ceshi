<template>
  <div class="main-wrap">
    <!-- 这里设置了ref属性后，在vue组件中，就可以用this.$refs.audio来访问该dom元素 -->
    <audio ref="audio" class="dn"
           :src="url" :preload="audio.preload"
           @play="onPlay"
           @error="onError"
           @waiting="onWaiting"
           @pause="onPause"
           @timeupdate="onTimeupdate"
           @loadedmetadata="onLoadedmetadata"
           @ended="playEndedHandler"
    ></audio>
    <div class="control">
      <div class="icon" @click="startPlayOrPause">
        <icon-svg :name="audio.playing | transPlayPause"></icon-svg>
      </div>
      <div class="time">{{ audio.currentTime | formatSecond}} / {{ audio.maxTime | formatSecond }}</div>
      <div v-if="fileSize != 0">{{size}}</div>
    </div>
  </div>
</template>

<script>
  function realFormatSecond(second) {
    var secondType = typeof second

    if (secondType === 'number' || secondType === 'string') {
      second = parseInt(second)

      var hours = Math.floor(second / 3600)
      second = second - hours * 3600
      var mimute = Math.floor(second / 60)
      second = second - mimute * 60

      return hours + ':' + ('0' + mimute).slice(-2) + ':' + ('0' + second).slice(-2)
    } else {
      return '0:00:00'
    }
  }

  export default {
    props: {
      theUrl: {
        type: String,
        required: true,
      },
      theSpeeds: {
        type: Array,
        default () {
          return [1, 1.5, 2]
        }
      },
      theControlList: {
        type: String,
        default: ''
      },
      fileSize: {
        type: Number,
        default: 0
      }
    },
    name: 'VueAudioSimple',
    data() {
      return {
        audio: {
          currentTime: 0,
          maxTime: 0,
          playing: false,
          muted: false,
          speed: 1,
          waiting: false,
          preload: 'auto'
        },
        playlist:[], //播放列表

        sliderTime: 0,
        volume: 100,
        speeds: this.theSpeeds,
        index:0,

        controlList: {
          // 不显示下载
          noDownload: false,
          // 不显示静音
          noMuted: true,
          // 不显示音量条
          noVolume: true,
          // 不显示进度条
          noProcess: false,
          // 只能播放一个
          onlyOnePlaying: false,
          // 不要快进按钮
          noSpeed: true
        }
      }
    },
    computed: {
      url(){
          if(this.theUrl.indexOf(';')>0){
            this.playlist=this.theUrl.split(';')
            this.index++
            return this.playlist[0]
          }
          else{
            this.playlist.push(this.theUrl)
            return this.theUrl
          }
      },
      size(){
        let val = this.fileSize
        if(val/1024/1024/1024 >= 1){
          return (val/1024/1024/1024).toFixed(2) + 'G'
        }else if(val/1024/1024 >= 1){
          return (val/1024/1024).toFixed(2) + 'M'
        }else if(val/1024 >= 1){
          return (val/1024).toFixed(2) + 'KB'
        }else{
          return val + 'B'
        }
      }
    },
    methods: {
      setControlList () {
        let controlList = this.theControlList.split(' ')
        controlList.forEach((item) => {
          if(this.controlList[item] !== undefined){
            this.controlList[item] = true
          }
        })
      },
      changeSpeed() {
        let index = this.speeds.indexOf(this.audio.speed) + 1
        this.audio.speed = this.speeds[index % this.speeds.length]
        this.$refs.audio.playbackRate = this.audio.speed
      },
      startMutedOrNot() {
        this.$refs.audio.muted = !this.$refs.audio.muted
        this.audio.muted = this.$refs.audio.muted
      },
      // 音量条toolTip
      formatVolumeToolTip(index) {
        return '音量条: ' + index
      },
      // 进度条toolTip
      formatProcessToolTip(index = 0) {
        index = parseInt(this.audio.maxTime / 100 * index)
        return '进度条: ' + realFormatSecond(index)
      },
      // 音量改变
      changeVolume(index = 0) {
        this.$refs.audio.volume = index / 100
        this.volume = index
      },
      // 播放跳转
      changeCurrentTime(index) {
        this.$refs.audio.currentTime = parseInt(index / 100 * this.audio.maxTime)
      },
      startPlayOrPause() {
        return this.audio.playing ? this.pausePlay() : this.startPlay()
      },
      // 开始播放
      startPlay() {
        this.$refs.audio.play()
      },
      // 暂停
      pausePlay() {
        this.$refs.audio.pause()
      },
      // 当音频暂停
      onPause () {
        this.audio.playing = false
      },
      // 当发生错误, 就出现loading状态
      onError () {
        this.audio.waiting = true
      },
      //播放完成
      playEndedHandler()
      {
        if(this.playlist.length>this.index){
          this.$refs.audio.src=this.playlist[this.index];
          this.index++
          this.$refs.audio.play();
        }
        else{
          this.index=0;
          this.$refs.audio.src=this.playlist[this.index];
          this.index++;
        }

      },
      // 当音频开始等待
      onWaiting (res) {
        // console.log(res)
      },
      // 当音频开始播放
      onPlay (res) {
        // console.log(res)
        this.audio.playing = true
        this.audio.loading = false

        if(!this.controlList.onlyOnePlaying){
          return
        }

        let target = res.target

        let audios = document.getElementsByTagName('audio');

        [...audios].forEach((item) => {
          if(item !== target){
            item.pause()
          }
        })
      },
      // 当timeupdate事件大概每秒一次，用来更新音频流的当前播放时间
      onTimeupdate(res) {
        // console.log('timeupdate')
        // console.log(res)
        this.audio.currentTime = res.target.currentTime
        this.sliderTime = parseInt(this.audio.currentTime / this.audio.maxTime * 100)
      },
      // 当加载语音流元数据完成后，会触发该事件的回调函数
      // 语音元数据主要是语音的长度之类的数据
      onLoadedmetadata(res) {
        // console.log('loadedmetadata')
        // console.log(res)
        this.audio.waiting = false
        this.audio.maxTime = parseInt(res.target.duration)
      }
    },
    filters: {
      formatSecond(second = 0) {
        return realFormatSecond(second)
      },
      transPlayPause(value) {
        return value ? 'pause' : 'play'
      },
      transMutedOrNot(value) {
        return value ? '放音' : '静音'
      },
      transSpeed(value) {
        return '快进: x' + value
      }
    },
    created() {
      this.setControlList()
    }
  }

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
  .main-wrap{
    background-color: #eee;
    width: 240px;
    border-radius: 5px;
    margin: 4px;
    .control{
      font-size: 14px;
      display: flex;
      line-height: 28px;
      color: #409eff;
      *{
        padding: 0 5px;
      }
      .progress{
        flex: 1;
        padding: 0 10px;
      }
      .play, .download{
        cursor: pointer;
      }
      .icon{
        display: flex;
        flex-direction: column;
        justify-content: center;
        font-size: 28px;
        text-decoration: none;
        cursor: pointer;
      }
    }
  }

</style>
