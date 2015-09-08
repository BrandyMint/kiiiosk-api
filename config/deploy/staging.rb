set :application, "openapi.aydamarket.ru"
set :stage, :staging
set :deploy_to, ->{"/home/wwwkiiiosk/#{fetch(:application)}"}
#server 'srv-1.kiiiosk.ru', user: 'wwwkiiiosk', port: 22
#server 'srv-2.kiiiosk.ru', user: 'wwwkiiiosk', port: 22
server 'icfdev.ru', user: 'wwwkiiiosk', port: 250
server 'icfdev.ru', user: 'wwwkiiiosk', port: 251

set :branch, ENV['BRANCH'] || 'master'
