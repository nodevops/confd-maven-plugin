@mavenBuild
Feature: Mvn Build Works as expectd

    Background:
        Given confd is located in: /opt/local/confd-0.11.0

    Scenario Outline: simple-noprofile
        Given my test project root is : src/integration-test/data/projects/simple-noprofile
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        When I run maven with args: clean, package
        Then the build is OK
        And 1 template was found
        And output contains: keys=[/your/namespace, /runtime]
        And files exist:
            | target/confd/conf.d/application.yml.toml       |
            | target/confd/templates/application.yml.tmpl    |
            | target/generated-configuration/application.yml |
        And file 'target/confd/conf.d/application.yml.toml' content is:
            """
            [template]
            src = 'application.yml.tmpl'
            dest = '/test/src/integration-test/data/projects/simple-noprofile/target/generated-configuration/application.yml'
            keys = [
            '/your/namespace',
            '/runtime',
            ]
            """
        And file 'target/confd/templates/application.yml.tmpl' content is:
            """
            server:
              port: {{getv "/your/namespace/myapp/httpport"}}
              tomcat:
                accesslog:
                  directory: {{getv "/your/namespace/myapp/tomcat/access/log/dir"}}
                  enabled: {{getv "/your/namespace/myapp/tomcat/access/log/enabled"}}
                  pattern: {{getv "/your/namespace/myapp/tomcat/access/log/pattern"}}
                  prefix: {{getv "/your/namespace/myapp/tomcat/access/log/prefix"}}
                  suffix: {{getv "/your/namespace/myapp/tomcat/access/log/suffix"}}

            management:
              context-path: {{getv "/your/namespace/myapp/management/context/path"}}

            info:
              env:
                name: {{getv "/your/namespace/myapp/env/name"}}
              orchestrator:
                app_id: {{getv "/runtime/orchestrator/app/id"}}
            """
        And file 'target/generated-configuration/application.yml' content is:
            """
            server:
              port: 8080
              tomcat:
                accesslog:
                  directory: target
                  enabled: false
                  pattern: combined
                  prefix: access_log
                  suffix: .log

            management:
              context-path: /admin

            info:
              env:
                name: local
              orchestrator:
                app_id: bazinga
            """

        Examples:
            | java_home          | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |
