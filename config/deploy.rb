# config valid only for current version of Capistrano
lock '3.3.5'
set :scm, :git
set :repo_url, 'git@github.com:BrandyMint/kiiiosk-open-api.git'
set :keep_releases, 5
set :linked_files, %w(profiles.clj)
set :linked_dirs, %w(log)

set :branch, ENV['BRANCH'] || 'master'
namespace :deploy do
  task  :leinclean do
    on roles :all do
      within current_path do
        execute "cd #{current_path} && lein do clean"
      end
    end
  end

  task  :leindeps do
    on roles :all do
      within current_path do
        execute "cd #{current_path} && lein deps"
      end
    end
  end

  task  :leinuberwar do
    on roles :all do
      within current_path do
        execute "cd #{current_path} && lein ring uberjar"
      end
    end
  end


  task  :makewebapps do
    on roles :all do
      within current_path do
        execute "cd #{deploy_to}/current && mkdir -v webapps && cp -v target/server.jar webapps/ROOT.war"
      end
    end
  end

  after :published, 'leinclean'
  after :leinclean, 'leindeps'
  after :leindeps, 'leinuberwar'
  after :leinuberwar, 'makewebapps'

end
