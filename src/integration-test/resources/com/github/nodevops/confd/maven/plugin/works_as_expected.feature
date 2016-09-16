@mavenBuild
Feature: When all is clear, all is good


    Scenario Outline: simple-noprofile-java-processor
        This project does not have any profile, hence the prepare and the generate goal are always launched.
        Yes, this is not realistic because in real life you'd like to generate more than only a local configuration :)

        Given my test project root is : src/integration-test/data/projects/simple-noprofile-java-processor
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
            dest = '/test/src/integration-test/data/projects/simple-noprofile-java-processor/target/generated-configuration/application.yml'
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
            | java_home                     | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |


    Scenario Outline: simple-with-profile-java-processor default profile
        This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
        This scenario uses the default profile, so we expect basically the same behaviour as with the simple-noprofile-java-processor scenario

        Given my test project root is : src/integration-test/data/projects/simple-with-profile-java-processor
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
            dest = '/test/src/integration-test/data/projects/simple-with-profile-java-processor/target/generated-configuration/application.yml'
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
            | java_home                     | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |


    Scenario Outline: simple-with-profile-java-processor delivery profile
        This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
        This scenario uses the delivery profile, so we expect that only the confd config file will be generated.
        In this use case, we also expect the target file (in the confd toml file) to be the path specified in the delivery profile and not
        the destination specified by the shared configuration.

        Given my test project root is : src/integration-test/data/projects/simple-with-profile-java-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        When I run maven with args: clean, package, -P, delivery
        Then the build is OK
        And 1 template was found
        And output contains: keys=[/your/namespace, /runtime]
        And files exist:
            | target/confd/conf.d/application.yml.toml       |
            | target/confd/templates/application.yml.tmpl    |
        And file 'target/generated-configuration/application.yml' does not exist
        And file 'target/confd/conf.d/application.yml.toml' content is:
            """
            [template]
            src = 'application.yml.tmpl'
            dest = '/usr/local/appli/config/application.yml'
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

        Examples:
            | java_home                     | m2_home                |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x |


    Scenario Outline: simple-with-profile-confd-processor default profile
    This project has the prepare step shared by 2 profiles: local is the default profile, delivery is the second.
    This scenario uses the default profile, so we expect basically the same behaviour as with the simple-noprofile-java-processor scenario

        Given my test project root is : src/integration-test/data/projects/simple-with-profile-confd-processor
        And JAVA_HOME is set to: <java_home>
        And M2_HOME is set to: <m2_home>
        And confd for cucumber is located in: <confd_location>
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
            dest = '/test/src/integration-test/data/projects/simple-with-profile-confd-processor/target/generated-configuration/application.yml'
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
            | java_home                     | m2_home                | confd_location                       |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.11.0/confd        |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.8-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.1.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.2.x | /opt/local/confd-0.12.0-alpha3/confd |
            | /usr/lib/jvm/java-1.7-openjdk | /opt/local/maven-3.3.x | /opt/local/confd-0.12.0-alpha3/confd |

