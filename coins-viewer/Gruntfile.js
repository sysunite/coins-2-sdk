//Using exclusion patterns slows down Grunt significantly
//instead of creating a set of patterns like '**/*.js' and '!**/node_modules/**'
//this method is used to create a set of inclusive patterns for all subdirectories
//skipping node_modules, bower_components, dist, and any .dirs
//This enables users to create any directory structure they desire.
var createFolderGlobs = function(fileTypePatterns) {
    fileTypePatterns = Array.isArray(fileTypePatterns) ? fileTypePatterns : [fileTypePatterns];
    var ignore = ['node_modules','bower_components','dist','tmp', 'server'];
    var fs = require('fs');
    return fs.readdirSync(process.cwd())
        .map(function(file){
            if (ignore.indexOf(file) !== -1 ||
                file.indexOf('.') === 0 ||
                !fs.lstatSync(file).isDirectory()) {
                return null;
            } else {
                return fileTypePatterns.map(function(pattern) {
                    return file + '/**/' + pattern;
                });
            }
        })
        .filter(function(patterns){
            return patterns;
        })
        .concat(fileTypePatterns);
};


module.exports = function(grunt) {

    // Load all grunt tasks
    require('load-grunt-tasks')(grunt);

    // Project configuration.
    grunt.initConfig({
        connect: {
            main: {
                options: {
                    livereload: 35730,
                    open: false,
                    port: 9000,
                    hostname: '0.0.0.0',
                },
                proxies: [
                    {
                        context: '/api',
                        host: 'localhost',
                        port: 8080,
                        https: false,
                        xforward: false,
                        hideHeaders: ['x-removed-header']
                    }
                ]
            }
        },

        pkg: grunt.file.readJSON('package.json'),

        less: {
            development: {
                options: {
                    paths: ["css"]
                },
                files: {
                    "css/app.css": "less/app.less",
                },
                cleancss: true
            }
        },
        csssplit: {
            your_target: {
                src: ['css/app.css'],
                dest: 'css/app.min.css',
                options: {
                    maxSelectors: 4095,
                    suffix: '.'
                }
            },
        },
        ngtemplates: {
          materialAdmin: {
            src: ['template/**/*.html', 'views/**/*.html'],
            dest: 'js/templates.js',
            options: {
              htmlmin: {
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true
              }
            }
          }
        },
        watch: {
            c: {
                files: ['js/**/*.js', 'views/**/*.html', 'index.html'], // which files to watch
                tasks: [],
                options: {
                    livereload: 35730,
                    livereloadOnError: false,
                    spawn: false
                },
            },
            a: {
                files: ['less/**/*.less'], // which files to watch
                tasks: ['less', 'csssplit'],
                options: {
                    livereload: 35730,
                    livereloadOnError: false,
                    spawn: false
                },
            },
            b: {
                files: ['template/**/*.html'], // which files to watch
                tasks: ['ngtemplates'],
                options: {
                    livereload: 35730,
                    livereloadOnError: false,
                    spawn: false
                },
            }
        }
    });

    // Default task(s).
    grunt.registerTask('default', ['less', 'connect', 'watch']);
};