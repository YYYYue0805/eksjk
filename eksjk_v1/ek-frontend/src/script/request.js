import axios from 'axios'
import JsEncrypt from 'jsencrypt'

axios.defaults.baseURL = 'api'
axios.interceptors.request.use((config) => {
  let regex = /.*csrftoken=([^;.]*).*$/
  config.headers['X-CSRFToken'] = document.cookie.match(regex) === null ? null : document.cookie.match(regex)[1]
  return config
})

// 数据请求接口
class Request {
  // 错误码
  Code = {
    // 正常
    OK: 0,
    // 未登录，无权限
    UNAUTHORIZED: 1,
    // 用户名或者密码错误
    USERNAME_PASSWORD_ERROR: 2,
    // 请求方法不存在
    BAD_METHOD: 3,
    // 数据解析错误
    WRONG_ARGUMENTS: 4,
    // 缺少必需参数-
    MISSING_REQUIRED_ARGUMENTS: 5,
    // 资源不存在
    RESOURCE_NOT_EXIST: 6,
    // 资源已存在
    RESOURCE_IS_EXIST: 7,
    // 权限不足
    PERMISSION_DENIED: 8,
    // 其他
    OTHER: 100
    
  }


  // 上传的信息记录，用于计算总进度和速度
  lastUploadProgress = {
    loaded: 0,
    total: 0,
    startTime: null,
    time: null
  }

  // 敏感数据，非对称加密
  encrypt = new JsEncrypt()
  // 验证码的key
  verifyImageKey = null

  onUnauthorized = null

  // 初始化，获取csrftoken和publicKey
  constructor() {
    this._get('login/', null, key => {
      this.encrypt.setPublicKey(key)
    })
  }

  setUnauthorizedCallback (callback) {
    this.onUnauthorized = callback
  }

  // 用户登录
  login (username, password, onSuccess, onError) {
    let items = [username, password]
    let data = new FormData()
    const word = this.encrypt.encrypt(items.join('"`"'))
    data.append('word', word)
    this._post('login/dologin', data, onSuccess, onError)
  }

    //获取用户信息
  userInfo(data, onSuccess, onError){
    this._get('login/user',data, onSuccess, onError)
  }

  // 用户登出
  logout (onSuccess, onError) {
    this._post('login/logout', null, onSuccess, onError)
  }

  //获取用户列表
  getUserList (data, onSuccess, onError){
    this._get('login/userList',data, onSuccess, onError)
  }

  //添加用户
  insertUser(data, onSuccess, onError){
    this._put('login/user',data, onSuccess, onError)
  }

  //修改用户状态
  userStatus(data, onSuccess, onError){
    this._post('login/userStatus',data, onSuccess, onError)
  }

  //删除用户
  deleteUser(data, onSuccess, onError){
    this._delete('login/user',data, onSuccess, onError)
  }

  //添加/修改单位
  insertUnit(data, onSuccess, onError){
    this._put('login/unit',data, onSuccess, onError)
  }

  //查询单位详情
  getUnitInfo(data, onSuccess, onError){
    this._get('login/unit',data, onSuccess, onError)
  }

  //删除单位
  deleteUnit(data, onSuccess, onError){
    this._delete('login/unit',data, onSuccess, onError)
  }


  //修改用户信息
  updateUser(data, onSuccess, onError){
    this._post('login/user',data, onSuccess, onError)
  }

  // 获取病案列表
  getCaseList (data, onSuccess, onError) {
    this._get('datamain/caseList/', data, onSuccess, onError)
  }

  //获取天元公学的上传数据
  getStudentList (data, onSuccess, onError){
    this._get('school/studentList/',data, onSuccess, onError)
  }
  //获取查询日志列表
  getLogList (data, onSuccess, onError){
    this._get('datamain/modifylogList/',data, onSuccess, onError)
  }
  //获取天元公学的上传单条数据
  getStudent(data, onSuccess, onError){
    this._get('school/student/',data, onSuccess, onError)
  }
  //添加病例
  addCase(data, onSuccess, onError){
    this._put('datamain/patient/',data, onSuccess, onError)
  }

  //添加病例随访信息
  addFollow(data, onSuccess, onError){
    this._put('datamain/follow/',data, onSuccess, onError)
  }

  //查询病例随访信息
  followDetail(data, onSuccess, onError){
    this._get('datamain/follow/',data, onSuccess, onError)
  }

  //删除随访信息
  delFollow(data, onSuccess, onError){
    this._delete('datamain/follow/',data, onSuccess, onError)
  }

  //获取病例随访信息
  getFollow(data, onSuccess, onError){
    this._get('datamain/followList/',data, onSuccess, onError)
  }

  //获取病例随访信息不分页
  allFollow(data, onSuccess, onError){
    this._get('datamain/followListNo/',data, onSuccess, onError)
  }

  //删除病例
  delCase(data, onSuccess, onError){
    this._delete('datamain/patient/',data, onSuccess, onError)
  }

  //查询病例动态数据
  getCase(data, onSuccess, onError){
    this._get('datamain/case/',data, onSuccess, onError)
  }

  //查询病例固定数据
  getCaseDetail(data, onSuccess, onError){
    this._get('datamain/patient/',data, onSuccess, onError)
  }

  //查询mas随访信息
  getMasFollow(data, onSuccess, onError){
    this._get('datamain/masFollow/',data, onSuccess, onError)
  }

  //根据id模糊查询
  getIdList(data, onSuccess, onError){
    this._get('datamain/patientNum/',data, onSuccess, onError)
  }

    //根据id查询病例固定数据
  getPatientById(data, onSuccess, onError){
    this._get('datamain/patientNumJq/',data, onSuccess, onError)
  }

  //查询单位列表信息
  getUnitList(data, onSuccess, onError){
    this._get('login/unitList',data, onSuccess, onError)
  }

  //获取所有单位
  getAllUnit(data, onSuccess, onError){
    this._get('login/unitAll',data, onSuccess, onError)
  }

  //获取百分位数标准差
  getSDS(data, onSuccess, onError){
    this._get('datamain/getSDS',data, onSuccess, onError)
  }

  //获取统计每家医院的上传数量
  getStatisticPosi (data, onSuccess, onError){
    this._get('datamain/statisticPosi/',data, onSuccess, onError)
  }

  // 下载压缩包
  getZipFile (data, onSuccess, onError) {
    this._put('datamain/downZip', data, (info) => {
      // 兼容调试环境
      let headPath = ''
      if (process.env.NODE_ENV != 'production') {
        const origin = window.location.origin
        headPath = origin.substring(0, origin.lastIndexOf(':')) + '/'
      }

      // 生成文件下载url
      const url = headPath
        + axios.defaults.baseURL
        + `/datamain/downZip/${info.organ}/${data.queryId}/${info.filename}`
      
      onSuccess && onSuccess(url)
    }, onError)
  }
  // 批量下载
  getZipPl (data, onSuccess, onError) {
    this._put('datamain/downZipPl', data, (info) => {
      // 兼容调试环境
      let headPath = ''
      if (process.env.NODE_ENV != 'production') {
        const origin = window.location.origin
        headPath = origin.substring(0, origin.lastIndexOf(':')) + '/'
      }

      // 生成文件下载url
      const url = headPath
        + axios.defaults.baseURL
        + `/datamain/downZipPl/${info.filename}`

      onSuccess && onSuccess(url)
    }, onError)
  }

  // 导出报告
  loadFile (data, onSuccess, onError) {
    this._put('datamain/loadFile', data, (info) => {
      // 兼容调试环境
      let headPath = ''
      if (process.env.NODE_ENV != 'production') {
        const origin = window.location.origin
        headPath = origin.substring(0, origin.lastIndexOf(':')) + '/'
      }
      // 生成文件下载url
      const url = headPath
        + axios.defaults.baseURL
        + `/datamain/loadFile/${info.filename}`

      onSuccess && onSuccess(url)
    }, onError)
  }

  // 文件上传
  upload (data, onProgress, onSuccess, onError) {
    this.lastUploadProgress.loaded = 0
    this.lastUploadProgress.startTime = new Date().getTime()
    this.lastUploadProgress.time = this.lastUploadProgress.startTime

    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      // 计算上传进度和速度
      onUploadProgress: (event) => {
        const loaded = event.loaded
        const total = event.total
        const now = new Date().getTime()
        const duration = (now - this.lastUploadProgress.time) * 1e-3
        const speed = (loaded - this.lastUploadProgress.loaded) / duration
        onProgress && onProgress({
          percent: loaded * 100 / total | 0,
          speed
        })
        
        this.lastUploadProgress.loaded = loaded
        this.lastUploadProgress.total = total / 1024
        this.lastUploadProgress.time = now
      }
    }

    axios.post('datamain/image', data, config).then(response => {
      if (response.data.code == this.Code.OK) {
        onSuccess(response.data)
      } else {
        onError(response.data)
      }
    }).catch(error => {
      onError(error)
    })
  }

  deleteImage (data, onSuccess, onError) {
    this._delete('datamain/image', data, onSuccess, onError)
  }

  getMask(data, onSuccess, onError){
    this._get('datamain/mask', data, onSuccess, onError)
  }

  upMask(data, onSuccess, onError) {

    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    }
      axios.post('datamain/mask', data, config).then(response => {
        if (response.data.code == this.Code.OK) {
          onSuccess(response.data)
        } else {
          onError(response.data)
        }
      }).catch(error => {
        onError(error)
      })
    }

  _get (endPoint, data, onSuccess, onError) {
    let promise = axios.get(endPoint, {
      params: data
    })

    this.handleResult(promise, onSuccess, onError)
  }

  _post (endPoint, data, onSuccess, onError) {
    let promise = axios.post(endPoint, data, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })

    this.handleResult(promise, onSuccess, onError)
  }

  _put (endPoint, data, onSuccess, onError) {
    let promise = axios.put(endPoint, data)

    this.handleResult(promise, onSuccess, onError)
  }

  _delete (endPoint, data, onSuccess, onError) {
    let promise = axios.delete(endPoint, {
      params: data
    })

    this.handleResult(promise, onSuccess, onError)
  }

  handleResult (promise, onSuccess, onError) {
    promise.then(response => {
      let message = response.data
      if (message.code == 0) {
        onSuccess && onSuccess(message.data)
      } else {
        this.handleError(message, onError)
      }
    }).catch(error => {
      if (error.response) {
        this.handleError(error.response.data, onError)
      } else {
        onError && onError(error)
      }
    })
  }

  handleError (error, onError) {
    if (error.code == this.Code.UNAUTHORIZED) {
      this.onUnauthorized && this.onUnauthorized()
    } else if (onError) {
      onError(error)
    }
  }
}

export default new Request()
