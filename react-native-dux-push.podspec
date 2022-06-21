# react-native-dux-push.podspec

require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-dux-push"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-dux-push
                   DESC
  s.homepage     = "https://github.com/ShaoGongBra/react-native-dux-push"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "ShaoGong" => "908634674@qq.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/ShaoGongBra/react-native-dux-push.git", :tag => "#{s.version}" }

  s.source_files    = 'ios/*.{h,m}'
  s.ios.vendored_libraries = 'ios/*.a'
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'RNCPushNotificationIOS'

  s.frameworks = 'UserNotifications','SystemConfiguration','MobileCoreServices','CFNetwork','CoreTelephony'
  s.library = 'resolv','xml2','z'
end

