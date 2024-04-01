class MqUtils {
  url = 'ws://127.0.0.1:15674/ws';
  userName = 'guest';
  password = 'guest';
  host = '/';
  reconnectTimes = -1;
  tryReconnectTimes = 0;
  ws = null;
  msgClient = null;
  subscribes = [];
  connected = false;
  reconnectListener = null;

  /**
   * 构造函数
   * @param url
   * @param userName
   * @param password
   * @param host
   */
  constructor(url, userName, password, host) {
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.host = host;
  }

  initWs(){
    this.tryReconnectTimes = 0;
    this.connectWs();
  }

  /**
   * 连接websocket
   */
  connectWs() {
    const _this = this;
    _this.ws = new WebSocket(_this.url);
    _this.msgClient = Stomp.over(_this.ws);
    _this.msgClient.connect(_this.userName, _this.password, () => {
      console.log("ws连接成功");
      _this.connected = true;
      _this.tryReconnectTimes = 0; //重置重连次数
      _this.reconnectEvent(); // 回调重连成功
      _this.subscribes.forEach(item => { //添加队列监听
        item.msgSubscribe = _this.msgClient.subscribe(item.queue, data => {
          item.callback(data);
        });
      });
    }, () => {
      console.log("ws连接失败");
      _this.connected = false;
      if (_this.reconnectTimes == -1 || _this.tryReconnectTimes < _this.reconnectTimes) { //小于允许重连次数
        _this.tryReconnectTimes++; //尝试重连次数增加
        _this.reconnectWs();
      }else{
        alert("由于网络原因导致消息服务断开连接，请退出系统重新登录！");
      }
    }, this.host);
  }

  /**
   * 重连
   */
  reconnectWs() {
    const _this = this;
    setTimeout(() => { //每次重连之后间隔5s，再次重连
      console.log(`ws第${_this.tryReconnectTimes}次尝试重连`);
      _this.reconnectEvent();
      _this.connectWs();
    }, 5 * 1000);
  }

  /**
   * 重连事件
   */
  reconnectEvent(){
    if(this.reconnectListener){
      this.reconnectListener(this.tryReconnectTimes);
    }
  }

  /**
   * 断开链接
   */
  disconnectWs() {
    const _this = this;
    _this.subscribes.forEach(item => {
      if (item.msgSubscribe) {
        item.msgSubscribe.unsubscribe();
      }
    })
    _this.subscribes = [];
    _this.msgClient.disconnect(() => {
      console.log("消息服务关闭");
      _this.ws.close();
      _this.ws = null;
      _this.msgClient = null;
      _this.connected = false;
    })
  }

  /**
   * 监听指定队列
   * @param queue
   * @param callback
   * @returns {*}
   */
  subscribe(id, queue, callback) {
    const _this = this;
    for (let i = 0; i < _this.subscribes.length; i++) {
      let item = _this.subscribes[i];
      if (item.id == id) {
        console.error(`${id}已存在监听，请不要重复添加`);
        return;
      }
    }
    let msgSubscribe = null;
    if(_this.connected) {
      msgSubscribe = _this.msgClient.subscribe(queue, (data) => {
        callback(data);
      });
    }

    _this.subscribes.push({
      id: id,
      queue: queue,
      callback: callback,
      msgSubscribe: msgSubscribe
    });
    return msgSubscribe;
  }

  /**
   * 取消监听队列
   * @param queue
   */
  unSubscribe(id) {
    const _this = this;
    for (let i = 0; i < _this.subscribes.length; i++) {
      let item = _this.subscribes[i];
      if (item.id == id) {
        if (item.msgSubscribe) {
          item.msgSubscribe.unsubscribe();
        }
        _this.subscribes.splice(i, 1);
        return;
      }
    }
  }

  /**
   * 设置重连监听
   * @param reconnectListener
   */
  setReconnectListener(reconnectListener){
    this.reconnectListener = reconnectListener;
  }
}

export default MqUtils

