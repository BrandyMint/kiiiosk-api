set :stage, :production
set :deploy_to, ->{"/home/wwwkiiiosk/#{fetch(:application)}"}
server 'srv-1.kiiiosk.ru', user: 'wwwkiiiosk', port: 22
server 'srv-2.kiiiosk.ru', user: 'wwwkiiiosk', port: 22
set :branch, ENV['BRANCH'] || 'master'
