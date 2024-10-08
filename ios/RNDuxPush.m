#import "RNDuxPush.h"

#import <UMPush/UMessage.h>
#import <UMCommon/UMCommon.h>

@implementation RNDuxPush

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(init:(NSString *)umAppKey channel:(NSString *)channel)
{
    /* 设置友盟appkey */
    [UMConfigure initWithAppkey:umAppKey channel:channel];
}

RCT_EXPORT_METHOD(setAlias:(NSString *)text)
{

    [UMessage setAlias:text type:@"default" response:^(id  _Nullable responseObject, NSError * _Nullable error) {

    }];
}

RCT_EXPORT_METHOD(unsetAlias:(NSString *)text)
{
    [UMessage removeAlias:text type:@"default" response:^(id  _Nullable responseObject, NSError * _Nullable error) {

    }];
}

RCT_EXPORT_METHOD(setTag:(NSString *)text)
{

    [UMessage addTags:text response:^(id  _Nullable responseObject, NSInteger remain, NSError * _Nullable error) {

    }];
}

RCT_EXPORT_METHOD(unsetTag:(NSString *)text)
{

    [UMessage deleteTags:text response:^(id  _Nullable responseObject, NSInteger remain, NSError * _Nullable error) {

    }];
}

RCT_EXPORT_METHOD(notificationsEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    if (@available(iOS 10 , *))
    {
        [[UNUserNotificationCenter currentNotificationCenter] getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            if (settings.authorizationStatus == UNAuthorizationStatusDenied ||
                settings.authorizationStatus == UNAuthorizationStatusNotDetermined)
            {
                /// 没有权限
                resolve(@(NO));
            } else {
                ///已经开启通知权限
                resolve(@(YES));
            }

        }];
    } else {
        UIUserNotificationSettings * setting = [[UIApplication sharedApplication] currentUserNotificationSettings];
        if (setting.types == UIUserNotificationTypeNone) {
            ///没有权限
            resolve(@(NO));
        } else {
            ///已经开启通知权限
            resolve(@(YES));
        }
    }
}

RCT_EXPORT_METHOD(goPushSetting) {
    NSString *urlStr = [[[UIDevice currentDevice] systemVersion] floatValue] >= 10.0 ? @"App-Prefs:root=NOTIFICATIONS_ID" : @"prefs:root=SETTING";

    NSURL * url = [NSURL URLWithString:urlStr];
    if ([[UIApplication sharedApplication] canOpenURL:url]) {
      [[UIApplication sharedApplication] openURL:url];
    }
}

- (void)miPushRequestSuccWithSelector:(NSString *)selector data:(NSDictionary *)data
{
    NSLog(@"data:%@", data);
}

- (void)miPushRequestErrWithSelector:(NSString *)selector error:(int)error data:(NSDictionary *)data
{
    // 请求失败
    NSLog(@"error:%@", error);
}

- (void)miPushReceiveNotification:(NSDictionary *)data {
    NSLog(@"data2:%@", data);
}

+ (void)application:(id)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    // Push组件基本功能配置
    UMessageRegisterEntity * entity = [[UMessageRegisterEntity alloc] init];
    //type是对推送的几个参数的选择，可以选择一个或者多个。默认是三个全部打开，即：声音，弹窗，角标
    entity.types = UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionSound|UMessageAuthorizationOptionAlert;
    [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity completionHandler:^(BOOL granted, NSError * _Nullable error) {

    }];
    NSLog(@"注册");
}

+ (void)application:(id)application didRegisterUserNotificationSettings:(id)notificationSettings {

    [RNCPushNotificationIOS didRegisterUserNotificationSettings:notificationSettings];
}

+ (void)application:(id)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {

    if(![deviceToken isKindOfClass:[NSData class]]) return;
    const unsigned *tokenBytes =(const unsigned*)[deviceToken bytes];
    NSString *hexToken =[NSString stringWithFormat:@"%08x%08x%08x%08x%08x%08x%08x%08x",
                          ntohl(tokenBytes[0]), ntohl(tokenBytes[1]), ntohl(tokenBytes[2]),
                          ntohl(tokenBytes[3]), ntohl(tokenBytes[4]), ntohl(tokenBytes[5]),
                          ntohl(tokenBytes[6]), ntohl(tokenBytes[7])];
    NSLog(@"deviceToken:%@",hexToken);
    [RNCPushNotificationIOS didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

+ (void)application:(id)application didReceiveRemoteNotification:(NSDictionary *)notification {

    [UMessage setAutoAlert:NO];
    if([[[UIDevice currentDevice] systemVersion]intValue]<10){
        [UMessage didReceiveRemoteNotification:notification];
    }

    [RNCPushNotificationIOS didReceiveRemoteNotification:notification];
}

+ (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {

    [RNCPushNotificationIOS didReceiveLocalNotification:notification];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"duxpush_click" object:notification.userInfo];
}

// 应用在前台收到通知
+ (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    [UMessage setAutoAlert:NO];
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {

        [RNCPushNotificationIOS didReceiveRemoteNotification:userInfo];
    } else {
        completionHandler(UNNotificationPresentationOptionSound | UNNotificationPresentationOptionAlert | UNNotificationPresentationOptionBadge);
    }
}

// 点击通知进入应用
+ (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler {
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [RNCPushNotificationIOS didReceiveRemoteNotification:userInfo];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"duxpush_click" object:userInfo];
    completionHandler();
}

- (void) handleSend:(NSNotification *)notification {

    [self sendEventWithName:notification.name body:notification.object];
}

- (void)startObserving
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleSend:)
                                                 name:@"duxpush_click"
                                               object:nil];

}

- (void)stopObserving
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents {
    NSMutableArray *arr = [[NSMutableArray alloc] init];

    [arr addObject:@"duxpush_click"];

    return arr;
}

@end
