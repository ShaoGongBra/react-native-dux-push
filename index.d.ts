interface InitDisable {
  /**
   * 是否禁用华为推送
   * @default false
  */
  hms?: boolean
  /**
   * 是否禁用OPPO推送
   * @default false
  */
  oppo?: boolean
  /**
   * 是否禁用VIVO推送
   */
  vivo?: string

  /**
   * 友盟平台appkey
   */
  umeng: string
  /**
   * 友盟平台channel
   */
  channel: string
}

namespace DuxPush {

  /**
   * 初始化推送
   * @param appid 项目的appid
   * @param secret 可不传
   */
  function init(appid: string, secret?: string, disable?: InitDisable): void

  /**
   * 设置别名
   * @param text 名称
   */
  function setAlias(text: string): void

  /**
   * 取消设置别名
   * @param text 名称
   */
  function unsetAlias(text: string): void

  /**
   * 设置标签
   * @param text 名称
   */
  function setTag(text: string): void

  /**
   * 取消设置标签
   * @param text 名称
   */
  function unsetTag(text: string): void

  /**
   * 发送本地通知
   * @param title 消息标题
   * @param body 消息内容
   */
  function notify(title: string, body: string): void
  
  /**
   * 判断是否开启了消息通知
   */
  function notificationsEnabled(): Promise

  /**
   * 跳转到开启消息通知页面
   */
  function goPushSetting(): void
}

export default DuxPush