/*
 * Copyright (C) 2016+ furplag (https://github.com/furplag/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
module.exports = function (grunt) {

  'use strict';

  grunt.util.linefeed = '\n';

  RegExp.quote = function (string) {
    return string.replace(/[-\\^$*+?.()|[\]{}]/g, '\\$&');
  };

  var path = require('path');

  // Project configuration.
  grunt.initConfig({

    // Metadata.
    pkg: grunt.file.readJSON('package.json'),
    banner: '',

    'bower-install-simple': {
      options: {
        color: true,
        directory: 'bower_components'
      },
      dist: {
        options: {
          production: true
        }
      }
    },

    bowercopy: {
      options: {
        srcPrefix: 'bower_components'
      },
      dist: {
        options: {
          destPrefix: 'src/main/resources/static/boosterpack/libs'
        },
        files: {
          'css/animate.min.css': 'animate.css/animate.min.css',

          'css/bootstrap-3x.min.css': 'bootstrap-3x/dist/css/bootstrap.min.css',
          'css/bootstrap-3x.min.css.map': 'bootstrap-3x/dist/css/bootstrap.min.css.map',
          'js/bootstrap-3x.min.js': 'bootstrap-3x/dist/js/bootstrap.min.js',

          'js/jquery-1x.min.js': 'jquery-1x/dist/jquery.min.js',
          'js/jquery-2x.min.js': 'jquery-2x/dist/jquery.min.js',

          'js/modernizr-2x.js': 'modernizr-2x/modernizr.js'
        }
      }
    },
    exec: {
      'uglify-modernizr': {
        command: 'npm run uglify-modernizr'
      }
    }
  });

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt, { scope: 'devDependencies' });
  require('time-grunt')(grunt);

  // Full distribution task.
  grunt.registerTask('dist', ['bower-install-simple:dist', 'bowercopy:dist', 'exec:uglify-modernizr'])

  // Default task.
  grunt.registerTask('default', ['dist'])
};
