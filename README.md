# native-image-fastdebug-repro

Reproducer repo for an assertion bug when generating a native image from it using a debug base JDK

# Steps to Reproduce

## Prerequisites

1. Build OpenJDK 11 with `--with-debug-level=fastdebug`
2. Build Mandrel using the fastdebug OpenJDK 11 from step 1

## Reproducer

```
$ mvn clean package
$ export GRAALVM_HOME=/path/to/mandrel-build-with-debug-base-jdk
$ $GRAALVM_HOME/bin/native-image -jar target/invokevirtualcons.jar invokevirtualcons
```

# Actual Results

Build asserts in JVM.

```
$ native-image -jar target/invokevirtualcons.jar invokevirtualcons
[invokevirtualcons:207749]    classlist:  44,922.72 ms,  0.96 GB
[invokevirtualcons:207749]        (cap):   3,192.16 ms,  0.96 GB
[invokevirtualcons:207749]        setup:  26,450.08 ms,  0.96 GB
# To suppress the following error report, specify this argument
# after -XX: or in .hotspotrc:  SuppressErrorAt=/linkResolver.cpp:1310
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  Internal Error (/disk/openjdk/upstream-sources/openjdk-11/src/hotspot/share/interpreter/linkResolver.cpp:1310), pid=207749, tid=209757
#  assert(resolved_method->name() != vmSymbols::object_initializer_name()) failed: should have been checked in verifier
#
# JRE version: OpenJDK Runtime Environment (11.0.11) (slowdebug build 11.0.11-internal+0-adhoc.sgehwolf.openjdk-11)
# Java VM: OpenJDK 64-Bit Server VM (slowdebug 11.0.11-internal+0-adhoc.sgehwolf.openjdk-11, mixed mode, tiered, jvmci, compressed oops, parallel gc, linux-amd64)
# Core dump will be written. Default location: Core dumps may be processed with "/usr/lib/systemd/systemd-coredump %P %u %g %s %t %c %h" (or dumping to /home/sgehwolf/Documents/mandrel/bugs/MANDREL-48/constructor-sub-reproducer/core.207749)
#
# An error report file with more information is saved as:
# /home/sgehwolf/Documents/mandrel/bugs/MANDREL-48/constructor-sub-reproducer/hs_err_pid207749.log
#
# If you would like to submit a bug report, please visit:
#   https://bugreport.java.com/bugreport/crash.jsp
#
Current thread is 209757
Dumping core ...
Error: Image build request failed with exit status 134
```

# Expected Results

Build works fine.

```
$ native-image -jar target/invokevirtualcons.jar invokevirtualcons
[invokevirtualcons:78024]    classlist:   1,513.35 ms,  0.96 GB
[invokevirtualcons:78024]        (cap):     466.22 ms,  0.96 GB
[invokevirtualcons:78024]        setup:   1,672.19 ms,  0.96 GB
[invokevirtualcons:78024]     (clinit):     186.39 ms,  1.71 GB
[invokevirtualcons:78024]   (typeflow):   4,728.82 ms,  1.71 GB
[invokevirtualcons:78024]    (objects):   4,701.73 ms,  1.71 GB
[invokevirtualcons:78024]   (features):     190.72 ms,  1.71 GB
[invokevirtualcons:78024]     analysis:  10,016.75 ms,  1.71 GB
[invokevirtualcons:78024]     universe:     404.18 ms,  1.71 GB
[invokevirtualcons:78024]      (parse):   1,429.77 ms,  1.71 GB
[invokevirtualcons:78024]     (inline):   2,491.38 ms,  1.71 GB
[invokevirtualcons:78024]    (compile):   7,642.39 ms,  2.33 GB
[invokevirtualcons:78024]      compile:  12,055.52 ms,  2.33 GB
[invokevirtualcons:78024]        image:   1,770.11 ms,  3.24 GB
[invokevirtualcons:78024]        write:     184.33 ms,  3.24 GB
[invokevirtualcons:78024]      [total]:  27,771.69 ms,  3.24 GB
```
