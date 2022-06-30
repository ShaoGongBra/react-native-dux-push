// main index.js

import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  PermissionsAndroid,
  Alert,
} from 'react-native';

const RNDuxPush = NativeModules.RNDuxPush;
import PushNotificationIOS from '@react-native-community/push-notification-ios';

class DuxPush extends NativeEventEmitter {

  // 构造
  constructor(props) {
    super(RNDuxPush);
    // 初始状态
    this.state = {};
  }

  init(appkey, secret, data) {
    if (Platform.OS == 'android') {
      RNDuxPush.init(appkey, secret, data);
    }
  }

  /**
   * 判断当前是否打开了通知权限
   * @returns
   */
  notificationsEnabled() {
    return RNDuxPush.notificationsEnabled();
  }

  /**
   * 去打开通知
   */
  goPushSetting() {
    RNDuxPush.goPushSetting();
  }

  /**
   * 设置别名
   * @param text
   */
  setAlias(text) {
    return RNDuxPush.setAlias(text);
  }

  /**
   * 注销别名
   * @param text
   */
  unsetAlias(text) {
    return RNDuxPush.unsetAlias(text);
  }

  /**
   * 设置标签
   * @param text
   */
  setTag(text) {
    return RNDuxPush.setTag(text);
  }

  /**
   * 注销标签
   * @param text
   */
  unsetTag(text) {
    return RNDuxPush.unsetTag(text);
  }

  notify(title, body) {
    if (Platform.OS == 'ios') {
      PushNotificationIOS.addNotificationRequest({
        id: '1',
        title: title,
        body: body,
      });
    } else {
      RNDuxPush.notify(title, body);
    }
  }

  /**
   *
   * @param type
   * ios :
   * notification => 监听收到apns通知
   * localNotification => 监听收到本地通知
   * register => 注册deviceToken 通知
   *
   * android :
   * duxpush_notify => 监听收到推送
   * duxpush_click => 监听推送被点击
   * @param handler
   */
  addEventListener(type, handler) {
    if (Platform.OS == 'ios') {
      switch (type) {
        case 'notification':
        case 'duxpush_notify':
          if (type == 'duxpush_notify') {
            type = 'notification';
          }
          PushNotificationIOS.addEventListener(type, (res) => {
            handler && handler(res._data);
          });
          break;
        case 'localNotification':
        case 'register':
          PushNotificationIOS.addEventListener(type, handler);
          break;
        default:
          this.addListener(type, handler);
          break;
      }
    } else {
      this.addListener(type, (res) => {
        handler && handler(JSON.parse(res));
      });
    }
  }

  removeEventListener(type) {
    if (Platform.OS == 'ios') {
      switch (type) {
        case 'notification':
        case 'localNotification':
        case 'register':
          PushNotificationIOS.removeEventListener(type);
          break;
        default:
          this.removeListener(type);
          break;
      }
    } else {
      this.removeListener(type);
    }
  }

  /**
   * 通过点击通知启动app
   * @param handler
   */
  getInitialNotification(handler) {
    if (Platform.OS == 'ios') {
      PushNotificationIOS.getInitialNotification()
        .then((res) => {
          if (res && res._data) {
            handler && handler(res._data);
          }
        });
    } else {
      // RNDuxPush.getInitialNotification()
      //     .then(handler);
    }
  }
}

module.exports = new DuxPush();
