# JinJava Maven Plugin 

The JinJava [Maven plugin](http://maven.apache.org/plugins/index.html) allows you to use static [Jinja template files](https://jinja.palletsprojects.com/en/3.0.x/) 
in your application, During build, the template engine replaces variables in a template file with actual values, 
and produce the final processed output file.
 
Available from the [Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22ansible-maven-plugin%22)

* [Overview](#overview)
* [Usage](#usage)
* [Goals](#goals)
* [Release Notes](#release-notes)

## Usage

* The JinJava maven plugin can be used to template out any type of text files (eg .yml files,.txt. .json ...) during build.
This plugin can be used when we need to generate text files based on the different context. 
(can be used when we need to generate yml based on host)

### ansible

To execute the ansible __ping__ module with the host as __localhost__

```
       <plugin>
                      <groupId>io.github.gowrish9595</groupId>
                      <artifactId>jinjava-maven-plugin</artifactId>
                      <version>1.0.0-RC2</version>
                      <executions>
                          <execution>
                              <goals>
                                  <goal>generate-files-for-templates</goal>
                              </goals>
                          </execution>
                      </executions>
                      <configuration>
                          <j2ResourcesDirectory>${project.basedir}/src/main/resources/j2-templates/</j2ResourcesDirectory>
                          <outputDirectory>${project.basedir}/src/main/resources/</outputDirectory>
                          <contextFilePath>${project.basedir}/src/main/resources/config-profiles/host-specific.yml</contextFilePath>
                      </configuration>
                  </plugin>
```
## Goals

* [generate-files-for-templates](#generate-files-for-templates)
* [playbook](#playbook-1)
* [pull](#pull-1)

### generate-files-for-templates 

Binds by default to the [generate-resources](http://maven.apache.org/ref/current/maven-core/lifecycles.html)

#### Parameters
  Name | Type | Description | Required | Default
  :----|:----:|:------------|:-------: | -------
  j2ResourcesDirectory|String| Absolute path of the directory that conatins j2 resources | Yes | -
  outputDirectory|String| The absolute path of the directory where templated files are generated | Yes | -
  contextFilePath|String| The absolute path of the variable file (the file should be yaml)| Yes | -
  isLStripBlocks|boolean| Determine when leading spaces and tabs should be stripped. When set to yes leading spaces and tabs are stripped from the start of a line to a block.| No | false
  isTrimBlocks|boolean|Determine when newlines should be removed from blocks. When set to yes the first newline after a block is removed | No | true
  isFailOnUnKnownVariables|boolean| Fail build when a variable in template not in context | No | true
  
 
## Release Notes

### 1.0.0 Initial release 
  Release date: 22-10-2021
  