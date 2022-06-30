#import "RNDuxPush.h"


@implementation RNDuxPush

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(setAlias:(NSString *)text)
{
    
    [MiPushSDK setAlias:text];
}

RCT_EXPORT_METHOD(unsetAlias:(NSString *)text)
{
    [MiPushSDK unsetAlias:text];
}

RCT_EXPORT_METHOD(setTag:(NSString *)text)
{
    
    [MiPushSDK subscribe:text];
}

RCT_EXPORT_METHOD(unsetTag:(NSString *)text)
{
    
    [MiPushSDK unsubscribe:text];
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
    
    [MiPushSDK registerMiPush:self];
    NSLog(@"注册");
}

+ (void)application:(id)application didRegisterUserNotificationSettings:(id)notificationSettings {
    
    [RNCPushNotificationIOS didRegisterUserNotificationSettings:notificationSettings];
}

+ (void)application:(id)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    
    [MiPushSDK bindDeviceToken:deviceToken];
    [RNCPushNotificationIOS didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

+ (void)application:(id)application didReceiveRemoteNotification:(NSDictionary *)notification {
    
    [RNCPushNotificationIOS didReceiveRemoteNotification:notification];
}

+ (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    
    [RNCPushNotificationIOS didReceiveLocalNotification:notification];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"duxpush_click" object:notification.userInfo];
}

// 应用在前台收到通知
+ (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    
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
